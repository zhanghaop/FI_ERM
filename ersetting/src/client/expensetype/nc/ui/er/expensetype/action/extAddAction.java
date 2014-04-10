package nc.ui.er.expensetype.action;

import java.awt.event.ActionEvent;

import nc.itf.org.IOrgConst;
import nc.ui.er.expensetype.view.ControlAreaOrgPanel;
import nc.ui.uif2.actions.batch.BatchAddLineWithDefValueAction;
import nc.vo.bd.pub.NODE_TYPE;
import nc.vo.er.expensetype.ExpenseTypeVO;

public class extAddAction extends BatchAddLineWithDefValueAction {

	/**
	 * @author liansg
	 */
	private static final long serialVersionUID = 1L;

	private ControlAreaOrgPanel caOrgPanel;

	@Override
	protected void setDefaultValue(Object obj) {
		super.setDefaultValue(obj);
		if (!(obj instanceof ExpenseTypeVO)) {
			return;
		}
		ExpenseTypeVO ext = (ExpenseTypeVO) obj;
		NODE_TYPE nodeType = getModel().getContext().getNodeType();
		String pk_group;
		if (nodeType == NODE_TYPE.GLOBE_NODE) {
			pk_group = IOrgConst.GLOBEORGTYPE;
		} else {
			pk_group = getModel().getContext().getPk_group();
		}
		ext.setPk_group(pk_group);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
	}


	public ControlAreaOrgPanel getCaOrgPanel() {
		return caOrgPanel;
	}

	public void setCaOrgPanel(ControlAreaOrgPanel caOrgPanel) {
		this.caOrgPanel = caOrgPanel;
	}
	
	@Override
	protected boolean isActionEnable() {
//		if(this.getModel().getUiState() == UIState.EDIT){
//			return false;
//		}
		return true;
	}

}
