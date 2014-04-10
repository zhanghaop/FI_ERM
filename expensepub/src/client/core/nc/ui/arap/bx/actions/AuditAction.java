package nc.ui.arap.bx.actions;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.ui.arap.bx.BxParam;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.exception.CmpAuthorizationException;
import nc.vo.cmp.exception.ErmException;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.util.StringUtils;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDate;
import nc.vo.uap.pf.PFBusinessException;

/**
 * @author twei
 *
 *  ������ˣ�����ˡ�Action
 *
 * nc.ui.arap.bx.actions.AuditAction
 */
public class AuditAction extends BXDefaultAction {

	/**
	 * @param background������˻���
	 * @throws BusinessException
	 */
	public void unAudit( ) throws BusinessException {

		JKBXVO[] vos = getSelBxvosClone();

		if (vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000135")/*
																	 * @res
																	 * "û�пɹ�����˵ĵ���,����ʧ��"
																	 */);
			
		}

		//��˽�����Ϣ
		MessageVO[] msgs = new MessageVO[vos.length];
		MessageVO[] msgReturn = new MessageVO[]{};
		List<JKBXVO> auditVOs=new ArrayList<JKBXVO>();

		for (int i = 0; i < vos.length; i++) {

			JKBXVO vo = vos[i];
			
			msgs[i] = checkUnShenhe(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}
			
			vo.setNCClient(true);
//modified by chendya ��ֲ����̨У��󣬴˷������ڵ��ã�����ֱ����ƽ̨�ķ�������������ڣ�����˵�
//			this.supplementInfoBeforeUnVerification(vo);
//--end

			auditVOs.add(vo);
		}

		if(auditVOs.size()>0){
			List<JKBXVO> resultVos=new ArrayList<JKBXVO>();

			for(JKBXVO bxvo:auditVOs){

				msgReturn = unAudit(msgReturn, bxvo);

				if(msgReturn==null)
					msgReturn = new MessageVO[]{new MessageVO(MessageVO.UNAUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000339")/*@res "�û�ȡ������"*/, false)};

				resultVos = combineMsgs(msgs, msgReturn,resultVos);

			}

			List<JKBXVO> updateVos=new ArrayList<JKBXVO>();
			for(JKBXVO vo:resultVos){
				JKBXHeaderVO parentVO = vo.getParentVO();
				JKBXVO cacheVONew = getVoCache().getVOByPk(parentVO.getPrimaryKey());
				cacheVONew.setParentVO(parentVO);
				updateVos.add(cacheVONew);
			}
			updateVoAndView(updateVos.toArray(new JKBXVO[]{}));
		}

		getMainPanel().viewLog(msgs);

	}


	/**
	 * @param ������˶�������
	 * @throws BusinessException -Exception
	 * @see ע��:����actionCode���Բ������ַ�ʽ
	 *           (1)��������+getBxParam().getPk_user ����userObj �û��Զ���������Ϊ ��
	 *           (2)��������                                                  ����userObj �û��Զ���������Ϊ getBxParam().getPk_user
	 * @author liansg
	 */
	@SuppressWarnings("restriction")
	private MessageVO[] unAudit(MessageVO[] msgReturn, JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();
		try {
			//����˶�������
			msgReturn = (MessageVO[]) PfUtilClient.runAction(getMainPanel(), "UNAPPROVE" + getBxParam().getPk_user(), head.getDjlxbm(), bxvo, null, null, null, null);

		} catch (nc.vo.cmp.exception.CmpAuthorizationException  exp) {
		    try {
				Class<?> handler = Class.forName("nc.ui.cmp.settlement.exception.AccountExceptionHandler");
				Constructor<?> constructor = handler.getConstructor(new Class[]{Component.class});
				Object handlerobj = constructor.newInstance(getMainPanel());
				Method handleexception = handler.getMethod("handleException", new Class[]{CmpAuthorizationException.class});
				Object pass = handleexception.invoke(handlerobj, exp);
				//�Ƿ�������������
				if ((Boolean)pass) {
					handleexception = handler.getMethod("getAccountList");
					List<String> authList =(List<String>) handleexception.invoke(handlerobj);
					bxvo.authList=authList;
					msgReturn=unAudit(msgReturn,bxvo);
				}else{
					msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000340")/*@res "�˻���������"*/, false)};
				}
			} catch (Exception e) {
				throw new BusinessRuntimeException(e.getMessage());
			}
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow","UPPpfworkflow-000602")/*@res "��ǰ�����ѽ��м�������"*/;
			if(e instanceof PFBusinessException && lockErrMsg.equals(errMsg)){
				errMsg += modifyAddLockErrShowMsg(e);
			}
			msgReturn = new MessageVO[]{new MessageVO(MessageVO.UNAUDIT, bxvo, errMsg, false)};
		}
		return msgReturn;
	}



	/**
	 * @param background ������˻���
	 * @throws BusinessException
	 */
	public void auditOld( ) throws BusinessException {

		JKBXVO[] vos = getSelBxvosClone();

		if (vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000116")/*
																	 * @res
																	 * "û�пɹ���˵ĵ���,����ʧ��"
																	 */);
		}

		// ��˽�����Ϣ
		MessageVO[] msgs = new MessageVO[vos.length];
		MessageVO[] msgReturn = new MessageVO[]{};
		List<JKBXVO> auditVOs=new ArrayList<JKBXVO>();

		for (int i = 0; i < vos.length; i++) {

			JKBXVO vo = vos[i];

			msgs[i] = checkShenhe(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}

			auditVOs.add(vo);
		}
		List<JKBXVO> resultVos =new ArrayList<JKBXVO>();

		if(auditVOs.size()>0){

			resultVos = auditOneByOne(msgs, msgReturn, auditVOs, resultVos);

			List<JKBXVO> updateVos=new ArrayList<JKBXVO>();
			for(JKBXVO vo:resultVos){
				JKBXHeaderVO parentVO = vo.getParentVO();
				JKBXVO cacheVONew = getVoCache().getVOByPk(parentVO.getPrimaryKey());
				cacheVONew.setParentVO(parentVO);
				updateVos.add(cacheVONew);
			}
			updateVoAndView(updateVos.toArray(new JKBXVO[]{}));
		}
		
		//��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
		if(!StringUtils.isNullWithTrim(vos[0].getWarningMsg())&&vos[0].getWarningMsg().length()>0){
			showWarningMsg(vos[0].getWarningMsg());
			vos[0].setWarningMsg(null);
		}

		getMainPanel().viewLog(msgs);
	}


