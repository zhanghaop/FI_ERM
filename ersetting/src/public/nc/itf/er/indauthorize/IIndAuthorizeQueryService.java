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
	 * @return String[]{��Ա������pk����Ա��������pk�����ŵ���pk}
	 * @throws BusinessException
	 */
	public String[] queryPsnidAndDeptid(String userid,String pk_group)throws BusinessException;
}
