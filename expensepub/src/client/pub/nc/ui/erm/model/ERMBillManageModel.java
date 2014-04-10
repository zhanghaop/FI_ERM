package nc.ui.erm.model;

import java.util.List;

import nc.ui.pubapp.uif2app.AppUiState;
import nc.ui.pubapp.uif2app.event.IAppEventHandler;
import nc.ui.pubapp.uif2app.model.AppModelExDelegate;
import nc.ui.pubapp.uif2app.model.IAppModelEx;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.ui.uif2.model.BillManageModel;
import nc.ui.uif2.model.RowOperationInfo;
import nc.vo.querytemplate.queryscheme.SimpleQuerySchemeVO;

/**
 * ֧������ɾ�� <b>Date:</b>2012-12-6<br>
 * 
 * @author��wangyhh@ufida.com.cn
 * @version $Revision$
 */
@SuppressWarnings("restriction")
public class ERMBillManageModel extends BillManageModel implements IAppModelEx {
	public static final String QueryScheme_CHANGED = "ERM_QUERYSCHEME_CHANGED";
	
	private AppModelExDelegate appModelExDelegate = new AppModelExDelegate(this);

	@Override
	public void fireEvent(AppEvent event) {
		super.fireEvent(event);
		// ������չҵ���¼�������
		this.fireExtEvent(event);
	}
	
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
		
		//���û�����ݵĻ����Ͳ�����
		int index = findBusinessData(obj);
		if (index == -1) {
			return;
		}

		@SuppressWarnings("rawtypes")
		List data = getData();
		data.remove(data.get(index));
		datapks.remove(index);

		boolean isDeleteSelectedData = false;
		if (index == getSelectedRow()) {// ����ɾ��ѡ���У��ڷ���ɾ���¼�ʱ���м�������ȡ��ǰѡ���г���
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
	 * ��������pk��������ݶ���
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


	public void fireExtEvent(AppEvent event) {
		this.appModelExDelegate.fireEvent(event);
	}
	
	@Override
	public void addAppEventListener(Class<? extends AppEvent> eventType,
			IAppEventHandler<? extends AppEvent> l) {
		appModelExDelegate.addAppEventListener(eventType, l);
	}

	@Override
	public AppUiState getAppUiState() {
		return this.appModelExDelegate.getAppUiState();
	}

	@Override
	public void removeAppEventListener(Class<? extends AppEvent> eventType,
			IAppEventHandler<? extends AppEvent> l) {
		appModelExDelegate.removeAppEventListener(eventType, l);
	}

	@Override
	public void setAppUiState(AppUiState appUiState) {
		this.appModelExDelegate.setAppUiState(appUiState);
	}
	
}

