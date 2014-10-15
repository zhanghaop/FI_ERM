package nc.ui.erm.mactrlschema.actions;

import nc.ui.uif2.AbstractShowMsgExceptionHandler;
import nc.ui.uif2.actions.batch.BatchEditAction;
import nc.ui.uif2.model.HierachicalDataAppModel;

public class EditBilAction extends BatchEditAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;

	// @Override
	// public void doAction(ActionEvent e) throws Exception {
	// MaCtrlSchemaChecker.checkOperation(getTreeModel());
	// super.doAction(e);
	// }

	/**
	 * ����action�Ĵ�����ʾ��Ϣ
	 */
	protected void processExceptionHandler(Exception ex) {

		if (!(exceptionHandler instanceof AbstractShowMsgExceptionHandler))
			exceptionHandler.handlerExeption(ex);
		else {
			AbstractShowMsgExceptionHandler dhandler = (AbstractShowMsgExceptionHandler) exceptionHandler;
			dhandler.setErrormsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0124")/* @res ""�޸�ʧ��"" */);
			dhandler.handlerExeption(ex);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}
}
