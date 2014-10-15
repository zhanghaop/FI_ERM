package nc.itf.erm.service;

import nc.vo.pub.BusinessException;

/**
 * �ͻ�����Ӧ�̺ϲ������÷���
 * 
 * @author chenshuaia
 * 
 */
public interface IErmMergeService {

	/**
	 * ��Ӧ�̺ϲ�
	 * 
	 * @param targetSup
	 *            �ϲ���Ĺ�Ӧ��pk
	 * @param sourceSup
	 *            ԭ��Ӧ��pk
	 */
	public void mergeSupplier(String targetSup, String sourceSup) throws BusinessException;

	/**
	 * �ͻ��ϲ�
	 * 
	 * @param targetCus
	 *            �ϲ���Ŀͻ�pk
	 * @param sourceCus
	 *            ԭ�ͻ�pk
	 */
	public void mergeCustomer(String targetCus, String sourceCus) throws BusinessException;
}
