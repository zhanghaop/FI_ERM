package nc.itf.er.indauthorize;

import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;

public interface IIndAuthorizeQueryService {
	/**
	 * @author liansg
	 */
	public IndAuthorizeVO[] queryIndAuthorizes(String whereCond)
			throws BusinessException;
	
	/**
	 * @param userid
	 * @param pk_group
	 * @return String[]{人员管理档案pk，人员基本档案pk，部门档案pk}
	 * @throws BusinessException
	 */
	public String[] queryPsnidAndDeptid(String userid,String pk_group)throws BusinessException;
}
