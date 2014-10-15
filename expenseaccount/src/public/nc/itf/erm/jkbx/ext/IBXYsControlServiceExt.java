package nc.itf.erm.jkbx.ext;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * ��������������������� Ԥ����ơ���д�ӿ�
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public interface IBXYsControlServiceExt {
	
	/**
	 * Ԥ��ҵ����
	 * @param vos ����vos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @throws BusinessException
	 */
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException;
	
	/**
	 * Ԥ������޸�
	 * @param vos ����VO
	 * @throws BusinessException
	 */
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException;
}
