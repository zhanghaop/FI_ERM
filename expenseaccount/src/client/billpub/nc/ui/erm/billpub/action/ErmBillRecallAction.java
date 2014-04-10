package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.bs.uif2.IActionCode;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.pf.PfUtilClient;
import nc.ui.uif2.DefaultExceptionHanler;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.ActionInitializer;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.trade.pub.IBillStatus;

/**
 * @author chenshuaia 收回
 * 
 *         查询活动
 * 
 */
public class ErmBillRecallAction extends NCAction {
	private static final long serialVersionUID = 1L;
	
	private BillManageModel model;

    private String billType = null;

    private String mdOperateCode = null;

    private String operateCode = null;

    private String resourceCode = null;

	public ErmBillRecallAction() {
		super();
		ActionInitializer.initializeAction(this, IActionCode.RECALL);
	}

	public void doAction(ActionEvent e) throws Exception {
		Object objs[] = getModel().getSelectedOperaDatas();

		if (objs == null || objs.length == 0) {
			return;
		}

		MessageVO[] msgs = new MessageVO[objs.length];
		List<JKBXVO> recallList = new ArrayList<JKBXVO>();

		for (int i = 0; i < objs.length; i++) {
			JKBXVO vo = (JKBXVO) objs[i];

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
	
	/**
	 * 校验
	 * @param vo
	 * @return
	 */
	private MessageVO checkRecall(JKBXVO vo) {
		MessageVO result = new MessageVO(vo, ActionUtils.RECALL);
		try {
			VOStatusChecker.checkRecallStatus(vo.getParentVO());
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}
	
	private MessageVO[] recallOneByOne(List<JKBXVO> auditVOs) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (JKBXVO aggVo : auditVOs) {
			MessageVO msgReturn = recallSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO recallSingle(JKBXVO appVO) throws Exception {
		MessageVO result = null;
		try {
			JKBXVO vo = (JKBXVO) PfUtilClient.runAction(getModel().getContext().getEntranceUI(),
					IPFActionName.UNSAVE, appVO.getParentVO().getDjlxbm(), appVO, null, null, null, null);
			result = new MessageVO(vo, ActionUtils.RECALL);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(appVO, ActionUtils.RECALL, false, errMsg);
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
			if (appStatus.equals(IBillStatus.COMMIT)) {
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

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getMdOperateCode() {
		return mdOperateCode;
	}

	public void setMdOperateCode(String mdOperateCode) {
		this.mdOperateCode = mdOperateCode;
	}

	public String getOperateCode() {
		return operateCode;
	}

	public void setOperateCode(String operateCode) {
		this.operateCode = operateCode;
	}

	public String getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(String resourceCode) {
		this.resourceCode = resourceCode;
	}
}