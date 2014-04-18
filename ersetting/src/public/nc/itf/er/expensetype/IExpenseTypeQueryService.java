package nc.itf.er.expensetype;

import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;

public interface IExpenseTypeQueryService {
	/**
	 * @author liansg
	 */
	public ExpenseTypeVO[] queryExpenseTypes(String whereCond)
			throws BusinessException;
}
