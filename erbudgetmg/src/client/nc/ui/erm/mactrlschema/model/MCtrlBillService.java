package nc.ui.erm.mactrlschema.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillManage;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.uif2.LoginContext;


public class MCtrlBillService implements IBatchAppModelService{

	private IErmMappCtrlBillManage service = null;


	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		return getService().batchSave(batchVO);
	}


	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}

	private IErmMappCtrlBillManage getService() {
		if (service == null) {
			service = NCLocator.getInstance().lookup(
					IErmMappCtrlBillManage.class);
		}
		return service;
	}
}