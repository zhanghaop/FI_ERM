package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * �����˳�������
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountWriteoffService {

	/**
	 * ����������
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void writeoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;

	/**
	 * ȡ������������
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void unWriteoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;

}
