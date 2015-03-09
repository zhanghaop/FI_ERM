package nc.erm.mobile.billaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.arap.util.BillOrgVUtils;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.fi.org.IOrgVersionQueryService;
import nc.itf.fi.pub.Currency;
import nc.itf.org.IOrgVersionQryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.scmpub.exp.AtpNotEnoughException;
import nc.vo.vorg.DeptVersionVO;
import nc.vo.vorg.OrgVersionVO;

import org.apache.commons.lang.ArrayUtils;

public class BillSaveAction {
	private String PK_ORG;
	private IErmMatterAppBillManage mgService;
	private IErmAccruedBillManage manageService;
	
	public BillSaveAction(String pk_org) {
		PK_ORG = pk_org;
	}

	/**
	 * 重构获取多版本部门id,供前台调用
	 * @param pk_dept
	 * @param billdate
	 * @return
	 * @throws BusinessException
	 */
	protected String getDept_vid(String pk_dept,UFDate billdate) throws BusinessException {
		Map<String, DeptVersionVO[]> versionMap=new HashMap<String, DeptVersionVO[]>();
		IOrgVersionQryService query = NCLocator.getInstance().lookup(IOrgVersionQryService.class);
		return getDept_vid(query, versionMap, pk_dept, billdate);
	}
	
