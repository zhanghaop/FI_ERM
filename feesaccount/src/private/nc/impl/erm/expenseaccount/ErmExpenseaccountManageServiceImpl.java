package nc.impl.erm.expenseaccount;

import nc.bs.erm.expenseaccount.ExpenseAccountBO;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;

/**
 * 费用账维护服务
 * 
 * @author lvhj
 *
 */
public class ErmExpenseaccountManageServiceImpl implements  IErmExpenseaccountManageService{

	@Override
	public void insertVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		if(accountVOs != null && accountVOs.length > 0){
			bo.insertVOs(accountVOs);
		}
	}

	@Override
	public void updateVOs(ExpenseAccountVO[] accountVOs,
			ExpenseAccountVO[] oldaccountVOs)
			throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		bo.updateVOs(accountVOs,oldaccountVOs);
	}

	@Override
	public void deleteVOs(ExpenseAccountVO[] accountVOs) throws BusinessException {
		ExpenseAccountBO bo = new ExpenseAccountBO();
		if(accountVOs != null && accountVOs.length > 0){
			bo.deleteVOs(accountVOs);
		}
	}

	
}
