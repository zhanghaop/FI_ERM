package nc.ui.er.expensetype.action;

import nc.ui.er.expensetype.view.ExpenseTypeEditor;
import nc.ui.uif2.actions.batch.BatchEditAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.expensetype.ExpenseTypeVO;

public class extEditAction extends BatchEditAction {

	/**
	 * author liansg
	 */
	private static final long serialVersionUID = 1L;

	private BatchBillTable editor = null;

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	@Override
	protected boolean isActionEnable() {
		ExpenseTypeVO extVO = (ExpenseTypeVO) getModel().getSelectedData();
		boolean flag = true;
		if (extVO != null) {
			flag = ((ExpenseTypeEditor) getEditor()).isShareExpenseType(extVO);
		}
		return flag;
	}

}
