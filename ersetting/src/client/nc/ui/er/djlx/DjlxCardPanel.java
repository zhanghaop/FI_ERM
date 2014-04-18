package nc.ui.er.djlx;

import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.er.djlx.BillTypeVO;

/**
 *
 * nc.ui.er.djlx.DjlxCardPanel
 */
public class DjlxCardPanel extends UIPanel {
	private BillCardPanel m_cardpanel = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5295113304733212772L;
	
	public DjlxCardPanel() {
		super();
		initialize();
		// TODO 自动生成构造函数存根
	}
	private void initialize() {
			// user code begin {1}
			// user code end
			setName("DjlxPanel");
			setLayout(new java.awt.BorderLayout());
			setSize(663, 411);
			add(getBillCardPanelDj(), "Center");
			setEnable(false);
	}
	protected BillCardPanel getBillCardPanelDj() {
				if (m_cardpanel == null) {
						m_cardpanel = new BillCardPanel(); //{
						m_cardpanel.loadTemplet("djlxZ3ertemplet00001");
						//FIXME 注销，按钮
//						inittemplet();
				}
				return m_cardpanel;
			}
	public void setBillValueVO(BillTypeVO billtypevo) {
		// TODO 自动生成方法存根
		getBillCardPanelDj().setBillValueVO(billtypevo);
		
	}
	public void setEnable(boolean bflag){
		getBillCardPanelDj().setEnabled(bflag);
	}
//	private void inittemplet(){
//		getBillCardPanelDj().setAutoExecHeadEditFormula(true);
//		getBillCardPanelDj().getBodyPanel().getPmBody().remove(
//				getBillCardPanelDj().getBodyPanel().getMiAddLine());
//		getBillCardPanelDj().getBodyPanel().getPmBody().remove(
//				getBillCardPanelDj().getBodyPanel().getMiInsertLine());
//		getBillCardPanelDj().getBodyPanel().getPmBody().remove(
//				getBillCardPanelDj().getBodyPanel().getMiPasteLine());
//		getBillCardPanelDj().getBodyPanel().getPmBody().remove(
//				getBillCardPanelDj().getBodyPanel()
//						.getMiPasteLineToTail());
//		getBillCardPanelDj().getBodyPanel().getPmBody().remove(
//				getBillCardPanelDj().getBodyPanel().getMiDelLine());
////		try{
////			((UIRefPane)getBillCardPanelDj().getBillModel().getItemByKey("templetname").getComponent()).setReturnCode(false);
////		}catch(Exception e){
////			Log.getInstance(this.getClass()).error(e.getMessage(),e);
////		}
//	}
	public void setNull(){
		getBillCardPanelDj().setBillValueVO(new BillTypeVO());
	}
	public BillTypeVO getBilltypevo(){
		getBillCardPanelDj().stopEditing();		
		BillTypeVO vo = (BillTypeVO) getBillCardPanelDj().getBillValueVO("nc.vo.er.djlx.BillTypeVO","nc.vo.er.djlx.DjLXVO","nc.vo.er.djlx.DjLXVO");
		return vo;
	}
}
