package nc.ui.erm.expamortize.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class ExpamortizeModelService implements IAppModelService,IPaginationQueryService{
	
	private ExpamorizeManageModel model;

	@Override
	public void delete(Object object) throws Exception {

	}

	@Override
	public Object insert(Object object) throws Exception {
		return null;
	}

	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context)
			throws Exception {
		return null;
	}

	@Override
	public Object update(Object object) throws Exception {
		return null;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQryService().queryExpamtinfoByPks(pks, getModel().getPeriod());
	}
	private IExpAmortizeinfoQuery qryService;

	public IExpAmortizeinfoQuery getQryService() {
		if(qryService == null){
			qryService = NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class);
		}
		return qryService;
	}

	public void setModel(ExpamorizeManageModel model) {
		this.model = model;
	}
	
	private ExpamorizeManageModel getModel(){
		return model;
	}
}
