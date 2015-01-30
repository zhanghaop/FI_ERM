package nc.ui.erm.accruedexpense.actions;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pub.BusinessException;

public class AccPreViewAction extends AccPrintAction {

	private static final long serialVersionUID = 1L;

	public AccPreViewAction() {
		ActionInitializer.initializeAction(this, IActionCode.PREVIEW);
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
		if(entry.selectTemplate() == 1){
			entry.preview();
		}
	}
}
