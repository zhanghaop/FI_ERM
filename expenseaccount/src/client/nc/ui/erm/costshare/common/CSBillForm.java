package nc.ui.erm.costshare.common;

import java.awt.event.ActionEvent;

import nc.ui.erm.costshare.ui.CsDetailCardDecimalListener;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.bill.UITabbedPaneUI;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.UIStateChangeEvent;
import nc.ui.uif2.components.AutoShowUpEventSource;
import nc.ui.uif2.components.CommonConfirmDialogUtils;
import nc.ui.uif2.components.IAutoShowUpComponent;
import nc.ui.uif2.components.IAutoShowUpEventListener;
import nc.ui.uif2.components.ITabbedPaneAwareComponent;
import nc.ui.uif2.components.ITabbedPaneAwareComponentListener;
import nc.ui.uif2.components.TabbedPaneAwareCompnonetDelegate;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.erm.costshare.CShareDetailVO;

/**
 * @author luolch
 * 
 */

public class CSBillForm extends BillForm implements ITabbedPaneAwareComponent, IAutoShowUpComponent {

	private boolean tabSingleShow = false;
	private static final long serialVersionUID = 1L;
	private TabbedPaneAwareCompnonetDelegate tabbedPaneAwareComponent;
	private  AutoShowUpEventSource autoShowComponent;
	private IExceptionHandler exceptionHandler;
	private NCAction saveAction;
	private NCAction saveAddAction;
	private NCAction cancelAction;
	private boolean canShowMeUp = true;

	public void initUI() {
		super.initUI();
		// µ¥Ò³Ç©Ê±£¬ÊÇ·ñÏÔÊ¾Ò³Ç©Ãû¡£Ä¬ÈÏÎªfalse¡£
		billCardPanel.getBodyTabbedPane().setUI(new UITabbedPaneUI(tabSingleShow));
		tabbedPaneAwareComponent = new TabbedPaneAwareCompnonetDelegate();
		autoShowComponent=new AutoShowUpEventSource(this);
		
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.BBHL}, CsDetailCardDecimalListener.RATE_TYPE_LOCAL);
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GROUPBBHL}, CsDetailCardDecimalListener.RATE_TYPE_GROUP);
		new CsDetailCardDecimalListener(getBillCardPanel().getBillModel(), CShareDetailVO.ASSUME_ORG, new String[]{CShareDetailVO.GLOBALBBHL}, CsDetailCardDecimalListener.RATE_TYPE_GLOBAL);
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SHOW_EDITOR.equals(event.getType())||isEditAppEvent(event)) {
			showMeUp();
		}
	}

	public boolean isEditAppEvent(AppEvent event) {
		return event instanceof UIStateChangeEvent&&(((UIStateChangeEvent)event).getNewState()==UIState.ADD ||((UIStateChangeEvent)event).getNewState()==UIState.EDIT);
	}

	public boolean isTabSingleShow() {
		return tabSingleShow;
	}

	public void setTabSingleShow(boolean tabSingleShow) {
		this.tabSingleShow = tabSingleShow;
	}

	@Override
	public void addTabbedPaneAwareComponentListener(ITabbedPaneAwareComponentListener l) {
		tabbedPaneAwareComponent.addTabbedPaneAwareComponentListener(l);
	}

	@Override
	public boolean canBeHidden() {
		boolean agreeHidden = true;
		try {
			if (getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT) {
				int dlg = CommonConfirmDialogUtils.showConfirmSaveDialog(getModel().getContext()
						.getEntranceUI());
				if (dlg == MessageDialog.ID_YES) {
					getSaveAction().doAction(new ActionEvent(getSaveAction(), 0, null));
				} else if (dlg == MessageDialog.ID_NO) {
					getCancelAction().doAction(new ActionEvent(getCancelAction(), 0, null));
				} else if (dlg == MessageDialog.ID_CANCEL) {
					agreeHidden = false;
				}
			}
		} catch (Exception ex) {
			exceptionHandler.handlerExeption(ex);
			agreeHidden = false;
		}
		return agreeHidden;
	}

	@Override
	public boolean isComponentVisible() {
		return tabbedPaneAwareComponent.isComponentVisible();
	}

	@Override
	public void setComponentVisible(boolean visible) {
		tabbedPaneAwareComponent.setComponentVisible(visible);
	}

	public NCAction getSaveAction() {
		return saveAction;
	}

	public void setSaveAction(NCAction saveAction) {
		this.saveAction = saveAction;
	}

	public NCAction getSaveAddAction() {
		return saveAddAction;
	}

	public void setSaveAddAction(NCAction saveAddAction) {
		this.saveAddAction = saveAddAction;
	}

	public NCAction getCancelAction() {
		return cancelAction;
	}

	public void setCancelAction(NCAction cancelAction) {
		this.cancelAction = cancelAction;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	
	@Override
	public void setAutoShowUpEventListener(IAutoShowUpEventListener l) {
		//×¢²á¼àÌýÆ÷
		autoShowComponent.setAutoShowUpEventListener(l);
	}

	@Override
	public void showMeUp() {
		if (canShowMeUp) {
			autoShowComponent.showMeUp();
		}
	}

	public AutoShowUpEventSource getAutoShowComponent() {
		return autoShowComponent;
	}

	public void setAutoShowComponent(AutoShowUpEventSource autoShowComponent) {
		this.autoShowComponent = autoShowComponent;
	}

	public void setCanShowMeUp(boolean canShowMeUp) {
		this.canShowMeUp = canShowMeUp;
	}

	public boolean isCanShowMeUp() {
		return canShowMeUp;
	}
}
