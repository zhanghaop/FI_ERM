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
import nc.ui.pub.beans.MessageDialog;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.exception.BugetAlarmBusinessException;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.uap.rbac.constant.INCSystemUserConst;

/**
 * ÷ÿ∆Ù
 * 
 * @author chenshuaia
 * 
 */
public class OpenBillAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private MAppModel model;

	private IErmMatterAppBillClose appBillCloseService;

	public OpenBillAction() {
		super();
		this.setBtnName(ErmActionConst.getOpenBillName());
		this.setCode(ErmActionConst.OPENBILL);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {

		Object[] objs = getModel().getSelectedOperaDatas();

		MessageVO[] msgs = new MessageVO[objs.length];
		List<AggMatterAppVO> openList = new ArrayList<AggMatterAppVO>();

		for (int i = 0; i < objs.length; i++) {
			AggMatterAppVO vo = (AggMatterAppVO) objs[i];
			msgs[i] = checkOpen(vo);
			if (msgs[i].isSuccess()) {
				openList.add(vo);
			}
		}
		if (!openList.isEmpty()) {
			MessageVO[] returnMsgs = openOneByOne(openList);
			List<AggregatedValueObject> openVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
			getModel().directlyUpdate(openVos.toArray(new AggregatedValueObject[] {}));
		}
		ErUiUtil.showBatchResults(getModel().getContext(), msgs);
	}

	private MessageVO checkOpen(AggMatterAppVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.OPEN);
		try {
			VOStatusChecker.checkOpenBillStatus(vo.getParentVO());
		} catch (DataValidateException e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}

	private MessageVO[] openOneByOne(List<AggMatterAppVO> auditVOs) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (AggMatterAppVO aggVo : auditVOs) {
			MessageVO msgReturn = openSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO openSingle(AggMatterAppVO appVO) throws Exception {
		MessageVO result = null;
		try {
			AggMatterAppVO[] vos = getAppBillService().openVOs(new AggMatterAppVO[] { appVO });
			result = new MessageVO(vos[0], ActionUtils.OPEN);
		} catch (BugetAlarmBusinessException e) {
			if (MessageDialog.showYesNoDlg(getModel().getContext().getEntranceUI(), nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
					.getStrByID("2011", "UPP2011-000049")/*
														 * @ res "Ã· æ"
														 */, e.getMessage()
					+ nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().
					getStrByID("upp2012v575_0","0upp2012V575-0125")/*@res "" «∑ÒºÃ–¯÷ÿ∆Ù£ø""*/) == MessageDialog.ID_YES) {
				appVO.getParentVO().setHasntbcheck(UFBoolean.TRUE); // ≤ªºÏ≤È
				result = openSingle(appVO);
			} else {
				result = new MessageVO(appVO, ActionUtils.OPEN, false, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
						.getStrByID("2011", "UPP2011-000405")/*
															 * @res "‘§À„…Í«Î ß∞‹"
															 */);
			}
		}catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(appVO, ActionUtils.OPEN, false, errMsg);
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

		MatterAppVO matterAppVO = (MatterAppVO) aggBean.getParentVO();
		Integer closeStatus = matterAppVO.getClose_status();
		Integer effecStatus = matterAppVO.getEffectstatus();

		if (effecStatus.equals(ErmMatterAppConst.EFFECTSTATUS_VALID)
				&& closeStatus.equals(ErmMatterAppConst.CLOSESTATUS_Y)
				&& !INCSystemUserConst.NC_USER_PK.equals(matterAppVO.getCloseman())) {
			return true;
		}

		return false;
	}
	
	@Override
	protected void processExceptionHandler(Exception ex) {
		String errorMsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString(
				"2011000_0", null, "02011000-0040", null,
				new String[] { this.getBtnName() })/*
													 * @ res "{0} ß∞‹£°"
													 */;
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
		this.model.addAppEventListener(this);
	}
}
