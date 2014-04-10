package nc.ui.erm.costshare.ui;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.IErmCostShareBillQueryPrivate;
import nc.ui.pub.linkoperate.ILinkType;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.BillManagePaginationDelegator;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.ModelDataDescriptor;
import nc.vo.pub.BusinessException;

/**
 * 费用结转数据管理模型类
 * 
 * @author luolch
 * 
 */
public class CostShareDataManager implements nc.ui.uif2.model.IQueryAndRefreshManager, IPaginationModelListener,
		nc.ui.uif2.model.IAppModelDataManager {

	private IExceptionHandler exceptionHandler;
	private AbstractAppModel model;

	private PaginationModel pageModel;

	private String condition = "";

	private int linkType = ILinkType.NONLINK_TYPE;

	private BillManagePaginationDelegator delegator;

	public void initModel() {
		if (linkType == ILinkType.NONLINK_TYPE) {
			// 只在非联查情况下，主动初始化状态
			model.initModel(null);
		}
	}

	public void handleEvent(AppEvent event) {
	}

	public void initModelByPKs(String[] pks) {
		try {
			getPageModel().setObjectPks(pks);
		} catch (BusinessException e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	public BillManagePaginationDelegator getDelegator() {
		if (delegator == null) {
			delegator = new BillManagePaginationDelegator((BillManageModel) getModel(), getPageModel());
		}
		return delegator;
	}

	/**
	 * 初始化数据
	 * 
	 * @param condition
	 */
	public void initData(String condition) {
		try {
			ModelDataDescriptor mdd = new ModelDataDescriptor();
			
			if(condition!=null){
//				String loginUser = BXUiUtil.getPk_user();
				condition+=" and PK_GROUP= '" + getModel().getContext().getPk_group() + "'";//+and billmaker='" + loginUser+"'";
			}
			String[] pks = getQryService().queryCostSharePksByCond(condition);
			getPageModel().setObjectPks(pks, mdd);
		} catch (BusinessException e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public PaginationModel getPageModel() {
		return pageModel;
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	public void onStructChanged() {
	}

	private IErmCostShareBillQueryPrivate qryService;

	public IErmCostShareBillQueryPrivate getQryService() {
		if (qryService == null) {
			qryService = NCLocator.getInstance().lookup(IErmCostShareBillQueryPrivate.class);
		}
		return qryService;
	}

	public void refresh() {
		if ("".equals(condition)) {
			return;
		}
		initModelBySqlWhere(this.condition);
	}

	public void initModelBySqlWhere(String sqlWhere) {
		this.condition = sqlWhere;
		this.initData(this.condition);
	}

	public void setShowSealDataFlag(boolean showSealDataFlag) {
	}

	public int getLinkType() {
		return linkType;
	}

	public void setLinkType(int linkType) {
		this.linkType = linkType;
	}

	public void setPageModel(PaginationModel pageModel) {
		this.pageModel = pageModel;
		pageModel.addPaginationModelListener(this);
	}

	@Override
	public void onDataReady() {
		getDelegator().onDataReady();
	}
}
