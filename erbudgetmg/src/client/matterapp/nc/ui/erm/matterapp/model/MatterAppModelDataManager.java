package nc.ui.erm.matterapp.model;

import nc.bs.erm.matterapp.common.MatterAppQueryCondition;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.matterapp.IErmMatterAppBillQueryPrivate;
import nc.ui.erm.matterapp.view.MatterAppMNListView;
import nc.ui.querytemplate.querytree.IQueryScheme;
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
			initModelBySqlWhere(" 1=1 ");
		}
	}

	/**
	 * 初始化数据
	 * 
	 * @param condition
	 */
	public void initData(String condition) {
		MatterAppQueryCondition condVo = new MatterAppQueryCondition();
		condVo.setWhereSql(condition);
		condVo.setPk_tradetype(((MAppModel) getModel()).getDjlxbm());
		condVo.setNodeCode(getModel().getContext().getNodeCode());
		condVo.setPk_group(getModel().getContext().getPk_group());
		condVo.setPk_user(getModel().getContext().getPk_loginUser());

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
		String sqlWhere = qryScheme.getWhereSQLOnly();
		initModelBySqlWhere(sqlWhere);
	}
}
