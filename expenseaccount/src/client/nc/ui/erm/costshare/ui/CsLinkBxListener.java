package nc.ui.erm.costshare.ui;

import nc.ui.erm.costshare.actions.LinkBxAction;
import nc.ui.pub.bill.BillItemHyperlinkEvent;
import nc.ui.pub.bill.BillItemHyperlinkListener;
import nc.ui.uif2.ShowStatusBarMsgUtil;
/**
 * ��Ƭ������鱨����
 * @author luolch 
 *
 */
public class CsLinkBxListener implements BillItemHyperlinkListener{

    private LinkBxAction linkBx = null;
	

	@Override
	public void hyperlink(BillItemHyperlinkEvent event) {
		try {
			linkBx.doAction(null);
		} catch (Exception e) {
			ShowStatusBarMsgUtil.showErrorMsg("error", nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
					getStrByID("common","UPP2011-000136")/*@res "���鱨����ʧ�ܣ�"*/, getLinkBx().getModel().getContext());
		}
	}


	public void setLinkBx(LinkBxAction linkBx) {
		this.linkBx = linkBx;
	}


	public LinkBxAction getLinkBx() {
		return linkBx;
	}

}
