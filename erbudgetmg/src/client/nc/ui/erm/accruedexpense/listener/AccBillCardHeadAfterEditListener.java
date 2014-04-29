package nc.ui.erm.accruedexpense.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.beans.ValueChangedListener;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.UIState;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

public class AccBillCardHeadAfterEditListener implements BillEditListener, ValueChangedListener {

	private AccMNBillForm billForm;
	private AccManageAppModel model;
	protected IExceptionHandler exceptionHandler;

	@Override
	public void afterEdit(BillEditEvent e) {
		String key = e.getKey();
		try {
			if (AccruedVO.PK_ORG.equals(key)) {// 财务组织
				afterEditPkOrg();
			} else if (AccruedVO.OPERATOR_ORG.equals(key)) {// 经办人单位
				afterEditOperatorOrg();
			} else if (AccruedVO.OPERATOR_DEPT.equals(key)) {// 经办人部门
				afterEditOperatorDept();
			} else if (AccruedVO.OPERATOR.equals(key)) {// 经办人
				afterEditOperator();
			} else if (AccruedVO.PK_CURRTYPE.equals(key)) {// 币种
				afterEditCurrType();
			} else if (AccruedVO.AMOUNT.equals(key)) {// 金额
				afterEditAmount();
			} else if (AccruedVO.BILLDATE.equals(key)) {// 日期
				afterEditBillDate();
			} else if (AccruedVO.ORG_CURRINFO.equals(key) || AccruedVO.GROUP_CURRINFO.equals(key)
					|| AccruedVO.GLOBAL_CURRINFO.equals(key)) {// 汇率
				String pk_org = billForm.getHeadItemStrValue(AccruedVO.PK_ORG);
				String pk_currtype = billForm.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);

				boolean isEnable = false;
				if (pk_org != null) {
					if (AccruedVO.ORG_CURRINFO.equals(key)) {
						isEnable = ErUiUtil.getOrgRateEnableStatus(pk_org, pk_currtype);
					} else if (AccruedVO.GROUP_CURRINFO.equals(key)) {
						isEnable = ErUiUtil.getGroupRateEnableStatus(pk_org, pk_currtype);
					} else if (AccruedVO.GLOBAL_CURRINFO.equals(key)) {
						isEnable = ErUiUtil.getGlobalRateEnableStatus(pk_org, pk_currtype);
					}
				}

				if (!isEnable) {// 不可编辑进入，可能是导入造成
					BillItem currinfoItem = billForm.getBillCardPanel().getHeadItem(key);
					currinfoItem.setValue(e.getOldValue());
				}
				
				afterEditCurrInfo(key);
			}

		} catch (BusinessException exception) {
			exceptionHandler.handlerExeption(exception);
		}
		
