package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.mactrlschema.view.MaCtrlTreePanel;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.ui.uif2.model.IAppModelDataManager;

public class RefreshAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	private IAppModelDataManager modelManager;

	private MaCtrlTreePanel treePanel;
	
	public RefreshAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.REFRESH);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		treePanel.init();
		modelManager.initModel();
		ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes.getInstance().getStrByID("common", "UCH007")/* "Ë¢ÐÂ³É¹¦£¡" */
				, getTreeModel().getContext());
	}

	@Override
	protected boolean isActionEnable() {

		return getTreeModel().getUiState() != UIState.DISABLE;
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
		this.treeModel.addAppEventListener(this);
	}

	public IAppModelDataManager getModelManager() {
		return modelManager;
	}

	public void setModelManager(IAppModelDataManager modelManager) {
		this.modelManager = modelManager;
	}

	public MaCtrlTreePanel getTreePanel() {
		return treePanel;
	}

	public void setTreePanel(MaCtrlTreePanel treePanel) {
		this.treePanel = treePanel;
	}
}
