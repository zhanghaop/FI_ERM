package nc.itf.erm.service;

import nc.vo.pub.BusinessException;

/**
 * ���ò�Ʒ����ҵ���ʼ��Ԥ�����ݷ���
 * 
 * @author lvhj
 *
 */
public interface IErmGroupPredataService {

	/**
	 * ��ʼ����������
	 * 
	 * @param groupPks
	 * @throws BusinessException
	 */
	public void initGroupData(String[] groupPks) throws BusinessException;
}
