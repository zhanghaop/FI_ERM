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
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.uap.IUAPQueryBS;
import nc.itf.uap.pf.IPFConfig;
import nc.itf.uap.pf.IPfExchangeService;
import nc.itf.uap.pf.IWorkflowAdmin;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.pf.busiflow.PfButtonClickContext;
import nc.itf.uap.pf.metadata.IFlowBizItf;
import nc.itf.uap.rbac.IUserManageQuery_C;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.message.Attachment;
import nc.message.vo.AttachmentVO;
import nc.security.NCAuthenticator;
import nc.security.NCAuthenticatorFactory;
import nc.uap.pf.metadata.PfMetadataTools;
import nc.ui.ml.NCLangRes;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.pf.AssignableInfo;
import nc.vo.pub.pf.PfAddInfo;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.pfflow01.BillbusinessVO;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pub.workflowpsn.WorkflowpersonVO;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.FlowDefNotFoundException;
import nc.vo.uap.pf.OrganizeUnit;
import nc.vo.uap.pf.PFRuntimeException;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.wfengine.core.activity.Activity;
import nc.vo.wfengine.core.activity.GenericActivityEx;
import nc.vo.wfengine.core.parser.UfXPDLParser;
import nc.vo.wfengine.core.parser.XPDLNames;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.workflow.BasicWorkflowProcess;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.IApproveflowConst;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.ext.ApproveFlowAdjustVO;
import nc.vo.wfengine.pub.WFTask;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;
import nc.vo.wfengine.pub.WfTaskType;
import nc.vo.workflow.admin.WorkflowManageContext;

/**
 * ����ƽ̨������������
 * 
 */
public class PfUtilPrivate {

	/**
	 * �����������������true��֮false;
	 */
	private static boolean m_checkFlag = true;

	// ��ǰ��������
	private static String m_currentBillType = null;

	/** ��ǰ�����ڵ��������� */
	private static int m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;


	/** Դ�������� */
	private static String m_sourceBillType = null;

	private static AggregatedValueObject m_tmpRetVo = null;

	private static AggregatedValueObject[] m_tmpRetVos = null;

	// �������Ʊ�־
	public static boolean makeFlag = false;

	private static IPfExchangeService exchangeService;

	private static int m_classifyMode = PfButtonClickContext.NoClassify;

	private PfUtilPrivate() {

	}

