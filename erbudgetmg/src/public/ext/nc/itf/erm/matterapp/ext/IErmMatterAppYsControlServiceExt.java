package nc.itf.erm.matterapp.ext;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * �������뵥Ԥ����ơ���д�ӿ�
 * 
 * ����Ԫ��Ŀר��
 * 
 * @author lvhj
 * 
 */
public interface IErmMatterAppYsControlServiceExt {

	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            �������뵥vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode)
			throws BusinessException;

	/**
	 * �������뵥�޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException;
}
