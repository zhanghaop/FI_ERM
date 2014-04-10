package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.bs.uif2.IActionCode;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.bill.BillData;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchSaveAction;
import nc.ui.uif2.model.HierachicalDataAppModel;

public class SaveFldAction extends BatchSaveAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	
	public SaveFldAction() {
		String str = NCLangRes.getInstance().getStrByID("uif2", "ActionRegistry-000000")/*保存*/;
		ActionInitializer.initializeAction(this, IActionCode.SAVE);
		setBtnName(str);
		putValue(Action.SHORT_DESCRIPTION, str + "(Alt+S)");
		setCode(IActionCode.SAVE);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke((int)'S', KeyEvent.ALT_MASK));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		getEditor().getBillCardPanel().stopEditing();
//		MaCtrlSchemaChecker.checkOperation(getTreeModel());
		doNotNulValidate();// 必输项校验
		super.doAction(e);
	}

	private void doNotNulValidate() {
		BillData data = getEditor().getBillCardPanel().getBillData();
		try {
			if (data != null)
				data.dataNotNullValidate();
		} catch (nc.vo.pub.ValidationException ex) {
			throw new BusinessExceptionAdapter(ex);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}
}
