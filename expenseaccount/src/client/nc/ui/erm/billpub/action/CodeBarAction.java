package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.erm.billpub.view.BarCodeDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;

/**
 *
 * @author wangled
 *
 */
public class CodeBarAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;

	public CodeBarAction() {
		super();
		setCode("CodeBarInput");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0052")/*@res "Ãı¬Î ‰»Î"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		new BarCodeDialog(getModel(),getEditor().isShowing()).showModal();
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