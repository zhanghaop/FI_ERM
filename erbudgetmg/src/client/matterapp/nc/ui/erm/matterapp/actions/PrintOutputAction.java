package nc.ui.erm.matterapp.actions;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.uif2.actions.OutputAction;

public class PrintOutputAction extends OutputAction {
	private static final long serialVersionUID = 1L;
	
	@Override
	public String getNodeKey() {
		String nodeCode = getModel().getContext().getNodeCode();
		if (!ErmMatterAppConst.MAPP_NODECODE_MN.equals(nodeCode)) {
			return null;
		} else {
			return ((MAppModel) getModel()).getDjlxbm();
		}
	}
}
