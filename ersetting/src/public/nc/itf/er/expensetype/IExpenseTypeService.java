package nc.itf.er.expensetype;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

public interface IExpenseTypeService {
	/**
	 * @author liansg
	 */
	public BatchOperateVO batchSaveExpenseType(BatchOperateVO batchVO)
			throws BusinessException;

}
