package nc.ui.erm.matterapp.listener;

import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.matterapp.common.MultiVersionUtils;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.erm.view.ERMOrgPane;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;

/**
 * ��Ƭ��ͷ�༭��listener
 * 
 * @author chenshuaia
 * 
 */
public class BillCardHeadAfterEditlistener implements BillEditListener, ValueChangedListener {
	private static final long serialVersionUID = 1L;

	private MAppModel model;

	private MatterAppMNBillForm billForm;

	protected IExceptionHandler exceptionHandler;

	@Override
	public void afterEdit(BillEditEvent evt) {
		final String key = evt.getKey();
		try {
			if (MatterAppVO.PK_CURRTYPE.equals(key)) {// ����
				afterEditCurrType();
			} else if (MatterAppVO.ORIG_AMOUNT.equals(key)) {// ���
				billForm.resetHeadAmounts();
			} else if (MatterAppVO.PK_ORG.equals(evt.getKey())) {// ����֯
				afterEditPkOrg();
			} else if (MatterAppVO.APPLY_DEPT.equals(evt.getKey())) {// ���벿��
				afterEditApplydept();
			} else if (MatterAppVO.BILLMAKER.equals(evt.getKey())) {// �������޸�
				afterEditBillMaker();
			} else if (MatterAppVO.ORG_CURRINFO.equals(evt.getKey())) {// ���һ���
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.GROUP_CURRINFO.equals(evt.getKey())) {// ���Ż���
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.GLOBAL_CURRINFO.equals(evt.getKey())) {// ȫ�ֻ���
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.PK_ORG_V.equals(evt.getKey())) {// ������֯�汾
				afterEditPk_org_v();
			} else if (MatterAppVO.REASON.equals(evt.getKey())) {// ����
				changeChildrenItemValue(MatterAppVO.REASON, billForm.getHeadItemStrValue(MatterAppVO.REASON));
			} else if (MatterAppVO.PK_SUPPLIER.equals(evt.getKey())) {// ��Ӧ��
				changeChildrenItemValue(MatterAppVO.PK_SUPPLIER, billForm.getHeadItemStrValue(MatterAppVO.PK_SUPPLIER));
			} else if (MatterAppVO.PK_CUSTOMER.equals(evt.getKey())) {// �ͻ�
				changeChildrenItemValue(MatterAppVO.PK_CUSTOMER, billForm.getHeadItemStrValue(MatterAppVO.PK_CUSTOMER));
			} else if(MatterAppVO.BILLDATE.equals(evt.getKey())){
				afterEditBillDate();
			}
			

		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}

	}
	
	/**
	 * ���ڱ༭��仯
	 * <li>��汾�ֶ�
	 * <li>���ʡ����ҽ��
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		UFDate billDate = (UFDate)billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
		//���û���
		billForm.setCurrencyRate();
		//���ʱ仯�����ý��
		billForm.resetHeadAmounts();
		billForm.resetCardBodyAmount();
		
		//���ý����汾
		String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, billDate, billForm
				.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V).getRefModel());
		billForm.setHeadValue(MatterAppVO.PK_ORG_V, pk_vid);
		// ���ò�����֯���յİ汾����
		billForm.getBillOrgPanel().setPkOrg(pk_vid);
		// ������֯
		ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(), billDate), billForm
				.getBillOrgPanel().getRefPane());
	}

	private void afterEditApplydept() {
		// ���������
		billForm.setHeadValue(MatterAppVO.BILLMAKER, null);
		// ���÷��óе�����
		String apply_dept = billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT);
		billForm.setHeadValue(MatterAppVO.ASSUME_DEPT,apply_dept);
	}

	private void afterEditBillMaker() {
		String billMaker = billForm.getHeadItemStrValue(MatterAppVO.BILLMAKER);
		if (billMaker == null) {
			return;
		}

		final String[] values = BXUiUtil.getPsnDocInfoById(billMaker);
		if (values != null && values.length > 0) {
			if (billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT) == null) {
				billForm.setHeadValue(MatterAppVO.APPLY_DEPT, values[1]);
			}
			if (billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT) == null) {
				billForm.setHeadValue(MatterAppVO.ASSUME_DEPT, values[1]);
			}
			
			if (billForm.getHeadItemStrValue(MatterAppVO.PK_ORG) == null) {
				billForm.setHeadValue(MatterAppVO.PK_ORG, values[2]);
			}
		}
	}

	private void clearFieldValue() {
		String[] headIterms = AggMatterAppVO.getApplyOrgHeadIterms().toArray(new String[] {});
		String[] bodyIterms = AggMatterAppVO.getApplyOrgBodyIterms().toArray(new String[] {});
		for (int i = 0; i < headIterms.length; i++) {
			getBillForm().setHeadValue(headIterms[i], null);
		}

		// ��ձ����е�ֵ
		int rowCount = getBillForm().getBillCardPanel().getBillModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			for (int i = 0; i < bodyIterms.length; i++) {
				getBillForm().setBodyValue(null, row, bodyIterms[i]);
			}
		}
	}

	private void afterEditPkOrg() throws BusinessException {
		if (billForm.getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG) != null) {
			clearFieldValue();
		}

		setBodyDefaultValueByHeadValue(MtAppDetailVO.PK_ORG);
		
		billForm.resetCurrency();
		billForm.resetHeadDigit();
		billForm.setCurrencyRate();
		billForm.resetOrgAmount();
		billForm.setHeadRateBillFormEnable();
	}

	private void setBodyDefaultValueByHeadValue(String key) {
		// ����֯�л���������pk_orgĬ��ֵ����
		int rowCount = getBillForm().getBillCardPanel().getBillModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			getBillForm()
					.setBodyValue(getBillForm().getHeadItemStrValue(key), row, key);
		}
	}

	/**
	 * �༭���ֺ���
	 * 
	 * @throws BusinessException
	 */
	private void afterEditCurrType() throws BusinessException {
		final String pk_currtype = billForm.getHeadItemStrValue(MatterAppVO.PK_CURRTYPE);
		if (pk_currtype != null) {
			billForm.resetHeadDigit();
			billForm.setCurrencyRate();
			billForm.resetOrgAmount();
			billForm.setHeadRateBillFormEnable();
		}
	}

