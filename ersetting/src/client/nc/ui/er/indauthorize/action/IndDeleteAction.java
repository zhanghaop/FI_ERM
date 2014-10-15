package nc.ui.er.indauthorize.action;

import nc.ui.er.indauthorize.view.IndAuthorizeEditor;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.indauthorize.IndAuthorizeVO;



public class IndDeleteAction extends BatchDelLineAction {

	/**
	 * @author liansg
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
		IndAuthorizeVO rvtVO = (IndAuthorizeVO) getModel()
				.getSelectedData();
		if (rvtVO != null) {
			flag = ((IndAuthorizeEditor) getEditor())
					.isShareIndAuthorize(rvtVO);
		}
		return flag;
	}
	
	
	

}
