package nc.pubitf.erm.costshare;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * ���ý�ת��ȷ�ϻ
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillApprove {
	
	/**
	 * ȷ�Ϸ��ý�ת��
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] approveVOs(AggCostShareVO[] vos,UFDate buDate) throws BusinessException;
	/**
	 * ȡ��ȷ�Ϸ��ý�ת��
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unapproveVOs(AggCostShareVO[] vos) throws BusinessException;

}
