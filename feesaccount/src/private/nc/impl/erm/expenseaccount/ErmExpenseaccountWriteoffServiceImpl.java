package nc.impl.erm.expenseaccount;

import nc.bs.erm.expenseaccount.ExpenseAccountBO;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountWriteoffService;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账冲销服务
 * 
 * @author lvhj
 *
 */
public class ErmExpenseaccountWriteoffServiceImpl implements IErmExpenseaccountWriteoffService{

	@Override
	public void writeoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.writeoffVOs(accountVOs);
	}

	@Override
	public void unWriteoffVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.unWriteoffVOs(accountVOs);
	}


}
