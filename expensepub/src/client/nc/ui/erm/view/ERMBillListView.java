package nc.ui.erm.view;

import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListData;
import nc.ui.pub.bill.UITabbedPaneUI;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillListView;
import nc.vo.pub.bill.BillTempletVO;

public class ERMBillListView extends BillListView implements
		ITabbedPaneAwareComponent, IAutoShowUpComponent {

	private static final long serialVersionUID = 1L;

	// 单页签时，是否显示页签名。默认为true。
	private boolean tabSingleShow = true;

	private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareComponent = null;
	private AutoShowUpEventSource autoShowComponent = null;

	/**
	 * 超链接监听器
	 */
	private ERMHyperLinkListener linklistener = null;

	public ERMBillListView() {
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
		autoShowComponent = new AutoShowUpEventSource(this);
	}

	@Override
	public void initUI() {
		super.initUI();
		// 单页签时，是否显示页签名。默认为true。
		// 需要判断页签的字段是否全都显示
		String[] bodyTableCodes = getBillListPanel().getBillListData().getBodyTableCodes();

		if (bodyTableCodes != null) {
			boolean isshow = false;
			for (String tableCodes : bodyTableCodes) {
				BillItem[] bodyShowItems = getBillListPanel().getBillListData().getBodyShowItems(tableCodes);
				if (bodyShowItems != null && bodyShowItems.length > 0) {
					isshow = true;
				} else {
					continue;
				}
			}
			this.tabSingleShow = isshow;

			this.getBillListPanel().getBodyTabbedPane().setUI(new UITabbedPaneUI(this.tabSingleShow));
		}
	}

	protected void processBillInfo(BillTempletVO template) {
		
		processTemplateVO(template);
		BillListData bld = new BillListData(template, getBillStatus());
		processErmBillListData(bld);
		if(getUserdefitemListPreparator() != null)
			getUserdefitemListPreparator().prepareBillListData(bld);
		processBillListData(bld);
		billListPanel.setListData(bld);
	}
	/**
	 * 处理用户自定义属性前，扩展BillData
	 * 
	 * @param bld
	 */
	protected void processErmBillListData(BillListData bld) {}

	public boolean isTabSingleShow() {
		return this.tabSingleShow;
	}

	public void setTabSingleShow(boolean tabSingleShow) {
		this.tabSingleShow = tabSingleShow;
	}

	@Override
	public void addTabbedPaneAwareComponentListener(
			ITabbedPaneAwareComponentListener l) {
		this.tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	@Override
	public boolean canBeHidden() {
		return true;
	}

	@Override
	public boolean isComponentVisible() {
		return this.tabbedPaneAwareComponent.isComponentVisible();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		this.tabbedPaneAwareComponent.setComponentVisible(visible);

	}

	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		// 注册监听器
		this.autoShowComponent.setAutoShowUpEventListener(l);
	}

	@Override
	public void showMeUp() {
		this.autoShowComponent.showMeUp();
	}

	public AutoShowUpEventSource getAutoShowComponent() {
		return this.autoShowComponent;
	}

	public void setAutoShowComponent(AutoShowUpEventSource autoShowComponent) {
		this.autoShowComponent = autoShowComponent;
	}

	public ERMHyperLinkListener getLinklistener() {
		return linklistener;
	}

	public void setLinklistener(ERMHyperLinkListener linklistener) {
		this.linklistener = linklistener;
	}
}
