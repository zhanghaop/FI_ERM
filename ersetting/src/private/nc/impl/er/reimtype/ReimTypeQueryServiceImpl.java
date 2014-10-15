package nc.impl.er.reimtype;

import nc.itf.er.reimtype.IReimTypeQueryService;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.pub.BusinessException;


public class ReimTypeQueryServiceImpl implements
		IReimTypeQueryService {
	/**
	 * @author liansg
	 */
	@Override
	public ReimTypeVO[] queryReimTypes(String whereCond)
			throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				ReimTypeVO.class, whereCond, false);
		if (ncobjects == null) {
			return new ReimTypeVO[0];
		}
		ReimTypeVO[] rvtVOs = new ReimTypeVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (ReimTypeVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;
	}

}
