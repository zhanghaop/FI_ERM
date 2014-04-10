package nc.ui.erm.accruedexpense.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

public class AccAccessoryAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public AccAccessoryAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.FILE);
	}

	public void showDocument() throws BusinessException {

		if (getModel().getSelectedData() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
					"UPP2006030102-000168")/*
											 * @res "请选择要进行文档管理的单据"
											 */);
		}

		showModal((AccruedVO) ((AggAccruedBillVO) getModel().getSelectedData()).getParentVO());
	}

	private void showModal(AccruedVO headvo) {
		UIDialog dlg = new UIDialog(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2006030102", "UPP2006030102-001119")/* @res "文档管理" */);
		dlg.getContentPane().setLayout(new BorderLayout());
		FileManageUI manageUI = new FileManageUI(headvo.getPrimaryKey());
		manageUI.setTreeRootVisible(false);
		dlg.getContentPane().add(manageUI, BorderLayout.CENTER);
		dlg.setResizable(true);
		dlg.setSize(600, 400);
		boolean isEdit = false;
		if (getModel().getUiState() == UIState.EDIT) {
			isEdit = true;
		}
		updateButton(manageUI, isEdit);
		dlg.showModal();
	}

	private void updateButton(FileManageUI manageUI, boolean isEdit) {
		manageUI.setCreateNewFolderEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
		manageUI.setUploadFileEnable(isEdit);
	}

	@Override
	protected boolean isActionEnable() {
		return this.model.getUiState() == UIState.NOT_EDIT && this.model.getSelectedData() != null;
	}

	public void doAction(ActionEvent e) throws Exception {
		showDocument();
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
}
