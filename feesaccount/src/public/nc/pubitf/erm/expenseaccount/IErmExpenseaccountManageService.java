package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账维护服务
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountManageService {

	/**
	 * 新增费用账
	 * 
	 * @param accountVOs
	 * @return  
	 * 			费用明细账PK数组
	 * @throws BusinessException
	 */
	public void insertVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * 修改费用账
	 * 
	 * @param accountVOs
	 * @param oldaccountVOs
	 * @return 费用明细账PK数组
	 * @throws BusinessException
	 */
	public void updateVOs(ExpenseAccountVO[] accountVOs,ExpenseAccountVO[] oldaccountVOs) throws BusinessException;
	/**
	 * 删除费用账
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void deleteVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	
}
