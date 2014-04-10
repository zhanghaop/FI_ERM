package nc.ui.arap.bx.listeners;

import java.util.EventObject;

import nc.ui.arap.bx.actions.BXDefaultAction;
import nc.ui.arap.eventagent.EventTypeConst;
import nc.ui.pub.bill.BillEditEvent;

/**
 * nc.ui.arap.bx.listeners.BxCardBodyEditListener
 *
 * @author twei
 *
 */
public class BxCardRowChangeListener extends BXDefaultAction {

	public void rowChange() {

		EventObject event = (EventObject) getMainPanel().getAttribute(EventTypeConst.TEMPLATE_EDIT_EVENT);

		if (event instanceof BillEditEvent) {
			
			getMainPanel().getBodyUIController().afterCardPanelEdit(event);
			
		}

	}

}
