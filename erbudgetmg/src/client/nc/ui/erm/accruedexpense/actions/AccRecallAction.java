package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.accruedexpense.check.AccruedBillVOStatusChecker;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.uif2.IActionCode;
import nc.imag.itf.IImagUtil;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.trade.pub.IBillStatus;

public class AccRecallAction extends NCAction {

	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;
	
	public AccRecallAction() {
		ActionInitializer.initializeAction(this, IActionCode.RECALL);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object objs[] = getModel().getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}

		MessageVO[] msgs = new MessageVO[objs.length];
		List<AggAccruedBillVO> recallList = new ArrayList<AggAccruedBillVO>();

		for (int i = 0; i < objs.length; i++) {
			AggAccruedBillVO vo = (AggAccruedBillVO) objs[i];

			// 这里将不符合状态的单据过滤掉，减少数据量
			msgs[i] = checkRecall(vo);
			if (!msgs[i].isSuccess()) {
				continue;
			}
			recallList.add(vo);
		}

		if (!recallList.isEmpty()) {
			MessageVO[] returnMsgs = recallOneByOne(recallList);
			List<AggregatedValueObject> recallVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			getModel().directlyUpdate(recallVos.toArray(new AggregatedValueObject[] {}));
		}

		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO[] recallOneByOne(List<AggAccruedBillVO> recallList) {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggAccruedBillVO aggVo : recallList) {
			MessageVO msgReturn = recallSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO recallSingle(AggAccruedBillVO aggvo) {
		MessageVO result = null;
		try {
			String actionType = ErUtil.getUnCommitActionCode(aggvo.getParentVO().getPk_org());
			AggAccruedBillVO[] vos = (AggAccruedBillVO[]) PfUtilClient.runAction(getModel().getContext().getEntranceUI(),
					actionType, aggvo.getParentVO().getPk_tradetype(), aggvo, null, null, null, null);
			result = new MessageVO(vos[0], ActionUtils.RECALL);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(aggvo, ActionUtils.RECALL, false, errMsg);
		}
		return result;
	}

	private MessageVO checkRecall(AggAccruedBillVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.RECALL);
		try {
			AccruedBillVOStatusChecker.checkRecallStatus(vo.getParentVO());
			validateRecall(vo, vo.getParentVO().getPk_accrued_bill());
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}
	
	private MessageVO validateRecall(AggAccruedBillVO vo , String djpk) throws Exception{
		boolean isWfOnImage = false;
		boolean isInstallImag = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),
				BXConstans.IMAG_MODULEID);

		MessageVO result = new MessageVO(vo, ActionUtils.RECALL);
		try {
			if(isInstallImag){
				isWfOnImage = ((IImagUtil) NCLocator.getInstance().lookup(IImagUtil.class.getName())).isWFOnImageActivity(djpk);
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		if(isWfOnImage){
			result.setSuccess(false);
			result.setErrorMessage("当前有影像扫描活动正在进行，无法收回单据");
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
			AggAccruedBillVO aggBean = (AggAccruedBillVO) selectedData[i];
			Integer appStatus = ((AccruedVO) aggBean.getParentVO()).getApprstatus();
			if (appStatus.equals(IBillStatus.COMMIT)) {
				return true;
			}
		}

		return false;
		
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
}
