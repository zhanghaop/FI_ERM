package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IplatFormEntry;
import nc.pubitf.para.SysInitQuery;
import nc.uap.rbac.core.dataperm.DataPermissionFacade;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pf.change.PfUtilUITools;
import nc.ui.pf.workitem.BatchApproveModel;
import nc.ui.pf.workitem.BatchApproveWorkitemAcceptDlg;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.NCAsynAction;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uif2.LoginContext;
import nc.vo.wfengine.core.parser.XPDLNames;

import org.apache.commons.lang.StringUtils;

/**
 * 费用审批按钮
 * <br>支持批量审批
 * 
 * @author chenshuaia
 * 
 */
public abstract class ErmAuditAction extends NCAsynAction {
	private static final long serialVersionUID = 1L;

	// 以下实现数据权限需要
	private String mdOperateCode = null; // 元数据操作编码
	private String operateCode = null; // 资源对象操作编码，以上两者注入其一，都不注入，则不进行数据权限控制。
	private String resourceCode = null; // 业务实体资源编码

	private BillManageModel model;

	private BillForm editor;
	
	private String actionCode = IPFActionName.APPROVE;

	/**
	 * 审批结果信息
	 */
	protected MessageVO[] msgs = null;

	private TPAProgressUtil tpaProgressUtil;
	
	protected IProgressMonitor monitor = null;
	
	public ErmAuditAction(){
		ActionInitializer.initializeAction(this, IActionCode.APPROVE);
	}

	/**
	 * 单个单据审批
	 * 
	 * @param aggvo
	 * @return
	 * @throws Exception
	 */
	protected abstract MessageVO approveSingle(AggregatedValueObject aggvo) throws Exception;

	protected void executeBatchAudit(List<AggregatedValueObject> auditVOs) throws Exception, BusinessException {
		// 审核动作处理
		List<MessageVO> result = null;
		Map<String, List<AggregatedValueObject>> typeMap = getTradeTypeAggVoMap(auditVOs);
		
		if(typeMap.keySet().size() > 1){//管理节点
			result = executeBatchManagerNodeAudit(typeMap);
		}else{
			result = executeBatchTradeNodeAudit(auditVOs);
		}
		
		if(result == null){//取消操作则直接返回
			return;
		}

		List<AggregatedValueObject> auditedVos = ErUiUtil.combineMsgs(msgs, result.toArray(new MessageVO[0]));
		getModel().directlyUpdate(auditedVos.toArray(new AggregatedValueObject[auditedVos.size()]));
		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}
	
