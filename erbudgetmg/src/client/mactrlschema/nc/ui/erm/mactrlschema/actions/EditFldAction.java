package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.AbstractShowMsgExceptionHandler;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchEditAction;
import nc.ui.uif2.model.HierachicalDataAppModel;

public class EditFldAction extends BatchEditAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	
	public EditFldAction() {
		String editStr = NCLangRes.getInstance().getStrByID("uif2", "ActionRegistry-000005")/*修改*/;
		ActionInitializer.initializeAction(this, IActionCode.EDIT);
		putValue(Action.SHORT_DESCRIPTION, editStr + "(Altl+E)");
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.ALT_MASK));
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
//		MaCtrlSchemaChecker.checkOperation(getTreeModel());
		super.doAction(e);
	}

	/**
	 * 调整action的错误提示信息
	 */
	protected void processExceptionHandler(Exception ex) {
		if (!(exceptionHandler instanceof AbstractShowMsgExceptionHandler))
			exceptionHandler.handlerExeption(ex);
		else {
			AbstractShowMsgExceptionHandler dhandler = (AbstractShowMsgExceptionHandler) exceptionHandler;
			dhandler.setErrormsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
					getStrByID("upp2012v575_0","0upp2012V575-0124")/*@res ""修改失败""*/);
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
