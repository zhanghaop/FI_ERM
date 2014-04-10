package nc.ui.arap.bx;

import nc.ui.pub.ButtonObject;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillListPanel;

/**
 * @author twei
 * 借款报销类单据入口节点基类.
 * 
 * 继承AbstractRuntime，提供默认的流程配置环境实现
 * @see AbstractRuntime
 * 
 * nc.ui.arap.bx.BaseUIPanel
 */
public abstract class BaseUIPanel extends BxActionRuntime{

	private static final long serialVersionUID = 6472446075997697939L;
	
	/**
	 * 单据缓存
	 */
	private VOCache voCache=null;
	
	/**
	 * 表体UI Controller
	 */
	protected IBodyUIController bodyUIController;
	
	/**
	 * @return 卡片面板
	 */
	public abstract BillCardPanel getBillCardPanel();
	
	/**
	 * @return 列表面板
	 */
	public abstract BillListPanel getBillListPanel();
	
	
	/**
	 * @param tableCode
	 * @return 是否模板展示方式. 
	 */
	public abstract boolean isTempletView(String tableCode);
	
	/**
	* 默认界面初始化方法.
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
