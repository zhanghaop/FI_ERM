package nc.impl.erm.expenseaccount;

import nc.bs.erm.expenseaccount.ExpenseAccountBO;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountApproveService;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账生效服务
 * 
 * @author lvhj
 *
 */
public class ErmExpenseaccountApproveServiceImpl implements IErmExpenseaccountApproveService{

	@Override
	public void signVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.signVOs(accountVOs);
	}

	@Override
	public void unsignVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.unSignVOs(accountVOs);
	}

	@Override
	public void approveVOs(ExpenseAccountVO[] accountVOs)
			throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.approveVOs(accountVOs);
	}

	@Override
	public void unApproveVOs(ExpenseAccountVO[] accountVOs)
			throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.unApproveVOs(accountVOs);
	}

	
	
}
