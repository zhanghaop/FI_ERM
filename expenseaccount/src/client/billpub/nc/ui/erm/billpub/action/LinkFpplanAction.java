package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;

public class LinkFpplanAction extends LinkYsAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	
	public LinkFpplanAction(){
		super();
		setCode("LinkFpplan");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000356")/*@res "联查资金计划"*/);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);

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
	@Override
	public String getNoResultMsg() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0113")/*
																										 * @
																										 * res
																										 * "没有符合条件的资金计划!"
																										 */;
	}

	@Override
	public String getNoInstallMsg() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0114")/*
																										 * @
																										 * res
																										 * "没有安装预算产品，不能联查资金计划执行情况！"
																										 */;
	}
	
}
