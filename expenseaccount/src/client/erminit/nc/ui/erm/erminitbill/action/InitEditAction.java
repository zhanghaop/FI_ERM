package nc.ui.erm.erminitbill.action;

import java.awt.event.ActionEvent;

import nc.ui.erm.billpub.action.ERMBillEditAction;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class InitEditAction extends ERMBillEditAction {
	private static final long serialVersionUID = 1L;
	private ErmBillBillForm editor;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		//检查期初是否关闭
		boolean flag=checkQCClose();
		if(flag==true){
			return;
		}
		//检查期初单据
		super.doAction(e);
	}
	
	private boolean checkQCClose() throws BusinessException {
		Object selectedData = getModel().getSelectedData();
		String pkOrg = ((JKBXVO)selectedData).getParentVO().getPk_org();
		try {
			return getEditor().getHelper().checkQCClose(pkOrg);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}
		return false;

	}
	
	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}
	
}
