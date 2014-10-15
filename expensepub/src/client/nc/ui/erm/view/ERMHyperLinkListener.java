package nc.ui.erm.view;

import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;

public class ERMHyperLinkListener implements BillItemHyperlinkListener{

    private BillManageModel model = null;
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	@Override
	public void hyperlink(BillItemHyperlinkEvent event) {
		model.fireEvent(new AppEvent(AppEventConst.SHOW_EDITOR));
	}

}
