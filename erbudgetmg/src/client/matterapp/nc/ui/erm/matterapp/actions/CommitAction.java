package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.trade.pub.IBillStatus;

/**
 * �ύ
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

		// ��˽�����Ϣ
		MessageVO[] msgs = new MessageVO[objs.length];
		List<AggMatterAppVO> commitList = new ArrayList<AggMatterAppVO>();

		for (int i = 0; i < objs.length; i++) {
			AggMatterAppVO vo = (AggMatterAppVO) objs[i];

			// ���ｫ������״̬�ĵ��ݹ��˵�������������
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
			Object obj = PfUtilClient.runAction(getModel().getContext().getEntranceUI(), IPFActionName.SAVE, appVO
					.getParentVO().getPk_tradetype(), appVO, null, null, null, null);
			if (obj == null) {
				result = new MessageVO(appVO, ActionUtils.COMMIT);
				result.setSuccess(false);
				result.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000339")/*
																													 * @
																													 * res
																													 * "�û�ȡ������"
																													 */);
			} else {
				if (obj instanceof AggMatterAppVO[]) {// ���ύ
					AggMatterAppVO[] vos = (AggMatterAppVO[]) obj;
					result = new MessageVO(vos[0], ActionUtils.COMMIT);
				} else if (obj instanceof MessageVO[]) {// �ύ����������������
					MessageVO[] messages = (MessageVO[]) obj;
					MatterAppVO parentVO = (MatterAppVO) messages[0].getSuccessVO().getParentVO();
					if (!StringUtils.isNullWithTrim(parentVO.getWarningmsg())) {
						MessageDialog.showWarningDlg(getModel().getContext().getEntranceUI(),
								nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0", "0201212-0000")/*
																												 * @
																												 * res
																												 * "��ʾ"
																												 */,
								parentVO.getWarningmsg());
						parentVO.setWarningmsg(null);
					}
					result = messages[0];
				}
			}

		} catch (BugetAlarmBusinessException ex) {
			if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("2011", "UPP2011-000049")/*
																		 * @ res
																		 * "��ʾ"
																		 */, ex.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000341")/*
																									 * @
																									 * res
																									 * " �Ƿ������ˣ�"
																									 */) == MessageDialog.ID_YES) {
				appVO.getParentVO().setHasntbcheck(UFBoolean.TRUE); // �����
				result = commitSingle(appVO);
			} else {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000405")
				/* @res "Ԥ������ʧ��" */, ex);
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
			// �����
			if (billStatus.equals(BXStatusConst.DJZT_Saved) && appStatus.equals(IBillStatus.FREE)) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = this.getBtnName() + ErmActionConst.FAIL_MSG;
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