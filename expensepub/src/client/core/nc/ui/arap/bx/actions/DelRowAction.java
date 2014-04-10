package nc.ui.arap.bx.actions;

import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.vo.pub.BusinessException;

/**
 * TODO ʵ�ֱ���ɾ�в���
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-8-12 ����04:49:10
 */
public class DelRowAction extends RowAction{

	public DelRowAction() {
		this.setCode(IActionCode.DELLINE);

	    NCAction action = new NCAction() {
	      private static final long serialVersionUID = 1L;

	      @Override
	      public void doAction(ActionEvent e) throws Exception {
	        // do nothing
	      }
	    };
	    ActionInitializer.initializeAction(action, IActionCode.DELLINE);
	    this.putActionValue(action);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {		
			try {
				delRow();
			} catch (BusinessException e1) {
				getMainPanel().handleException(e1);
			}		
	}

}
