package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.matterapp.check.VOStatusChecker;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;

/**
 * ¹Ø±Õ°´Å¥
 * 
 * @author chenshuaia
 * 
 */
public class CloseBillAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private IErmMatterAppBillClose appBillCloseService;

	private MAppModel model;

	public CloseBillAction() {
		super();
		this.setBtnName(ErmActionConst.getCloseBillName());
		this.setCode(ErmActionConst.CLOSEBILL);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] objs = getModel().getSelectedOperaDatas();

		MessageVO[] msgs = new MessageVO[objs.length];
		List<AggMatterAppVO> closeList = new ArrayList<AggMatterAppVO>();

		for (int i = 0; i < objs.length; i++) {
			AggMatterAppVO vo = (AggMatterAppVO) objs[i];

			msgs[i] = checkClose(vo);

			if (msgs[i].isSuccess()) {
				closeList.add(vo);
			}
		}
		if (!closeList.isEmpty()) {
			MessageVO[] returnMsgs = closeOneByOne(closeList);
			List<AggregatedValueObject> closeVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			getModel().directlyUpdate(closeVos.toArray(new AggregatedValueObject[] {}));
		}
		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO checkClose(AggMatterAppVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.CLOSE);
		try {
			VOStatusChecker.checkCloseStatus(vo.getParentVO());
		} catch (DataValidateException e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}

	private MessageVO[] closeOneByOne(List<AggMatterAppVO> auditVOs) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggMatterAppVO aggVo : auditVOs) {
			MessageVO msgReturn = closeSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO closeSingle(AggMatterAppVO appVO) throws Exception {
		MessageVO result = null;
		try {
			AggMatterAppVO[] vos = getAppBillService().closeVOs(new AggMatterAppVO[] { appVO });
			result = new MessageVO(vos[0], ActionUtils.CLOSE);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(appVO, ActionUtils.CLOSE, false, errMsg);
		}
		return result;
	}

	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT)
			return false;
		Object selectedData = getModel().getSelectedData();
		if (selectedData == null)
			return false;
		AggMatterAppVO aggBean = (AggMatterAppVO) selectedData;

		Integer closeStatus = ((MatterAppVO) aggBean.getParentVO()).getClose_status();
		Integer effecStatus = ((MatterAppVO) aggBean.getParentVO()).getEffectstatus();

		if (effecStatus.equals(ErmMatterAppConst.EFFECTSTATUS_VALID)
				&& closeStatus.equals(ErmMatterAppConst.CLOSESTATUS_N)) {
			return true;
		}

		return false;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = getBtnName() + ErmActionConst.FAIL_MSG;
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(errorMsg);
		super.processExceptionHandler(ex);
		((DefaultExceptionHanler)getExceptionHandler()).setErrormsg(null);
	}

	public IErmMatterAppBillClose getAppBillService() {
		if (appBillCloseService == null) {
			appBillCloseService = NCLocator.getInstance().lookup(IErmMatterAppBillClose.class);
		}
		return appBillCloseService;
	}

	public MAppModel getModel() {
		return model;
	}

	public void setModel(MAppModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}
}
