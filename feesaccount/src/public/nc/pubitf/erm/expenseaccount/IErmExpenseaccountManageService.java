package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * ������ά������
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountManageService {

	/**
	 * ����������
	 * 
	 * @param accountVOs
	 * @return  
	 * 			������ϸ��PK����
	 * @throws BusinessException
	 */
	public void insertVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	/**
	 * �޸ķ�����
	 * 
	 * @param accountVOs
	 * @param oldaccountVOs
	 * @return ������ϸ��PK����
	 * @throws BusinessException
	 */
	public void updateVOs(ExpenseAccountVO[] accountVOs,ExpenseAccountVO[] oldaccountVOs) throws BusinessException;
	/**
	 * ɾ��������
	 * 
	 * @param accountVOs
	 * @throws BusinessException
	 */
	public void deleteVOs(ExpenseAccountVO[] accountVOs) throws BusinessException;
	
}
