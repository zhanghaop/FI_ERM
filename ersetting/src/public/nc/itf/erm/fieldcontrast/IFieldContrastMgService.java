package nc.itf.erm.fieldcontrast;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * �ֶζ���ʵ��Ĺ���ӿڣ� �ڲ�ʹ�ã�
 * 
 * @author lvhj
 *
 */
public interface IFieldContrastMgService {
	/**
	 * ��������
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;

}
