package nc.ui.erm.costshare.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import nc.bs.uif2.IActionCode;
import nc.funcnode.ui.FuncletInitData;
import nc.funcnode.ui.action.INCAction;
import nc.funcnode.ui.action.SeparatorAction;
import nc.itf.pub.link.ILinkQueryDataPlural;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.actions.StandAloneToftPanelActionContainer;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.DefaultFuncNodeInitDataListener;
import nc.vo.er.link.LinkQuery;
import nc.vo.pub.link.DefaultLinkData;

import org.apache.commons.collections.CollectionUtils;

/**
 * @author wangled
 *
 */
public class CostShareLinkListener extends DefaultFuncNodeInitDataListener {
	 private StandAloneToftPanelActionContainer listViewActions;
	 private StandAloneToftPanelActionContainer editorActions;

	@Override
	public void initData(FuncletInitData data) {
		super.initData(data);
	}
	
	@Override
	protected void doOther(int type) {
		if (ILinkType.LINK_TYPE_QUERY == type) {
			CostShareDataManager modelDataManager = (CostShareDataManager) getDatamanager();
			String[] pks = getBillIDs();
			modelDataManager.initModelByPKs(pks);
			if (pks != null && pks.length == 1) {
				// 只有一条数据情况下，显示卡片
				((BillManageModel) modelDataManager.getModel()).fireShowEditorEvent();
			}

			initActions(listViewActions.getActions(), funcletInitData);
			listViewActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
			initActions(editorActions.getActions(), funcletInitData);
			editorActions.handleEvent(new AppEvent(AppEventConst.UISTATE_CHANGED));
		}
	}

	protected String[] getBillIDs() {
		String[] pks = null;
		if (funcletInitData != null && funcletInitData.getInitData() instanceof LinkQuery) {
			LinkQuery querydata = (LinkQuery) funcletInitData.getInitData();
			if (querydata != null) {
				if (querydata.getBillIDs().length != 0) {
					pks = querydata.getBillIDs();
				} else {
					pks = new String[] { querydata.getBillID() };
				}
			}
		} else if (funcletInitData != null && funcletInitData.getInitData() instanceof ILinkQueryData) {
			if (funcletInitData.getInitData() instanceof ILinkQueryDataPlural) {
				pks = ((DefaultLinkData) funcletInitData.getInitData()).getBillIDs();

			} else {
				pks = new String[] { ((ILinkQueryData) funcletInitData.getInitData()).getBillID() };
			}
		}
		return pks;
	}
	
    private void initActions(List<Action> actionList,FuncletInitData data) {
        if (!CollectionUtils.isEmpty(actionList)) {
            List<Action> actionListNew = new ArrayList<Action>();
            List<String> linkQueryShowActions = getLinkQueryShowActions(data);
            for (Action action : actionList)
            {
                Object actionCode = action.getValue(INCAction.CODE);
				if ((actionCode != null) && (linkQueryShowActions.contains(actionCode.toString())))
                {
                    actionListNew.add(action);
                } else if(action instanceof SeparatorAction){//增加按钮的分隔符
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
        showActionCodes.add("linkview");
        showActionCodes.add(IActionCode.PRINT);
        return showActionCodes;
    }

	public StandAloneToftPanelActionContainer getListViewActions() {
		return listViewActions;
	}

	public void setListViewActions(
			StandAloneToftPanelActionContainer listViewActions) {
		this.listViewActions = listViewActions;
	}

	public StandAloneToftPanelActionContainer getEditorActions() {
		return editorActions;
	}

	public void setEditorActions(StandAloneToftPanelActionContainer editorActions) {
		this.editorActions = editorActions;
	}
    
}
