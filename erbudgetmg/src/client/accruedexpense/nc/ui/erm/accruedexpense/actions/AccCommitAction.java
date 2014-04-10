package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.check.AccruedBillVOStatusChecker;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.trade.pub.IBillStatus;

/**
 * 预提单提交
 * @author shengqy
 *
 */
public class AccCommitAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public AccCommitAction() {
		ActionInitializer.initializeAction(this, IActionCode.COMMIT);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object objs[] = getModel().getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}

		// 审核较验信息
		MessageVO[] msgs = new MessageVO[objs.length];
		List<AggAccruedBillVO> commitList = new ArrayList<AggAccruedBillVO>();

		for (int i = 0; i < objs.length; i++) {
			AggAccruedBillVO vo = (AggAccruedBillVO) objs[i];

			// 这里将不符合状态的单据过滤掉，减少数据量
			msgs[i] = checkCommit(vo);
			if (!msgs[i].isSuccess()) {
				continue;
			}
			commitList.add(vo);
		}

		if (!commitList.isEmpty()) {
			MessageVO[] returnMsgs = commitOneByOne(commitList);
			List<AggregatedValueObject> commitVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			getModel().directlyUpdate(commitVos.toArray(new AggregatedValueObject[] {}));
		}

		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO[] commitOneByOne(List<AggAccruedBillVO> commitList) throws BusinessException {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggAccruedBillVO aggVo : commitList) {
			MessageVO msgReturn = commitSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO commitSingle(AggAccruedBillVO aggvo) throws BusinessException {
		MessageVO result = null;
		try {
			Object obj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), IPFActionName.SAVE, aggvo
					.getParentVO().getPk_tradetype(), aggvo, null, null, null, null);
			if (obj == null) {
				result = new MessageVO(aggvo, ActionUtils.COMMIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000339")/*
																													 * @
																													 * res
																													 * "用户取消操作"
																													 */);
			} else {
				if (obj instanceof AggAccruedBillVO[]) {// 仅提交
					AggAccruedBillVO[] vos = (AggAccruedBillVO[]) obj;
					result = new MessageVO(vos[0], ActionUtils.COMMIT);
				} else if (obj instanceof MessageVO[]) {// 提交并审批的情况会出现
					MessageVO[] messages = (MessageVO[]) obj;
					result = messages[0];
				}
			}

		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(aggvo, ActionUtils.COMMIT, false, errMsg);
		}
		return result;
	}

	private MessageVO checkCommit(AggAccruedBillVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.COMMIT);
		try {
			AccruedBillVOStatusChecker.checkCommitStatus(vo.getParentVO());
		} catch (DataValidateException e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}
	
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggAccruedBillVO aggBean = (AggAccruedBillVO) selectedData[i];
			Integer appStatus = ((AccruedVO) aggBean.getParentVO()).getApprstatus();
			Integer billStatus = ((AccruedVO) aggBean.getParentVO()).getBillstatus();
			// 审核中
			if (billStatus.equals(ErmAccruedBillConst.BILLSTATUS_SAVED) && appStatus.equals(IBillStatus.FREE)) {
				return true;
			}
		}

		return false;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		model.addAppEventListener(this);
		this.model = model;
	}
}
