package nc.itf.erm.prv;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.erm.fieldcontrast.FieldcontrastVO;
import nc.vo.pub.BusinessException;

public interface IFieldContrastService {
	/**
	 * ��������
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
	
	/**
	 * ��ѯ���ֶζ���
	 * @param scene ���� nc.bs.erm.cache.ErmBillFieldContrastCache
	 * @param src_billtype Դ��������
	 * @param des_billtype Ŀ�굥������
	 * @return
	 * @throws BusinessException
	 */
	public FieldcontrastVO[] queryFieldContrastVOS(int scene, String src_billtype, String des_billtype) throws BusinessException;
}
