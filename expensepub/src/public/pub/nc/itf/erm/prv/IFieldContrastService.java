package nc.itf.erm.prv;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

public interface IFieldContrastService {
	/**
	 * 批量保存
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
	
	/**
	 * 查询出字段对照
	 * @param scene 场景 nc.bs.erm.cache.ErmBillFieldContrastCache
	 * @param src_billtype 源单据类型
	 * @param des_billtype 目标单据类型
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] queryFieldContrastVOS(int scene, String src_billtype, String des_billtype) throws BusinessException;
}
