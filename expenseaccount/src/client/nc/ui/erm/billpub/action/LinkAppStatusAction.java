package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.pubitf.para.SysInitQuery;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.wfengine.definition.WorkflowTypeEnum;

public class LinkAppStatusAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	public LinkAppStatusAction() {
		super();
		setCode(ErmActionConst.LINKAPPSTATUS);
		setBtnName(ErmActionConst.getLinkAppStatusName());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		String pk_jkbx = selectedVO.getParentVO().getPk_jkbx();
		String djlxbm = selectedVO.getParentVO().getDjlxbm();

		FlowStateDlg app = new FlowStateDlg(getEditor(), djlxbm, pk_jkbx, getWorkFlowType(selectedVO.getParentVO().getPk_org()));
		app.showModal();
	}

	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null) {
			return false;
		}
		return true;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);

	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	/**
	 * ��ȡ�����ű�����
	 * 
	 * @param pk_org
	 * @return
	 */
	protected int getWorkFlowType(String pk_org) {
		int flowtype = WorkflowTypeEnum.Approveflow.getIntValue();
		try {
			String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
			if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {
				flowtype = WorkflowTypeEnum.Workflow.getIntValue();
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return flowtype;
	}
}
