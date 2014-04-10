package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;

public class UnPrintOfficialAction extends NCAction{

	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private ErmBillBillForm editor;

	public UnPrintOfficialAction() {
		super();
		putValue(Action.ACCELERATOR_KEY, null);
		setBtnName(ErmActionConst.getCancelprintName());
		setCode(ErmActionConst.CANCELPRINT);
		putValue(SHORT_DESCRIPTION, ErmActionConst.getCancelprintName());
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selBxvos = (JKBXVO) getModel().getSelectedData();
		JKBXHeaderVO head = (JKBXHeaderVO) selBxvos.getParentVO().clone();
		head.setOfficialprintdate(null);
		head.setOfficialprintuser(null);
		head = getIBXBillPublic().updateHeader(head,
				new String[] { JKBXHeaderVO.OFFICIALPRINTDATE,JKBXHeaderVO.OFFICIALPRINTUSER });
		selBxvos.setParentVO(head);
		editor.getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTDATE,
				"");
		editor.getBillCardPanel().setHeadItem(JKBXHeaderVO.OFFICIALPRINTUSER,
				"");
		editor.getBillCardPanel().setHeadItem(JKBXHeaderVO.TS,
				selBxvos.getParentVO().getTs());
		
		((ErmBillBillManageModel)getModel()).directlyUpdate(selBxvos);
		
		ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl
				.getNCLangRes().getStrByID("2011", "UPP2011-000415")/** @res* "取消正式打印成功!"*/, getModel().getContext());
	}

	protected IBXBillPublic getIBXBillPublic() throws ComponentException {
		return NCLocator.getInstance().lookup(IBXBillPublic.class);
	}

	protected boolean isActionEnable() {
		return getModel().getUiState() != UIState.EDIT && getModel().getSelectedData() != null;
	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}

}