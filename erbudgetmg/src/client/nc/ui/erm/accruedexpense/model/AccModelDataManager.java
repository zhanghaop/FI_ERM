package nc.ui.erm.accruedexpense.model;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.common.AccruedBillQueryCondition;
import nc.bs.erm.common.ErmConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.accruedexpense.IErmAccruedBillQueryPrivate;
import nc.ui.erm.accruedexpense.view.AccMNListView;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.ui.uif2.model.IQueryAndRefreshManagerEx;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.pub.BusinessException;

public class AccModelDataManager implements IQueryAndRefreshManagerEx, IAppModelDataManager, IPaginationModelListener,
		AppEventListener {

	private AbstractAppModel model;

	private PaginationModel pageModel;

	private BillManagePaginationDelegator delegator;

	private String qryCondition;

	private IErmAccruedBillQueryPrivate queryPriService;

	private IExceptionHandler exceptionHandler;

	private boolean isRefresh4Query;

	private AccMNListView listView;
	
	private IQueryScheme qryScheme;

	@Override
	public void initModelBySqlWhere(IQueryScheme qryScheme) {
		this.setQryScheme(qryScheme);
		String sqlWhere = qryScheme.getWhereSQLOnly();
		initModelBySqlWhere(sqlWhere);
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.qryCondition = sqlWhere;
		this.isRefresh4Query = true;
		this.initData(this.qryCondition);
	}

	/**
	 * 初始化数据
	 * 
	 * @param condition
	 */
	private void initData(String condition) {
		AccruedBillQueryCondition condvo = new AccruedBillQueryCondition();
		initQueryCondition(condition,condvo);

		try {
			ModelDataDescriptor mdd = new ModelDataDescriptor();
			String[] pks = getQueryPriService().queryBillPksByWhere(condvo);
			getPageModel().setObjectPks(pks, mdd);

			if (!getListView().isComponentVisible()) {
				getListView().showMeUp();
			}

			if (pks != null) {
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQuerySuccessInfo(pks.length), getModel()
						.getContext());
			} else {
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQueryNullInfo(), getModel().getContext());
			}
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	public void initQueryCondition(String condition, AccruedBillQueryCondition condvo) {
		condvo.setWhereSql(condition);
		condvo.setPk_tradetype(((AccManageAppModel)getModel()).getCurrentTradeTypeCode());
		condvo.setNodeCode(getModel().getContext().getNodeCode());
		condvo.setPk_group(getModel().getContext().getPk_group());
		condvo.setPk_user(getModel().getContext().getPk_loginUser());
		
		if(getQryScheme() != null){
			//已审批、待审批
			IFilter[] filters = (IFilter[]) getQryScheme().get(IQueryScheme.KEY_FILTERS);
			if (filters != null) {
				for (IFilter iFilter : filters) {
					String fieldCode = iFilter.getFilterMeta().getFieldCode();
					if (fieldCode.equals(ErmConst.QUERY_CONDITION_APPROVING)) {// 待审批
						List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
						List<String> valueList = new ArrayList<String>();
						for (IFieldValueElement value : fieldValues) {
							valueList.add(value.getSqlString());
							if ("Y".equals(value.getSqlString())) {
								condvo.setUser_approving(true);
							}
						}
					}
					
					if (fieldCode.equals(ErmConst.QUERY_CONDITION_APPROVED)) {// 我已审批
						List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
						List<String> valueList = new ArrayList<String>();
						for (IFieldValueElement value : fieldValues) {
							valueList.add(value.getSqlString());
							if ("Y".equals(value.getSqlString())) {
								condvo.setUser_approved(true);
							}
						}
					}
				}
			}
		}
	}
	
	
	@Override
	public void refresh() {
		if (isRefresh4Query) {
			initModelBySqlWhere(qryCondition);
		} else {
			initModelBySqlWhere(" 1=0 ");
		}
	}

	@Override
	public void initModel() {
		model.setUiState(UIState.NOT_EDIT);
		isRefresh4Query = false;
		getModel().initModel(null);
	}

	@Override
	public void handleEvent(AppEvent event) {
		getDelegator().handleEvent(event);
	}
	
	@Override
	public void onStructChanged() {
		getDelegator().onDataReady();
		
	}

	@Override
	public void onDataReady() {
		// TODO Auto-generated method stub
		
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public PaginationModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(PaginationModel pageModel) {
		this.pageModel = pageModel;
		this.pageModel.addPaginationModelListener(this);
	}

	public BillManagePaginationDelegator getDelegator() {
		if (delegator == null) {
			delegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPageModel());
		}
		return delegator;
	}

	public void setDelegator(BillManagePaginationDelegator delegator) {
		this.delegator = delegator;
	}

	public String getQryCondition() {
		return qryCondition;
	}

	public void setQryCondition(String qryCondition) {
		this.qryCondition = qryCondition;
	}

	public IErmAccruedBillQueryPrivate getQueryPriService() {
		if (queryPriService == null) {
			queryPriService = NCLocator.getInstance().lookup(IErmAccruedBillQueryPrivate.class);
		}
		return queryPriService;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public boolean isRefresh4Query() {
		return isRefresh4Query;
	}

	public void setRefresh4Query(boolean isRefresh4Query) {
		this.isRefresh4Query = isRefresh4Query;
	}

	public AccMNListView getListView() {
		return listView;
	}

	public void setListView(AccMNListView listView) {
		this.listView = listView;
	}

	public IQueryScheme getQryScheme() {
		return qryScheme;
	}

	public void setQryScheme(IQueryScheme qryScheme) {
		this.qryScheme = qryScheme;
	}
	
	
}
