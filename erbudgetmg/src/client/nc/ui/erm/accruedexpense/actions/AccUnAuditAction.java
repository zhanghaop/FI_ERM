package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
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
	// ����ʵ������Ȩ����Ҫ
	private String mdOperateCode = null; // Ԫ���ݲ�������
	private String operateCode = null; // ��Դ����������룬��������ע����һ������ע�룬�򲻽�������Ȩ�޿��ơ�
	private String resourceCode = null; // ҵ��ʵ����Դ����
	
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

			// ��˽�����Ϣ
			MessageVO[] msgs = new MessageVO[objs.length];
			List<AggAccruedBillVO> unApproveList = new ArrayList<AggAccruedBillVO>();

			for (int i = 0; i < objs.length; i++) {
				AggAccruedBillVO vo = (AggAccruedBillVO) objs[i];
				
				//���ｫ������״̬�ĵ��ݹ��˵�������������
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
			if (!checkDataPermission(aggVo)) {//Ȩ��У��
				result = new MessageVO(aggVo, ActionUtils.UNAUDIT);
				result.setSuccess(false);
				result.setErrorMessage(IShowMsgConstant.getDataPermissionInfo());
				return result;
			}
			
			Object returnObj =  PfUtilClient.runAction(getBillForm().getParent(), "UNAPPROVE", aggVo
					.getParentVO().getPk_tradetype(), aggVo, null, null, null, null);
			
			if(returnObj ==null){//�����������У�������˽��棬Ȼ��ֱ�ӵ����ϽǵĹر�
				result = new MessageVO(aggVo, ActionUtils.UNAUDIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000339")/*@res "�û�ȡ������"*/);	
			}else{
				if (returnObj instanceof MessageVO[]) {
					MessageVO[] msgVos = (MessageVO[]) returnObj;
					result = msgVos[0];
				} else if (returnObj instanceof AggAccruedBillVO) {// ��ǩ�ͼ�ǩ������»���ַ���AggVo
					result = new MessageVO((AggAccruedBillVO) returnObj, ActionUtils.AUDIT);
				}
			}
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "��ǰ�����ѽ��м�������"
																													 */;
			if (e instanceof PFBusinessException && lockErrMsg.equals(errMsg)) {
				errMsg = e.getMessage()
						+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																													 * @
																													 * res
																													 * "���õ��ݿ���ͬʱ�����˲����У���ˢ�º�����"
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
		
		if (hasp) {//�����Ȩ��
			boolean isEnable = DataPermissionFacade.isEnableApproverPerm(userId, getResourceCode(), pkgroup, true);

			if (isEnable) {
				String approver = aggVo.getParentVO().getApprover();
				if (!userId.equals(approver)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0153")/*
											 * @res "�ü��������������Ȩ�ޣ�������˱����������!"
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
		Integer approveStatus = itf.getApproveStatus();// ����״̬

		if (!approveStatus.equals(IBillStatus.CHECKGOING) && !approveStatus.equals(IBillStatus.CHECKPASS) && !approveStatus.equals(IBillStatus.NOPASS)) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("201212_0", "���ݵ�ǰ״̬���ܽ��з�������", "0201212-0103"));
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
			// ����ĵ��ݲ�������Ч��ԭԤ�ᵥҲ��������Ч
			if (aggBean.getParentVO().getRedflag() != null
					&& (ErmAccruedBillConst.REDFLAG_RED == aggBean.getParentVO().getRedflag() || ErmAccruedBillConst.REDFLAG_REDED == aggBean
							.getParentVO().getRedflag())) {
				return false;
			}

			Integer appStatus = (aggBean.getParentVO()).getApprstatus();
			// �����
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
