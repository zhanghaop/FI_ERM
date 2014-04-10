package nc.itf.erm.termendtransact;

import nc.vo.pub.BusinessException;

public interface ICloseAccountService {

	/**
	 * ����pk_org���ڵ��Ÿ��½�����Ϣ
	 * @param nodeCode
	 * @param pk_org
	 * @param period
	 * @throws Exception
	 */
	public void updateCloseAccountInfo(String nodeCode ,String pk_org ,String year,String month) throws Exception ;
	
	/**
	 * ���ݲ�����֯pk������Ų�ѯĳ���ڼ��Ƿ����
	 * @param nodeCode:��������2011      
	 * @return ���ʣ�true��δ���ʣ�false
	 * @throws BusinessException
	 */
	public boolean isAccountClosed(String nodeCode ,String pk_org ,String year,String month) throws BusinessException;
	
	public String[] getCloseAccountInfo (String nodeCode ,String pk_org) throws BusinessException;

}
