package nc.ui.erm.accruedexpense.listener;

import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;

public class AccBillCardHeadBeforeEditListener implements BillCardBeforeEditListener {

	private AccMNBillForm billForm;

	@Override
	public boolean beforeEdit(BillItemEvent e) {
		BillItem item = (BillItem) e.getSource();
		String key = item.getKey();

		if (AccruedVO.OPERATOR_DEPT.equals(key)) {// 经办人部门根据经办人单位做过滤
			beforeEditOperatorDept(item);
		} else if (AccruedVO.OPERATOR.equals(key)) {
			beforeEditOperator(item);
		} else if (item.getComponent() instanceof UIRefPane 
				&& ((UIRefPane) item.getComponent()).getRefModel() != null) {
			String pk_org = billForm.getHeadItemStrValue(AccruedVO.PK_ORG);
			((UIRefPane) item.getComponent()).setPk_org(pk_org);
		}
		
		try {
			AccUiUtil.crossCheck(key, billForm, "N");
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		
		return true;
	}

	private void beforeEditOperator(BillItem item) {
		String oeprator_org = billForm.getHeadItemStrValue(AccruedVO.OPERATOR_ORG);
		AbstractRefTreeModel model = (AbstractRefTreeModel) ((UIRefPane) item.getComponent()).getRefModel();
		model.setPk_org(oeprator_org);
		String operator_dept = billForm.getHeadItemStrValue(AccruedVO.OPERATOR_DEPT);
		if (operator_dept != null) {
			model.setWherePart("pk_dept = '" + operator_dept + "' ");
			model.setClassWherePart(DeptVO.PK_DEPT + "='" + operator_dept + "'");
		} else {
			model.setWherePart(null);
			model.setClassWherePart(null);
		}
	}

	private void beforeEditOperatorDept(BillItem item) {
		String operator_org = billForm.getHeadItemStrValue(AccruedVO.OPERATOR_ORG);
		((UIRefPane) item.getComponent()).setPk_org(operator_org);
	}

	public AccMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(AccMNBillForm billForm) {
		this.billForm = billForm;
	}

}
