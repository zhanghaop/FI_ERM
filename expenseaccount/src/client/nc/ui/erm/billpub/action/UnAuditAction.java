package nc.ui.erm.billpub.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bs.ml.NCLangResOnserver;
import nc.bs.uif2.IActionCode;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.pub.MessageLog;
import nc.ui.erm.billpub.btnstatus.BxApproveBtnStatusListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.NCAsynAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.cmp.exception.CmpAuthorizationException;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

public class UnAuditAction extends NCAsynAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private BillForm editor;
	private BxApproveBtnStatusListener auditstausListener;

	private IProgressMonitor monitor = null;

	private TPAProgressUtil tpaProgressUtil;

	public UnAuditAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.UNAPPROVE);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] vos = getModel().getSelectedOperaDatas();

		if (vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030102",
					"UPP2006030102-000135")/*
											 * @res "û�пɹ�����˵ĵ���,����ʧ��"
											 */);
		}

		// ��˽�����Ϣ
		MessageVO[] msgs = new MessageVO[vos.length];
		List<JKBXVO> auditVOs = new ArrayList<JKBXVO>();

		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		for (int i = 0; i < jkbxVos.length; i++) {

			JKBXVO vo = jkbxVos[i];

			vo.setNCClient(true);

			msgs[i] = checkUnShenhe(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}

			auditVOs.add(vo);
		}

		if (auditVOs.size() > 0) {
			List<MessageVO> resultList = new ArrayList<MessageVO>();
			for (JKBXVO bxvo : auditVOs) {
				MessageVO msg = unAudit(bxvo);
				resultList.add(msg);

			}

			List<AggregatedValueObject> resultVos = ErUiUtil.combineMsgs(msgs, resultList.toArray(new MessageVO[0]));
			getModel().directlyUpdate(resultVos.toArray(new AggregatedValueObject[0]));
		}
		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	/**
	 * @param ������˶�������
	 * @throws BusinessException
	 *             -Exception
	 * @see ע��:����actionCode���Բ������ַ�ʽ (1)��������+getBxParam().getPk_user ����userObj
	 *      �û��Զ���������Ϊ �� (2)�������� ����userObj �û��Զ���������Ϊ getBxParam().getPk_user
	 * @author liansg
	 */
	private MessageVO unAudit(JKBXVO bxvo) {
		JKBXHeaderVO head = bxvo.getParentVO();

		MessageVO result = null;
		try {
			// ����˶�������
			Object msgReturn = PfUtilClient.runAction(getEditor(), "UNAPPROVE"
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
					result = msgVos[0];
				} else if (msgReturn instanceof JKBXVO) {
					result = new MessageVO((JKBXVO) msgReturn, ActionUtils.AUDIT);
				}
			}
		} catch (nc.vo.cmp.exception.CmpAuthorizationException exp) {
			try {
				Class<?> handler = Class.forName("nc.ui.cmp.settlement.exception.AccountExceptionHandler");
				Constructor<?> constructor = handler.getConstructor(new Class[] { Component.class });
				Object handlerobj = constructor.newInstance(getEditor());
				Method handleexception = handler.getMethod("handleException",
						new Class[] { CmpAuthorizationException.class });
				Object pass = handleexception.invoke(handlerobj, exp);
				// �Ƿ�������������
				if ((Boolean) pass) {
					handleexception = handler.getMethod("getAccountList");
					@SuppressWarnings("unchecked")
					List<String> authList = (List<String>) handleexception.invoke(handlerobj);
					bxvo.authList = authList;
					result = unAudit(bxvo);
				} else {
					result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2011", "UPP2011-000340")/*
																 * @res
																 * "�˻���������"
																 */);
				}
			} catch (Exception e) {
				throw new BusinessRuntimeException(e.getMessage());
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
				errMsg += modifyAddLockErrShowMsg(e);
			}
			result = new MessageVO(bxvo, ActionUtils.UNAUDIT, false, errMsg);
		}
		return result;
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
	 *         У�鷴�����Ϣ
	 */
	private MessageVO checkUnShenhe(JKBXVO bxvo) {

		// ���ݱ�ͷVO
		JKBXHeaderVO head = bxvo.getParentVO();

		// ���鷵�ص���Ϣ
		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.UNAUDIT);

		if (null != head.getQcbz() && head.getQcbz().booleanValue()) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000091"))/*
																													 * @
																													 * res
																													 * "�ڳ����ݲ��ܷ����"
																													 */;
			return msgVO;
		}
		
		if(head.getDjzt().intValue()==BXStatusConst.DJZT_Invalid){
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPP2011-000955"));
			return msgVO;
		}

		return msgVO;
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && getAuditstausListener().unApproveButtonStatus();
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

	public BxApproveBtnStatusListener getAuditstausListener() {
		return auditstausListener;
	}

	public void setAuditstausListener(BxApproveBtnStatusListener auditstausListener) {
		this.auditstausListener = auditstausListener;
	}

	/**
	 * ��������������ʾ
	 * 
	 * @param context
	 * @param messageVos
	 * @throws BusinessException
	 */
	public void showBatchResults(LoginContext context, MessageVO[] messageVos) throws BusinessException {
		if (ArrayUtils.isEmpty(messageVos))
			return;

		Vector<String> v = new Vector<String>();
		for (MessageVO messagevo : messageVos) {
			if (messagevo == null)
				continue;
			v.addElement(messagevo.toString());
		}
		if (v.size() > 1) {
			JComponent parent = context.getEntranceUI();
			MessageLog f = new MessageLog(parent);
			Double w = Double.valueOf((parent.getToolkit().getScreenSize().getWidth() - f.getWidth()) / 2);
			Double h = Double.valueOf((parent.getToolkit().getScreenSize().getHeight() - f.getHeight()) / 2);
			f.setLocation(w.intValue(), h.intValue());
			f.f_setText(v);
			f.showModal();
		} else if (v.size() == 1) {
			if (messageVos[0].isSuccess()) {
				ShowStatusBarMsgUtil.showStatusBarMsg(v.get(0), context);
			} else {
				throw new BusinessException(v.get(0));
			}
		}
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

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;
	}
}
