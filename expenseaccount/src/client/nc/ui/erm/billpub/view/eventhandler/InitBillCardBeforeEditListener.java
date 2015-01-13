package nc.ui.erm.billpub.view.eventhandler;

import javax.swing.JComponent;

import nc.ui.bd.ref.model.FreeCustRefModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.view.ERMBillForm;
import nc.ui.org.ref.DeptDefaultRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXConstans;
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
					getHeadFieldHandle().beforeEditDept_v(dwbm, JKBXHeaderVO.DEPTID_V);
				}
			} else if (JKBXHeaderVO.FYDEPTID_V.equals(key)) {
				BillItem headItem = ((ErmBillBillForm) editor).getBillCardPanel().getHeadItem(JKBXHeaderVO.FYDWBM);
				if (headItem != null) {
					String fydwbm = (String) headItem.getValueObject();
					getHeadFieldHandle().beforeEditDept_v(fydwbm, JKBXHeaderVO.FYDEPTID_V);
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
			} else if (JKBXHeaderVO.JKBXR.equals(key)) {// ������
				getHeadFieldHandle().initJkbxr();
			} else if (key != null && (key.startsWith(BXConstans.HEAD_USERDEF_PREFIX))) {// �Զ��������
				filterZyxField(key);
			} else if (JKBXHeaderVO.CUSTACCOUNT.equals(key)) {// �ͻ������˺�
				beforeEditCustaccount();
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {// ɢ��
				beforeEditFreecust();
			} else if (JKBXHeaderVO.SKYHZH.equals(key)) {// ���������˻�
				beforeEditSkyhzh();
			} else if (JKBXHeaderVO.JOBID.equals(key)) {// ��Ŀ
				getHeadFieldHandle().initProj();
			} else if (JKBXHeaderVO.PK_CASHACCOUNT.equals(key)) {// �ֽ������˻�
				getHeadFieldHandle().initCashAccount();
			} else if (JKBXHeaderVO.PK_CHECKELE.equals(key)) {// ����Ҫ��
				getHeadFieldHandle().initPk_Checkele();
			} else if (JKBXHeaderVO.CASHPROJ.equals(key)) {// �ʽ�ƻ���Ŀ
				getHeadFieldHandle().initCashProj();
			} else if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(key)) {// �ɱ�����
				getHeadFieldHandle().initResaCostCenter();
			} else if (JKBXHeaderVO.PROJECTTASK.equals(key)) {// ��Ŀ����
				getHeadFieldHandle().initProjTask();
			} else if (JKBXHeaderVO.FKYHZH.equals(key)) {// ���������˻�
				getHeadFieldHandle().initFkyhzh();
			} else if (JKBXHeaderVO.SZXMID.equals(key)) {// ��֧��Ŀ
				getHeadFieldHandle().initSzxm();
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
		getHeadFieldHandle().initSkyhzh();
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
		getHeadFieldHandle().initCustAccount();
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

	private HeadFieldHandleUtil getHeadFieldHandle() {
		return ((ErmBillBillForm) editor).getEventHandle().getHeadFieldHandle();
	}
}
