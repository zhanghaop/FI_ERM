package nc.ui.erm.fieldcontrast.model;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.prv.IFieldContrastService;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.uif2.LoginContext;
/**
 * 字段对照服务类
 * @author luolch
 *
 */
public class FieldContrastService implements IBatchAppModelService{

	private IFieldContrastService service = null;

	@Override
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		BatchOperateVO res = getService().batchSave(batchVO);
		if(batchVO != null){
			ErmBillFieldContrastCache.clearCache();
		}
		return res;
	}

	private IFieldContrastService getService() {
		if (service == null) {
			service = NCLocator.getInstance().lookup(
					IFieldContrastService.class);
		}
		return service;
	}
	
	@Deprecated
	public Object[] queryByDataVisibilitySetting(LoginContext context)
	throws Exception {
		return null;
	}
}

