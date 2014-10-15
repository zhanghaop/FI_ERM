package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.matterapp.AggMatterAppVO;

/**
 * @author chenshuaia
 * 查看审批意见
 */
@SuppressWarnings( { "serial" })
public class LinkApproveResultAction extends NCAction {
	private BillManageModel model;

	public LinkApproveResultAction() {
		super();
		setCode(ErmActionConst.LINKAPPSTATUS);
		setBtnName(ErmActionConst.getLinkAppStatusName());
	}

	public void doAction(ActionEvent e) throws Exception {
		AggMatterAppVO selectvo = (AggMatterAppVO) getModel().getSelectedData();
		if (selectvo == null)
			return;

		if (selectvo.getParentVO().getPk_mtapp_bill() == null)
			return;

		String pk_jkbx = selectvo.getParentVO().getPk_mtapp_bill();
		String djlxbm = selectvo.getParentVO().getPk_tradetype();

		FlowStateDlg app = new FlowStateDlg(getModel().getContext().getEntranceUI(), djlxbm, pk_jkbx, 
				ErUtil.getWorkFlowType(selectvo.getParentVO().getPk_org()));
		app.showModal();
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0}失败！"
													 */;
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(null);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
}