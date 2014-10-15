package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErUtil;
import nc.bs.ml.NCLangResOnserver;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.ui.erm.action.ErmAuditAction;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

public class AccAuditAction extends ErmAuditAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected MessageVO approveSingle(AggregatedValueObject aggvo) throws Exception {
		AggAccruedBillVO accbillvo = (AggAccruedBillVO) aggvo;
		MessageVO result = null;
		String actionName = ErUtil.getApproveActionCode(accbillvo.getParentVO().getPk_org());
		try {
			Object returnObj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), actionName, accbillvo
					.getParentVO().getPk_tradetype(), aggvo, null, null, null, null);
			if (returnObj == null) {// �����������У�������˽��棬Ȼ��ֱ�ӵ����ϽǵĹر�
				result = new MessageVO(aggvo, ActionUtils.AUDIT);
				result.setSuccess(false);
				result
						.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2011", "UPP2011-000339")/*
																	 * @res
																	 * "�û�ȡ������"
																	 */);
			} else {
				if (returnObj instanceof MessageVO[]) {
					MessageVO[] msgVos = (MessageVO[]) returnObj;
					AccruedVO parentVO = (AccruedVO) msgVos[0].getSuccessVO().getParentVO();
					parentVO.setWarningmsg(null);
					result = msgVos[0];
				} else if (returnObj instanceof AggAccruedBillVO) {// ��ǩ�ͼ�ǩ������»���ַ���AggVo
					((AggAccruedBillVO) returnObj).getParentVO().setWarningmsg(null);
					result = new MessageVO((AggAccruedBillVO) returnObj, ActionUtils.AUDIT);
				}
			}
		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getEditor().getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "��ʾ"
														 */, e.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " �Ƿ������ˣ�"
																									 */) == MessageDialog.ID_YES) {
				accbillvo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // �����
				result = approveSingle(aggvo);
				accbillvo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
			} else {
				result = new MessageVO(aggvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "Ԥ������ʧ��"
															 */);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "��ǰ�����ѽ��м�������"
																													 */;
			if (e instanceof PFBusinessException && lockErrMsg.equals(errMsg)) {
				errMsg = e.getMessage()
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																													 * @
																													 * res
																													 * "���õ��ݿ���ͬʱ�����˲����У���ˢ�º�����"
																													 */;
			}

			result = new MessageVO(aggvo, ActionUtils.AUDIT, false, errMsg);
		}
		return result;
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object objs[] = getModel().getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}

		// ��˽�����Ϣ
		msgs = new MessageVO[objs.length];
		List<AggregatedValueObject> auditList = new ArrayList<AggregatedValueObject>();

		for (int i = 0; i < objs.length; i++) {
			AggAccruedBillVO vo = (AggAccruedBillVO) objs[i];

			msgs[i] = checkApprove(vo);

			if (msgs[i].isSuccess()) {
				auditList.add(vo);
			}
		}

		if (!auditList.isEmpty()) {
			if (auditList.size() > 1) {
				executeBatchAudit(auditList);
			} else {
				MessageVO[] returnMsgs = new MessageVO[] { approveSingle(auditList.get(0)) };
				List<AggregatedValueObject> auditedVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
				getModel().directlyUpdate(auditedVos.toArray(new AggregatedValueObject[auditedVos.size()]));
				ErUiUtil.showBatchResults(getModel().getContext(), msgs);
			}
		} else {
			ErUiUtil.showBatchResults(getModel().getContext(), msgs);
		}
	}

	private MessageVO checkApprove(AggAccruedBillVO aggvo) {
		MessageVO msgVO = new MessageVO(aggvo, ActionUtils.AUDIT);

		// �������У��
		UFDate shrq = ErUiUtil.getBusiDate();
		if (aggvo.getParentVO().getBilldate().afterDate(shrq)) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000336")/*
																												 * @res
																												 * "������ڲ������ڵ���¼������"
																												 */);
			return msgVO;
		}

		// ����״̬У��
		NCObject ncObj = NCObject.newInstance(aggvo);
		IFlowBizItf itf = (IFlowBizItf) ncObj.getBizInterface(nc.itf.uap.pf.metadata.IFlowBizItf.class);
		Integer approveStatus = itf.getApproveStatus();// ����״̬

		if (!(approveStatus.equals(IBillStatus.CHECKGOING) || approveStatus.equals(IBillStatus.COMMIT))) {
			msgVO.setSuccess(false);
			msgVO
					.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0008")/*
																													 * @res
																													 * "�õ��ݵ�ǰ״̬���ܽ�����ˣ�"
																													 */);
			return msgVO;
		}

		// Ȩ��У��
		if (!checkDataPermission(aggvo)) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(IShowMsgConstant.getDataPermissionInfo());
			return msgVO;
		}

		return msgVO;
	}
	
	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggAccruedBillVO aggBean = (AggAccruedBillVO) selectedData[i];
			Integer appStatus = ((AccruedVO) aggBean.getParentVO()).getApprstatus();
			// �����
			if (appStatus.equals(IBillStatus.CHECKGOING) || appStatus.equals(IBillStatus.COMMIT)) {
				return true;
			}
		}
		return false;
	}

}
