package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * �����������رշ���
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillClose {
	
	/**
	 * �رյ���
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] closeVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * ��������
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] openVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * �Զ��رյ���
	 * @param vos
	 * @param isSelf �Ƿ����뵥�ڵ㱾��ά��
	 * @return
	 * @throws BusinessException
	 */
	
	public AggMatterAppVO[] autoCloseVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * �Զ���������
	 * @param vos
	 * @param isSelf �Ƿ����뵥�ڵ㱾��ά��
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] autoOpenVOs(AggMatterAppVO[] vos) throws BusinessException;

}
