package nc.pubitf.erm.matterapp;

import nc.vo.pub.BusinessException;

/**
 * 费用申请单关闭服务
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillCloseService {
	
	/**
	 * 按申请单明细行，关闭单据
	 * @param mtapp_detail_pks
	 * @return
	 * @throws BusinessException
	 */

	public void closeVOs(String[] mtapp_detail_pks) throws BusinessException;
	/**
	 * 按申请单明细行，重启单据
	 * @param mtapp_detail_pks
	 * @return
	 * @throws BusinessException
	 */
	public void openVOs(String[] mtapp_detail_pks) throws BusinessException;
	
}
