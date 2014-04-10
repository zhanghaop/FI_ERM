package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.IEditor;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.uif2.LoginContext;

/**
 * @author chenshuaia
 *　
 *	修改Action　
 *
 */
@SuppressWarnings("serial")
public class UpdateAction extends nc.ui.uif2.actions.EditAction {
	private IEditor editor;
	
	private LoginContext context;

	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO aggVo = (AggMatterAppVO)getModel().getSelectedData();
		MatterAppVO parentVo = aggVo.getParentVO();
		
		checkUpdate(parentVo);
		
		super.doAction(e);
	}

	private void checkUpdate(MatterAppVO parentVo) throws BusinessException {
		Integer spzt = parentVo.getApprstatus();
		if (spzt != null && ((spzt.equals(IPfRetCheckInfo.GOINGON)) || (spzt.equals(IPfRetCheckInfo.COMMIT)))) {
			String userId = ErUiUtil.getPk_user();
			String billId = parentVo.getPk_mtapp_bill();
			String billType = parentVo.getPk_tradetype();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// 启动了审批流后
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(parentVo.getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "请取消审批再修改！"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
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
		if(getModel().getUiState()!=UIState.NOT_EDIT || getModel().getSelectedData()==null){
			return false;
		}
		
		AggMatterAppVO aggVo = (AggMatterAppVO)getModel().getSelectedData();
		MatterAppVO parentVo = aggVo.getParentVO();
		
		Integer spzt = parentVo.getApprstatus();
		if(spzt.equals(IPfRetCheckInfo.PASSING) || spzt.equals(IPfRetCheckInfo.NOPASS)){
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = this.getBtnName() + ErmActionConst.FAIL_MSG;
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(null);
	}

	public BillCardPanel getBillCardPanel() {
		return ((BillForm)getEditor()).getBillCardPanel();
	}
	
	public void setEditor(IEditor editor) {
		this.editor = editor;
	}

	public IEditor getEditor() {
		return editor;
	}

	public LoginContext getContext() {
		return context;
	}

	public void setContext(LoginContext context) {
		this.context = context;
	}
}