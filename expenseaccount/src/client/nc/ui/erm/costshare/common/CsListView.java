package nc.ui.erm.costshare.common;

import nc.ui.erm.view.ERMHyperLinkListener;
import nc.ui.pub.bill.UITabbedPaneUI;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillListView;

public class CsListView extends BillListView implements ITabbedPaneAwareComponent, IAutoShowUpComponent {

	private static final long serialVersionUID = 1L;
	private IAutoShowUpComponent autoShowUpComponent;
	private boolean isShowTab=false;

	private ITabbedPaneAwareComponent tabbedPaneAwareComponent;

	public CsListView() {
		super();
		autoShowUpComponent = new AutoShowUpEventSource(this);
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
	}
	
	/**
	 * 超链接监听器
	 */
	private ERMHyperLinkListener linklistener = null;
	
	public void initUI() {
		super.initUI();
		//单页签时，是否显示页签名。默认为false。
		getBillListPanel().getBodyTabbedPane().setUI(new UITabbedPaneUI(isShowTab));
	}
	
	@Override
	public void setBillListData() {
		super.setBillListData();
	}
	

	public void addTabbedPaneAwareComponentListener(ITabbedPaneAwareComponentListener l) {
		tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	public boolean canBeHidden() {
		return true;
	}

	public boolean isComponentVisible() {
		return tabbedPaneAwareComponent.isComponentVisible();
	}

	public void setComponentVisible(boolean visible) {
		tabbedPaneAwareComponent.setComponentVisible(visible);
	}

	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		autoShowUpComponent.setAutoShowUpEventListener(l);
	}

	public void showMeUp() {
		autoShowUpComponent.showMeUp();
	}

	public void setShowTab(boolean isShowTab) {
		this.isShowTab = isShowTab;
	}

	public boolean isShowTab() {
		return isShowTab;
	}

	public ERMHyperLinkListener getLinklistener() {
		return linklistener;
	}

	public void setLinklistener(ERMHyperLinkListener linklistener) {
		this.linklistener = linklistener;
	}
	
	
}