	/**
	 * 录入节点/单交易类型单一的数据集合审批
	 * 
	 * @param auditVOs
	 * @param typeMap
	 * @return
	 * @throws Exception
	 */
	protected List<MessageVO> executeBatchTradeNodeAudit(List<AggregatedValueObject> auditVOs) throws Exception {
		String tradeType = (String) auditVOs.get(0).getParentVO().getAttributeValue("pk_tradetype");
		if (tradeType == null) {
			tradeType = (String) (String) auditVOs.get(0).getParentVO().getAttributeValue("djlxbm");
		}

		PfProcessBatchRetObject retObject = null;
		try {
			String actionName = getActionCode((String) auditVOs.get(0).getParentVO().getAttributeValue("pk_org"));
			// 批量审批
			retObject = PfUtilClient.runBatchNew(getModel().getContext().getEntranceUI(), actionName, tradeType,
					auditVOs.toArray(new AggregatedValueObject[0]), null, null, null);
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

		if (retObject == null) {
			return null;
		}

		List<MessageVO> result = dealRetObject(auditVOs, retObject);

		return result;
	}
	

	/**
	 * 管理节点批量审批 管理节点存在多交易类型的情况，需要特殊处理
	 * 
	 * @param auditVOs
	 * @param result
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected List<MessageVO> executeBatchManagerNodeAudit(Map<String, List<AggregatedValueObject>> typeMap) throws BusinessException {
		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();//所有单据
		for (Map.Entry<String, List<AggregatedValueObject>> entry : typeMap.entrySet()) {
			auditVOs.addAll(entry.getValue());
		}
		
		List<MessageVO> result = new ArrayList<MessageVO>();//结果
		String[] billIds = VOUtils.getAttributeValues(auditVOs.toArray(new AggregatedValueObject[0]), null);

		BatchApproveModel batchApproveMode = new BatchApproveModel();
		if (typeMap.size() > 1) {
			batchApproveMode.setSingleBillSelected(false);
		} else {
			batchApproveMode.setSingleBillSelected(true);
		}

		batchApproveMode.setMessageUI(true);
		batchApproveMode.setBillItem(auditVOs.size());

		// 获得审批意见, 放在WorkflownoteVO中
		WorkflownoteVO noteVO = new WorkflownoteVO();
		try {// CA签字
			boolean isNeedCASign = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
					.isNeedCASign4Batch(PfUtilUITools.getLoginUser(), typeMap.keySet().toArray(new String[0]), billIds);
			if (isNeedCASign) {
				noteVO.getRelaProperties().put(XPDLNames.ELECSIGNATURE, "true");
			}
		} catch (BusinessException e2) {
			ExceptionHandler.handleException(e2);
		}

		BatchApproveWorkitemAcceptDlg dlg = new BatchApproveWorkitemAcceptDlg(this.getEditor(), noteVO);
		dlg.setBachApproveMode(batchApproveMode);

		if (!(dlg.showModal() == UIDialog.ID_OK)) {
			return null;
		}

		String actionName = getActionCode((String) auditVOs.get(0).getParentVO().getAttributeValue("pk_org")) + WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();

		for (Map.Entry<String, List<AggregatedValueObject>> entry : typeMap.entrySet()) {
			List<AggregatedValueObject> aggVosTmepList = entry.getValue();
			PfProcessBatchRetObject retObject = null;
			String billType = entry.getKey();

			try {
				@SuppressWarnings("rawtypes")
				HashMap currParam = new HashMap();
				WorkflownoteVO currNote = (WorkflownoteVO) noteVO.clone();
				currParam.put(PfUtilBaseTools.PARAM_WORKNOTE, currNote);
				currParam.put(PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
				IplatFormEntry platFormService = NCLocator.getInstance().lookup(IplatFormEntry.class);
				retObject = (PfProcessBatchRetObject) platFormService.processBatch(actionName, billType, currNote, aggVosTmepList.toArray(new AggregatedValueObject[0]), null, currParam);// 批量审批
			} catch (Exception ex) {
				MessageVO messageVo = new MessageVO(aggVosTmepList.get(0), ActionUtils.AUDIT);
				messageVo.setSuccess(false);
				messageVo.setErrorMessage(ex.getMessage());
				result.add(messageVo);
				continue;
			}
			
			result.addAll(dealRetObject(entry.getValue(), retObject));
		}

		return result;
	}

	/**
	 * 根据交易类型将VO分组
	 * @param auditVos
	 * @param tradeTypeKey
	 * @return
	 */
	private Map<String, List<AggregatedValueObject>> getTradeTypeAggVoMap(List<AggregatedValueObject> auditVos) {
		if (auditVos == null || auditVos.size() == 0) {
			return null;
		}

		Map<String, List<AggregatedValueObject>> result = new HashMap<String, List<AggregatedValueObject>>();

		for (AggregatedValueObject auditVo : auditVos) {
			String tradeType = (String) auditVo.getParentVO().getAttributeValue(MatterAppVO.PK_TRADETYPE);
			if (tradeType == null) {
				tradeType = (String) auditVo.getParentVO().getAttributeValue(JKBXHeaderVO.DJLXBM);
				if (tradeType == null) {
					continue;
				}
			}

			if (result.get(tradeType) != null) {
				result.get(tradeType).add(auditVo);
			} else {
				List<AggregatedValueObject> tempAggVoList = new ArrayList<AggregatedValueObject>();
				tempAggVoList.add(auditVo);
				result.put(tradeType, tempAggVoList);
			}
		}
		return result;
	}
	
	/**
	 * 处理批量审批结果
	 * @param auditVOs
	 * @param retObject
	 * @return
	 */
	protected List<MessageVO> dealRetObject(List<AggregatedValueObject> auditVOs, PfProcessBatchRetObject retObject) {
		List<MessageVO> result = new ArrayList<MessageVO>();// 结果

		// 结果处理
		HashMap<Integer, String> errMap = retObject.getExceptionInfo() == null ? null : retObject
				.getExceptionInfo().getErrorMessageMap();

		MessageVO[] resultVos = (MessageVO[]) retObject.getRetObj();
		for (int i = 0; i < auditVOs.size(); i++) {
			String errorMsg = errMap == null ? null : errMap.get(Integer.valueOf(i));
			MessageVO messageVo = null;
			if (!StringUtils.isEmpty(errorMsg)) {
				messageVo = new MessageVO(auditVOs.get(i), ActionUtils.AUDIT);
				messageVo.setSuccess(false);
				messageVo.setErrorMessage(errorMsg);

			} else {
				messageVo = resultVos[i];
			}
			result.add(messageVo);
		}
		return result;
	}
	
	/**
	 * 权限校验
	 * @return
	 */
	protected boolean checkDataPermission(Object data) {
		if (StringUtil.isEmptyWithTrim(getOperateCode()) && StringUtil.isEmptyWithTrim(getMdOperateCode())
				|| StringUtil.isEmptyWithTrim(getResourceCode()))
			return true;

		LoginContext context = getModel().getContext();
		String userId = context.getPk_loginUser();
		String pkgroup = context.getPk_group();
		if(data == null){
			data = getModel().getSelectedData();
		}
		boolean hasp = true;
		if (!StringUtil.isEmptyWithTrim(getMdOperateCode()))
			hasp = DataPermissionFacade.isUserHasPermissionByMetaDataOperation(userId, getResourceCode(),
					getMdOperateCode(), pkgroup, data);
		else
			hasp = DataPermissionFacade.isUserHasPermission(userId, getResourceCode(), getOperateCode(), pkgroup, data);
		return hasp;
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

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;

	}
	
	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {
		if (monitor != null && !monitor.isDone()) {
			return false;
		}
		monitor = getTpaProgressUtil().getTPAProgressMonitor();
		monitor.beginTask("audit", -1);
		monitor.setProcessInfo("audit");
		return true;
	}
	
	@Override
	public void doAfterSuccess(ActionEvent actionEvent) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
	}

	@Override
	public boolean doAfterFailure(ActionEvent actionEvent, Throwable ex) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
		return true;
	}
	
	/**
	 * 获取动作脚本类型
	 * @param pk_org
	 * @return
	 */
	protected String getActionCode(String pk_org) {
		try {
			String paraString = SysInitQuery.getParaString(pk_org, BXParamConstant.ER_FLOW_TYPE);
			if (BXParamConstant.ER_FLOW_TYPE_WORKFLOW.equals(paraString)) {// 借款报销单位
				actionCode = IPFActionName.SIGNAL;
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return actionCode;
	}
}
