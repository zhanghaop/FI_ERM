package nc.ui.erm.view;

import nc.ui.pub.bill.BillData;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.UserdefitemContainerPreparator;
import nc.ui.uif2.userdefitem.UserDefItemContainer;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

public class ERMUserdefitemContainerPreparator extends UserdefitemContainerPreparator {
	/**
	 * �û����������ֶε�ǰ׺,Ĭ��Ϊdefitem��
	 * */
	private String prefix = "defitem";

	private LoginContext loginContext;
	
	private UserDefItemContainer userDefitemContainer;
	
	/**
	 * �������ļ�ע�룬������Ҫ�ֶ����ô����û������������
	 * ����֧��resetBillData()
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
	 * �û��������Ե�ǰ׺����Ĭ��ֵdefitemʱ����
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
	 * �������¼����û���������
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	public void resetBillData(){
		BillData billData = billForm.getBillCardPanel().getBillData();
		prepareBillData(billData);
		billForm.getBillCardPanel().setBillData(billData);
	}
	
	/**
	 * �����������û��������ԣ�û�м��ص�ҳ��
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
