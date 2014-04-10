package nc.vo.er.check;

import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class VOStatusChecker {

	public static void checkAuditStatus(JKBXHeaderVO head, UFDateTime shrq) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.AUDIT, new int[] {BXStatusConst.DJZT_Saved});

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

		if(head.getShrq()==null)
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0147")/*@res "单据审核日期不能为空!"*/);

		if(head.getApprover()==null)
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0148")/*@res "单据审核人不能为空!"*/);
		//审核日期改动
//		if (head.getDjrq().after(shrq.getDate())) {
//		if (head.getDjrq().afterDate(shrq.getDate())) {
//			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000336")/*@res "审核日期不能早于单据录入日期"*/);
//		}


	}

	public static void checkUnAuditStatus(JKBXHeaderVO head) throws DataValidateException {

		boolean auto=SettleUtil.isAutoSign(head);

		//没有结算信息的单据，等同于自动签字生效
		if(head.getZfybje().compareTo(new UFDouble(0))==0 && head.getHkybje().compareTo(new UFDouble(0))==0){
			auto=true;
		}

		String msg = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.UNAUDIT, auto?new int[] { BXStatusConst.DJZT_Sign,BXStatusConst.DJZT_Verified,BXStatusConst.DJZT_Saved}:new int[] { BXStatusConst.DJZT_Verified,BXStatusConst.DJZT_Saved});

		if(!StringUtils.isNullWithTrim(msg)){
			throw new DataValidateException(msg);
		}

	}

	public static void checkSettleStatus(JKBXHeaderVO head, UFDate jsrq) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.SETTLE, new int[] { BXStatusConst.DJZT_Verified });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
//begin--added by chendya@ufida.com.cn
//		if (head.getShrq().getDate().after(jsrq)) {
//		if (head.getShrq().getDate().afterDate(jsrq)) {
////--end
//			throw new DataValidateException("签字日期"+head.getShrq().toString()+"不能早于单据审核日期"+jsrq.toString()/*@res "签字日期不能早于单据审核日期"*/);
////			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000337")/*@res "签字日期不能早于单据审核日期"*/);
//		}

	}

	public static void checkUnSettleStatus(JKBXHeaderVO head) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.UNSETTLE, new int[] { BXStatusConst.DJZT_Sign });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

	}

	public static void checkDeleteStatus(JKBXHeaderVO head) throws DataValidateException {

		String msg = null;

		if(head.getQcbz().booleanValue()){ //期初单据
			msg = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.DELETE, new int[]{
				BXStatusConst.DJZT_Saved,
				BXStatusConst.DJZT_TempSaved,
				BXStatusConst.DJZT_Sign
			});
		}else{
			msg = ActionUtils.checkBillStatus(head.getDjzt(), MessageVO.DELETE, new int[]{
			BXStatusConst.DJZT_Saved,
			BXStatusConst.DJZT_TempSaved
			});
			if(head.getSpzt()!=null && head.getSpzt().equals(IPfRetCheckInfo.GOINGON)){
				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000398")/*@res "单据正在审核中，不能删除！"*/;
			}
		}

		if (!StringUtils.isNullWithTrim(msg)) {
			throw new DataValidateException(msg);
		}

	}
}