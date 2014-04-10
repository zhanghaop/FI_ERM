package nc.ui.arap.bx.listeners;

import nc.bs.logging.Logger;
import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.bx.actions.CardAction;
import nc.ui.er.pub.BillWorkPageConst;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.ui.arap.bx.listeners.BxListMouseListener
 */
public class BxListMouseListener extends BXDefaultAction {
	
	public void click2() {

		try {
			CardAction action = new CardAction();
			action.setActionRunntimeV0(this.getActionRunntimeV0());
			action.changeTab(BillWorkPageConst.CARDPAGE, true, false,getCurrentSelectedVO());
			getMainPanel().refreshBtnStatus();
		} catch (BusinessException ex) {
			ExceptionHandler.consume(ex);
		}

	}


}
