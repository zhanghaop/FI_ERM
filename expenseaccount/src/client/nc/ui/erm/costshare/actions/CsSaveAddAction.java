package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.AddAction;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.model.AbstractAppModel;
import nc.uif2.annoations.MethodType;
import nc.uif2.annoations.ModelMethod;
import nc.uif2.annoations.ModelType;

@SuppressWarnings("serial")
public class CsSaveAddAction extends NCAction {
	private SaveAction saveAction;
	private AddAction addAction;
	private AbstractAppModel model;
	public CsSaveAddAction (){
		super(); 
		ActionInitializer.initializeAction(this, IActionCode.SAVEADD);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		getSaveAction().doAction(e);
		getAddAction().doAction(e);
	}

	public void setSaveAction(SaveAction saveAction) {
		this.saveAction = saveAction;
	}

	public SaveAction getSaveAction() {
		return saveAction;
	}
	
	@Override
	protected boolean isActionEnable() {
		return model.getUiState()==UIState.EDIT||model.getUiState()==UIState.ADD;
	}

	public void setAddAction(AddAction addAction) {
		this.addAction = addAction;
	}

	public AddAction getAddAction() {
		return addAction;
	}
	
	@ModelMethod(modelType=ModelType.AbstractAppModel,methodType=MethodType.SETTER)
	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	
	@ModelMethod(modelType=ModelType.AbstractAppModel,methodType=MethodType.GETTER)
	public AbstractAppModel getModel() {
		return model;
	}

	
}
