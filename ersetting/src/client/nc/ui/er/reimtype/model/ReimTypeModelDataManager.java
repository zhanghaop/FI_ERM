package nc.ui.er.reimtype.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.reimtype.IReimTypeQueryService;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.uif2.LoginContext;

public class ReimTypeModelDataManager implements IAppModelDataManager,
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
//
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
		ReimTypeVO[] vos = null;
//		ArrayList<String> pks = new ArrayList<String>();
		String whereCond = "pk_group = '" + getContext().getPk_group() + "'";
		try {
			vos = getReimTypeQueryService().queryReimTypes(
					whereCond);
			getModel().initModel(vos);
//			if (vos != null) {
//				for (ReimTypeVO vo : vos) {
//					pks.add(vo.getPk_reimtype());
//				}
//			}
//			getPaginationModel().setObjectPks(pks.toArray(new String[0]));
		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
		// getModel().initModel(vos);
	}

	private IReimTypeQueryService getReimTypeQueryService() {
		return NCLocator.getInstance().lookup(
				IReimTypeQueryService.class);
	}

	private LoginContext getContext() {
		return getModel().getContext();
	}

}
