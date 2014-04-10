package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import nc.bs.uif2.IActionCode;
import nc.ui.ml.NCLangRes;
import nc.ui.uif2.AbstractShowMsgExceptionHandler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.actions.batch.BatchAddLineAction;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.billtype.BilltypeVO;

public class AddFldAction extends BatchAddLineAction {

	private static final long serialVersionUID = 1L;

	private HierachicalDataAppModel treeModel;
	
	public AddFldAction() {
		//控制维度新增按钮快捷键：Alt+I
		String addStr = NCLangRes.getInstance().getStrByID("uif2", "BatchAddLineAction-000000")/*新增*/;
		ActionInitializer.initializeAction(this, IActionCode.ADDLINE);
		setBtnName(addStr);
		setCode(IActionCode.ADD); 
		putValue(Action.SHORT_DESCRIPTION, addStr + "(Alt+I)");
		putValue(Action.ACCELERATOR_KEY,KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.ALT_MASK));
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getUiState() != UIState.DISABLE;
	}

	@Override
	protected void setDefaultData(Object obj) {
		if (obj != null) {
			MtappCtrlfieldVO vo = (MtappCtrlfieldVO) obj;
			vo.setPk_group(getTreeModel().getContext().getPk_group());
			vo.setPk_org(getTreeModel().getContext().getPk_org());
			vo.setPk_tradetype(((BilltypeVO) getTreeModel().getSelectedData()).getPk_billtypecode());
		}
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
//		MaCtrlSchemaChecker.checkOperation(getTreeModel());
		super.doAction(e);
	}

	/**
	 * 调整action的错误提示信息
	 */
	protected void processExceptionHandler(Exception ex) {
		if (!(exceptionHandler instanceof AbstractShowMsgExceptionHandler))
			exceptionHandler.handlerExeption(ex);
		else {
			AbstractShowMsgExceptionHandler dhandler = (AbstractShowMsgExceptionHandler) exceptionHandler;
			dhandler.setErrormsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("ersetting_0","02011001-0029")/*@res "新增失败!"*/);
			dhandler.handlerExeption(ex);
		}
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
	}

}
