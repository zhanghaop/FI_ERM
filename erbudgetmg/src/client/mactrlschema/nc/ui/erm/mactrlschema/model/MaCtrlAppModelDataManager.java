package nc.ui.erm.mactrlschema.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldQuery;
import nc.ui.erm.mactrlschema.view.MaCtrlOrgPanel;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.UIState;
import nc.ui.uif2.UIStateChangeEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.HierachicalDataAppModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

public class MaCtrlAppModelDataManager implements IAppModelDataManager {

	private BatchBillTableModel ctrlfieldtableModel;
	private BatchBillTableModel ctrlbilltableModel;
	private HierachicalDataAppModel treeModel;
	private MaCtrlOrgPanel orgPanel;

	private DefaultExceptionHanler exceptionHandler;

	public void initModel() {
		//先初始化数据，再根据数据初始化模型间的状态，注意两个方法调用顺序
		initData();
		initModelState();
	}

	private void initModelState() {
		if (getOrgPanel().isComponentDisplayable() && getOrgPanel().getRefPane().getRefPK() == null)  {
			getTreeModel().setUiState(UIState.DISABLE);
			getCtrlfieldtableModel().setUiState(UIState.DISABLE);
			getCtrlbilltableModel().setUiState(UIState.DISABLE);
		} else {
			getTreeModel().setUiState(UIState.NOT_EDIT);
			if (getTreeModel().getSelectedData() == null) {
				getCtrlfieldtableModel().setUiState(UIState.DISABLE);
				getCtrlbilltableModel().setUiState(UIState.DISABLE);
			} else {
				getCtrlfieldtableModel().setUiState(UIState.NOT_EDIT);
				getCtrlbilltableModel().setUiState(UIState.NOT_EDIT);
			}
		}
	}

	private void initData() {
		clearAllModel();
		String tradeType = null;
		String pk_org = getTreeModel().getContext().getPk_org();
		if (getTreeModel().getSelectedData() != null) {
			tradeType = ((BilltypeVO) getTreeModel().getSelectedData()).getPk_billtypecode();
		}
		if (tradeType == null || pk_org == null) {
			return;
		}
		IErmMappCtrlBillQuery ibillquery = (IErmMappCtrlBillQuery) NCLocator.getInstance()
				.lookup(IErmMappCtrlBillQuery.class);
		IErmMappCtrlFieldQuery ifieldquery = (IErmMappCtrlFieldQuery) NCLocator.getInstance()
				.lookup(IErmMappCtrlFieldQuery.class);
		try {
			MtappCtrlbillVO[] billrs = ibillquery.queryCtrlBillVos(pk_org, tradeType);
			MtappCtrlfieldVO[] fieldrs = ifieldquery.queryCtrlFieldVos(pk_org, tradeType);
			getCtrlbilltableModel().initModel(billrs);
			getCtrlfieldtableModel().initModel(fieldrs);
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}

	}

	private void clearAllModel() {
		getCtrlfieldtableModel().initModel(null);
		getCtrlbilltableModel().initModel(null);
	}

	public HierachicalDataAppModel getTreeModel() {
		return treeModel;
	}

	public void setTreeModel(HierachicalDataAppModel treeModel) {
		this.treeModel = treeModel;
		this.treeModel.addAppEventListener(new AppEventListener() {

			@Override
			public void handleEvent(AppEvent event) {
				if (event.getType().equals(AppEventConst.SELECTION_CHANGED)) {
					initModel();
				}
			}
		});
	}

	public BatchBillTableModel getCtrlfieldtableModel() {
		return ctrlfieldtableModel;
	}

	public void setCtrlfieldtableModel(BatchBillTableModel ctrlfieldtableModel) {
		this.ctrlfieldtableModel = ctrlfieldtableModel;
		this.ctrlfieldtableModel.addAppEventListener(new AppEventListener() {

			@Override
			public void handleEvent(AppEvent event) {
				if ( (event instanceof UIStateChangeEvent) && (event.getType().equals(AppEventConst.UISTATE_CHANGED))) {
					UIStateChangeEvent e = (UIStateChangeEvent) event;
					if (e.getNewState() != e.getOldState()) {
						if (getCtrlfieldtableModel().getUiState() == UIState.EDIT) {
							getOrgPanel().getRefPane().setEnabled(false);
							getTreeModel().setUiState(UIState.DISABLE);
							getCtrlbilltableModel().setUiState(UIState.DISABLE);
						} else if (getCtrlfieldtableModel().getUiState() != UIState.DISABLE) {
							getOrgPanel().getRefPane().setEnabled(true);
							getTreeModel().setUiState(UIState.NOT_EDIT);
							getCtrlbilltableModel().setUiState(UIState.NOT_EDIT);
						}
					}
				}
			}
		});
	}

	public BatchBillTableModel getCtrlbilltableModel() {
		return ctrlbilltableModel;
	}

	public void setCtrlbilltableModel(BatchBillTableModel ctrlbilltableModel) {
		this.ctrlbilltableModel = ctrlbilltableModel;
		this.ctrlbilltableModel.addAppEventListener(new AppEventListener() {

			@Override
			public void handleEvent(AppEvent event) {
				if ( (event instanceof UIStateChangeEvent) && (event.getType().equals(AppEventConst.UISTATE_CHANGED))) {
					UIStateChangeEvent e = (UIStateChangeEvent) event;
					if (e.getNewState() != e.getOldState()) {
						if (getCtrlbilltableModel().getUiState() == UIState.EDIT) {
							getOrgPanel().getRefPane().setEnabled(false);
							getTreeModel().setUiState(UIState.DISABLE);
							getCtrlfieldtableModel().setUiState(UIState.DISABLE);
						} else if (getCtrlbilltableModel().getUiState() != UIState.DISABLE) {
							getOrgPanel().getRefPane().setEnabled(true);
							getTreeModel().setUiState(UIState.NOT_EDIT);
							getCtrlfieldtableModel().setUiState(UIState.NOT_EDIT);
						}
					}
				}
			}
		});
	}

	public MaCtrlOrgPanel getOrgPanel() {
		return orgPanel;
	}

	public void setOrgPanel(MaCtrlOrgPanel orgPanel) {
		this.orgPanel = orgPanel;
		this.orgPanel.addAppEventListener(new AppEventListener() {

			@Override
			public void handleEvent(AppEvent event) {
				if (event.getType().equals(AppEventConst.UISTATE_CHANGED)) {
					initModelState();
				}
			}
		});
	}

	public DefaultExceptionHanler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(DefaultExceptionHanler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
