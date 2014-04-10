package nc.vo.er.settle;

import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.BusiBillStatus;
import nc.itf.cm.prv.CmpConst;
import nc.itf.er.pub.IArapBillTypePublic;
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

	public static boolean isAutoSign(DjLXVO typeVOs){
		boolean isAuto=true;
		//�ж�CMP��Ʒ�Ƿ�����
		boolean iscmpused = BXUtil.isProductInstalled(typeVOs.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		
		if(!iscmpused)
			return true;
		if(typeVOs!=null){
			isAuto=!(typeVOs.getIsqr().booleanValue());
		}
		return isAuto;
	}
	
	public static boolean isAutoSign(JKBXHeaderVO head){
		
		boolean isAuto=true;
		//�ж�CMP��Ʒ�Ƿ�����
		boolean iscmpused = BXUtil.isProductInstalled(head.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		
		if(!iscmpused)
			return true;
		try{
			IArapBillTypePublic billtype = NCLocator.getInstance().lookup(IArapBillTypePublic.class);
			DjLXVO typeVOs = billtype.getDjlxvoByDjlxbm(head.getDjlxbm(), head.getPk_group());
			if(typeVOs!=null){
				isAuto=!(typeVOs.getIsqr().booleanValue());
			}
		}catch (Exception e) {
			throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000403")/*@res "��ѯ����������Ϣ���������·��䵥������."*/);
		}
		return isAuto;
	}

	public static BusiBillStatus getBusiBillStatusByNode(String node, int nodeopentype){
		if(nodeopentype==2)//BxParam.NodeOpenType_Link
			return BusiBillStatus.QUERY;
		if(BXConstans.BXMNG_NODECODE.equals(node)){
			return BusiBillStatus.MANAGER;
		//FIXME ��ѯ�ڵ�ȥ��	
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
//begin--modified by chendya ���������㡰����ϵͳ�ֶΡ�			
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
				msg.setIsauthpass(UFBoolean.TRUE);//������Ժ����ִ��
			}
			msg.setIdmap(zbvo.getCmpIdMap());
			msg.setApplyCombine(UFBoolean.TRUE.equals(djlx.getIsautocombinse())?CmpConst.ONE_TO_MANY:CmpConst.ONE_TO_ONE);
			msg.setCopyMap(null);
			msg.setPk_ftsbill(null);
			msg.setFts_billtype(null);
			//�Ƿ������ ���ڱ���
			msg.setHasLoanCheck(zbvo.getHasJkCheck());
			//�Ƿ��ʽ�ƻ����� �����ʽ�ƻ�
			msg.setHasFundPlan(zbvo.getHasZjjhCheck());
			//�Ƿ�Ԥ�����
			msg.setHasNtbCheck(zbvo.getHasNtbCheck());
		}
		return msg;
	}

	public static BusiStatus getBillStatus(JKBXHeaderVO head,boolean isAudit,BusiStatus defaultStatus){
		BusiStatus bs=null;

//		Deleted(0), /**ɾ��״̬*/
//		Tempeorary(100), /**�ݴ�״̬*/
//		Save(200), /**����״̬*/
//		AuditHandling(312), /**������*/
//		AuditHandlFail(310), /**����ʧ��*/
//		Audit(311), /**������ˡ�*/
//		Sign(400), /** ǩ��״̬ */
//		EffectNever(500), /**δ��Ч*/
//		Effet(510), /**����Ч״̬��*/
//		EffectHandling(505),/**�����У��ո�ί�и�������У���Ч��־Ϊί�и�����*/
//		HangUp(600),
//		UnComfirm(700);

		if(head.isAdjustBxd()){
			// ��������Ϊ���õ����ĵ��ݣ�������֧������
			return BusiStatus.Deleted;
		}
		if(StringUtils.isNullWithTrim(head.getPrimaryKey()))
			return BusiStatus.Save;

		if((head.getZfybje()==null || head.getZfybje().equals(new UFDouble(0)))&& (head.getHkybje()==null || head.getHkybje().equals(new UFDouble(0)))){
			//���ݼȲ����Ҳ���տ�
			return BusiStatus.Deleted;
		}
		
		if(defaultStatus!=null)
			return defaultStatus;

		if(head.isInit()){
			//���õ���
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
		 if(head.isInit())
			 return false;

		 if(head.getQcbz().booleanValue())
			 return false;

		 if(head==null || head.getDjdl()==null)
			 return true;

		 if(head.getDjdl().equals(BXConstans.JK_DJDL) && head.getIscheck().booleanValue()){
		    	return false;
		 }

         return true;
	}

	public static BusiStatus getBillStatus(JKBXHeaderVO parentVO, boolean isaudit) {
		return getBillStatus(parentVO,isaudit,null);
	}
}