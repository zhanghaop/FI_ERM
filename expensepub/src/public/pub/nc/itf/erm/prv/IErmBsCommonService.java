package nc.itf.erm.prv;

import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * 费用公共业务服务
 * 
 * @author lvhj
 * 
 */
public interface IErmBsCommonService {

	/**
	 * 缓存有权限的组织
	 * 
	 * @param pk_user
	 * @param nodeCode
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getPermissonOrgMapCall(String pk_user,
			String nodeCode, String pk_group) throws BusinessException;
}
