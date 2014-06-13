package nc.ui.erm.billpub.view.eventhandler;

import javax.swing.JComponent;

import nc.ui.bd.ref.model.CustBankaccDefaultRefModel;
import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.bd.ref.model.PsnbankaccDefaultRefModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.org.ref.DeptDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.bankaccount.IBankAccConstant;
import nc.vo.bd.pub.IPubEnumConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

public class InitBillCardBeforeEditListener implements BillCardBeforeEditListener {

	private BillForm editor;

	public InitBillCardBeforeEditListener(BillForm editor) {
		super();
		this.editor = editor;
	}

	/**
	 * ��������ͷ�༭ǰ�¼�
	 */
	@Override
	public boolean beforeEdit(BillItemEvent e) {
		String key = e.getItem().getKey();
		try {
			if (JKBXHeaderVO.DEPTID_V.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (headItem != null) {
					String dwbm = (String) headItem.getValueObject();
					((ErmBillBillForm) editor).getEventHandle().getHeadFieldHandle().beforeEditDept_v(dwbm, JKBXHeaderVO.DEPTID_V);
				}
			} else if (JKBXHeaderVO.FYDEPTID_V.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (headItem != null) {
					String fydwbm = (String) headItem.getValueObject();
					((ErmBillBillForm) editor).getEventHandle().getHeadFieldHandle().beforeEditDept_v(fydwbm, JKBXHeaderVO.FYDEPTID_V);
				}
			} else if (JKBXHeaderVO.DEPTID.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (headItem != null) {
					String dwbm = (String) headItem.getValueObject();
					beforeEditDept(dwbm, JKBXHeaderVO.DEPTID);
				}
			} else if (JKBXHeaderVO.FYDEPTID.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (headItem != null) {
					String dwbm = (String) headItem.getValueObject();
					beforeEditDept(dwbm, JKBXHeaderVO.FYDEPTID);
				}
			} else if (JKBXHeaderVO.JKBXR.equals(key)) {
				((ErmBillBillForm) editor).getEventHandle().getHeadFieldHandle().initJkbxr();
			} else if (key != null && (key.startsWith(BXConstans.HEAD_USERDEF_PREFIX))) {// �Զ��������
				filterZyxField(key);
			} else if (JKBXHeaderVO.CUSTACCOUNT.equals(key)) {// �ͻ������˺�
				beforeEditCustaccount();
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {// ɢ��
				beforeEditFreecust();
			} else if (JKBXHeaderVO.SKYHZH.equals(key)) {// ���������˻�
				beforeEditSkyhzh();
			} else if(JKBXHeaderVO.JOBID.equals(key)){//��Ŀ
				new HeadFieldHandleUtil((ErmBillBillForm) editor).initProj();
			}

			if (!JKBXHeaderVO.PK_ORG_V.equals(key) && !JKBXHeaderVO.PK_ORG.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(key);
				if (headItem != null && headItem.getComponent() instanceof UIRefPane && ((UIRefPane) headItem.getComponent()).getRefModel() != null) {
					CrossCheckUtil.checkRule("Y", key, editor);
				}
			}
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		return ((ERMBillForm) editor).getEventTransformer().beforeEdit(e);
	}

	private void beforeEditSkyhzh() {
		// �տ���
		String receiver = getHeadItemStrValue(JKBXHeaderVO.RECEIVER);
		if(!isReceiverPaytarget()){
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
	 * ɢ�����ݿ��̵����е�ɢ������������
	 */
	private void beforeEditFreecust() {
		// ɢ��
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.FREECUST);
		// ɢ�����ù�Ӧ��
		((FreeCustRefModel) refPane.getRefModel()).setCustomSupplier(getCustomerSupplier());
	}

	/***
	 * �ͻ������˺Ÿ��ݿͻ�����+����������
	 * 
	 */
	private void beforeEditCustaccount() {
		// ���̵���
		String pk_custsup = null;
		int accclass = 0;
		if(isBxBill()){
			Integer paytarget = (Integer)getHeadValue(JKBXHeaderVO.PAYTARGET);
			if(paytarget.intValue() == BXStatusConst.PAY_TARGET_HBBM){
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
				accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
			}else if(paytarget.intValue() == BXStatusConst.PAY_TARGET_CUSTOMER){
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}
		}else{
			pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
			accclass = IBankAccConstant.ACCCLASS_SUPPLIER;
			if (StringUtil.isEmptyWithTrim(pk_custsup)) {
				pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
				accclass = IBankAccConstant.ACCCLASS_CUST;
			}
		}
		
		if(isReceiverPaytarget()){
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

	private String getCustomerSupplier() {
		String pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.HBBM);
		if (StringUtil.isEmptyWithTrim(pk_custsup)) {
			pk_custsup = (String) getHeadItemStrValue(JKBXHeaderVO.CUSTOMER);
		}
		return pk_custsup;
	}

	private String getHeadItemStrValue(String itemKey) {
		BillItem headItem = editor.getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}

	private UIRefPane getHeadItemUIRefPane(final String key) {
		JComponent component = editor.getBillCardPanel().getHeadItem(key).getComponent();
		return component instanceof UIRefPane ? (UIRefPane) component : null;
	}

	/**
	 * �Զ�������� ���������ļ������õ�ֵ���й�������pk_org
	 * 
	 * @author chenshuaia
	 * @param key
	 */
	private void filterZyxField(String key) {
		BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(key);
		if (headItem.getComponent() instanceof UIRefPane && ((UIRefPane) headItem.getComponent()).getRefModel() != null) {
			ErmBillBillForm ermBillFom = (ErmBillBillForm) editor;
			String pk_org = null;
			if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_ORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.DWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.DWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.FYDWBM).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else if (ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG) != null && ermBillFom.getOrgRefFields(JKBXHeaderVO.PK_PAYORG).contains(key)) {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_PAYORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			} else {
				BillItem item = ermBillFom.getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG);
				if (item != null) {
					pk_org = (String) item.getValueObject();
				}
			}

			((UIRefPane) headItem.getComponent()).getRefModel().setPk_org(pk_org);
		}
	}

	private void beforeEditDept(String dwbm, String deptid) {
		BillItem headItem = editor.getBillCardPanel().getHeadItem(deptid);
		if (headItem != null) {
			UIRefPane refPane = (UIRefPane) headItem.getComponent();
			DeptDefaultRefModel model = (DeptDefaultRefModel) refPane.getRefModel();
			model.setPk_org(dwbm);
		}
	}

	// �Ƿ��Ǳ�����
	private boolean isBxBill() {
		return BXConstans.BX_DJDL.equals(getHeadItemStrValue(JKBXHeaderVO.DJDL));
	}

	/**
	 * �Ƿ�Ը��˽���֧��
	 * 
	 * @return
	 */
	private boolean isReceiverPaytarget() {
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
	
	protected Object getHeadValue(String key) {
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
