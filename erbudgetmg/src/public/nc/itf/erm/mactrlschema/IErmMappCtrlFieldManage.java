package nc.itf.erm.mactrlschema;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * ������������ά�Ȳ�������
 * @author chenshuaia
 *
 */
public interface IErmMappCtrlFieldManage {
	/**
	 * ��������
	 * @param batchVO
	 * @return
	 * @throws BusinessException
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
}
