package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ��������
 * @author shengqy
 *
 */
public interface IErmAccruedBillApprove {
	
	/**
	 * ��������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] approveVOs(AggAccruedBillVO[] vos) throws BusinessException;
	

	/**
	 * ȡ����������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unApproveVOs(AggAccruedBillVO[] vos) throws BusinessException;
	
	/**
	 * ����VO����״̬
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO updateVOBillStatus(AggAccruedBillVO vo)  throws BusinessException;
}
