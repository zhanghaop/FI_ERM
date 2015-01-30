package nc.ui.erm.accruedexpense.actions;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pub.BusinessException;

public class AccPrintOutputAction extends AccPrintAction {
	private static final long serialVersionUID = 1L;

	public AccPrintOutputAction() {
		ActionInitializer.initializeAction(this, IActionCode.OUTPUT);
	}

	@Override
	protected void printByNodeKey(String nodeKey) throws BusinessException {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(ErUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);

		setDatasource(entry);
		if (entry.selectTemplate() == 1) {
			entry.output();
		}
	}
}
