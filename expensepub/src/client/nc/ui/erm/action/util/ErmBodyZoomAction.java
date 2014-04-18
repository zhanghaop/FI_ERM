package nc.ui.erm.action.util;

import nc.ui.pub.bill.IBillItem;
import nc.ui.pubapp.uif2app.actions.DefaultHeadZoomAction;

@SuppressWarnings("restriction")
public class ErmBodyZoomAction extends DefaultHeadZoomAction{
	private static final long serialVersionUID = 1L;
	public ErmBodyZoomAction() {
		setPos(IBillItem.BODY);
	}

}
