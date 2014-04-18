package nc.ui.erm.mactrlschema.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldManage;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.uif2.LoginContext;

public class MCtrlFieldService implements IBatchAppModelService{

	private IErmMappCtrlFieldManage service = null;


	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		return getService().batchSave(batchVO);
	}


	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}

	private IErmMappCtrlFieldManage getService() {
		if (service == null) {
			service = NCLocator.getInstance().lookup(
					IErmMappCtrlFieldManage.class);
		}
		return service;
	}
}
