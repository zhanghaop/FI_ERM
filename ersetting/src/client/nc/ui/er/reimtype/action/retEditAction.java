package nc.ui.er.reimtype.action;

import nc.ui.er.reimtype.view.ReimTypeEditor;
import nc.ui.pubapp.uif2app.actions.batch.BatchEditAction;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.er.reimtype.ReimTypeVO;


public class retEditAction extends BatchEditAction {

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
		ReimTypeVO extVO = (ReimTypeVO) getModel()
				.getSelectedData();
		boolean flag = true;
		if (extVO != null) {
			flag = ((ReimTypeEditor) getEditor())
					.isShareReimType(extVO);
		}

		return flag;
	}

}
