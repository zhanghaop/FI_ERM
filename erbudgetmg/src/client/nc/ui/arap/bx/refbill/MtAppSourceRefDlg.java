package nc.ui.arap.bx.refbill;

import java.awt.Container;

import nc.ui.pub.pf.BillSourceVar;
import nc.ui.pubapp.billref.src.view.SourceRefDlg;

@SuppressWarnings("restriction")
public class MtAppSourceRefDlg extends SourceRefDlg {

	private static final long serialVersionUID = 1L;

	public MtAppSourceRefDlg(Container parent, BillSourceVar bsVar) {
		super(parent, bsVar);
	}

	@Override
	public String getRefBillInfoBeanPath() {
		return "nc/ui/arap/bx/refbill/matterrefinfo.xml";
	}
	
	@Override
	public void addBillUI() {
		this.getRefContext().getRefInfo().setBillNodeKey("mtTO35");
		this.getRefContext().getRefInfo().setBillViewNodeKey("mtTO35_L");
		((MatterRefPanelInit)this.getRefContext().getRefInfo().getRefPanelInit()).setContext(this.getRefContext());
		super.addBillUI();
	}

}
