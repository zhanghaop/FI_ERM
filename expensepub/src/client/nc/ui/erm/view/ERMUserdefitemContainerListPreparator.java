package nc.ui.erm.view;

import nc.ui.pub.bill.BillListData;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.editor.UserdefitemContainerListPreparator;
import nc.ui.uif2.userdefitem.UserDefItemContainer;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.uif2.LoginContext;

public class ERMUserdefitemContainerListPreparator extends UserdefitemContainerListPreparator {

	/**
	 * �û��������Ե�ǰ׺,Ĭ��Ϊdefitem
	 */
	private String prefix = "defitem";

	private LoginContext loginContext;
	
	private UserDefItemContainer userDefitemContainer;
	
	/**
	 * ������Ҫ�ֶ����ô����û������������
	 * ����֧��resetBillData()
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
	 * �û��������Ե�ǰ׺����Ĭ��ֵdefitemʱ����
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
	 * �������¼����û���������
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	public void resetBillData(){
		BillListData billData = billListview.getBillListPanel().getBillListData();
		prepareBillListData(billData);
		billListview.getBillListPanel().setListData(billData);
	}
	
	/**
	 * �����������û��������ԣ�û�м��ص�ҳ��
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
