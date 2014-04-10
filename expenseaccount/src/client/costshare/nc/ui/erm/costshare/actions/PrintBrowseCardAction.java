package nc.ui.erm.costshare.actions;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.actions.ActionInitializer;

/**
 * @author luolch
 * 
 *         ¥Ú”°‘§¿¿
 * 
 */
@SuppressWarnings("serial")
public class PrintBrowseCardAction extends CsPrintAction {
	public PrintBrowseCardAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.PREVIEW);
	}
}