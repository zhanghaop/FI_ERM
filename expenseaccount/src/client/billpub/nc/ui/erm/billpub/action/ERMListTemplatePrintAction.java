package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.pub.print.PrintEntry;
import nc.ui.uif2.actions.TemplatePrintAction;

/** 
 * 打印列表
 * <b>Date:</b>2012-12-14<br>
 * @author：wangyhh@ufida.com.cn
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
	/**
	 * 每次打印都要预览
	 */
	@Override
	public void doAction(ActionEvent e) throws Exception {
		PrintEntry entry = createPrintentry();
		if(entry.selectTemplate() == 1)
			entry.preview();
	}
}
