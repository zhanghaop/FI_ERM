package nc.ui.er.expensetype.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.expensetype.IExpenseTypeQueryService;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.uif2.LoginContext;

public class ExpenseTypeModelDataManager implements IAppModelDataManager,
		IPaginationModelListener, AppEventListener {
	/**
	 * @author liansg
	 */
	private BatchBillTableModel model = null;
	private PaginationModel paginationModel = null;
	private BillManagePaginationDelegator paginationDelegator = null;
	private IExceptionHandler exceptionHandler = null;

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

//	public PaginationModel getPaginationModel() {
//		return paginationModel;
//	}

//	public void setPaginationModel(PaginationModel paginationModel) {
//		this.paginationModel = paginationModel;
//		this.paginationModel.addPaginationModelListener(this);
//	}

//	public BillManagePaginationDelegator getPaginationDelegator() {
//		return paginationDelegator;
//	}

//	public void setPaginationDelegator(
//			BillManagePaginationDelegator paginationDelegator) {
//		this.paginationDelegator = paginationDelegator;
//	}

	@Override
	public void onDataReady() {
//		paginationDelegator.onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

	@Override
	public void handleEvent(AppEvent event) {
//		paginationDelegator.handleEvent(event);
	}

	public void initModel() {
		ExpenseTypeVO[] vos = null;
//		ArrayList<String> pks = new ArrayList<String>();
		String whereCond = "pk_group = '" + getContext().getPk_group() + "'";
		try {
			vos = getExpenseTypeQueryService().queryExpenseTypes(
					whereCond);
			getModel().initModel(vos);

//			if (vos != null) {
//				for (ExpenseTypeVO vo : vos) {
//					pks.add(vo.getPk_expensetype());
//				}
//			}
//			getPaginationModel().setObjectPks(pks.toArray(new String[0]));
		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
		// getModel().initModel(vos);
	}

	private IExpenseTypeQueryService getExpenseTypeQueryService() {
		return NCLocator.getInstance().lookup(
				IExpenseTypeQueryService.class);
	}

	private LoginContext getContext() {
		return getModel().getContext();
	}

}
