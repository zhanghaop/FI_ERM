package nc.arap.mobile.impl;

import java.awt.Container;
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
 * ����ƽ̨�ͻ��˹�����
 * 
 * @author fangj 2001-10
 * @modifier leijun 2005-5 ȡ����������UI����������<Y>��ͷ�ſ�ָ�ɵ�����
 * @modifier leijun 2006-7 ����ʱ��ָ�ɶԻ�������û����ȡ����������
 * @modifier leijun 2007-5 ʹ���µĲ�ѯģ��
 * @modifier leijun 2008-3 �ع�����������API����һ������
 * @modifier dingxm 2009-7 �����Ƶ����ڰ�ť�߼��Ĵ���Ų��BusinessDelegator�������Ƶ��б���ֻ�ṩ��Ϣ����������ť
 * @modifier zhouzhenga 20120107 �����߼�Ų��PfUtilClientAssistor
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

	private static boolean m_isOk = false;

	/** fgj2001-11-27 �жϵ�ǰ�����Ƿ�ִ�гɹ� */
	private static boolean m_isSuccess = true;

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
	private static WorkflownoteVO checkOnSave(Container parent, String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO worknoteVO = new WorkflownoteVO();

		//guowl+ 2010-5,�����������������ȡָ����Ϣ��ֱ�ӷ���
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return worknoteVO;
		
		try {
			worknoteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
			.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
		}catch(FlowDefNotFoundException e) {
			return worknoteVO;
		}
		
//		//��������������ʾ֮ǰ������ҵ����
//		PFClientBizRetObj retObj = executeBusinessPlugin(parent, billVo, worknoteVO, true);
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
	private static WorkflownoteVO checkOnStart(Container parent, String actionName, String billType,
			AggregatedValueObject billVo, Stack dlgResult, HashMap hmPfExParams) throws BusinessException {
		WorkflownoteVO wfVo = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(
				actionName, billType, billVo, hmPfExParams);

		//guowl+ 2010-5,�����������������ȡָ����Ϣ��ֱ�ӷ���
		if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
			return wfVo;
		if (wfVo != null) {
			// �õ���ָ�ɵ���Ϣ
			Vector assignInfos = wfVo.getTaskInfo().getAssignableInfos();
			Vector tSelectInfos = wfVo.getTaskInfo().getTransitionSelectableInfos();
			if (assignInfos.size() > 0 || tSelectInfos.size() > 0) {
				// ��ʾָ�ɶԻ����ռ�ʵ��ָ����Ϣ
//				WFStartDispatchDialog wfdd = new WFStartDispatchDialog(parent, wfVo);
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
	private static WorkflownoteVO checkWorkitemWhenApprove(Container parent,
			String actionName, String billType, AggregatedValueObject billVo,
			HashMap hmPfExParams,int voAryLen) throws BusinessException {
		WorkflownoteVO noteVO = null;
		if (hmPfExParams != null
				&& hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null) {
			
			
			Object notSilent = hmPfExParams
					.get(PfUtilBaseTools.PARAM_NOTSILENT);
			// ��鵥���Ƿ����������������û�ж��壬�򲻵���,�˴�ֻ�򵥼���һ�ŵ��ݵĵ����������Ƿ������̶���
			if (notSilent == null && !hasApproveflowDef(billType, billVo)) {
				m_checkFlag = true;
				noteVO = new WorkflownoteVO();
				noteVO.setApproveresult("Y");
				Logger.debug("*checkWorkitemWhenApprove 1 billType.");
				return noteVO;
			} else {
				// Ԥ�㿪����Ҫ������ʱ������û�����̶��壬���������������
				noteVO = new WorkflownoteVO();
				
//				putElecsignatureRelaProperties(noteVO,hmPfExParams);
//				
//				BatchApproveModel batchApproveMode = new BatchApproveModel();
//				batchApproveMode.setBillUI(true);
//				batchApproveMode.setSingleBillSelected(true);
//				batchApproveMode.setContainUnApproveBill(false);
//				batchApproveMode.setBillItem(voAryLen);
//				dlg = new BatchApproveWorkitemAcceptDlg(parent, noteVO);
//				((BatchApproveWorkitemAcceptDlg) dlg)
//						.setBachApproveMode(batchApproveMode);
//				Logger.debug("*checkWorkitemWhenApprove 2.");
			}
		} else {
			noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class)
					.checkWorkFlow(actionName, billType, billVo, hmPfExParams);
			Logger.debug("*checkWorkitemWhenApprove 3.");
			
			
			if (noteVO != null && isBesideApprove(hmPfExParams)) {
				noteVO = BesideApprove(hmPfExParams, noteVO);
			} else {
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
				onApprove(true,noteVO);
				Logger.debug("*checkWorkitemWhenApprove 4.");
			}			
			
		}

		if (hmPfExParams != null
				&& hmPfExParams.get(PfUtilBaseTools.PARAM_WORKNOTE) != null) {
			WorkflownoteVO worknote = (WorkflownoteVO) hmPfExParams
					.get(PfUtilBaseTools.PARAM_WORKNOTE);
			noteVO.setApproveresult(worknote.getApproveresult());
			noteVO.setChecknote(worknote.getChecknote());
			PfUtilPrivate.m_checkFlag = true;
			return noteVO;
		}

		Logger.debug("*checkWorkitemWhenApprove 5.");
		
	    if (hmPfExParams == null) {
	        hmPfExParams = new HashMap<String, Object>();
	    }
	    if((!hmPfExParams.containsKey(PfUtilBaseTools.PARAM_WORKNOTE) || hmPfExParams.get(PfUtilBaseTools.PARAM_WORKNOTE) == null)
				&& hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null)
	    	hmPfExParams.put(PfUtilBaseTools.PARAM_WORKNOTE, noteVO);
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
	 * ���� ��ǰ�����ڵ�Ĵ������ lj+ 2005-1-20
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
	 * �ж��û��Ƿ����ˣ�ȡ������ť
	 * 
	 * @return boolean leijun+
	 */
	public static boolean isCanceled() {
		return !m_checkFlag;
	}

	/**
	 * ���� ���յ����Ƿ������ر�
	 * 
	 * @return boolean
	 */
	public static boolean isCloseOK() {
		return m_isOk;
	}

	/**
	 * ���� ��ǰ���ݶ���ִ���Ƿ�ɹ�
	 * 
	 * @return boolean
	 */
	public static boolean isSuccess() {
		return m_isSuccess;
	}
	
	/**
	 * ǰ̨���ݶ�������API���㷨���£�
	 * <li>1.����ִ��ǰ��ʾ�Լ���ǰ����������û�ȡ�����򷽷�ֱ�ӷ���
	 * <li>2.�鿴��չ�������ж��Ƿ���Ҫ��������ش��������Ϊ�ύ�������������Ҫ�ռ��ύ�˵�ָ����Ϣ��
	 * ���Ϊ�����������������Ҫ�ռ������˵�������Ϣ
	 * <li>3.��ִ̨�ж����������ض���ִ�н���� 
	 * 
	 * @param parent ������
	 * @param actionCode �������룬����"SAVE"
	 * @param billOrTranstype ���ݣ����ף�����PK
	 * @param billvo ���ݾۺ�VO
	 * @param userObj �û��Զ������
	 * @param checkVo У�鵥�ݾۺ�VO
	 * @param eParam ��չ����
	 * @return ���������ķ��ؽ��
	 * @throws BusinessException 
	 * @throws Exception
	 * @since 5.5
	 */
	public static Object runAction(Container parent, String actionCode, String billOrTranstype,
			AggregatedValueObject billvo, Object userObj, String strBeforeUIClass,
			AggregatedValueObject checkVo, HashMap eParam) throws BusinessException {
		Logger.debug("*���ݶ������� ��ʼ");
		debugParams(actionCode, billOrTranstype, billvo, userObj);
		long start = System.currentTimeMillis();
		m_isSuccess = true;

		// 2.�鿴��չ�������Ƿ�Ҫ���̽�������
		WorkflownoteVO worknoteVO = null;
		Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
		Object paramNoflow = getParamFromMap(eParam,
				PfUtilBaseTools.PARAM_NOFLOW);
		if (paramNoflow == null && paramSilent == null) {
			// ��Ҫ��������
			if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype)
					|| PfUtilBaseTools.isApproveAction(actionCode,
							billOrTranstype)) {
				// ��������������
				worknoteVO = actionAboutApproveflow(parent, actionCode,
						billOrTranstype, billvo, eParam,0);
				if (!m_isSuccess)
					return null;
			} else if (PfUtilBaseTools.isStartAction(actionCode,
					billOrTranstype)
					|| PfUtilBaseTools.isSignalAction(actionCode,
							billOrTranstype)) {
				// ������������
				worknoteVO = actionAboutWorkflow(parent, actionCode,
						billOrTranstype, billvo, eParam,0);
				if (!m_isSuccess)
					return null;
			}
//			putParam(eParam, PfUtilBaseTools.PARAM_WORKNOTE, worknoteVO);
		}
		
		if (worknoteVO == null) {
			//��鲻����������̨�����ٴμ��
			if (eParam == null)
				eParam = new HashMap<String, String>();
			if (paramSilent == null)
				eParam.put(PfUtilBaseTools.PARAM_NOTE_CHECKED, PfUtilBaseTools.PARAM_NOTE_CHECKED);
		}

		// 4.��ִ̨�ж���
		Object retObj = null;
		//��ִ̨������ǰ����־λ��Ϊfalse
		m_isSuccess = false;
		
		Logger.debug("*��̨�������� ��ʼ");
		long start2 = System.currentTimeMillis();
		IplatFormEntry iIplatFormEntry = (IplatFormEntry) NCLocator.getInstance().lookup(
				IplatFormEntry.class.getName());
		retObj = iIplatFormEntry.processAction(actionCode, billOrTranstype, worknoteVO, billvo,
				userObj, eParam);
		Logger.debug("*��̨�������� ����=" + (System.currentTimeMillis() - start2) + "ms");

		m_isSuccess = true;

		// 5.���ض���ִ��
		//retObjRun(parent, retObj);
		Logger.debug("*���ݶ������� ����=" + (System.currentTimeMillis() - start) + "ms");

		return retObj;
	}

	/**
	 * ����ͨ����ͨ��
	 */
	protected static void onApprove(boolean pass,WorkflownoteVO worknoteVO) {

		// �ж��Ƿ���Ҫ���ָ��
		boolean isNeedDispatch = pass ? isExistAssignableInfoWhenPass(worknoteVO)
				: isExistAssignableInfoWhenNopass(worknoteVO);
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

		// yanke1+ 2011-7-15 ���õ�ǰ�������Ƿ�����̽��и���
		worknoteVO.setTrack(false);

		String checkNote = "��׼";
		worknoteVO.setChecknote(checkNote);
		worknoteVO.setApproveresult(UFBoolean.valueOf(pass).toString());
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
	private static WorkflownoteVO actionAboutApproveflow(Container parent, String actionName,
			String billType, AggregatedValueObject billvo, HashMap eParam,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isSaveAction(actionName, billType)) {
			Logger.debug("*�ύ����=" + actionName + "�����������");
			// ���Ϊ�ύ������������Ҫ�ռ��ύ�˵�ָ����Ϣ������ͳһ�������� lj@2005-4-8
			Stack dlgResult = new Stack();
			worknoteVO = checkOnSave(parent, IPFActionName.SAVE, billType, billvo, dlgResult, eParam);
			if (dlgResult.size() > 0) {
				m_isSuccess = false;
				Logger.debug("*�û�ָ��ʱ�����ȡ������ֹͣ����");
			}
		} else if (PfUtilBaseTools.isApproveAction(actionName, billType)) {
			Logger.debug("*��������=" + actionName + "�����������");
			// ���õ����Ƿ����������У����ռ������˵�������Ϣ
			worknoteVO = checkWorkitemWhenApprove(parent, actionName, billType, billvo, eParam,voAryLen);
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
				m_isSuccess = false;
				Logger.debug("*�û�����ʱ�����ȡ������ֹͣ����");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * ��������صĽ�������
	 * @throws BusinessException 
	 */
	private static WorkflownoteVO actionAboutWorkflow(Container parent, String actionName,
			String billType, AggregatedValueObject billvo, HashMap eParam,int voAryLen) throws BusinessException {
		WorkflownoteVO worknoteVO = null;

		if (PfUtilBaseTools.isStartAction(actionName, billType)) {
			Logger.debug("*��������=" + actionName + "����鹤����");
			Stack dlgResult = new Stack();
			worknoteVO = checkOnStart(parent, actionName, billType, billvo, dlgResult, eParam);
			if (dlgResult.size() > 0) {
				m_isSuccess = false;
				Logger.debug("*�û�ָ��ʱ�����ȡ������ֹͣ����������");
			}
		} else if (PfUtilBaseTools.isSignalAction(actionName, billType)) {
			Logger.debug("*ִ�ж���=" + actionName + "����鹤����");
			// ���õ����Ƿ��ڹ�������
			worknoteVO = checkWorkitemWhenSignal(parent, actionName, billType, billvo, eParam, voAryLen);
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
				m_isSuccess = false;
				Logger.debug("*�û�����������ʱ�����ȡ������ִֹͣ�й�����");
			}
		}
		return worknoteVO;
	}
	
	/**
	 * ��鵱ǰ�����Ƿ��ڹ��������л������������������У������н���
	 */
	private static WorkflownoteVO checkWorkitemWhenSignal(Container parent, String actionCode,
			String billType, AggregatedValueObject billVo, HashMap hmPfExParams, int voAryLen) throws BusinessException {
		WorkflownoteVO noteVO = null;
			//��鵱ǰ�û��Ĺ�����������+���������̹�����
			noteVO = NCLocator.getInstance().lookup(IWorkflowMachine.class).checkWorkFlow(actionCode,
					billType, billVo, hmPfExParams);
			if (noteVO == null) {
				m_checkFlag = true;
				return noteVO;
			} else {
				//XXX:guowl+,����Ƿ񵯳���������
//				if (!PfUtilClientAssistor.isExchange(noteVO.getTaskInfo().getTask())) {
					m_checkFlag = true;
					noteVO.setApproveresult("Y");
					return noteVO;
//				}

//				if (noteVO.getWorkflow_type() == WorkflowTypeEnum.SubWorkApproveflow.getIntValue()) {
					//������������������
//					if(hmPfExParams != null && hmPfExParams.get(PfUtilBaseTools.PARAM_BATCH) != null) {
//						dlg = new BatchApproveWorkitemAcceptDlg(parent, noteVO);
//						
//						BatchApproveModel batchApproveMode = new BatchApproveModel();
//						batchApproveMode.setBillUI(true);
//						batchApproveMode.setSingleBillSelected(true);
//						batchApproveMode.setContainUnApproveBill(false);
//						batchApproveMode.setBillItem(voAryLen);
//						
//						((BatchApproveWorkitemAcceptDlg) dlg).setBachApproveMode(batchApproveMode);
//					} else {
//						dlg = new ApproveWorkitemAcceptDlg(parent, noteVO, true);
//					}
//				} 
//				else{
					//����������������
//					dlg = new WorkflowWorkitemAcceptDlg(parent, noteVO,PfUtilClientAssistor.isCanTransfer(noteVO.getTaskInfo().getTask()));
//				}
//				if (dlg.showModal() == UIDialog.ID_OK) {
					// ���ش�����Ĺ�����
//					m_checkFlag = true;
//				} else {
//					// �û�ȡ��
//					m_checkFlag = false;
//					noteVO = null;
//				}
			}
	}
	
	/**
	 * @return �Ƿ���������
	 * */
	private static boolean isBesideApprove(HashMap hmPfExParams){
		return hmPfExParams!=null&&hmPfExParams.get(PfUtilBaseTools.PARAM_BESIDEAPPROVE)!=null;
	}

	/**
	 * ��־һ�¶��������������Ĳ���
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
	 * ǰ̨���ݶ���������API���㷨���£�
	 * <li>1.����ִ��ǰ��ʾ�Լ���ǰ����������û�ȡ�����򷽷�ֱ�ӷ���
	 * <li>2.�鿴��չ�������ж��Ƿ���Ҫ��������ش��������Ϊ�ύ�������ҵ���VO������ֻ��һ�ŵ���ʱ������Ҫ�ռ��ύ�˵�ָ����Ϣ��
	 * ���Ϊ��������������Ե�һ�ŵ��ݿ�����Ҫ�ռ������˵�������Ϣ
	 * <li>3.��ִ̨���������������ض���ִ�н���� 
	 * 
	 * @param parent ������
	 * @param actionCode �������룬����"SAVE"
	 * @param billOrTranstype �������ͣ��������ͣ�PK
	 * @param voAry ���ݾۺ�VO����
	 * @param userObjAry �û��Զ����������
	 * @param eParam ��չ����
	 * @return �����������ķ��ؽ��
	 * @throws Exception
	 * @since 5.5
	 */
	public static Object[] runBatch(Container parent, String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, String strBeforeUIClass, HashMap eParam)
			throws Exception {
		Logger.debug("*���ݶ��������� ��ʼ");
		debugParams(actionCode, billOrTranstype, voAry, userObjAry);
		long start = System.currentTimeMillis();
		if(voAry!= null && voAry.length == 1) {
			Object obj = runAction(parent, actionCode, billOrTranstype, voAry[0], userObjAry, strBeforeUIClass, null, eParam);
			Object[] ret = null;
			ret = PfUtilBaseTools.composeResultAry(obj,1,0,ret);
			return ret;
		}
		
		m_isSuccess = true;
		
		putElecsignatureValue(billOrTranstype,voAry,eParam);
		
		

		WorkflownoteVO workflownote = null;
		
		// 2.�鿴��չ�������Ƿ�Ҫ���̽�������
		Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
		Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
		if (paramNoflow == null && paramSilent == null) {
			//XXX:guowl,2010-5,����ʱ�����������Ի�����ֻ��ʾ��׼������׼�����أ��ʴ���һ�������������
			//������˵�VO���鳤��Ϊ1��ͬ��������һ��
			if (voAry != null && voAry.length > 1) {
				eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
			}
			if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
				//��������������
				workflownote = actionAboutApproveflow(parent, actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
				if (!m_isSuccess)
					return null;
			} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
				//��������������
				workflownote = actionAboutWorkflow(parent, actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
				if (!m_isSuccess)
					return null;
			}
		}

		// 3.��̨����������
		Logger.debug("*��̨���������� ��ʼ");
		Object retObj = NCLocator.getInstance().lookup(IplatFormEntry.class).processBatch(actionCode,
				billOrTranstype, workflownote, voAry, userObjAry, eParam);
		if(retObj instanceof PfProcessBatchRetObject) {
			String errMsg = ((PfProcessBatchRetObject)retObj).getExceptionMsg();
			retObj = ((PfProcessBatchRetObject)retObj).getRetObj();
		}
		if(retObj != null && ((Object[]) retObj).length > 0) {
			//������ʱ����һ���ɹ��ľ���Ϊ�ɹ�
			m_isSuccess = true;
		}
		Logger.debug("*���ݶ��������� ����=" + (System.currentTimeMillis() - start) + "ms");
		
		return (Object[]) retObj;
	}
	
	public static PfProcessBatchRetObject runBatchNew(Container parent, String actionCode, String billOrTranstype,
			AggregatedValueObject[] voAry, Object[] userObjAry, String strBeforeUIClass, HashMap eParam)
			throws Exception {
		Logger.debug("*���ݶ��������� ��ʼ");
		debugParams(actionCode, billOrTranstype, voAry, userObjAry);
		long start = System.currentTimeMillis();
		if(voAry!= null && voAry.length == 1) {
			Object obj = runAction(parent, actionCode, billOrTranstype, voAry[0], userObjAry, strBeforeUIClass, null, eParam);
			Object[] retObj = null;
			retObj = PfUtilBaseTools.composeResultAry(obj,1,0,retObj);
			return new PfProcessBatchRetObject(retObj, null);
		}
		
		m_isSuccess = true;
		putElecsignatureValue(billOrTranstype,voAry,eParam);

		// 2.�鿴��չ�������Ƿ�Ҫ���̽�������
		WorkflownoteVO workflownote = null;//(WorkflownoteVO)getParamFromMap(eParam, PfUtilBaseTools.PARAM_WORKNOTE);
//		if(workflownote == null){
			Object paramNoflow = getParamFromMap(eParam, PfUtilBaseTools.PARAM_NOFLOW);
			Object paramSilent = getParamFromMap(eParam, PfUtilBaseTools.PARAM_SILENTLY);
			if (paramNoflow == null && paramSilent == null) {
				//XXX:guowl,2010-5,����ʱ�����������Ի�����ֻ��ʾ��׼������׼�����أ��ʴ���һ�������������
				//������˵�VO���鳤��Ϊ1��ͬ��������һ��
				if (voAry != null && voAry.length > 1) {
					eParam = putParam(eParam, PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);
				}
				if (PfUtilBaseTools.isSaveAction(actionCode, billOrTranstype) || PfUtilBaseTools.isApproveAction(actionCode, billOrTranstype)) {
					//��������������
					workflownote = actionAboutApproveflow(parent, actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
					if (!m_isSuccess)
						return null;
				} else if (PfUtilBaseTools.isStartAction(actionCode, billOrTranstype) || PfUtilBaseTools.isSignalAction(actionCode, billOrTranstype)) {
					//��������������
					workflownote = actionAboutWorkflow(parent, actionCode, billOrTranstype, voAry[0], eParam,voAry.length);
					if (!m_isSuccess)
						return null;
				}
				putParam(eParam, PfUtilBaseTools.PARAM_WORKNOTE, workflownote);
			}
		//}
		// 3.��̨����������
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


}