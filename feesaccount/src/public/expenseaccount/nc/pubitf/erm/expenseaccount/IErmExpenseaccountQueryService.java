package nc.pubitf.erm.expenseaccount;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * �����ʵ��ݲ�ѯ����
 * 
 * @author lvhj
 *
 */
public interface IErmExpenseaccountQueryService {
	
	public ExpenseAccountVO queryByPK(String pk) throws BusinessException;
	
	public ExpenseAccountVO[] queryByPKs(String[] pks) throws BusinessException;
	
	public ExpenseAccountVO[] queryBySrcID(String[] srcIDS) throws BusinessException;

}
