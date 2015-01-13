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
	 * 借款报销单表头编辑前事件
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
			} else if (JKBXHeaderVO.JKBXR.equals(key)) {// 借款报销人
				getHeadFieldHandle().initJkbxr();
			} else if (key != null && (key.startsWith(BXConstans.HEAD_USERDEF_PREFIX))) {// 自定义项过滤
				filterZyxField(key);
			} else if (JKBXHeaderVO.CUSTACCOUNT.equals(key)) {// 客户银行账号
				beforeEditCustaccount();
			} else if (JKBXHeaderVO.FREECUST.equals(key)) {// 散户
				beforeEditFreecust();
			} else if (JKBXHeaderVO.SKYHZH.equals(key)) {// 个人银行账户
				beforeEditSkyhzh();
			} else if (JKBXHeaderVO.JOBID.equals(key)) {// 项目
				getHeadFieldHandle().initProj();
			} else if (JKBXHeaderVO.PK_CASHACCOUNT.equals(key)) {// 现金银行账户
				getHeadFieldHandle().initCashAccount();
			} else if (JKBXHeaderVO.PK_CHECKELE.equals(key)) {// 核算要素
				getHeadFieldHandle().initPk_Checkele();
			} else if (JKBXHeaderVO.CASHPROJ.equals(key)) {// 资金计划项目
				getHeadFieldHandle().initCashProj();
			} else if (JKBXHeaderVO.PK_RESACOSTCENTER.equals(key)) {// 成本中心
				getHeadFieldHandle().initResaCostCenter();
			} else if (JKBXHeaderVO.PROJECTTASK.equals(key)) {// 项目任务
				getHeadFieldHandle().initProjTask();
			} else if (JKBXHeaderVO.FKYHZH.equals(key)) {// 付款银行账户
				getHeadFieldHandle().initFkyhzh();
			} else if (JKBXHeaderVO.SZXMID.equals(key)) {// 收支项目
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
	 * 散户根据客商档案中的散户属性做过滤
	 */
	private void beforeEditFreecust() {
		// 散户
		UIRefPane refPane = getHeadItemUIRefPane(JKBXHeaderVO.FREECUST);
		// 散户设置供应商
		((FreeCustRefModel) refPane.getRefModel()).setCustomSupplier(getCustomerSupplier());
	}

	/***
	 * 客户银行账号根据客户档案+币种做过滤
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
	 * 自定义项过滤 根据配置文件中设置的值进行过滤设置pk_org
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
