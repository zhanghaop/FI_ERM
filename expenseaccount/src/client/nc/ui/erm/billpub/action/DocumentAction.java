package nc.ui.erm.billpub.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

/**
 *
 * @author wangled
 *
 */
public class DocumentAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	public DocumentAction() {
		super();
		setCode("Document");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0054")/*@res "附件管理"*/);
	}
	
	
	@Override
	protected boolean isActionEnable() {
		Object selectedData = getModel().getSelectedData();
		return selectedData != null && ((JKBXVO)selectedData).getParentVO().getDjzt() != BXStatusConst.DJZT_Invalid;
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