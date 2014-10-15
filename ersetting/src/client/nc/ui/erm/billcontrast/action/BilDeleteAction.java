package nc.ui.erm.billcontrast.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.vo.util.ManageModeUtil;



public class BilDeleteAction extends BatchDelLineAction{

	private static final long serialVersionUID = 1L;
	
	public void doAction(ActionEvent e) throws Exception {
		if (isDoBeforeAction(this, e)) {
			super.doAction(e);

		}
	}
	
	public boolean isDoBeforeAction(Action action, ActionEvent e) {
		if (!ManageModeUtil.manageable(getModel().getSelectedData(), getModel()
				.getContext())) {
			MessageDialog.showErrorDlg(getModel().getContext().getEntranceUI(),
					null, getErrorMsg());
			return false;
		}
		return true;
	}
	
	public String getErrorMsg() {
		return ManageModeUtil.getDisManageableMsg(getModel().getContext()
				.getNodeType());
	}
}
