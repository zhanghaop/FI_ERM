package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账冲销服务
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountWriteoffService {

	/**
	 * 冲销费用账
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void writeoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;

	/**
	 * 取消冲销费用账
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unWriteoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;

}
