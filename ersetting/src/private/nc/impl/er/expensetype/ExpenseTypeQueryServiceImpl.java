package nc.impl.er.expensetype;

import nc.itf.er.expensetype.IExpenseTypeQueryService;
import nc.md.data.access.NCObject;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;


public class ExpenseTypeQueryServiceImpl implements
		IExpenseTypeQueryService {
	/**
	 * @author liansg
	 */
	@Override
	public ExpenseTypeVO[] queryExpenseTypes(String whereCond)
			throws BusinessException {
		IMDPersistenceQueryService queryService = MDPersistenceService
				.lookupPersistenceQueryService();
		NCObject[] ncobjects = queryService.queryBillOfNCObjectByCond(
				ExpenseTypeVO.class, whereCond, false);
		if (ncobjects == null) {
			return new ExpenseTypeVO[0];
		}
		ExpenseTypeVO[] rvtVOs = new ExpenseTypeVO[ncobjects.length];
		for (int i = 0; i < rvtVOs.length; i++) {
			rvtVOs[i] = (ExpenseTypeVO) ncobjects[i].getContainmentObject();
		}
		return rvtVOs;
	}

}
