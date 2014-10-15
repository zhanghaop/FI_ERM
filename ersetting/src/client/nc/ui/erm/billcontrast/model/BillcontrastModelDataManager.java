package nc.ui.erm.billcontrast.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.billcontrast.IErmBillcontrastQuery;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.editor.BatchBillTable;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.erm.billcontrast.BillcontrastVO;
import nc.vo.uif2.LoginContext;

public class BillcontrastModelDataManager implements IAppModelDataManager,
		IPaginationModelListener, AppEventListener {
	/**
	 * @author wangle
	 */
	private BatchBillTableModel model = null;
	private IExceptionHandler exceptionHandler = null;
	private BatchBillTable editor = null;

	@Override
	public void onDataReady() {
	}

	@Override
	public void onStructChanged() {

	}

	@Override
	public void handleEvent(AppEvent event) {
	}

	public void initModel() {
		BillcontrastVO[] vos = null;
		String pk_org = getContext().getPk_org();
		String pk_group=getContext().getPk_group();
		try {
			if (pk_org != null) {
				vos = getBillcontrastQueryService().queryAllByOrg(pk_org,pk_group,getContext());
			}
			getModel().initModel(vos);
		} catch (Exception e) {
			getExceptionHandler().handlerExeption(e);
		}
	}

	private IErmBillcontrastQuery getBillcontrastQueryService() {
		return NCLocator.getInstance().lookup(IErmBillcontrastQuery.class);
	}

	private LoginContext getContext() {
		return getModel().getContext();
	}

	public BatchBillTable getEditor() {
		return editor;
	}

	public void setEditor(BatchBillTable editor) {
		this.editor = editor;
	}

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

}
