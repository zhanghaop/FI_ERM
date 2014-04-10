package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥԤ����Ʒ���ӿ�
 * @author chenshuaia
 *
 */
public interface IErmAccruedBillYsControlService {
	/**
	 * Ԥ��ҵ����
	 * 
	 * @param vos
	 *            Ԥ�ᵥvos
	 * @param isContray
	 *            �Ƿ������
	 * @param actionCode
	 *            ��������
	 * @param isExistParent
	 *            �Ƿ�ִ������Ԥ��
	 * @throws BusinessException
	 */
	public void ysControl(AggAccruedBillVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException;

	/**
	 * Ԥ�ᵥ�޸�����µ�Ԥ�����
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            �Ƿ����Ԥռ���� false��ʾ����ִ����
	 * @param isExistParent
	 *            �Ƿ�ִ������Ԥ��
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggAccruedBillVO[] vos, AggAccruedBillVO[] oldvos) throws BusinessException;
}