	protected String getDept_vid(IOrgVersionQryService query,
			Map<String, DeptVersionVO[]> versionMap, String pk_org,
			UFDate billdate) throws BusinessException {
	
		if(billdate==null){
			billdate=new UFDate();
		}
	
		String pk_org_v = null;
	
		DeptVersionVO[] versions = versionMap.get(pk_org);
		if (ArrayUtils.isEmpty(versions)) {
			versions = query.queryDeptVersionVOSByOID(pk_org);
			versionMap.put(pk_org, versions == null ? new DeptVersionVO[0]: versions);
		}
		if(ArrayUtils.isEmpty(versions)){
			Log.getInstance(BillOrgVUtils.class).error(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0582")/*@res "~~~~~~~~~~@@@@~~~~~~~~~开始设置部门版本号"*/+pk_org, BillOrgVUtils.class, "setDept_v");
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006pub_0","02006pub-0583")/*@res "查询部门版本错误，该部门无可用版本!"*/);
		}
	
		for(DeptVersionVO vso:versions){
			if(vso.getEnablestate()!=2){ //是否可用
				continue;
			}
			if((vso.getVstartdate().compareTo(billdate)<=0 &&  vso.getVenddate().compareTo(billdate)>=0)||vso.getVstartdate().isSameDate(billdate)||vso.getVenddate().isSameDate(billdate)){
				pk_org_v=vso.getPk_vid();
			}
		}
	
		// 未匹配到可用版本，取历史较早版本
		int index = 0;
		if (pk_org_v == null) {
			for (int i = 1; i < versions.length; i++) {
				if(versions[index].getVstartdate()!=null
						&&versions[index].getVstartdate().after(versions[i].getVstartdate())){
					index = i;
				}
			}
			pk_org_v = versions[index].getPk_vid();
		}
	
		
		if(pk_org_v==null){
			pk_org_v=versions[0].getPk_vid();
		}
		return pk_org_v;
	}
	public String insertAcc(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
		AggAccruedBillVO aggValue = (AggAccruedBillVO) vo;
		aggValue.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
		try {
			saveAccBackValue(aggValue,false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	public IErmAccruedBillManage getManageService() {
		if (manageService == null) {
			manageService = NCLocator.getInstance().lookup(IErmAccruedBillManage.class);
		}
		return manageService;
	}
	private AggAccruedBillVO saveAccBackValue(Object object, boolean isEdit) throws Exception {
		AggAccruedBillVO returnObj = null;
		if (isEdit) {
			returnObj = getManageService().updateVO((AggAccruedBillVO) object);
		} else {
			returnObj = getManageService().insertVO((AggAccruedBillVO) object);
		}
	
		// 显示预算，借款控制的提示信息
		if (!StringUtils.isNullWithTrim(returnObj.getParentVO().getWarningmsg())) {
//			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
//																													 * @
//																													 * res
//																													 * "警告"
//																													 */, returnObj.getParentVO().getWarningmsg());
//			returnObj.getParentVO().setWarningmsg(null);
		}
	
		return returnObj;
	}
	public String insertMa(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
//		dealBodyRowNum();
		AggMatterAppVO aggMatterVo = (AggMatterAppVO) vo;
		try {
			UFDouble newYbje = null;
			MtAppDetailVO[] items = aggMatterVo.getChildrenVO();

			int length = items.length;
			for (int i = 0; i < length; i++) {
				if (items[i].getOrig_amount() != null) {// 当表体中存在空行时，原币金额为空，所以在这里判空
					if (newYbje == null) {
						newYbje = items[i].getOrig_amount();
					} else {
						newYbje = newYbje.add(items[i].getOrig_amount());
					}
				}
			}
			// 金额校验
			UFDouble oriAmount = aggMatterVo.getParentVO().getOrig_amount();
			if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
				throw new ValidationException("请录入正确的金额");
			}

//			validate(aggMatterVo);
			// 执行单据模板验证公式
//			boolean execValidateFormulas = ((BillForm) getEditor()).getBillCardPanel().getBillData()
//					.execValidateFormulas();
//			if (!execValidateFormulas) {
//				return null;
//			}
			aggMatterVo.getParentVO().setBillstatus(ErmMatterAppConst.BILLSTATUS_SAVED);
			saveMaBackValue(aggMatterVo,false);

		} catch (BugetAlarmBusinessException ex) {
//			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
//					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
//																		 * @ res
//																		 * "提示"
//																		 */, ex.getMessage()
//					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
//																									 * @
//																									 * res
//																									 * " 是否继续审核？"
//																									 */) == MessageDialog.ID_YES) {
//				aggMatterVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // 不检查
//				saveBackValue(aggMatterVo);
//			} else {
//				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
//						"UPP2011-000405")
//				/* @res "预算申请失败" */, ex);
//			}
		} catch (AtpNotEnoughException ex) {
//			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
//					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
//																		 * @ res
//																		 * "提示"
//																		 */, ex.getMessage()) == MessageDialog.ID_YES) {
//
//				aggMatterVo.getParentVO().setIsignoreatpcheck(UFBoolean.TRUE);
//				saveBackValue(aggMatterVo);
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private IErmMatterAppBillManage getMgService(){
		if(mgService == null){
			mgService = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class);
		}
		return mgService;
	}
	private AggMatterAppVO saveMaBackValue(Object object, boolean isEdit) throws Exception {
		AggMatterAppVO returnObj = null;
		if (isEdit) {
			returnObj = getMgService().updateVO((AggMatterAppVO) object);
		} else {
			returnObj = getMgService().insertVO((AggMatterAppVO) object);
		}

		// 显示预算，借款控制的提示信息
		if (!StringUtils.isNullWithTrim(returnObj.getParentVO().getWarningmsg())) {
//			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
//																													 * @
//																													 * res
//																													 * "警告"
//																													 */, returnObj.getParentVO().getWarningmsg());
//			returnObj.getParentVO().setWarningmsg(null);
		}

		return returnObj;
	}
	public void fillAmount(JKBXVO jkbxvo,String djlxbm,String userid) throws BusinessException{
		UFTime time= new UFTime();
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		parentVO.setDjrq(new UFDate(parentVO.getDjrq().getMillis() + time.getMillis()));
		//根据数据类型将需要转换的数据进行转换
		parentVO.setPk_jkbx(null);
		if(StringUtil.isEmptyWithTrim(parentVO.getBzbm())){
			throw new BusinessException("币种不能为空！");
		}
		if(parentVO.getPaytarget() == null){
			parentVO.setPaytarget(0);
		}
		// 补充表头组织字段版本信息
		IOrgVersionQueryService orgvservice = NCLocator.getInstance().lookup(IOrgVersionQueryService.class);
		Map<String, OrgVersionVO> orgvmap = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, parentVO.getDjrq());
		OrgVersionVO orgVersionVO = orgvmap.get(parentVO.getPk_org());
		if(orgVersionVO==null){
			Map<String, OrgVersionVO> orgvmap2 = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, new UFDate("2990-01-01"));
			orgVersionVO= orgvmap2.get(parentVO.getPk_org());
		}
		String orgVid = orgVersionVO.getPk_vid();
		parentVO.setDwbm_v(orgVid);
		parentVO.setFydwbm_v(orgVid);
		parentVO.setPk_org_v(orgVid);
		parentVO.setPk_payorg_v(orgVid);
		
		String deptVid = getDept_vid(parentVO.getDeptid(),  parentVO.getDjrq());
		parentVO.setDeptid_v(deptVid);
		parentVO.setFydeptid_v(deptVid);
		
		// 设置表体数据
		//把表头收支项目同步到表体
		String szxmid = parentVO.getSzxmid();
		parentVO.setPk_item(null);
		BXBusItemVO[] items = jkbxvo.getChildrenVO();
		if(items != null && items.length>0){
			UFDouble totalAmount = UFDouble.ZERO_DBL;   
			for(int i = 0;i < items.length;i++){
				BXBusItemVO itemvo = items[i];
				if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid())){
					//若表头设置了收支项目，表体没有设，则表体同步为表头的收支项目
					itemvo.setSzxmid(szxmid);
				}
				UFDouble amount = itemvo.getAmount();
				itemvo.setYbje(amount);
				itemvo.setBbje(amount);
				totalAmount = totalAmount.add(amount);
			}
			parentVO.setYbje(totalAmount);
			parentVO.setTotal(totalAmount);
			parentVO.setBbje(totalAmount);
			
		}
		resetAmount(parentVO);
	}
	public String insertJkbx(AggregatedValueObject vo,String djlxbm,String userid)
			throws BusinessException {
		if(!(vo instanceof JKBXVO))
			return null;
		JKBXVO jkbxvo = (JKBXVO) vo;
		//fillAmount(jkbxvo,djlxbm,userid);
		// 保存报销单数据
		IBXBillPublic service = NCLocator.getInstance().lookup(IBXBillPublic.class);
		JKBXVO[] result = service.save(new JKBXVO[] { jkbxvo });
		
		String bxpk = result[0].getParentVO().getPrimaryKey();
		return bxpk; 
	}

