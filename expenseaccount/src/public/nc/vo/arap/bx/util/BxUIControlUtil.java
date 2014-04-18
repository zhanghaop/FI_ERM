package nc.vo.arap.bx.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErUtil;
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
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class BxUIControlUtil {
	/**
	 * 借款报销单多个子表的编码
	 * 与单据模版的页签编码对应
	 */
	public static String[] tableCodes =  new String[]{"er_busitem",
		   		"costsharedetail","er_bxcontrast","er_tbbdetail",
		   		"accrued_verify","jk_contrast","jk_busitem"};
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
	public static String getAgentWhereString(String jkbxr, String rolersql, String billtype, String user, String date,
			String pk_org) throws BusinessException {
		return ErUtil.getAgentWhereString(jkbxr, rolersql, billtype, user, date, pk_org);
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
	public JKBXVO doContrast(JKBXVO bxvo, List<BxcontrastVO> contrastsData) throws BusinessException {
		JKBXHeaderVO head = bxvo.getParentVO();
		UFDouble zero = UFDouble.ZERO_DBL;
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
			for (BxcontrastVO contrast : contrastsData) {
				cjkybje = cjkybje.add(contrast.getCjkybje());
			}

			// 计算冲借款
			setJeMul(contrastsData, head, new String[] { JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.CJKBBJE,
					JKBXHeaderVO.GROUPCJKBBJE, JKBXHeaderVO.GLOBALCJKBBJE });
			
			//表头还款,支付本币, 取借款单汇率, 日期取借款单日期
			if (cjkybje.doubleValue() > head.getYbje().doubleValue()) {
				// 冲借款金额>报销金额，则有还款
				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
				head.setGroupzfbbje(zero);
				head.setGlobalzfbbje(zero);
			} else if (cjkybje.doubleValue() < head.getYbje().doubleValue()) {
				// 冲借款金额<报销金额，则有支付
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
				head.setGrouphkbbje(zero);
				head.setGlobalhkbbje(zero);
			} else if (cjkybje.doubleValue() == head.getYbje().doubleValue()) {
				// 冲借款金额==报销金额，则既无还款，又无支付
				setHeadJe(head, head.getYbje().sub(cjkybje), new String[] { JKBXHeaderVO.ZFYBJE, JKBXHeaderVO.ZFBBJE,
						JKBXHeaderVO.GROUPZFBBJE, JKBXHeaderVO.GLOBALZFBBJE });
				head.setHkybje(zero);
				head.setHkbbje(zero);
				head.setGrouphkbbje(zero);
				head.setGlobalhkbbje(zero);

				setHeadJe(head, cjkybje.sub(head.getYbje()), new String[] { JKBXHeaderVO.HKYBJE, JKBXHeaderVO.HKBBJE,
						JKBXHeaderVO.GROUPHKBBJE, JKBXHeaderVO.GLOBALHKBBJE });
				head.setZfybje(zero);
				head.setZfbbje(zero);
				head.setGroupzfbbje(zero);
				head.setGlobalzfbbje(zero);
			}
		}

		// 折算表体冲借款金额
		caculateBodyCjkje(bxvo);
		//折算冲借款页签中还款金额 add by chenshuaia
		caculateContrastHkJe(bxvo, contrastsData);
		return bxvo;
	}

	/**
	 * 折算冲借款表体
	 * 
	 * @param bxvo
	 * @param contrastsData
	 * @author chenshuaia
	 */
	private void caculateContrastHkJe(JKBXVO bxvo, List<BxcontrastVO> contrastsData) {
		if (contrastsData == null || contrastsData.size() == 0) {
			return;
		}
		UFDouble hkybje = bxvo.getParentVO().getHkybje();
		UFDouble sumHkJe = UFDouble.ZERO_DBL;
		for (BxcontrastVO contrastVo : contrastsData) {
			if (contrastVo.getHkybje() != null) {
				sumHkJe = sumHkJe.add(contrastVo.getHkybje());
			}
		}

		if (hkybje.compareTo(sumHkJe) != 0) {
			UFDouble diffJe = hkybje.sub(sumHkJe);// 还款金额一般落在最后一行
			UFDouble diffJeAbs = diffJe.abs();
			boolean isMore = diffJe.compareTo(UFDouble.ZERO_DBL) > 0;

			for (int i = contrastsData.size() - 1; i >= 0; i--) {
				BxcontrastVO contrastVo = contrastsData.get(i);
				UFDouble bodyHkJe = contrastVo.getHkybje() == null ? UFDouble.ZERO_DBL : contrastVo.getHkybje();
				UFDouble bodyfyYbJe = contrastVo.getFyybje() == null ? UFDouble.ZERO_DBL : contrastVo.getFyybje();

				if (isMore) {
					if (bodyfyYbJe.compareTo(UFDouble.ZERO_DBL) > 0) {
						if (diffJeAbs.compareTo(bodyfyYbJe) > 0) {
							contrastVo.setFyybje(UFDouble.ZERO_DBL);
							contrastVo.setHkybje(contrastVo.getCjkybje());
							diffJeAbs = diffJeAbs.sub(bodyfyYbJe);
							continue;
						} else {
							contrastVo.setFyybje(bodyfyYbJe.sub(diffJeAbs));
							contrastVo.setHkybje(bodyHkJe.add(diffJeAbs));
							break;
						}
					}
				} else {
					if (bodyHkJe.compareTo(UFDouble.ZERO_DBL) > 0) {
						if (diffJeAbs.compareTo(bodyHkJe) > 0) {
							contrastVo.setHkybje(UFDouble.ZERO_DBL);
							contrastVo.setFyybje(contrastVo.getCjkybje());
							diffJeAbs = diffJeAbs.sub(bodyHkJe);
							continue;
						} else {
							contrastVo.setFyybje(bodyfyYbJe.add(diffJeAbs));
							contrastVo.setHkybje(bodyHkJe.sub(diffJeAbs));
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * 折算表体冲借款金额(同时计算支付或还款金额)
	 * 
	 * @author chendya
	 * @param bxvo
	 */
	public void caculateBodyCjkje(JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		if (childrenVO == null || childrenVO.length == 0)
			return;

		List<BXBusItemVO> noNullChildrenVoList = new ArrayList<BXBusItemVO>();

		for (int i = 0; i < childrenVO.length; i++) {
			if (!isJeNullRow(childrenVO[i])) {
				noNullChildrenVoList.add(childrenVO[i]);
			}
		}

		UFDouble cjkje = head.getCjkybje();//总冲借款金额，进行分配
		for (int i = 0; i < noNullChildrenVoList.size(); i++) {
			BXBusItemVO child = noNullChildrenVoList.get(i);

			if (cjkje != null) {
				// 还有没分配完的冲借款金额
				UFDouble ybje = child.getYbje();
				// 当前行是最后一行
				if (i == noNullChildrenVoList.size() - 1) {
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
				child.setAttributeValue(BXBusItemVO.CJKYBJE, new UFDouble(0));
				modifyValues(child);
				transYbjeToBbje(head, child);
			}
		}
	}

	@SuppressWarnings("unused")
	private BXBusItemVO[] filterJeNullRow(BXBusItemVO[] chilerenVO) {
		// 过滤掉没有金额的行
		List<BXBusItemVO> voList = new ArrayList<BXBusItemVO>();
		for (BXBusItemVO vo : chilerenVO) {
			UFDouble ybje = vo.getYbje();
			if (ybje == null) {
				ybje = UFDouble.ZERO_DBL;
			}
			if (UFDouble.ZERO_DBL.equals(ybje)) {
				// 没有原币金额的行不折算
				continue;
			}
			voList.add(vo);
		}
		return voList.toArray(new BXBusItemVO[0]);
	}

	private boolean isJeNullRow(BXBusItemVO vo) {
		// 判断是否是金额为空的行
		UFDouble ybje = vo.getYbje() == null ? UFDouble.ZERO_DBL : vo.getYbje();
		UFDouble cjkJe = vo.getCjkybje() == null ? UFDouble.ZERO_DBL : vo.getCjkybje();

		if (UFDouble.ZERO_DBL.equals(ybje) && UFDouble.ZERO_DBL.equals(cjkJe)) {
			// 原币金额为空则表示为空行
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
			UFDouble[] bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.BBJE, bbje[2]);
			itemVO.setAttributeValue(JKBXHeaderVO.BBYE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.CJKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.HKBBJE, bbje[2]);
			bbje = Currency.computeYFB(pk_corp, Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					head.getDjrq());
			itemVO.setAttributeValue(JKBXHeaderVO.ZFBBJE, bbje[2]);

			// 折算冲借款集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.CJKYBJE, BXBusItemVO.CJKYBJE,
					BXBusItemVO.GROUPCJKBBJE, BXBusItemVO.GLOBALCJKBBJE);

			// 折算支付集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.ZFYBJE, BXBusItemVO.ZFYBJE, BXBusItemVO.GROUPZFBBJE,
					BXBusItemVO.GLOBALZFBBJE);

			// 折算还款集团、全局本币金额
			caculateGroupAndGlobalBbje(head, itemVO, BXBusItemVO.HKYBJE, BXBusItemVO.HKYBJE, BXBusItemVO.GROUPHKBBJE,
					BXBusItemVO.GLOBALHKBBJE);

		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * 折算集团和全局本币金额
	 * 
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
	private static void caculateGroupAndGlobalBbje(JKBXHeaderVO head, BXBusItemVO itemVO, String ybjeField,
			String bbjeField, String groupbbjeField, String globalbbjeField) throws BusinessException {
		UFDouble[] moneys = Currency.computeGroupGlobalAmount((UFDouble) itemVO.getAttributeValue(ybjeField),
				(UFDouble) itemVO.getAttributeValue(bbjeField), head.getBzbm(), head.getDjrq(), head.getPk_org(),
				head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());
		// 集团
		itemVO.setAttributeValue(groupbbjeField, moneys[0]);
		// 全局
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
			vo.setAttributeValue(BXBusItemVO.HKYBJE, UFDouble.ZERO_DBL);
		} else {
			vo.setAttributeValue(BXBusItemVO.HKYBJE, cjkybje.sub(ybje));// 还款金额=冲借款金额-原币金额
			vo.setAttributeValue(BXBusItemVO.ZFYBJE, UFDouble.ZERO_DBL);
		}
	}

	/**
	 * 冲销页签的冲借款金额本币计算<br>
	 * 表头冲借款金额本币的计算
	 * @param contrastsData
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	private void setJeMul(List<BxcontrastVO> contrastsData, JKBXHeaderVO head, String[] yfbKeys)
			throws BusinessException {
		try {
			UFDouble[] yfbs = null;
			for (Iterator<BxcontrastVO> iter = contrastsData.iterator(); iter.hasNext();) {
				BxcontrastVO vo = iter.next();
				UFDouble cjkybje = vo.getCjkybje();
				UFDouble[] values = Currency.computeYFB(head.getPk_org(), Currency.Change_YBJE, head.getBzbm(),
						cjkybje, null, null, null, head.getBbhl(), head.getDjrq());

				vo.setCjkbbje(values[2]);
				vo.setBbje(values[2]);

				UFDouble[] money = Currency
						.computeGroupGlobalAmount(cjkybje, values[2], head.getBzbm(), head.getDjrq(), head.getPk_org(),
								head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

				vo.setGroupcjkbbje(money[0]);
				vo.setGlobalcjkbbje(money[1]);
				vo.setGroupbbje(money[0]);
				vo.setGlobalbbje(money[1]);

				if (yfbs == null) {
					yfbs = values;
				} else {
					for (int i = 0; i < 3; i++) {
						yfbs[i] = yfbs[i].add(values[i]);
					}
				}
			}
			UFDouble[] money2 = Currency.computeGroupGlobalAmount(yfbs[0], yfbs[2], head.getBzbm(), head.getDjrq(),
					head.getPk_org(), head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

			head.setAttributeValue(yfbKeys[0], yfbs[0]);
			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money2[0]);
			head.setAttributeValue(yfbKeys[3], money2[1]);

		} catch (BusinessException e) {
			// 设置本币错误.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
																														 * "设置本币错误!"
																														 */);
		}
	}

	/**
	 * 折算表头金额字段
	 * 
	 * @param head
	 * @param ybje
	 * @param yfbKeys
	 * @throws BusinessException
	 */
	private void setHeadJe(JKBXHeaderVO head, UFDouble ybje, String[] yfbKeys) throws BusinessException {
		try {
			head.setAttributeValue(yfbKeys[0], ybje);
			UFDouble[] yfbs = Currency.computeYFB(head.getPk_org(), Currency.Change_YBJE, head.getBzbm(), ybje, null,
					null, null, head.getBbhl(), head.getDjrq());
			UFDouble[] money = Currency.computeGroupGlobalAmount(ybje, yfbs[2], head.getBzbm(), head.getDjrq(),
					head.getPk_org(), head.getPk_group(), head.getGlobalbbhl(), head.getGroupbbhl());

			head.setAttributeValue(yfbKeys[1], yfbs[2]);
			head.setAttributeValue(yfbKeys[2], money[0]);
			head.setAttributeValue(yfbKeys[3], money[1]);

		} catch (BusinessException e) {
			// 设置本币错误.
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000009")/*
																														 * @
																														 * res
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
	public static List<JKBXVO> getInitBill(String pk_org, String pk_group, String djlxbm, boolean includeGroup)
			throws BusinessException {

		DjCondVO condVO = new DjCondVO();

		condVO.isInit = true;
		condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and ((isinitgroup='N' and pk_org='" + pk_org
				+ "') or isinitgroup='Y') ";
		condVO.isCHz = false;
		condVO.pk_group = new String[] { pk_group };
		// 组织和集团的合并查询，减少远程调用次数，查询完后分组,优先返回组织级的VO
		List<JKBXVO> all = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOs(0, -99, condVO);

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

	public static boolean doSimpleNoEquals(String reftype, String pk_corp, Object ruleKey, Object voKey) {
		IGeneralAccessor acc = null;

		if (reftype.equals("地区分类")) {/* -=notranslate=- */
			acc = GeneralAccessorFactory.getAccessor(IBDMetaDataIDConst.AREACLASS);
		}
		if (reftype.equals("部门")) {/* -=notranslate=- */
			acc = GeneralAccessorFactory.getAccessor(IBDMetaDataIDConst.DEPT);
		}

		if (acc != null) {
			while (voKey != null && voKey.toString().trim().length() != 0 && doSimpleNoEquals(voKey, ruleKey)) {
				IBDData doc = acc.getDocByPk(voKey.toString());
				if (doc == null)
					return doSimpleNoEquals(voKey, ruleKey);
				voKey = doc.getParentPk();
			}
			return doSimpleNoEquals(voKey, ruleKey);
		}

		return doSimpleNoEquals(ruleKey, voKey);
	}

	public static boolean doSimpleNoEquals(Object ruleKey, Object voKey) {
		if (ruleKey == null)
			return false;
		else if (voKey == null)
			return true;
		else
			return !VOUtils.simpleEquals(ruleKey.toString(), voKey.toString());
	}

	/**
	 * 执行表体报销标准
	 * 
	 * @param bxvo
	 *            单据VO
	 * @param reimRuleDataMap
	 *            报销标准Map
	 * @param bodyReimRuleMap
	 *            表体报销标准Map
	 * @return
	 */
	public static List<BodyEditVO> doBodyReimAction(JKBXVO bxvo, Map<String, List<SuperVO>> reimRuleDataMap,
			BillTempletBodyVO[] billtempletbodyvos, Map<String, List<SuperVO>> reimDimDataMap) {
		List<BodyEditVO> result = new ArrayList<BodyEditVO>();
		if (bxvo == null || bxvo.getParentVO() == null || bxvo.getParentVO().getDjlxbm() == null
				|| billtempletbodyvos==null || billtempletbodyvos.length==0)
			return result;

		String djlxbm = bxvo.getParentVO().getDjlxbm();

		//若还未设置报销标准维度或没有标准，则直接返回
		List<SuperVO> matchReimDim = reimDimDataMap.get(djlxbm);
		if(matchReimDim==null || reimRuleDataMap.get(djlxbm)==null)
			return result;
		
		//报销标准维度中，单据对应项 为子表的项，需要拿出来与表体进行比较
		//例如报销类型就对应子表的属性，需要控制子表的项，dims中存储<itemkey,billrefcode,数据类型名称>
		List<String> dims = new ArrayList<String>();
		String centcontrolitem = null;
		for(SuperVO vo:reimDimDataMap.get(djlxbm))
		{
			ReimRuleDimVO dimvo = (ReimRuleDimVO)vo;
			if(dimvo.getControlflag().booleanValue())
				centcontrolitem=dimvo.getCorrespondingitem();
			for(String tablecode:tableCodes)
				if(dimvo.getBillrefcode()!=null && dimvo.getBillrefcode().startsWith(tablecode))
				{
					dims.add(dimvo.getCorrespondingitem()+","+dimvo.getBillrefcode().substring(dimvo.getBillrefcode().indexOf(".")+1)
							+","+dimvo.getDatatypename());
				}
		}
		if(centcontrolitem==null)
			return result;
		//选出与单据对应的报销标准，只匹配表头
		List<ReimRulerVO> matchReimRule = getMatchReimRuleByHead(bxvo, reimRuleDataMap.get(djlxbm),matchReimDim);
		List<String> matchedpks = new ArrayList<String>();
		for(ReimRulerVO mvo:matchReimRule){
			matchedpks.add(mvo.getAttributeValue(centcontrolitem).toString());
		}
		//将所有标准的显示项提取出来，修改表头时需要清零 pk showitem
		List<String> pks = new ArrayList<String>();
		for(SuperVO vo:reimRuleDataMap.get(djlxbm)){
			if(!matchedpks.contains(vo.getAttributeValue(centcontrolitem).toString()) 
					&& !pks.contains(vo.getAttributeValue(centcontrolitem).toString())){
				pks.add(vo.getAttributeValue(centcontrolitem).toString());
				ReimRulerVO newvo = new ReimRulerVO();
				newvo.setAttributeValue(centcontrolitem, vo.getAttributeValue(centcontrolitem));
				newvo.setShowitem(((ReimRulerVO)vo).getShowitem());
				newvo.setShowitem_name(((ReimRulerVO)vo).getShowitem_name());
				newvo.setAmount(new UFDouble(0));
				matchReimRule.add(newvo);
			}
		}
		//根据上面的报销标准计算出单据模板上应该显示标准的项与控制项
		addBodyEdit(result,bxvo,billtempletbodyvos,matchReimRule,dims,centcontrolitem);
		return result;
	}

	/**
	 *  表体报销标准Map<tablecode@itemkey,centcontrolPK（默认为费用类型pk）>循环
	 * @param bodyReimRuleMap
	 * @return
	 */
	public static void addBodyEdit(List<BodyEditVO> result,JKBXVO bxvo,
			BillTempletBodyVO[] billtempletbodyvos,List<ReimRulerVO> matchReimRule,
			List<String> dims,String centcontrolitem) {
		//逐一遍历表体每个页签的每一个item，查看其元数据属性，提取显示项与控制项
		HashMap<String, String> bodyReimRuleMap = new HashMap<String, String>();
		for(BillTempletBodyVO bodyvo:billtempletbodyvos)
		{
			//该项没有元数据，直接跳过
			if(bodyvo.getMetadataproperty()==null){
				continue;
			}
			else{
				for(ReimRulerVO rule:matchReimRule){
					//提取vo的单据显示项、控制项与核心控制项PK
					String showitem = rule.getShowitem_name();
					String controlitem = rule.getControlitem_name();
					//0不控制 1提示 2控制
					Integer controlflag = rule.getControlflag();
					String centcontrolPK = rule.getAttributeValue(centcontrolitem).toString();
				
					//查看页签的元数据属性
					String metadata = bodyvo.getMetadataproperty();
					metadata = metadata.substring(metadata.indexOf(".") + 1);
					//若与标准的显示项相同，则加入<页签item,核心控制项pk>
					if(metadata.equals(showitem)){
						bodyReimRuleMap.put(bodyvo.getTable_code()+ "."+ bodyvo.getItemkey(), 
								centcontrolPK);
						break;
					}
					//若与标准的控制项相同，则加入<页签item,核心控制项pk;控制模式>
					if(metadata.equals(controlitem)){
						bodyReimRuleMap.put(bodyvo.getTable_code()+ "."+ bodyvo.getItemkey(), 
								centcontrolPK+";"+controlflag);
						break;
					}
				}
			}
		}//for
		for (String key : bodyReimRuleMap.keySet()) {
			String centcontrolKey = bodyReimRuleMap.get(key);// 费用类型key
			String[] keys = key.split(ReimRulerVO.REMRULE_SPLITER);
			String tableCode = keys[0];
			String itemkey = keys[1];
			CircularlyAccessibleValueObject[] bodyValueVOs = bxvo.getTableVO(tableCode);

			if (bodyValueVOs != null) {
				int row = -1;
				// 对对应tableCode下的所有数据行进行遍历
				for (CircularlyAccessibleValueObject body : bodyValueVOs) {
					row++;

					String[] pks = null;
					String pk = centcontrolKey;
					if(centcontrolKey.contains(";")){
						pks= centcontrolKey.split(";");
						pk = pks[0];
					}
					else{
						BodyEditVO bodyEditVO2 = new BodyEditVO();
						bodyEditVO2.setValue(new UFDouble(0));
						bodyEditVO2.setRow(row);
						bodyEditVO2.setItemkey(itemkey);
						bodyEditVO2.setTablecode(tableCode);
						result.add(bodyEditVO2);
					}

					for (ReimRulerVO rule : matchReimRule) {
						// 判断核心控制项（默认为费用类型）是否相同
						if (doSimpleNoEquals(rule.getAttributeValue(centcontrolitem), pk)) {
							continue;
						}
						// 查看维度设置与表体中对应字段值是否相同
						boolean match = true;
						List<BodyEditVO> dimlist = new ArrayList<BodyEditVO>();
						for(String str:dims){
							String[] itemvalues = str.split(",");
							//dims中存的是[PK_REIMTYPE, pk_reimtype, 报销类型]，最后一位是数据类型
							if(itemvalues.length>3 && itemvalues[2]!=null){
								if (doSimpleNoEquals(itemvalues[2], bxvo.getParentVO().getPk_group(),
										rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
									match = false;
									break;
								}
							}
							else{
								if (doSimpleNoEquals(rule.getAttributeValue(itemvalues[0]), body.getAttributeValue(itemvalues[1]))) {
									match = false;
									break;
								}
							}
							BodyEditVO bvo = new BodyEditVO();
							bvo.setTablecode(tableCode);
							bvo.setItemkey(itemvalues[1]);
							dimlist.add(bvo);
						}
						if (match) {
							//pks为空说明不包含分号，为显示项
							if(pks==null){
								BodyEditVO bodyEditVO = new BodyEditVO();
								bodyEditVO.setValue(rule.getAmount());
								bodyEditVO.setRow(row);
								bodyEditVO.setItemkey(itemkey);
								bodyEditVO.setTablecode(tableCode);
								result.add(bodyEditVO);
							}
							else{
								ControlBodyEditVO bodyEditVO = new ControlBodyEditVO();
								bodyEditVO.setValue(rule.getAmount());
								bodyEditVO.setRow(row);
								bodyEditVO.setItemkey(itemkey);
								bodyEditVO.setTablecode(tableCode);
								bodyEditVO.setDimlist(dimlist);
								if(rule.getControlformula()!=null)
									bodyEditVO.setFormulaRule(rule.getControlformula());
								if(pks.length>1){
									try{
									bodyEditVO.setTip(Integer.valueOf(pks[1]));
									}catch(Exception e){
										
									}
								}
								result.add(bodyEditVO);
								
							}
						}
					}
				}
			}
		}
	}
	/**
	 * 表头报销标准
	 * 
	 * @param bxvo
	 *            单据VO
	 * @param reimRuleDataMap
	 *            报销标准Map
	 * @param reimRuleDataMap
	 *            标准维度Map
	 * @param expenseType
	 *            费用类型
	 * @param reimtypeMap
	 *            报销类型
	 * @return
	 */
	public static List<String> doHeadReimAction(JKBXVO bxvo, Map<String, List<SuperVO>> reimRuleDataMap,
			Map<String, List<SuperVO>> reimDimDataMap) {//,Map<String, SuperVO> expenseType, Map<String, SuperVO> reimtypeMap
		List<String> reimrule = new ArrayList<String>();

		if (bxvo == null || bxvo.getParentVO() == null || bxvo.getParentVO().getDjlxbm() == null)
			return reimrule;

		String djlxbm = bxvo.getParentVO().getDjlxbm();
		List<SuperVO> matchReimDim = reimDimDataMap.get(djlxbm);
		if(matchReimDim==null)
			return reimrule;
		// 根据单据类型获取自定义报销标准
		List<ReimRulerVO> matchReimRule = getMatchReimRuleByHead(bxvo, reimRuleDataMap.get(djlxbm),matchReimDim);
		//选出配置标准维度时设置的需要展示的列
		List<String> dims = new ArrayList<String>();
		for(SuperVO vo:matchReimDim)
		{
			ReimRuleDimVO dimvo = (ReimRuleDimVO)vo;
			if(dimvo.getShowflag().booleanValue())
			{
				dims.add(dimvo.getCorrespondingitem());
			}
		}
		for (ReimRulerVO rule : matchReimRule) {
			StringBuffer reim = new StringBuffer("");
			for (String str : dims)
			{
				if(str.equalsIgnoreCase("amount"))
					continue;
				if(rule.getAttributeValue(str+"_name")!=null)
					reim.append(rule.getAttributeValue(str+"_name")+":");
			}
			reim.append(rule.getAmount());
			reimrule.add(reim.toString());
		}
		return reimrule;
	}

	public static List<ReimRulerVO> getMatchReimRuleByHead(JKBXVO bxvo,
			List<? extends SuperVO> dataList,List<? extends SuperVO> matchReimDim) {
		List<ReimRulerVO> matchedRule = new ArrayList<ReimRulerVO>();

		if (dataList == null || dataList.size() == 0 || bxvo == null || bxvo.getParentVO() == null || matchReimDim==null)
			return matchedRule;

		ReimRulerVO[] ruleVOs = dataList.toArray(new ReimRulerVO[] {});
		Arrays.sort(ruleVOs);
		// 用来保存构造的报销标准
		HashSet<String> ruleset = new HashSet<String>();

		for (ReimRulerVO rule : ruleVOs) {
			StringBuffer bodykey = new StringBuffer();
			boolean match = true;
			for (SuperVO vo : matchReimDim) {
				ReimRuleDimVO dim = (ReimRuleDimVO)vo;
				if (rule.getAttributeValue(dim.getCorrespondingitem()) != null) {
					bodykey.append(rule.getAttributeValue(dim.getCorrespondingitem()));
				}
				//如果此列没有设置单据对应项或对应标准上为空，则不做比较，默认为此标准可用
				if(dim.getBillrefcode()==null || rule.getAttributeValue(dim.getCorrespondingitem())==null)
					continue;
				String itemvalue = dim.getBillrefcode();
				Object headvalue = "";
				if (itemvalue.contains(".")) {
					//包含"."，说明可能是主表的下拉项，也可能是子表
					//主表下拉项只支持报销人、收款人、报销部门、费用承担部门
					String[] keys = itemvalue.split(ReimRulerVO.REMRULE_SPLITER);
					if (keys[0].equals(ReimRulerVO.Reim_jkbxr_key)
							|| itemvalue.startsWith(ReimRulerVO.Reim_receiver_key)) {
						headvalue = getPsnDef1((String) bxvo.getParentVO().getAttributeValue(keys[0]), keys[1]);
					} else if (keys[0].equals(ReimRulerVO.Reim_deptid_key)
							|| itemvalue.startsWith(ReimRulerVO.Reim_fydeptid_key)) {
						try {
							headvalue = getDeptDef1((String) bxvo.getParentVO().getAttributeValue(keys[0]),keys[1]);
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					}
				} else {
					//不包含"."，直接与单据的ParentVO进行比较
					//部门需要特殊处理，本部门没有的话要一直向上找
					if(dim.getCorrespondingitem().equalsIgnoreCase(ReimRulerVO.PK_DEPTID))
					{
						String deptid = bxvo.getParentVO().getDeptid();
						while (deptid != null && doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
							//如果部门不一致则一直向上查找，直到找到相同的部门为止
							deptid = getFatherDept(deptid);
						}

						if (doSimpleNoEquals(rule.getPk_deptid(), deptid)) {
							match = false;
							break;
						}
					}
					headvalue=bxvo.getParentVO().getAttributeValue(dim.getBillrefcode());
				}
				if(headvalue==null || headvalue.equals(""))
					continue;
				// 当表头有中有一个条件与标准不同，则跳出，表示不符合
				if (doSimpleNoEquals(dim.getDatatypename(), bxvo.getParentVO().getPk_group(),
						rule.getAttributeValue(dim.getCorrespondingitem()), headvalue)) {
					match = false;
					break;
				}
			}

			if (match == false)
				continue;

			int scale = 2;
			if (bxvo.getParentVO().getBzbm() != null) {
				try {
					scale = Currency.getCurrDigit(bxvo.getParentVO().getBzbm());
				} catch (Exception e) {
					ExceptionHandler.consume(e);
				}
				rule.setAmount(rule.getAmount().setScale(scale, UFDouble.ROUND_HALF_UP));
			}

			if (!ruleset.contains(bodykey.toString())) {
				matchedRule.add(rule);
				ruleset.add(bodykey.toString());
			}
		}
		return matchedRule;
	}

	private static String getFatherDept(String dept) {
		if (dept == null || dept.trim().length() == 0)
			return null;

		DeptVO deptdocVOs = null;
		try {
			deptdocVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryDeptVOByID(dept);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance("ermExceptionLog").error(e);
		}

		if (deptdocVOs == null)
			return null;

		return deptdocVOs.getPk_fatherorg();
	}

	private static String getDeptDef1(String dept, String keyString) throws BusinessException {
		if (dept == null)
			return null;

		DeptVO deptdocVO = new DeptVO();
		deptdocVO.setPk_dept(dept);
		DeptVO deptdocVOs = NCLocator.getInstance().lookup(IDeptQryService.class).queryDeptVOByID(dept);

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