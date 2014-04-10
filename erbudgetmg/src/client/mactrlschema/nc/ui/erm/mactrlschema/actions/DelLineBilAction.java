package nc.ui.erm.mactrlschema.actions;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.mactrlschema.MaCtrlSchemaChecker;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;

public class DelLineBilAction extends BatchDelLineAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable() && (getModel().getUiState() != UIState.DISABLE);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		MaCtrlSchemaChecker.checkCtrlBillOperation(getTreeModel(), getCheckCtrlBillList());
		super.doAction(e);
	}

	/** 
	 * 得到需要删除的控制对象做校验
	 * @return
	 */
	private List<String> getCheckCtrlBillList() {
		Object[] objs = getModel().getSelectedOperaDatas();
		List<String> deltCtrlBillList = new ArrayList<String>();
		for (Object obj : objs) {
			if(((MtappCtrlbillVO)obj).getSrc_tradetype() != null){
				deltCtrlBillList.add(((MtappCtrlbillVO)obj).getSrc_tradetype());
			}
		}
		return deltCtrlBillList;
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}

}
