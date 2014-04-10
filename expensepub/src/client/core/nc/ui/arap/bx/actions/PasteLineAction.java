package nc.ui.arap.bx.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pub.BusinessException;

/**
 * TODO 实现表体粘贴行操作
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-8-12 下午04:49:10
 */
public class PasteLineAction extends RowAction {

	public PasteLineAction() {
		this.setCode(IActionCode.PASTELINE);

	    NCAction action = new NCAction() {
	      private static final long serialVersionUID = 1L;

	      @Override
	      public void doAction(ActionEvent e) throws Exception {
	        // do nothing
	      }
	    };
	    ActionInitializer.initializeAction(action, IActionCode.PASTELINE);
	    this.putActionValue(action);
	}
	@Override
	public void actionPerformed(ActionEvent e) {		
			try {
				pasteLine();
			} catch (BusinessException e1) {
				getMainPanel().handleException(e1);
			}		
	}
}
