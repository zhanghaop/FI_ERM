package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.UIState;
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
 * @author chenshuaia
 * 
 *         单据删除动作，支持批量删除
 * 
 */
@SuppressWarnings("serial")
public class DeleteAction extends nc.ui.uif2.actions.DeleteAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		MAppModel model = (MAppModel) getModel();
		Object[] objs = model.getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}
		MessageVO[] msgs = new MessageVO[objs.length];

		List<AggMatterAppVO> deleteVos = new ArrayList<AggMatterAppVO>();
		for (int i = 0; i < objs.length; i++) {
			AggMatterAppVO aggVO = (AggMatterAppVO) objs[i];
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

	private MessageVO[] deleteOneByOne(List<AggMatterAppVO> deleteVos) {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggMatterAppVO aggVo : deleteVos) {
			MessageVO msgReturn = deleteSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO deleteSingle(AggMatterAppVO aggVo) {
		MessageVO result = null;
		try {
			PfUtilClient.runAction(getModel().getContext().getEntranceUI(), "DELETE", aggVo.getParentVO()
					.getPk_tradetype(), aggVo, null, null, null, null);

			result = new MessageVO(aggVo, ActionUtils.DELETE, true, "");
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();

			result = new MessageVO(aggVo, ActionUtils.DELETE, false, errMsg);
		}
		return result;
	}

	private MessageVO checkDelete(AggMatterAppVO aggVO) {
		MessageVO result = new MessageVO(aggVO, ActionUtils.DELETE, true, "");
		try {
			VOStatusChecker.checkDeleteStatus(aggVO.getParentVO());
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

		Object[] selectedData = ((MAppModel) getModel()).getSelectedOperaDatas();
		if (selectedData == null)
			return false;

		for (int i = 0; i < selectedData.length; i++) {
			AggMatterAppVO aggBean = (AggMatterAppVO) selectedData[i];
			Integer billStatus = ((MatterAppVO) aggBean.getParentVO()).getBillstatus();
			if ((billStatus.equals(BXStatusConst.DJZT_Saved) && aggBean.getParentVO().getApprstatus()
					.equals(IBillStatus.FREE))
					|| billStatus.equals(BXStatusConst.DJZT_TempSaved)) {
				return true;
			}
		}

		return false;
	}
}