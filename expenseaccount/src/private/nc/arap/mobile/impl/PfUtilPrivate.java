package nc.arap.mobile.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.message.Attachment;
import nc.message.vo.AttachmentVO;
import nc.security.NCAuthenticator;
import nc.security.NCAuthenticatorFactory;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.AssignableInfo;
import nc.vo.pub.pf.PfAddInfo;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.pfflow01.BillbusinessVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.uap.pf.FlowDefNotFoundException;
import nc.vo.uap.pf.PFRuntimeException;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.wfengine.core.parser.XPDLNames;
import nc.vo.wfengine.definition.IApproveflowConst;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WfTaskType;

/**
 * 流程平台轻量级工具类
 * 
 */
public class PfUtilPrivate {

	/**
	 * 审批变量如果审批则true反之false;
	 */
	private static boolean m_checkFlag = true;

	// 当前单据类型
	private static String m_currentBillType = null;

	/** 当前审批节点的审批结果 */
	private static int m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;


	/** 源单据类型 */
	private static String m_sourceBillType = null;

	private static AggregatedValueObject m_tmpRetVo = null;

	private static AggregatedValueObject[] m_tmpRetVos = null;

	// 单据自制标志
	public static boolean makeFlag = false;

	private static IPfExchangeService exchangeService;

	private static int m_classifyMode = PfButtonClickContext.NoClassify;

	private PfUtilPrivate() {

	}

	/**
	 * 提交单据时,需要的指派信息
	 * <li>只有"SAVE","EDIT"动作才调用
	 */
	private static WorkflownoteVO checkOnSave(String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();

		//guowl+ 2010-5,如果是批处理，不用取指派信息，直接返回
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return worknoteVO;
		
		try {
			worknoteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
			.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
		}catch(FlowDefNotFoundException e) {
			return worknoteVO;
		}
		
//		//在审批处理框显示之前，调用业务处理
//		PFClientBizRetObj retObj = executeBusinessPlugin(billVo, worknoteVO, true);
//		if(retObj != null && retObj.isStopFlow()){
//			m_isSuccess = false;
//			return null;
//		}
		if (worknoteVO != null) {
			// 得到可指派的输入数据
			Vector assignInfos = worknoteVO.getTaskInfo().getAssignableInfos();
			if (assignInfos != null && assignInfos.size() > 0) {
				// 显示指派对话框并收集实际指派信息
//				DispatchDialog dd = new DispatchDialog(parent);
//				dd.initByWorknoteVO(worknoteVO);
//				int iClose = dd.showModal();
//				if (iClose == UIDialog.ID_CANCEL)
//					dlgResult.push(Integer.valueOf(iClose));
			}
		}
		return worknoteVO;
	}

	/**
	 * 单据启动工作流时,需要的指派信息
	 * <li>包括选择后继活动参与者、选择后继分支转移
	 */
	private static WorkflownoteVO checkOnStart(String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO wfVo = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(
				actionName, billType, billVo, hmPfExParams);

		//guowl+ 2010-5,如果是批处理，不用取指派信息，直接返回
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return wfVo;
		if (wfVo != null) {
			// 得到可指派的信息
			Vector assignInfos = wfVo.getTaskInfo().getAssignableInfos();
			Vector tSelectInfos = wfVo.getTaskInfo().getTransitionSelectableInfos();
			if (assignInfos.size() > 0 || tSelectInfos.size() > 0) {
				// 显示指派对话框并收集实际指派信息
//				WFStartDispatchDialog wfdd = new WFStartDispatchDialog(wfVo);
//				int iClose = wfdd.showModal();
//				if (iClose == UIDialog.ID_CANCEL)
//					dlgResult.push(Integer.valueOf(iClose));
			}
		}
		return wfVo;
	}
	

