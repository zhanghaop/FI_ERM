package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.eventhandler.BodyEventHandleUtil;
import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.DelLineAction;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class ERMDelLineAction extends DelLineAction {
	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		// 防止界面上，最后编辑的内容不生效
		getBillCardPanel().stopEditing();
		
		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(getBillCardPanel());

		if (getBillCardPanel().getBillModel().getRowCount() != 0) {

			validateAddRow();
			
			boolean isNeed = isNeedContrast();

			super.doAction(e);

			// 删除，粘贴行后进行重设表头金额的操作.
			((ErmBillBillForm) getCardpanel()).getbodyEventHandle().resetJeAfterModifyRow();
			
			if (isNeed) {
				doContract();
			}

			if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
				BillItem ismashare = getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE);
				
				DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
				boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO,ErmDjlxConst.BXTYPE_ADJUST);
					
				// 拉分摊申请单后，分摊标志位不允许取消
				if (getBillCardPanel().getBillModel().getRowCount() == 0 
						&& (ismashare == null || !(Boolean)ismashare.getValueObject())
						&& !isAdjust) {
					JKBXVO jkbxvo = (JKBXVO) getCardpanel().getValue();
					if(isCancelCostshareEnable(jkbxvo)){
						
						int result = MessageDialog.showYesNoDlg(getCardpanel(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("upp2012v575_0", "0upp2012V575-0038")/*
								 * @
								 * res
								 * "确认取消"
								 */, NCLangRes4VoTransl.getNCLangRes()
								 .getStrByID("201107_0", "0201107-0006"));
						if (result == MessageDialog.ID_YES) {
							if (getBillCardPanel().getHeadItem(BXHeaderVO.ISCOSTSHARE) != null) {
								getBillCardPanel().getHeadItem(BXHeaderVO.ISCOSTSHARE).setValue(UFBoolean.FALSE);
							}
							ErmForCShareUiUtil.setCostPageShow(this.getBillCardPanel(), false);
							this.getBillCardPanel().getHeadItem(BXHeaderVO.FYDWBM_V).getComponent().setEnabled(true);
						}
					}
				} else {
					if (isNeedAvg) {
						ErmForCShareUiUtil.reComputeAllJeByAvg(getBillCardPanel());
						for (int i = 0; i < getBillCardPanel().getRowCount(); i++) {
							//ErmForCShareUiUtil.setRateAndAmount(i, this.getBillCardPanel());
							ErmForCShareUiUtil.setRateAndAmountNEW(i, this.getBillCardPanel(),"DEL_");
						}
					}
				}
			}
		}
		
		new BodyEventHandleUtil((ErmBillBillForm) getCardpanel()).exeBodyUserdefine2();
	}
	
	protected boolean isCancelCostshareEnable(JKBXVO jkbxvo){
		return true;
	}

	/**
	 * 冲销操作 add by wangle
	 * 
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	private void doContract() throws ValidationException, BusinessException {
		int rows = getBillCardPanel().getBillModel(BXConstans.CONST_PAGE).getRowCount();
		if (rows > 0) {
			BXUiUtil.doContract((ErmBillBillForm) getCardpanel());
		}
	}

	/**
	 * 是否需要冲销
	 * 
	 * @return
	 */
	private boolean isNeedContrast() {
		String tableCode = getBillCardPanel().getCurrentBodyTableCode();
		BillScrollPane bsp = getBillCardPanel().getBodyPanel(tableCode);
		int selectedRow = bsp.getTable().getSelectedRow();

		UFDouble cjkybje = (UFDouble) getBillCardPanel().getBodyValueAt(selectedRow, JKBXHeaderVO.CJKYBJE);

		if (!tableCode.equals(BXConstans.CONST_PAGE)) {
			if (cjkybje != null && cjkybje.compareTo(UFDouble.ZERO_DBL) > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean validateAddRow() throws BusinessException {
		DjLXVO currentDjLXVO = ((ErmBillBillManageModel)getModel()).getCurrentDjLXVO();
		boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(currentDjLXVO,ErmDjlxConst.BXTYPE_ADJUST);
		// 分摊页签，报销金额不能为0
		if (getBillCardPanel().getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
			UFDouble totalAmount = (UFDouble) getBillCardPanel().getHeadItem(JKBXHeaderVO.YBJE).getValueObject();
			if (!isAdjust&&!ErmForCShareUtil.isUFDoubleGreaterThanZero(totalAmount)
					&& !BXConstans.BXINIT_NODECODE_G.equals(getNodeCode())
					&& !BXConstans.BXINIT_NODECODE_U.equals(getNodeCode())) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0005")/* @res "报销金额应大于0！" */);
			}
		}
		return true;
	}

	private String getNodeCode() {
		return getCardpanel().getModel().getContext().getNodeCode();
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			String tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		return super.isActionEnable();
	}

}
