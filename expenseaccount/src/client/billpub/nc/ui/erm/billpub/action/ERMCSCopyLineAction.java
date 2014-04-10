package nc.ui.erm.billpub.action;

import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.CopyLineAction;

public class ERMCSCopyLineAction  extends CopyLineAction {

	private static final long serialVersionUID = 1L;
	
	@Override
	protected boolean isActionEnable() {
		return (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT);
	}
}
