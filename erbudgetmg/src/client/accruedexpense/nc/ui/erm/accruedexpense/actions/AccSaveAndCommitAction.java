package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.trade.pub.IBillStatus;

public class AccSaveAndCommitAction extends NCAction {

	private static final long serialVersionUID = 1L;
	
	private AbstractAppModel model;

	private NCAction saveAction ;
	private NCAction commitAction ;
	
	
	public AccSaveAndCommitAction() {
    	ActionInitializer.initializeAction(this, IActionCode.SAVECOMMIT);
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		getSaveAction().doAction(arg0);
		getCommitAction().doAction(arg0);
	}

	public NCAction getSaveAction() {
		return saveAction;
	}

	public void setSaveAction(NCAction saveAction) {
		this.saveAction = saveAction;
	}

	public NCAction getCommitAction() {
		return commitAction;
	}

	public void setCommitAction(NCAction commitAction) {
		this.commitAction = commitAction;
	}
	
	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() == UIState.EDIT) {
			AggAccruedBillVO vo = ((AggAccruedBillVO) getModel().getSelectedData());
			return ((vo.getParentVO().getApprstatus()).equals(IBillStatus.FREE));
		}
		return super.isActionEnable();
	}
}
