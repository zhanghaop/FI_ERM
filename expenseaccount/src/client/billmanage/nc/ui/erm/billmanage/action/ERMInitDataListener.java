package nc.ui.erm.billmanage.action;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.action.INCAction;
import nc.funcnode.ui.action.SeparatorAction;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.pub.link.ILinkQueryDataPlural;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.actions.StandAloneToftPanelActionContainer;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultFuncNodeInitDataListener;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.dj.ERMDjCondVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;

import org.apache.commons.collections.CollectionUtils;

public class ERMInitDataListener extends DefaultFuncNodeInitDataListener {

	private BillManageModel model = null;

	private ErmBillBillForm card;

	private ERMBillListView listView;

	private IExceptionHandler exceptionHandler;

	private StandAloneToftPanelActionContainer listViewActions;

	private StandAloneToftPanelActionContainer editorActions;

	@Override
	public void initData(FuncletInitData data) {
		if (data == null) {
			super.initData(data);
		} else {
			setData2UI(data);
			
			if(!(data.getInitData() instanceof SimpleQuerySchemeVO)){
				initActions(listViewActions.getActions(), data);
				listViewActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
				initActions(editorActions.getActions(), data);
				editorActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			}
		}
	}

	private void setData2UI(FuncletInitData data) {
		boolean bMultiRow = false;
		String[] pk_jkbx = null;
		if (data.getInitData() instanceof ILinkQueryDataPlural) {
			// 总账联查单据,费用结转，待摊摊销（联查单据）
			pk_jkbx = ((ILinkQueryDataPlural) data.getInitData()).getBillIDs();
			if (pk_jkbx != null && pk_jkbx.length > 1) {
				bMultiRow = true;
			}
		} else if (data.getInitData() instanceof PfLinkData) {
			pk_jkbx = new String[] { ((PfLinkData) data.getInitData()).getBillID() };
		} else if (data.getInitData() instanceof SimpleQuerySchemeVO) {
			// 从快捷方式
			super.initData(data);
			return;
		} else {
			pk_jkbx = new String[] { ((ILinkQueryData) data.getInitData()).getBillID() };
		}
		try {
			ErmBillBillManageModel ermBillBillManageModel = (ErmBillBillManageModel) getModel();
			ERMDjCondVO djCondVO = null;
			if (ermBillBillManageModel != null && ermBillBillManageModel.getDjCondVO() != null) {
				djCondVO = ermBillBillManageModel.getDjCondVO();
			}
			List<JKBXVO> vos = ((IBXBillPrivate) NCLocator.getInstance().lookup(IBXBillPrivate.class.getName()))
					.queryHeadVOsByPrimaryKeys(pk_jkbx, null, false, djCondVO);
			card.getModel().initModel(vos.toArray());
			if (bMultiRow) {
				listView.showMeUp();
			} else {
				card.showMeUp();
			}
		} catch (Exception e) {
			ExceptionHandler.handleRuntimeException(e);
		}
	}

	private void initActions(List<Action> actionList, FuncletInitData data) {
		if (!CollectionUtils.isEmpty(actionList)) {
			List<Action> actionListNew = new ArrayList<Action>();
			List<String> linkQueryShowActions = getLinkQueryShowActions(data);
			for (Action action : actionList) {
				Object actionCode = action.getValue(INCAction.CODE);
				if ((actionCode != null) && (linkQueryShowActions.contains(actionCode.toString()))) {
					actionListNew.add(action);
				} else if (action instanceof SeparatorAction) {// 增加按钮的分隔符
					actionListNew.add(action);
				}
			}
			actionList.clear();
			actionList.addAll(actionListNew);
		}
	}

	public List<String> getLinkQueryShowActions(FuncletInitData data) {
		List<String> showActionCodes = new ArrayList<String>();
		showActionCodes.add("Document");
		showActionCodes.add("联查"/* -=notranslate=- */);
		showActionCodes.add(IActionCode.PRINT);
		if (data.getInitData() instanceof PfLinkData) {
			showActionCodes.add(IActionCode.EDIT);
			showActionCodes.add(IActionCode.APPROVE);
			showActionCodes.add("Bill");
		}
		return showActionCodes;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public ERMBillListView getListView() {
		return listView;
	}

	public void setListView(ERMBillListView listView) {
		this.listView = listView;
	}

	public StandAloneToftPanelActionContainer getListViewActions() {
		return listViewActions;
	}

	public void setListViewActions(StandAloneToftPanelActionContainer listViewActions) {
		this.listViewActions = listViewActions;
	}

	public StandAloneToftPanelActionContainer getEditorActions() {
		return editorActions;
	}

	public void setEditorActions(StandAloneToftPanelActionContainer editorActions) {
		this.editorActions = editorActions;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
	}

	public ErmBillBillForm getCard() {
		return card;
	}

	public void setCard(ErmBillBillForm card) {
		this.card = card;
	}

	public IExceptionHandler getExceptionHandler() {
		if (exceptionHandler == null) {
			exceptionHandler = new DefaultExceptionHanler();
		}
		return exceptionHandler;
	}
}
