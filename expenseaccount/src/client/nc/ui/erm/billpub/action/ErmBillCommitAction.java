package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErUtil;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.trade.pub.IBillStatus;

/**
 * 报销单提交
 * 
 * @author chenshuaia
 * 
 */
public class ErmBillCommitAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public ErmBillCommitAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.COMMIT);
	}

	public void doAction(ActionEvent e) throws Exception {
		Object objs[] = getModel().getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}

		// 审核较验信息
		MessageVO[] msgs = new MessageVO[objs.length];
		List<JKBXVO> commitList = new ArrayList<JKBXVO>();

		for (int i = 0; i < objs.length; i++) {
			JKBXVO vo = (JKBXVO) objs[i];

			// 这里将不符合状态的单据过滤掉，减少数据量
			msgs[i] = checkCommit(vo);
			if (!msgs[i].isSuccess()) {
				continue;
			}
			commitList.add(vo);
		}

		if (!commitList.isEmpty()) {
			MessageVO[] returnMsgs = commitOneByOne(commitList);
			List<AggregatedValueObject> commitVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			getModel().directlyUpdate(commitVos.toArray(new AggregatedValueObject[] {}));
		}

		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO checkCommit(JKBXVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.COMMIT);
		try {
			 VOStatusChecker.checkCommitStatus(vo.getParentVO());
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}

	private MessageVO[] commitOneByOne(List<JKBXVO> auditVOs) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (JKBXVO aggVo : auditVOs) {
			MessageVO msgReturn = commitSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO commitSingle(JKBXVO appVO) throws Exception {
		MessageVO result = null;
		String actionType = ErUtil.getCommitActionCode(appVO.getParentVO().getPk_org());
		try {
			Object obj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), actionType, appVO
					.getParentVO().getDjlxbm(), appVO, null, null, null, null);
			if (obj == null) {
				result = new MessageVO(appVO, ActionUtils.COMMIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000339")/*
																													 * @
																													 * res
																													 * "用户取消操作"
																													 */);
			} else {
				if (obj instanceof JKBXVO) {// 仅提交
					result = new MessageVO((JKBXVO) obj, ActionUtils.COMMIT);
				} else if (obj instanceof MessageVO[]) {// 提交并审批的情况会出现
					MessageVO[] messages = (MessageVO[]) obj;
					result = messages[0];
				}
			}

		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(appVO, ActionUtils.COMMIT, false, errMsg);
		}
		return result;
	}

	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object[] selectedData = getModel().getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			JKBXVO aggBean = (JKBXVO) selectedData[i];
			Integer appStatus = ((JKBXHeaderVO) aggBean.getParentVO()).getSpzt();
			Integer billStatus = ((JKBXHeaderVO) aggBean.getParentVO()).getDjzt();
			// 审核中
			if (billStatus.equals(BXStatusConst.DJZT_Saved) && appStatus.equals(IBillStatus.FREE)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0", null, "02011000-0040",
				null, new String[] { this.getBtnName() })/*
														 * @ res "{0}失败！"
														 */;
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler) getExceptionHandler()).setErrormsg(null);
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
}