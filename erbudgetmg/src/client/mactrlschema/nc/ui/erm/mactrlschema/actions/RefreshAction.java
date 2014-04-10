package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.mactrlschema.view.MaCtrlTreePanel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.SuperVO;
import nc.vo.pub.billtype.BilltypeVO;

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
		// 交易类型树展现
		HashMap<String, BilltypeVO> billtypes = PfDataCache.getBilltypes();
		List<BilltypeVO> list = new ArrayList<BilltypeVO>();
		for (BilltypeVO vo : billtypes.values()) {
			if (vo.getSystemcode() != null
					&& vo.getSystemcode().equalsIgnoreCase(
							BXConstans.ERM_PRODUCT_CODE)) {
				if (vo.getPk_group() != null
						&& !vo.getPk_group().equalsIgnoreCase(
								ErUiUtil.getPK_group())) {
					continue;
				}
				if (vo.getPk_billtypecode().startsWith(
						ErmMatterAppConst.MatterApp_PREFIX)
						&& !vo.getPk_billtypecode().equals(
								ErmMatterAppConst.MatterApp_BILLTYPE)) {
					list.add(vo);
				}
			}
		}
		
		getTreeModel().initModel(list.toArray(new SuperVO[0]));
		this.getModelManager().initModel();
		ShowStatusBarMsgUtil.showStatusBarMsg(NCLangRes.getInstance().getStrByID("common", "UCH007")/* "刷新成功！" */
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