		// 事件转换，且发出事件 
		billForm.getEventTransformer().afterEdit(e);
	}

	private void afterEditCurrInfo(String key) throws BusinessException {// 汇率
		billForm.resetHeadAmounts();
		String pk_org = billForm.getHeadItemStrValue(AccruedVO.PK_ORG);

		BillModel billModel = billForm.getBillCardPanel().getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		int rowCount = billModel.getRowCount();
		if (rowCount > 0) {
			for (int row = 0; row < rowCount; row++) {
				String assume_org = billForm.getBodyItemStrValue(row, AccruedDetailVO.ASSUME_ORG);
				if (pk_org != null && assume_org != null) {
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
	}

	private void afterEditPkOrg() throws BusinessException {

		billForm.resetCurrency();
		// billForm.resetHeadDigit();
		// billForm.resetCurrencyRate();
		// billForm.resetCardBodyRate();
		// billForm.resetOrgAmount();
		// billForm.setHeadRateBillFormEnable();
		afterEditCurrType();
	}

	private void afterEditBillDate() throws BusinessException {
		// 设置汇率
		billForm.resetCurrencyRate();
		// 汇率变化后，设置金额
		billForm.resetHeadAmounts();
		// 表体汇率
		billForm.resetCardBodyRate();
		// 表体金额
		billForm.resetCardBodyAmount();

	}

	private void afterEditAmount() throws BusinessException {
		// 金额字段编辑后，预提单暂时只处理表头间金额字段的联动，不处理与表体的联动
		billForm.resetHeadAmounts();
	}

	private void afterEditCurrType() throws BusinessException {
		billForm.resetHeadDigit();// 重置精度
		billForm.resetCurrencyRate();// 表头汇率重算
		billForm.resetCardBodyRate();// 表体汇率重算
		billForm.resetOrgAmount();
		billForm.setHeadRateBillFormEnable();// 汇率是否可编辑
	}

	private void afterEditOperator() throws BusinessException {

		String operator = billForm.getHeadItemStrValue(AccruedVO.OPERATOR);
		if (operator == null) {
			return;
		}

		PsnjobVO[] jobs = CacheUtil.getVOArrayByPkArray(PsnjobVO.class, "PK_PSNDOC", new String[] { operator });
		// 缓存中没有
		if (jobs == null) {
			IBxUIControl pd = NCLocator.getInstance().lookup(IBxUIControl.class);
			jobs = pd.queryPsnjobVOByPsnPK(operator);
		}

		// 人员有兼职，多个公司和部门的情况,切换人员档案时，不跳转公司和部门
		if (jobs != null && jobs.length > 1) {
			List<String> deptList = new ArrayList<String>();
			Map<String, List<String>> orgAndDeptMap = new HashMap<String, List<String>>();

			for (PsnjobVO vo : jobs) {
				deptList.add(vo.getPk_dept());
				List<String> list = orgAndDeptMap.get(vo.getPk_org());
				if (list == null) {
					list = new ArrayList<String>();
					list.add(vo.getPk_dept());
				}
				orgAndDeptMap.put(vo.getPk_org(), list);
			}

			String pk_deptid = billForm.getHeadItemStrValue(AccruedVO.OPERATOR_DEPT);
			if (pk_deptid == null || !deptList.contains(pk_deptid)) {
				List<String> dept = orgAndDeptMap.get(billForm.getHeadItemStrValue(AccruedVO.OPERATOR_ORG));
				billForm.setHeadValue(AccruedVO.OPERATOR_DEPT, dept.get(0));
			}
		} else {
			final String[] values = ErUiUtil.getPsnDocInfoById(operator);
			if (values != null && values.length > 0) {
				billForm.setHeadValue(AccruedVO.OPERATOR_DEPT, values[1]);
				billForm.setHeadValue(AccruedVO.OPERATOR_ORG, values[2]);
			}
		}

	}

	private void afterEditOperatorDept() {
		billForm.setHeadValue(AccruedVO.OPERATOR, null);
	}

	private void afterEditOperatorOrg() {
		billForm.setHeadValue(AccruedVO.OPERATOR_DEPT, null);
		billForm.setHeadValue(AccruedVO.OPERATOR, null);
	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void valueChanged(ValueChangedEvent event) {
		Object newValue = event.getNewValue();
		String newpk_org_v = null;
		if (newValue instanceof String[]) {
			newpk_org_v = ((String[]) newValue)[0];
		}
		try {
			confirmChangeOrg(event);
			billForm.getBillCardPanel().getHeadItem(AccruedVO.PK_ORG).setValue(newpk_org_v);
			afterEditPkOrg();

		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	private void confirmChangeOrg(ValueChangedEvent event) {
		Object oldValue = event.getOldValue();
		if (oldValue == null) {// null的时候不需要确认修改
			getBillForm().setEditable(true);
		}

		Object newValue = event.getNewValue();
		if (newValue == null) {
			getBillForm().setEditable(false);
		} else {
			getBillForm().setEditable(true);
			getBillForm().getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPE).setEnabled(false);
			getBillForm().getBillCardPanel().getHeadItem(AccruedVO.PK_TRADETYPEID).setEnabled(false);
		}
		if (getModel().getUiState() == UIState.ADD) {
			// getBillForm().setEditable(true)
			// 方法会将billStatus设置成update，会出现错误，
			// 这里只能做出特殊处理
			getBillForm().getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
		}
	}

	public AccManageAppModel getModel() {
		return model;
	}

	public void setModel(AccManageAppModel model) {
		this.model = model;
	}

	public AccMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(AccMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
