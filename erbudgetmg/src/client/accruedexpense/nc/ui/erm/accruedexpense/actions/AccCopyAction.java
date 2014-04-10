package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillUtils;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

/**
 * 预提单复制按钮
 */
public class AccCopyAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	private AccMNBillForm billForm;

	public AccCopyAction() {
		ActionInitializer.initializeAction(this, IActionCode.COPY);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object[] selectedOperaDatas = getModel().getSelectedOperaDatas();
		if (selectedOperaDatas == null) {
			return;
		}

		if (selectedOperaDatas.length != 1) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000902")/**
			 * @res
			 *      * "请选择一张单据进行复制"
			 */
			);
		}

		AggAccruedBillVO selectedVO = (AggAccruedBillVO) getModel().getSelectedData();
		// 校验交易类型是否封存
		if (selectedVO != null && selectedVO.getParentVO() != null) {
			DjLXVO tradeTypeVo = ((AccManageAppModel) getModel()).getTradeTypeVo(selectedVO.getParentVO()
					.getPk_tradetype());

			if (tradeTypeVo == null || tradeTypeVo.getFcbz().booleanValue()) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000352")/**
				 * @res * "该节点单据类型已被封存，不能进行复制操作！"
				 */
				);
			}
		}
		getModel().setUiState(UIState.ADD);

		if (selectedVO != null && getBillForm() != null) {
			AggAccruedBillVO aggNewVo = (AggAccruedBillVO) selectedVO.clone();

			// 清空不复制的值
			ErmAccruedBillUtils.clearNotCopyValue(aggNewVo);

			// 设置默认值
			setCopyDefaultValue(aggNewVo);

			// 设置VO值
			getBillForm().setValue(aggNewVo);
			// 设置表头精度
			billForm.resetHeadDigit();

			// 在用户未关联人员，复制他做的其他人的单据时，需设置
			billForm.setEditable(true);

			// 清空核销明细页签值
			billForm.getBillCardPanel().getBillData().setBodyValueVO(ErmAccruedBillConst.Accrued_MDCODE_VERIFY, null);
			
			// 设置表体状态
			BillTabVO[] allTabVos = ((BillForm) getBillForm()).getBillCardPanel().getBillData().getAllTabVos();

			for (BillTabVO billTabVO : allTabVos) {
				BillModel bodyModel = ((BillForm) getBillForm()).getBillCardPanel()
						.getBillModel(billTabVO.getTabcode());

				if (bodyModel != null) {
					for (int i = 0; i < bodyModel.getRowCount(); i++) {
						bodyModel.setRowState(i, BillModel.ADD);
					}
				}

			}

			billForm.resetCurrencyRate();// 汇率重算
			billForm.resetCardBodyRate();// 重算表体汇率
			billForm.resetOrgAmount();// 本币重算
			billForm.setHeadRateBillFormEnable();// 汇率是否可编辑
			// billForm.filtBillCardItem();//过滤界面数据

			((BillForm) getBillForm()).getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			// 设置焦点
			getBillForm().getBillCardPanel().transferFocusTo(0);
		}

		BillScrollPane bsp = getBillForm().getBillCardPanel().getBodyPanel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		if (bsp != null && bsp.getTable() != null) {
			bsp.getTable().requestFocus();
		}
	}

	private void setCopyDefaultValue(AggAccruedBillVO aggNewVo) {
		AccruedVO parentVo = (AccruedVO) aggNewVo.getParentVO();

		parentVo.setBilldate(ErUiUtil.getBusiDate());
		parentVo.setPk_group(ErUiUtil.getPK_group());

		// 状态设置
		parentVo.setBillstatus(ErmAccruedBillConst.BILLSTATUS_SAVED);
		parentVo.setApprstatus(IBillStatus.FREE);
		parentVo.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_NO);

		// 金额设置
		parentVo.setRest_amount(parentVo.getAmount());
		parentVo.setOrg_rest_amount(parentVo.getOrg_amount());
		parentVo.setGroup_rest_amount(parentVo.getGroup_amount());
		parentVo.setGlobal_rest_amount(parentVo.getGlobal_amount());
		parentVo.setVerify_amount(UFDouble.ZERO_DBL);
		parentVo.setOrg_verify_amount(UFDouble.ZERO_DBL);
		parentVo.setGroup_verify_amount(UFDouble.ZERO_DBL);
		parentVo.setGlobal_verify_amount(UFDouble.ZERO_DBL);
		parentVo.setPredict_rest_amount(parentVo.getAmount());// 预计余额

		getBillForm().getBillOrgPanel().setPkOrg(parentVo.getPk_org());

		for (AccruedDetailVO child : aggNewVo.getChildrenVO()) {
			child.setRest_amount(child.getAmount());
			child.setOrg_rest_amount(child.getOrg_amount());
			child.setGroup_rest_amount(child.getGroup_amount());
			child.setGlobal_rest_amount(child.getGlobal_amount());

			child.setPredict_rest_amount(child.getAmount());
		}
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getSelectedData() == null) {
			return false;
		}
		AggAccruedBillVO selectedVO = (AggAccruedBillVO) getModel().getSelectedData();
		//红冲单据不允许复制
		if (selectedVO.getParentVO().getRedflag() != null
				&& selectedVO.getParentVO().getRedflag() == ErmAccruedBillConst.REDFLAG_RED) {
			return false;
		}
		return getModel().getSelectedData() != null && getModel().getUiState() == UIState.NOT_EDIT;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public AccMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(AccMNBillForm billForm) {
		this.billForm = billForm;
	}
}
