package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

public class AccLinkApproveResultAction extends NCAction {

	private static final long serialVersionUID = 1L;

	public AccLinkApproveResultAction() {
		ActionInitializer.initializeAction(this, IActionCode.APPROVEINFO);
	}

	private BillManageModel model;

	public void doAction(ActionEvent e) throws Exception {
		AggAccruedBillVO selectvo = (AggAccruedBillVO) getModel().getSelectedData();
		if (selectvo == null)
			return;

		if (selectvo.getParentVO().getPk_accrued_bill() == null)
			return;

		String billPk = selectvo.getParentVO().getPk_accrued_bill();
		String tradeType = selectvo.getParentVO().getPk_tradetype();

		FlowStateDlg app = new FlowStateDlg(getModel().getContext().getEntranceUI(), tradeType, billPk,
				WorkflowTypeEnum.Approveflow.getIntValue());
		app.showModal();
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
}