package nc.ui.erm.sharerule.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.IAppModelDataManager;

public class RefreshShareruleAction extends NCAction {

	private static final long serialVersionUID = -4738346832430164006L;

	private IAppModelDataManager dataMrg = null;

	private AbstractUIAppModel model = null;

	public RefreshShareruleAction() {
		ActionInitializer.initializeAction(this, IActionCode.REFRESH);
	}

	@Override
	protected boolean isActionEnable() {

		return getModel().getUiState() == UIState.INIT
				|| getModel().getUiState() == UIState.NOT_EDIT;

	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		getDataMrg().initModel();
	}

	public IAppModelDataManager getDataMrg() {
		return dataMrg;
	}

	public void setDataMrg(IAppModelDataManager dataMrg) {
		this.dataMrg = dataMrg;
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
	}

}
