package nc.vo.arap.bx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.bd.pub.IBDMetaDataIDConst;
import nc.itf.fi.pub.Currency;
import nc.itf.org.IDeptQryService;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.ReimRuleDef;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class BxUIControlUtil {

	/**
	 * 供yer调用，借款报销人设置授权代理
	 * 
	 * @param jkbxr
	 * @param rolersql
	 * @param billtype
	 * @param user
	 * @param date
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public static String getAgentWhereString(String jkbxr, String rolersql,
			String billtype, String user, String date, String pk_org)
			throws BusinessException {
		return NCLocator.getInstance().lookup(IBXBillPrivate.class)
				.getAgentWhereString(jkbxr, rolersql, billtype, user, date,
						pk_org);
	}

	/**
	 * @param bxvo
	 * @param contrastData
	 * @return
	 * @throws BusinessException
	 * 
	 *             参数：报销单 冲销操作的VO
	 * 
	 *             返回：处理后的报销单
	 * 
	 */
	public JKBXVO doContrast(JKBXVO bxvo, List<BxcontrastVO> contrastsData)
			throws BusinessException {
		JKBXHeaderVO head = bxvo.getParentVO();
		UFDouble zero = new UFDouble(0);
		if (contrastsData == null || contrastsData.size() == 0) {
			// 取消借款单的冲销
			head.setCjkybje(zero);
			head.setCjkbbje(zero);
			head.setHkybje(zero);
			head.setHkbbje(zero);
			head.setZfybje(head.getYbje());
			head.setZfbbje(head.getBbje());
		} else {
			UFDouble cjkybje = zero;
			for (Iterator<BxcontrastVO> iter = contrastsData.iterator(); iter
					.hasNext();) {
				BxcontrastVO contrast = iter.next();
				cjkybje = cjkybje.add(contrast.getCjkybje());
			}
			// 计算冲借款,还款,支付本币, 取借款单汇率, 日期取借款单日期
			setJeMul(contrastsData, head, new String[] { 
					JKBXHeaderVO.CJKYBJE,
					JKBXHeaderVO.CJKBBJE, 
					JKBXHeaderVO.GROUPCJKBBJE,
					JKBXHeaderVO.GLOBALCJKBBJE });
			if (cjkybje.doubleValue() > head.getYbje().doubleValue()) {
				//冲借款金额>报销金额，则有还款
				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] {
						JKBXHeaderVO.HKYBJE, 
						JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, 
						JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
			} else if (cjkybje.doubleValue() < head.getYbje().doubleValue()) {
				//冲借款金额<报销金额，则有支付
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] {
						JKBXHeaderVO.ZFYBJE, 
						JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, 
						JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
			}else if (cjkybje.doubleValue() == head.getYbje().doubleValue()) {
				//冲借款金额==报销金额，则既无还款，又无支付
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] {
					JKBXHeaderVO.ZFYBJE, 
					JKBXHeaderVO.ZFBBJE,
					JKBXHeaderVO.GROUPZFBBJE, 
					JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
				
				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] {
					JKBXHeaderVO.HKYBJE, 
					JKBXHeaderVO.HKBBJE,
					JKBXHeaderVO.GROUPHKBBJE, 
					JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
			}
		}
		//折算表体冲借款金额
		caculateBodyCjkje(bxvo);
		return bxvo;
	}

	/**
	 * 折算表体冲借款金额(同时计算支付或还款金额)
	 * @author chendya
	 * @param bxvo
	 */
	public void caculateBodyCjkje(JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		if (childrenVO == null || childrenVO.length == 0)
			return;
		
		List<BXBusItemVO> noNullChildrenVoList = new ArrayList<BXBusItemVO>();
		
		for(int i = 0 ; i < childrenVO.length; i++){
			if(!isJeNullRow(childrenVO[i])){
				noNullChildrenVoList.add(childrenVO[i]);
			}
		}
		
		// 取得表头币种编码和汇率值，根据汇率值换算本币的值，若币种与本位币相同，则忽略界面中自定的汇率
		UFDouble cjkje = head.getCjkybje();
		for (int i = 0; i < noNullChildrenVoList.size(); i++) {
			BXBusItemVO child = noNullChildrenVoList.get(i);
			
			if (cjkje != null) {
				// 还有没分配完的冲借款金额
				UFDouble ybje = child.getYbje();
				//当前行是最后一行
				if(i == noNullChildrenVoList.size() - 1){
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
					modifyValues(child);
					transYbjeToBbje(head, child);
					return;
				}
				if (cjkje.compareTo(ybje) > 0) {
					// 如果剩余的冲借款金额大于原币金额，则该项的冲借款金额值与原币金额相同
					child.setAttributeValue(BXBusItemVO.CJKYBJE, ybje);
					modifyValues(child);
					transYbjeToBbje(head, child);
					cjkje = cjkje.sub(ybje);
				} else {
					// 如果剩余的冲借款金额不大于原币金额，则该项的冲借款金额值设为剩余的冲借款金额
					child.setAttributeValue(BXBusItemVO.CJKYBJE, cjkje);
					modifyValues(child);
					transYbjeToBbje(head, child);
					cjkje = null;
				}
			} else {
				// 冲借款金额都分配完了，剩余项都用0填补
				child.setAttributeValue(BXBusItemVO.CJKYBJE,new UFDouble(0));
				modifyValues(child);
				transYbjeToBbje(head, child);
			}
		}
	}

	private BXBusItemVO[] filterJeNullRow(BXBusItemVO[] chilerenVO) {
		//过滤掉没有金额的行
		List<BXBusItemVO> voList = new ArrayList<BXBusItemVO>();
		for(BXBusItemVO vo : chilerenVO){
			UFDouble ybje = vo.getYbje();
			if(ybje == null){
				ybje = UFDouble.ZERO_DBL;
			}
			if(UFDouble.ZERO_DBL.equals(ybje)){
				//没有原币金额的行不折算
				continue;
			}
			voList.add(vo);
		}
		return voList.toArray(new BXBusItemVO[0]);
	}
	
	
	private boolean isJeNullRow(BXBusItemVO vo) {
		//判断是否是金额为空的行
		UFDouble ybje = vo.getYbje() == null ? UFDouble.ZERO_DBL : vo.getYbje();
		UFDouble cjkJe = vo.getCjkybje() == null ? UFDouble.ZERO_DBL : vo.getCjkybje();
		
		if(UFDouble.ZERO_DBL.equals(ybje) && UFDouble.ZERO_DBL.equals(cjkJe)){
			//原币金额为空则表示为空行
			return true;
		}
		
		return false;
	}

	private void transYbjeToBbje(JKBXHeaderVO head, BXBusItemVO itemVO) {
		String pk_corp = head.getPk_org();
		String bzbm = head.getBzbm();
		UFDouble hl = head.getBbhl();
		UFDouble ybje = itemVO.getYbje();
		UFDouble cjkybje = itemVO.getCjkybje();
		UFDouble hkybje = itemVO.getHkybje();
		UFDouble zfybje = itemVO.getZfybje();
		try {
			UFDouble[] bbje = Currency.computeYFB(pk_corp,
					Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.BBJE, bbje[2]);
			itemVO.setAttributeValue(JKBXHeaderVO.BBYE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm,
					cjkybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.CJKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm,
					hkybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.HKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm,
					zfybje, null, null, null, hl, head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.ZFBBJE, bbje[2]);
			
			//折算冲借款集团、全局本币金额
			caculateGroupAndGlobalBbje(head,itemVO,BXBusItemVO.CJKYBJE,BXBusItemVO.CJKYBJE,BXBusItemVO.GROUPCJKBBJE,BXBusItemVO.GLOBALCJKBBJE);
			
			//折算支付集团、全局本币金额
			caculateGroupAndGlobalBbje(head,itemVO,BXBusItemVO.ZFYBJE,BXBusItemVO.ZFYBJE,BXBusItemVO.GROUPZFBBJE,BXBusItemVO.GLOBALZFBBJE);
			
			//折算还款集团、全局本币金额
			caculateGroupAndGlobalBbje(head,itemVO,BXBusItemVO.HKYBJE,BXBusItemVO.HKYBJE,BXBusItemVO.GROUPHKBBJE,BXBusItemVO.GLOBALHKBBJE);
			
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}
	/**
	 * 折算集团和全局本币金额
	 * @param ybje
	 * @param bbje
	 * @param pk_currtype
	 * @param date
	 * @param pk_org
	 * @param pk_group
	 * @param globalrate
	 * @param grouprate
	 * @throws BusinessException 
	 */
	private static void caculateGroupAndGlobalBbje(JKBXHeaderVO head, BXBusItemVO itemVO, 
			String ybjeField,String bbjeField,String groupbbjeField,String globalbbjeField) throws BusinessException{
		UFDouble[] moneys = Currency.computeGroupGlobalAmount((UFDouble)itemVO.getAttributeValue(ybjeField), 
				(UFDouble)itemVO.getAttributeValue(bbjeField), head.getBzbm(), head.getDjrq(), head.getPk_org(), head.getPk_group(), 
				head.getGlobalbbhl(), head.getGroupbbhl());
		//集团
		itemVO.setAttributeValue(groupbbjeField, moneys[0]);
		//全局
		itemVO.setAttributeValue(globalbbjeField, moneys[1]);
	}

	/**
	 * 根据冲借款金额的变化，修改表体中数据的其他金额数值
	 * 
	 * @author zhangxiao1
	 */
	private void modifyValues(BXBusItemVO vo) {
		UFDouble ybje = vo.getYbje();
		UFDouble cjkybje = vo.getCjkybje();
		if (ybje.getDouble() > cjkybje.getDouble()) {// 如果原币金额大于冲借款金额
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, ybje.sub(cjkybje));// 支付金额=原币金额-冲借款金额
			vo.setAttributeValue(BXBusItemVO.HKYBJE, new UFDouble(0));
		} else {
			vo.setAttributeValue(BXBusItemVO.HKYBJE, cjkybje.sub(ybje));// 还款金额=冲借款金额-原币金额
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, new UFDouble(0));
		}
	}

	/**
	 * @param contrastsData
	 * @param yfbKeys
	 * 
	 *            设置冲销金额
	 * @throws BusinessException
	 */
	private void setJeMul(List<BxcontrastVO> contrastsData, JKBXHeaderVO head,
			String[] yfbKeys) throws BusinessException {
		try {
			UFDouble[] yfbs = null;
			for (Iterator<BxcontrastVO> iter = contrastsData.iterator(); iter
					.hasNext();) {
				BxcontrastVO vo = iter.next();
				UFDouble cjkybje = vo.getCjkybje();
				UFDouble[] values = Currency.computeYFB(head.getPk_org(),
						Currency.Change_YBJE, head.getBzbm(), cjkybje, null,
						null, null, head.getBbhl(), head.getDjrq());

				vo.setCjkbbje(values[2]);
				vo.setBbje(values[2]);

				UFDouble[] money = Currency.computeGroupGlobalAmount(cjkybje,
						values[2], head.getBzbm(), head.getDjrq(), head
								.getPk_org(), head.getPk_group(), head
								.getGlobalbbhl(), head.getGroupbbhl());

				vo.setGroupcjkbbje(money[0]);
				vo.setGlobalcjkbbje(money[1]);
				vo.setGroupbbje(money[0]);
				vo.setGlobalbbje(money[1]);

				if (yfbs == null) {
					yfbs = values;
				} else {
					for (int i = 0; i < 3; i++) {
						yfbs[i] = yfbs[i].add(values[0]);
					}
				}
			}
			UFDouble[] money2 = Currency.computeGroupGlobalAmount(yfbs[0],
					yfbs[2], head.getBzbm(), head.getDjrq(), head.getPk_org(),
					head.getPk_group(), head.getGlobalbbhl(), head
							.getGroupbbhl());

			head.setAttributeValue(yfbKeys[0], yfbs[0]);
			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money2[0]);
			head.setAttributeValue(yfbKeys[3], money2[1]);

		} catch (BusinessException e) {
			// 设置本币错误.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																		 * @res
																		 * "设置本币错误!"
																		 */);
		}
	}

	/**
	 * 折算表头金额字段
	 * @param head
	 * @param ybje
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	private void setHeadJe(JKBXHeaderVO head, UFDouble ybje, String[] yfbKeys)
			throws BusinessException {
		try {
			head.setAttributeValue(yfbKeys[0], ybje);
			UFDouble[] yfbs = Currency.computeYFB(head.getPk_org(), Currency.Change_YBJE,
					head.getBzbm(), ybje, null, null, null, head.getBbhl(),
					head.getDjrq());
			UFDouble[] money = Currency.computeGroupGlobalAmount(ybje, yfbs[2],
					head.getBzbm(), head.getDjrq(), head.getPk_org(), head
							.getPk_group(), head.getGlobalbbhl(), head
							.getGroupbbhl());

			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money[0]);
			head.setAttributeValue(yfbKeys[3], money[1]);

		} catch (BusinessException e) {
			// 设置本币错误.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																		 * @res
																		 * "设置本币错误!"
																		 */);
		}
	}

	/**
	 * 返回常用单据VO
	 * 
	 * @author chendya
	 * @param pk_org
	 * @param pk_group
	 * @param djlxbm
	 * @param includeGroup
	 * @return
	 * @throws BusinessException
	 */
	public static List<JKBXVO> getInitBill(String pk_org, String pk_group,
			String djlxbm, boolean includeGroup) throws BusinessException {

		DjCondVO condVO = new DjCondVO();

		condVO.isInit = true;
		condVO.defWhereSQL = " zb.djlxbm='" + djlxbm
				+ "' and zb.dr=0 and ((isinitgroup='N' and pk_org='" + pk_org
				+ "') or isinitgroup='Y') ";
		condVO.isCHz = false;
		condVO.pk_group = new String[] { pk_group };
		// 组织和集团的合并查询，减少远程调用次数，查询完后分组,优先返回组织级的VO
		List<JKBXVO> all = NCLocator.getInstance().lookup(IBXBillPrivate.class)
				.queryVOs(0, -99, condVO);
		
		if (all == null) {
			return new ArrayList<JKBXVO>();
		}
		List<JKBXVO> orgVOList = new ArrayList<JKBXVO>();
		List<JKBXVO> groupVOList = new ArrayList<JKBXVO>();
		for (Iterator<JKBXVO> iterator = all.iterator(); iterator.hasNext();) {
			JKBXVO vo = iterator.next();
			if (UFBoolean.TRUE.equals(vo.getParentVO().getIsinitgroup())) {
				groupVOList.add(vo);
			} else {
				orgVOList.add(vo);
			}
		}
		// 优先返回组织级的常用单据
		return orgVOList.size() > 0 ? orgVOList : groupVOList;
	}

	public static BusiTypeVO getBusTypeVO(String djdl, String djlxbm) {
		return BXUtil.getBusTypeVO(djlxbm, djdl);
	}

	private static boolean doSimpleNoEquals(String reftype, String pk_corp,
			Object ruleKey, Object voKey) {
		IGeneralAccessor acc = null;

		if (reftype.equals("地区分类")) {/*-=notranslate=-*/
			acc = GeneralAccessorFactory
					.getAccessor(IBDMetaDataIDConst.AREACLASS);
		}
		if (reftype.equals("部门")) {/*-=notranslate=-*/
			acc = GeneralAccessorFactory.getAccessor(IBDMetaDataIDConst.DEPT);
		}

		if (acc != null) {
			while (voKey != null && voKey.toString().trim().length() != 0
					&& doSimpleNoEquals(voKey, ruleKey)) {
				IBDData doc = acc.getDocByPk(voKey.toString());
				if (doc == null)
					return doSimpleNoEquals(voKey, ruleKey);
				voKey = doc.getParentPk();
			}
			return doSimpleNoEquals(voKey, ruleKey);
		}

		return doSimpleNoEquals(ruleKey, voKey);
	}

	private static boolean doSimpleNoEquals(Object ruleKey, Object voKey) {
		if (ruleKey == null)
			return false;
		else if (voKey == null)
			return true;
		else
			return !VOUtils.simpleEquals(ruleKey.toString(), voKey.toString());
	}
	
	/**
	 * 执行表体报销标准
	 * @param bxvo 单据VO
	 * @param reimRuleDataMap 报销标准Map
	 * @param bodyReimRuleMap 表体报销标准Map
	 * @return
	 */
	public static List<BodyEditVO> doBodyReimAction(JKBXVO bxvo,
			Map<String, List<SuperVO>> reimRuleDataMap,
			HashMap<String, String> bodyReimRuleMap) {

		List<BodyEditVO> result = new ArrayList<BodyEditVO>();
		if (bxvo == null || bxvo.getParentVO() == null
				|| bxvo.getParentVO().getDjlxbm() == null)
			return result;

		String djlxbm = bxvo.getParentVO().getDjlxbm();
		List<ReimRuleVO> matchReimRule = getMatchReimRuleByHead(djlxbm, bxvo, reimRuleDataMap);

		for (String key : bodyReimRuleMap.keySet()) {//表体报销标准Map<tablecode@itemkey,费用类型pk>循环
			String expenseKey = bodyReimRuleMap.get(key);//费用类型key
			String[] keys = key.split(ReimRuleVO.REMRULE_SPLITER);
			String tableCode = keys[0];
			String itemkey = keys[1];
			CircularlyAccessibleValueObject[] bodyValueVOs = bxvo.getTableVO(tableCode);
			
			if (bodyValueVOs != null) {
				int row = -1;
				for (CircularlyAccessibleValueObject body : bodyValueVOs) {//对对应tableCode下的所有数据行进行遍历
					row++;

					BodyEditVO bodyEditVO2 = new BodyEditVO();
					bodyEditVO2.setValue(new UFDouble(0));
					bodyEditVO2.setRow(row);
					bodyEditVO2.setItemkey(itemkey);
					bodyEditVO2.setTablecode(tableCode);
					result.add(bodyEditVO2);

					for (ReimRuleVO rule : matchReimRule) {
						if (doSimpleNoEquals(rule.getPk_reimtype(), body.getAttributeValue(BXBusItemVO.PK_REIMTYPE))) {
							//报销类型
							continue;
						}
						if (doSimpleNoEquals(rule.getPk_expensetype(),expenseKey)) {//费用类型
							continue;
						}
						boolean match = true;
						ReimRuleDefVO reimRuleDefvo = BXUtil.getReimRuleDefvo(djlxbm);
						for (ReimRuleDef def : reimRuleDefvo.getReimRuleDefList()) {//报销标准自定义项
							
							if (def.getItemvalue().startsWith(ReimRuleVO.Reim_body_key)) {
								
								String itemkey2 = def.getItemkey();
								String itemvalue = def.getItemvalue();
								String[] itemvalues = itemvalue.split(ReimRuleVO.REMRULE_SPLITER);
								String bodyCol = itemvalues[1];
								if (doSimpleNoEquals(def.getReftype(), bxvo.getParentVO().getPk_group(),
										rule.getAttributeValue(itemkey2), 
										body.getAttributeValue(bodyCol))) {//自定义与表体中对应字段值不同
									match = false;
									break;
								}
							}
						}

						if (match) {
							BodyEditVO bodyEditVO = new BodyEditVO();
							bodyEditVO.setValue(rule.getAmount());
							bodyEditVO.setRow(row);
							bodyEditVO.setItemkey(itemkey);
							bodyEditVO.setTablecode(tableCode);
							result.add(bodyEditVO);
						}
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 表头报销标准
	 * @param bxvo 单据VO
	 * @param reimRuleDataMap 报销标准Map
	 * @param expenseType 费用类型
	 * @param reimtypeMap 报销类型
	 * @return
	 */
	public static String doHeadReimAction(JKBXVO bxvo,
			Map<String, List<SuperVO>> reimRuleDataMap,
			Map<String, SuperVO> expenseType, Map<String, SuperVO> reimtypeMap) {
		StringBuffer reimrule = new StringBuffer("");

		if (bxvo == null || bxvo.getParentVO() == null
				|| bxvo.getParentVO().getDjlxbm() == null)
			return reimrule.toString();

		String djlxbm = bxvo.getParentVO().getDjlxbm();
		//根据单据类型获取自定义报销标准
		List<ReimRuleVO> matchReimRule = getMatchReimRuleByHead(djlxbm, bxvo, reimRuleDataMap);

		String langIndex = PubCommonReportMethod.getMultiLangIndex();
		for (ReimRuleVO rule : matchReimRule) {
			if (expenseType.get(rule.getPk_expensetype()) != null) {
				reimrule.append(expenseType.get(rule.getPk_expensetype()).getAttributeValue("name" + langIndex));
			}
			String pk_reimtype = rule.getPk_reimtype();
			if (pk_reimtype != null) {
				if (reimtypeMap.get(pk_reimtype) != null) {
					reimrule.append(":");
					reimrule.append(reimtypeMap.get(pk_reimtype).getAttributeValue("name" + langIndex));
				}
			}
			
			ReimRuleDefVO reimRuleDefvo = BXUtil.getReimRuleDefvo(djlxbm);
			for (ReimRuleDef def : reimRuleDefvo.getReimRuleDefList()) {
				if (def.getItemvalue().startsWith(ReimRuleVO.Reim_body_key)) {
					if (rule.getAttributeValue(def.getItemkey()) != null) {
						reimrule.append(":");
						Object nameValue = rule.getAttributeValue(def.getItemkey() + "_name");
						reimrule.append(nameValue != null ? nameValue : rule.getAttributeValue(def.getItemkey()));
					}
				}
			}
			reimrule.append("  ");
			reimrule.append(rule.getAmount());
			reimrule.append("\t");
		}
		return reimrule.toString();
	}

	private static List<ReimRuleVO> getMatchReimRuleByHead(String djlxbm, JKBXVO bxvo,
			Map<String, List<SuperVO>> reimRuleDataMap) {
		List<SuperVO> dataList = reimRuleDataMap.get(djlxbm);
		List<ReimRuleVO> matchedRule = new ArrayList<ReimRuleVO>();

		if (dataList == null || dataList.size() == 0 || bxvo == null
				|| bxvo.getParentVO() == null)
			return matchedRule;

		ReimRuleVO[] ruleVOs = dataList.toArray(new ReimRuleVO[] {});
		Arrays.sort(ruleVOs);
		// 用来保存构造的报销标准
		HashMap<String, String> rulemap = new HashMap<String, String>();

		for (ReimRuleVO rule : ruleVOs) {
			StringBuffer bodykey = new StringBuffer();
			bodykey.append(rule.getPk_expensetype());
			bodykey.append(rule.getPk_reimtype());

			String deptid = bxvo.getParentVO().getDeptid();
			while (deptid != null
					&& doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
				deptid = getFatherDept(deptid);
			}

			if (doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
				continue;
			}

			if (doSimpleNoEquals(rule.getPk_psn(), bxvo.getParentVO()
					.getJkbxr())) {
				continue;
			}
			if (doSimpleNoEquals(rule.getPk_currtype(), bxvo.getParentVO()
					.getBzbm())) {
				continue;
			}

			boolean notmatch = false;
			ReimRuleDefVO reimRuleDefvo = BXUtil.getReimRuleDefvo(djlxbm);
			for (ReimRuleDef def : reimRuleDefvo.getReimRuleDefList()) {
				String itemvalue = def.getItemvalue();
				if (!itemvalue.startsWith(ReimRuleVO.Reim_body_key)) {
					Object headvalue = "";
					String[] keys = itemvalue.split(ReimRuleVO.REMRULE_SPLITER);
					String typeString = keys[0];
					String keyString = keys[1];
					if (itemvalue.startsWith(ReimRuleVO.Reim_head_key)) {
						headvalue = bxvo.getParentVO().getAttributeValue(
								keyString);
					} else {
						if (itemvalue.startsWith(ReimRuleVO.Reim_jkbxr_key)
								|| itemvalue
										.startsWith(ReimRuleVO.Reim_receiver_key)) {
							headvalue = getPsnDef1((String) bxvo.getParentVO()
									.getAttributeValue(typeString), keyString);
						} else if (itemvalue
								.startsWith(ReimRuleVO.Reim_deptid_key)
								|| itemvalue
										.startsWith(ReimRuleVO.Reim_fydeptid_key)) {
							try {
								headvalue = getDeptDef1((String) bxvo.getParentVO().getAttributeValue(typeString), keyString);
							} catch (BusinessException e) {
								ExceptionHandler.consume(e);
							}
						}
					}
					if (doSimpleNoEquals(def.getReftype(), bxvo.getParentVO()
							.getPk_group(), rule.getAttributeValue(def
							.getItemkey()), headvalue)) {//当表头有中有一个条件与标准不同，则跳出，表示不符合
						notmatch = true;
						break;
					}
				} else {
					if (rule.getAttributeValue(def.getItemkey()) != null) {
						bodykey.append(rule.getAttributeValue(def.getItemkey()));
					}
				}
			}

			if (notmatch)
				continue;

			int scale = 2;
			if (bxvo.getParentVO().getBzbm() != null) {
				try {
					scale = Currency.getCurrDigit(bxvo.getParentVO().getBzbm());
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
				rule.setAmount(rule.getAmount().setScale(scale,
						UFDouble.ROUND_HALF_UP));
			}

			if (rulemap.get(bodykey.toString()) == null) {
				matchedRule.add(rule);
				rulemap.put(bodykey.toString(), "");
			}
		}

		return matchedRule;
	}

	private static String getFatherDept(String dept) {
		if (dept == null || dept.trim().length() == 0)
			return null;

		DeptVO deptdocVOs = null;
		try {
			deptdocVOs = NCLocator.getInstance().lookup(IDeptQryService.class)
					.queryDeptVOByID(dept);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
		}

		if (deptdocVOs == null)
			return null;

		return deptdocVOs.getPk_fatherorg();
	}

	private static String getDeptDef1(String dept, String keyString)
			throws BusinessException {
		if (dept == null)
			return null;

		DeptVO deptdocVO = new DeptVO();
		deptdocVO.setPk_dept(dept);
		DeptVO deptdocVOs = NCLocator.getInstance().lookup(
				IDeptQryService.class).queryDeptVOByID(dept);

		if (deptdocVOs == null)
			return null;

		try {
			if (deptdocVOs.getDef1() != null) {
				return (String) deptdocVOs.getAttributeValue(keyString);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		return getDeptDef1(deptdocVOs.getPk_fatherorg(), keyString);
	}

	private static String getPsnDef1(String pk_psndoc, String keyString) {
		if (pk_psndoc == null)
			return null;

		PsndocVO[] psnDoc = null;
		try {
			psnDoc = NCLocator.getInstance().lookup(IPsndocQueryService.class)
					.queryPsndocVOsByCondition("pk_psndoc='" + pk_psndoc + "'");
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (psnDoc == null)
			return null;

		return (String) psnDoc[0].getAttributeValue(keyString);
	}
}