	/**
	 * @param headers
	 * @return �����ݴ������VO����
	 */
	private Map<String, List<JKBXHeaderVO>> splitJkbx(JKBXHeaderVO[] headers) {
		Map<String, List<JKBXHeaderVO>> map = new HashMap<String, List<JKBXHeaderVO>>();
		for (int i = 0; i < headers.length; i++) {
			JKBXHeaderVO headerVO = headers[i];
			String djdl = headerVO.getDjdl();
			if (map.containsKey(djdl)) {
				map.get(djdl).add(headerVO);
			} else {
				List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
				list.add(headerVO);
				map.put(djdl, list);
			}
		}
		return map;
	}
	
	/**
	 * @param background ������˻���
	 * @throws BusinessException
	 */
	public void audit( ) throws BusinessException {

		JKBXVO[] vos = getSelBxvosClone();

		if (vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance()
					.getStrByID("2006030102", "UPP2006030102-000116")/*
																	 * @res
																	 * "û�пɹ���˵ĵ���,����ʧ��"
																	 */);
		}

		// ��˽�����Ϣ
		MessageVO[] msgs = new MessageVO[vos.length];
		MessageVO[] msgReturn = new MessageVO[]{};
		List<JKBXVO> auditVOs_JK=new ArrayList<JKBXVO>();
		List<JKBXVO> auditVOs_BX=new ArrayList<JKBXVO>();
		
