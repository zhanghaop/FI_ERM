package nc.ui.er.expensetype.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.ui.er.expensetype.view.ExpenseTypeEditor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;

public class extDeleteAction extends BatchDelLineAction {

	private BatchBillTable editor = null;

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

	public void doAction(ActionEvent e) throws Exception {
		getEditor().getBillCardPanel().stopEditing();
		ExpenseTypeVO expVO = (ExpenseTypeVO) this.getModel().getSelectedData();
		String pk_expensetype = expVO.getPk_expensetype();
		if (pk_expensetype != null) {
			boolean isexpensetype = NCLocator.getInstance().lookup(nc.itf.arap.prv.IBXBillPrivate.class).getIsExpensetypeUsed(pk_expensetype);
			if (isexpensetype) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0001")/* @res "该费用类型已被引用,不能删除！" */);
			}
		}
		super.doAction(e);
	}

	@Override
	protected boolean isActionEnable() {
		if (this.getModel().getUiState() == UIState.EDIT) {
			return true;
		}
		ExpenseTypeVO vo = (ExpenseTypeVO) getModel().getSelectedData();
		boolean flag = true;
		if (vo != null) {
			flag = ((ExpenseTypeEditor) getEditor()).isShareExpenseType(vo);
		}
		return flag;
	}

}