package nc.itf.erm.billcontrast;

import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.pub.BusinessException;

/**
 * ���õ��ݶ������ݱ�����ڲ�ʹ�ýӿڣ�
 * 
 * @author lvhj
 *
 */
public interface IErmBillcontrastManage {

	/**
	 * ����������������ص����ݱ��뱣�ִ������ݵ�˳��
	 * 
	 * @param batchVO
	 * @return
	 * @throws Exception
	 */
	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws BusinessException;
}
