package nc.pubitf.erm.erminit;

import nc.vo.pub.BusinessException;

/**
 * 费用期初关闭服务
 * 
 * @author lvhj
 *
 */
public interface IErminitCloseService {

	/**
	 * 组织期初关闭
	 * 
	 * @param pk_org
	 * @throws BusinessException
	 */
	public boolean close(String pk_org) throws BusinessException;
	/**
	 * 组织期初取消关闭
	 * 
	 * @param pk_org
	 * @throws BusinessException
	 */
	public boolean unclose(String pk_org) throws BusinessException;
}
