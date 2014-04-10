package nc.itf.er.indauthorize;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

public interface IIndAuthorizeService {
	/**
	 * @author liansg
	 */
	public BatchOperateVO batchSaveIndAuthorize(BatchOperateVO batchVO)
			throws BusinessException;

}
