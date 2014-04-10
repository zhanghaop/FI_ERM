package nc.ui.erm.mactrlschema.actions;


import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchAddLineAction;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.billtype.BilltypeVO;

public class AddBilAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;

	@Override
	protected boolean isActionEnable() {
		return getModel().getUiState() != UIState.DISABLE;
	}
	
//	@Override
//	public void doAction(ActionEvent e) throws Exception {
//		MaCtrlSchemaChecker.checkOperation(getTreeModel());
//		super.doAction(e);
//	}

	@Override
	protected void setDefaultData(Object obj) {
		if(obj != null){
			MtappCtrlbillVO vo = (MtappCtrlbillVO)obj;
			vo.setPk_group(getTreeModel().getContext().getPk_group());
			vo.setPk_org(getTreeModel().getContext().getPk_org());
			vo.setPk_tradetype(((BilltypeVO) getTreeModel().getSelectedData()).getPk_billtypecode());
		}
	}
	

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}
}
