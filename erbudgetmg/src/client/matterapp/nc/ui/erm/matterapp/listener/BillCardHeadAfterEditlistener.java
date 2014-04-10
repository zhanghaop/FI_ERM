package nc.ui.erm.matterapp.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.ui.erm.matterapp.common.ErmForMatterAppUtil;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
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
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.bd.psn.PsnjobVO;
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
				//UFDouble bodyje = (UFDouble) billForm.getBillCardPanel().getBodyValueAt(0, MtAppDetailVO.ORIG_AMOUNT);
				//������һ��ʱ������ͷ�Ľ���������
				if (billForm.getBillCardPanel().getRowCount() == 1) {
					ErmForMatterAppUtil.reComputeBodyJeByAvg(billForm.getBillCardPanel());
					MatterAppUiUtil.setBodyShareRatio(billForm.getBillCardPanel());
					billForm.resetCardBodyAmount(0);
				}
			} else if (MatterAppVO.PK_ORG.equals(evt.getKey())) {// ����֯
				afterEditPkOrg();
			} else if (MatterAppVO.APPLY_DEPT.equals(evt.getKey())) {// ���벿��
				afterEditApplydept();
			} else if (MatterAppVO.BILLMAKER.equals(evt.getKey())) {// �������޸�
				afterEditBillMaker();
			} else if (MatterAppVO.ORG_CURRINFO.equals(key) || MatterAppVO.GROUP_CURRINFO.equals(key)
					|| MatterAppVO.GLOBAL_CURRINFO.equals(key)) {// ����
				billForm.resetHeadAmounts();
				String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);

				BillModel billModel = billForm.getBillCardPanel().getBillModel(
						ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
				int rowCount = billModel.getRowCount();
				if (rowCount > 0) {
					for (int row = 0; row < rowCount; row++) {
						String assume_org = billForm.getBodyItemStrValue(row, MtAppDetailVO.ASSUME_ORG);
						if(pk_org != null && assume_org != null){
							if (pk_org.equals(assume_org)) {
								billForm.setBodyValue(billForm.getHeadUFDoubleValue(key), row, key);
							} else {
								String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
								String assumeLocalCurrPK = Currency.getOrgLocalCurrPK(assume_org);
								if (orgLocalCurrPK != null && assumeLocalCurrPK != null
										&& assumeLocalCurrPK.equals(orgLocalCurrPK)) {
									billForm.setBodyValue(billForm.getHeadUFDoubleValue(key), row, key);
								}
							}
						}
					}
				}
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.PK_ORG_V.equals(evt.getKey())) {// ������֯�汾
				afterEditPk_org_v();
			} else if (MatterAppVO.REASON.equals(evt.getKey())) {// ����
				changeChildrenItemValue(MatterAppVO.REASON, billForm.getHeadItemStrValue(MatterAppVO.REASON));
			} else if (MatterAppVO.PK_SUPPLIER.equals(evt.getKey())) {// ��Ӧ��
				changeChildrenItemValue(MatterAppVO.PK_SUPPLIER, billForm.getHeadItemStrValue(MatterAppVO.PK_SUPPLIER));
			} else if (MatterAppVO.PK_CUSTOMER.equals(evt.getKey())) {// �ͻ�
				changeChildrenItemValue(MatterAppVO.PK_CUSTOMER, billForm.getHeadItemStrValue(MatterAppVO.PK_CUSTOMER));
			} else if (MatterAppVO.BILLDATE.equals(evt.getKey())) {
				afterEditBillDate();
			} else if (MatterAppVO.ASSUME_DEPT.equals(evt.getKey())) {
				afterEditAssumeDept();
			}

		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}
		// �¼�ת�����ҷ����¼�
		billForm.getEventTransformer().afterEdit(evt);
	}

	private void afterEditAssumeDept() {
		// ���÷��óе�����
		String assumeDept = billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT);

		// ���óе������л��������з��óе�����Ĭ��ֵ����
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
		BillItem assumeDeptItem = getBillForm().getBillCardPanel().getBodyItem(
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ASSUME_DEPT);
		
		if(assumeDeptItem == null){
			return;
		}
		
		for (int row = 0; row < rowCount; row++) {
			if (assumeDeptItem != null && assumeDeptItem.isShow()) {
				if (billForm.getBodyValue(row, MtAppDetailVO.ASSUME_DEPT) == null) {
					getBillForm().setBodyValue(assumeDept, row, MtAppDetailVO.ASSUME_DEPT + IBillItem.ID_SUFFIX);
					ErmForMatterAppUtil.setCostCenter(row, billForm.getBillCardPanel());
				}
			} else {
				getBillForm().setBodyValue(assumeDept, row, MtAppDetailVO.ASSUME_DEPT + IBillItem.ID_SUFFIX);
				ErmForMatterAppUtil.setCostCenter(row, billForm.getBillCardPanel());
			}
		}
	}

	/**
	 * ���ڱ༭��仯 <li>��汾�ֶ� <li>���ʡ����ҽ��
	 * 
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		UFDate billDate = (UFDate) billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
		// ���û���
		billForm.setCurrencyRate();
		// ���ʱ仯�����ý��
		billForm.resetHeadAmounts();
		// �������
		billForm.resetCardBodyRate();
		// ������
		billForm.resetCardBodyAmount();

		// ������֯
		ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(), billDate), billForm.getBillOrgPanel()
				.getRefPane());

		// ���ý����汾
		String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, billDate,
				billForm.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V).getRefModel());

		// ���ò�����֯���յİ汾����
		billForm.getBillOrgPanel().setPkOrg(pk_vid);

		billForm.setHeadValue(MatterAppVO.PK_ORG_V, billForm.getBillOrgPanel().getRefPane().getRefPK());
	}

	private void afterEditApplydept() {
		// ���������
		billForm.setHeadValue(MatterAppVO.BILLMAKER, null);
		// ���÷��óе�����
		String assumeDept = billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT);
		if (assumeDept == null) {
			String apply_dept = billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT);
			billForm.setHeadValue(MatterAppVO.ASSUME_DEPT, apply_dept);
		}
	}

	private void afterEditBillMaker() throws BusinessException {
		String billMaker = billForm.getHeadItemStrValue(MatterAppVO.BILLMAKER);
		if (billMaker == null) {
			return;
		}
		
		PsnjobVO[] jobs = CacheUtil.getVOArrayByPkArray(PsnjobVO.class, "PK_PSNDOC", new String[] { billMaker });
		// ������û��
		if (jobs == null) {
			IBxUIControl pd = NCLocator.getInstance().lookup(IBxUIControl.class);
			jobs = pd.queryPsnjobVOByPsnPK(billMaker);
		}
		
		//��Ա�м�ְ�������˾�Ͳ��ŵ����,�л���Ա����ʱ������ת��˾�Ͳ���
		if(jobs!=null && jobs.length>1){
			List<String> deptList = new ArrayList<String>();
			Map<String,List<String>> orgAndDeptMap = new HashMap<String,List<String>>();
			
			for(PsnjobVO vo : jobs){
				deptList.add(vo.getPk_dept());
				List<String> list = orgAndDeptMap.get(vo.getPk_org());
				if(list==null){
					list= new ArrayList<String>();
					list.add(vo.getPk_dept());
				}
				orgAndDeptMap.put(vo.getPk_org(), list);
			}
			
			Object pk_deptid = billForm.getBillCardPanel().getHeadItem(MatterAppVO.APPLY_DEPT).getValueObject();
			if(pk_deptid==null || !deptList.contains(pk_deptid.toString())){
				List<String> dept = orgAndDeptMap.get(billForm.getHeadItemStrValue(MatterAppVO.PK_ORG));
				billForm.setHeadValue(MatterAppVO.APPLY_DEPT, dept.get(0));
			}
		}else{
			final String[] values = ErUiUtil.getPsnDocInfoById(billMaker);
			if (values != null && values.length > 0) {
				billForm.setHeadValue(MatterAppVO.APPLY_DEPT, values[1]);

				if (billForm.getHeadItemStrValue(MatterAppVO.ASSUME_DEPT) == null) {
					billForm.setHeadValue(MatterAppVO.ASSUME_DEPT, values[1]);
				}

				if (billForm.getHeadItemStrValue(MatterAppVO.PK_ORG) == null) {
					billForm.setHeadValue(MatterAppVO.PK_ORG, values[2]);
				}
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
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
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

		// ����֯�л��������з��óе���λĬ��ֵ����
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();
		BillItem assumeOrgItem = getBillForm().getBillCardPanel().getBodyItem(
				ErmMatterAppConst.MatterApp_MDCODE_DETAIL, MtAppDetailVO.ASSUME_ORG);
		for (int row = 0; row < rowCount; row++) {
			if (assumeOrgItem != null && assumeOrgItem.isShow()) {
				if (billForm.getBodyValue(row, MtAppDetailVO.ASSUME_ORG) == null) {
					resetBodyAssumeOrg(row);
				}
			} else {
				resetBodyAssumeOrg(row);
			}
		}
	}

	private void resetBodyAssumeOrg(int row) {
		getBillForm().setBodyValue(getBillForm().getHeadItemStrValue(MatterAppVO.PK_ORG), row,
				MtAppDetailVO.ASSUME_ORG + IBillItem.ID_SUFFIX);

		billForm.resetCardBodyRate(row);
		billForm.resetCardBodyAmount(row);
		ErmForMatterAppUtil.resetFieldValue(new int[] { row }, getBillForm().getBillCardPanel(),
				MtAppDetailVO.ASSUME_ORG, null);
	}

	private void setBodyDefaultValueByHeadValue(String key) {
		// ����֯�л���������pk_orgĬ��ֵ����
		int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
				.getRowCount();

		for (int row = 0; row < rowCount; row++) {
			getBillForm().setBodyValue(getBillForm().getHeadItemStrValue(key), row, key);
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
			int rowCount = getBillForm().getBillCardPanel().getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL)
					.getRowCount();
			for (int row = 0; row < rowCount; row++) {
				getBillForm().setBodyValue(pk_currtype, row, MtAppDetailVO.PK_CURRTYPE + IBillItem.ID_SUFFIX);
			}

			billForm.resetHeadDigit();// ���þ���
			billForm.setCurrencyRate();// ��ͷ��������
			billForm.resetCardBodyRate();// �����������
			billForm.resetOrgAmount();
			billForm.setHeadRateBillFormEnable();// �����Ƿ�ɱ༭
		}
	}

	private void afterEditPk_org_v() throws BusinessException {
		String pk_org = MultiVersionUtils.getOrgByMultiVersionOrg(billForm.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V)
				.getRefModel(), billForm.getHeadItemStrValue(MatterAppVO.PK_ORG_V));
		String pk_oldOrg = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);

		if (pk_org == null || (pk_org != null && !pk_org.equals(pk_oldOrg))) {
			billForm.setHeadValue(MatterAppVO.PK_ORG, pk_org);
			// afterEditPkOrg();
		}
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// �¼�ת�����ҷ����¼�
		billForm.getEventTransformer().bodyRowChange(e);
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
			if (confirmChangeOrg(event)) {
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
		if (oldValue == null) {// null��ʱ����Ҫȷ���޸�
			getBillForm().setEditable(true);
			if (getModel().getUiState() == UIState.ADD) {
				// getBillForm().setEditable(true)
				// �����ὫbillStatus���ó�update������ִ���
				// ����ֻ���������⴦��
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
			if (getModel().getUiState() == UIState.ADD) {
				// getBillForm().setEditable(true)
				// �����ὫbillStatus���ó�update������ִ���
				// ����ֻ���������⴦��
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
