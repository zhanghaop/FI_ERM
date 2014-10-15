package nc.ui.erm.fieldcontrast.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.ui.uif2.model.AbstractUIAppModel;

public class FieldctrstFunNodeClosingHandler implements IFunNodeClosingListener {

	private AbstractUIAppModel fieldContrastModel;
	private NCAction saveFieldctrstAction;
	private NCAction cancelFieldctrstAction;

	private DefaultExceptionHanler exceptionHandler;

	public boolean canBeClosed() {
		if (fieldContrastModel.getUiState() == UIState.ADD || fieldContrastModel.getUiState() == UIState.EDIT) {
			return doAction(fieldContrastModel, saveFieldctrstAction, cancelFieldctrstAction);
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

	public AbstractUIAppModel getFieldContrastModel() {
		return fieldContrastModel;
	}

	public void setFieldContrastModel(AbstractUIAppModel fieldContrastModel) {
		this.fieldContrastModel = fieldContrastModel;
	}

	public NCAction getSaveFieldctrstAction() {
		return saveFieldctrstAction;
	}

	public void setSaveFieldctrstAction(NCAction saveFieldctrstAction) {
		this.saveFieldctrstAction = saveFieldctrstAction;
	}

	public NCAction getCancelFieldctrstAction() {
		return cancelFieldctrstAction;
	}

	public void setCancelFieldctrstAction(NCAction cancelFieldctrstAction) {
		this.cancelFieldctrstAction = cancelFieldctrstAction;
	}

	public DefaultExceptionHanler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(DefaultExceptionHanler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
