package nc.itf.erm.mactrlschema;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * �����������Ƶ��ݶ����������
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlBillManage {
	/**
	 * ��������
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
}
