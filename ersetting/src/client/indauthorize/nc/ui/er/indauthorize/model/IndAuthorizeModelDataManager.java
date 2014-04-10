package nc.ui.er.indauthorize.model;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.er.indauthorize.IIndAuthorizeQueryService;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.AppEventListener;
import nc.ui.uif2.IExceptionHandler;
import nc.ui.uif2.components.pagination.IPaginationModelListener;
import nc.ui.uif2.model.BatchBillTableModel;
import nc.ui.uif2.model.IAppModelDataManager;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

public class IndAuthorizeModelDataManager implements IAppModelDataManager,
		IPaginationModelListener, AppEventListener {
	/**
	 * @author liansg
	 */
	private String[] userid = null;
	private BatchBillTableModel model = null;
	private IExceptionHandler exceptionHandler = null;
	private boolean  ismult = false;

	public boolean isIsmult() {
		return ismult;
	}

	public void setIsmult(boolean ismult) {
		this.ismult = ismult;
	}

	public BatchBillTableModel getModel() {
		return model;
	}

	public void setModel(BatchBillTableModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public IExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(IExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
    
//	public PaginationModel getPaginationModel() {
//		return paginationModel;
//	}
//
//	public void setPaginationModel(PaginationModel paginationModel) {
//		this.paginationModel = paginationModel;
//		this.paginationModel.addPaginationModelListener(this);
//	}
//
//	public BillManagePaginationDelegator getPaginationDelegator() {
//		return paginationDelegator;
//	}

//	public void setPaginationDelegator(
//			BillManagePaginationDelegator paginationDelegator) {
//		this.paginationDelegator = paginationDelegator;
//	}

	@Override
	public void onDataReady() {
//		paginationDelegator.onDataReady();
	}

	@Override
	public void onStructChanged() {

	}

	@Override
	public void handleEvent(AppEvent event) {
//		paginationDelegator.handleEvent(event);
	}

	public void initModel() {
		IndAuthorizeVO[] vos = null;
		String[] user = null;
		try {
			user = getOperatorUser();
		} catch (BusinessException e1) {
			ExceptionHandler.consume(e1);
		}
		if(user!=null &&  user.length!=0){
			String whereCond = "pk_group = '" + getContext().getPk_group() + "' and pk_user ='"+ user[0] + "' and type=1";
			try {
				vos = getIndAuthorizeQueryService().queryIndAuthorizes(
						whereCond);
				getModel().initModel(vos);
				
				
//				if (vos != null) {
//					for (IndAuthorizeVO vo : vos) {
//						pks.add(vo.getPk_authorize());
//					}
//				}
//				getPaginationModel().setObjectPks(pks.toArray(new String[0]));
			} catch (Exception e) {
				getExceptionHandler().handlerExeption(e);
			}
		}
		
		// getModel().initModel(vos);
	}

	private IIndAuthorizeQueryService getIndAuthorizeQueryService() {
		return NCLocator.getInstance().lookup(
				IIndAuthorizeQueryService.class);
	}

	private LoginContext getContext() {
		return getModel().getContext();
	}
	private String[] getOperatorUser() throws BusinessException {
		if(userid == null){
			String cid = ErUiUtil.getPk_user();
			String[] queryPsnidAndDeptid = null;
			try {
				//通过用户区查找业务员（代理人（用户）查找人员）
				queryPsnidAndDeptid = getIndAuthorizeQueryService().queryPsnidAndDeptid(cid, ErUiUtil.getPK_group());
			} catch (ComponentException e2) {
				ExceptionHandler.consume(e2);
			} catch (BusinessException e2) {
				throw e2;
			}
			if (queryPsnidAndDeptid == null)
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000065")/*@res "操作员对应的业务员为空，此节点不可用！"*/);
			else
				userid = queryPsnidAndDeptid;

		}
		return userid;
	}

}

