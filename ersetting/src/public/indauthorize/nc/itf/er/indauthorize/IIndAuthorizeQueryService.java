package nc.itf.er.indauthorize;

import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;

public interface IIndAuthorizeQueryService {
	/**
	 * @author liansg
	 */
	public IndAuthorizeVO[] queryIndAuthorizes(String whereCond)
			throws BusinessException;
}
