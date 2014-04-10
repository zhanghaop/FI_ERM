package nc.vo.er.check;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.trade.pub.IBillStatus;

public class VOStatusChecker {

	public static void checkAuditStatus(JKBXHeaderVO head, UFDateTime shrq) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.AUDIT,
				new int[] { BXStatusConst.DJZT_Saved });

		if (head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.NOPASS)) {
			if (msgs == null) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000915")/*
																									 * @
																									 * res
																									 * "审批未通过的单据不可以审批！"
																									 */;
			}
			msgs += ", " + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000915")/*
																										 * @
																										 * res
																										 * "审批未通过的单据不可以审批！"
																										 */;
		}
		
		if (head.getSpzt() == IBillStatus.FREE) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0",
					"单据未提交，不能进行审批!", "0201212-0104"));
		}

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

		if (head.getShrq() == null){
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0147")/* @res "单据审核日期不能为空!" */);
		}

		if (head.getApprover() == null){
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
					"02011002-0148")/* @res "单据审核人不能为空!" */);
		}
	}

	public static void checkUnAuditStatus(JKBXHeaderVO head) throws DataValidateException {

		boolean auto = SettleUtil.isAutoSign(head);

		// 没有结算信息的单据，等同于自动签字生效
		if (head.getZfybje().compareTo(new UFDouble(0)) == 0 && head.getHkybje().compareTo(new UFDouble(0)) == 0) {
			auto = true;
		}

		String msg = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.UNAUDIT, auto ? new int[] {
				BXStatusConst.DJZT_Sign, BXStatusConst.DJZT_Verified, BXStatusConst.DJZT_Saved } : new int[] {
				BXStatusConst.DJZT_Verified, BXStatusConst.DJZT_Saved });

		if (!StringUtils.isNullWithTrim(msg)) {
			throw new DataValidateException(msg);
		}

	}

	public static void checkSettleStatus(JKBXHeaderVO head, UFDate jsrq) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.SETTLE,
				new int[] { BXStatusConst.DJZT_Verified });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
		// begin--added by chendya@ufida.com.cn
		// if (head.getShrq().getDate().after(jsrq)) {
		// if (head.getShrq().getDate().afterDate(jsrq)) {
		// //--end
		// throw new
		// DataValidateException("签字日期"+head.getShrq().toString()+"不能早于单据审核日期"+jsrq.toString()/*@res
		// "签字日期不能早于单据审核日期"*/);
		// // throw new
		// DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000337")/*@res
		// "签字日期不能早于单据审核日期"*/);
		// }

	}

	public static void checkUnSettleStatus(JKBXHeaderVO head) throws DataValidateException {

		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.UNSETTLE,
				new int[] { BXStatusConst.DJZT_Sign });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

	}

	/**
	 * 单据收回状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkRecallStatus(JKBXHeaderVO head) throws DataValidateException {
		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.RECALL,
				new int[] { ErmMatterAppConst.BILLSTATUS_SAVED });

		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getSpzt();// 审核状态
			if (apprStatus.equals(Integer.valueOf(IBillStatus.CHECKGOING))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0055")/*
																										 * @
																										 * res
																										 * "单据正在审核中，不能收回单据！"
																										 */;
			}
		}

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * 单据提交状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkCommitStatus(JKBXHeaderVO head) throws DataValidateException {
		String msgs = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.COMMIT,
				new int[] { ErmMatterAppConst.BILLSTATUS_SAVED });

		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getSpzt();// 审核状态
			if (!apprStatus.equals(Integer.valueOf(IBillStatus.FREE))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", "非自由态单据,不可提交！", "0201212-0102");
			}
		}

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	public static void checkDeleteStatus(JKBXHeaderVO head) throws DataValidateException {

		String msg = null;

		if (head.getQcbz().booleanValue()) { // 期初单据
			msg = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.DELETE, new int[] { BXStatusConst.DJZT_Saved,
					BXStatusConst.DJZT_TempSaved, BXStatusConst.DJZT_Sign });
		} else {
			msg = ActionUtils.checkBillStatus(head.getDjzt(), ActionUtils.DELETE, new int[] { BXStatusConst.DJZT_Saved,
					BXStatusConst.DJZT_TempSaved });
			if (head.getSpzt() != null
					&& (head.getSpzt().equals(IPfRetCheckInfo.GOINGON) || head.getSpzt().equals(IPfRetCheckInfo.COMMIT))) {
				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000398")/*
																									 * @
																									 * res
																									 * "单据正在审核中，不能删除！"
																									 */;
			}
			if (head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.NOPASS)) {
				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000914")/*
																									 * @
																									 * res
																									 * "审批未通过的单据不可以删除！"
																									 */;
			}
//			//ehp2： 对于暂估的单据是不可以删除的
//			if(head.getVouchertag()!= null && head.getVouchertag()==BXStatusConst.ZGDeal){
//				msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000953")/*
//				 * @
//				 * res
//				 * "存在暂估凭证的单据，不可以删除！"
//				 */;
//
//			}
		}
		if (!StringUtils.isNullWithTrim(msg)) {
			throw new DataValidateException(msg);
		}

	}
}