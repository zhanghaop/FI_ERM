package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.check.VOStatusChecker;
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
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.trade.pub.IBillStatus;

/**
 * 申请单提交
 * 
 * @author chenshuaia
 * 
 */
public class CommitAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public CommitAction() {
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
		List<AggMatterAppVO> commitList = new ArrayList<AggMatterAppVO>();

		for (int i = 0; i < objs.length; i++) {
			AggMatterAppVO vo = (AggMatterAppVO) objs[i];

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

	private MessageVO checkCommit(AggMatterAppVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.COMMIT);
		try {
			VOStatusChecker.checkCommitStatus(vo.getParentVO());
		} catch (DataValidateException e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}

	private MessageVO[] commitOneByOne(List<AggMatterAppVO> auditVOs) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggMatterAppVO aggVo : auditVOs) {
			MessageVO msgReturn = commitSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO commitSingle(AggMatterAppVO appVO) throws Exception {
		MessageVO result = null;
		try {
			String actionType = ErUtil.getCommitActionCode(appVO.getParentVO().getPk_org());
			Object obj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), actionType, appVO
					.getParentVO().getPk_tradetype(), appVO, null, null, null, null);
			if (obj == null) {
				result = new MessageVO(appVO, ActionUtils.COMMIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000339")/*
																													 * @
																													 * res
																													 * "用户取消操作"
																													 */);
			} else {
				if (obj instanceof AggMatterAppVO[]) {// 仅提交
					AggMatterAppVO[] vos = (AggMatterAppVO[]) obj;
					result = new MessageVO(vos[0], ActionUtils.COMMIT);
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
			AggMatterAppVO aggBean = (AggMatterAppVO) selectedData[i];
			Integer appStatus = ((MatterAppVO) aggBean.getParentVO()).getApprstatus();
			Integer billStatus = ((MatterAppVO) aggBean.getParentVO()).getBillstatus();
			// 审核中
			if (billStatus.equals(BXStatusConst.DJZT_Saved) && appStatus.equals(IBillStatus.FREE)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
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