package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.DeleteAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.IPfRetCheckInfo;

import org.apache.commons.lang.ArrayUtils;

/**
 * 
 * @author wangled
 * 
 */
public class ERMBillDeleteAction extends DeleteAction {
	private static final long serialVersionUID = 1L;
	private ErmBillBillForm editor;

	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {
		// 特殊数据权限，根据业务实体
		if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO() != null
				&& BXConstans.BX_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
			super.setResourceCode("ermexpenseservice");
		} else if (((ErmBillBillManageModel) getModel()).getCurrentDjLXVO() != null
				&& BXConstans.JK_DJDL.equals(((ErmBillBillManageModel) getModel()).getCurrentDjLXVO().getDjdl())) {
			super.setResourceCode("ermloanservice");
		}
		return super.beforeStartDoAction(actionEvent);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object[] obj = (Object[]) ((BillManageModel) getModel()).getSelectedOperaDatas();
		if (ArrayUtils.isEmpty(obj))
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
					"0201107-0137"));

		String msgStr = "";
		boolean isSuccess = true;
		JKBXVO[] vos = Arrays.asList(obj).toArray(new JKBXVO[0]);
		for (JKBXVO vo : vos) {
			vo.setNCClient(true);// 是从NC客户端
		}

		// 对于常用单据处理
		if (((ErmBillBillManageModel) getModel()).iscydj()) {
			checkCommonBill(vos);

			MessageVO[] msgs = deleteVos(vos, msgStr, isSuccess);
			ErUiUtil.showBatchResults(getModel().getContext(), msgs);
		} else {
			// 删除借款报销单
			handleJKBXbill(msgStr, isSuccess, vos);
		}
	}

	/**
	 * 处理常用单据和暂存的单据
	 * 
	 * @param vos
	 * @param msgStr
	 * @param isSuccess
	 * @return
	 * @throws Exception
	 */
	private MessageVO[] deleteVos(JKBXVO[] vos, String msgStr, boolean isSuccess) throws Exception {
		MessageVO[] msgs = new MessageVO[vos.length];
		for (int i = 0; i < vos.length; i++) {
			msgs[i] = new MessageVO(vos[i], ActionUtils.DELETE);
		}
		try {
			MessageVO[] returnMsg = NCLocator.getInstance().lookup(IBXBillPublic.class).deleteBills(vos);
			for (int i = 0; i < returnMsg.length; i++) {
				if (returnMsg[i].isSuccess()) {
					((BillManageModel) getModel()).directlyDelete(returnMsg[i].getSuccessVO());
				}
			}
			ErUiUtil.combineMsgs(msgs, returnMsg);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			List<MessageVO> failMesVO = new ArrayList<MessageVO>();
			for (MessageVO messageVO : msgs) {
				String errMsg = e.getMessage();
				failMesVO.add(new MessageVO(messageVO.getSuccessVO(), ActionUtils.DELETE, false, errMsg));

			}
			return failMesVO.toArray(new MessageVO[0]);
		}

		return msgs;

	}

	private void handleJKBXbill(String msgStr, boolean isSuccess, JKBXVO[] vos) throws Exception {
		MessageVO[] msgs = new MessageVO[vos.length];

		List<JKBXVO> removedVos = new ArrayList<JKBXVO>();
		for (int i = 0; i < vos.length; i++) {
			// 如果是暂存的单据直接删除
			if (vos[i].getParentVO().getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
				MessageVO[] delVoTempsave = deleteVos(new JKBXVO[] { vos[i] }, msgStr, isSuccess);
				msgs[i] = delVoTempsave[0];
				continue;
			}
			try {
				VOStatusChecker.checkDeleteStatus(vos[i].getParentVO());
				msgs[i] = new MessageVO(vos[i], ActionUtils.DELETE);
				removedVos.add(vos[i]);
			} catch (DataValidateException e1) {
				msgs[i] = new MessageVO(vos[i], ActionUtils.DELETE, false, e1.getMessage());
				continue;
			}
		}
		if (removedVos.size() != 0) {
			MessageVO[] returnMsgs = deleteOneByOne(removedVos);
			List<AggregatedValueObject> successVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			((BillManageModel) getModel()).directlyDelete(successVos.toArray(new AggregatedValueObject[] {}));
		}

		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO[] deleteOneByOne(List<JKBXVO> removedVos) throws BusinessException {
		List<MessageVO> result = new ArrayList<MessageVO>();

		for (JKBXVO bxvo : removedVos) {
			try {
				nc.ui.pub.pf.PfUtilClient.runAction(getEditor(), "DELETE", bxvo.getParentVO().getDjlxbm(), bxvo,
						new JKBXVO[] { bxvo }, null, null, null);

				result.add(new MessageVO(bxvo, ActionUtils.DELETE));
			} catch (Exception e) {
				ExceptionHandler.consume(e);
				String errMsg = e.getMessage();
				result.add(new MessageVO(bxvo, ActionUtils.DELETE, false, errMsg));
			}
		}
		return result.toArray(new MessageVO[0]);
	}

	/**
	 * 对于常用单据处理
	 * 
	 * @param vos
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private void checkCommonBill(JKBXVO[] vos) throws BusinessException {
		if (((ErmBillBillManageModel) getModel()).iscydj()) {
			UFBoolean isGroup = BXUiUtil.isGroup(getModel().getContext().getNodeCode());
			// 删除逻辑
			for (JKBXVO vo : vos) {
				if (!isGroup.booleanValue() && vo.getParentVO().getIsinitgroup().booleanValue()) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0002")/*
											 * @res "集团常用单据不能在组织级节点删除"
											 */);
				}
			}
		}
	}

	@Override
	public void doAfterSuccess(ActionEvent actionEvent) {
		if (getMonitor() != null) {
			getMonitor().done();
			setMonitor(null);
		}
	}

	/**
	 * 如果单据审核后就将删除按钮置灰
	 */
	@Override
	protected boolean isActionEnable() {
		Object[] obj = (Object[]) ((BillManageModel) getModel()).getSelectedOperaDatas();
		if (obj != null && model.getUiState() == UIState.NOT_EDIT) {
			JKBXVO[] selectedData = Arrays.asList(obj).toArray(new JKBXVO[0]);
			if (selectedData.length == 1) {
				JKBXHeaderVO parentVO = selectedData[0].getParentVO();
				int spzt = parentVO.getSpzt().intValue();
				int djzt = parentVO.getDjzt().intValue();
				return spzt == IPfRetCheckInfo.NOSTATE && djzt!=BXStatusConst.DJZT_Invalid;
			} else {
				boolean enable = false;
				for (int i = 0; i < selectedData.length; i++) {
					JKBXHeaderVO parentVO = selectedData[i].getParentVO();
					if (parentVO.getSpzt().intValue() == IPfRetCheckInfo.NOSTATE
							&& parentVO.getDjzt().intValue()!=BXStatusConst.DJZT_Invalid) {
						enable = true;
						break;
					}
				}
				return enable;
			}
		} else {
			return false;
		}
	}

	public ErmBillBillForm getEditor() {
		return editor;
	}

	public void setEditor(ErmBillBillForm editor) {
		this.editor = editor;
	}
}
