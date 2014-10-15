package nc.ui.er.reimtype.model;

import java.util.ArrayList;

import nc.bs.framework.common.NCLocator;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.er.expensetype.IExpenseTypeService;
import nc.itf.er.reimtype.IReimTypeService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.uif2.LoginContext;

public class ReimTypeBatchModelService implements IBatchAppModelService,
		IPaginationQueryService {
	/**
	 * @author liansg
	 */
	private String modifier = WorkbenchEnvironment.getInstance().getLoginUser()
			.getPrimaryKey();
	private UFDateTime modifierTime = WorkbenchEnvironment.getServerTime();


	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		Object[] upds = batchVO.getUpdObjs();
//		if (upds != null) {
//			for (int i = 0; i < upds.length; i++) {
//				ExpenseTypeVO vo = (ExpenseTypeVO) upds[i];
//				vo.setModifier(modifier);
//				vo.setModifiedtime(modifierTime);
//				
//			}
//		}

		return getReimTypeService().batchSaveReimType(batchVO);
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
		ReimTypeVO[] returnVOs = null;
		ArrayList<ReimTypeVO> qryResult = (ArrayList<ReimTypeVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKs(
						ReimTypeVO.class, pks, false);
		if (qryResult != null && qryResult.size() > 0) {
			returnVOs = qryResult.toArray(new ReimTypeVO[qryResult
					.size()]);
		}

		return returnVOs;
	}

	private IReimTypeService getReimTypeService() {
		return NCLocator.getInstance().lookup(IReimTypeService.class);
	}


}
