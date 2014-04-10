package nc.ui.erm.accruedexpense.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class AccManageModelService implements IAppModelService, IPaginationQueryService {

	IErmAccruedBillQuery queryService;

	IErmAccruedBillManage manageService;

	@Override
	public void delete(Object object) throws Exception {
		AggAccruedBillVO deleteVo = (AggAccruedBillVO) object;
		nc.ui.pub.pf.PfUtilClient.runAction(null, "DELETE", deleteVo.getParentVO().getPk_billtype(), deleteVo,
				new AggAccruedBillVO[] { deleteVo }, null, null, null);
	}

	@Override
	public Object insert(Object object) throws Exception {
		return getManageService().insertVO((AggAccruedBillVO) object);
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		return getManageService().updateVO((AggAccruedBillVO) object);
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		if (pks != null && pks.length == 1) {
			return getQueryService().queryBillByPks(pks, false);
		}
		return getQueryService().queryBillByPks(pks, true);
	}

	public IErmAccruedBillQuery getQueryService() {
		if (queryService == null) {
			queryService = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
		}
		return queryService;
	}

	public IErmAccruedBillManage getManageService() {
		if (manageService == null) {
			manageService = NCLocator.getInstance().lookup(IErmAccruedBillManage.class);
		}
		return manageService;
	}

}
