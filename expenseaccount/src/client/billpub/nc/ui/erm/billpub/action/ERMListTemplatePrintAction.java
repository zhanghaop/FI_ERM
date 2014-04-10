package nc.ui.erm.billpub.action;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.uif2.actions.TemplatePrintAction;

/** 
 * ¥Ú”°¡–±Ì
 * <b>Date:</b>2012-12-14<br>
 * @author£∫wangyhh@ufida.com.cn
 * @version $Revision$
 */ 
public class ERMListTemplatePrintAction extends TemplatePrintAction {

	private static final long serialVersionUID = 1L;

	public ERMListTemplatePrintAction() {
		super();
		setCode(ErmActionConst.PRINTLIST);
		setBtnName(ErmActionConst.getPrintListName());
		putValue(Action.ACCELERATOR_KEY, null);
		putValue(SHORT_DESCRIPTION, ErmActionConst.getPrintListName());
	}

}
