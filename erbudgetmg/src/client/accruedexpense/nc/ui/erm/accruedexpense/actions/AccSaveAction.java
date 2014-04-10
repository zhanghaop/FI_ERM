package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * Ԥ�ᵥ����
 * @author 
 *
 */
public class AccSaveAction extends SaveAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		dealBodyRowNum();

		AggAccruedBillVO aggValue = (AggAccruedBillVO) getEditor().getValue();
		aggValue.getParentVO().setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
		validate(aggValue);

		// ִ�е���ģ����֤��ʽ
		boolean execValidateFormulas = ((BillForm) getEditor()).getBillCardPanel().getBillData().execValidateFormulas();
		if (!execValidateFormulas) {
			return;
		}
		try {
			saveBackValue(aggValue);
		} catch (BugetAlarmBusinessException ex) {
			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																		 * @ res
																		 * "��ʾ"
																		 */, ex.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " �Ƿ������ˣ�"
																									 */) == MessageDialog.ID_YES) {
				aggValue.getParentVO().setHasntbcheck(UFBoolean.TRUE); // �����
				saveBackValue(aggValue);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "Ԥ������ʧ��" */, ex);
			}
		}
	}

	private void saveBackValue(Object value) throws Exception {
		if (getModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (getModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}
		showSuccessInfo();
	}

	/**
	 * ���������к�
	 */
	private void dealBodyRowNum() {
		AggAccruedBillVO aggvo = (AggAccruedBillVO) getEditor().getValue();
		if (aggvo.getChildrenVO() != null) {
			// ��ձ����е�ֵ
			BillModel billModel = ((BillForm) getEditor()).getBillCardPanel().getBillModel(
					ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
			int rowCount = billModel.getRowCount();
			if (rowCount > 0) {
				for (int row = 0; row < rowCount; row++) {
					Integer currNum = (Integer) billModel.getValueAt(rowCount, AccruedDetailVO.ROWNO);
					if (currNum == null || currNum != (row + 1)) {
						billModel.setValueAt(row + 1, row, AccruedDetailVO.ROWNO);
						if (billModel.getRowState(row) == BillModel.NORMAL) {
							billModel.setRowState(row, BillModel.MODIFICATION);
						}
					}
				}
			}
		}
	}

}
