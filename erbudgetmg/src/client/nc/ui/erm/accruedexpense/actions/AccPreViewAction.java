package nc.ui.erm.accruedexpense.actions;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;

public class AccPreViewAction extends AccPrintAction {

	private static final long serialVersionUID = 1L;

	public AccPreViewAction() {
		ActionInitializer.initializeAction(this, IActionCode.PREVIEW);
	}

	@Override
	protected void printByNodeKey(String nodeKey) {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI(), getDataSource());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		entry.selectTemplate();
		entry.preview();
	}
}
