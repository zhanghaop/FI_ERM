package nc.pubitf.erm.matterapp;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * ���������������ӿ�
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillApprove {

	/**
	 * �ύ����
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] commitVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * �ջص���
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos) throws BusinessException;

	/**
	 * ��������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public MessageVO[] approveVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * ȡ����������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unApproveVOs(AggMatterAppVO[] vos) throws BusinessException;
	
	/**
	 * ����VO����״̬
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO vo)  throws BusinessException;
}
