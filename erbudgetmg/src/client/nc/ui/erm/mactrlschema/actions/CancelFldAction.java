package nc.ui.erm.mactrlschema.actions;

import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchCancelAction;

public class CancelFldAction extends BatchCancelAction {

	private static final long serialVersionUID = 1L;
	
	public CancelFldAction() {
		// 取消控制维度快捷键：Alt+Q
		String addStr = NCLangRes.getInstance().getStrByID("uif2", "ActionRegistry-000015")/*取消*/;
		ActionInitializer.initializeAction(this, IActionCode.CANCEL);
		setBtnName(addStr);
		setCode(IActionCode.CANCEL); 
		putValue(Action.SHORT_DESCRIPTION, addStr + "(Alt+Q)");
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke((int)'Q', KeyEvent.ALT_MASK));
	}

}
