package nc.bs.erm.accruedexpense.check;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.trade.pub.IBillStatus;

public class AccruedBillVOStatusChecker {

	/**
	 * 单据删除状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkDeleteStatus(AccruedVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.DELETE, new int[] {
				ErmAccruedBillConst.BILLSTATUS_SAVED, ErmAccruedBillConst.BILLSTATUS_TEMPSAVED });

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
	public static void checkCommitStatus(AccruedVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.COMMIT,
				new int[] { ErmAccruedBillConst.BILLSTATUS_SAVED });

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
	public static void checkRecallStatus(AccruedVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.RECALL, new int[] {
				ErmAccruedBillConst.BILLSTATUS_SAVED });

		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getApprstatus();// 审核状态
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
	 * 审核单据状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkApproveStatus(AccruedVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.AUDIT,
				new int[] { ErmAccruedBillConst.BILLSTATUS_SAVED });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * 反审核单据状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkUnApproveStatus(AggAccruedBillVO aggVo) throws DataValidateException {
		AccruedVO head = aggVo.getParentVO();
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.UNAUDIT, new int[] {
				ErmAccruedBillConst.BILLSTATUS_SAVED ,
				ErmAccruedBillConst.BILLSTATUS_APPROVED });

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

	}

	/**
	 * @param djzt
	 *            单据状态
	 * @param operation
	 *            执行动作
	 * @param statusAllowed
	 *            允许动作执行的状态值
	 * @return 空表示验证通过, 否则返回错误提示信息
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed) {

		for (int i = 0; i < statusAllowed.length; i++) {
			if (statusAllowed[i] == djzt)
				return null;
		}
		String djztName = getDjztName(djzt);
		String strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", null, "0201212-0083",
				null, new String[] { djztName });/*
												 * @ res "单据状态为"
												 */

		String operationName = ActionUtils.getOperationName(operation);

		return strMessage + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000267")/*
																											 * @
																											 * res
																											 * ",不能"
																											 */
				+ operationName;
	}

	/**
	 * 获取单据状态名称
	 * 
	 * @param djzt
	 * @return
	 */
	private static String getDjztName(int djzt) {
		String name = "";
		switch (djzt) {
		case ErmAccruedBillConst.BILLSTATUS_TEMPSAVED:
			name = ErmAccruedBillConst.BILLSTATUS_TEMPSAVED_NAME;
			break;
		case ErmAccruedBillConst.BILLSTATUS_SAVED:
			name = ErmAccruedBillConst.BILLSTATUS_SAVED_NAME;
			break;
		case ErmAccruedBillConst.BILLSTATUS_APPROVED:
			name = ErmAccruedBillConst.BILLSTATUS_APPROVED_NAME;
			break;
		default:
			break;
		}
		return name;
	}
}
