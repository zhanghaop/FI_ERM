package nc.ui.erm.costshare.ui;

import nc.bs.framework.common.NCLocator;
import nc.itf.erm.costshare.IErmCostShareBillManagePrivate;
import nc.pubitf.erm.costshare.IErmCostShareBillApprove;
import nc.pubitf.erm.costshare.IErmCostShareBillManage;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.uif2.LoginContext;

public class CostShareModelService implements IAppModelService,IPaginationQueryService {
	

	public void delete(Object object) throws Exception {
	}
	
	public MessageVO[] newDelete(Object object) throws Exception {
		AggCostShareVO[] costArr = null;
		if(object.getClass().isArray()){
			costArr = (AggCostShareVO[]) object;
		}else {
			costArr = new AggCostShareVO[] {(AggCostShareVO) object};
		}
		return getMgService().deleteVOs((AggCostShareVO[]) costArr);
	}

	public Object insert(Object object) throws Exception {
		return getMgService().insertVO((AggCostShareVO) object);
	}

	public Object update(Object object) throws Exception {
		return getMgService().updateVO((AggCostShareVO) object);
	}

	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQryService().queryBillByPKs(pks);
	}
	
	public MessageVO[] confirm(Object object,UFDate busiDate) throws Exception {
		return getAppService().approveVOs((AggCostShareVO[]) object,busiDate);
	}
	public MessageVO[] unConfirm(Object object) throws Exception {
		return getAppService().unapproveVOs( (AggCostShareVO[]) object);
	}
	
	public AggCostShareVO[] printNormal(String[] pks,String businDate,String pk_user) throws Exception {
		return getIBillmagePrivateService().printNormal(pks,businDate,pk_user);
	}
	
	private IErmCostShareBillQuery qryService;

	public IErmCostShareBillQuery getQryService() {
		if(qryService == null){
			qryService = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class);
		}
		return qryService;
	}
	private IErmCostShareBillManage mgService;
	
	public IErmCostShareBillManage getMgService() {
		if(mgService == null){
			mgService = NCLocator.getInstance().lookup(IErmCostShareBillManage.class);
		}
		return mgService;
	}
	private IErmCostShareBillApprove appService;
	
	public IErmCostShareBillApprove getAppService() {
		if(appService == null){
			appService = NCLocator.getInstance().lookup(IErmCostShareBillApprove.class);
		}
		return appService;
	}
	private IErmCostShareBillManagePrivate iBillmagePrivate;
	
	public IErmCostShareBillManagePrivate getIBillmagePrivateService() {
		if(iBillmagePrivate == null){
			iBillmagePrivate = NCLocator.getInstance().lookup(IErmCostShareBillManagePrivate.class);
		}
		return iBillmagePrivate;
	}
	
	@Deprecated
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
	return null;
}

}