	/**
	 * 检查当前单据是否处于审批流程中，并进行交互
	 */
	private static WorkflownoteVO checkWorkitemWhenApprove(String actionName, String billType, AggregatedValueObject billVo,
			HashMap hmPfExParams,int voAryLen) throws BusinessException {
		WorkflownoteVO noteVO = null;
		if (!hasApproveflowDef(billType, billVo)) {
			//如果没有定义审批流,则默认通过 
			noteVO = new WorkflownoteVO();
			noteVO.setApproveresult("Y");
			Logger.debug("*checkWorkitemWhenApprove 1 billType.");
			return noteVO;
		}
		noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
		Logger.debug("*checkWorkitemWhenApprove 3.");
		
		Object notSilent = null;
		if(hmPfExParams != null){
			notSilent = hmPfExParams.get(PfUtilBaseTools.PARAM_NOTSILENT);
		}

		if (noteVO == null) {
			if (notSilent == null) {
				m_checkFlag = true;
				Logger.debug("*checkWorkitemWhenApprove 1 billType.");
				return noteVO;
			} else {
				noteVO = new WorkflownoteVO();
			}
		}
					
	    if (hmPfExParams == null) {
	        hmPfExParams = new HashMap<String, Object>();
	    }
	    if((!hmPfExParams.containsKey(PfUtilBaseTools.PARAM_WORKNOTE) || hmPfExParams.get(PfUtilBaseTools.PARAM_WORKNOTE) == null)
				&& hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
	    	hmPfExParams.put(PfUtilBaseTools.PARAM_WORKNOTE, noteVO);
		return noteVO;
	}
	
	
	/**
	 * 侧边栏审批
	 * */
	private static WorkflownoteVO  BesideApprove(HashMap hmPfExParams,WorkflownoteVO noteVO){
	
		return noteVO;
	}
	
	
	//上传服务器，不影响流程
	private static List<AttachmentVO> updateAttachment2DocServer(List<Attachment> attchlist) {
		List<AttachmentVO> vos = new ArrayList<AttachmentVO>();
		try {
			for (Attachment attachment : attchlist) {
				AttachmentVO vo = attachment.uploadToFileServer();
				vo.setFilesize(attachment.getSize());
				vo.setFilename(attachment.getName());
				vos.add(vo);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage() + "上传附件失败！");
		}
		return vos;
	}

	private static boolean hasApproveflowDef(String billType, AggregatedValueObject billVo)throws BusinessException {
		IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(billVo, IFlowBizItf.class);
		if (fbi == null)
			throw new PFRuntimeException("元数据实体没有提供业务接口IFlowBizItf的实现类");

		IWorkflowDefine wfDefine = NCLocator.getInstance().lookup(IWorkflowDefine.class);
		Logger.debug("查询流程定义: billType=" + billType + ";pkOrg=" + fbi.getPkorg() + ";userId=" + fbi.getBillMaker() + ";开始");
		return wfDefine.hasValidProcessDef(InvocationInfoProxy.getInstance().getGroupId(), billType, fbi.getPkorg(), fbi.getBillMaker(), fbi.getEmendEnum(),WorkflowTypeEnum.Approveflow.getIntValue());
	}


	/**
	 * 返回 当前审批节点的处理结果 lj+ 2005-1-20
	 */
	public static int getCurrentCheckResult() {
		return m_iCheckResult;
	}

	/**
	 * 返回 用户选择的VO
	 */
	public static AggregatedValueObject getRetOldVo() {
		return m_tmpRetVo;
	}

	/**
	 * 返回 用户选择VO数组.
	 */
	public static AggregatedValueObject[] getRetOldVos() {
		return m_tmpRetVos;
	}

	/**
	 * 返回 用户选择的VO或交换过的VO
	 * 
	 * @return
	 */
	public static AggregatedValueObject getRetVo() {
		try {
			// 需要进行VO交换
			m_tmpRetVo = getExchangeService().runChangeData(m_sourceBillType, m_currentBillType, m_tmpRetVo, null);
			jumpBusitype(m_tmpRetVo==null?null:new AggregatedValueObject[]{m_tmpRetVo});
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new PFRuntimeException("VO交换错误");
		}
		return m_tmpRetVo;
	}

	/**
	 * 返回 用户选择VO数组或交换过的VO数组
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	public static AggregatedValueObject[] getRetVos() throws BusinessException {
		//默认根据业务流程进行VO交换
		return getRetVos(true);
	}
	
	/**
	 * 返回 用户选择VO数组或交换过的VO数组
	 * 
	 * @param exchangeByBusiType 是否根据业务流程进行交换，如果否，则在交换前将来源业务流程PK清除
	 * @return
	 * @throws BusinessException 
	 */
	public static AggregatedValueObject[] getRetVos(boolean exchangeByBusiType) throws BusinessException {
		// 如果不根据业务流程进行交换，则将单据中的业务流程PK去掉(供应链的需求，来源单据有业务流程pK，但是想走的是另外的流程)
		if(!exchangeByBusiType && m_tmpRetVos != null) {
			for(int i=0; i < m_tmpRetVos.length; i++) {
				AggregatedValueObject aggVO = m_tmpRetVos[i];
				IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(aggVO, IFlowBizItf.class);
				if(fbi != null) {
					fbi.setBusitype(null);
				}
			}
		}
		// 需要进行VO交换
		m_tmpRetVos = changeVos(m_tmpRetVos, m_classifyMode);
		jumpBusitype(m_tmpRetVos);
		return m_tmpRetVos;
	}

	private static IPfExchangeService getExchangeService() {
		if(exchangeService==null)
			exchangeService = NCLocator.getInstance().lookup(IPfExchangeService.class);
		return exchangeService;
	}

	/**
	 * 返回 用户选择的VO或交换过的VO
	 * 
	 * @return
	 */
	private static AggregatedValueObject[] changeVos(AggregatedValueObject[] vos, int classifyMode) {
		AggregatedValueObject[] tmpRetVos = null;

		try {
			tmpRetVos = getExchangeService().runChangeDataAryNeedClassify(m_sourceBillType, m_currentBillType, vos, null, classifyMode);
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
			throw new PFRuntimeException("VO交换错误");
		}

		return tmpRetVos;
	}
	
	
	/**
	 * 业务流跳转
	 * @throws BusinessException 
	 * */
	private static void jumpBusitype(AggregatedValueObject[] vos) throws BusinessException{
		if(vos==null||vos.length==0)
			return;
		IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(vos[0], IFlowBizItf.class);
		//未实现业务流接口的单据或者未走流程直接return
		if(fbi==null||StringUtil.isEmptyWithTrim(fbi.getBusitype()))
			return;
		BillbusinessVO condVO = new BillbusinessVO();
		condVO.setPk_businesstype(fbi.getBusitype());
		condVO.setJumpflag(UFBoolean.TRUE);
		//得到单据类型编码
		String billtype =PfUtilBaseTools.getRealBilltype(m_currentBillType);
		condVO.setPk_billtype(billtype);
		condVO.setTranstype(fbi.getTranstype());
		try {
			Collection co = NCLocator.getInstance().lookup(IUAPQueryBS.class).retrieve(condVO, true);
			if(co.size()>0){
				HashMap<String, String> busitypeMaps = new HashMap<String, String>();
				for(AggregatedValueObject vo:vos){
					String destBusitypePk = null;
					fbi = PfMetadataTools.getBizItfImpl(vo, IFlowBizItf.class);
					String transtype =fbi.getTranstype();
					String pk_org=fbi.getPkorg();
					String operator =InvocationInfoProxy.getInstance().getUserId();
					if(StringUtil.isEmptyWithTrim(billtype)){
						//单据类型不能为空
						continue;
					}
					String key =billtype+(StringUtil.isEmptyWithTrim(transtype)?"":transtype)+(StringUtil.isEmptyWithTrim(pk_org)?"":pk_org)
							   +operator;
					if(busitypeMaps.containsKey(key)){
						destBusitypePk = busitypeMaps.get(key);
					}else{
						destBusitypePk = NCLocator.getInstance().lookup(IPFConfig.class).retBusitypeCanStart(billtype, transtype, pk_org, operator);
					}
					//如果没有找到要跳转的业务流
					if(StringUtil.isEmptyWithTrim(destBusitypePk)){
						continue;
					}
					fbi.setBusitype(destBusitypePk);
				}
			}
		} catch (DAOException e) {
			Logger.error(e.getMessage(), e);
		}	
	}

	
	/**
	 *
	 * @param actionCode 动作编码，比如"SAVE"
	 * @param billOrTranstype 单据（或交易）类型PK
	 * @param billvo 单据聚合VO
	 * @param userObj 用户自定义对象
	 * @param checkVo 校验单据聚合VO
	 * @param eParam 扩展参数
	 * @return 动作处理的返回结果
	 * @throws BusinessException 
	 * @throws Exception
	 * @since 5.5
	 */
	public static Object runAction(String actionCode, String billOrTranstype,
			AggregatedValueObject billvo, Object userObj, BesideApproveContext besideContext,
			AggregatedValueObject checkVo, HashMap eParam) throws BusinessException {
		Logger.debug("*单据动作处理 开始");
		debugParams(actionCode, billOrTranstype, billvo, userObj);
		long start = System.currentTimeMillis();

		WorkflownoteVO worknoteVO = null;
		//得到审批信息
		if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype)
				|| PfUtilBaseTools.isApproveAction(actionCode,
						billOrTranstype)) {
			// 审批流交互处理
			worknoteVO = actionAboutApproveflow(actionCode,
					billOrTranstype, billvo, eParam,0);
			onApprove(besideContext,worknoteVO);
		} else if (PfUtilBaseTools.isStartAction(actionCode,
				billOrTranstype)
				|| PfUtilBaseTools.isSignalAction(actionCode,
						billOrTranstype)) {
			// 工作流互处理
			worknoteVO = actionAboutWorkflow(actionCode,
					billOrTranstype, billvo, besideContext,0);
		}
		
