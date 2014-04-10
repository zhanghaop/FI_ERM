package nc.itf.er.reimtype;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

public interface IReimTypeService {
	/**
	 * @author liansg
	 */
	public BatchOperateVO batchSaveReimType(BatchOperateVO batchVO)
			throws BusinessException;

}
