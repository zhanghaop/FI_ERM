package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.bd.psnbankacc.IPsnBankaccPubService;
import nc.pubitf.uapbd.ICustomerPubService;
import nc.pubitf.uapbd.ISupplierPubService_C;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.model.BankaccSubDefaultRefModel;
import nc.ui.bd.ref.model.CashAccountRefModel;
import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.bd.ref.model.PsnbankaccDefaultRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.remote.UserBankAccVoCall;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.vorg.ref.DeptVersionDefaultRefModel;
import nc.ui.vorg.ref.FinanceOrgVersionDefaultRefTreeModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.bankaccount.BankAccSubVO;
import nc.vo.bd.bankaccount.BankAccbasVO;
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.bd.cashaccount.CashAccountVO;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.resa.costcenter.CostCenterVO;
/**
 * ��ͷ�ֶα༭�󣬴�����Ӧ�ֶι��˵Ĺ�����
 * @author wangled
 *
 */
public class HeadFieldHandleUtil {
	
	private ErmBillBillForm editor = null;
	public HeadFieldHandleUtil(ErmBillBillForm editor) {
		super();
		this.editor = editor;
	}
	
	/**
	 * ��Ŀ�ֶδ���
	 */
	public void initProj() {
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.JOBID);
		String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		if(pk_org == null){
			refPane.setEnabled(false);
		}else{
			refPane.setEnabled(true);
		}
		refPane.getRefModel().setPk_org(pk_org);
	}
	
	/**
	 * ���ݹ�Ӧ�̴���ɢ���ֶ�
	 * 
	 * @param key
	 */
	public void initFreeCustBySupplier() {
		// ɢ��
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.FREECUST);
		// ��Ӧ��
		final String pk_supplier = getHeadItemStrValue(JKBXHeaderVO.HBBM);

		if (pk_supplier != null && pk_supplier.trim().length() > 0) {
			// ɢ�����ù�Ӧ��
			try {
				SupplierVO[] supplierVO = NCLocator.getInstance().lookup(ISupplierPubService_C.class).getSupplierVO(new String[] { pk_supplier }, new String[] { SupplierVO.ISFREECUST });
				if (supplierVO != null && supplierVO.length != 0 && supplierVO[0].getIsfreecust().equals(UFBoolean.TRUE)) {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(true);
					((FreeCustRefModel) refPane.getRefModel()).setCustomSupplier(pk_supplier);
				} else {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(false);
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(false);
		}
	}
	

	/**
	 * ���ݿͻ�����ɢ���ֶ�
	 */
	public void initFreeCustByCustomer() {

		// ɢ��
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.FREECUST);

		final String customer = getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);

		if (customer != null && customer.trim().length() > 0) {
			try {

				CustomerVO[] customerVO = NCLocator.getInstance().lookup(ICustomerPubService.class).getCustomerVO(
						new String[] { customer }, new String[] { CustomerVO.ISFREECUST });
				if (customerVO != null && customerVO.length != 0
						&& customerVO[0].getIsfreecust().equals(UFBoolean.TRUE)) {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(true);
					((FreeCustRefModel) refPane.getRefModel()).setCustomSupplier(customer);
				} else {
					getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(false);
				}
			} catch (BusinessException e) {
				ExceptionHandler.handleExceptionRuntime(e);
			}
		} else {
			getBillCardPanel().getHeadItem(JKBXHeaderVO.FREECUST).setEnabled(false);
		}

	}
	
	/**
	 * �ʽ�ƻ���Ŀ�ֶδ��� </br>����֧����֯���й���
	 */
	public void initCashProj() {
		UIRefPane ref = getHeadItemUIRefPane(JKBXHeaderVO.CASHPROJ);
		String pk_org = null;
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG);
		if (headItem == null) {
			pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
		} else {
			pk_org = (String) headItem.getValueObject();
		}
		ref.getRefModel().setPk_org(pk_org);
		ref.getRefModel().addWherePart(" and inoutdirect = '1' ", false);
	}
	
	/**
	 * ����Ҫ�أ������������Ĺ���
	 * @param itemKey
	 * @return
	 */
	public void initPk_Checkele(){
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.PK_CHECKELE);
		String pk_pcorg = getHeadItemStrValue(JKBXHeaderVO.PK_PCORG);
		setPkOrg2RefModel(refPane, pk_pcorg);
	}
	
	/**
	 * @author wangle 
	 * ����汾��֯����
	 */
	public void beforeEditPkOrg_v(String vOrgField) {
		//����������޸Ľ�����λ
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (editor.isInit()) {//�ڳ�����
				date=new UFDate("3000-01-01");
		} else {
			if(date == null || StringUtil.isEmpty(date.toString())){
				//��������Ϊ�գ�ȥҵ������
				date = BXUiUtil.getBusiDate();
			}
		}
		UIRefPane refPane = getHeadItemUIRefPane(vOrgField);
		FinanceOrgVersionDefaultRefTreeModel model = (FinanceOrgVersionDefaultRefTreeModel) refPane.getRefModel();
		model.setVstartdate(date);
		//���ڶ��ڳ��õ�������֯��ҲҪ������Ȩ��
		//if((!getBxParam().isInit()) && JKBXHeaderVO.PK_ORG_V.equals(vOrgField)){
			//������֯�ֶθ��ݹ���Ȩ�޹���
			String refPK = refPane.getRefPK();
			String[] pk_vids = ErUiUtil.getPermissionOrgVs(editor.getModel().getContext(),date);
			if(pk_vids==null ){
				pk_vids = new String[0];
			}
        // refPane.getRefModel().setFilterPks(pk_vids);
        ErUiUtil.setRefFilterPks(refPane.getRefModel(), pk_vids);
			List<String> list = Arrays.asList(pk_vids);
			if(list.contains(refPK)){
				refPane.setPK(refPK);
			}else{
				refPane.setPK(null);
			}
		//}
	}
	
	/**
	 * ��汾���Ź���
	 * @param evt
	 * @param vDeptField
	 */
	public void beforeEditDept_v(final String pk_org, final String vDeptField){
		UIRefPane refPane = getHeadItemUIRefPane(vDeptField);
		DeptVersionDefaultRefModel model = (DeptVersionDefaultRefModel) refPane.getRefModel();
		UFDate date = (UFDate) getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		if (date == null) {
			// ��������Ϊ�գ�ȥҵ������
			date = BXUiUtil.getBusiDate();
		}
		model.setVstartdate(date);
		model.setPk_org(pk_org);
	}
	
	
	/**
	 * �����˱༭ǰ�¼�
	 */
	public void initJkbxr() {
		// ���ڳ�����������Ȩ������
		if (!(editor).isInit()) {
			try {
				BillItem headItem = editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.JKBXR);
				initSqdlr(editor, headItem, ((ErmBillBillManageModel) editor.getModel()).getCurrentBillTypeCode(), getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM));
			} catch (BusinessException e) {
				Log.getInstance(getClass()).error(e);
			}
		}
	}

	/**
	 * ���˽����� ���⴦��
	 * 
	 * @param panel
	 * @param headItem
	 * @param billtype
	 * @param headOrg
	 * @throws BusinessException
	 */
	public static void initSqdlr(ErmBillBillForm editor, BillItem jkbxr, String billtype, BillItem dwbm)
			throws BusinessException {
		UFDate billDate = (UFDate) editor.getBillCardPanel().getHeadItem(JKBXHeaderVO.DJRQ).getValueObject();
		ErUiUtil.initSqdlr(editor, jkbxr, billtype, (String) dwbm.getValueObject(), billDate);
	}
	
	/**
	 * �ɱ����ĸ�����������������
	 * @author wangle
	 * @throws BusinessException
	 */
	public void initResaCostCenter(){
		String pk_pcorg = getHeadItemStrValue(JKBXHeaderVO.PK_PCORG);
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.PK_RESACOSTCENTER);
		
		String wherePart = CostCenterVO.PK_PROFITCENTER+"="+"'"+pk_pcorg+"'"; 
		addWherePart2RefModel(refPane, pk_pcorg, wherePart);
		
		if(pk_pcorg == null){
			refPane.setPK(null);
		}
	}
	
	/**
	 * �տ������ʺŸ��ݽ����˺ͱ��ֱ������
	 * 
	 * @param key
	 */
	public void initSkyhzh() {
		// �տ���
		String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
		if (!isReceiverPaytarget()) {
			receiver = null;
		}

		String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		// �տ����в���
		UIRefPane refpane = getHeadItemUIRefPane(JKBXHeaderVO.SKYHZH);
		StringBuffer wherepart = new StringBuffer();
		wherepart.append(" pk_psndoc='" + receiver + "'");
		wherepart.append(" and pk_currtype='" + pk_currtype + "'");

		// ��ά��Ŀ������տ������˻���������Ա���й��˼���
		PsnbankaccDefaultRefModel psnbankModel = (PsnbankaccDefaultRefModel) refpane.getRefModel();
		psnbankModel.setWherePart(wherepart.toString());
		psnbankModel.setPk_psndoc(receiver);
	}
	
	
	
	/**
	 * @author wangle �տ��˱༭���¼�
	 * @throws BusinessException
	 */
	public void editReceiver() throws BusinessException {
		// �տ���
		String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);

		// �տ��˷������,��ո��������ʺ�
		if (receiver == null) {
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
			editor.getHelper().changeBusItemValue(BXBusItemVO.SKYHZH, null);
		}
		
		//�տ��˱༭��Ҫ�ж��տ�����Ƿ����տ���
		setDefaultSkyhzhByReceiver();
	}
	
	/**
	 * �տ��˸���ʱ������Ĭ�ϸ��������˻�
	 */
	public void setDefaultSkyhzhByReceiver() {
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.RECEIVER);
		String receiver = headItem == null ? null : (String) headItem.getValueObject();
		if(receiver == null){
			return;
		}
		
		if(!isReceiverPaytarget()){//�Ƿ��Ƕ�˽֧��
			return;
		}
		
		initSkyhzh();//��ʼ������
		
		// �Զ������տ������ʺ�
		try {
			String key = UserBankAccVoCall.USERBANKACC_VOCALL + receiver;
			if (WorkbenchEnvironment.getInstance().getClientCache(key) != null) {
				BankAccSubVO[] vos = (BankAccSubVO[]) WorkbenchEnvironment.getInstance().getClientCache(key);
				if (vos != null && vos.length > 0 && vos[0] != null) {
					getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, vos[0].getPk_bankaccsub());
					editor.getHelper().changeBusItemValue(BXBusItemVO.SKYHZH, vos[0].getPk_bankaccsub());
				}
			} else {// ���������˻���Ĭ���˻�
				BankAccbasVO bank = NCLocator.getInstance().lookup(IPsnBankaccPubService.class).queryDefaultBankAccByPsnDoc(receiver);
				if (bank != null && bank.getBankaccsub() != null) {
					WorkbenchEnvironment.getInstance().putClientCache(UserBankAccVoCall.USERBANKACC_VOCALL + receiver, bank.getBankaccsub());
					getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, bank.getBankaccsub()[0].getPk_bankaccsub());
					editor.getHelper().changeBusItemValue(BXBusItemVO.SKYHZH, bank.getBankaccsub()[0].getPk_bankaccsub());
				}
			}
		} catch (Exception e) {
			getBillCardPanel().setHeadItem(JKBXHeaderVO.SKYHZH, null);
		}
	}

	/**
	 * ��Ŀ������ݷ��óе���λ����Ŀ������
	 * wangle
	 */
	public void initProjTask(){
		String pk_project = getHeadItemStrValue(JKBXHeaderVO.JOBID);
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.PROJECTTASK);
		if (pk_project != null) {
			String wherePart = " pk_project=" + "'" + pk_project + "'";
			//��Ŀ����֯(�����Ǽ��ż���)
			final String pkOrg = getHeadItemUIRefPane(JKBXHeaderVO.JOBID).getRefModel().getPk_org();
			String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
			if(BXUiUtil.getPK_group().equals(pkOrg)){
				//���ż���Ŀ
				pk_org = BXUiUtil.getPK_group(); 
			}
			//������Ŀ����
			setWherePart2RefModel(refPane,pk_org, wherePart);
		}else{
			setWherePart2RefModel(refPane,null, "1=0");
		}
	}
	
	/**
	 * ���������ʺŸ��ݱ��ֺ͹�Ӧ�̹���
	 */
	public void initCustAccount() {
		// ���̵���
		String pk_custsup = null;
		int accclass = 0;
		if (isBxBill()) {
			Integer paytarget = (Integer) getHeadValue(JKBXHeaderVO.PAYTARGET);
			if (paytarget.intValue() == BXStatusConst.PAY_TARGET_HBBM) {
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
				accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
			} else if (paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER) {
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}
		} else {
			pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
			accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
			if (StringUtil.isEmptyWithTrim(pk_custsup)) {
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}
		}

		if (isReceiverPaytarget()) {
			pk_custsup = null;
		}

		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.CUSTACCOUNT);
		String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		StringBuffer wherepart = new StringBuffer();
		wherepart.append(" pk_currtype='" + pk_currtype + "'");
		wherepart.append(" and enablestate='" + IPubEnumConst.ENABLESTATE_ENABLE + "'");
		wherepart.append(" and accclass='" + accclass + "'");
		HeadFieldHandleUtil.setWherePart2RefModel(refPane, null, wherepart.toString());
		if (refPane.getRefModel() != null && refPane.getRefModel() instanceof CustBankaccDefaultRefModel) {
			CustBankaccDefaultRefModel refModel = (CustBankaccDefaultRefModel) refPane.getRefModel();
			if (refModel != null) {
				refModel.setPk_cust(pk_custsup);
			}
		}
	}

	/**
	 *��֧��Ŀ���ݷ��óе���λ���� 
	 *wangle
	 */
	public void initSzxm(){
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.SZXMID);
		String pk_org = getHeadItemStrValue(JKBXHeaderVO.FYDWBM);
		refPane.setPk_org(pk_org);
	}
	
	
	/**
	 * �����ֶ�
	 * 
	 */
	public void initZy(){
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.ZY);
		//���ղ��Զ�ƥ��
		refPane.setAutoCheck(false);
	}
	
	/**
	 * ���������ʺŸ��ݱ��ֱ����֧����λ����
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public void initFkyhzh() {
		final String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		BillItem headItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.FKYHZH);
		if (headItem != null) {
			UIRefPane refPane = (UIRefPane) headItem.getComponent();

			BankaccSubDefaultRefModel model = (BankaccSubDefaultRefModel) refPane.getRefModel();
			final String prefix = "pk_currtype = ";
			String pk_org = null;
			BillItem payHeadItem = getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG);
			if (payHeadItem == null) {
				pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject();
			} else {
				pk_org = (String) payHeadItem.getValueObject();
			}

			// ��λ�����ʻ����ܿ���֪ͨ�����������˻�
			if (StringUtil.isEmpty(pk_currtype)) {
				model.setWherePart(" acctype not in ('1','2')");
				model.setPk_org(pk_org);
				return;
			}
			model.setPk_org(pk_org);
			model.setWherePart(prefix + "'" + pk_currtype + "' and acctype not in ('1','2')");
			model.setMatchPkWithWherePart(true);
			model.setPKMatch(true);

			final String refPK = refPane.getRefPK();
			if (refPK != null) {
				Vector vec = model.matchPkData(refPK);
				if (vec == null || vec.isEmpty()) {
					refPane.setPK(null);
				}
			}
		}
	}
	
	/**
	 * �ֽ��ʻ�(���ݱ��ֺ�֧����λ����)
	 * 
	 */
	@SuppressWarnings({ "unchecked" })
	public void initCashAccount() {
		String pk_currtype = getHeadItemStrValue(JKBXHeaderVO.BZBM);
		UIRefPane refPane = (UIRefPane) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_CASHACCOUNT).getComponent();
		nc.ui.bd.ref.model.CashAccountRefModel model = (CashAccountRefModel) refPane.getRefModel();
		final String prefix = CashAccountVO.PK_MONEYTYPE + "=";
		final String pk_org = (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG).getValueObject();
		if (StringUtil.isEmpty(pk_currtype)) {
			model.setWherePart(null);
			model.setPk_org(pk_org);
			return;
		}
		model.setPk_org(pk_org);
		model.setWherePart(prefix + "'" + pk_currtype + "'");

		final String refPK = refPane.getRefPK();
		if (refPK != null) {
			List<String> pkValueList = new ArrayList<String>();
			Vector vct = model.reloadData();
			Iterator<Vector> it = vct.iterator();
			int index = model.getFieldIndex(CashAccountVO.PK_CASHACCOUNT);
			while (it.hasNext()) {
				Vector next = it.next();
				pkValueList.add((String) next.get(index));
			}

			if (!pkValueList.contains(refPK)) {
				refPane.setPK(null);
			}
		}
	}
	
	/**
	 * �������wherepart
	 * 
	 * @param refPane
	 * @param pk_org
	 * @param addwherePart
	 */
	public static void addWherePart2RefModel(UIRefPane refPane, String pk_org, String addwherePart) {
		filterRefModelWithWherePart(refPane, pk_org, null, addwherePart);
	}

	public static void setWherePart2RefModel(UIRefPane refPane, String pk_org, String wherePart) {
		filterRefModelWithWherePart(refPane, pk_org, wherePart, null);
	}

	public static void filterRefModelWithWherePart(UIRefPane refPane, String pk_org, String wherePart,
			String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		if (model != null) {
			model.setPk_org(pk_org);
			model.setWherePart(wherePart);
			if (addWherePart != null) {
				model.setPk_org(pk_org);
				model.addWherePart(" and " + addWherePart);
			}
		}
	}
	
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	/**
	 * @author wangle �Ƿ����൥��
	 * @return
	 */
	protected boolean isJk() {
		DjLXVO currentDjlx = ((ErmBillBillManageModel)editor.getModel()).getCurrentDjLXVO();
		return BXConstans.JK_DJDL.equals(currentDjlx.getDjdl());
	}
	
	public UIRefPane getHeadItemUIRefPane(final String key) {
		JComponent component = getBillCardPanel().getHeadItem(key).getComponent();
		return component instanceof UIRefPane ? (UIRefPane) component : null;
	}
	
	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}
	
	public  void setPkOrg2RefModel(UIRefPane refPane, String pk_org) {
		refPane.getRefModel().setPk_org(pk_org);
	}
	
	/**
	 * �Ƿ�Ը��˽���֧��
	 * 
	 * @return
	 */
	public boolean isReceiverPaytarget() {
		if (isBxBill()) {
			Integer paytarget = (Integer) getHeadValue(JKBXHeaderVO.PAYTARGET);
			if (paytarget == null || paytarget.intValue() == 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return getHeadValue(JKBXHeaderVO.ISCUSUPPLIER) == null || !(Boolean) getHeadValue(JKBXHeaderVO.ISCUSUPPLIER);
		}
	}

	// �Ƿ��Ǳ�����
	public boolean isBxBill() {
		return BXConstans.BX_DJDL.equals(getHeadItemStrValue(JKBXHeaderVO.DJDL));
	}

	public Object getHeadValue(String key) {
		BillItem headItem = editor.getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = editor.getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}
}
