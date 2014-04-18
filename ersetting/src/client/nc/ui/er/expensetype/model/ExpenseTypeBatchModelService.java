package nc.ui.er.expensetype.model;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.expensetype.IExpenseTypeService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * @author liansg
 */
public class ExpenseTypeBatchModelService implements IBatchAppModelService,
		IPaginationQueryService {


	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
				

		return getExpenseTypeTypeService().batchSaveExpenseType(batchVO);
	}


	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		if (pks == null || pks.length == 0)
			return null;
		ExpenseTypeVO[] returnVOs = null;
		ArrayList<ExpenseTypeVO> qryResult = (ArrayList<ExpenseTypeVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKs(
						ExpenseTypeVO.class, pks, false);
		if (qryResult != null && qryResult.size() > 0) {
			returnVOs = qryResult.toArray(new ExpenseTypeVO[qryResult
					.size()]);
		}

		return returnVOs;
	}

	private IExpenseTypeService getExpenseTypeTypeService() {
		return NCLocator.getInstance().lookup(IExpenseTypeService.class);
	}


}
