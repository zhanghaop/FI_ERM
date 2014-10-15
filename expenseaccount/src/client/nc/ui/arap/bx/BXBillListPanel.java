package nc.ui.arap.bx;

import nc.ui.pubapp.billref.src.view.RefListPanel;

/**
 * @author twei
 * 
 * 报销单据列表面板
 * 
 * nc.ui.arap.bx.BXBillListPanel
 */
@SuppressWarnings("restriction")
public class BXBillListPanel extends RefListPanel {

	private static final long serialVersionUID = -5616632058617552629L;
	
	public 	BXBillListPanel(){
		super();
	}
	/**
	 * 设置列表界面表头和表体的监听
	 */
	public void setHeadAndBodyRowStateListener(){
		this.getHeadTable().getSelectionModel().addListSelectionListener(
				getBillRowManager());
		this.getBodyTable().getSelectionModel().addListSelectionListener(
				getBillRowManager());
		this.getHeadBillModel().addRowStateChangeEventListener(
				getBillRowManager());
		this.getBodyBillModel().addRowStateChangeEventListener(
				getBillRowManager());
	}
	
	
	


}
