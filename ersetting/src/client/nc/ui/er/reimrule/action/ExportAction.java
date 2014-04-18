package nc.ui.er.reimrule.action;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.er.reimrule.ExportExcelDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.AbstractAppModel;

public class ExportAction extends NCAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1071816648210000749L;
	private ExportExcelDialog expDialog;
	private BatchBillTable editor;

	private AbstractAppModel model = null;

	public ExportAction() {
		ActionInitializer.initializeAction(this, IActionCode.EXPORT);
	}

	public ExportExcelDialog getExpDialog() {
		if(expDialog == null)
			expDialog = new ExportExcelDialog(getModel().getContext().getEntranceUI(), editor.getBillCardPanel());
		return expDialog;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		 getExpDialog().setVisible(true);
	}
	
	@Override
	protected boolean isActionEnable() {
		return model.getUiState() == UIState.NOT_EDIT && model.getSelectedData()!=null;
	}
	
	public AbstractAppModel getModel() {
		return model;
	}
 
	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
	
	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}
}
