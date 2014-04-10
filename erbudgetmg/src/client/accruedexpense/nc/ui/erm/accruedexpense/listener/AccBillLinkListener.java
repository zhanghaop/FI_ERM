package nc.ui.erm.accruedexpense.listener;

import nc.funcnode.ui.FuncletInitData;
import nc.itf.pub.link.ILinkQueryDataPlural;
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

/**
 * ��ʼ�����ܽڵ���������ڵ�ΪԤ�ᵥ
 *
 */
public class AccBillLinkListener extends DefaultFuncNodeInitDataListener {
	private PaginationModel pageModel;
	
	private StandAloneToftPanelActionContainer listViewActions;
	private StandAloneToftPanelActionContainer editorActions;
	
	private FuncletInitData data;
	
	@Override
	public void initData(FuncletInitData data) {
		this.data = data;
		if(getModel().getUiState() == UIState.ADD || getModel().getUiState() == UIState.EDIT){
			return;//�༭̬������ֵ
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
			// �µ������ӿ�
			billIDs = ((ILinkQueryDataPlural) querydata).getBillIDs();
		} else {
			billIDs = new String[] { querydata.getBillID() };
		}
		return billIDs;
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
}
