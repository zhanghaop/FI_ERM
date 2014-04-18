package nc.ui.erm.view;

import nc.ui.pub.bill.BillListData;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.editor.UserdefitemContainerListPreparator;
import nc.ui.uif2.userdefitem.UserDefItemContainer;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

public class ERMUserdefitemContainerListPreparator extends UserdefitemContainerListPreparator {

	/**
	 * 用户定义属性的前缀,默认为defitem
	 */
	private String prefix = "defitem";

	private LoginContext loginContext;
	
	private UserDefItemContainer userDefitemContainer;
	
	/**
	 * 兼容需要手动调用处理用户定义属性情况
	 * 用于支持resetBillData()
	 */
	private BillListView billListview;
	

	public ERMUserdefitemContainerListPreparator() {
		super();
	}
	
	public ERMUserdefitemContainerListPreparator(LoginContext loginContext,BillListView billListview) {
		super();
		this.loginContext = loginContext;
		this.billListview = billListview;
	}
	
	/**
	 * 用户定义属性的前缀不是默认值defitem时调用
	 * 
	 * @param loginContext
	 * @param billCard
	 * @param prefix
	 * @author: wangyhh@ufida.com.cn
	 */
	public ERMUserdefitemContainerListPreparator(LoginContext loginContext,BillListView billListview,String prefix) {
		super();
		this.loginContext = loginContext;
		this.billListview = billListview;
		this.prefix = prefix;
	}

	/**
	 * 界面重新加载用户定义属性
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	public void resetBillData(){
		BillListData billData = billListview.getBillListPanel().getBillListData();
		prepareBillListData(billData);
		billListview.getBillListPanel().setListData(billData);
	}
	
	/**
	 * 仅重新设置用户定义属性，没有加载到页面
	 * 
	 * @param bld
	 * @author: wangyhh@ufida.com.cn
	 */
	@Override
	public void prepareBillListData(BillListData bld) {
		UserDefItemContainer container = getUserDefitemContainer();
		if (container == null) {
			container = new UserDefItemContainer();
		}

		container.setContext(loginContext);
		BillTempletVO billTempletVO = bld.getBillTempletVO();
		container.setParams(UserdefitemContainerUtil.getQueryParams(billTempletVO));

		setContainer(container);
		setParams(UserdefitemContainerUtil.getUserdefQueryParams(billTempletVO, prefix));
		super.prepareBillListData(bld);
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public BillListView getBillListview() {
		return billListview;
	}

	public void setBillListview(BillListView billListview) {
		this.billListview = billListview;
	}

	public UserDefItemContainer getUserDefitemContainer() {
		return userDefitemContainer;
	}

	public void setUserDefitemContainer(UserDefItemContainer userDefitemContainer) {
		this.userDefitemContainer = userDefitemContainer;
	}
}
