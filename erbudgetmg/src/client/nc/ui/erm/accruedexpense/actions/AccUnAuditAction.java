package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.ErUtil;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.uif2.IActionCode;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.md.data.access.NCObject;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.IShowMsgConstant;
import nc.ui.uif2.NCAsynAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uif2.LoginContext;

public class AccUnAuditAction extends NCAsynAction {

	private static final long serialVersionUID = 1L;
	
	private AccManageAppModel model;
	private BillForm billForm;
	// 以下实现数据权限需要
	private String mdOperateCode = null; // 元数据操作编码
	private String operateCode = null; // 资源对象操作编码，以上两者注入其一，都不注入，则不进行数据权限控制。
	private String resourceCode = null; // 业务实体资源编码
	
	private IProgressMonitor monitor = null;

	private TPAProgressUtil tpaProgressUtil;
	
	public AccUnAuditAction() {
		ActionInitializer.initializeAction(this, IActionCode.UNAPPROVE);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {

			Object objs[] = getModel().getSelectedOperaDatas();

			if (objs == null || objs.length == 0) {
				return;
			}

			// 审核较验信息
			MessageVO[] msgs = new MessageVO[objs.length];
			List<AggAccruedBillVO> unApproveList = new ArrayList<AggAccruedBillVO>();

			for (int i = 0; i < objs.length; i++) {
				AggAccruedBillVO vo = (AggAccruedBillVO) objs[i];
				
				//这里将不符合状态的单据过滤掉，减少数据量
				msgs[i] = checkUnApprove(vo);

				if (!msgs[i].isSuccess()) {
					continue;
				}
				unApproveList.add(vo);
			}

			if (!unApproveList.isEmpty()) {
				MessageVO[] returnMsgs = unAuditOneByOne(unApproveList);
				List<AggregatedValueObject> unAuditedVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
				getModel().directlyUpdate(unAuditedVos.toArray(new AggregatedValueObject[] {}));
			}

			ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO[] unAuditOneByOne(List<AggAccruedBillVO> unApproveList) {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggAccruedBillVO aggVo : unApproveList) {
			MessageVO msgReturn = unApproveSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[]{});
	}

	private MessageVO unApproveSingle(AggAccruedBillVO aggVo) {
		MessageVO result = null;
		try {
			if (!checkDataPermission(aggVo)) {//权限校验
				result = new MessageVO(aggVo, ActionUtils.UNAUDIT);
				result.setSuccess(false);
				result.setErrorMessage(IShowMsgConstant.getDataPermissionInfo());
				return result;
			}
			
			String actionType = ErUtil.getUnApproveActionCode(aggVo.getParentVO().getPk_org());
			
			Object returnObj =  PfUtilClient.runAction(getBillForm().getParent(), actionType, aggVo
					.getParentVO().getPk_tradetype(), aggVo, null, null, null, null);
			
			if(returnObj ==null){//在审批过程中，弹出审核界面，然后直接点右上角的关闭
				result = new MessageVO(aggVo, ActionUtils.UNAUDIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000339")/*@res "用户取消操作"*/);	
			}else{
				if (returnObj instanceof MessageVO[]) {
					MessageVO[] msgVos = (MessageVO[]) returnObj;
					result = msgVos[0];
				} else if (returnObj instanceof AggAccruedBillVO) {// 改签和加签的情况下会出现返回AggVo
					result = new MessageVO((AggAccruedBillVO) returnObj, ActionUtils.AUDIT);
				}
			}
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "当前单据已进行加锁处理"
																													 */;
			if (e instanceof PFBusinessException && lockErrMsg.equals(errMsg)) {
				errMsg = e.getMessage()
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																													 * @
																													 * res
																													 * "，该单据可能同时被他人操作中，请刷新后再试"
																													 */;
			}
			
			result = new MessageVO(aggVo, ActionUtils.UNAUDIT, false, errMsg);
		}
		return result;
	}

	private boolean checkDataPermission(AggAccruedBillVO aggVo) throws BusinessException {
		if (StringUtil.isEmptyWithTrim(getOperateCode()) && StringUtil.isEmptyWithTrim(getMdOperateCode())
				|| StringUtil.isEmptyWithTrim(getResourceCode()))
			return true;
		
		LoginContext context = getModel().getContext();
		String userId = context.getPk_loginUser();
		String pkgroup = context.getPk_group();
		Object data = aggVo;
		boolean hasp = true;
		if (!StringUtil.isEmptyWithTrim(getMdOperateCode()))
			hasp = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(userId, getResourceCode(),
					getMdOperateCode(), pkgroup, data);
		else
			hasp = DataPermissionFacade.isUserHasPermission(userId, getResourceCode(), getOperateCode(), pkgroup, data);
		
		if (hasp) {//审核者权限
			boolean isEnable = DataPermissionFacade.isEnableApproverPerm(userId, getResourceCode(), pkgroup, true);

			if (isEnable) {
				String approver = aggVo.getParentVO().getApprover();
				if (!userId.equals(approver)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0153")/*
											 * @res "该集团启用了审核者权限，反审核人必须是审核人!"
											 */);
				}
			}
		}
		
		return hasp;
	}

	private MessageVO checkUnApprove(AggAccruedBillVO aggvo) {
		MessageVO msgVO = new MessageVO(aggvo, ActionUtils.UNAUDIT, true, "");

		NCObject ncObj = NCObject.newInstance(aggvo);
		IFlowBizItf itf = (IFlowBizItf) ncObj.getBizInterface(nc.itf.uap.pf.metadata.IFlowBizItf.class);
		Integer approveStatus = itf.getApproveStatus();// 审批状态

		if (!approveStatus.equals(IBillStatus.CHECKGOING) && !approveStatus.equals(IBillStatus.CHECKPASS) && !approveStatus.equals(IBillStatus.NOPASS)) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", "单据当前状态不能进行反审批！", "0201212-0103"));
		}
		return msgVO;
	}

	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {
		if (monitor != null && !monitor.isDone()) {
			return false;
		}

		monitor = getTpaProgressUtil().getTPAProgressMonitor();
		monitor.beginTask("unAudit", -1);
		monitor.setProcessInfo("unAudit");
		return true;
	}

	@Override
	public boolean doAfterFailure(ActionEvent actionEvent, Throwable ex) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
		return true;
	}

	@Override
	public void doAfterSuccess(ActionEvent actionEvent) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;
	}
	
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggAccruedBillVO aggBean = (AggAccruedBillVO) selectedData[i];
			// 红冲后的单据不允许反生效，原预提单也不允许反生效
			if (aggBean.getParentVO().getRedflag() != null
					&& (ErmAccruedBillConst.REDFLAG_RED == aggBean.getParentVO().getRedflag() || ErmAccruedBillConst.REDFLAG_REDED == aggBean
							.getParentVO().getRedflag())) {
				return false;
			}

			Integer appStatus = (aggBean.getParentVO()).getApprstatus();
			// 审核中
			if (appStatus.equals(IBillStatus.CHECKGOING) || appStatus.equals(IBillStatus.CHECKPASS) ||appStatus.equals(IBillStatus.NOPASS)) {
				return true;
			}
		}
		return false;
	}
	
	public AccManageAppModel getModel() {
		return model;
	}

	public void setModel(AccManageAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillForm getBillForm() {
		return billForm;
	}

	public void setBillForm(BillForm billForm) {
		this.billForm = billForm;
	}

	public String getMdOperateCode() {
		return mdOperateCode;
	}

	public void setMdOperateCode(String mdOperateCode) {
		this.mdOperateCode = mdOperateCode;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}

}
