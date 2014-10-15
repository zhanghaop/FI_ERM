package nc.ui.er.indauthorize.model;

import nc.ui.uif2.AppEvent;
import nc.ui.uif2.components.pagination.PaginationModel;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.bd.meta.IBDObject;

public class BillManagePaginationDelegator {
	/**
	 * @author liansg
	 */
	// 分页信息模型
	private PaginationModel paginationModel = null;

	private BatchBillTableModel billModel = null;

	public BillManagePaginationDelegator() {
	}

	public BillManagePaginationDelegator(BatchBillTableModel billModel,
			PaginationModel pModel) {
		this.paginationModel = pModel;
		this.billModel = billModel;
	}

	public void handleEvent(AppEvent event) {

		if (event.getType().equals(AppEventConst.DATA_DELETED)) {
			RowOperationInfo info = (RowOperationInfo) event.getContextObject();
			Object[] objs = info.getRowDatas();
			String[] pks = new String[objs.length];
			for (int i = 0; i < objs.length; i++) {
				IBDObject bdObj = billModel.getBusinessObjectAdapterFactory()
						.createBDObject(objs[i]);
				pks[i] = (String) bdObj.getId();
			}
			paginationModel.removePks(pks);

		} else if (event.getType().equals(AppEventConst.DATA_INSERTED)) {
			RowOperationInfo info = (RowOperationInfo) event.getContextObject();
			int indexs[] = info.getRowIndexes();
			Object[] objs = info.getRowDatas();
			for (int i = 0; i < indexs.length; i++) {
				IBDObject bdObj = billModel.getBusinessObjectAdapterFactory()
						.createBDObject(objs[i]);
				paginationModel.insertPkByIndexs(indexs[i], (String) bdObj
						.getId(), objs[i]);
			}
		} else if (event.getType().equals(AppEventConst.SELECTED_DATE_CHANGED)) {
			RowOperationInfo info = (RowOperationInfo) event.getContextObject();
			int indexs[] = info.getRowIndexes();
			Object[] objs = info.getRowDatas();
			for (int i = 0; i < indexs.length; i++) {
				IBDObject bdObj = billModel.getBusinessObjectAdapterFactory()
						.createBDObject(objs[i]);
				paginationModel.update((String) bdObj.getId(), objs[i]);
			}
		}
	}

	public void onDataReady() {

		Object[] objs = getPaginationModel().getCurrentDatas();
		getBillModel().initModel(objs);

	}

	public PaginationModel getPaginationModel() {
		return paginationModel;
	}

	public void setPaginationModel(PaginationModel paginationModel) {
		this.paginationModel = paginationModel;
	}

	public BatchBillTableModel getBillModel() {
		return billModel;
	}

	public void setBillModel(BatchBillTableModel billModel) {
		this.billModel = billModel;
	}

}
