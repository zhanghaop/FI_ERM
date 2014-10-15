package nc.ui.erm.accruedexpense.listener;


import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.ERMDealForJeAndRatioUtil;
import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class AccBillCardBodyAfterEditListener implements BillEditListener {

	private AccMNBillForm billForm;

	@Override
	public void afterEdit(BillEditEvent e) {
		String key = e.getKey();
		BillItem bodyItem = billForm.getBillCardPanel().getBodyItem(e.getTableCode(),key);
		
		if(bodyItem==null)
			return;
		
		if (ErmAccruedBillConst.Accrued_MDCODE_DETAIL.equals(e.getTableCode())) {
			// 多选后自动进行复制行实现
			int[] changRow = null;
			if (ArrayUtils.indexOf(AggAccruedBillVO.getBodyMultiSelectedItems(), bodyItem.getKey(), 0) >= 0) {
				changRow = ERMDealForJeAndRatioUtil.pasteLineForMultiSelected(e.getRow(), AccruedDetailVO.AMOUNT,
						AccruedVO.AMOUNT, null, key, getBillForm().getBillCardPanel(),
						AccruedDetailVO.PK_ACCRUED_DETAIL);

				// 根据多选变化的行，选择相关的参照字段，余额，和最大金额
				if (changRow != null && (changRow.length > 1 || key.equals(AccruedDetailVO.ASSUME_ORG))) {
					// 设置余额,预计余额
					resetOtherJe(changRow);

					try {
						// 重算表体汇率
						if (bodyItem.getKey().equals(AccruedDetailVO.ASSUME_ORG)) {
							resetBodyRate((String) e.getOldValue(), changRow);
						}
						// 计算本币等联动金额
						afterEditAmount(changRow);
					} catch (BusinessException exception) {
						ExceptionHandler.consume(exception);
					}
				}

				// 基本档案编辑后
				afterEditBdDoc(e, changRow);

				// 档案变化，清空相关档案
				resetFieldValue(changRow, getBillForm().getBillCardPanel(), bodyItem.getKey(), e.getOldValue());
			}

			if (bodyItem.getKey().equals(AccruedDetailVO.AMOUNT) || isAmoutField(bodyItem)) {// 金额变化
				try {
					AccUiUtil.setHeadAmountByBodyAmounts(billForm.getBillCardPanel());// 表体金额相加结果放入表头
					// 计算本币等联动金额
					afterEditAmount(e.getRow());
				} catch (BusinessException exception) {
					ExceptionHandler.consume(exception);
				}
			} else if (bodyItem.getKey().equals(AccruedDetailVO.ORG_CURRINFO) || bodyItem.getKey().equals(AccruedDetailVO.GROUP_CURRINFO)
					|| bodyItem.getKey().equals(AccruedDetailVO.GLOBAL_CURRINFO)) {
				// 按表体费用承担单位过滤
				String assume_org = billForm.getBodyItemStrValue(e.getRow(), AccruedDetailVO.ASSUME_ORG);
				String pk_currtype = billForm.getHeadItemStrValue(AccruedVO.PK_CURRTYPE);
				
				boolean isEnable = false;
				if(assume_org != null){
					if(AccruedVO.ORG_CURRINFO.equals(key)){
						isEnable = ErUiUtil.getOrgRateEnableStatus(assume_org, pk_currtype);
					}else if(AccruedVO.GROUP_CURRINFO.equals(key)){
						isEnable = ErUiUtil.getGroupRateEnableStatus(assume_org, pk_currtype);
					}else if(AccruedVO.GLOBAL_CURRINFO.equals(key)){
						isEnable = ErUiUtil.getGlobalRateEnableStatus(assume_org, pk_currtype);
					}
				}
				
				if(!isEnable){//不可编辑进入，可能是导入造成
					billForm.setBodyValue(e.getOldValue(), e.getRow(), key);
				}
				
				billForm.resetCardBodyAmount(e.getRow());
			}
		}
		
		// 事件转换，且发出事件 
		billForm.getEventTransformer().afterEdit(e);
	}

	/**
	 * 判断自定义项是否设置公式改变金额字段
	 * @param bodyItem
	 * @return
	 */
	private boolean isAmoutField(BillItem bodyItem) {
		String[] editFormulas = bodyItem.getEditFormulas();
		if(editFormulas==null){
			return false;
		}
		for(String formula:editFormulas){
			if(formula.indexOf(AccruedDetailVO.AMOUNT)!=-1){
				return true;
			}
		}
		return false;
	}
	
	public void resetFieldValue(int[] changRow, BillCardPanel cardPanel, String selectfield, Object oldValue) {
		if(changRow == null || changRow.length < 1){
			return;
		}
		// 费用单位在多选时，要将部门和项目、比例置空
		for (int rowNum = changRow[0]; rowNum < changRow.length + changRow[0]; rowNum++) {
			Object valueObjectAt = cardPanel.getBillModel().getValueObjectAt(rowNum, selectfield);
			if (valueObjectAt instanceof DefaultConstEnum) {
				if (oldValue != null && oldValue.equals(((DefaultConstEnum) valueObjectAt).getValue())) {
					continue;
				}
			}

			if (AccruedDetailVO.ASSUME_ORG.equals(selectfield)) {
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.ASSUME_DEPT);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_IOBSCLASS);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_SUPPLIER);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_PROJECT);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_WBS);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CUSTOMER);
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_PCORG);// 利润中心
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_RESACOSTCENTER);// 成本中心
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CHECKELE);// 核算要算
			} else if (AccruedDetailVO.PK_PCORG.equals(selectfield)) {
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_RESACOSTCENTER);// 成本中心
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CHECKELE);// 核算要算
			} else if (AccruedDetailVO.PK_PROJECT.equals(selectfield)) {// 项目变化清空项目任务
				cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_WBS);
			}
		}
	}

	// 切换基本档案后，
	private void afterEditBdDoc(BillEditEvent eve, int[] changRow) {
		BillItem bodyItem = billForm.getBillCardPanel().getBodyItem(eve.getTableCode(), eve.getKey());
		if (bodyItem.getKey().equals(AccruedDetailVO.ASSUME_DEPT)) {// 费用承担部门
			for (int row : changRow) {
				AccUiUtil.setCostCenter(row, billForm.getBillCardPanel());
			}
		}
	}
	

	/**
	 * 根据多选变化的行，选择相关的参照字段
	 * 
	 * @param changRow
	 */
	private void resetOtherJe(int[] changRow) {
		// 设置余额,预计余额
		for (int i = changRow[0]; i < changRow.length + changRow[0]; i++) {
			Object amount = billForm.getBillCardPanel().getBodyValueAt(i, AccruedDetailVO.AMOUNT);
			billForm.getBillCardPanel().setBodyValueAt(amount, i, AccruedDetailVO.REST_AMOUNT);
			billForm.getBillCardPanel().setBodyValueAt(amount, i, AccruedDetailVO.PREDICT_REST_AMOUNT);
		}
	}

	private void resetBodyRate(String oldPk_org, int... changRow) throws BusinessException {
		for (int i = 0; i < changRow.length; i++) {
			String assume_org = billForm.getBodyItemStrValue(changRow[i], AccruedDetailVO.ASSUME_ORG);
			if (oldPk_org == null || assume_org == null || !assume_org.equals(oldPk_org)) {
				billForm.resetCardBodyRate(changRow[i]);
			}
		}
	}

	private void afterEditAmount(int... changRow) throws BusinessException {
		billForm.resetHeadAmounts();

		for (int i = 0; i < changRow.length; i++) {
			billForm.resetCardBodyAmount(changRow[i]);
		}

	}

	@Override
	public void bodyRowChange(BillEditEvent e) {
		// TODO Auto-generated method stub

	}

	public AccMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(AccMNBillForm billForm) {
		this.billForm = billForm;
	}


}
