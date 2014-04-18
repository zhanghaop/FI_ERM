package nc.itf.er.reimtype;

import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.pub.BusinessException;

public interface IReimTypeQueryService {
	/**
	 * @author liansg
	 */
	public ReimTypeVO[] queryReimTypes(String whereCond)
			throws BusinessException;
}
