package nc.ui.erm.matterapp.model;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.components.pagination.IPaginationQueryService;
import nc.ui.uif2.model.IAppModelService;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 * 事项审批modelservice
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
		return billSave(object, false);
	}

	@Override
	public Object update(Object object) throws Exception {
		return billSave(object, true);
	}

	private Object billSave(Object object, boolean isEdit) throws Exception {
		AggMatterAppVO returnObj = null;
		if (isEdit) {
			returnObj = getMgService().updateVO((AggMatterAppVO) object);
		} else {
			returnObj = getMgService().insertVO((AggMatterAppVO) object);
		}

		// 显示预算，借款控制的提示信息
		if (!StringUtils.isNullWithTrim(returnObj.getParentVO().getWarningmsg())) {
			MessageDialog.showWarningDlg(null, nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
																													 * @
																													 * res
																													 * "警告"
																													 */, returnObj.getParentVO().getWarningmsg());
			returnObj.getParentVO().setWarningmsg(null);
		}

		return returnObj;
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
