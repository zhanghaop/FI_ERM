package nc.bs.erm.matterapp.control;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * �������������ơ���д�ӿ�
 * 
 * @author lvhj
 * 
 */
public interface IErmMatterAppYsControlService {

	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            ����������vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @param isExistParent
	 *            �Ƿ�ִ������Ԥ��
	 * @throws BusinessException
	 */
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException;

	/**
	 * �����������޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            �Ƿ����Ԥռ���� false��ʾ����ִ����
	 * @param isExistParent
	 *            �Ƿ�ִ������Ԥ��
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException;
}
