package nc.bs.erm.matterapp.check;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.trade.pub.IBillStatus;

public class VOStatusChecker {
	/**
	 * ��˵���״̬����
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkApproveStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.AUDIT,
				new int[] { ErmMatterAppConst.BILLSTATUS_SAVED });

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
	public static void checkUnApproveStatus(AggMatterAppVO aggVo) throws DataValidateException {
		MatterAppVO head = aggVo.getParentVO();
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.UNAUDIT, new int[] {
			ErmMatterAppConst.BILLSTATUS_SAVED, ErmMatterAppConst.BILLSTATUS_APPROVED });

		if (StringUtils.isNullWithTrim(msgs)) {
			int closeStatus = head.getClose_status();
			if (closeStatus == ErmMatterAppConst.CLOSESTATUS_Y) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0054")/*
																										 * @
																										 * res
																										 * "�����Ѿ��رգ�����ȡ���������ݣ�"
																										 */;
			}
			
			if(msgs == null){
				for (MtAppDetailVO detail : aggVo.getChildrenVO()) {
					if(detail.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y){
						msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0086")/*
																												 * @
																												 * res
																												 * "���ݴ����ѹر��У�����ȡ����˵��ݣ�"
																												 */;
						break;
					}
				}
			}
		}
		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}

	}

	/**
	 * ����ɾ��״̬����
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkDeleteStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.DELETE, new int[] {
				ErmMatterAppConst.BILLSTATUS_SAVED, ErmMatterAppConst.BILLSTATUS_TEMPSAVED });

		if (!StringUtils.isNullWithTrim(msgs)) {
			if (head.getApprstatus() != null
					&& (head.getApprstatus().equals(IPfRetCheckInfo.GOINGON) || head.getApprstatus().equals(
							IPfRetCheckInfo.COMMIT))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000398")/*
																									 * @
																									 * res
																									 * "������������У�����ɾ����"
																									 */;
			}
		}

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
	public static void checkCommitStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.COMMIT,
				new int[] { ErmMatterAppConst.BILLSTATUS_SAVED });
		
		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getApprstatus();// ���״̬
			if (!apprStatus.equals(Integer.valueOf(IBillStatus.FREE))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", "������̬����,�����ύ��", "0201212-0102");
			}
		}
		
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
	public static void checkRecallStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.RECALL,
				new int[] {ErmMatterAppConst.BILLSTATUS_SAVED});

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
	 * ���ݹر�״̬����
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkCloseStatus(MatterAppVO head) throws DataValidateException {
		// ���ñ�ͷ�ر���Ϣ
		UFDateTime closeDate = new UFDateTime(InvocationInfoProxy.getInstance().getBizDateTime());
		
		if (head.getApprovetime() != null && closeDate.before(head.getApprovetime())) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0091")/*
					 * @
					 * res
					 * "�ر����ڲ������ڵ����������ڣ�"
					 */);
		}
			
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.CLOSE,
					new int[] { ErmMatterAppConst.BILLSTATUS_APPROVED });
		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * ���ݴ�״̬����
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkOpenBillStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.OPEN,
				new int[] { ErmMatterAppConst.BILLSTATUS_APPROVED });

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
		String strMessage =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0",null,"0201212-0083",null,new String[]{djztName});/*
																											 * @
																											 * res
																											 * "����״̬Ϊ"
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
	 * @param djzt
	 * @return
	 */
	private static String getDjztName(int djzt) {
		String name = "";
		switch (djzt) {
		case ErmMatterAppConst.BILLSTATUS_TEMPSAVED:
			name = ErmMatterAppConst.BILLSTATUS_TEMPSAVED_NAME;
			break;
		case ErmMatterAppConst.BILLSTATUS_SAVED:
			name = ErmMatterAppConst.BILLSTATUS_SAVED_NAME;
			break;
		case ErmMatterAppConst.BILLSTATUS_APPROVED:
			name = ErmMatterAppConst.BILLSTATUS_APPROVED_NAME;
			break;
		default:
			break;
		}
		return name;
	}
}