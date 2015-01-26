package nc.ui.erm.accruedexpense.listener;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.pub.BusinessException;
import nc.vo.resa.costcenter.CostCenterVO;

public class AccBillCardBodyBeforeEditListener implements BillEditListener2 {

	private AccMNBillForm billForm;

	@Override
	public boolean beforeEdit(BillEditEvent e) {
		BillItem item = (BillItem) e.getSource();
		String key = e.getKey();
		
		// 红冲的单据，除了金额字段，其他字段不可编辑。
		BillItem redItem = billForm.getBillCardPanel().getHeadItem(AccruedVO.REDFLAG);
		if (redItem != null && redItem.getValueObject() != null
				&& redItem.getValueObject().equals(ErmAccruedBillConst.REDFLAG_RED)
				&& !AccruedDetailVO.AMOUNT.equals(key)) {
			return false;
		}
		
		if (ErmAccruedBillConst.Accrued_MDCODE_DETAIL.equals(e.getTableCode())) {
			// 根据费用承担单位过滤
			String assume_org = billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.ASSUME_ORG);
			if (AccruedDetailVO.ASSUME_DEPT.equals(key)) {// 费用承担部门
				filtUIRefPaneByOrg(e, assume_org);
			} else if (AccruedDetailVO.PK_IOBSCLASS.equals(key)) {// 收支项目
				filtUIRefPaneByOrg(e, assume_org);
			} else if (AccruedDetailVO.PK_RESACOSTCENTER.equals(key)) {// 成本中心
				beforeEditResacostcenter(e);
			} else if (AccruedDetailVO.PK_WBS.equals(key)) {// 项目任务
				beforeEditWBS(e, assume_org);
			} else if (AccruedDetailVO.PK_CHECKELE.equals(key)) {// 核算要素
				beforeEditCheckele(e);
			} else if (AccruedDetailVO.PK_CUSTOMER.equals(key)) {// 客户
				filtUIRefPaneByOrg(e, assume_org);
			} else if (AccruedDetailVO.PK_SUPPLIER.equals(key)) {// 供应商
				filtUIRefPaneByOrg(e, assume_org);
			} else if (item.getComponent() instanceof UIRefPane
					&& ((UIRefPane) item.getComponent()).getRefModel() != null) {
				((UIRefPane) item.getComponent()).setPk_org(assume_org);
			} else if (AccruedDetailVO.ORG_CURRINFO.equals(key) || AccruedDetailVO.GROUP_CURRINFO.equals(key)
					|| AccruedDetailVO.GLOBAL_CURRINFO.equals(key)) {
				beforeEditCurrInfo(e);
			}

			try {
				AccUiUtil.crossCheck(key, billForm, "N");
			} catch (BusinessException e1) {
				ExceptionHandler.handleExceptionRuntime(e1);
				return false;
			}
		}
		return true;
	}

	private void beforeEditCurrInfo(BillEditEvent e) {
		String assume_org = billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.ASSUME_ORG);
		String pk_org = billForm.getHeadItemStrValue(AccruedVO.PK_ORG);
		String pk_currtype = billForm.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
		String key = e.getKey();
		if (pk_org != null && assume_org != null) {
			if (!pk_org.equals(assume_org)) {
				boolean isEnable = isEnableByCurrInfo(key, assume_org, pk_currtype);
				billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(isEnable);
			} else {
				billForm.getBillCardPanel().getBodyItem(key).getComponent().setEnabled(false);
			}
		} else {
			billForm.getBillCardPanel().getBodyItem(key).setEnabled(false);
		}

	}

	private boolean isEnableByCurrInfo(String key, String pk_org, String pk_currtype) {
		boolean isEnable = true;
		if (AccruedDetailVO.ORG_CURRINFO.equals(key)) {
			isEnable = ErUiUtil.getOrgRateEnableStatus(pk_org, pk_currtype);
		} else if (AccruedDetailVO.GROUP_CURRINFO.equals(key)) {
			isEnable = ErUiUtil.getGroupRateEnableStatus(pk_org, pk_currtype);
		} else if (AccruedDetailVO.GLOBAL_CURRINFO.equals(key)) {
			isEnable = ErUiUtil.getGlobalRateEnableStatus(pk_org, pk_currtype);
		}
		return isEnable;
	}

	private void beforeEditResacostcenter(BillEditEvent e) {
		// 按成本中心所属利润中心过滤
		UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), e.getKey());
		String pk_pcorg = billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.PK_PCORG);
		if (pk_pcorg != null) {
			refPane.setEnabled(true);
			refPane.getRefModel().setPk_org(pk_pcorg);
		} else {
			refPane.setEnabled(false);
		}
		String wherePart = CostCenterVO.PK_PROFITCENTER + "=" + "'" + pk_pcorg + "'";
		billForm.filterRefModelWithWherePart(refPane, pk_pcorg, wherePart, null);
	}

	private void beforeEditCheckele(BillEditEvent e) {
		// 核算要素根据利润中心过滤
		UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), e.getKey());
		String pk_pcorg = (String) billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.PK_PCORG);
		if (pk_pcorg == null) {
			refPane.setEnabled(false);
			refPane.setWhereString("1=0");
		} else {
			refPane.setEnabled(true);
			refPane.setWhereString(null);
			refPane.setPk_org(pk_pcorg);
		}
	}

	private void beforeEditWBS(BillEditEvent e, String assume_org) {
		String pk_project = (String) billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.PK_PROJECT);
		UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), e.getKey());
		if (pk_project != null) {
			String wherePart = " pk_project=" + "'" + pk_project + "'";
			// 过滤项目任务
			billForm.filterRefModelWithWherePart(refPane, assume_org, null, wherePart);
		} else {
			billForm.filterRefModelWithWherePart(refPane, assume_org, null, "1=0");
		}

	}

	/**
	 * 为参照设置过滤组织
	 * 
	 * @param e
	 * @param assume_org
	 */
	private void filtUIRefPaneByOrg(BillEditEvent e, String assume_org) {
		UIRefPane refPane = billForm.getBodyItemUIRefPane(e.getTableCode(), e.getKey());
		refPane.setPk_org(assume_org);
	}

	public AccMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(AccMNBillForm billForm) {
		this.billForm = billForm;
	}

}
