package nc.ui.erm.matterapp.listener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.uif2.IActionCode;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.action.INCAction;
import nc.itf.pub.link.ILinkQueryDataPlural;
import nc.ui.erm.matterapp.view.AbstractMappBillForm;
import nc.ui.erm.matterapp.view.MatterAppMNListView;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.pub.msg.PfLinkData;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.actions.StandAloneToftPanelActionContainer;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.DefaultFuncNodeInitDataListener;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

import org.apache.commons.collections.CollectionUtils;

/**
 * ��ʼ�����ܽڵ���������ڵ�Ϊ�����������ڵ�
 * @author chenshuaia
 *
 */
public class MatterAppLinkListener extends DefaultFuncNodeInitDataListener {
	private PaginationModel pageModel;
	
	private StandAloneToftPanelActionContainer listViewActions;
	private StandAloneToftPanelActionContainer editorActions;
	
	private MatterAppMNListView listView;
	
	private FuncletInitData data;
	
	@Override
	public void initData(FuncletInitData data) {
		this.data = data;
		if(data != null){
			((AbstractMappBillForm)getEditor()).setLink_type(data.getInitType());
		}
		super.initData(data);
	}
	@Override
	protected void doLinkApprove(FuncletInitData data) {
		if(data.getInitData() instanceof PfLinkData){
			PfLinkData pfData = (PfLinkData)data.getInitData();
			try {
				pageModel.setObjectPks(new String[]{pfData.getBillID()});
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			
			// �������鰴ť
			initActions(listViewActions.getActions(), getApproveNotShowActions(), false);
			initActions(editorActions.getActions(), getApproveNotShowActions(), false);
			editorActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			listViewActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			
			getEditor().showMeUp();
		}
	}

	@Override
	protected void doOther(int type) {
		if (ILinkType.LINK_TYPE_QUERY == type) {
			ILinkQueryData querydata = (ILinkQueryData)data.getInitData();
			if (querydata == null)
				return;
			String[] billIDs = null;
			if (querydata instanceof ILinkQueryDataPlural) {
				// �µ������ӿ�
				billIDs = ((ILinkQueryDataPlural) querydata).getBillIDs();
			} else {
				billIDs = new String[] { querydata.getBillID() };
			}
			
			try {
				pageModel.setObjectPks(billIDs);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			
			initActions(listViewActions.getActions(), getLinkQueryShowActions(), true);
			initActions(editorActions.getActions(), getLinkQueryShowActions(), true);
			editorActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			listViewActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			
			if(billIDs != null && billIDs.length == 1){
				getEditor().showMeUp();
			}
		}
	}
	
	/**
	 * 
	 * @param actionList �����ϵ�Action����
	 * @param actionsCodes ��Ҫ�����actioncode����
	 * @param isShow �ڶ�����������������actioncode�Ƿ���ʾ�� true��ʾ��false����ʾ
	 */
	private void initActions(List<Action> actionList, List<String> actionsCodes, boolean isShow) {
		if (!CollectionUtils.isEmpty(actionList)) {
			List<Action> actionListNew = new ArrayList<Action>();
			for (Action action : actionList) {
				String actionCode = (String)action.getValue(INCAction.CODE);
				if (isShow) {
					if (actionsCodes.contains(actionCode)) {
						actionListNew.add(action);
					}
				} else {
					if (actionCode == null || !actionsCodes.contains(actionCode)) {
						actionListNew.add(action);
					}
				}
			}
			actionList.clear();
			actionList.addAll(actionListNew);
		}
	}

	public void setPageModel(PaginationModel pageModel) {
		this.pageModel = pageModel;
	}

	public PaginationModel getPageModel() {
		return pageModel;
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

	public MatterAppMNListView getListView() {
		return listView;
	}

	public void setListView(MatterAppMNListView listView) {
		this.listView = listView;
	}

	public List<String> getApproveNotShowActions() {
		List<String> notShowActionCodes = new ArrayList<String>();
		notShowActionCodes.add(IActionCode.QUERY);
		notShowActionCodes.add(IActionCode.ADD);
		notShowActionCodes.add(IActionCode.COPY);
		notShowActionCodes.add(IActionCode.DELETE);
		// ���鳣��
		notShowActionCodes.add(ErmActionConst.BILLTYE);
		notShowActionCodes.add("commitmenu");
		notShowActionCodes.add("closemenu");
		return notShowActionCodes;
	}
	
	public List<String> getLinkQueryShowActions() {
		List<String> showActionCodes = new ArrayList<String>();
		showActionCodes.add("Refresh");
		showActionCodes.add(null);
		showActionCodes.add(ErmActionConst.DOCUMENT);
		showActionCodes.add(null);
		showActionCodes.add("link");
		showActionCodes.add(ErmActionConst.LINKBUDGET);
		showActionCodes.add(ErmActionConst.LINKAPPSTATUS);
		showActionCodes.add("BillLinkQuery");
		return showActionCodes;
	}
}
