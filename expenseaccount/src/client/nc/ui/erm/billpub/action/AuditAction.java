package nc.ui.erm.billpub.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.logging.Logger;
import nc.bs.ml.NCLangResOnserver;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.erm.action.ErmAuditAction;
import nc.ui.erm.billpub.btnstatus.BxApproveBtnStatusListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.IShowMsgConstant;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.cmp.exception.CmpAuthorizationException;
import nc.vo.cmp.exception.ErmException;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.exception.ProjBudgetAlarmBusinessException;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDate;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PFBusinessException;

public class AuditAction extends ErmAuditAction {
	private static final long serialVersionUID = 1L;

	private BxApproveBtnStatusListener auditstausListener;
	
	public AuditAction() {
		super();
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();

		if (vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102",
					"UPP2006030102-000116")/*
											 * @res "û�пɹ���˵ĵ���,����ʧ��"
											 */);
		}
		// ��˽�����Ϣ
		msgs = new MessageVO[vos.length];

		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();

		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);

		for (int i = 0; i < jkbxVos.length; i++) {

			JKBXVO vo = jkbxVos[i];
			// ���������ȫ�����������������֧��״̬Ӧ������Ϊ��ȫ������
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (!parentVO.isAdjustBxd()&&parentVO.zfybje.doubleValue() == 0 && parentVO.hkybje.doubleValue() == 0) {
				parentVO.setPayflag(BXStatusConst.ALL_CONTRAST);
			}

			vo.setNCClient(true);

			msgs[i] = checkVo(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}
			auditVOs.add(vo);
		}

		if (auditVOs.size() > 0) {
			// ���������������
			if (auditVOs.size() > 1) {// ���������������
				executeBatchAudit(auditVOs);
			} else {
				MessageVO[] returnMsgs = new MessageVO[] { approveSingle(auditVOs.get(0)) };
				List<AggregatedValueObject> auditedVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
				getModel().directlyUpdate(auditedVos.toArray(new AggregatedValueObject[auditedVos.size()]));
				ErUiUtil.showBatchResults(getModel().getContext(), msgs);
			}
		} else {
			ErUiUtil.showBatchResults(getModel().getContext(), msgs);
		}
	}

	/**
	 * �޸ļ���ʧ�ܵ���ʾ��Ϣ
	 * 
	 * @author chendya
	 */
	private String modifyAddLockErrShowMsg(Exception e) {
		StringBuffer msg = new StringBuffer();
		msg.append(e.getMessage()).append(
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																										 * @
																										 * res
																										 * "���õ��ݿ���ͬʱ�����˲����У���ˢ�º�����"
																										 */);
		return msg.toString();
	}

	/**
	 * @param bxvo
	 * @param background
	 * @return
	 * 
	 *         У�������Ϣ
	 */
	private MessageVO checkVo(JKBXVO bxvo) {

		JKBXHeaderVO head = null;

		head = (bxvo.getParentVO());

		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.AUDIT);

		UFDate djrq = head.getDjrq();

		// begin-- modified by chendya@ufida.com.cn ���Ƚ�ʱ���룬ֻ�Ƚ�Y-M-D
		if (djrq.afterDate(WorkbenchEnvironment.getInstance().getBusiDate())) {
			// --end
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000000")/*
																												 * @
																												 * res
																												 * "������ڲ������ڵ���¼������,�������"
																												 */);
			return msgVO;
		}
		
		if (!(head.getSpzt().equals(IBillStatus.CHECKGOING) || head.getSpzt().equals(IBillStatus.COMMIT))) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0008")/*@res "�õ��ݵ�ǰ״̬���ܽ�����ˣ�"*/);
			return msgVO;
		}
		
		if (head.getDjzt().intValue() == BXStatusConst.DJZT_Invalid) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000955"));
			return msgVO;
		}
		
		//Ȩ��У��
		if(!checkDataPermission(bxvo)){
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(IShowMsgConstant.getDataPermissionInfo());
			return msgVO;
		}

		return msgVO;
	}

	/**
	 * @param ����˶�������
	 * @throws BusinessException
	 *             -Exception
	 * @see ע��:����actionCode���Բ������ַ�ʽ (1)��������+getBxParam().getPk_user ����userObj
	 *      �û��Զ���������Ϊ �� (2)�������� ����userObj �û��Զ���������Ϊ getBxParam().getPk_user
	 * @author liansg
	 */

	protected MessageVO approveSingle(AggregatedValueObject appVO) throws Exception {
		JKBXVO bxvo = (JKBXVO) appVO;
		JKBXHeaderVO head = bxvo.getParentVO();
		MessageVO result = null;
		
		String actionName = getActionCode(head.getPk_org());
		try {
			// ��˶�������
			Object msgReturn = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), actionName
					+ WorkbenchEnvironment.getInstance().getLoginUser().getCuserid(), head.getDjlxbm(), bxvo, null,
					null, null, null);

			if (msgReturn == null) {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000339")/*
															 * @res "�û�ȡ������"
															 */);
			} else {
				if (msgReturn instanceof MessageVO[]) {
					MessageVO[] msgVos = (MessageVO[]) msgReturn;
					JKBXVO jkbxvo = (JKBXVO) msgVos[0].getSuccessVO();
					jkbxvo.setWarningMsg(null);
					result = msgVos[0];
				} else if (msgReturn instanceof JKBXVO) {// ��ǩ�ͼ�ǩ������»���ַ���AggVo
					((JKBXVO) msgReturn).setWarningMsg(null);
					result = new MessageVO((JKBXVO) msgReturn, ActionUtils.AUDIT);
				}
			}

		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "��ʾ"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																											 * @
																											 * res
																											 * " �Ƿ������ˣ�"
																											 */) == MessageDialog.ID_YES) {
				bxvo.setHasNtbCheck(Boolean.TRUE); // �����
				result = approveSingle(bxvo);
			} else {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "Ԥ������ʧ��"
															 */);
			}
		} catch (ErmException e) {
			if (!Boolean.TRUE.equals(bxvo.getHasJkCheck())) {
				if (MessageDialog.showYesNoDlg(getEditor(),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																										 * @
																										 * res
																										 * "��ʾ"
																										 */,
						e.getMessage()
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																												 * @
																												 * res
																												 * " �Ƿ������ˣ�"
																												 */) == MessageDialog.ID_YES) {
					bxvo.setHasJkCheck(Boolean.TRUE); // �����
					result = approveSingle(bxvo);
				} else {
					result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2011", "UPP2011-000406")/*
																 * @res "�������ʧ��"
																 */);
				}
			}
		} catch (nc.vo.cmp.exception.CmpAuthorizationException exp) {
			try {
				Class<?> handler = Class.forName("nc.ui.cmp.settlement.exception.AccountExceptionHandler");
				Constructor<?> constructor = handler.getConstructor(new Class[] { Component.class });
				Object handlerobj = constructor.newInstance(getEditor());
				Method handleexception = handler.getMethod("handleException",
						new Class[] { CmpAuthorizationException.class });

				// �Ƿ�������
				Object pass = handleexception.invoke(handlerobj, exp);
				if ((Boolean) pass) {
					handleexception = handler.getMethod("getAccountList");
					@SuppressWarnings("unchecked")
					List<String> authList = (List<String>) handleexception.invoke(handlerobj);
					bxvo.authList = authList;
					result = approveSingle(bxvo);
				} else {
					result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2011", "UPP2011-000340")/*
																 * @res
																 * "�˻���������"
																 */);
				}
			} catch (Exception e) {
				String msg = e.getMessage();
				if (e instanceof java.lang.reflect.InvocationTargetException) {
					msg = e.getCause().getMessage();
				}
				Logger.error(msg, e);
				throw new BusinessRuntimeException(msg);
			}
		} catch (ProjBudgetAlarmBusinessException e) {// ��ĿԤ��ɻָ��쳣
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "��ʾ"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																											 * @
																											 * res
																											 * " �Ƿ������ˣ�"
																											 */) == MessageDialog.ID_YES) {
				bxvo.setHasProBudgetCheck(Boolean.TRUE); // �����
				result = approveSingle(bxvo);

			} else {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201107_0", "0201107-0000")/*
																 * @res
																 * "��ĿԤ������ʧ��"
																 */);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "��ǰ�����ѽ��м�������"
																													 */;
			if (e instanceof PFBusinessException && lockErrMsg.equals(errMsg)) {
				errMsg = modifyAddLockErrShowMsg(e);
			}
			result = new MessageVO(bxvo, ActionUtils.AUDIT, false, errMsg);
		}
		return result;
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null
				&& getAuditstausListener().approveButtonStatus();
	}

	public BxApproveBtnStatusListener getAuditstausListener() {
		return auditstausListener;
	}

	public void setAuditstausListener(BxApproveBtnStatusListener auditstausListener) {
		this.auditstausListener = auditstausListener;
	}
}
