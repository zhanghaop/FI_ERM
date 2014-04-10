package nc.ui.erm.matterapp.view;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMHyperLinkListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillListView;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;

public class MatterAppMNListView extends BillListView implements ITabbedPaneAwareComponent, IAutoShowUpComponent ,BillTableMouseListener{
	private static final long serialVersionUID = 1L;
	private IAutoShowUpComponent autoShowUpComponent;
	private ITabbedPaneAwareComponent tabbedPaneAwareComponent;
	
	/**
	 * 超链接监听器
	 */
	private ERMHyperLinkListener linklistener = null;
	
	public MatterAppMNListView(){
		super();
	}
	
	@Override
	public void initUI() {
		super.initUI();
	        
		billListPanel.addMouseListener(new BillTableMouseListener() {
			@Override
			public void mouse_doubleclick(BillMouseEnent e) {
				if(e.getPos() == BillItem.HEAD)
					onHeadMouseDBClick(e);
				else if(e.getPos() == BillItem.BODY)
					onBodyMouseDBClick(e);
				
			}
		});
		/**
		 * 单据编号增加超链接
		 */
		BillItem item = billListPanel.getHeadItem(MatterAppVO.BILLNO);
	    item.addBillItemHyperlinkListener(getLinklistener());
	    
		autoShowUpComponent = new AutoShowUpEventSource(this);
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
		
		MatterAppUiUtil.addDigitListenerToListpanel(this.getBillListPanel());
		
		// 设置交易类型名称、事由的显示
		resetSpecialItemCellRender();
		
	}

	/**
	 * 设置交易类型名称
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	@SuppressWarnings("serial")
	private void resetSpecialItemCellRender() {
		try {
			String name = getBillListPanel().getBillListData().getHeadItem(MatterAppVO.PK_TRADETYPE).getName();
			getBillListPanel().getHeadTable().getColumn(name).setCellRenderer(new BillTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if(data != null){
						if(row < data.size()){
							AggMatterAppVO vo = (AggMatterAppVO) data.get(row);
							if(vo != null){
								setValue(ErUiUtil.getDjlxNameMultiLang((vo.getParentVO().getPk_tradetype())));
							}
						}
					}
					return this;
				}
			});
			
			String reasonName = getBillListPanel().getBillListData().getHeadItem(MatterAppVO.REASON).getName();
			
			getBillListPanel().getHeadTable().getColumn(reasonName).setCellRenderer(new BillTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if(data != null){
						if(row < data.size()){
							AggMatterAppVO vo = (AggMatterAppVO) data.get(row);
							if(vo != null){
								setValue((String)vo.getParentVO().getReason());
							}
						}
					}
					return this;
				}
			});
		} catch (IllegalArgumentException e) {
			ExceptionHandler.consume(e);
		}
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

	public ERMHyperLinkListener getLinklistener() {
		return linklistener;
	}

	public void setLinklistener(ERMHyperLinkListener linklistener) {
		this.linklistener = linklistener;
	}
	
}