	public String updateJkbx(AggregatedValueObject vo,String userid) throws BusinessException {
		if(!(vo instanceof JKBXVO))
			return null;
		JKBXVO jkbxvo = (JKBXVO) vo;
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		parentVO.setStatus(VOStatus.UPDATED);
		String billId = parentVO.getPrimaryKey();
		// 查询旧借款报销单
		List<JKBXVO> vos = NCLocator.getInstance().
		  lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{billId}, null);
		if(vos == null || vos.isEmpty()){
			throw new BusinessException("单据已被删除，请检查");
		}
		JKBXVO oldvo = vos.get(0);
		
		// 审批状态控制
		Integer spzt = parentVO.getSpzt();

		if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON) || spzt.equals(IPfRetCheckInfo.COMMIT))) {
			String userId = InvocationInfoProxy.getInstance().getUserId();
			String billType = parentVO.getDjlxbm();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// 启动了审批流后
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(jkbxvo.getParentVO().getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "请取消审批再修改！"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
				}
			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		parentVO.setBzbm("1002Z0100000000001K1");// 默认人民币
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setPaytarget(0);
		//重新合计表头
		List<BXBusItemVO> itemlist = new ArrayList<BXBusItemVO>();
		
		//删除旧的业务行
		BXBusItemVO[] olditems = oldvo.getBxBusItemVOS();
		if(olditems != null && olditems.length > 0){
			for (int i = 0; i < olditems.length; i++) {
				olditems[i].setStatus(VOStatus.DELETED);
				itemlist.add(olditems[i]);
			}
		}
		UFDouble totalAmount = UFDouble.ZERO_DBL;
		BXBusItemVO[] items = jkbxvo.getBxBusItemVOS();
		if(items != null && items.length>0){
			//把表头收支项目同步到表体
			String szxmid = parentVO.getSzxmid();
			for (BXBusItemVO itemvo : items) {
				String amountvalue = (String) itemvo.getAttributeValue("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				itemvo.setStatus(VOStatus.NEW);
				//收支项目字段同步到表体行
				if(!StringUtil.isEmpty(szxmid) && StringUtil.isEmpty(itemvo.getSzxmid())){
					itemvo.setSzxmid(szxmid);
				}
				itemvo.setYbje(itemvo.getAmount()); 
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
				itemvo.setPrimaryKey(null);
				itemlist.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
		}
		jkbxvo.setBxBusItemVOS(itemlist.toArray(new BXBusItemVO[itemlist.size()]));
		parentVO.setYbje(totalAmount);
		parentVO.setTotal(totalAmount);
		parentVO.setBbje(totalAmount);
		
		// 更新单据
		NCLocator.getInstance().lookup(IBXBillPublic.class).update(new JKBXVO[]{jkbxvo});
		return billId;
	}
	
	private void resetAmount(JKBXHeaderVO parentVO){
		
		//集团
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		// 原币币种pk
		String pk_currtype = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		UFDouble ybje = parentVO.getYbje();

		// 汇率(本币，集团本币，全局本币汇率)
		UFDouble orgRate = new UFDouble(1);
		
		UFDouble groupRate = new UFDouble(1);
		UFDouble globalRate = new UFDouble(1);
		try {
			orgRate = Currency.getRate(PK_ORG, pk_currtype, djrq);
			groupRate = Currency.getGroupRate(PK_ORG, pk_group, pk_currtype, djrq);
			globalRate = Currency.getGlobalRate(PK_ORG, pk_currtype, djrq);
		} catch (BusinessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		parentVO.setBbhl(orgRate);
		parentVO.setGroupbbhl(groupRate);
		parentVO.setGlobalbbhl(globalRate);
		
		try {
			// 组织本币金额
			UFDouble[] bbje = Currency.computeYFB(PK_ORG,
					Currency.Change_YBCurr, pk_currtype, ybje, null, null, null, orgRate,new UFDate());
			parentVO.setYbye(ybje);
			parentVO.setBbje(bbje[2]);
			parentVO.setBbye(bbje[2]);

			// 集团、全局金额
			UFDouble[] money = Currency.computeGroupGlobalAmount(bbje[0], bbje[2],
					pk_currtype, new UFDate(), PK_ORG, pk_group,groupRate, globalRate);
			parentVO.setGroupbbje(money[0]);
			parentVO.setGroupbbye(money[0]);
			parentVO.setGlobalbbje(money[1]);
			parentVO.setGlobalbbye(money[1]);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
}
