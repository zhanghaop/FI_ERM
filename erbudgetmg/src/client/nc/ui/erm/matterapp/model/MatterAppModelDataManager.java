package nc.ui.erm.matterapp.model;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.common.ErmConst;
import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.ui.erm.matterapp.view.MatterAppMNListView;
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

/**
 * 数据模型管理器，主要负责各种方式的模型初始化
 * 
 * @author chenshuaia
 * 
 */
public class MatterAppModelDataManager implements IQueryAndRefreshManagerEx, IAppModelDataManager, IPaginationModelListener,
		AppEventListener {

	private AbstractAppModel model;

	private PaginationModel pageModel;

	private BillManagePaginationDelegator delegator;

	private String qryCondition;

	private IErmMatterAppBillQueryPrivate queryService;

	private IExceptionHandler exceptionHandler;

	private boolean isRefresh4Query;

	private MatterAppMNListView listView;
	
	private IQueryScheme qryScheme;
	@Override
	public void initModel() {
		model.setUiState(UIState.NOT_EDIT);
		isRefresh4Query = false;
		getModel().initModel(null);
	}

	@Override
	public void initModelBySqlWhere(String sqlWhere) {
		this.qryCondition = sqlWhere;
		this.isRefresh4Query = true;
		this.initData(this.qryCondition);
	}

	@Override
	public void refresh() {
		if (isRefresh4Query) {
			initModelBySqlWhere(qryCondition);
		} else {
			initModelBySqlWhere(" 1=0 ");
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param condition
	 */
	public void initData(String condition) {
		MatterAppQueryCondition condVo = new MatterAppQueryCondition();
		initQueryCondition(condition, condVo);

		try {
			ModelDataDescriptor mdd = new ModelDataDescriptor();
			String[] pks = getQueryService().queryBillPksByWhere(condVo);
			getPageModel().setObjectPks(pks, mdd);

			if (!getListView().isComponentVisible()) {
				getListView().showMeUp();
			}
			
			if(pks != null){
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQuerySuccessInfo(pks.length), getModel().getContext());
			}else{
				ShowStatusBarMsgUtil.showStatusBarMsg(IShowMsgConstant.getQueryNullInfo(), getModel().getContext());
			}
		} catch (BusinessException e) {
			exceptionHandler.handlerExeption(e);
		}
	}

	public void initQueryCondition(String condition, MatterAppQueryCondition condVo) {
		condVo.setWhereSql(condition);
		condVo.setPk_tradetype(((MAppModel) getModel()).getDjlxbm());
		condVo.setNodeCode(getModel().getContext().getNodeCode());
		condVo.setPk_group(getModel().getContext().getPk_group());
		condVo.setPk_user(getModel().getContext().getPk_loginUser());
		
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
								condVo.setUser_approving(true);
							}
						}
					}
					
					if (fieldCode.equals(ErmConst.QUERY_CONDITION_APPROVED)) {// 我已审批
						List<IFieldValueElement> fieldValues = iFilter.getFieldValue().getFieldValues();
						List<String> valueList = new ArrayList<String>();
						for (IFieldValueElement value : fieldValues) {
							valueList.add(value.getSqlString());
							if ("Y".equals(value.getSqlString())) {
								condVo.setUser_approved(true);
							}
						}
					}
				}
			}
		}
	}

	private IErmMatterAppBillQueryPrivate getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(IErmMatterAppBillQueryPrivate.class);
		}
		return queryService;
	}

	public BillManagePaginationDelegator getDelegator() {
		if (delegator == null) {
			delegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPageModel());
		}
		return delegator;
	}

	@Override
	public void handleEvent(AppEvent event) {
		getDelegator().handleEvent(event);
	}

	@Override
	public void onDataReady() {
		getDelegator().onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public PaginationModel getPageModel() {
		return pageModel;
	}

	public void setPageModel(PaginationModel pageModel) {
		this.pageModel = pageModel;
		pageModel.addPaginationModelListener(this);
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public MatterAppMNListView getListView() {
		return listView;
	}

	public void setListView(MatterAppMNListView listView) {
		this.listView = listView;
	}

	@Override
	public void initModelBySqlWhere(IQueryScheme qryScheme) {
		setQryScheme(qryScheme);
		String sqlWhere = qryScheme.getWhereSQLOnly();
		initModelBySqlWhere(sqlWhere);
	}

	public IQueryScheme getQryScheme() {
		return qryScheme;
	}

	public void setQryScheme(IQueryScheme qryScheme) {
		this.qryScheme = qryScheme;
	}
}
