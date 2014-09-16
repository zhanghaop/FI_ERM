package nc.ui.erm.matterapp.actions;

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
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 申请单审核
 * @author chenshuaia
 */
public class AuditAction extends ErmAuditAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		try {
			Object objs[] = getModel().getSelectedOperaDatas();

			if(objs == null || objs.length == 0){
				return;
			}

			// 审核较验信息
			msgs = new MessageVO[objs.length];
			List<AggregatedValueObject> auditList = new ArrayList<AggregatedValueObject>();

			for (int i = 0; i < objs.length; i++) {
				AggMatterAppVO vo = (AggMatterAppVO) objs[i];

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
			}else{
				ErUiUtil.showBatchResults(getModel().getContext(), msgs);
			}
		} catch (Exception e2) {
			exceptionHandler.handlerExeption(e2);
		}
	}
	
	/**
	 * 校验审批数据
	 * @param billvo
	 * @return
	 */
	private MessageVO checkApprove(AggMatterAppVO billvo) {
		MessageVO msgVO = new MessageVO(billvo, ActionUtils.AUDIT);
		
		//审核日期校验
		UFDate shrq = ErUiUtil.getBusiDate();
		if (billvo.getParentVO().getBilldate().afterDate(shrq)) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000336")/*@res "审核日期不能早于单据录入日期"*/);
			return msgVO;
		}
		
		//审批状态校验
		NCObject ncObj = NCObject.newInstance(billvo);
		IFlowBizItf itf = (IFlowBizItf) ncObj.getBizInterface(nc.itf.uap.pf.metadata.IFlowBizItf.class);
		Integer approveStatus = itf.getApproveStatus();// 审批状态

		if (!(approveStatus.equals(IBillStatus.CHECKGOING) || approveStatus.equals(IBillStatus.COMMIT))) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0008")/*@res "该单据当前状态不能进行审核！"*/);
			return msgVO;
		}
		
		//权限校验
		if(!checkDataPermission(billvo)){
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(IShowMsgConstant.getDataPermissionInfo());
			return msgVO;
		}

		return msgVO;
	}
	
	protected MessageVO approveSingle(AggregatedValueObject appVO) throws Exception {
		AggMatterAppVO aggMaVo = (AggMatterAppVO)appVO;
		MessageVO result = null;
		String actionName = ErUtil.getApproveActionCode(aggMaVo.getParentVO().getPk_org());
		try {
			Object returnObj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), actionName, aggMaVo
					.getParentVO().getPk_tradetype(), appVO, null, null, null, null);
			if(returnObj ==null){//在审批过程中，弹出审核界面，然后直接点右上角的关闭
				result = new MessageVO(appVO, ActionUtils.AUDIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000339")/*@res "用户取消操作"*/);	
			}else{
				if (returnObj instanceof MessageVO[]) {
					MessageVO[] msgVos = (MessageVO[]) returnObj;
					MatterAppVO parentVO = (MatterAppVO) msgVos[0].getSuccessVO().getParentVO();
					parentVO.setWarningmsg(null);
					result = msgVos[0];
				} else if (returnObj instanceof AggMatterAppVO) {// 改签和加签的情况下会出现返回AggVo
					((AggMatterAppVO) returnObj).getParentVO().setWarningmsg(null);
					result = new MessageVO((AggMatterAppVO) returnObj, ActionUtils.AUDIT);
				}
			}
		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getEditor().getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "提示"
														 */, e.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " 是否继续审核？"
																									 */) == MessageDialog.ID_YES) {
				aggMaVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // 不检查
				result = approveSingle(appVO);
				aggMaVo.getParentVO().setHasntbcheck(UFBoolean.FALSE);
			} else {
				result = new MessageVO(appVO, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "预算申请失败"
															 */);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "当前单据已进行加锁处理"
																													 */;
			if (e instanceof PFBusinessException && lockErrMsg.equals(errMsg)) {
				errMsg = e.getMessage()
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																													 * @
																													 * res
																													 * "，该单据可能同时被他人操作中，请刷新后再试"
																													 */;
			}
			
			result = new MessageVO(appVO, ActionUtils.AUDIT, false, errMsg);
		}
		return result;
	}
	
	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggMatterAppVO aggBean = (AggMatterAppVO) selectedData[i];
			Integer appStatus = ((MatterAppVO) aggBean.getParentVO()).getApprstatus();
			// 审核中
			if (appStatus.equals(IBillStatus.CHECKGOING) || appStatus.equals(IBillStatus.COMMIT)) {
				return true;
			}
		}

		return false;
	}
}