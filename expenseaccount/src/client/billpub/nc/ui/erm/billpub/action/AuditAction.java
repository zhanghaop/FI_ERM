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
import nc.ui.pub.beans.progress.DefaultProgressMonitor;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.editor.BillForm;
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
import nc.vo.uap.pf.PFBusinessException;

public class AuditAction extends ErmAuditAction {
	private static final long serialVersionUID = 1L;
	private BillForm editor;
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
											 * @res "没有可供审核的单据,操作失败"
											 */);
		}
		// 审核较验信息
		msgs = new MessageVO[vos.length];

		List<JKBXVO> auditVOs = new ArrayList<JKBXVO>();

		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);

		for (int i = 0; i < jkbxVos.length; i++) {

			JKBXVO vo = jkbxVos[i];
			// 如果报销单全部用来冲借款后，审批后，支付状态应该设置为：全部冲借款
			if (vo.getParentVO().zfybje.doubleValue() == 0 && vo.getParentVO().hkybje.doubleValue() == 0) {
				vo.getParentVO().setPayflag(BXStatusConst.ALL_CONTRAST);
			}

			vo.setNCClient(true);

			msgs[i] = checkVo(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}

			auditVOs.add(vo);
		}

		if (auditVOs.size() > 0) {
			// 加入进度条的审批
			if (auditVOs.size() > 1) {// 加入进度条的审批
				final DefaultProgressMonitor mon = getTpaProgressUtil().getTPAProgressMonitor();
				mon.beginTask(getBtnName(), auditVOs.size());
				ListApproveSwingWork lpsw = new ListApproveSwingWork(auditVOs.toArray(new AggregatedValueObject[0]),
						mon);
				lpsw.execute();
			} else {
				MessageVO[] returnMsgs = new MessageVO[] { approveSingle(auditVOs.get(0)) };
				List<AggregatedValueObject> auditedVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
				getModel().directlyUpdate(auditedVos.toArray(new AggregatedValueObject[auditedVos.size()]));
				ErUiUtil.showBatchResults(getModel().getContext(), msgs);
			}
		}else{
			ErUiUtil.showBatchResults(getModel().getContext(), msgs);
		}

