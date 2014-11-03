package nc.ui.erm.accruedexpense.actions;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.uif2.NCAsynAction;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.trade.pub.IBillStatus;
/**
 * 预提单作废按钮
 */

public class AccInvalidAction extends NCAsynAction{
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	private TPAProgressUtil tpaProgressUtil;
	private IProgressMonitor monitor = null;

	public AccInvalidAction() {
		setCode("Invalid");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0175")/*@res "作废"*/);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos==null || vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0","0201107-0176")/* @res "没有可作废的单据,操作失败"*/);
		}
		AggAccruedBillVO[] accruedBill = Arrays.asList(vos).toArray(new AggAccruedBillVO[0]);

		MessageVO[] msgs = new MessageVO[accruedBill.length];

		MessageVO[] returnMsgs = invalidOneByOne(accruedBill);

		List<AggregatedValueObject> successVos = ErUiUtil.combineMsgs(msgs, returnMsgs);

		//更新界面数据
		getModel().directlyUpdate(successVos.toArray(new AggregatedValueObject[] {}));

		//界面提示
		ErUiUtil.showBatchResults(getModel().getContext(), returnMsgs);
	}

	private MessageVO[] invalidOneByOne(AggAccruedBillVO[] accruedBill) {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggAccruedBillVO aggVo : accruedBill) {
			MessageVO msgReturn = invalidSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO invalidSingle(AggAccruedBillVO aggVo) {
		MessageVO result = null;
		try {
			AggAccruedBillVO returnVo =NCLocator.getInstance().lookup(IErmAccruedBillManage.class).invalidBill(aggVo);
			result = new MessageVO(returnVo, ActionUtils.INVALID);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(aggVo, ActionUtils.INVALID, false, errMsg);
		}
		return result;
	}

	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent)
			throws Exception {
		boolean ret = UIDialog.ID_YES == showConfirmDisableDialog(model.getContext().getEntranceUI());
		if(ret){
			if (monitor != null && !monitor.isDone()) {
				return false;
			}
			monitor = getTpaProgressUtil().getTPAProgressMonitor();
			monitor.beginTask("invalid", -1);
			monitor.setProcessInfo("invalid");
		}
		return ret;
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;
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

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		boolean inenable = false;
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos != null && vos.length != 0) {
			for (int i = 0; i < vos.length; i++) {
				AccruedVO parentVO = ((AggAccruedBillVO) vos[i]).getParentVO();
				int djzt = parentVO.getBillstatus().intValue();
				int spzt = parentVO.getApprstatus().intValue();
				if (ErmAccruedBillConst.BILLSTATUS_SAVED == djzt && spzt == IBillStatus.FREE) {
					inenable = true;
					break;
				}
			}
		}
		return inenable;
	}

	public int showConfirmDisableDialog(Container parent){
		String TITLE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0003")/*@res "确认作废"*/;
		String QUESTION = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("accruedbill_0","02011001-0004")/*@res "是否确认作废?"*/;
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION, UIDialog.ID_NO);
	}
}