package nc.ui.erm.matterapp.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 *  ¬œÓ…Û≈˙modelservice
 * @author chenshuaia
 *
 */
public class MatterAppManageModelService implements IAppModelService ,IPaginationQueryService{
	private IErmMatterAppBillManage mgService;
	
	private IErmMatterAppBillQuery queryService;

	@Override
	public void delete(Object object) throws Exception {
		AggMatterAppVO deleteVo = (AggMatterAppVO)object;
		nc.ui.pub.pf.PfUtilClient.runAction(null, "DELETE", deleteVo.getParentVO().getPk_billtype(), deleteVo,
				new AggMatterAppVO[] { deleteVo }, null, null, null);
	}

	@Override
	public Object insert(Object object) throws Exception {
		return getMgService().insertVO((AggMatterAppVO)object);
	}

	@Override
	public Object update(Object object) throws Exception {
		return getMgService().updateVO((AggMatterAppVO)object);
	}
	
	@Override
	public Object[] queryByDataVisibilitySetting(LoginContext context) throws Exception {
		return null;
	}
	
	private IErmMatterAppBillManage getMgService(){
		if(mgService == null){
			mgService = NCLocator.getInstance().lookup(IErmMatterAppBillManage.class);
		}
		return mgService;
	}
	
	private IErmMatterAppBillQuery getQueryService(){
		if(queryService == null){
			queryService = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		}
		return queryService;
	}

	@Override
	public Object[] queryObjectByPks(String[] pks) throws BusinessException {
		return getQueryService().queryBillByPKs(pks,true); 
	}
}