//		// 显示预算，借款控制的提示信息
//		if (!StringUtils.isNullWithTrim(jkbxVos[0].getWarningMsg()) && jkbxVos[0].getWarningMsg().length() > 0) {
//			MessageDialog.showWarningDlg(getEditor(),
//					nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
//																							 * *
//																							 * 
//																							 * @
//																							 * res
//																							 * *
//																							 * "警告"
//																							 */,
//					jkbxVos[0].getWarningMsg());
//			jkbxVos[0].setWarningMsg(null);
//		}
	}

	/**
	 * 修改加锁失败的提示信息
	 * 
	 * @author chendya
	 */
	private String modifyAddLockErrShowMsg(Exception e) {
		StringBuffer msg = new StringBuffer();
		msg.append(e.getMessage()).append(
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0", "02011v61013-0000")/*
																										 * @
																										 * res
																										 * "，该单据可能同时被他人操作中，请刷新后再试"
																										 */);
		return msg.toString();
	}

	/**
	 * @param bxvo
	 * @param background
	 * @return
	 * 
	 *         校验审核信息
	 */
	private MessageVO checkVo(JKBXVO bxvo) {

		JKBXHeaderVO head = null;

		head = (bxvo.getParentVO());

		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.AUDIT);

		UFDate djrq = head.getDjrq();

		// begin-- modified by chendya@ufida.com.cn 不比较时分秒，只比较Y-M-D
		if (djrq.afterDate(WorkbenchEnvironment.getInstance().getBusiDate())) {
			// --end
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000000")/*
																												 * @
																												 * res
																												 * "审核日期不能早于单据录入日期,不能审核"
																												 */);
			return msgVO;
		}

		return msgVO;
	}

	/**
	 * @param 　审核动作处理
	 * @throws BusinessException
	 *             -Exception
	 * @see 注意:参数actionCode可以采用两种方式 (1)动作编码+getBxParam().getPk_user 参数userObj
	 *      用户自定义对象可以为 空 (2)动作编码 参数userObj 用户自定义对象可以为 getBxParam().getPk_user
	 * @author liansg
	 */

	protected MessageVO approveSingle(AggregatedValueObject appVO) throws Exception{
		JKBXVO bxvo = (JKBXVO)appVO;
		JKBXHeaderVO head = bxvo.getParentVO();
		MessageVO result = null;
		try {
			// 审核动作处理
			Object msgReturn =  PfUtilClient.runAction(getModel().getContext().getEntranceUI(), "APPROVE"
					+ WorkbenchEnvironment.getInstance().getLoginUser().getCuserid(), head.getDjlxbm(), bxvo, null,
					null, null, null);

			if (msgReturn == null) {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000339")/*
															 * @res "用户取消操作"
															 */);
			} else {
				// 显示预算，借款控制的提示信息
//				String warningMsg = null;
				if(msgReturn instanceof MessageVO[]){
					MessageVO[] msgVos = (MessageVO[])msgReturn;
					JKBXVO jkbxvo = (JKBXVO) msgVos[0].getSuccessVO();
//					warningMsg = jkbxvo.getWarningMsg();
					jkbxvo.setWarningMsg(null);
					result = msgVos[0];
				}else if(msgReturn instanceof JKBXVO){//改签和加签的情况下会出现返回AggVo
//					warningMsg = ((JKBXVO)msgReturn).getWarningMsg();
					((JKBXVO)msgReturn).setWarningMsg(null);
					result = new MessageVO((JKBXVO)msgReturn, ActionUtils.AUDIT);
				}
				
//				if (!StringUtils.isNullWithTrim(warningMsg)) {
//					MessageDialog.showWarningDlg(getEditor(),
//							nc.ui.ml.NCLangRes.getInstance().getStrByID("smcomm", "UPP1005-000070")/*
//							 * @
//							 * res
//							 * "警告"
//							 */,
//							 warningMsg);
//				}
			}

		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "提示"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																											 * @
																											 * res
																											 * " 是否继续审核？"
																											 */) == MessageDialog.ID_YES) {
				bxvo.setHasNtbCheck(Boolean.TRUE); // 不检查
				result = approveSingle(bxvo);
			} else {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "预算申请失败"
															 */);
			}
		} catch (ErmException e) {
			if (!Boolean.TRUE.equals(bxvo.getHasJkCheck())) {
				if (MessageDialog.showYesNoDlg(getEditor(),
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																										 * @
																										 * res
																										 * "提示"
																										 */,
						e.getMessage()
								+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																												 * @
																												 * res
																												 * " 是否继续审核？"
																												 */) == MessageDialog.ID_YES) {
					bxvo.setHasJkCheck(Boolean.TRUE); // 不检查
					result = approveSingle(bxvo);
				} else {
					result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("2011", "UPP2011-000406")/*
																 * @res "借款申请失败"
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

				// 是否继续审核
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
																 * "账户金额超出控制"
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
		} catch (ProjBudgetAlarmBusinessException e) {// 项目预算可恢复异常
			if (MessageDialog.showYesNoDlg(getEditor(),
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																									 * @
																									 * res
																									 * "提示"
																									 */, e.getMessage()
							+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																											 * @
																											 * res
																											 * " 是否继续审核？"
																											 */) == MessageDialog.ID_YES) {
				bxvo.setHasProBudgetCheck(Boolean.TRUE); // 不检查
				result = approveSingle(bxvo);

			} else {
				result = new MessageVO(bxvo, ActionUtils.AUDIT, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("201107_0", "0201107-0000")/*
																 * @res
																 * "项目预算申请失败"
																 */);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			final String lockErrMsg = NCLangResOnserver.getInstance().getStrByID("pfworkflow", "UPPpfworkflow-000602")/*
																													 * @
																													 * res
																													 * "当前单据已进行加锁处理"
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
		return getModel().getSelectedData() != null && getAuditstausListener().approveButtonStatus();
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

}
