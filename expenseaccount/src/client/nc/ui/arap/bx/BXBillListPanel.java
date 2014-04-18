package nc.ui.arap.bx;

import nc.ui.pubapp.billref.src.view.RefListPanel;

/**
 * @author twei
 * 
 * ���������б����
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
	 * �����б�����ͷ�ͱ���ļ���
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