		for (int i = 0; i < vos.length; i++) {

			JKBXVO vo = vos[i];
			
			vo.setNCClient(true);
			
			msgs[i] = checkShenhe(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}

			if(vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)){
				auditVOs_BX.add(vo);
			}else if(vo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)){
				auditVOs_JK.add(vo);
			}
		}
		List<JKBXVO> resultVos_JK =new ArrayList<JKBXVO>();
		List<JKBXVO> resultVos_BX =new ArrayList<JKBXVO>();
		List<JKBXVO> updateVos=new ArrayList<JKBXVO>();
		
		if(auditVOs_BX.size()>0){
			resultVos_BX = auditBatch(msgs, msgReturn, auditVOs_BX, resultVos_BX);
			for(JKBXVO vo:resultVos_BX){
				JKBXHeaderVO parentVO = vo.getParentVO();
				JKBXVO cacheVONew = getVoCache().getVOByPk(parentVO.getPrimaryKey());
				cacheVONew.setParentVO(parentVO);
				updateVos.add(cacheVONew);
			}
		}
		if(auditVOs_JK.size()>0){
			resultVos_JK = auditBatch(msgs, msgReturn, auditVOs_JK, resultVos_JK);
			for(JKBXVO vo:resultVos_JK){
				JKBXHeaderVO parentVO = vo.getParentVO();
				JKBXVO cacheVONew = getVoCache().getVOByPk(parentVO.getPrimaryKey());
				cacheVONew.setParentVO(parentVO);
				updateVos.add(cacheVONew);
			}
		}
		updateVoAndView(updateVos.toArray(new JKBXVO[]{}));
		
		//��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
		if(!StringUtils.isNullWithTrim(vos[0].getWarningMsg())&&vos[0].getWarningMsg().length()>0){
			showWarningMsg(vos[0].getWarningMsg());
			vos[0].setWarningMsg(null);
		}

		getMainPanel().viewLog(msgs);
	}

	private List<JKBXVO> auditOneByOne(MessageVO[] msgs, MessageVO[] msgReturn, List<JKBXVO> auditVOs, List<JKBXVO> resultVos) {
		for(JKBXVO bxvo:auditVOs){

			msgReturn = auditSingle(msgReturn, bxvo);

			if(msgReturn==null)
				msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000339")/*@res "�û�ȡ������"*/, false)};

			resultVos = combineMsgs(msgs, msgReturn,resultVos);
		}
		return resultVos;
	}

	private List<JKBXVO> auditBatch(MessageVO[] msgs, MessageVO[] msgReturn, List<JKBXVO> auditVOs, List<JKBXVO> resultVos){
		JKBXHeaderVO head = auditVOs.get(0).getParentVO();

		try {
			//��˶�������
			msgReturn = (MessageVO[]) PfUtilClient.runBatch(getMainPanel(), "APPROVE"+getBxParam().getPk_user(), head.getDjlxbm(), auditVOs.toArray(new JKBXVO[]{}), null , null, null);
			
			int i=0;
			for(MessageVO vo:msgReturn){
				if(vo==null){
					msgReturn[i]=new MessageVO(MessageVO.AUDIT,auditVOs.get(i),"",false);
				}
				i++;
			}
			//��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
			if(!StringUtils.isNullWithTrim(msgReturn[0].getBxvo().getWarningMsg())){
				showWarningMsg(msgReturn[0].getBxvo().getWarningMsg());
				msgReturn[0].getBxvo().setWarningMsg(null);
			}
		}  catch (BugetAlarmBusinessException e) {
			return auditOneByOne(msgs, msgReturn, auditVOs, resultVos);
		} catch (ErmException e) {
			return auditOneByOne(msgs, msgReturn, auditVOs, resultVos);
		}catch (nc.vo.cmp.exception.CmpAuthorizationException exp) {
			return auditOneByOne(msgs, msgReturn, auditVOs, resultVos);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow","UPPpfworkflow-000602")/*@res "��ǰ�����ѽ��м�������"*/;
			if(e instanceof PFBusinessException && lockErrMsg.equals(errMsg)){
				errMsg = modifyAddLockErrShowMsg(e);
			}
			msgReturn=new MessageVO[auditVOs.size()];
			int i=0;
			for(JKBXVO bxvo:auditVOs){
				msgReturn[i++] = new MessageVO(MessageVO.AUDIT, bxvo, errMsg, false);
			}
		}
		
		resultVos = combineMsgs(msgs, msgReturn,resultVos);
		
		return resultVos;
	}
	
	/**
	 * @param ����˶�������
	 * @throws BusinessException -Exception
	 * @see ע��:����actionCode���Բ������ַ�ʽ
	 *           (1)��������+getBxParam().getPk_user ����userObj �û��Զ���������Ϊ ��
	 *           (2)��������                                                  ����userObj �û��Զ���������Ϊ getBxParam().getPk_user
	 * @author liansg
	 */

	private MessageVO[] auditSingle(MessageVO[] msgReturn, JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();

		try {
			//��˶�������
			msgReturn = (MessageVO[]) PfUtilClient.runAction(getMainPanel(), "APPROVE"+getBxParam().getPk_user(), head.getDjlxbm(), bxvo, null , null, null, null);

			//��ʾԤ�㣬�����Ƶ���ʾ��Ϣ
			if(!StringUtils.isNullWithTrim(msgReturn[0].getBxvo().getWarningMsg())){
				showWarningMsg(msgReturn[0].getBxvo().getWarningMsg());
				msgReturn[0].getBxvo().setWarningMsg(null);
			}

		}  catch (BugetAlarmBusinessException e) {
			if(MessageDialog.showYesNoDlg(getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, e.getMessage() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000341")/*@res " �Ƿ������ˣ�"*/)== MessageDialog.ID_YES){
				bxvo.setHasNtbCheck(Boolean.TRUE);  //�����
				msgReturn=auditSingle(msgReturn,bxvo);

			}else{
				msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000405")/*@res "Ԥ������ʧ��"*/, false)};
			}
		} catch (ErmException e) {
				if(!Boolean.TRUE.equals(bxvo.getHasJkCheck())){
					if(MessageDialog.showYesNoDlg(getParent(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000049")/*@res "��ʾ"*/, e.getMessage() + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000341")/*@res " �Ƿ������ˣ�"*/)== MessageDialog.ID_YES){
						bxvo.setHasJkCheck(Boolean.TRUE);  //�����
						msgReturn=auditSingle(msgReturn,bxvo);
					}else{
						msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000406")/*@res "�������ʧ��"*/, false)};
					}
				}
		}catch (nc.vo.cmp.exception.CmpAuthorizationException exp) {
			 try {
					Class<?> handler = Class.forName("nc.ui.cmp.settlement.exception.AccountExceptionHandler");
					Constructor<?> constructor = handler.getConstructor(new Class[]{Component.class});
					Object handlerobj = constructor.newInstance(getMainPanel());
					Method handleexception = handler.getMethod("handleException", new Class[]{CmpAuthorizationException.class});

					//�Ƿ�������
					Object pass = handleexception.invoke(handlerobj, exp);
					if ((Boolean)pass) {
						handleexception = handler.getMethod("getAccountList");
						List<String> authList =(List<String>) handleexception.invoke(handlerobj);
						bxvo.authList=authList;
						msgReturn = auditSingle(msgReturn,bxvo);
					}else{
						msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000340")/*@res "�˻���������"*/, false)};
					}
				} catch (Exception e) {
					throw new BusinessRuntimeException(e.getMessage());
				}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow","UPPpfworkflow-000602")/*@res "��ǰ�����ѽ��м�������"*/;
			if(e instanceof PFBusinessException && lockErrMsg.equals(errMsg)){
				errMsg = modifyAddLockErrShowMsg(e);
			}
			msgReturn = new MessageVO[]{new MessageVO(MessageVO.AUDIT, bxvo, errMsg, false)};
		}
		return msgReturn;
	}

	/**
	 * �޸ļ���ʧ�ܵ���ʾ��Ϣ
	 * @author chendya
	 */
	private String modifyAddLockErrShowMsg(Exception e){
		StringBuffer msg = new StringBuffer();
		msg.append(e.getMessage()).append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0","02011v61013-0000")/*@res "���õ��ݿ���ͬʱ�����˲����У���ˢ�º�����"*/);
		return msg.toString();
	}

	/**
	 * @param bxvo
	 * @param background
	 * @return
	 *
	 * У�������Ϣ
	 */
	private MessageVO checkShenhe(JKBXVO bxvo ) {

		BxParam bxParam = getBxParam();

		JKBXHeaderVO head = null;

		head = (bxvo.getParentVO());

		MessageVO msgVO = new MessageVO(MessageVO.AUDIT, bxvo, "", true);


		if (!msgVO.isSuccess()) {
			return msgVO;
		}

		UFDate djrq = null;

		djrq = head.getDjrq();
//begin-- modified by chendya@ufida.com.cn ���Ƚ�ʱ���룬ֻ�Ƚ�Y-M-D
//		if (djrq.after(bxParam.getBusiDate())) {
		if (djrq.afterDate(bxParam.getBusiDate())) {
//--end
			msgVO.setSuccess(false);
			msgVO.setMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000000")/*@res "������ڲ������ڵ���¼������,�������"*/);
			return msgVO;
		}

		return msgVO;

	}
//modified by chendya ��ֲ����̨У��󣬴˷������ڵ��ã�����ֱ����ƽ̨�ķ�������������ڣ�����˵�
//	/**
//	 * @param vo
//	 * @return
//	 *
//	 * vo�����Ϣ����
//	 */
//	private BXVO supplementInfoBeforeVerification(BXVO vo) {
//		BxParam bxParam = getBxParam();
//		BXHeaderVO head = (BXHeaderVO) vo.getParentVO();
//		UFDateTime sysDateTime = bxParam.getSysDateTime();
//		UFDate busiDate = bxParam.getBusiDate();
//		UFDateTime ufDateTime = new UFDateTime(busiDate,sysDateTime.getUFTime());
//		head.setShrq(ufDateTime);
//
//		head.setApprover(bxParam.getPk_user());
//		return vo;
//	}
//--end

	/**
	 * @param bxvo
	 * @param background
	 * @return
	 *
	 * У�鷴�����Ϣ
	 */
	private MessageVO checkUnShenhe(JKBXVO bxvo ) {

		// ���ݱ�ͷVO
		JKBXHeaderVO head = bxvo.getParentVO();

		// ���鷵�ص���Ϣ
		MessageVO msgVO = new MessageVO(MessageVO.UNAUDIT, bxvo, "", true);

		if (null != head.getQcbz() && head.getQcbz().booleanValue()) {
			msgVO.setSuccess(false);
			msgVO.setMsg(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000091"))/* @res "�ڳ����ݲ��ܷ����" */;
			return msgVO;
		}

		return msgVO;
	}


}