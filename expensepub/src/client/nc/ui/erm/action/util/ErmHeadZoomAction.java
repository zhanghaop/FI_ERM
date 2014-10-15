package nc.ui.erm.action.util;

import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction;

@SuppressWarnings("restriction")
public class ErmHeadZoomAction extends DefaultHeadZoomAction {
	private static final long serialVersionUID = 1L;

	public ErmHeadZoomAction() {
		setPos(IBillItem.HEAD);
	}

}
