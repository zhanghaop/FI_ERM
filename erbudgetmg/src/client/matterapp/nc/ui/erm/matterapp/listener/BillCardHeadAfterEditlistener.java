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
 * 卡片表头编辑后listener
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
			if (MatterAppVO.PK_CURRTYPE.equals(key)) {// 币种
				afterEditCurrType();
			} else if (MatterAppVO.ORIG_AMOUNT.equals(key)) {// 金额
				billForm.resetHeadAmounts();
			} else if (MatterAppVO.PK_ORG.equals(evt.getKey())) {// 主组织
				afterEditPkOrg();
			} else if (MatterAppVO.APPLY_DEPT.equals(evt.getKey())) {// 申请部门
				afterEditApplydept();
			} else if (MatterAppVO.BILLMAKER.equals(evt.getKey())) {// 申请人修改
				afterEditBillMaker();
			} else if (MatterAppVO.ORG_CURRINFO.equals(evt.getKey())) {// 本币汇率
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.GROUP_CURRINFO.equals(evt.getKey())) {// 集团汇率
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.GLOBAL_CURRINFO.equals(evt.getKey())) {// 全局汇率
				billForm.resetHeadAmounts();
				billForm.resetCardBodyAmount();
			} else if (MatterAppVO.PK_ORG_V.equals(evt.getKey())) {// 财务组织版本
				afterEditPk_org_v();
			} else if (MatterAppVO.REASON.equals(evt.getKey())) {// 事由
				changeChildrenItemValue(MatterAppVO.REASON, billForm.getHeadItemStrValue(MatterAppVO.REASON));
			} else if (MatterAppVO.PK_SUPPLIER.equals(evt.getKey())) {// 供应商
				changeChildrenItemValue(MatterAppVO.PK_SUPPLIER, billForm.getHeadItemStrValue(MatterAppVO.PK_SUPPLIER));
			} else if (MatterAppVO.PK_CUSTOMER.equals(evt.getKey())) {// 客户
				changeChildrenItemValue(MatterAppVO.PK_CUSTOMER, billForm.getHeadItemStrValue(MatterAppVO.PK_CUSTOMER));
			} else if(MatterAppVO.BILLDATE.equals(evt.getKey())){
				afterEditBillDate();
			}
			

		} catch (Exception e) {
			exceptionHandler.handlerExeption(e);
		}

	}
	
	/**
	 * 日期编辑后变化
	 * <li>多版本字段
	 * <li>汇率、本币金额
	 * @throws BusinessException
	 */
	private void afterEditBillDate() throws BusinessException {
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		UFDate billDate = (UFDate)billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
		//设置汇率
		billForm.setCurrencyRate();
		//汇率变化后，设置金额
		billForm.resetHeadAmounts();
		billForm.resetCardBodyAmount();
		
		//设置界面多版本
		String pk_vid = MultiVersionUtils.getHeadOrgMultiVersion(pk_org, billDate, billForm
				.getHeadItemUIRefPane(MatterAppVO.PK_ORG_V).getRefModel());
		billForm.setHeadValue(MatterAppVO.PK_ORG_V, pk_vid);
		// 设置财务组织参照的版本设置
		billForm.getBillOrgPanel().setPkOrg(pk_vid);
		// 过滤组织
		ERMOrgPane.filtOrgs(ErUiUtil.getPermissionOrgVs(getModel().getContext(), billDate), billForm
				.getBillOrgPanel().getRefPane());
	}

	private void afterEditApplydept() {
		// 清空申请人
		billForm.setHeadValue(MatterAppVO.BILLMAKER, null);
		// 设置费用承担部门
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

		// 清空表体中的值
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
		// 主组织切换，表体中pk_org默认值设置
		int rowCount = getBillForm().getBillCardPanel().getBillModel().getRowCount();
		for (int row = 0; row < rowCount; row++) {
			getBillForm()
					.setBodyValue(getBillForm().getHeadItemStrValue(key), row, key);
		}
	}

	/**
	 * 编辑币种后动作
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
	 * 表头数据联动到表体
	 * 
	 * @param key
	 *            被改变的item的key
	 * @param value
	 *            改变后的值
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
				// 如果tableCode页签中有项目key
				if (key.equals(item.getKey())) {
					int rowCount = panel.getBillModel(tableCode).getRowCount();
					for (int i = 0; i < rowCount; i++) {
						panel.setBodyValueAt(value, i, key, tableCode);
						panel.getBillModel(tableCode).loadLoadRelationItemValue(i, key);

						// 发送事件给表体
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
	 * 处理卡片界面，头部财务组织面板的监听
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
		if(oldValue == null){//null的时候不需要确认修改
			getBillForm().setEditable(true);
			if(getModel().getUiState() == UIState.ADD){
				//getBillForm().setEditable(true) 方法会将billStatus设置成update，会出现错误，
				//这里只能做出特殊处理
				getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			}
			return true;
		}
		
		if (MessageDialog.showYesNoDlg(getBillForm(),
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0", "0upp2012V575-0128")/*
																											 * @
																											 * res
																											 * "确认修改"
																											 */,
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-001123"))/*
																									 * @
																									 * res
																									 * "是否修改组织，这样会清空您录入的信息?"
																									 */== MessageDialog.ID_YES) {
			Object newValue = event.getNewValue();
			if (newValue == null) {
				getBillForm().setEditable(false);
			} else {
				getBillForm().setEditable(true);
			}
			if(getModel().getUiState() == UIState.ADD){
				//getBillForm().setEditable(true) 方法会将billStatus设置成update，会出现错误，
				//这里只能做出特殊处理
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
