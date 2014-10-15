package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchDelLineAction;
import nc.ui.uif2.model.HierachicalDataAppModel;

public class DelLineFldAction extends BatchDelLineAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	
	public DelLineFldAction() {
		String str = NCLangRes.getInstance().getStrByID("uif2", "BatchDelLineAction-000000")/*É¾³ý*/;
		ActionInitializer.initializeAction(this, IActionCode.DELLINE);
		setBtnName(str);
		putValue(Action.SHORT_DESCRIPTION, str + "(Alt+D)");
		setCode(IActionCode.DELETE);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke((int)'D', KeyEvent.ALT_MASK));
	}

	@Override
	protected boolean isActionEnable() {
		return super.isActionEnable() && (getModel().getUiState() != UIState.DISABLE);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
//		MaCtrlSchemaChecker.checkOperation(getTreeModel());
		super.doAction(e);
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}
}
