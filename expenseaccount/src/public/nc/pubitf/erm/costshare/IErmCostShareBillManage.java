package nc.pubitf.erm.costshare;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת��ά���
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillManage {
	
	/**
	 * ���ý�ת������
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO insertVO(AggCostShareVO vo) throws BusinessException;
	
	/**
	 * ���ý�ת���޸�
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException;
	/**
	 * ���ý�ת��ɾ��
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] deleteVOs(AggCostShareVO[] vos) throws BusinessException;
	
}
