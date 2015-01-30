package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用帐单据查询服务
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountQueryService {
	
	public ExpenseAccountVO queryByPK(String pk) throws BusinessException;
	
	public ExpenseAccountVO[] queryByPKs(String[] pks) throws BusinessException;
	
	public ExpenseAccountVO[] queryBySrcID(String[] srcIDS) throws BusinessException;

	ExpenseAccountVO[] queryBySqlWhere(String sqlWhere) throws BusinessException;

}
