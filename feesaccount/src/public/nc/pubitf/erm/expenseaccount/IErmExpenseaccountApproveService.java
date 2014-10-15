package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * ��������Ч����
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountApproveService {

	/**
	 *��������Ч
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void signVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * ������ȡ����Ч
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unsignVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 *����������
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void approveVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * ������ȡ������
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unApproveVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	
	
}