		if (worknoteVO == null) {
			//检查不到工作项，则后台无需再次检查
			if (eParam == null)
				eParam = new HashMap<String, String>();
			eParam.put(PfUtilBaseTools.PARAM_NOTE_CHECKED, PfUtilBaseTools.PARAM_NOTE_CHECKED);
		}

		// 4.后台执行动作
		Object retObj = null;
		
		Logger.debug("*后台动作处理 开始");
		long start2 = System.currentTimeMillis();
		IplatFormEntry iIplatFormEntry = (IplatFormEntry) NCLocator.getInstance().lookup(
				IplatFormEntry.class.getName());
		retObj = iIplatFormEntry.processAction(actionCode, billOrTranstype, worknoteVO, billvo,
				userObj, eParam);
		Logger.debug("*后台动作处理 结束=" + (System.currentTimeMillis() - start2) + "ms");

		// 5.返回对象执行
		//retObjRun(retObj);
		Logger.debug("*单据动作处理 结束=" + (System.currentTimeMillis() - start) + "ms");

		return retObj;
	}

	private static void onSignal(BesideApproveContext besideContext,
			WorkflownoteVO worknoteVO) {
		worknoteVO.setChecknote(besideContext.getCheckNote());
		worknoteVO.setApproveresult(besideContext.getApproveResult());
	}

	/**
	 * 审批通过或不通过
	 */
	protected static void onApprove(BesideApproveContext besideContext,WorkflownoteVO worknoteVO) {

		// 判断是否需要后继指派
		boolean isNeedDispatch = isExistAssignableInfoWhenPass(worknoteVO);
		if (isNeedDispatch) {
//			// 填充指派信息
//			getDispatchDialog().getDisPatchPanel().initByWorknoteVO(
//					worknoteVO,
//					pass ? AssignableInfo.CRITERION_PASS
//							: AssignableInfo.CRITERION_NOPASS);
//			int result = getDispatchDialog().showModal();
//			if (result == UIDialog.ID_CANCEL) {
//				// 如果指派对话框点击了取消，那么取消这次审批操作，重新回到审批面板 （changlx需求定义）modified by
//				// zhangrui 2012-04-17
//				return;
//			}
		}
		if (!beforeButtonOperate(worknoteVO))
			return;

		//设置当前审批人是否对流程进行跟踪
		worknoteVO.setTrack(false);
		worknoteVO.setChecknote(besideContext.getCheckNote());
		worknoteVO.setApproveresult(besideContext.getApproveResult());
	}
	
	private static boolean beforeButtonOperate(WorkflownoteVO worknoteVO) {
		// 抄送
		// @modifier yanke1 2011-7-15 设置抄送人信息
		// 无论是否批准都进行抄送
//		worknoteVO.setMailExtCpySenders(getCopySendDialog().getCpySendPanel().getMailVOs());
//		worknoteVO.setMsgExtCpySenders(getCopySendDialog().getCpySendPanel().getMsgVOs());

		String checkNote ="批准Y";
		//String result =ApproveWorkitemAssistor.sign(worknoteVO,note);
		String result = null;
		if(!isNeedCASign(worknoteVO))
			result = null;
		NCAuthenticator authenticator;
		try {
			authenticator = NCAuthenticatorFactory.getBusiAuthenticator(InvocationInfoProxy.getInstance().getUserId());
			String signResult =authenticator.sign(worknoteVO.getApproveresult()+checkNote);
			worknoteVO.setCiphertext(signResult);
		} catch (Exception e) {
			result =e.getMessage();
			Logger.error(e.getMessage());
		}
		return StringUtil.isEmptyWithTrim(result);
	}
	/**
	 * 环节是否需要CA签名
	 * */
	private static boolean isNeedCASign(WorkflownoteVO worknoteVO){
		Object value = worknoteVO.getRelaProperties().get(XPDLNames.ELECSIGNATURE);
		if (value != null && "true".equalsIgnoreCase(value.toString())) {
			return true;
		} else
			return false;
	}
	/**
	 * 批准时，是否存在可指派的后继活动
	 *
	 * @return
	 */
	private static boolean isExistAssignableInfoWhenPass(WorkflownoteVO worknoteVO) {
		if (worknoteVO.getActiontype().endsWith(
				WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX))
			return false;

		Vector<AssignableInfo> assignInfos = worknoteVO.getTaskInfo()
				.getAssignableInfos();
		if (assignInfos != null && assignInfos.size() > 0) {
			String strCriterion = null;
			for (AssignableInfo ai : assignInfos) {
				strCriterion = ai.getCheckResultCriterion();
				if (AssignableInfo.CRITERION_NOTGIVEN.equals(strCriterion)
						|| AssignableInfo.CRITERION_PASS.equals(strCriterion))
					return true;
			}
		}
		return false;
	}
	/**
	 * 不批准时，是否存在可指派的后继活动
	 *
	 * @return
	 */
	private static boolean isExistAssignableInfoWhenNopass(WorkflownoteVO worknoteVO) {
		if (worknoteVO.getActiontype().endsWith(
				WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX))
			return false;

		Vector<AssignableInfo> assignInfos = worknoteVO.getTaskInfo()
				.getAssignableInfos();
		if (assignInfos != null && assignInfos.size() > 0) {
			String strCriterion = null;
			for (AssignableInfo ai : assignInfos) {
				strCriterion = ai.getCheckResultCriterion();
				if (AssignableInfo.CRITERION_NOTGIVEN.equals(strCriterion)
						|| AssignableInfo.CRITERION_NOPASS.equals(strCriterion))
					return true;
			}
		}
		return false;
	}
	
	private static Object getParamFromMap(HashMap eParam, String paramKey) {
		return eParam == null ? null : eParam.get(paramKey);
	}

	/**
	 * 审批流相关的交互处理
	 * @throws BusinessException 
	 */
	private static WorkflownoteVO actionAboutApproveflow(String actionName,
			String billType, AggregatedValueObject billvo, HashMap eParam,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isSaveAction(actionName, billType)) {
			Logger.debug("*提交动作=" + actionName + "，检查审批流");
			// 如果为提交动作，可能需要收集提交人的指派信息，这里统一动作名称 lj@2005-4-8
			Stack dlgResult = new Stack();
			worknoteVO = checkOnSave(IPFActionName.SAVE, billType, billvo, dlgResult, eParam);
		} else if (PfUtilBaseTools.isApproveAction(actionName, billType)) {
			Logger.debug("*审批动作=" + actionName + "，检查审批流");
			// 检查该单据是否处于审批流中，并收集审批人的审批信息
			worknoteVO = checkWorkitemWhenApprove(actionName, billType, billvo, eParam,voAryLen);
			if (worknoteVO != null) {
				if ("Y".equals(worknoteVO.getApproveresult())) {
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;
				} else if("R".equals(worknoteVO.getApproveresult())) {
					// XXX::驳回也作为审批通过的一种,需要继续判断 lj+
					WFTask currTask = worknoteVO.getTaskInfo().getTask();
					if (currTask != null && currTask.getTaskType() == WfTaskType.Backward.getIntValue()) {
						if (currTask.isBackToFirstActivity())
							m_iCheckResult = IApproveflowConst.CHECK_RESULT_REJECT_FIRST;
						else
							m_iCheckResult = IApproveflowConst.CHECK_RESULT_REJECT_LAST;
					}
				} else
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_NOPASS;
			} else if (!m_checkFlag) {
				Logger.debug("*用户审批时点击了取消，则停止审批");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * 工作流相关的交互处理
	 * @throws BusinessException 
	 */
	private static WorkflownoteVO actionAboutWorkflow(String actionName,
			String billType, AggregatedValueObject billvo, BesideApproveContext besideContext,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isStartAction(actionName, billType)) {
			Logger.debug("*启动动作=" + actionName + "，检查工作流");
			worknoteVO = checkOnStart(actionName, billType, billvo, null, null);
		} else if (PfUtilBaseTools.isSignalAction(actionName, billType)) {
			Logger.debug("*执行动作=" + actionName + "，检查工作流");
			// 检查该单据是否处于工作流中
			worknoteVO = checkWorkitemWhenSignal(actionName, billType, billvo, besideContext, voAryLen);
			if (worknoteVO != null) {
				if ("Y".equals(worknoteVO.getApproveresult())) {
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;
				} else if("R".equals(worknoteVO.getApproveresult())) {
					// XXX::驳回也作为审批通过的一种,需要继续判断 lj+
					WFTask currTask = worknoteVO.getTaskInfo().getTask();
					if (currTask != null && currTask.getTaskType() == WfTaskType.Backward.getIntValue()) {
						if (currTask.isBackToFirstActivity())
							m_iCheckResult = IApproveflowConst.CHECK_RESULT_REJECT_FIRST;
						else
							m_iCheckResult = IApproveflowConst.CHECK_RESULT_REJECT_LAST;
					}
				} else
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_NOPASS;
			} else if (!m_checkFlag) {
				Logger.debug("*用户驱动工作流时点击了取消，则停止执行工作流");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * 检查当前单据是否处于工作流程中或工作流的审批子流程中，并进行交互
	 */
	private static WorkflownoteVO checkWorkitemWhenSignal(String actionCode,
			String billType, AggregatedValueObject billVo, BesideApproveContext besideContext, int voAryLen) throws BusinessException {
		//检查当前用户的工作流工作项+审批子流程工作项
		WorkflownoteVO noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(actionCode,
					billType, billVo, null);
		if (noteVO.getWorkflow_type() == WorkflowTypeEnum.SubWorkApproveflow.getIntValue()) {
			//工作流的审批子流程
			if(besideContext.getApproveResult().equals("Y")){
				//批准
				onApprove(besideContext,noteVO);
			}else if(besideContext.getApproveResult().equals("R")){
				//驳回
				noteVO.setChecknote(besideContext.getCheckNote());
				noteVO.getTaskInfo().getTask().setTaskType(WfTaskType.Backward.getIntValue());
		    	noteVO.getTaskInfo().getTask().setBackToFirstActivity(besideContext.isBackToFirstActivity());
			    noteVO.getTaskInfo().getTask().setJumpToActivity(besideContext.getJumpToActivity());
			    noteVO.setApproveresult("R");
			}else if(besideContext.getApproveResult().equals("R")){
				//加签
				if(!canAddApprover(noteVO))
					throw new BusinessException("该单据不支持加签！");
				noteVO.setChecknote(besideContext.getCheckNote());
				noteVO.getTaskInfo().getTask().setTaskType(WfTaskType.Backward.getIntValue());
		    	noteVO.getTaskInfo().getTask().setBackToFirstActivity(besideContext.isBackToFirstActivity());
			    noteVO.getTaskInfo().getTask().setJumpToActivity(besideContext.getJumpToActivity());
			    noteVO.setApproveresult("R");
			}
		}else{
			onSignal(besideContext,noteVO);
		}
		return noteVO;
	}
	
	/**
	 * 是否可以加签
	 * @return
	 */
	private static boolean canAddApprover(WorkflownoteVO noteVO) {
		Object value = noteVO.getRelaProperties().get(
				XPDLNames.CAN_ADDAPPROVER);
		if (value != null && "true".equalsIgnoreCase(value.toString())) {
			if (noteVO.actiontype
					.equalsIgnoreCase(WorkflownoteVO.WORKITEM_TYPE_APPROVE
							+ WorkflownoteVO.WORKITEM_ADDAPPROVER_SUFFIX))
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * 日志一下动作处理的上下文参数
	 */
	private static void debugParams(String actionCode, String billType, Object billEntity,
			Object userObj) {
		Logger.debug("*********************************************");
		Logger.debug("* actionCode=" + actionCode);
		Logger.debug("* billType=" + billType);
		Logger.debug("* billEntity=" + billEntity);
		Logger.debug("* userObj=" + userObj);
		Logger.debug("*********************************************");
	}
	
	
	/**
	 * 
	 * @since 6.3 批审时候预先判断是否需要CA
	 * */
	private static void putElecsignatureValue(String billOrTranstype,AggregatedValueObject[] voAry,HashMap eParam) throws BusinessException{
//        //判断是否需要CA
//		List<String> billIdList =new ArrayList<String>();
//		for(AggregatedValueObject vo:voAry){
//			billIdList.add(vo.getParentVO().getPrimaryKey());
//		}
//		
//		boolean  isNeedCASign= NCLocator.getInstance().lookup(IPFWorkflowQry.class).isNeedCASign4Batch(PfUtilUITools.getLoginUser(), new String[]{billOrTranstype}, billIdList.toArray(new String[0]));
//		
//		if(isNeedCASign){
//			putParam(eParam, XPDLNames.ELECSIGNATURE, "Y");
//		}
	}
	
	
	private static void putElecsignatureRelaProperties(WorkflownoteVO noteVO,HashMap hmPfExParams){
		//批量审批时是否需要进行CA签名
		Object isNeedCASign =hmPfExParams.get(XPDLNames.ELECSIGNATURE);
		if(isNeedCASign!=null&&isNeedCASign.toString().equals("Y")){
			noteVO.getRelaProperties().put(XPDLNames.ELECSIGNATURE, "true");
		}
	}
	
	
	
	/**
	 * 
	 * @param actionCode 动作编码，比如"SAVE"
	 * @param billOrTranstype 单据类型（或交易类型）PK
	 * @param voAry 单据聚合VO数组
	 * @param userObjAry 用户自定义对象数组
	 * @param eParam 扩展参数
	 * @return 动作批处理的返回结果
	 * @throws Exception
	 * @since 5.5
	 */
	public static Object[] runBatch(String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, BesideApproveContext besideContext, HashMap eParam)
			throws Exception {
		Logger.debug("*单据动作批处理 开始");
		debugParams(actionCode, billOrTranstype, voAry, userObjAry);
		long start = System.currentTimeMillis();
		if(voAry!= null && voAry.length == 1) {
			Object obj = runAction(actionCode, billOrTranstype, voAry[0], userObjAry, besideContext, null, eParam);
			Object[] ret = null;
			ret = PfUtilBaseTools.composeResultAry(obj,1,0,ret);
			return ret;
		}
		
		putElecsignatureValue(billOrTranstype,voAry,eParam);
		
		WorkflownoteVO workflownote = null;
		
		// 2.查看扩展参数，是否要流程交互处理
		Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
		Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
		if (paramNoflow == null && paramSilent == null) {
			//XXX:guowl,2010-5,批审时，审批处理对话框上只显示批准、不批准、驳回，故传入一个参数用于甄别
			//如果传人的VO数组长度为1，同单个处理一样
			if (voAry != null && voAry.length > 1) {
				eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
			}
			if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
				//审批流交互处理
				workflownote = actionAboutApproveflow(actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
			} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
				//工作流交互处理
				workflownote = actionAboutWorkflow(actionCode, billOrTranstype, voAry[0], besideContext,voAry.length);
			}
		}

		// 3.后台批处理动作
		Logger.debug("*后台动作批处理 开始");
		Object retObj = NCLocator.getInstance().lookup(IplatFormEntry.class).processBatch(actionCode,
				billOrTranstype, workflownote, voAry, userObjAry, eParam);
		if(retObj instanceof PfProcessBatchRetObject) {
			String errMsg = ((PfProcessBatchRetObject)retObj).getExceptionMsg();
			retObj = ((PfProcessBatchRetObject)retObj).getRetObj();
		}
		Logger.debug("*单据动作批处理 结束=" + (System.currentTimeMillis() - start) + "ms");
		
		return (Object[]) retObj;
	}
	
	public static PfProcessBatchRetObject runBatchNew(String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, BesideApproveContext besideContext, HashMap eParam)
			throws Exception {
		Logger.debug("*单据动作批处理 开始");
		debugParams(actionCode, billOrTranstype, voAry, userObjAry);
		long start = System.currentTimeMillis();
		if(voAry!= null && voAry.length == 1) {
			Object obj = runAction(actionCode, billOrTranstype, voAry[0], userObjAry, besideContext, null, eParam);
			Object[] retObj = null;
			retObj = PfUtilBaseTools.composeResultAry(obj,1,0,retObj);
			return new PfProcessBatchRetObject(retObj, null);
		}
		
		putElecsignatureValue(billOrTranstype,voAry,eParam);

		// 2.查看扩展参数，是否要流程交互处理
		WorkflownoteVO workflownote = null;//(WorkflownoteVO)getParamFromMap(eParam, PfUtilBaseTools.PARAM_WORKNOTE);
//		if(workflownote == null){
			Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
			Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
			if (paramNoflow == null && paramSilent == null) {
				//XXX:guowl,2010-5,批审时，审批处理对话框上只显示批准、不批准、驳回，故传入一个参数用于甄别
				//如果传人的VO数组长度为1，同单个处理一样
				if (voAry != null && voAry.length > 1) {
					eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
				}
				if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
					//审批流交互处理
					workflownote = actionAboutApproveflow(actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
				} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
					//工作流交互处理
					workflownote = actionAboutWorkflow(actionCode, billOrTranstype, voAry[0], besideContext,voAry.length);
				}
				putParam(eParam, PfUtilBaseTools.PARAM_WORKNOTE, workflownote);
			}
		//}
		// 3.后台批处理动作
		Logger.debug("*后台动作批处理 开始");
		Object retObj = NCLocator.getInstance().lookup(IplatFormEntry.class).processBatch(actionCode,
				billOrTranstype, workflownote, voAry, userObjAry, eParam);
		Logger.debug("*单据动作批处理 结束=" + (System.currentTimeMillis() - start) + "ms");
		
		return (PfProcessBatchRetObject)retObj;
	}
	
	private static HashMap putParam(HashMap eParam, String paramKey, Object value) {
		if(eParam == null) {
			eParam = new HashMap();
		}
		eParam.put(paramKey, value);
		return eParam;
	}

	/**
	 * 返回某个单据或交易类型可使用的“新增”下拉菜单信息
	 * <li>包括自制、来源单据
	 * 
	 * @param billtype
	 * @param transtype 如果没有交易类型，可空 
	 * @param pk_group 某集团PK
	 * @param userId 某用户PK
	 * @param includeBillType 是否包括单据类型的来源单据信息，只用于transtype非空的情况
	 * @return
	 * @throws BusinessException 
	 */
	public static PfAddInfo[] retAddInfo(String billtype, String transtype, String pk_group,
			String userId, boolean includeBillType) throws BusinessException {

		return NCLocator.getInstance().lookup(IPFConfig.class).retAddInfo(billtype, transtype,
				pk_group, userId, includeBillType);
	}

	/**
	 * 返回某个用户对某个单据或交易类型，在某组织下 可启动的业务流程
	 * @param billtype
	 * @param transtype 如果没有交易类型，可空 
	 * @param pk_org 某组织PK
	 * @param userId 某用户PK
	 * @return
	 * @throws BusinessException
	 */
	public static String retBusitypeCanStart(String billtype, String transtype, String pk_org,
			String userId) throws BusinessException {

		return NCLocator.getInstance().lookup(IPFConfig.class).retBusitypeCanStart(billtype, transtype,
				pk_org, userId);
	}
	
	public static void setM_sourceBillType(String m_sourceBillType) {
		PfUtilPrivate.m_sourceBillType = m_sourceBillType;
	}
	
	public static void setM_currentBillType(String m_currentBillType) {
		PfUtilPrivate.m_currentBillType = m_currentBillType;
	}


}
