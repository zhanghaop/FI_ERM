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
	 * 审核单据状态控制
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
	 * 反审核单据状态控制
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
																										 * "单据已经关闭，不能取消审批单据！"
																										 */;
			}
			
			if(msgs == null){
				for (MtAppDetailVO detail : aggVo.getChildrenVO()) {
					if(detail.getClose_status() == ErmMatterAppConst.CLOSESTATUS_Y){
						msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0086")/*
																												 * @
																												 * res
																												 * "单据存在已关闭行，不能取消审核单据！"
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
	 * 单据删除状态控制
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
																									 * "单据正在审核中，不能删除！"
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
	public static void checkCommitStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.COMMIT,
				new int[] { ErmMatterAppConst.BILLSTATUS_SAVED });
		
		if (StringUtils.isNullWithTrim(msgs)) {
			Integer apprStatus = head.getApprstatus();// 审核状态
			if (!apprStatus.equals(Integer.valueOf(IBillStatus.FREE))) {
				msgs = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", "非自由态单据,不可提交！", "0201212-0102");
			}
		}
		
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
	public static void checkRecallStatus(MatterAppVO head) throws DataValidateException {
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.RECALL,
				new int[] {ErmMatterAppConst.BILLSTATUS_SAVED});

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
	 * 单据关闭状态控制
	 * 
	 * @param head
	 * @throws DataValidateException
	 */
	public static void checkCloseStatus(MatterAppVO head) throws DataValidateException {
		// 设置表头关闭信息
		UFDateTime closeDate = new UFDateTime(InvocationInfoProxy.getInstance().getBizDateTime());
		
		if (head.getApprovetime() != null && closeDate.before(head.getApprovetime())) {
			throw new DataValidateException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0091")/*
					 * @
					 * res
					 * "关闭日期不得早于单据审批日期！"
					 */);
		}
			
		String msgs = checkBillStatus(head.getBillstatus(), ActionUtils.CLOSE,
					new int[] { ErmMatterAppConst.BILLSTATUS_APPROVED });
		if (!StringUtils.isNullWithTrim(msgs)) {
			throw new DataValidateException(msgs);
		}
	}

	/**
	 * 单据打开状态控制
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
		String strMessage =nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0",null,"0201212-0083",null,new String[]{djztName});/*
																											 * @
																											 * res
																											 * "单据状态为"
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