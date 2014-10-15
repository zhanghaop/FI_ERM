package nc.itf.erm.billcontrast;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * 费用单据对照数据表管理（内部使用接口）
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastManage {

	/**
	 * 批量保存操作，返回的数据必须保持传入数据的顺序
	 * 
	 * @param batchVO
	 * @return
	 * @throws Exception
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
}
