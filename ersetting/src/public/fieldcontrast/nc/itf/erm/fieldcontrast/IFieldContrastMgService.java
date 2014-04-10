package nc.itf.erm.fieldcontrast;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * 字段对照实体的管理接口（ 内部使用）
 * 
 * @author lvhj
 *
 */
public interface IFieldContrastMgService {
	/**
	 * 批量保存
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;

}
