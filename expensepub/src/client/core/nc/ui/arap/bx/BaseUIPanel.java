package nc.ui.arap.bx;

import nc.ui.pub.ButtonObject;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;

/**
 * @author twei
 * �����൥����ڽڵ����.
 * 
 * �̳�AbstractRuntime���ṩĬ�ϵ��������û���ʵ��
 * @see AbstractRuntime
 * 
 * nc.ui.arap.bx.BaseUIPanel
 */
public abstract class BaseUIPanel extends BxActionRuntime{

	private static final long serialVersionUID = 6472446075997697939L;
	
	/**
	 * ���ݻ���
	 */
	private VOCache voCache=null;
	
	/**
	 * ����UI Controller
	 */
	protected IBodyUIController bodyUIController;
	
	/**
	 * @return ��Ƭ���
	 */
	public abstract BillCardPanel getBillCardPanel();
	
	/**
	 * @return �б����
	 */
	public abstract BillListPanel getBillListPanel();
	
	
	/**
	 * @param tableCode
	 * @return �Ƿ�ģ��չʾ��ʽ. 
	 */
	public abstract boolean isTempletView(String tableCode);
	
	/**
	* Ĭ�Ͻ����ʼ������.
	 */
	protected void initialize() {

		ButtonObject[] btArray = getDjButtons();
		setButtons(btArray);
		setName("MainPanel");
		setAutoscrolls(true);
		setLayout(new java.awt.CardLayout());
		setSize(774, 419);
		add(getBillListPanel(), getBillListPanel().getName());
		add(getBillCardPanel(), getBillCardPanel().getName());
	}
	
	
	public VOCache getCache() {
		if(voCache==null){
			voCache=new VOCache();
		}
		return voCache;
	}

	public IBodyUIController getBodyUIController() {
		return bodyUIController;
	}
	public void setBodyUIController(IBodyUIController bodyUIController) {
		this.bodyUIController = bodyUIController;
	}

	
	


}
