package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账生效服务
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountApproveService {

	/**
	 *费用账生效
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void signVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * 费用账取消生效
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unsignVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 *费用账审批
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void approveVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * 费用账取消审批
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unApproveVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	
	
}
