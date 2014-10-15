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
	 * ����ɾ��״̬����
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
	 * �����ύ״̬����
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
	 * �����ջ�״̬����
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkRecallStatus(AccruedVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.RECALL, new int[] {
				ErmAccruedBillConst.BILLSTATUS_SAVED });

		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getApprstatus();// ���״̬
			if (apprStatus.equals(Integer.valueOf(IBillStatus.CHECKGOING))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0055")/*
																										 * @
																										 * res
																										 * "������������У������ջص��ݣ�"
																										 */;
			}
		}

		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * ��˵���״̬����
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
	 * ����˵���״̬����
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
	 *            ����״̬
	 * @param operation
	 *            ִ�ж���
	 * @param statusAllowed
	 *            ������ִ�е�״ֵ̬
	 * @return �ձ�ʾ��֤ͨ��, ���򷵻ش�����ʾ��Ϣ
	 */
	public static String checkBillStatus(int djzt, int operation, int[] statusAllowed) {

		for (int i = 0; i < statusAllowed.length; i++) {
			if (statusAllowed[i] == djzt)
				return null;
		}
		String djztName = getDjztName(djzt);
		String strMessage = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", null, "0201212-0083",
				null, new String[] { djztName });/*
												 * @ res "����״̬Ϊ"
												 */

		String operationName = ActionUtils.getOperationName(operation);

		return strMessage + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000267")/*
																											 * @
																											 * res
																											 * ",����"
																											 */
				+ operationName;
	}

	/**
	 * ��ȡ����״̬����
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
