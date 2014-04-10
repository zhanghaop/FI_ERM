package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.UIState;

/**
 * ���÷�̯ҳǩ����ճ����action
 * @author wangled
 *
 */
public class ERMCSPasteLineAction extends ERMPasteLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isActionEnable() {
		return (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}
}
