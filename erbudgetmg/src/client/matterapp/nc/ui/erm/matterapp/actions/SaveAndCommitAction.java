package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.AbstractAppModel;

/**
 * 保存并提交按钮
 * @author chenshuaia
 *
 */
public class SaveAndCommitAction extends NCAction {
	private static final long serialVersionUID = 2584277015394869264L;

	private NCAction saveAction ;
	private NCAction commitAction ;
	
	private AbstractAppModel model;
	
    public SaveAndCommitAction(){
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
	}
}
