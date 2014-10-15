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
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.StandAloneToftPanelActionContainer;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.DefaultFuncNodeInitDataListener;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

import org.apache.commons.collections.CollectionUtils;

/**
 * 初始化功能节点监听器，节点为费用申请管理节点
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
		if(getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT){
			return;//编辑态不设置值
		}
		
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
			
			// 处理联查按钮
//			initActions(listViewActions.getActions(), getApproveNotShowActions(), false);
//			initActions(editorActions.getActions(), getApproveNotShowActions(), false);
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
			String[] billIDs = getBillIDs(querydata);
			
			try {
				pageModel.setObjectPks(billIDs);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
			
			editorActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			listViewActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			
			if(billIDs != null && billIDs.length == 1){
				getEditor().showMeUp();
			}
		}
	}
	protected String[] getBillIDs(ILinkQueryData querydata) {
		String[] billIDs = null;
		if (querydata instanceof ILinkQueryDataPlural) {
			// 新的批量接口
			billIDs = ((ILinkQueryDataPlural) querydata).getBillIDs();
		} else {
			billIDs = new String[] { querydata.getBillID() };
		}
		
		if(billIDs != null){
			for(int i = 0; i < billIDs.length; i ++){
				if(billIDs[i].endsWith("_CLOSE")){
					billIDs[i] = billIDs[i].substring(0, billIDs[i].indexOf("_"));
				}
			}
		}
		return billIDs;
	}
	
	/**
	 * 
	 * @param actionList 界面上的Action集合
	 * @param actionsCodes 需要处理的actioncode集合
	 * @param isShow 第二个参数中所包含的actioncode是否显示， true显示，false不显示
	 */
	@SuppressWarnings("unused")
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
		// 待抽常量
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
