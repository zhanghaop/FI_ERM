package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.trade.pub.IBillStatus;

/**
 * ���沢�ύ��ť
 * @author chenshuaia
 *
 */
public class ErmBillSaveAndCommitAction extends NCAction {
	private static final long serialVersionUID = 2584277015394869264L;

	private NCAction saveAction ;
	private NCAction commitAction ;
	
	private AbstractAppModel model;
	
    public ErmBillSaveAndCommitAction(){
    	ActionInitializer.initializeAction(this, IActionCode.SAVECOMMIT);
    }
    
    public void doAction(ActionEvent e) throws Exception {
		getSaveAction().doAction(e);
		getCommitAction().doAction(e);
	}
    
	public NCAction getSaveAction() {
		return saveAction;
	}
	public void setSaveAction(NCAction saveAction) {
		this.saveAction = saveAction;
	}
	public NCAction getCommitAction() {
		return commitAction;
	}
	public void setCommitAction(NCAction commitAction) {
		this.commitAction = commitAction;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() == UIState.EDIT) {
			JKBXVO vo = ((JKBXVO) getModel().getSelectedData());
			return ((vo.getParentVO().getSpzt()).equals(IBillStatus.FREE));
		}

		return super.isActionEnable();
	}
}
