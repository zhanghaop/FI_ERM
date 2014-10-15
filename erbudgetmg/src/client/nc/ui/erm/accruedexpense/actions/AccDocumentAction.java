package nc.ui.erm.accruedexpense.actions;

import nc.ui.erm.action.DocumentAction;
import nc.ui.uif2.UIState;

public class AccDocumentAction extends DocumentAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD) {
			return true;
		}else if(getModel().getSelectedData() != null){
			return true;
		}
		return false;  
	}
	
	
}
