package nc.erm.mobile.billaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.erm.mobile.eventhandler.MultiVersionUtil;
import nc.erm.mobile.util.ErMobileUtil;
import nc.erm.mobile.util.JsonData;
import nc.itf.bd.psnbankacc.IPsnBankaccPubService;
import nc.itf.er.reimtype.IReimTypeService;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.arap.bx.util.ControlBodyEditVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.reimrule.ReimRuleDimVO;
import nc.vo.er.reimrule.ReimRulerVO;
import nc.vo.er.util.ErmBillCalUtil;
import nc.vo.erm.util.VOUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class JKBXBillAddAction extends BillAddAction{
	private JKBXVO billvo;
	// 报销规则组织级数据缓存，<组织，标准>
	private Map<String, Map<String, List<SuperVO>>> reimRuleDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>();
	private Map<String, Map<String, List<SuperVO>>> reimDimDataCacheMap = new HashMap<String, Map<String, List<SuperVO>>>();
	/**
	 * 报销标准需要控制的项,存（item、值），aftereditor中用
	 */
	private List<ControlBodyEditVO> controlRule = new ArrayList<ControlBodyEditVO>();
			
	/**
	 * 将前台的单据默认值与传过来的VO值合并
	 * @param backVO
	 * @param frontVO
	 */
	private void combineVO(JKBXVO backVO, JKBXVO frontVO) {
		if(backVO.getParentVO().getPk_jkbx()==null || frontVO.getParentVO().getPk_jkbx()==null
				|| backVO.getParentVO().getPk_jkbx().equals(frontVO.getParentVO().getPk_jkbx())){
			backVO.getParentVO().combineVO(frontVO.getParentVO());
		}
	}
	/**
	 * 
	 * @param jsonData
	 * @param resVO
	 * @param object 初始化后的值
	 * @throws BusinessException
	 */
	public void getJKBXValue(JsonData jsonData,Object resVO,Object object) throws BusinessException {
		if(object == null){
			return;
		}
		billvo = (JKBXVO)object;
		//设置精度,然后在设置值
		JKBXHeaderVO parentVO = billvo.getParentVO();
		//首先取模板上的默认值
		Object backvo = jsonData.newObject();
		combineVO((JKBXVO)object,(JKBXVO)backvo);					
		//报销VO分页签设置业务行
		//resetBusItemVOs(object);
								
		// 根据对公支付标志位设置收款信息
		setSkInfByIscusupplier(object);
		//过滤单据卡片上的组织
		//filtOrgField();
				
		UFDate date = (UFDate) parentVO.getDjrq();
		//单据日期为空，去业务日期
		if(date == null || StringUtil.isEmpty(date.toString())){
			long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
			date = new UFDate(busitime);
		}
		//过滤主面板的组织,需要设置日期后再设置
		//ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(),date), getBillOrgPanel().getRefPane());
											
		String pkOrg = parentVO.getPk_org();
		//根据组织设置单据默认值
		String currentBillTypeCode = parentVO.getDjlxbm();
		setDefaultWithOrg(parentVO,parentVO.getDjdl(), currentBillTypeCode, pkOrg, false);
				
		if (resVO != null) {
			// 拉单页面字段处理
			//getAddFromMtAppEditorUtil().resetBillItemOnAdd();
		}
				
		// 初始化根据分摊标志显示或隐藏分摊页签
		//initCostPageShow(null);
		//收款对象可编辑
		//getEventHandle().afterEditPayarget(false);
				
				
		// 加载常用单据，根据汇率重新计算表头表体页签金额值，注意分摊应在初始化分摊页签后
		// 以后可以考虑，因为汇率带来的金额变化的逻辑放在后台处理
//				String billTypeCode = ((ErmBillBillManageModel) (getModel())).getCurrentBillTypeCode();
//				DjLXVO djdl = ((ErmBillBillManageModel) (getModel())).getCurrentDjlx(billTypeCode);
//				if (djdl != null && djdl.getIsloadtemplate() != null && djdl.getIsloadtemplate().booleanValue()
//						&& getHeadValue(JKBXHeaderVO.PK_ORG) != null && getHeadValue(JKBXHeaderVO.PK_ITEM) == null) {
//					getEventHandle().resetBodyFinYFB();
//					getEventHandle().getEventHandleUtil().setHeadYFB();
//				}
			
			//过滤单据卡片上的部门
			filtDeptField(parentVO);

//				helper.getAfterEditUtil().initPayentityItems(false);
//				helper.getAfterEditUtil().initCostentityItems(false);
//				helper.getAfterEditUtil().initUseEntityItems(false);
//				helper.getAfterEditUtil().initPayorgentityItems(false);
			prepareForNullJe((JKBXVO)object);

			// 在报销借款单位为空时，需要先设置报销借款单位后，才能编辑其他字段;过滤借款报销人
//				doPKOrgField();
			
			//过滤相应字段
//				filterHeadItem();
			
		//设置界面多版本
		setHeadOrgMultiVersion(jsonData,new String[] { JKBXHeaderVO.PK_ORG_V, JKBXHeaderVO.FYDWBM_V,
				JKBXHeaderVO.DWBM_V, JKBXHeaderVO.PK_PCORG_V, JKBXHeaderVO.PK_PAYORG_V }, new String[] {
				JKBXHeaderVO.PK_ORG, JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM, JKBXHeaderVO.PK_PCORG,
				JKBXHeaderVO.PK_PAYORG });

		setHeadDeptMultiVersion(jsonData,JKBXHeaderVO.DEPTID_V, parentVO.getDwbm(),
				JKBXHeaderVO.DEPTID);
		setHeadDeptMultiVersion(jsonData,JKBXHeaderVO.FYDEPTID_V, parentVO.getFydwbm(),
				JKBXHeaderVO.FYDEPTID);
		BillTabVO[] tabvos = jsonData.getBillBaseTabVOsByPosition(IBillItem.BODY);
		BXBusItemVO bodyVO = (BXBusItemVO) setBodyDefaultValue(parentVO,tabvos[0].getTabcode(),tabvos[0].getMetadataclass());
		bodyVO.setRowno(0);
		billvo.setChildrenVO(new BXBusItemVO[]{bodyVO});
	}

	/**
	 * 设置组织多版本
	 * 
	 * @throws BusinessException
	 */
	public void setHeadOrgMultiVersion(JsonData jsonData,String[] fields, String[] ofields)
			throws BusinessException {
		for (int i = 0; i < fields.length; i++) {
			Object value = ((JKBXVO)billvo).getParentVO().getAttributeValue(ofields[i]);
			if(value == null)
				continue;
			MultiVersionUtil.setHeadOrgMultiVersion(jsonData,fields[i], value.toString(), (JKBXVO)billvo);
		}
	}
	public void setHeadDeptMultiVersion(JsonData jsonData,String field_v, String pk_org, String field) throws BusinessException {
		String value = ((JKBXVO)billvo).getParentVO().getAttributeValue(field).toString();
		MultiVersionUtil.setHeadDeptMultiVersion(jsonData,field_v, pk_org, value, ((JKBXVO)billvo).getParentVO());
	}
	
	private void setSkInfByIscusupplier(Object object) {
		//对公支付时，收款人、个人银行账户不可编辑
		if(UFBoolean.TRUE.equals(((JKBXVO)object).getParentVO().getIscusupplier())){
//			if (getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER) != null) {
//				getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setValue(null);
//			}
//			getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER).setEnabled(false);
//			getBillCardPanel().getHeadItem(JKBXHeaderVO.SKYHZH).setEnabled(false);
		}
	}
	
	// 报销规则
		public void doReimRuleAction(JKBXVO bxvo) {
			JKBXVO vo = bxvo;
			if (vo == null) {
				return;
			}
			
			// 表头报销规则，组织级+集团级
			StringBuffer reim = new StringBuffer("");
			List<String> reimrule = BxUIControlUtil.doHeadReimAction(vo,
					getReimRuleDataMap(), getReimDimDataMap());
			List<String> reimrule1 = BxUIControlUtil.doHeadReimAction(vo,
							reimRuleDataCacheMap.get(ReimRulerVO.PKORG), reimDimDataCacheMap.get(ReimRulerVO.PKORG));
			for(String str:reimrule1){
				if(!reimrule.contains(str))
					reimrule.add(str);
			}
			for(String str:reimrule){
				reim.append(str+"\t");
			}
			bxvo.getParentVO().setReimrule(reim.toString());
//			doBodyReimAction();
		}
		// 根据集团级参数“报销标准适用规则”,来取组织
		public String getPkOrg(String pk_group){
			//获取组织
			String pk_org = null;
			try {
				String PARAM_ER8 = SysInit.getParaString(pk_group, BXParamConstant.PARAM_ER_REIMRULE);
				if (PARAM_ER8 != null) {
					if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_PK_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getPk_org();//(String) getHeadValue(JKBXHeaderVO.PK_ORG);
					} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_OPERATOR_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getDwbm();//(String) getHeadValue(JKBXHeaderVO.DWBM);
					} else if (PARAM_ER8.equals(BXParamConstant.ER_ER_REIMRULE_ASSUME_ORG)) {
						pk_org = ((JKBXVO)billvo).getParentVO().getFydwbm();//(String) getHeadValue(JKBXHeaderVO.FYDWBM);
					}
				}
			} catch (BusinessException e1) {
				ExceptionHandler.consume(e1);
			}
			return pk_org;
		}
		
		//获取当前组织级标准
		public Map<String, List<SuperVO>> getReimRuleDataMap() {
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			String pk_org = getPkOrg(pk_group);
			//如果集团级标准为空则需要首次获取集团级和组织级标准
			if (reimRuleDataCacheMap.get(ReimRulerVO.PKORG) == null) {
				List<ReimRulerVO> vos;
				List<ReimRuleDimVO> vodims;
				try {
					if (pk_org != null) {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimRuler(null, pk_group,pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimDim(null, pk_group,pk_org);
						List<SuperVO> vos1 = new ArrayList<SuperVO>();
						List<SuperVO> vos2 = new ArrayList<SuperVO>();
						if(vos!=null){
							for(ReimRulerVO vo:vos){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
						vos1.clear();
						vos2.clear();
						if(vodims!=null){
							for(ReimRuleDimVO vo:vodims){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					}
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			else{
				//如果集团级标准不为空说明已经取到了，直接取组织就可以了
				if (pk_org != null) {
					if (reimRuleDataCacheMap.get(pk_org) == null) {
						List<ReimRulerVO> vos;
						List<ReimRuleDimVO> vodims;
						try {
							vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimRuler(null, pk_group,pk_org);
							vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimDim(null, pk_group,pk_org);
							reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					} else {
						return reimRuleDataCacheMap.get(pk_org);
					}
				}
			}
			return reimRuleDataCacheMap.get(pk_org);
		}
		
		//获取当前组织级标准维度
		public Map<String, List<SuperVO>> getReimDimDataMap() {
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			String pk_org = getPkOrg(pk_group);
			//如果集团级标准为空则需要首次获取集团级和组织级标准
			if (reimDimDataCacheMap.get(ReimRulerVO.PKORG) == null) {
				List<ReimRulerVO> vos;
				List<ReimRuleDimVO> vodims;
				try {
					if (pk_org != null) {
						vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimRuler(null, pk_group,pk_org);
						vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryGroupOrgReimDim(null, pk_group,pk_org);
						List<SuperVO> vos1 = new ArrayList<SuperVO>();
						List<SuperVO> vos2 = new ArrayList<SuperVO>();
						if(vos!=null){
							for(ReimRulerVO vo:vos){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimRuleDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimRuleDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
						vos1.clear();
						vos2.clear();
						if(vodims!=null){
							for(ReimRuleDimVO vo:vodims){
								if(vo.getPk_org().equals(ReimRulerVO.PKORG))
									vos1.add(vo);
								else
									vos2.add(vo);
							}
						}
						reimDimDataCacheMap.put(ReimRulerVO.PKORG, VOUtils.changeCollectionToMapList(vos1, "pk_billtype"));
						reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos2, "pk_billtype"));
					}
				} catch (BusinessException e) {
					ExceptionHandler.consume(e);
				}
			}
			else{
				//如果集团级标准不为空说明已经取到了，直接取组织就可以了
				if (pk_org != null) {
					if (reimDimDataCacheMap.get(pk_org) == null) {
						List<ReimRulerVO> vos;
						List<ReimRuleDimVO> vodims;
						try {
							vos = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimRuler(null, pk_group,pk_org);
							vodims = NCLocator.getInstance().lookup(IReimTypeService.class)
							.queryReimDim(null, pk_group,pk_org);
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vos, "pk_billtype"));
							reimDimDataCacheMap.put(pk_org, VOUtils.changeCollectionToMapList(vodims, "pk_billtype"));
						} catch (BusinessException e) {
							ExceptionHandler.consume(e);
						}
					} else {
						return reimDimDataCacheMap.get(pk_org);
					}
				}
			}
			return reimDimDataCacheMap.get(pk_org);
		}
	/**
	 * 没有设置金额的字段，设置为0
	 * 
	 */
	public void prepareForNullJe(JKBXVO bxvo) {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		String[] jeField = JKBXHeaderVO.getJeField();
		String[] bodyJeField = BXBusItemVO.getBodyJeFieldForDecimal();
		for (String field : jeField) {
			if (parentVO.getAttributeValue(field) == null) {
				parentVO.setAttributeValue(field, UFDouble.ZERO_DBL);
			}
		}

		for (String field : bodyJeField) {
			BXBusItemVO[] bxBusItemVOS = bxvo.getBxBusItemVOS();
			if (bxBusItemVOS != null) {
				for (BXBusItemVO item : bxBusItemVOS) {
					if (item.getAttributeValue(field) == null) {
						item.setAttributeValue(field, UFDouble.ZERO_DBL);
					}
				}
			}
		}
	}
	/**
	 * @author wangled 设置币种相关信息
	 */
	public void setCurrencyInfo(JKBXHeaderVO parentVO,String pk_org) throws BusinessException {
		// 原币币种
		String pk_currtype = null;

		// 组织本币币种
		String pk_loccurrency = null;

		if (pk_org != null && pk_org.length() != 0) {
			pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
		} else {
			// 组织为空，取默认组织
			pk_org = ErMobileUtil.getBXDefaultOrgUnit();
			if (pk_org != null && pk_org.trim().length() > 0) {
				pk_loccurrency = Currency.getOrgLocalCurrPK(pk_org);
			} else {
				// 没有组织
				return;
			}
		}

		if (pk_currtype == null) {
			// 优先取交易类型 中定义的币种
			DjLXVO currentDjlx = ErmDjlxCache.getInstance().getDjlxVO(
					InvocationInfoProxy.getInstance().getGroupId(), parentVO.getDjlxbm());
			pk_currtype = currentDjlx.getDefcurrency();
			// 如果交易类型 中定义的币种,则查询组织的本位币
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
			}
			// 默认组织本币作为原币进行报销
			parentVO.setBzbm(pk_currtype);
		}

		// 单据日期
		UFDate date = parentVO.getDjrq();
		// 设置汇率是否可编辑
		setCurrencyInfo(parentVO,pk_org, pk_loccurrency, pk_currtype, date);
	}
	/**
	 * 根据组织本币设置币种相关信息
	 * 
	 * @param pk_org
	 * @param pk_loccurrency
	 * @param pk_currtype
	 */
	public void setCurrencyInfo(JKBXHeaderVO parentVO,String pk_org, String pk_loccurrency,
			String pk_currtype, UFDate date) {
		try {
			// 如果表头币种字段为空,取组织本币设置为默认币种
			if (pk_currtype == null || pk_currtype.trim().length() == 0) {
				pk_currtype = pk_loccurrency;
				parentVO.setBzbm(pk_currtype);
			}
			// 返回汇率(本币，集团本币，全局本币汇率)
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			UFDouble[] rates = ErmBillCalUtil.getRate(pk_currtype, pk_org,
					pk_group, date, pk_loccurrency);
			UFDouble hl = rates[0];
			UFDouble grouphl = rates[1];
			UFDouble globalhl = rates[2];

			// 重设汇率精度
//			try {
//				BXUiUtil.resetDecimal(getBillCardPanel(), pk_org, pk_currtype);
//			} catch (Exception ex) {
//				ExceptionHandler.handleExceptionRuntime(ex);
//			}
			parentVO.setBbhl(hl);
			parentVO.setGlobalbbhl(globalhl);
			parentVO.setGroupbbhl(grouphl);
			
			// 根据汇率折算原辅币金额字段
			resetYFB_HL(parentVO,hl, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE);
			// 设置相关汇率能否编辑
			//setCurrRateEnable(pk_org, pk_currtype);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	protected void resetYFB_HL(JKBXHeaderVO parentVO,UFDouble hl, String ybjeField, String bbjeField) {
		if (parentVO.getBzbm() == null)
			return;

		try {
			String pk_org = parentVO.getPk_org();
			UFDouble[] yfbs = Currency.computeYFB(pk_org,
					Currency.Change_YBCurr, parentVO.getBzbm(), parentVO.getYbjeField() == null ? null
							: new UFDouble(parentVO.getYbjeField().toString()),
					null, parentVO.getBbjeField() == null ? null
							: new UFDouble(parentVO.getBbjeField().toString()),
					null, hl, ErMobileUtil.getSysdate());

			if (yfbs[0] != null) {
//				getBillCardPanel().setHeadItem(ybjeField, yfbs[0]);
				parentVO.setTotal(yfbs[0]);
			}
			if (yfbs[2] != null) {
//				getBillCardPanel().setHeadItem(bbjeField, yfbs[2]);
			}
			if (yfbs[4] != null) {
				parentVO.setBbhl(yfbs[4]);
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	/**
	 * 利润中心，先判断是否是利润中心，不是不设值
	 * 
	 * @param value
	 * @param field
	 * @author: wangyhh@ufida.com.cn
	 */
	private void fillPcOrg(Object value, String field) {
		try {
			OrgVO[] orgs = NCLocator.getInstance().lookup(IOrgUnitPubService_C.class).getOrgs(new String[]{(String) value}, new String[]{});
//			if(orgs[0].getOrgtype15().booleanValue() && getBillCardPanel().getHeadItem(field).isEnabled()){
//				getBillCardPanel().setHeadItem(field, value);
//			}
		} catch (BusinessException e) {
			Logger.error(e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *将key字段设置为主组织
	 * 
	 * @param pk_org
	 * @param key
	 */
	private void setPk_org(JKBXHeaderVO parentVO,String pk_org, String key) {
		if (parentVO.getAttributeValue(key) == null) {
			if (JKBXHeaderVO.PK_PCORG.equals(key)) {
				// 利润中心，先判断是否是利润中心，不是不设值
				fillPcOrg(pk_org, key);
			} else {
				parentVO.setAttributeValue(key, pk_org);
			}
		}
	}
	/**
	 * @author wangle 根据组织设置单据默认值
	 * @param strDjdl
	 * @param strDjlxbm
	 * @param org
	 * @param isAdd
	 * @param permOrgs
	 * @throws BusinessException
	 */
	public void setDefaultWithOrg(JKBXHeaderVO parentVO,String strDjdl, String strDjlxbm,
			String pk_org, boolean isEdit) throws BusinessException {
		// 设置费用承担单位，借款人单位和利润中心
		String[] keys = new String[] { JKBXHeaderVO.FYDWBM, JKBXHeaderVO.DWBM,
				JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_FIORG,
				JKBXHeaderVO.PK_PAYORG };
		for (String key : keys) {
			setPk_org(parentVO,pk_org, key);
		}
		
		//
		// 常用单据和拉单过来时，都不加载常用单据
//		if (!(((ErmBillBillManageModel)getModel()).iscydj())&& editor.getResVO()==null) {
//			// 加载组织的常用单据
//			setInitBill(pk_org);
//			String initBill_org = (String)editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			//在加载完常用单据后，要判断常用的组织是否期初关闭
//			if(initBill_org != null){
//				checkQCClose(initBill_org);
//				getModel().getContext().setPk_org(initBill_org);
//				pk_org=(String) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
//			}
//		}
		
//		//在加载完常用单据后，要判断常用的组织是否期初关闭
		String initBill_org = parentVO.getPk_org();
		if(initBill_org != null){
			//checkQCClose(initBill_org);
			pk_org = parentVO.getPk_org();
		}

		// 设置组织币种汇率信息
		setCurrencyInfo(parentVO,pk_org);
		
		// 根据借款报销单位自动带出收款银行帐号
		setDefaultSkyhzhByReceiver(parentVO);

		// 设置最迟还款日期
		try {
			setZhrq(pk_org);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
		
		// v6.1新增单据 根据费用承担部门带出默认成本中心 TODO : 在后台处理好了，前台只需设置一下表体即可
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject() == null) {// 优先带出常用单据中组织
//
//			Object pk_body_center = getBillCardPanel().getBodyValueAt(0, BXBusItemVO.PK_RESACOSTCENTER);
//			if (pk_body_center == null) {
//				String pk_fydept = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDEPTID).getValueObject();
//				String pk_pcorg = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PCORG).getValueObject();
//				setCostCenter(pk_fydept, pk_pcorg);
//			}
//		}
		
//		Object valueObject = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_RESACOSTCENTER).getValueObject();
//		if(valueObject != null){
//			String pk_costcenter =valueObject.toString();
//			changeBusItemValue(BXBusItemVO.PK_RESACOSTCENTER, pk_costcenter);
//		}
		// 插入业务流程 --> 在后台处理
		//insertBusitype(strDjdl, pk_org);
	}
	
	/**
	 * 收款人更换时，设置默认个人银行账户
	 * @param parentVO 
	 */
	public void setDefaultSkyhzhByReceiver(JKBXHeaderVO parentVO) {
		String receiver = parentVO.getReceiver();
		if(receiver == null){
			return;
		}
		
		if(!isReceiverPaytarget(parentVO.getDjdl(),parentVO.getPaytarget(),parentVO.getIscusupplier())){//是否是对私支付
			return;
		}
		
		initSkyhzh(parentVO);//初始化银行
		
		// 自动带出收款银行帐号
		try {
			String key = UserBankAccVoCall.USERBANKACC_VOCALL + receiver;
			// 个人银行账户带默认账户
			BankAccbasVO bank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class).queryDefaultBankAccByPsnDoc(receiver);
			if (bank != null && bank.getBankaccsub() != null) {
//				WorkbenchEnvironment.getInstance().putClientCache(UserBankAccVoCall.USERBANKACC_VOCALL + receiver, bank.getBankaccsub());
//				parentVO.setSkyhzh(bank.getBankaccsub()[0].getPk_bankaccsub());
//				editor.getHelper().changeBusItemValue(BXBusItemVO.SKYHZH, bank.getBankaccsub()[0].getPk_bankaccsub());
			}
			
		} catch (Exception e) {
			parentVO.setSkyhzh(null);
		}
	}
	
	/**
	 * 是否对个人进行支付
	 * @param djdl 
	 * 
	 * @return
	 */
	public boolean isReceiverPaytarget(String djdl,Integer paytarget,UFBoolean iscusupplier) {
		if (BXConstans.BX_DJDL.equals(djdl)) {
			if (paytarget == null || paytarget.intValue() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return iscusupplier.booleanValue();
		}
	}
	
	/**
	 * 收款银行帐号根据借款报销人和币种编码过滤
	 * @param parentVO 
	 * 
	 * @param key
	 */
	public void initSkyhzh(JKBXHeaderVO parentVO) {
		// 收款人
		String receiver = parentVO.getReceiver();
		if (!isReceiverPaytarget(parentVO.getDjdl(),parentVO.getPaytarget(),parentVO.getIscusupplier())) {
			receiver = null;
		}

		String pk_currtype = parentVO.getBzbm();
		// 收款银行参照
//		UIRefPane refpane = getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH);
//		StringBuffer wherepart = new StringBuffer();
//		wherepart.append(" pk_psndoc='" + receiver + "'");
//		wherepart.append(" and pk_currtype='" + pk_currtype + "'");
//
//		// 创维项目测出，收款银行账户仅根据人员进行过滤即可
//		PsnbankaccDefaultRefModel psnbankModel = (PsnbankaccDefaultRefModel) refpane.getRefModel();
//		psnbankModel.setWherePart(wherepart.toString());
//		psnbankModel.setPk_psndoc(receiver);
	}
	
	// 过滤部门字段
	private void filtDeptField(JKBXHeaderVO parentVO) {
		String dwbm = parentVO.getDwbm();
		beforeEditDept_v(dwbm, JKBXHeaderVO.DEPTID_V);
		String fydwbm = parentVO.getFydwbm();
		beforeEditDept_v(fydwbm, JKBXHeaderVO.FYDEPTID_V);
	}
	
	/**
	 * 多版本部门过滤
	 * @param evt
	 * @param vDeptField
	 */
	public void beforeEditDept_v(final String pk_org, final String vDeptField){
//		UIRefPane refPane = getHeadItemUIRefPane(vDeptField);
//		DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) refPane.getRefModel();
//		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
//		if (date == null) {
//			// 单据日期为空，去业务日期
//			date = BXUiUtil.getBusiDate();
//		}
//		model.setVstartdate(date);
//		model.setPk_org(pk_org);
	}
		
	/**
	 * 设置最迟还款日
	 * 
	 * @param org
	 * @throws BusinessException
	 */
	protected void setZhrq(String org) throws BusinessException {
		if (org == null) {// || editor.isInit() 63期初也加入最迟还款日期
			return;
		}
//		// 设置最迟还款日
//		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.ZHRQ) != null) {
//			Object billDate = getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ)
//					.getValueObject();
//			int days = SysInit.getParaInt(org,
//					BXParamConstant.PARAM_ER_RETURN_DAYS);
//			if (billDate != null && billDate.toString().length() > 0) {
//				UFDate billUfDate = (UFDate) billDate;
//				UFDate zhrq = billUfDate.getDateAfter(days);
//				getBillCardPanel().setHeadItem(JKBXHeaderVO.ZHRQ, zhrq);
//			}
//		}
	}
	
	@Override
	public SuperVO setBodyDefaultValue(SuperVO parentVO, String tablecode,
			String classname) {
		try {
			JsonVoTransform votra = new JsonVoTransform();
			SuperVO bodyVO = votra.InitBodyVo(classname);
			bodyVO.setAttributeValue("tablecode", tablecode);
			// 将数据从表头联动到表体
			String[] keys = new String[]{JKBXHeaderVO.SZXMID,JKBXHeaderVO.JKBXR,JKBXHeaderVO.JOBID,
					JKBXHeaderVO.CASHPROJ,JKBXHeaderVO.PROJECTTASK,JKBXHeaderVO.PK_PCORG,JKBXHeaderVO.PK_PCORG_V,JKBXHeaderVO.PK_CHECKELE
					,JKBXHeaderVO.PK_RESACOSTCENTER,JKBXHeaderVO.PK_PROLINE,JKBXHeaderVO.PK_BRAND,JKBXHeaderVO.DWBM,JKBXHeaderVO.DEPTID
					,JKBXHeaderVO.JKBXR,JKBXHeaderVO.PAYTARGET,JKBXHeaderVO.RECEIVER,JKBXHeaderVO.SKYHZH,JKBXHeaderVO.HBBM,JKBXHeaderVO.CUSTOMER
					,JKBXHeaderVO.CUSTACCOUNT,JKBXHeaderVO.FREECUST};
			doCoresp(parentVO,bodyVO, Arrays.asList(keys), tablecode);
	
			String[] bodyKeys = new String[]{JKBXHeaderVO.YBJE,JKBXHeaderVO.CJKYBJE,JKBXHeaderVO.ZFYBJE,JKBXHeaderVO.HKYBJE,
					JKBXHeaderVO.BBJE,JKBXHeaderVO.CJKBBJE,JKBXHeaderVO.ZFBBJE,JKBXHeaderVO.HKBBJE};
			for (String key : bodyKeys) {
				if(bodyVO instanceof BXBusItemVO){
	    			((BXBusItemVO)bodyVO).setJsonAttributeValue(key, UFDouble.ZERO_DBL);
	    		}else{
	    			bodyVO.setAttributeValue(key, UFDouble.ZERO_DBL);
	    		}
			}
	
			// 带出报销标准
			doBodyReimAction(parentVO,bodyVO);
			return bodyVO; 
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("表体默认值转换异常：" + e.getMessage());
		}
		return null;
	}
	
	private void doCoresp(SuperVO parentVO,SuperVO bodyVO, List<String> keyList, String tablecode) {
		for (String key : keyList) {
			bodyVO.setAttributeValue(key, parentVO.getAttributeValue(key));
		}
	}
	
	/**
	 * 表体报销规则
	 */
	public void doBodyReimAction(SuperVO parentVO,SuperVO bodyVO) {
		//得到最终标准值,先走组织级标准，再走集团级标准
//		List<BodyEditVO> result = BxUIControlUtil.doBodyReimAction(bxvo,
//				getReimRuleDataMap(), billtempletbodyvos,getReimDimDataMap());
//		List<BodyEditVO> result1 = BxUIControlUtil.doBodyReimAction(bxvo,
//				reimRuleDataCacheMap.get(ReimRulerVO.PKORG),  billtempletbodyvos,reimDimDataCacheMap.get(ReimRulerVO.PKORG));
//		result.addAll(result1);
//		List<String> editstring = new ArrayList<String>();
//		controlRule.clear();
//		for (BodyEditVO vo : result) {
//			//ControlBodyEditVO表示控制项
//			if(vo instanceof ControlBodyEditVO){
//				if(!editstring.contains(vo.toString())){
//					editstring.add(vo.toString());
//					controlRule.add((ControlBodyEditVO)vo);
//				}
//			}
//			else{
//				getBillCardPanel().setBodyValueAt(vo.getValue(), vo.getRow(),
//						vo.getItemkey(), vo.getTablecode());
//			}
//		}
	}

	@Override
	public AggregatedValueObject setDefaultValue() throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
}
