package nc.ui.erm.matterapp.listener;

import nc.ui.bd.ref.AbstractRefTreeModel;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardBeforeEditListener;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillItemEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;

public class BillCardHeadBeforeEditlistener implements BillCardBeforeEditListener{
	private static final long serialVersionUID = 1L;
	
	private MAppModel model;

	private MatterAppMNBillForm billForm;
	
	protected IExceptionHandler exceptionHandler;
	
	@Override
	public boolean beforeEdit(BillItemEvent evt) {
		BillItem item = (BillItem) evt.getSource();
		String key = item.getKey();
		
		/*if(MatterAppVO.PK_ORG.equals(key)){
			//保存后不允许修改借款报销单位
			UFDate date = (UFDate) billForm.getBillCardPanel().getHeadItem(MatterAppVO.BILLDATE).getValueObject();
			if (date == null) {
				// 单据日期为空，取业务日期
				date = BXUiUtil.getBusiDate();
			}
			UIRefPane refPane = billForm.getHeadItemUIRefPane(key);
			
			//仅主组织字段根据功能权限过滤
			String refPK = refPane.getRefPK();
			String[] pk_orgs = BXUiUtil.getPermissionOrgs(billForm.getContext().getNodeCode());
			refPane.getRefModel().setFilterPks(pk_orgs);
			List<String> list = Arrays.asList(pk_orgs);
			if(list.contains(refPK)){
				refPane.setPK(refPK);
			}else{
				refPane.setPK(null);
			}
		}else */
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
		return true;
	}
	
	// 用申请单位过滤部门
	private void beforeEditDept(BillItem item){
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		((UIRefPane) item.getComponent()).setPk_org(pk_org);
	}
	
	//过滤申请人
	private void beforeEditBillMaker(BillItem item) {
		String pk_org = billForm.getHeadItemStrValue(MatterAppVO.PK_ORG);
		AbstractRefTreeModel model = (AbstractRefTreeModel)((UIRefPane) item.getComponent()).getRefModel();
		model.setPk_org(pk_org);
		
		String applyDept = billForm.getHeadItemStrValue(MatterAppVO.APPLY_DEPT);
		
		if(applyDept != null){
			model.setWherePart("pk_dept = '" + applyDept + "' ");
			model.setClassWherePart(DeptVO.PK_DEPT  + "='" + applyDept + "'");
		}else{
			model.setWherePart(null);
			model.setClassWherePart(null);
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
