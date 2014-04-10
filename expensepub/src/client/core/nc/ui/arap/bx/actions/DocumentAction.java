package nc.ui.arap.bx.actions;

import java.awt.BorderLayout;

import nc.ui.er.pub.BillWorkPageConst;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 * 
 *         nc.ui.arap.bx.actions.DocumentAction
 */
public class DocumentAction extends BXDefaultAction {
	public void showDocument() throws BusinessException {
		boolean isEdit = getMainPanel().getCurrentPageStatus() == BillWorkPageConst.WORKSTAT_EDIT;

		if (getCurrentSelectedVO() == null
				|| getCurrentSelectedVO().getParentVO() == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006030102", "UPP2006030102-000168")/*
																	 * @res
																	 * "请选择要进行文档管理的单据"
																	 */);
		}

		showModal(getCurrentSelectedVO().getParentVO().getPrimaryKey(), isEdit);
	}

	private void showModal(String headpk, boolean isEdit) {
		UIDialog dlg = new UIDialog(getParent(), nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("2006030102",
						"UPPbusinessbill-000230")/* @res "文档管理" */);
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
}