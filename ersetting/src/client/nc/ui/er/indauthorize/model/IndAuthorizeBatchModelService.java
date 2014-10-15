package nc.ui.er.indauthorize.model;

import java.util.ArrayList;
import nc.bs.framework.common.NCLocator;
import nc.itf.er.indauthorize.IIndAuthorizeService;
import nc.md.persist.framework.MDPersistenceService;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IBatchAppModelService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class IndAuthorizeBatchModelService implements IBatchAppModelService,
		IPaginationQueryService  {
	/**
	 * @author liansg
	 */

	public BatchOperateVO batchSave(BatchOperateVO batchVO) throws Exception {
		return getIndAuthorizeService().batchSaveIndAuthorize(batchVO);
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
		IndAuthorizeVO[] returnVOs = null;
		ArrayList<IndAuthorizeVO> qryResult = (ArrayList<IndAuthorizeVO>) MDPersistenceService
				.lookupPersistenceQueryService().queryBillOfVOByPKs(
						IndAuthorizeVO.class, pks, false);
		if (qryResult != null && qryResult.size() > 0) {
			returnVOs = qryResult.toArray(new IndAuthorizeVO[qryResult
					.size()]);
		}

		return returnVOs;
	}

	private IIndAuthorizeService getIndAuthorizeService() {
		return NCLocator.getInstance().lookup(IIndAuthorizeService.class);
	}


}
