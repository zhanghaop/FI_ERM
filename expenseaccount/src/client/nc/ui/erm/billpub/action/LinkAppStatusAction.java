package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.pub.workflownote.FlowStateDlg;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;

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

		FlowStateDlg app = new FlowStateDlg(getEditor(), djlxbm, pk_jkbx, ErUtil.getWorkFlowType(selectedVO.getParentVO().getPk_org()));
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
}
