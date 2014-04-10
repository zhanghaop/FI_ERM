package nc.ui.erm.costshare.actions;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.bs.uif2.IActionCode;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class AccessoryAction extends NCAction{
	private BillManageModel model;
	public  AccessoryAction(){
		super();
		ActionInitializer.initializeAction(this, IActionCode.FILE);
	}
	public void showDocument() throws BusinessException {

		if (getModel().getSelectedData()==null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2006030102", "UPP2006030102-000168")/*
																		 * @res
																		 * "请选择要进行文档管理的单据"
																		 */);
		}

		showModal((CostShareVO) ((AggCostShareVO)getModel().getSelectedData()).getParentVO());
	}
	private void showModal(CostShareVO headvo){
		UIDialog dlg = new UIDialog(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102","UPP2006030102-001119")/*@res "文档管理"*/);
		dlg.getContentPane().setLayout(new BorderLayout());
		FileManageUI manageUI = new FileManageUI(headvo.getPrimaryKey());
		manageUI.setTreeRootVisible(false);
		dlg.getContentPane().add(manageUI, BorderLayout.CENTER);
		dlg.setResizable(true);
		dlg.setSize(600, 400);
		// 控制附件管理中的可编辑按钮，只有自制及未生效时才可进行上传文件等
		//boolean isEdit = headvo.getSrc_type()==IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL&&headvo.getEffectstate() == IErmCostShareConst.CostShare_Bill_Effectstate_N;
		updateButton(manageUI,false);
		dlg.showModal();
	}
	
	private void updateButton(FileManageUI manageUI,boolean isEdit) {
		manageUI.setCreateNewFolderEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
		manageUI.setUploadFileEnable(isEdit);
	}
	
	@Override
	protected boolean isActionEnable() {
		return model.getUiState()== UIState.NOT_EDIT&& getModel().getSelectedData()!=null;
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
