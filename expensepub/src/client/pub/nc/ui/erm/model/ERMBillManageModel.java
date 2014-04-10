package nc.ui.erm.model;

import java.util.List;

import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;

/**
 * 支持批量删除 <b>Date:</b>2012-12-6<br>
 * 
 * @author：wangyhh@ufida.com.cn
 * @version $Revision$
 */
public class ERMBillManageModel extends BillManageModel {
	public static final String QueryScheme_CHANGED = "ERM_QUERYSCHEME_CHANGED";
	
	
	@Override
	protected void dbDeleteMultiRows(Object... deletedObjects) throws Exception {
		// super.dbDeleteMultiRows(deletedObjects);
		getService().delete(deletedObjects);
	}

	public void setSimpleQueryVO(SimpleQuerySchemeVO qvo) {
		fireEvent(new AppEvent(QueryScheme_CHANGED, this, qvo));
	}

	@SuppressWarnings("unchecked")
	public void directlyUpdateWithoutFireEvent(Object obj) throws Exception {
		if (obj == null)
			return;
		if(getSelectedRow() >=0){
			getData().set(getSelectedRow(), obj);
		}
	}

	@Override
	public void directlyDelete(Object obj) throws Exception {

		if (obj == null)
			return;
		
		//如果没有数据的话，就不处理
		int index = findBusinessData(obj);
		if (index == -1) {
			return;
		}

		@SuppressWarnings("rawtypes")
		List data = getData();
		data.remove(data.get(index));
		datapks.remove(index);

		boolean isDeleteSelectedData = false;
		if (index == getSelectedRow()) {// 避免删除选中行，在发送删除事件时，有监听器获取当前选中行出错。
			setSelectedRowWithoutEvent(-1);
			isDeleteSelectedData = true;
		}
		clearSelectedOperaRows();
		fireEvent(new AppEvent(AppEventConst.DATA_DELETED, this,
				new RowOperationInfo(index, obj)));
		if (isDeleteSelectedData)
			setSelectedRow(Math.min(index, data.size() - 1));

	}
	
	/**
	 * 根据数据pk，获得数据对象
	 * 
	 * @param pk
	 * @return
	 */
	public Object getDataByPK(String pk){
		if(datapks.contains(pk)){
			int row = datapks.indexOf(pk);
			return getData().get(row);
		}
		return null;
	}
	
}
