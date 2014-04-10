package nc.bs.erm.expenseaccount;

import nc.bs.dao.BaseDAO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.pub.BusinessException;

/**
 * �����ʳ־û�
 * 
 * @author lvhj
 * 
 */
public class ExpenseAccountDAO {

	private BaseDAO basedao;

	private BaseDAO getBaseDAO() {
		if (basedao == null) {
			basedao = new BaseDAO();
		}
		return basedao;
	}

	public String[] insertAccountVO(ExpenseAccountVO[] vos) throws BusinessException {
//		String[] pks = ;
//		@SuppressWarnings("unchecked")
//		Collection<ExpenseAccountVO> c = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
//				ExpenseAccountVO.class, pks, true);
//
//		if (c == null || c.isEmpty()) {
//			return null;
//		}
		return getBaseDAO().insertVOArray(vos);
	}

	public String[] insertAccountBalanceVO(ExpenseBalVO[] vos) throws BusinessException {
//		@SuppressWarnings("unchecked")
//		Collection<ExpenseBalVO> c = getBaseDAO().retrieveByClause(ExpenseBalVO.class,
//				SqlTools.getInStr(ExpenseBalVO.PK_EXPENSEBAL, pks, true));
		
//		@SuppressWarnings("unchecked")
//		Collection<ExpenseBalVO> c = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPKs(
//				ExpenseBalVO.class, pks, true);
//		
//		if (c == null || c.isEmpty()) {
//			return null;
//		}
		return getBaseDAO().insertVOArray(vos);
	}

	public void updateAccountVO(ExpenseAccountVO[] vos, String[] fieldNames) throws BusinessException {
		getBaseDAO().updateVOArray(vos, fieldNames);
	}

	public void updateAccountBalanceVO(ExpenseBalVO[] vos, String[] fieldNames) throws BusinessException {
		getBaseDAO().updateVOArray(vos, fieldNames);
	}

	public void deleteAccountVO(ExpenseAccountVO[] vos) throws BusinessException {
		getBaseDAO().deleteVOArray(vos);
	}

	public void deleteAccountBalanceVO(ExpenseBalVO[] vos) throws BusinessException {
		getBaseDAO().deleteVOArray(vos);
	}

}
