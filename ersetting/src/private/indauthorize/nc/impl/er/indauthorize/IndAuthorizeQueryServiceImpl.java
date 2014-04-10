package nc.impl.er.indauthorize;

import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;


public class IndAuthorizeQueryServiceImpl implements
		IIndAuthorizeQueryService {
	/**
	 * @author liansg
	 */
	@Override
	public IndAuthorizeVO[] queryIndAuthorizes(String whereCond)
			throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				IndAuthorizeVO.class, whereCond, false);
		if (ncobjects == null) {
			return new IndAuthorizeVO[0];
		}
		IndAuthorizeVO[] rvtVOs = new IndAuthorizeVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (IndAuthorizeVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;
	}

}
