package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.EditAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.pf.IPfRetCheckInfo;

public class AccEditAction extends EditAction {
	private static final long serialVersionUID = 1L;

	private IEditor editor;

	public void doAction(ActionEvent e) throws Exception {
		AggAccruedBillVO aggVo = (AggAccruedBillVO) getModel().getSelectedData();

		checkEdit(aggVo);

		super.doAction(e);

		// 设置一下焦点，否则修改后不正确
		BillScrollPane bsp = ((BillForm) getEditor()).getBillCardPanel().getBodyPanel(
				ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		if (bsp != null && bsp.getTable() != null) {
			bsp.getTable().requestFocus();
		}
	}

	private void checkEdit(AggAccruedBillVO aggVo) throws BusinessException {
		if (!checkDataPermission()) {// 权限校验
			throw new ValidationException(IShowMsgConstant.getDataPermissionInfo());
		}

		AccruedVO parentVo = aggVo.getParentVO();
		Integer spzt = parentVo.getApprstatus();
		Integer djzt = parentVo.getBillstatus();// 单据状态控制
		if (djzt != null && djzt.equals(ErmAccruedBillConst.BILLSTATUS_SAVED)) {
			String userId = ErUiUtil.getPk_user();
			String billId = parentVo.getPk_accrued_bill();
			String billType = parentVo.getPk_tradetype();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// 启动了审批流后
					if (spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(parentVo.getCreator())) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
								"0201212-0093")/* @res "请收回单据再修改！" */);
					}

					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId, billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
								"0201212-0092")/* @res "请取消审批再修改！" */);
					}
				} else {
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
							"0201212-0093")/* @res "请收回单据再修改！" */);
				}

			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT || getModel().getSelectedData() == null) {
			return false;
		}

		AggAccruedBillVO aggVo = (AggAccruedBillVO) getModel().getSelectedData();
		AccruedVO parentVo = aggVo.getParentVO();
		
		
		Integer spzt = parentVo.getApprstatus();
		Integer billstatus = parentVo.getBillstatus();
		if(billstatus.equals(ErmAccruedBillConst.BILLSTATUS_INVALID)){
			return false;
		}
		
		if (billstatus.equals(ErmAccruedBillConst.BILLSTATUS_APPROVED) || spzt.equals(IPfRetCheckInfo.NOPASS)) {
			return false;
		}

		return true;
	}

	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public IEditor getEditor() {
		return editor;
	}

}
