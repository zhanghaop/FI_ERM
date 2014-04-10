package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillModel;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.SaveAction;
import nc.ui.uif2.editor.BillForm;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
import nc.vo.scmpub.exp.AtpNotEnoughException;

/**
 * 申请单保存按钮
 * @author chenshuaia
 *
 */
public class MaSaveAction extends SaveAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		dealBodyRowNum();
		AggMatterAppVO aggMatterVo = (AggMatterAppVO) getEditor().getValue();
		try {
			// 金额校验
			UFDouble oriAmount = aggMatterVo.getParentVO().getOrig_amount();
			if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) <= 0) {
				throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0021")/* @res "请录入正确的金额" */);
			}

			validate(aggMatterVo);
			// 执行单据模板验证公式
			boolean execValidateFormulas = ((BillForm) getEditor()).getBillCardPanel().getBillData()
					.execValidateFormulas();
			if (!execValidateFormulas) {
				return;
			}
			aggMatterVo.getParentVO().setBillstatus(ErmMatterAppConst.BILLSTATUS_SAVED);
			saveBackValue(aggMatterVo);

		} catch (BugetAlarmBusinessException ex) {
			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																		 * @ res
																		 * "提示"
																		 */, ex.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " 是否继续审核？"
																									 */) == MessageDialog.ID_YES) {
				aggMatterVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // 不检查
				saveBackValue(aggMatterVo);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "预算申请失败" */, ex);
			}
		} catch (AtpNotEnoughException ex) {
			if (MessageDialog.showYesNoDlg(((BillForm) getEditor()).getParent(), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																		 * @ res
																		 * "提示"
																		 */, ex.getMessage()) == MessageDialog.ID_YES) {

				aggMatterVo.getParentVO().setIsignoreatpcheck(UFBoolean.TRUE);
				saveBackValue(aggMatterVo);
			}
		}
	}

	/**
	 * 处理重置行号
	 */
	private void dealBodyRowNum() {
		AggMatterAppVO aggMatterVo = (AggMatterAppVO) getEditor().getValue();
		if (aggMatterVo.getChildrenVO() != null) {
			// 清空表体中的值
			BillModel billModel = ((BillForm) getEditor()).getBillCardPanel().getBillModel(
					ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
			int rowCount = billModel.getRowCount();
			if (rowCount > 0) {
				for (int row = 0; row < rowCount; row++) {
					Integer currNum = (Integer) billModel.getValueAt(rowCount, MtAppDetailVO.ROWNO);
					if (currNum == null || currNum != (row + 1)) {
						billModel.setValueAt(row + 1, row, MtAppDetailVO.ROWNO);
						if (billModel.getRowState(row) == BillModel.NORMAL) {
							billModel.setRowState(row, BillModel.MODIFICATION);
						}
					}
				}
			}
		}
	}

	private void saveBackValue(Object value) throws Exception {
		if (getModel().getUiState() == UIState.ADD) {
			doAddSave(value);
		} else if (getModel().getUiState() == UIState.EDIT) {
			doEditSave(value);
		}
		((AggMatterAppVO) value).getParentVO().setIsignoreatpcheck(UFBoolean.FALSE);
		showSuccessInfo();
	}
}
