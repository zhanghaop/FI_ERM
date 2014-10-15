package nc.impl.erm.expenseaccount;

import java.util.Collection;

import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

/**
 * 费用帐单据查询服务
 * 
 * @author lvhj
 * 
 */
public class ErmExpenseaccountQueryServiceImpl implements IErmExpenseaccountQueryService {

	@Override
	public ExpenseAccountVO queryByPK(String pk) throws BusinessException {
		return MDPersistenceService.lookupPersistenceQueryService()
				.queryBillOfVOByPK(ExpenseAccountVO.class, pk, false);
	}

	@Override
	public ExpenseAccountVO[] queryByPKs(String[] pks) throws BusinessException {
		return queryBySqlWhere(SqlUtils.getInStr(ExpenseAccountVO.PK_EXPENSEACCOUNT, pks, false));
	}

	@Override
	public ExpenseAccountVO[] queryBySrcID(String[] srcIDS) throws BusinessException {
		return queryBySqlWhere(SqlUtils.getInStr(ExpenseAccountVO.SRC_ID, srcIDS, false));
	}

	@SuppressWarnings("unchecked")
	private ExpenseAccountVO[] queryBySqlWhere(String sqlWhere) throws BusinessException {
		Collection<ExpenseAccountVO> c = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByCond(
				ExpenseAccountVO.class, sqlWhere, false);

		if (c == null || c.isEmpty()) {
			return null;
		}
		return c.toArray(new ExpenseAccountVO[c.size()]);
	}

}
