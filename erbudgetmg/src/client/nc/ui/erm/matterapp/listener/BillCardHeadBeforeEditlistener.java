package nc.ui.erm.matterapp.listener;

import nc.bs.erm.common.ErmConst;
import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 
 * @author chenshuaia
 *
 */
public class BillCardHeadBeforeEditlistener implements BillCardBeforeEditListener{
	private static final long serialVersionUID = 1L;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	protected IExceptionHandler exceptionHandler;
	
	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		BillItem item = (BillItem) evt.getSource();
		String key = item.getKey();
		
		if(MatterAppVO.APPLY_DEPT.equals(key)){//申请部门按申请单位过滤
			beforeEditDept(item);
		}else if(MatterAppVO.ASSUME_DEPT.equals(key)){//费用承担部门按申请单位过滤
			beforeEditDept(item);
		}else if(MatterAppVO.BILLMAKER.equals(key)){
			beforeEditBillMaker(item);
		}else if(item.getComponent() instanceof UIRefPane
				&& ((UIRefPane) item.getComponent()).getRefModel() != null) {
			String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
			((UIRefPane) item.getComponent()).setPk_org(pk_org);
		}
		
		try {
			MatterAppUiUtil.crossCheck(key, billForm, "Y");//交叉校验
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
			return false;
		}
		// 事件转换，且发出事件 
		return billForm.getEventTransformer().beforeEdit(evt);
	}
	
	// 用申请单位过滤部门
	private void beforeEditDept(BillItem item){
		String apply_org = billForm.getHeadItemStrValue(MatterAppVO.APPLY_ORG);
		((UIRefPane) item.getComponent()).setPk_org(apply_org);
	}
	
	// 过滤申请人
	private void beforeEditBillMaker(BillItem item) {
		String apply_org = billForm.getHeadItemStrValue(MatterAppVO.APPLY_ORG);
		UFDate billDate = (UFDate) billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();

		try {
			Integer matype = getModel().getTradeTypeVo(getModel().getDjlxbm()).getMatype();
			if (matype == null || matype == ErmConst.MATTERAPP_BILLTYPE_BX) {
				ErUiUtil.initSqdlr(billForm, item, getModel().getDjlxbm(), apply_org, billDate);
			} else {
				AbstractRefTreeModel model = (AbstractRefTreeModel) ((UIRefPane) item.getComponent()).getRefModel();
				model.setPk_org(apply_org);
				String applyDept = billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT);

				if (applyDept != null) {
					model.setWherePart("pk_dept = '" + applyDept + "' ");
					model.setClassWherePart(DeptVO.PK_DEPT + "='" + applyDept + "'");
				} else {
					model.setWherePart(null);
					model.setClassWherePart(null);
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
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