	/**
	 * �ύ����ʱ,��Ҫ��ָ����Ϣ
	 * <li>ֻ��"SAVE","EDIT"�����ŵ���
	 */
	private static WorkflownoteVO checkOnSave(String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();

		//guowl+ 2010-5,���������������ȡָ����Ϣ��ֱ�ӷ���
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return worknoteVO;
		
		try {
			worknoteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
			.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
		}catch(FlowDefNotFoundException e) {
			return worknoteVO;
		}
		
//		//�������������ʾ֮ǰ������ҵ����
//		PFClientBizRetObj retObj = executeBusinessPlugin(billVo, worknoteVO, true);
//		if(retObj != null && retObj.isStopFlow()){
//			m_isSuccess = false;
//			return null;
//		}
		if (worknoteVO != null) {
			// �õ���ָ�ɵ���������
			Vector assignInfos = worknoteVO.getTaskInfo().getAssignableInfos();
			if (assignInfos != null && assignInfos.size() > 0) {
				// ��ʾָ�ɶԻ����ռ�ʵ��ָ����Ϣ
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
	 * ��������������ʱ,��Ҫ��ָ����Ϣ
	 * <li>����ѡ���̻�����ߡ�ѡ���̷�֧ת��
	 */
	private static WorkflownoteVO checkOnStart(String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO wfVo = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(
				actionName, billType, billVo, hmPfExParams);

		//guowl+ 2010-5,���������������ȡָ����Ϣ��ֱ�ӷ���
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return wfVo;
		if (wfVo != null) {
			// �õ���ָ�ɵ���Ϣ
			Vector assignInfos = wfVo.getTaskInfo().getAssignableInfos();
			Vector tSelectInfos = wfVo.getTaskInfo().getTransitionSelectableInfos();
			if (assignInfos.size() > 0 || tSelectInfos.size() > 0) {
				// ��ʾָ�ɶԻ����ռ�ʵ��ָ����Ϣ
//				WFStartDispatchDialog wfdd = new WFStartDispatchDialog(wfVo);
//				int iClose = wfdd.showModal();
//				if (iClose == UIDialog.ID_CANCEL)
//					dlgResult.push(Integer.valueOf(iClose));
			}
		}
		return wfVo;
	}
	

	/**
	 * ��鵱ǰ�����Ƿ������������У������н���
	 */
	private static WorkflownoteVO checkWorkitemWhenApprove(String actionName, String billType, AggregatedValueObject billVo,
			HashMap hmPfExParams,int voAryLen) throws BusinessException {
		WorkflownoteVO noteVO = null;
		if (!hasApproveflowDef(billType, billVo)) {
			//���û�ж���������,��Ĭ��ͨ�� 
			noteVO = new WorkflownoteVO();
			noteVO.setApproveresult("Y");
			Logger.debug("*checkWorkitemWhenApprove 1 billType.");
			return noteVO;
		}
		noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
				.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
		Logger.debug("*checkWorkitemWhenApprove 3.");
		
		Object notSilent = null;
		Object operatertype = null;
		Object checknote = null;
		Object pk_users = null;		
		if(hmPfExParams != null){
			notSilent = hmPfExParams.get(PfUtilBaseTools.PARAM_NOTSILENT);
			operatertype = hmPfExParams.get("operatertype");
			checknote = hmPfExParams.get("checknote");
			pk_users = hmPfExParams.get("pk_users");
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
		if("reject".equals(operatertype)){
			if(checknote == null){
				noteVO.setChecknote("����");
			}else{
				noteVO.setChecknote(checknote.toString());
			}
			noteVO.getTaskInfo().getTask().setTaskType(WfTaskType.Backward.getIntValue());
			noteVO.getTaskInfo().getTask().setBackToFirstActivity(true);
			noteVO.getTaskInfo().getTask().setJumpToActivity(null);
			noteVO.setApproveresult("R");
			
		}else if("addassign".equals(operatertype)){
			List<OrganizeUnit> assignedList = getSelectedUsers((String[])pk_users);
			if(checknote == null){
				noteVO.setChecknote("��ǩ");
			}else{
				noteVO.setChecknote(checknote.toString());
			}
			UserVO operator = WorkbenchEnvironment.getInstance().getLoginUser();
			noteVO.setSenderman(operator.getCuserid());
			
			//��ǩaction
		    String processDefPK = noteVO.getTaskInfo().getTask().getWfProcessDefPK();
		    String processInstPK = noteVO.getTaskInfo().getTask().getWfProcessInstancePK();
		    String activityDefPK = noteVO.getTaskInfo().getTask().getActivityID();

		    IUAPQueryBS dao = (IUAPQueryBS)NCLocator.getInstance().lookup(IUAPQueryBS.class.getName());
		    Collection<ApproveFlowAdjustVO> adjustVoCol = dao.retrieveByClause(ApproveFlowAdjustVO.class, "pk_wf_instance='" + processInstPK + "'");
		        
		    ApproveFlowAdjustVO advo;
		    if ((adjustVoCol == null) || (adjustVoCol.size() == 0)) {
		    	advo = new ApproveFlowAdjustVO();
		    	advo.setPk_wf_instance(processInstPK);
		    } else {
		    	advo = (ApproveFlowAdjustVO)adjustVoCol.iterator().next();
		    }
		    WorkflowProcess wp;
			try {
				if (StringUtil.isEmptyWithTrim(advo.getContent())) {
					wp = PfDataCache.getWorkflowProcess(processDefPK, processInstPK);
				} else {
					wp = UfXPDLParser.getInstance().parseProcess(advo.getContent());
				}
			    Activity activity = wp.findActivityByID(activityDefPK);
			    addByCooperation(wp, activity, assignedList,noteVO);
			    
			} catch (XPDLParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
		}else if("transfer".equals(operatertype)){
			if(checknote == null){
				noteVO.setChecknote("����");
			}else{
				noteVO.setChecknote(checknote.toString());
			}
			
		}
		return noteVO;
	}	
	
	/**
	 * ���������
	 * */
	private static WorkflownoteVO  BesideApprove(HashMap hmPfExParams,WorkflownoteVO noteVO){
	
		return noteVO;
	}
	
	
	//�ϴ�����������Ӱ������
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
			Logger.error(e.getMessage() + "�ϴ�����ʧ�ܣ�");
		}
		return vos;
	}

	private static boolean hasApproveflowDef(String billType, AggregatedValueObject billVo)throws BusinessException {
		IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(billVo, IFlowBizItf.class);
		if (fbi == null)
			throw new PFRuntimeException("Ԫ����ʵ��û���ṩҵ��ӿ�IFlowBizItf��ʵ����");

		IWorkflowDefine wfDefine = NCLocator.getInstance().lookup(IWorkflowDefine.class);
		Logger.debug("��ѯ���̶���: billType=" + billType + ";pkOrg=" + fbi.getPkorg() + ";userId=" + fbi.getBillMaker() + ";��ʼ");
		return wfDefine.hasValidProcessDef(InvocationInfoProxy.getInstance().getGroupId(), billType, fbi.getPkorg(), fbi.getBillMaker(), fbi.getEmendEnum(),WorkflowTypeEnum.Approveflow.getIntValue());
	}


	/**
	 * ���� ��ǰ�����ڵ�Ĵ����� lj+ 2005-1-20
	 */
	public static int getCurrentCheckResult() {
		return m_iCheckResult;
	}

	/**
	 * ���� �û�ѡ���VO
	 */
	public static AggregatedValueObject getRetOldVo() {
		return m_tmpRetVo;
	}

	/**
	 * ���� �û�ѡ��VO����.
	 */
	public static AggregatedValueObject[] getRetOldVos() {
		return m_tmpRetVos;
	}

	/**
	 * ���� �û�ѡ���VO�򽻻�����VO
	 * 
	 * @return
	 */
	public static AggregatedValueObject getRetVo() {
		try {
			// ��Ҫ����VO����
			m_tmpRetVo = getExchangeService().runChangeData(m_sourceBillType, m_currentBillType, m_tmpRetVo, null);
			jumpBusitype(m_tmpRetVo==null?null:new AggregatedValueObject[]{m_tmpRetVo});
		} catch (Exception ex) {
			Logger.error(ex.getMessage(), ex);
			throw new PFRuntimeException("VO��������");
		}
		return m_tmpRetVo;
	}

	/**
	 * ���� �û�ѡ��VO����򽻻�����VO����
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	public static AggregatedValueObject[] getRetVos() throws BusinessException {
		//Ĭ�ϸ���ҵ�����̽���VO����
		return getRetVos(true);
	}
	
	/**
	 * ���� �û�ѡ��VO����򽻻�����VO����
	 * 
	 * @param exchangeByBusiType �Ƿ����ҵ�����̽��н�������������ڽ���ǰ����Դҵ������PK���
	 * @return
	 * @throws BusinessException 
	 */
	public static AggregatedValueObject[] getRetVos(boolean exchangeByBusiType) throws BusinessException {
		// ���������ҵ�����̽��н������򽫵����е�ҵ������PKȥ��(��Ӧ����������Դ������ҵ������pK���������ߵ������������)
		if(!exchangeByBusiType && m_tmpRetVos != null) {
			for(int i=0; i < m_tmpRetVos.length; i++) {
				AggregatedValueObject aggVO = m_tmpRetVos[i];
				IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(aggVO, IFlowBizItf.class);
				if(fbi != null) {
					fbi.setBusitype(null);
				}
			}
		}
		// ��Ҫ����VO����
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
	 * ���� �û�ѡ���VO�򽻻�����VO
	 * 
	 * @return
	 */
	private static AggregatedValueObject[] changeVos(AggregatedValueObject[] vos, int classifyMode) {
		AggregatedValueObject[] tmpRetVos = null;

		try {
			tmpRetVos = getExchangeService().runChangeDataAryNeedClassify(m_sourceBillType, m_currentBillType, vos, null, classifyMode);
		} catch (BusinessException ex) {
			Logger.error(ex.getMessage(), ex);
			throw new PFRuntimeException("VO��������");
		}

		return tmpRetVos;
	}
	
	
	/**
	 * ҵ������ת
	 * @throws BusinessException 
	 * */
	private static void jumpBusitype(AggregatedValueObject[] vos) throws BusinessException{
		if(vos==null||vos.length==0)
			return;
		IFlowBizItf fbi = PfMetadataTools.getBizItfImpl(vos[0], IFlowBizItf.class);
		//δʵ��ҵ�����ӿڵĵ��ݻ���δ������ֱ��return
		if(fbi==null||StringUtil.isEmptyWithTrim(fbi.getBusitype()))
			return;
		BillbusinessVO condVO = new BillbusinessVO();
		condVO.setPk_businesstype(fbi.getBusitype());
		condVO.setJumpflag(UFBoolean.TRUE);
		//�õ��������ͱ���
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
						//�������Ͳ���Ϊ��
						continue;
					}
					String key =billtype+(StringUtil.isEmptyWithTrim(transtype)?"":transtype)+(StringUtil.isEmptyWithTrim(pk_org)?"":pk_org)
							   +operator;
					if(busitypeMaps.containsKey(key)){
						destBusitypePk = busitypeMaps.get(key);
					}else{
						destBusitypePk = NCLocator.getInstance().lookup(IPFConfig.class).retBusitypeCanStart(billtype, transtype, pk_org, operator);
					}
					//���û���ҵ�Ҫ��ת��ҵ����
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
	 * @param actionCode �������룬����"SAVE"
	 * @param billOrTranstype ���ݣ����ף�����PK
	 * @param billvo ���ݾۺ�VO
	 * @param userObj �û��Զ������
	 * @param checkVo У�鵥�ݾۺ�VO
	 * @param eParam ��չ����
	 * @return ��������ķ��ؽ��
	 * @throws BusinessException 
	 * @throws Exception
	 * @since 5.5
	 */ 
	public static Object runAction(String actionCode, String billOrTranstype,
			AggregatedValueObject billvo, Object userObj, BesideApproveContext besideContext,
			AggregatedValueObject checkVo, HashMap eParam) throws BusinessException {
		Logger.debug("*���ݶ������� ��ʼ");
		debugParams(actionCode, billOrTranstype, billvo, userObj);
		long start = System.currentTimeMillis();

		WorkflownoteVO worknoteVO = null;
		//�õ�������Ϣ
		if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype)
				|| PfUtilBaseTools.isApproveAction(actionCode,
						billOrTranstype)) {
			// ��������������
			worknoteVO = actionAboutApproveflow(actionCode,
					billOrTranstype, billvo, eParam,0);
			onApprove(besideContext,worknoteVO);
		} else if (PfUtilBaseTools.isStartAction(actionCode,
				billOrTranstype)
				|| PfUtilBaseTools.isSignalAction(actionCode,
						billOrTranstype)) {
			// ������������
			worknoteVO = actionAboutWorkflow(actionCode,
					billOrTranstype, billvo, besideContext,0);
		}
		
		if (worknoteVO == null) {
			//��鲻����������̨�����ٴμ��
			if (eParam == null)
				eParam = new HashMap<String, String>();
			eParam.put(PfUtilBaseTools.PARAM_NOTE_CHECKED, PfUtilBaseTools.PARAM_NOTE_CHECKED);
		}

		// 4.��ִ̨�ж���
		Object retObj = null;
		
		Logger.debug("*��̨�������� ��ʼ");
		long start2 = System.currentTimeMillis();
		IplatFormEntry iIplatFormEntry = (IplatFormEntry) NCLocator.getInstance().lookup(
				IplatFormEntry.class.getName());
		retObj = iIplatFormEntry.processAction(actionCode, billOrTranstype, worknoteVO, billvo,
				userObj, eParam);
		Logger.debug("*��̨�������� ����=" + (System.currentTimeMillis() - start2) + "ms");

		// 5.���ض���ִ��
		//retObjRun(retObj);
		Logger.debug("*���ݶ������� ����=" + (System.currentTimeMillis() - start) + "ms");

		return retObj;
	}

	private static void onSignal(BesideApproveContext besideContext,
			WorkflownoteVO worknoteVO) {
		worknoteVO.setChecknote(besideContext.getCheckNote());
		worknoteVO.setApproveresult(besideContext.getApproveResult());
	}

	/**
	 * ����ͨ����ͨ��
	 */
	protected static void onApprove(BesideApproveContext besideContext,WorkflownoteVO worknoteVO) {
		if(besideContext == null)
			return;

		// �ж��Ƿ���Ҫ���ָ��
		boolean isNeedDispatch = isExistAssignableInfoWhenPass(worknoteVO);
		if (isNeedDispatch) {
//			// ���ָ����Ϣ
//			getDispatchDialog().getDisPatchPanel().initByWorknoteVO(
//					worknoteVO,
//					pass ? AssignableInfo.CRITERION_PASS
//							: AssignableInfo.CRITERION_NOPASS);
//			int result = getDispatchDialog().showModal();
//			if (result == UIDialog.ID_CANCEL) {
//				// ���ָ�ɶԻ�������ȡ������ôȡ������������������»ص�������� ��changlx�����壩modified by
//				// zhangrui 2012-04-17
//				return;
//			}
		}
		if (!beforeButtonOperate(worknoteVO))
			return;

		//���õ�ǰ�������Ƿ�����̽��и���
		worknoteVO.setTrack(false);
		worknoteVO.setChecknote(besideContext.getCheckNote());
		worknoteVO.setApproveresult(besideContext.getApproveResult());
	}
	
	private static boolean beforeButtonOperate(WorkflownoteVO worknoteVO) {
		// ����
		// @modifier yanke1 2011-7-15 ���ó�������Ϣ
		// �����Ƿ���׼�����г���
//		worknoteVO.setMailExtCpySenders(getCopySendDialog().getCpySendPanel().getMailVOs());
//		worknoteVO.setMsgExtCpySenders(getCopySendDialog().getCpySendPanel().getMsgVOs());

		String checkNote ="��׼Y";
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
	 * �����Ƿ���ҪCAǩ��
	 * */
	private static boolean isNeedCASign(WorkflownoteVO worknoteVO){
		Object value = worknoteVO.getRelaProperties().get(XPDLNames.ELECSIGNATURE);
		if (value != null && "true".equalsIgnoreCase(value.toString())) {
			return true;
		} else
			return false;
	}
	/**
	 * ��׼ʱ���Ƿ���ڿ�ָ�ɵĺ�̻
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
	 * ����׼ʱ���Ƿ���ڿ�ָ�ɵĺ�̻
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
	 * ��������صĽ�������
	 * @throws BusinessException 
	 */
	private static WorkflownoteVO actionAboutApproveflow(String actionName,
			String billType, AggregatedValueObject billvo, HashMap eParam,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isSaveAction(actionName, billType)) {
			Logger.debug("*�ύ����=" + actionName + "�����������");
			// ���Ϊ�ύ������������Ҫ�ռ��ύ�˵�ָ����Ϣ������ͳһ�������� lj@2005-4-8
			Stack dlgResult = new Stack();
			worknoteVO = checkOnSave(IPFActionName.SAVE, billType, billvo, dlgResult, eParam);
		} else if (PfUtilBaseTools.isApproveAction(actionName, billType)) {
			Logger.debug("*��������=" + actionName + "�����������");
			// ���õ����Ƿ����������У����ռ������˵�������Ϣ
			worknoteVO = checkWorkitemWhenApprove(actionName, billType, billvo, eParam,voAryLen);
			if (worknoteVO != null) {
				if ("Y".equals(worknoteVO.getApproveresult())) {
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;
				} else if("R".equals(worknoteVO.getApproveresult())) {
					// XXX::����Ҳ��Ϊ����ͨ����һ��,��Ҫ�����ж� lj+
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
				Logger.debug("*�û�����ʱ�����ȡ������ֹͣ����");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * ��������صĽ�������
	 * @throws BusinessException 
	 */
	private static WorkflownoteVO actionAboutWorkflow(String actionName,
			String billType, AggregatedValueObject billvo, BesideApproveContext besideContext,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isStartAction(actionName, billType)) {
			Logger.debug("*��������=" + actionName + "����鹤����");
			worknoteVO = checkOnStart(actionName, billType, billvo, null, null);
		} else if (PfUtilBaseTools.isSignalAction(actionName, billType)) {
			Logger.debug("*ִ�ж���=" + actionName + "����鹤����");
			// ���õ����Ƿ��ڹ�������
			worknoteVO = checkWorkitemWhenSignal(actionName, billType, billvo, besideContext, voAryLen);
			if (worknoteVO != null) {
				if ("Y".equals(worknoteVO.getApproveresult())) {
					m_iCheckResult = IApproveflowConst.CHECK_RESULT_PASS;
				} else if("R".equals(worknoteVO.getApproveresult())) {
					// XXX::����Ҳ��Ϊ����ͨ����һ��,��Ҫ�����ж� lj+
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
				Logger.debug("*�û�����������ʱ�����ȡ������ִֹͣ�й�����");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * ��鵱ǰ�����Ƿ��ڹ��������л������������������У������н���
	 */
	private static WorkflownoteVO checkWorkitemWhenSignal(String actionCode,
			String billType, AggregatedValueObject billVo, BesideApproveContext besideContext, int voAryLen) throws BusinessException {
		//��鵱ǰ�û��Ĺ�����������+���������̹�����
		WorkflownoteVO noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(actionCode,
					billType, billVo, null);
		if (noteVO.getWorkflow_type() == WorkflowTypeEnum.SubWorkApproveflow.getIntValue()) {
			//������������������
			if(besideContext.getApproveResult().equals("Y")){
				//��׼
				onApprove(besideContext,noteVO);
			}else if(besideContext.getApproveResult().equals("R")){
				//����
				noteVO.setChecknote(besideContext.getCheckNote());
				noteVO.getTaskInfo().getTask().setTaskType(WfTaskType.Backward.getIntValue());
		    	noteVO.getTaskInfo().getTask().setBackToFirstActivity(besideContext.isBackToFirstActivity());
			    noteVO.getTaskInfo().getTask().setJumpToActivity(besideContext.getJumpToActivity());
			    noteVO.setApproveresult("R");
			}else if(besideContext.getApproveResult().equals("R")){
				//��ǩ
				if(!canAddApprover(noteVO))
					throw new BusinessException("�õ��ݲ�֧�ּ�ǩ��");
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
	 * �Ƿ���Լ�ǩ
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
	 * ��־һ�¶�������������Ĳ���
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
	 * @since 6.3 ����ʱ��Ԥ���ж��Ƿ���ҪCA
	 * */
	private static void putElecsignatureValue(String billOrTranstype,AggregatedValueObject[] voAry,HashMap eParam) throws BusinessException{
//        //�ж��Ƿ���ҪCA
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
		//��������ʱ�Ƿ���Ҫ����CAǩ��
		Object isNeedCASign =hmPfExParams.get(XPDLNames.ELECSIGNATURE);
		if(isNeedCASign!=null&&isNeedCASign.toString().equals("Y")){
			noteVO.getRelaProperties().put(XPDLNames.ELECSIGNATURE, "true");
		}
	}
	
	
	
	/**
	 * 
	 * @param actionCode �������룬����"SAVE"
	 * @param billOrTranstype �������ͣ��������ͣ�PK
	 * @param voAry ���ݾۺ�VO����
	 * @param userObjAry �û��Զ����������
	 * @param eParam ��չ����
	 * @return ����������ķ��ؽ��
	 * @throws Exception
	 * @since 5.5
	 */
	public static Object[] runBatch(String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, BesideApproveContext besideContext, HashMap eParam)
			throws Exception {
		Logger.debug("*���ݶ��������� ��ʼ");
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
		
		// 2.�鿴��չ�������Ƿ�Ҫ���̽�������
		Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
		Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
		if (paramNoflow == null && paramSilent == null) {
			//XXX:guowl,2010-5,����ʱ����������Ի�����ֻ��ʾ��׼������׼�����أ��ʴ���һ�������������
			//������˵�VO���鳤��Ϊ1��ͬ��������һ��
			if (voAry != null && voAry.length > 1) {
				eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
			}
			if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
				//��������������
				workflownote = actionAboutApproveflow(actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
			} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
				//��������������
				workflownote = actionAboutWorkflow(actionCode, billOrTranstype, voAry[0], besideContext,voAry.length);
			}
		}

		// 3.��̨��������
		Logger.debug("*��̨���������� ��ʼ");
		Object retObj = NCLocator.getInstance().lookup(IplatFormEntry.class).processBatch(actionCode,
				billOrTranstype, workflownote, voAry, userObjAry, eParam);
		if(retObj instanceof PfProcessBatchRetObject) {
			String errMsg = ((PfProcessBatchRetObject)retObj).getExceptionMsg();
			retObj = ((PfProcessBatchRetObject)retObj).getRetObj();
		}
		Logger.debug("*���ݶ��������� ����=" + (System.currentTimeMillis() - start) + "ms");
		
		return (Object[]) retObj;
	}
	
	public static PfProcessBatchRetObject runBatchNew(String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, BesideApproveContext besideContext, HashMap eParam)
			throws Exception {
		Logger.debug("*���ݶ��������� ��ʼ");
		debugParams(actionCode, billOrTranstype, voAry, userObjAry);
		long start = System.currentTimeMillis();
		if(voAry!= null && voAry.length == 1) {
			Object obj = runAction(actionCode, billOrTranstype, voAry[0], userObjAry, besideContext, null, eParam);
			Object[] retObj = null;
			retObj = PfUtilBaseTools.composeResultAry(obj,1,0,retObj);
			return new PfProcessBatchRetObject(retObj, null);
		}
		
		putElecsignatureValue(billOrTranstype,voAry,eParam);

		// 2.�鿴��չ�������Ƿ�Ҫ���̽�������
		WorkflownoteVO workflownote = null;//(WorkflownoteVO)getParamFromMap(eParam, PfUtilBaseTools.PARAM_WORKNOTE);
//		if(workflownote == null){
			Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
			Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
			if (paramNoflow == null && paramSilent == null) {
				//XXX:guowl,2010-5,����ʱ����������Ի�����ֻ��ʾ��׼������׼�����أ��ʴ���һ�������������
				//������˵�VO���鳤��Ϊ1��ͬ��������һ��
				if (voAry != null && voAry.length > 1) {
					eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
				}
				if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
					//��������������
					workflownote = actionAboutApproveflow(actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
				} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
					//��������������
					workflownote = actionAboutWorkflow(actionCode, billOrTranstype, voAry[0], besideContext,voAry.length);
				}
				putParam(eParam, PfUtilBaseTools.PARAM_WORKNOTE, workflownote);
			}
		//}
		// 3.��̨��������
		Logger.debug("*��̨���������� ��ʼ");
		Object retObj = NCLocator.getInstance().lookup(IplatFormEntry.class).processBatch(actionCode,
				billOrTranstype, workflownote, voAry, userObjAry, eParam);
		Logger.debug("*���ݶ��������� ����=" + (System.currentTimeMillis() - start) + "ms");
		
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
	 * ����ĳ�����ݻ������Ϳ�ʹ�õġ������������˵���Ϣ
	 * <li>�������ơ���Դ����
	 * 
	 * @param billtype
	 * @param transtype ���û�н������ͣ��ɿ� 
	 * @param pk_group ĳ����PK
	 * @param userId ĳ�û�PK
	 * @param includeBillType �Ƿ�����������͵���Դ������Ϣ��ֻ����transtype�ǿյ����
	 * @return
	 * @throws BusinessException 
	 */
	public static PfAddInfo[] retAddInfo(String billtype, String transtype, String pk_group,
			String userId, boolean includeBillType) throws BusinessException {

		return NCLocator.getInstance().lookup(IPFConfig.class).retAddInfo(billtype, transtype,
				pk_group, userId, includeBillType);
	}

	/**
	 * ����ĳ���û���ĳ�����ݻ������ͣ���ĳ��֯�� ��������ҵ������
	 * @param billtype
	 * @param transtype ���û�н������ͣ��ɿ� 
	 * @param pk_org ĳ��֯PK
	 * @param userId ĳ�û�PK
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

	/**
	 * ���o����
	 * @param actionCode �������룬����"SAVE"
	 * @param billOrTranstype ���ݣ����ף�����PK
	 * @param billvo ���ݾۺ�VO
	 * @param userObj �û��Զ������
	 * @param checkVo У�鵥�ݾۺ�VO
	 * @param eParam ��չ����
	 * @return ��������ķ��ؽ��
	 * @throws BusinessException 
	 * @throws Exception
	 * @since 5.5
	 */ 
	public static HashMap getOperateAuthorization(String actionCode, String billOrTranstype,
			AggregatedValueObject billvo, Object userObj, BesideApproveContext besideContext,
			AggregatedValueObject checkVo, HashMap eParam) throws BusinessException{
		HashMap retmap = new HashMap();
		Logger.debug("*���ݶ������� ��ʼ");
		debugParams(actionCode, billOrTranstype, billvo, userObj);
		long start = System.currentTimeMillis();

		WorkflownoteVO worknoteVO = null;
		//�õ�������Ϣ
		if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype)
				|| PfUtilBaseTools.isApproveAction(actionCode,
						billOrTranstype)) {
			// ��������������
			worknoteVO = actionAboutApproveflow(actionCode,
					billOrTranstype, billvo, eParam,0);
			onApprove(besideContext,worknoteVO);
		} else if (PfUtilBaseTools.isStartAction(actionCode,
				billOrTranstype)
				|| PfUtilBaseTools.isSignalAction(actionCode,
						billOrTranstype)) {
			// ������������
			worknoteVO = actionAboutWorkflow(actionCode,
					billOrTranstype, billvo, besideContext,0);
		}
		if (worknoteVO == null) {
			//��鲻����������̨�����ٴμ��
			if (eParam == null)
				eParam = new HashMap<String, String>();
			eParam.put(PfUtilBaseTools.PARAM_NOTE_CHECKED, PfUtilBaseTools.PARAM_NOTE_CHECKED);
		}else{
			boolean canReject = canRejectbtn(worknoteVO);
			boolean canTransefer = canTransferbtn(worknoteVO);
			boolean canAddApprover = canAddApproverbtn(worknoteVO);
			retmap.put("canReject", canReject);
			retmap.put("canTransefer", canTransefer);
			retmap.put("canAddApprover", canAddApprover);
		}
		return retmap;
	}

	private static boolean canRejectbtn(WorkflownoteVO worknoteVO ) {
		return !worknoteVO.getActiontype().endsWith("_A");
	}

	private static boolean canTransferbtn(WorkflownoteVO worknoteVO ) {
		Object value = worknoteVO.getRelaProperties().get("CanTransfer");

		if ((value != null) && ("true".equalsIgnoreCase(value.toString()))) {
			if (worknoteVO.actiontype.endsWith("_A")) {
				return false;
			}
			return true;
		}
		return false;
	}

	private static boolean canAddApproverbtn(WorkflownoteVO worknoteVO ) {
		Object value = worknoteVO.getRelaProperties()
				.get("CanAddApprover");

		if ((value != null) && ("true".equalsIgnoreCase(value.toString()))) {
			if (worknoteVO.actiontype.endsWith("_A")) {
				return false;
			}
			return true;
		}
		return false;
	}
	
	private static List<OrganizeUnit> getSelectedUsers(String[] pk_users)
	{
		List<OrganizeUnit> assignedList = new ArrayList();
		String[] refPKs = pk_users;
		for (int i = 0; i < refPKs.length; i++) {
			String pk = refPKs[i];
			UserVO uservo = new UserVO();
			try {
				uservo = NCLocator.getInstance()
						.lookup(IUserManageQuery_C.class).getUser(pk);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String userCode = uservo.getUser_code();
			String pk_org = uservo.getPk_org();
			String name = uservo.getUser_name();
			OrganizeUnit unit = new OrganizeUnit();
			unit.setOrgUnitType(1);
			unit.setPk(pk);
			unit.setCode(userCode);
			unit.setPkOrg(pk_org);
			unit.setName(name);
			assignedList.add(unit);
		}
		return assignedList;
   }
   
   private static void addByCooperation(WorkflowProcess wp, Activity currentAct, List<OrganizeUnit> orgUnits,WorkflownoteVO worknote)
     throws BusinessException
   {
		if (orgUnits.size() == 0)
			return;
		ArrayList<String> approve_userIds = new ArrayList();
		for (int i = 0; i < orgUnits.size(); i++) {
			approve_userIds.add(((OrganizeUnit) orgUnits.get(i)).getPk());
		}
		worknote.getTaskInfo().getTask().setAddApprover(true);
		worknote.setSenddate(new UFDateTime());
		worknote.setExtApprovers(approve_userIds);

		((IWorkflowAdmin) NCLocator.getInstance().lookup(IWorkflowAdmin.class))
				.addApprover(worknote);
  }

   private static OrganizeUnit[] checkAlterAssignUsers(OrganizeUnit[] selectedUsers)
     throws BusinessException
   {
		if (selectedUsers.length == 0) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow1", "WorkflowManageUtil-000001"));
		}

		if (selectedUsers.length > 1) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow", "UPPpfworkflow-000013"));
		}
		if (selectedUsers[0].getPk().equals(
				WorkbenchEnvironment.getInstance().getLoginUser()
						.getPrimaryKey())) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow", "UPPpfworkflow-000014"));
		}
		Collection<WorkflowpersonVO> wps = ((IUAPQueryBS) NCLocator
				.getInstance().lookup(IUAPQueryBS.class)).retrieveByClause(
				WorkflowpersonVO.class,
				"pk_cuserid='" + selectedUsers[0].getPk() + "'");
		if ((wps != null) && (!wps.isEmpty())) {
			return null;
		}
		return selectedUsers;
   }

	private static void checkFlowStatusOfItem(WorkflowManageContext context)
			throws BusinessException {
		String billversionpk = context.getBillId();
		Integer approvestatus = context.getApproveStatus();
		String billType = context.getBillType();
		Integer workflowType = context.getFlowType();

		if (approvestatus.intValue() != WfTaskOrInstanceStatus.Started
				.getIntValue()) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow1", "WorkflowManageUtil-000003"));
		}

		if (!hasRunningInstance(billversionpk, billType,
				workflowType.toString())) {

			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow", "UPPpfworkflow-000828"));
		}
	}

	private static boolean hasRunningInstance(String billId, String billtype,
			String flowType) {
		return ((IWorkflowAdmin) NCLocator.getInstance().lookup(
				IWorkflowAdmin.class)).hasRunningProcess(billId, billtype,
				flowType).booleanValue();
	}
	
	private static void checkAlterSendApplicable(WorkflowManageContext context)
			throws BusinessException {
		GenericActivityEx act = findActivity(context);

		if ((act != null) && (!act.getCanTransfer())) {
			throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("pfworkflow61_0", "0pfworkflow61-0047"));
		}
	}

	private static GenericActivityEx findActivity(WorkflowManageContext context)
			throws BusinessException {
		if (context.getActivity() != null) {
			return context.getActivity();
		}

		String pk_checkflow = context.getWorkflownotePk();

		String sql = "select k.activitydefid, i.processdefid from pub_workflownote n join pub_wf_task k on n.pk_wf_task=k.pk_wf_task join pub_wf_instance i on k.pk_wf_instance=i.pk_wf_instance where pk_checkflow=?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_checkflow);

		IUAPQueryBS qry = (IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class);
		Object[] result = (Object[]) qry.executeQuery(sql, param,
				new ArrayProcessor());

		if (result != null) {
			String activitydef = (String) result[0];
			String processdef = (String) result[1];
			BasicWorkflowProcess p;
			try {
				p = PfDataCache.getWorkflowProcess(processdef);
			} catch (XPDLParserException e) {
				throw new BusinessException(e);
			}

			if (p == null) {
				throw new BusinessException(NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("pfworkflow61_0", "0pfworkflow61-0046"));
			}

			Activity act = p.findActivityByID(activitydef);

			if ((act != null) && ((act instanceof GenericActivityEx))) {
				context.setActivity((GenericActivityEx) act);
				return (GenericActivityEx) act;
			}
		}

		return null;
	}
	
	private static void checkWorkItemCanReassignOrAddApprover(
			WorkflowManageContext context, int checkType)
			throws BusinessException {
		String pk_checkflow = context.getWorkflownotePk();
		String sql = "select actiontype from pub_workflownote where pk_checkflow = ?";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_checkflow);
		IUAPQueryBS qry = (IUAPQueryBS) NCLocator.getInstance().lookup(
				IUAPQueryBS.class);
		Object[] result = (Object[]) qry.executeQuery(sql, param,
				new ArrayProcessor());

		String actionType = (String) result[0];

		if ((checkType == 1) && (actionType.endsWith("_A"))) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow1", "WorkflowManageUtil-000004"));
		}

		if ((checkType == 2) && (actionType.endsWith("_A"))) {
			throw new BusinessException(NCLangRes.getInstance().getStrByID(
					"pfworkflow1", "WorkflowManageUtil-000005"));
		}
	}
}