	private void afterEditPk_org_v() throws BusinessException {
		String pk_org = MultiVersionUtils.getOrgByMultiVersionOrg(billForm.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V)
				.getRefModel(), billForm.getHeadItemStrValue(MatterAppVO.PK_ORG_V));
		String pk_oldOrg = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);

		if (pk_org == null || (pk_org != null && !pk_org.equals(pk_oldOrg))) {
			billForm.setHeadValue(MatterAppVO.PK_ORG, pk_org);
			afterEditPkOrg();
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {

	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * ��ͷ��������������
	 * 
	 * @param key
	 *            ���ı��item��key
	 * @param value
	 *            �ı���ֵ
	 * @throws ValidationException
	 */
	private void changeChildrenItemValue(String key, String value) throws ValidationException {
		if (value == null) {
			return;
		}

		BillCardPanel panel = billForm.getBillCardPanel();
		String[] tableCodes = billForm.getBillCardPanel().getBillData().getBodyTableCodes();
		for (String tableCode : tableCodes) {
			BillItem[] items = panel.getBillModel(tableCode).getBodyItems();
			for (BillItem item : items) {
				// ���tableCodeҳǩ������Ŀkey
				if (key.equals(item.getKey())) {
					int rowCount = panel.getBillModel(tableCode).getRowCount();
					for (int i = 0; i < rowCount; i++) {
						panel.setBodyValueAt(value, i, key, tableCode);
						panel.getBillModel(tableCode).loadLoadRelationItemValue(i, key);

						// �����¼�������
						BillEditEvent event = new BillEditEvent(billForm.getBillCardPanel().getBodyItem(key), value,
								key, i);
						event.setTableCode(tableCode);
						billForm.getBillCardBodyAfterEditlistener().afterEdit(event);
					}
				}
			}
		}
	}

	/**
	 * ����Ƭ���棬ͷ��������֯���ļ���
	 */
	@Override
	public void valueChanged(ValueChangedEvent event) {
		Object newValue = event.getNewValue();
		String newpk_org_v = null;
		if (newValue instanceof String[]) {
			newpk_org_v = ((String[]) newValue)[0];
		}
		try {
			if(confirmChangeOrg(event)){
				billForm.getBillCardPanel().getHeadItem(MatterAppVO.PK_ORG_V).setValue(newpk_org_v);
				afterEditPk_org_v();
				clearFieldValue();
			}
			
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	private boolean confirmChangeOrg(ValueChangedEvent event) {
		Object oldValue = event.getOldValue();
		if(oldValue == null){//null��ʱ����Ҫȷ���޸�
			getBillForm().setEditable(true);
			if(getModel().getUiState() == UIState.ADD){
				//getBillForm().setEditable(true) �����ὫbillStatus���ó�update������ִ���
				//����ֻ���������⴦��
				getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			}
			return true;
		}
		
		if (MessageDialog.showYesNoDlg(getBillForm(),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0", "0upp2012V575-0128")/*
																											 * @
																											 * res
																											 * "ȷ���޸�"
																											 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-001123"))/*
																									 * @
																									 * res
																									 * "�Ƿ��޸���֯�������������¼�����Ϣ?"
																									 */== MessageDialog.ID_YES) {
			Object newValue = event.getNewValue();
			if (newValue == null) {
				getBillForm().setEditable(false);
			} else {
				getBillForm().setEditable(true);
			}
			if(getModel().getUiState() == UIState.ADD){
				//getBillForm().setEditable(true) �����ὫbillStatus���ó�update������ִ���
				//����ֻ���������⴦��
				getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			}
			return true;
		} else {
			String oldpk_org_v = null;
			if (oldValue instanceof String[]) {
				oldpk_org_v = ((String[]) oldValue)[0];
			}
			getBillForm().getBillOrgPanel().getRefPane().setPK(oldpk_org_v);
			return false;
		}
	}
}
