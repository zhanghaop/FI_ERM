package nc.ui.erm.view;

import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.UserdefitemContainerPreparator;
import nc.ui.uif2.userdefitem.UserDefItemContainer;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

public class ERMUserdefitemContainerPreparator extends UserdefitemContainerPreparator {
	/**
	 * 用户定义属性字段的前缀,默认为defitem，
	 * */
	private String prefix = "defitem";

	private LoginContext loginContext;
	
	private UserDefItemContainer userDefitemContainer;
	
	/**
	 * 非配置文件注入，兼容需要手动调用处理用户定义属性情况
	 * 用于支持resetBillData()
	 */
	private BillForm billForm;
	
	public ERMUserdefitemContainerPreparator() {
		super();
	}

	public ERMUserdefitemContainerPreparator(LoginContext loginContext,BillForm billForm) {
		super();
		this.loginContext = loginContext;
		this.billForm = billForm;
	}
	
	/**
	 * 用户定义属性的前缀不是默认值defitem时调用
	 * 
	 * @param loginContext
	 * @param billForm
	 * @param prefix
	 * @author: wangyhh@ufida.com.cn
	 */
	public ERMUserdefitemContainerPreparator(LoginContext loginContext,BillForm billForm,String prefix) {
		super();
		this.loginContext = loginContext;
		this.billForm = billForm;
		this.prefix = prefix;
	}

	/**
	 * 界面重新加载用户定义属性
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	public void resetBillData(){
		BillData billData = billForm.getBillCardPanel().getBillData();
		prepareBillData(billData);
		billForm.getBillCardPanel().setBillData(billData);
	}
	
	/**
	 * 仅重新设置用户定义属性，没有加载到页面
	 * 
	 * @param bld
	 * @author: wangyhh@ufida.com.cn
	 */
	@Override
	public void prepareBillData(BillData bld) {
		UserDefItemContainer container = getUserDefitemContainer();
		if(container == null){
			container = new UserDefItemContainer();
		}
		container.setContext(loginContext);
		BillTempletVO billTempletVO = bld.getBillTempletVO();
		container.setParams(UserdefitemContainerUtil.getQueryParams(billTempletVO));
		
		setContainer(container);
		setParams(UserdefitemContainerUtil.getUserdefQueryParams(billTempletVO,prefix));
		super.prepareBillData(bld);
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

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	public UserDefItemContainer getUserDefitemContainer() {
		return userDefitemContainer;
	}

	public void setUserDefitemContainer(UserDefItemContainer userDefitemContainer) {
		this.userDefitemContainer = userDefitemContainer;
	}
}
