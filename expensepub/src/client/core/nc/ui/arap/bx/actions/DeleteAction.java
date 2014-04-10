package nc.ui.arap.bx.actions;

import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.uap.pf.IWorkflowMachine;
import nc.ui.er.util.BXUiUtil;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author twei
 * 
 *         单据删除动作，支持批量删除
 * 
 *         nc.ui.arap.bx.actions.DeleteAction
 */
public class DeleteAction extends BXDefaultAction {

	@SuppressWarnings("deprecation")
	private boolean isConfirmDelete() {

		if (getParent().showOkCancelMessage(
				NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
						"UPP2006030102-000384")/*
												 * @res "确定删除单据?"
												 */) == nc.ui.pub.msg.MessageDetailDlg.ID_OK) {
			return true;
		} else {
			getParent().showHintMessage(
					NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102",
							"UPP2006030102-000385")/* @res "操作取消" */);
			return false;
		}
	}

	public void delete() throws BusinessException {

		if (!isConfirmDelete()) {
			return;
		}

		JKBXVO[] vos = getSelBxvos();

		if (vos == null || vos.length == 0)
			return;

		List<JKBXVO> removedVos = new ArrayList<JKBXVO>();

		MessageVO[] msgs = new MessageVO[vos.length];
		MessageVO[] msgReturn = new MessageVO[] {};

		JKBXHeaderVO head = vos[0].getParentVO();

		List<JKBXVO> resultVos = new ArrayList<JKBXVO>();
		String funcode = getMainPanel().getFuncCode();
		UFBoolean isGroup = BXUiUtil.isGroup(funcode);

		if (getBxParam().isInit() || getBxParam().getIsQc()
				|| head.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {

			// 删除逻辑
			for (JKBXVO vo : vos) {
				
				vo.setNCClient(true);
				
				if (!isGroup.booleanValue() && getBxParam().isInit()
						&& vo.getParentVO().getIsinitgroup().booleanValue()) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
							.getNCLangRes().getStrByID("expensepub_0",
									"02011002-0002")/* @res "集团常用单据不能在组织级节点删除" */);
				}
			}

			msgReturn = getIBXBillPublic().deleteBills(vos);

			msgs = msgReturn;

			resultVos = combineMsgs(msgs, msgReturn, resultVos);

		} else {

			for (int i = 0; i < vos.length; i++) {

				String msg = "";

				try {
					VOStatusChecker.checkDeleteStatus(vos[i].getParentVO());
				} catch (DataValidateException e) {
					msg = e.getMessage();
				}

				if (vos[i].getParentVO().isInit()) {
					if (!WorkbenchEnvironment.getInstance().getGroupVO()
							.getPrimaryKey().equals(
									vos[i].getParentVO().getPk_group())) {
						msg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("2011", "UPP2011-000411")/*
																	 * @res
																	 * "不能删除其他公司设置的常用单据内容！"
																	 */;
					}
				}

				if (!StringUtils.isNullWithTrim(msg)) {

					msgs[i] = new MessageVO(MessageVO.DELETE, vos[i], msg,
							false);

					continue;
				} else {
					msgs[i] = new MessageVO(MessageVO.DELETE, vos[i], "", true);

					removedVos.add(vos[i]);
				}
			}

			if (removedVos.size() != 0) {
				for (JKBXVO bxvo : removedVos) {
					try {
						msgReturn = (MessageVO[]) nc.ui.pub.pf.PfUtilClient
								.runAction(getMainPanel(), "DELETE", bxvo
										.getParentVO().getDjlxbm(), bxvo,
										new JKBXVO[] { bxvo }, null, null, null);
					} catch (Exception e) {
						ExceptionHandler.consume(e);
						msgReturn = new MessageVO[] { new MessageVO(
								MessageVO.DELETE, bxvo, e.getMessage(), false) };
					}
					resultVos = combineMsgs(msgs, msgReturn, resultVos);
				}
			}
		}

		if (removedVos.size() != 0) {
			getMainPanel().getBillListPanel().getBodyBillModel()
					.clearBodyData();
		}

		getMainPanel().viewLog(msgs);

		if (resultVos.size() > 0) {

			getVoCache().removeVOList(resultVos);
			for (JKBXVO bx : resultVos) {
				if (bx.getParentVO().getDjzt() == BXStatusConst.DJZT_Saved) {
					((IWorkflowMachine) NCLocator.getInstance().lookup(
							IWorkflowMachine.class.getName())).deleteCheckFlow(
							bx.getParentVO().getDjlxbm(), bx.getParentVO()
									.getDjbh(), bx, bx.getParentVO()
									.getAuditman());
				}
			}

			getMainPanel().updateView();
		}
	}
}