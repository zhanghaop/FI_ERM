package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.check.AccruedBillVOStatusChecker;
import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.DeleteAction;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.trade.pub.IBillStatus;

public class AccDelAction extends DeleteAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AccManageAppModel model = (AccManageAppModel) getModel();
		Object[] objs = model.getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}
		MessageVO[] msgs = new MessageVO[objs.length];

		List<AggAccruedBillVO> deleteVos = new ArrayList<AggAccruedBillVO>();
		for (int i = 0; i < objs.length; i++) {
			AggAccruedBillVO aggVO = (AggAccruedBillVO) objs[i];
			msgs[i] = checkDelete(aggVO);
			if (msgs[i].isSuccess()) {
				deleteVos.add(aggVO);
			}
		}

		if (!deleteVos.isEmpty()) {
			MessageVO[] returnMsgs = deleteOneByOne(deleteVos);
			List<AggregatedValueObject> realDeletedVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			model.directlyDelete(realDeletedVos.toArray(new AggregatedValueObject[] {}));
		}

		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	@Override
	public void doAfterSuccess(ActionEvent actionEvent) {
		if (getMonitor() != null) {
			getMonitor().done();
			setMonitor(null);
		}
	}

	private MessageVO[] deleteOneByOne(List<AggAccruedBillVO> deleteVos) {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggAccruedBillVO aggVo : deleteVos) {
			MessageVO msgReturn = deleteSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO deleteSingle(AggAccruedBillVO aggVo) {
		MessageVO result = null;
		try {
			if (aggVo.getParentVO().getRedflag() != null
					&& ErmAccruedBillConst.REDFLAG_RED == aggVo.getParentVO().getRedflag()) {// 删除红冲单据
				NCLocator.getInstance().lookup(IErmAccruedBillManage.class).unRedbackVO(aggVo);

			} else {
				PfUtilClient.runAction(getModel().getContext().getEntranceUI(), "DELETE", aggVo.getParentVO()
						.getPk_tradetype(), aggVo, null, null, null, null);
			}

			result = new MessageVO(aggVo, ActionUtils.DELETE, true, "");
		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "提示"
														 */, e.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
					getStrByID("upp2012v575_0","0upp2012V575-0133")/*@res "是否继续删除？"*/) == MessageDialog.ID_YES) {
				aggVo.getParentVO().setHasntbcheck(UFBoolean.TRUE); // 不检查
				result = deleteSingle(aggVo);
			} else {
				result = new MessageVO(aggVo, ActionUtils.DELETE, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "预算申请失败"
															 */);
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();

			result = new MessageVO(aggVo, ActionUtils.DELETE, false, errMsg);
		}
		return result;
	}

	private MessageVO checkDelete(AggAccruedBillVO aggVO) {
		MessageVO result = new MessageVO(aggVO, ActionUtils.DELETE, true, "");
		try {
			if (aggVO.getParentVO().getRedflag() != null
					&& aggVO.getParentVO().getRedflag() == ErmAccruedBillConst.REDFLAG_RED) {// 红冲单据不校验单据状态
				return result;
			}else {
				AccruedBillVOStatusChecker.checkDeleteStatus(aggVO.getParentVO());
			}
		} catch (DataValidateException ex) {
			result.setSuccess(false);
			result.setErrorMessage(ex.getMessage());
		}

		return result;
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT || getModel().getSelectedData() == null) {
			return false;
		}

		Object[] selectedData = ((AccManageAppModel) getModel()).getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggAccruedBillVO aggBean = (AggAccruedBillVO) selectedData[i];

			// 红冲的单据可以删除
			if (aggBean.getParentVO().getRedflag() != null
					&& aggBean.getParentVO().getRedflag() == ErmAccruedBillConst.REDFLAG_RED) {
				return true;
			}

			Integer billStatus = ((AccruedVO) aggBean.getParentVO()).getBillstatus();
			if ((billStatus.equals(ErmAccruedBillConst.BILLSTATUS_SAVED) && aggBean.getParentVO().getApprstatus()
					.equals(IBillStatus.FREE))
					|| billStatus.equals(ErmAccruedBillConst.BILLSTATUS_TEMPSAVED)) {
				return true;
			}
		}

		return false;
	}

}
