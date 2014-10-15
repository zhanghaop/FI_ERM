package nc.pubitf.erm.erminit;

import nc.vo.pub.BusinessException;

/**
 * �����ڳ��رշ���
 * 
 * @author lvhj
 *
 */
public interface IErminitCloseService {

	/**
	 * ��֯�ڳ��ر�
	 * 
	 * @param pk_org
	 * @throws BusinessException
	 */
	public boolean close(String pk_org) throws BusinessException;
	/**
	 * ��֯�ڳ�ȡ���ر�
	 * 
	 * @param pk_org
	 * @throws BusinessException
	 */
	public boolean unclose(String pk_org) throws BusinessException;
}
