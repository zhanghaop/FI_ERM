package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.ui.erm.matterapp.common.MatterAppCopyEvent;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IBillStatus;

/**
 * 复制按钮
 * @author chenshuaia
 *
 */
public class CopyAction extends NCAction {
	private static final long serialVersionUID = 1L;
	
	private String[] parentFieldNotCopy;
	
	private String[] childFieldNotCopy;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	public CopyAction() {
		ActionInitializer.initializeAction(this, "Copy");
	}
	 
	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedOperaDatas = getModel().getSelectedOperaDatas();
		if(selectedOperaDatas == null){
			return ;
		}
		
		if(selectedOperaDatas.length!=1){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000902")/** @res* "请选择一张单据进行复制"*/);
		}
		
		AggMatterAppVO selectedVO = (AggMatterAppVO) getModel().getSelectedData();
		//校验交易类型是否封存
		DjLXVO tradeTypeVo = ((MAppModel)getModel()).getTradeTypeVo(selectedVO.getParentVO().getPk_tradetype());
		if (tradeTypeVo == null || tradeTypeVo.getFcbz().booleanValue()) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000352")/**
			 * @res*
			 *      "该节点单据类型已被封存，不能进行复制操作！"
			 */
			);
		}
		getModel().setUiState(UIState.ADD);
		
		if (selectedVO != null && getBillForm() != null) {
			AggMatterAppVO aggNewVo = (AggMatterAppVO) selectedVO.clone();
			
			//清空不复制的值
			clearNotCopyValue(aggNewVo);
			
			//设置表头精度
			billForm.resetHeadDigit(aggNewVo.getParentVO().getPk_currtype(), aggNewVo.getParentVO().getPk_org());
			
			//设置默认值
			setCopyDefaultValue(aggNewVo);
			
			//设置VO值
			getBillForm().setValue(aggNewVo);
			
			//在用户未关联人员，复制他做的其他人的单据时，需设置
			billForm.setEditable(true);
			
			//设置表体状态
			BillTabVO[] allTabVos = ((BillForm) getBillForm()).getBillCardPanel().getBillData().getAllTabVos();
			
			for (BillTabVO billTabVO : allTabVos) {
				BillModel bodyModel = ((BillForm) getBillForm()).getBillCardPanel()
				.getBillModel(billTabVO.getTabcode());
				
				if(bodyModel != null){
					for (int i = 0; i < bodyModel.getRowCount(); i++){
						bodyModel.setRowState(i, BillModel.ADD);
					}
				}
				
			}
			
			billForm.setCurrencyRate();//汇率重算
			billForm.resetCardBodyRate();//重算表体汇率
			billForm.resetOrgAmount();//本币重算
			billForm.setHeadRateBillFormEnable();//汇率是否可编辑
			billForm.resetBodyMaxAmount();//表体最大报销金额重算
			billForm.filtBillCardItem();//过滤界面数据
			
			
			((BillForm) getBillForm()).getBillCardPanel().getBillData().setBillstatus(VOStatus.NEW);
			//设置焦点
			getBillForm().getBillCardPanel().transferFocusTo(0);
		}
		
		BillScrollPane bsp = getBillForm().getBillCardPanel().getBodyPanel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		if(bsp!=null && bsp.getTable()!=null){
			bsp.getTable().requestFocus();
		}
		
		MatterAppCopyEvent event = new MatterAppCopyEvent(getBillForm().getBillCardPanel(), e.getClass().getName());
		getModel().fireExtEvent(event);
	}
	
	/**
	 * 清空不需要copy的值
	 * @param aggNewVo
	 */
	private void clearNotCopyValue(AggMatterAppVO aggNewVo) {
		String[] fieldNotCopy = getParentFieldNotCopy(); //取不需要拷贝的属性
		String[] childFieldNotCopy = getChildFieldNotCopy();
		for (int i = 0; i < fieldNotCopy.length; i++) {
			aggNewVo.getParentVO().setAttributeValue(fieldNotCopy[i], null);
		}
		
		if(aggNewVo.getChildrenVO() != null){
			for (MtAppDetailVO child : aggNewVo.getChildrenVO()) {
				for (int i = 0; i < childFieldNotCopy.length; i ++) {
					child.setAttributeValue(childFieldNotCopy[i], null);
				}
			}
		}
	}

	private void setCopyDefaultValue(AggMatterAppVO aggNewVo) {
		MatterAppVO parentVo = (MatterAppVO) aggNewVo.getParentVO();
		
		parentVo.setBilldate(MatterAppUiUtil.getBusiDate());
		parentVo.setPk_group(MatterAppUiUtil.getPK_group());
		
		// 状态设置
		parentVo.setBillstatus(BXStatusConst.DJZT_Saved);
		parentVo.setApprstatus(IBillStatus.FREE);
		parentVo.setEffectstatus(ErmMatterAppConst.EFFECTSTATUS_NO);
		parentVo.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
		parentVo.setRest_amount(parentVo.getOrig_amount());
		parentVo.setOrg_rest_amount(parentVo.getOrg_amount());
		parentVo.setGroup_rest_amount(parentVo.getGroup_amount());
		parentVo.setGlobal_rest_amount(parentVo.getGlobal_amount());
		parentVo.setPre_amount(UFDouble.ZERO_DBL);
		parentVo.setOrg_pre_amount(UFDouble.ZERO_DBL);
		parentVo.setGroup_pre_amount(UFDouble.ZERO_DBL);
		parentVo.setGlobal_pre_amount(UFDouble.ZERO_DBL);
		
		parentVo.setExe_amount(UFDouble.ZERO_DBL);
		parentVo.setOrg_exe_amount(UFDouble.ZERO_DBL);
		parentVo.setGroup_exe_amount(UFDouble.ZERO_DBL);
		parentVo.setGlobal_exe_amount(UFDouble.ZERO_DBL);
		
		getBillForm().getBillOrgPanel().setPkOrg(parentVo.getPk_org_v());

		for (MtAppDetailVO child : aggNewVo.getChildrenVO()) {
			child.setRest_amount(child.getOrig_amount());
			child.setOrg_rest_amount(child.getOrg_amount());
			child.setGroup_rest_amount(child.getGroup_amount());
			child.setGlobal_rest_amount(child.getGlobal_amount());
			child.setClose_status(ErmMatterAppConst.CLOSESTATUS_N);
			
			child.setPre_amount(UFDouble.ZERO_DBL);
			child.setOrg_pre_amount(UFDouble.ZERO_DBL);
			child.setGroup_pre_amount(UFDouble.ZERO_DBL);
			child.setGlobal_pre_amount(UFDouble.ZERO_DBL);
			
			child.setExe_amount(UFDouble.ZERO_DBL);
			child.setOrg_exe_amount(UFDouble.ZERO_DBL);
			child.setGroup_exe_amount(UFDouble.ZERO_DBL);
			child.setGlobal_exe_amount(UFDouble.ZERO_DBL);
		}
	}
	
	private String[] getParentFieldNotCopy() {
		if (parentFieldNotCopy == null) {
			parentFieldNotCopy = new String[] { MatterAppVO.APPROVER, MatterAppVO.APPROVETIME,
					MatterAppVO.PK_MTAPP_BILL, MatterAppVO.BILLNO, MatterAppVO.BILLSTATUS, MatterAppVO.EFFECTSTATUS,
					MatterAppVO.APPRSTATUS, MatterAppVO.MODIFIER, MatterAppVO.MODIFIEDTIME, MatterAppVO.CLOSEMAN,
					MatterAppVO.CLOSEDATE, MatterAppVO.PRINTER, MatterAppVO.PRINTDATE, MatterAppVO.CREATOR,
					MatterAppVO.CREATIONTIME, MatterAppVO.EXE_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT,
					MatterAppVO.GROUP_EXE_AMOUNT, MatterAppVO.GLOBAL_EXE_AMOUNT, MatterAppVO.PRE_AMOUNT,
					MatterAppVO.ORG_PRE_AMOUNT, MatterAppVO.GROUP_PRE_AMOUNT, MatterAppVO.GLOBAL_PRE_AMOUNT,
					"ts"};
		}
		return parentFieldNotCopy;
	}

	private String[] getChildFieldNotCopy() {
		if (childFieldNotCopy == null) {
			childFieldNotCopy = new String[] { MtAppDetailVO.EFFECTSTATUS, MtAppDetailVO.PK_MTAPP_DETAIL,
					MtAppDetailVO.BILLNO, MtAppDetailVO.BILLSTATUS, MtAppDetailVO.CLOSE_STATUS,
					MtAppDetailVO.CLOSEDATE, MtAppDetailVO.EXE_AMOUNT, MtAppDetailVO.ORG_EXE_AMOUNT,
					MtAppDetailVO.GROUP_EXE_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT, MtAppDetailVO.PK_MTAPP_DETAIL,
					MtAppDetailVO.PRE_AMOUNT, MtAppDetailVO.ORG_PRE_AMOUNT, MtAppDetailVO.GROUP_PRE_AMOUNT,
					MtAppDetailVO.GLOBAL_PRE_AMOUNT ,MtAppDetailVO.CLOSEDATE, MtAppDetailVO.CLOSEMAN,
					MtAppDetailVO.ROWNO};
		}
		return childFieldNotCopy;
	}
	
	@Override
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && getModel().getUiState() == UIState.NOT_EDIT;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0}失败！"
													 */;
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(null);
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}
}
