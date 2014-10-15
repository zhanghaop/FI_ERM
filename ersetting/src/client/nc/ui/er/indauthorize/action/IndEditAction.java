package nc.ui.er.indauthorize.action;

import nc.ui.er.indauthorize.view.IndAuthorizeEditor;
import nc.ui.pubapp.uif2app.actions.batch.BatchEditAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.indauthorize.IndAuthorizeVO;


public class IndEditAction extends BatchEditAction {

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
		boolean flag = super.isActionEnable();
		IndAuthorizeVO extVO = (IndAuthorizeVO) getModel()
				.getSelectedData();
		if (getModel().getUiState()==UIState.EDIT&&extVO != null) {
			flag = ((IndAuthorizeEditor) getEditor())
					.isShareIndAuthorize(extVO);
		}

		return flag;
	}

}
