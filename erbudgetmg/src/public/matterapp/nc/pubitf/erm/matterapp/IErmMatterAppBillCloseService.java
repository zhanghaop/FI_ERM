package nc.pubitf.erm.matterapp;

import nc.vo.pub.BusinessException;

/**
 * �������뵥�رշ���
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillCloseService {
	
	/**
	 * �����뵥��ϸ�У��رյ���
	 * @param mtapp_detail_pks
	 * @return
	 * @throws BusinessException
	 */

	public void closeVOs(String[] mtapp_detail_pks) throws BusinessException;
	/**
	 * �����뵥��ϸ�У���������
	 * @param mtapp_detail_pks
	 * @return
	 * @throws BusinessException
	 */
	public void openVOs(String[] mtapp_detail_pks) throws BusinessException;
	
}
