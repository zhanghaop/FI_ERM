package nc.ui.erm.billcontrast.model;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.billcontrast.IErmBillcontrastManage;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.uif2.LoginContext;
/**
 * 
 * @author wangled
 *
 */
public class BillcontrastModelService implements IBatchAppModelService  {

	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		return getBillcontrastService().batchSave(batchVO);
	}


	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}



	private IErmBillcontrastManage getBillcontrastService() {
		return NCLocator.getInstance().lookup(IErmBillcontrastManage.class);
	}


}
