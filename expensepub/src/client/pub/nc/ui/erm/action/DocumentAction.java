package nc.ui.erm.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.jdbc.framework.generator.IdGenerator;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.costshare.ui.CostShareEditor;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.filesystem.FileManageUI;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

@SuppressWarnings("serial")
public class DocumentAction extends NCAction{
	
	private BillForm editor;

	private BillListView listView;

	private BillManageModel model;
	public DocumentAction() {
		super();
		setCode("Document");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0054")/*@res "附件管理"*/);
	}
	
	public void showDocument() throws BusinessException {
		AggregatedValueObject selectVo = getSelectedOneAggVO();
		if (selectVo == null) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
					"UPP2006030102-000168")/*
											 * @res "请选择要进行文档管理的单据"
											 */);
		}
		
		String rootDirStr = null;
		
		if (selectVo.getParentVO().getPrimaryKey() == null) {
			String pk = getOID();
			setHeadItemPK(editor, pk);
			rootDirStr = pk;
		} else {
			rootDirStr = selectVo.getParentVO().getPrimaryKey();
		}

		showModal(rootDirStr);
	}

	private void showModal(String rootDirStr) {
		UIDialog dlg = new UIDialog(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
				.getStrByID("2006030102", "UPP2006030102-001119")/* @res "文档管理" */);
		dlg.getContentPane().setLayout(new BorderLayout());
		FileManageUI manageUI = new FileManageUI(rootDirStr);
		manageUI.setTreeRootVisible(false);
		dlg.getContentPane().add(manageUI, BorderLayout.CENTER);
		dlg.setResizable(true);
		dlg.setSize(600, 400);
		boolean isEdit = false;
		if (getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD) {
			isEdit = true;
		}
		updateButton(manageUI, isEdit);
		dlg.showModal();
	}

	private void updateButton(FileManageUI manageUI,boolean isEdit) {
		manageUI.setCreateNewFolderEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
		manageUI.setUploadFileEnable(isEdit);
		manageUI.setDeleteNodeEnable(isEdit);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() == UIState.EDIT || getModel().getUiState() == UIState.ADD) {
			return true;
		}else if(getModel().getSelectedData() != null){
			return true;
		}
		return false;  
	}


	public void doAction(ActionEvent e) throws Exception {
		showDocument();
	}
	
	/**
	 * @param editor
	 * @param pk
	 * @throws BusinessException
	 */
	private void setHeadItemPK(BillForm editor, String pk) throws BusinessException {

		BillCardPanel panel = editor.getBillCardPanel();
		String pkName = null;
		
		if (editor instanceof MatterAppMNBillForm) {
			pkName = MatterAppVO.PK_MTAPP_BILL;
		} else if (editor instanceof CostShareEditor) {
			pkName = CostShareVO.PK_COSTSHARE;
		} else if(editor instanceof ErmBillBillForm){
			pkName = JKBXHeaderVO.PK_JKBX;
		}
		
		BillItem billItem = panel.getHeadItem(pkName);
		if (billItem == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("3607mng_0",
					"03607mng-0067")/*
									 * @ res "单据模板需要配置主表ID"
									 */);
		}
		billItem.setValue(pk);
	}
	
	/*
	 * 获取选择VOs的聚合aggvos
	 * 
	 * @return
	 */
	private AggregatedValueObject getSelectedOneAggVO() throws BusinessException {
		Object value = null;
		// 判断显示什么界面
		if (listView != null && listView.isShowing()) {
			value = getModel().getSelectedData();
			if (value == null) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
						"UPP2006030102-000168")/*
												 * @res "请选择要进行文档管理的单据"
												 */);
			}
		} else if (editor.isShowing()) {
			value = ((BillForm) editor).getValue() == null ? ((BillForm) editor).getModel().getSelectedData()
					: ((BillForm) editor).getValue();
		}

		if (null == value) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
					"UPP2006030102-000168")/*
											 * @res "请选择要进行文档管理的单据"
											 */);
		}
		AggregatedValueObject agg = (AggregatedValueObject) value;
		return agg;
	}
	
	/**
	 * 分配一个ID
	 * 
	 * @return 分配的ID
	 */
	private String getOID() {
		IdGenerator idGenerator = NCLocator.getInstance().lookup(IdGenerator.class);
		return idGenerator.generate();
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}
	public BillForm getEditor() {
		return editor;
	}
	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	public BillListView getListView() {
		return listView;
	}
	public void setListView(BillListView listView) {
		this.listView = listView;
	}
}