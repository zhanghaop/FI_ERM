package nc.ui.erm.matterapp.listener;

import javax.swing.Action;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.AbstractMappBillForm;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.uitheme.ui.ThemeResourceCenter;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

/**
 * 卡片表体编辑后listener
 * @author chenshuaia
 *
 */
public class BillCardBodyAfterEditlistener implements BillEditListener {
	private static final long serialVersionUID = 1L;
	
	private NCAction closeRowAction;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	protected IExceptionHandler exceptionHandler;

	@Override
	public void afterEdit(BillEditEvent eve) {
		BillItem bodyItem = billForm.getBillCardPanel().getBodyItem(eve.getTableCode(),eve.getKey());
		
		if(bodyItem==null)
			return;
		
		if(bodyItem.getKey().equals(MtAppDetailVO.ORIG_AMOUNT) || isAmoutField(bodyItem)){//金额变化
			try {
				afterEditAmount(eve);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		} else if(bodyItem.getKey().equals(MtAppDetailVO.PK_PCORG)){//利润中心变化，核算要素清空
			billForm.setBodyValue(null, eve.getRow(), MtAppDetailVO.PK_CHECKELE);
		} else if(bodyItem.getKey().equals(MtAppDetailVO.PK_PROJECT)){//项目变化清空项目任务
			billForm.setBodyValue(null, eve.getRow(), MtAppDetailVO.PK_WBS);
		}
	}

	private void afterEditAmount(BillEditEvent eve) throws BusinessException {
		MatterAppUiUtil.setHeadAmountByBodyAmounts(billForm.getBillCardPanel());//表体金额相加结果放入表头
		billForm.resetHeadAmounts();
		billForm.resetCardBodyAmount(eve.getRow());
	}


	@Override
	public void bodyRowChange(BillEditEvent e) {
		updateLineCloseActionStatus();
	}
	
	/**
	 * 更新费用申请行关闭按钮状态
	 */
	private void updateLineCloseActionStatus() {
		
		if(getModel().getUiState() != UIState.NOT_EDIT){
			return;
		}
		
		//设置图标
		int[] rows = billForm.getBillCardPanel().getBodyPanel().getTable().getSelectedRows();
		if (rows == null || rows.length <= 0) {
			return;
		}
		
		AggMatterAppVO matterAppVo = (AggMatterAppVO)getModel().getSelectedData();
		
		if(matterAppVo == null){
			getCloseRowAction().setEnabled(false);
			return;
		}
		
		MtAppDetailVO children = (MtAppDetailVO)billForm.getBillCardPanel().getBillModel().getBodyValueRowVO(
				rows[0], MtAppDetailVO.class.getName());
		if(children == null){
			return;
		}

		// 只有生效的单据,非联查情况才可以
		if (matterAppVo.getParentVO().getEffectstatus() < ErmMatterAppConst.EFFECTSTATUS_VALID
				|| ((AbstractMappBillForm) billForm).getLink_type() != -1
				|| (children.getCloseman() != null &&  children.getCloseman().equals(INCSystemUserConst.NC_USER_PK))) {
			getCloseRowAction().setEnabled(false);
		}else{
			getCloseRowAction().setEnabled(true);
		}
		
		if (children.getClose_status() == null || children.getClose_status() == ErmMatterAppConst.CLOSESTATUS_N) {
			getCloseRowAction().putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getCloseLineName());
			getCloseRowAction().putValue(Action.SMALL_ICON,
					ThemeResourceCenter.getInstance().getImage(ErmMatterAppConst.TOOLBARICONS_CLOSE_PNG));
		} else {
			getCloseRowAction().putValue(Action.SHORT_DESCRIPTION, ErmActionConst.getOpenLineName());
			getCloseRowAction().putValue(Action.SMALL_ICON,
					ThemeResourceCenter.getInstance().getImage(ErmMatterAppConst.TOOLBARICONS_OPEN_PNG));
		}
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
			if(formula.indexOf(MtAppDetailVO.ORIG_AMOUNT)!=-1){
				return true;
			}
		}
		return false;
	}

	public NCAction getCloseRowAction() {
		return closeRowAction;
	}

	public void setCloseRowAction(NCAction closeRowAction) {
		this.closeRowAction = closeRowAction;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
	}

	public MatterAppMNBillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(MatterAppMNBillForm billForm) {
		this.billForm = billForm;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
