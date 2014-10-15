package nc.ui.erm.mactrlschema.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.ui.uif2.model.AbstractUIAppModel;

public class FunNodeClosingHandler implements IFunNodeClosingListener {

	private AbstractUIAppModel ctrlFieldTableModel;
	private AbstractUIAppModel ctrlBillTableModel;
	private NCAction saveFldAction;
	private NCAction saveBilAction;
	private NCAction cancelFldAction;
	private NCAction cancelBilAction;

	private DefaultExceptionHanler exceptionHandler;

	public boolean canBeClosed() {
		if (ctrlFieldTableModel.getUiState() == UIState.ADD || ctrlFieldTableModel.getUiState() == UIState.EDIT) {
			return doAction(ctrlFieldTableModel, saveFldAction, cancelFldAction);
		} else if (ctrlBillTableModel.getUiState() == UIState.ADD || ctrlBillTableModel.getUiState() == UIState.EDIT) {
			return doAction(ctrlBillTableModel, saveBilAction, cancelBilAction);
		} else {
			return true;
		}

	}

	private boolean doAction(AbstractUIAppModel model, NCAction saveaction, NCAction cancelaction) {

		int i = CommonConfirmDialogUtils.showConfirmSaveDialog(model.getContext().getEntranceUI());

		exceptionHandler.setContext(model.getContext());
		switch (i) {
		case UIDialog.ID_YES: {
			IExceptionHandler handler = null;
			try {
				handler = saveaction.getExceptionHandler();
				// 保存操作
				if (handler != null)
					saveaction.setExceptionHandler(null);
			} catch (Exception e) {
				exceptionHandler.handlerExeption(e);
				return false;
			}
			try {
				saveaction.actionPerformed(new ActionEvent(saveaction, 0, null));
			} catch (Exception e) {
				if (handler != null)
					handler.handlerExeption(e);
				else
					exceptionHandler.handlerExeption(e);
				return false;
			} finally {
				saveaction.setExceptionHandler(handler);
			}
			return true;
		}
		case UIDialog.ID_NO: {
			// 如注射了cancelaction，则调用取消操作，否则直接返回true
			if (cancelaction != null) {

				IExceptionHandler handler = cancelaction.getExceptionHandler();
				// 保存操作
				if (handler != null)
					cancelaction.setExceptionHandler(null);

				try {
					// 取消操作
					cancelaction.actionPerformed(new ActionEvent(cancelaction, 0, null));
				} catch (Exception e) {
					if (handler != null)
						handler.handlerExeption(e);
					else
						exceptionHandler.handlerExeption(e);
					return false;
				} finally {
					cancelaction.setExceptionHandler(handler);
				}
			}
			return true;
		}
		case UIDialog.ID_CANCEL: {
			return false;
		}

		default:
			return true;
		}

	}

	public AbstractUIAppModel getCtrlFieldTableModel() {
		return ctrlFieldTableModel;
	}

	public void setCtrlFieldTableModel(AbstractUIAppModel ctrlFieldTableModel) {
		this.ctrlFieldTableModel = ctrlFieldTableModel;
	}

	public AbstractUIAppModel getCtrlBillTableModel() {
		return ctrlBillTableModel;
	}

	public void setCtrlBillTableModel(AbstractUIAppModel ctrlBillTableModel) {
		this.ctrlBillTableModel = ctrlBillTableModel;
	}

	public NCAction getSaveFldAction() {
		return saveFldAction;
	}

	public void setSaveFldAction(NCAction saveFldAction) {
		this.saveFldAction = saveFldAction;
	}

	public NCAction getSaveBilAction() {
		return saveBilAction;
	}

	public void setSaveBilAction(NCAction saveBilAction) {
		this.saveBilAction = saveBilAction;
	}

	public NCAction getCancelFldAction() {
		return cancelFldAction;
	}

	public void setCancelFldAction(NCAction cancelFldAction) {
		this.cancelFldAction = cancelFldAction;
	}

	public NCAction getCancelBilAction() {
		return cancelBilAction;
	}

	public void setCancelBilAction(NCAction cancelBilAction) {
		this.cancelBilAction = cancelBilAction;
	}

	public DefaultExceptionHanler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(DefaultExceptionHanler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
