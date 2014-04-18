package nc.ui.erm.erminitbill.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

public class AccessoryAction extends NCAction{
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	public AccessoryAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.FILE);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {

		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null || selectedData.getParentVO() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006030102", "UPP2006030102-000168")/*
																	 * @res
																	 * "请选择要进行文档管理的单据"
																	 */);
		}
		boolean isEdit = false;
		if (getModel().getUiState() == UIState.EDIT) {
			isEdit = true;
		}
		showModal(selectedData.getParentVO().getPrimaryKey(), isEdit);
	}

	private void showModal(String headpk, boolean isEdit) {
		UIDialog dlg = new UIDialog(getEditor(), nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("2006030102",
						"UPP2006030102-001119")/* @res "文档管理" */);
		dlg.getContentPane().setLayout(new BorderLayout());
		FileManageUI manageUI = new FileManageUI(headpk);
		manageUI.setTreeRootVisible(false);
		dlg.getContentPane().add(manageUI, BorderLayout.CENTER);
		dlg.setResizable(true);
		dlg.setSize(600, 400);
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
		return this.model.getUiState() == UIState.NOT_EDIT
				&& this.model.getSelectedData() != null;
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
