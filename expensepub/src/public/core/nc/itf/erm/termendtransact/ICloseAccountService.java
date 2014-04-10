package nc.itf.erm.termendtransact;

import nc.vo.pub.BusinessException;

public interface ICloseAccountService {

	/**
	 * 根据pk_org、节点编号更新结账信息
	 * @param nodeCode
	 * @param pk_org
	 * @param period
	 * @throws Exception
	 */
	public void updateCloseAccountInfo(String nodeCode ,String pk_org ,String year,String month) throws Exception ;
	
	/**
	 * 根据财务组织pk、结点编号查询某个期间是否结帐
	 * @param nodeCode:报销管理2011      
	 * @return 结帐：true。未结帐：false
	 * @throws BusinessException
	 */
	public boolean isAccountClosed(String nodeCode ,String pk_org ,String year,String month) throws BusinessException;
	
	public String[] getCloseAccountInfo (String nodeCode ,String pk_org) throws BusinessException;

}
