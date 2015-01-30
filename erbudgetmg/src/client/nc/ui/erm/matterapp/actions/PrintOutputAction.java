package nc.ui.erm.matterapp.actions;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pub.BusinessException;

public class PrintOutputAction extends PreViewAction {
	private static final long serialVersionUID = 1L;
	
	public PrintOutputAction(){
		ActionInitializer.initializeAction(this, IActionCode.OUTPUT);
	}
	
	@Override
	protected void printByNodeKey(String nodeKey) throws BusinessException {
		if (nodeKey == null) {
			nodeKey = getNodeKey();
		}

		PrintEntry entry = new PrintEntry(this.getModel().getContext().getEntranceUI());
		String pkUser = getModel().getContext().getPk_loginUser();
		entry.setTemplateID(MatterAppUiUtil.getPK_group(), getModel().getContext().getNodeCode(), pkUser, null, nodeKey);
		
		setDatasource(entry);
		if(entry.selectTemplate() == 1){
			entry.output();
		}
	}
}
