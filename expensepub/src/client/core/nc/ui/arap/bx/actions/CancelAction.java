package nc.ui.arap.bx.actions;

import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 *         ȡ����ť����
 * 
 *         nc.ui.arap.bx.actions.CancelAction
 */
public class CancelAction extends BXDefaultAction {

	public void cancel() throws BusinessException {
		int yesOrNo = MessageDialog.showYesNoDlg(getMainPanel(), "",
				(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000348")/* @res "ȷ��Ҫȡ����" */));
		if (yesOrNo == UIDialog.ID_YES) {
			getMainPanel().setCurrentPageStatus(
					BillWorkPageConst.WORKSTAT_BROWSE);
			getMainPanel().updateView();
		}
	}
}