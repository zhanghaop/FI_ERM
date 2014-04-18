package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.erm.costshare.ui.CsBarCodeDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IQueryAndRefreshManager;

/**
 * @author luolch
 *
 * 条码输入
 *
 */
@SuppressWarnings("serial")
public class CodeBarAction extends NCAction {
	private BillManageModel model;
	private IQueryAndRefreshManager dataManagerEx;
	private boolean isCard;
	public CodeBarAction() {
		super();
		setBtnName(ErmActionConst.getCodeImportName());
		setCode(ErmActionConst.CODEIMPORT);
	}
	public void doAction(ActionEvent e) throws Exception {
		getCsBarCodeDialog().showModal();
	}
	
	private UIDialog dialog;
	/**
	 * 条码输入对话框
	 * @return
	 */
	private UIDialog getCsBarCodeDialog() {
		if (dialog == null) {
			dialog = new CsBarCodeDialog(model.getContext(),dataManagerEx,isCard(),model);
		}
		return dialog;
	}

	@Override
	protected boolean isActionEnable() {
		return model.getUiState()== UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	public BillManageModel getModel() {
		return model;
	}
	public void setDataManagerEx(IQueryAndRefreshManager dataManagerEx) {
		this.dataManagerEx = dataManagerEx;
	}
	public IQueryAndRefreshManager getDataManagerEx() {
		return dataManagerEx;
	}
	public void setCard(boolean isCard) {
		this.isCard = isCard;
	}
	public boolean isCard() {
		return isCard;
	}
}