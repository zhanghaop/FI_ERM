package nc.vo.er.settle;

import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.BusiBillStatus;
import nc.itf.cm.prv.CmpConst;
import nc.itf.er.pub.IArapBillTypePublic;
import nc.itf.uap.busibean.SysinitAccessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.cmp.BusiStatus;
import nc.vo.cmp.settlement.CmpMsg;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class SettleUtil {

	public static boolean isAutoSign(DjLXVO typeVO){
		if(typeVO == null){
			return false;
		}
		boolean isAuto=true;
		//判断CMP产品是否启用
		boolean iscmpused = BXUtil.isProductInstalled(typeVO.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		
		if(!iscmpused)
			return true;
		if(typeVO!=null){
			isAuto=!(typeVO.getIsqr().booleanValue());
		}
		return isAuto;
	}
	
	/**
	 * 是否自动签字
	 * 
	 * @param head
	 * @return
	 */
	public static boolean isAutoSign(JKBXHeaderVO head) {
		if (head.getRed_status() != null && head.getRed_status() == BXStatusConst.RED_STATUS_RED) {
			return true;// 红冲单据自动签字
		}
		
		boolean isAuto = true;
		// 判断CMP产品是否启用
		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);

		if (!iscmpused)
			return true;
		try {
			IArapBillTypePublic billtype = NCLocator.getInstance().lookup(IArapBillTypePublic.class);
			DjLXVO typeVOs = billtype.getDjlxvoByDjlxbm(head.getDjlxbm(), head.getPk_group());
			if (typeVOs != null) {
				isAuto = !(typeVOs.getIsqr().booleanValue());
			}
		} catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000403")/*
																															 * @
																															 * res
																															 * "查询单据类型信息出错，请重新分配单据类型."
																															 */);
		}
		return isAuto;
	}
	
	/**
	 * 是否自动结算
	 * <br>根据交易类型参数判断
	 * @param head
	 * @return
	 */
	public static boolean isAutoJS(JKBXHeaderVO head) {
		boolean isAuto = false;
		try {
			if(head.getRed_status() != null && head.getRed_status() == BXStatusConst.RED_STATUS_RED){
				return true;//红冲单据自动结算
			}
			
			DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, "pk_group = '" + head.getPk_group() + "' and djlxbm = '" + head.getDjlxbm() + "'");
			if (vos != null && vos.length != 0) {
				isAuto = vos[0].getAutosettle() == null ? false : vos[0].getAutosettle().booleanValue();
			}
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000403")/*
																															 * @
																															 * res
																															 * "查询单据类型信息出错，请重新分配单据类型."
																															 */);
		}
		return isAuto;
	}
	
	/**
	 * 是否结算环节传会计平台
	 * @param head
	 * @return
	 * @throws BusinessException
	 */
	public static boolean isJsToFip(JKBXHeaderVO head) throws BusinessException{
		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		if (!iscmpused) {
			return false;
		}
		
		String param = SysinitAccessor.getInstance().getParaString(head.getPk_org(), "CMP37");
		
		if(param != null && param.equals(BXStatusConst.VounterCondition_ZF)){
			return true;
		}
		
		return false;
	}

	public static BusiBillStatus getBusiBilheadlStatusByNode(String node, int nodeopentype){
		if(nodeopentype==2)//BxParam.NodeOpenType_Link
			return BusiBillStatus.QUERY;
		if(BXConstans.BXMNG_NODECODE.equals(node)){
			return BusiBillStatus.MANAGER;
		//FIXME 查询节点去掉	
//		}else if(BXConstans.BXQUERY_NODECODE.equals(node)){
//			return BusiBillStatus.QUERY;
		}else if(nodeopentype==3){ //BxParam.NodeOpenType_LR_PUB_Approve
			return BusiBillStatus.MANAGER;
		}else{
			return BusiBillStatus.RECORD;
		}
	}

	public static CmpMsg getCmpMsg(JKBXVO zbvo,DjLXVO djlx,UFDate operatedate,BusiStatus busiStatus) throws BusinessException{
		CmpMsg msg=new CmpMsg();

		if(null!=zbvo){
			JKBXHeaderVO head=(JKBXHeaderVO)zbvo.getParentVO();
			msg.setLastOperator(zbvo.getParentVO().getOperationUser());
			msg.setLastOperatorDate(zbvo.getParentVO().getOperationDate());
			msg.setAutoSign(isAutoSign(djlx));
			if(head.getRed_status() != null && head.getRed_status() == BXStatusConst.RED_STATUS_RED){
				msg.setAutoSign(true);//红冲单据自动结算
			}
			
			msg.setBillcode(head.getDjbh());
			msg.setBillDate(head.getDjrq());
			msg.setBillkey(head.getPk_jkbx());
			
			if(!busiStatus.equals(BusiStatus.Save) && !busiStatus.equals(BusiStatus.Deleted)){
				if(head.auditman != null){
					msg.setLastauditer(head.auditman);
				}else{
					msg.setLastauditer(head.getAuditman());
				}
			}
			msg.setLastOperator(head.getOperator());
			msg.setLastOperatorDate(head.getOperationDate());
			msg.setLastauditedate(head.getShrq()!=null?head.getShrq().getDate():null);
			
			msg.setDirection(head.getZfybje()==null || head.getZfybje().equals(new UFDouble(0)) ? 0 : 1);
			msg.setFtsExec(false);
			msg.setIspay(false);
			msg.setBillOperator(head.getOperator());
			msg.setBilltype(head.getDjlxbm());
			msg.setDirty(head.getDr()==null || head.getDr()==0?false:true);
			msg.setPk_org(head.getPk_payorg());
			msg.setPk_org_v(head.getPk_payorg_v());
			msg.setPk_group(head.getPk_group());
//begin--modified by chendya 报销传结算“归属系统字段”			
			msg.setSystem(BXConstans.SYSTEMCODE_ER_TO_SETTLE);
//--end
			if(busiStatus!=BusiStatus.Save && head.getZfybje().compareTo(UFDouble.ZERO_DBL)==0 && head.getHkybje().compareTo(UFDouble.ZERO_DBL)==0){
				busiStatus=BusiStatus.Deleted;
			}
			
			msg.setBusistatus(busiStatus);
			
			msg.setBusiFlow(head.getBusitype());

			if(head.getZfybje()!=null && head.getHkybje()!=null){
				msg.setPrimal(head.getZfybje().add(head.getHkybje()));
				msg.setLocal(head.getZfbbje().add(head.getHkbbje()));
			}else{
				msg.setPrimal(head.getYbje());
				msg.setLocal(head.getBbje());
			}

			msg.setEbankRed(false);

			//msg.setAuthList(zbvo.authList);
			if(zbvo.authList != null && zbvo.authList.size() > 0 ){
				msg.setIsauthpass(UFBoolean.TRUE);//点击是以后继续执行
			}
			msg.setIdmap(zbvo.getCmpIdMap());
			msg.setApplyCombine(UFBoolean.TRUE.equals(djlx.getIsautocombinse())?CmpConst.ONE_TO_MANY:CmpConst.ONE_TO_ONE);
			msg.setCopyMap(null);
			msg.setPk_ftsbill(null);
			msg.setFts_billtype(null);
			//是否借款控制 用于报销
			msg.setHasLoanCheck(zbvo.getHasJkCheck());
			//是否资金计划控制 用于资金计划
			msg.setHasFundPlan(zbvo.getHasZjjhCheck());
			//是否预算控制
			msg.setHasNtbCheck(zbvo.getHasNtbCheck());
		}
		return msg;
	}

	public static BusiStatus getBillStatus(JKBXHeaderVO head,boolean isAudit,BusiStatus defaultStatus){
		BusiStatus bs=null;

//		Deleted(0), /**删除状态*/
//		Tempeorary(100), /**暂存状态*/
//		Save(200), /**保存状态*/
//		AuditHandling(312), /**审批中*/
//		AuditHandlFail(310), /**审批失败*/
//		Audit(311), /**　已审核　*/
//		Sign(400), /** 签字状态 */
//		EffectNever(500), /**未生效*/
//		Effet(510), /**　生效状态　*/
//		EffectHandling(505),/**处理中，收付委托付款进行中，生效标志为委托付款中*/
//		HangUp(600),
//		UnComfirm(700);

		if(head.isAdjustBxd()){
			// 报销类型为费用调整的单据，不进行支付结算
			return BusiStatus.Deleted;
		}
		if(StringUtils.isNullWithTrim(head.getPrimaryKey()))
			return BusiStatus.Save;

		if((head.getZfybje()==null || head.getZfybje().equals(new UFDouble(0)))&& (head.getHkybje()==null || head.getHkybje().equals(new UFDouble(0)))){
			//单据既不付款，也不收款
			return BusiStatus.Deleted;
		}
		
		if(defaultStatus!=null)
			return defaultStatus;

		if(head.isInit()){
			//常用单据
			return BusiStatus.Deleted;
		}

		if (!isAudit) {
			if (head.getDr()!=null && head.getDr() == 1)
				bs = BusiStatus.Deleted;
			else if (head.getDjzt() == BXStatusConst.DJZT_TempSaved)
				bs = BusiStatus.Tempeorary;
			else if (head.getDjzt() == BXStatusConst.DJZT_Saved)
				bs = BusiStatus.Save;
			else if (head.getDjzt() == BXStatusConst.DJZT_Verified)
				bs = BusiStatus.Audit;
			else if (head.getDjzt() == BXStatusConst.DJZT_Sign)
				bs = BusiStatus.Effet;
		}else{
//			if (BXStatusConst.SPZT_Init.equals(head.getSpzt())) {
//				bs = BusiStatus.Save;
//			}else if (BXStatusConst.SPZT_Pass.equals(head.getSpzt())) {
//				bs = BusiStatus.Audit;
//			} else if (BXStatusConst.SPZT_Verifying.equals(head.getSpzt())) {
//				bs = BusiStatus.AuditHandling;
//			} else if (BXStatusConst.SPZT_NoPass.equals(head.getSpzt())) {
//				bs = BusiStatus.AuditHandlFail;
//			}
//			if (BXStatusConst.SPZT_Init==head.getSpzt()) {
//				bs = BusiStatus.Save;
//			}else if (BXStatusConst.SPZT_Pass==head.getSpzt()) {
//				bs = BusiStatus.Audit;
//			} else if (BXStatusConst.SPZT_Verifying==head.getSpzt()) {
//				bs = BusiStatus.AuditHandling;
//			} else if (BXStatusConst.SPZT_NoPass==head.getSpzt()) {
//				bs = BusiStatus.AuditHandlFail;
//			}
			if (IPfRetCheckInfo.NOSTATE==head.getSpzt()) {
				bs = BusiStatus.Save;
			}else if (IPfRetCheckInfo.PASSING==head.getSpzt()) {
				bs = BusiStatus.Audit;
			} else if (IPfRetCheckInfo.GOINGON==head.getSpzt()) {
				bs = BusiStatus.AuditHandling;
			} else if (IPfRetCheckInfo.NOPASS==head.getSpzt()) {
				bs = BusiStatus.AuditHandlFail;
			}
			
		}
		return bs;
	}


	public static boolean hasSettleInfo(JKBXHeaderVO head, String currentDjdl) {
		if (head == null) {
			return false;
		}

		if (head.isInit())
			return false;

		if (head.getQcbz().booleanValue())
			return false;

		if (head == null || head.getDjdl() == null)
			return true;

		if (head.getDjdl().equals(BXConstans.JK_DJDL) && head.getIscheck().booleanValue()) {
			return false;
		}

		return true;
	}

	public static BusiStatus getBillStatus(JKBXHeaderVO parentVO, boolean isaudit) {
		return getBillStatus(parentVO,isaudit,null);
	}
}