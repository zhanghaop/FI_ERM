package nc.ui.erm.billquery.action;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.pub.IBXBillPublic;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.progress.IProgressMonitor;
import nc.ui.uif2.NCAsynAction;
import nc.ui.uif2.components.progress.TPAProgressUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
/**
 * 借款报销单作废按钮
 *
 */
public class InvalidAction extends NCAsynAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;
	
	private TPAProgressUtil tpaProgressUtil;
	private IProgressMonitor monitor = null;
	
	public InvalidAction() {
		setCode("Invalid");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0175")/*@res "作废"*/);
	}
	
	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos==null || vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0","0201107-0176")/* @res "没有可作废的单据,操作失败"*/);
		}
		
		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		
		MessageVO[] msgs = new MessageVO[jkbxVos.length];
		
		MessageVO[] returnMsgs = invalidOneByOne(jkbxVos);
		
		List<AggregatedValueObject> successVos = ErUiUtil.combineMsgs(msgs, returnMsgs);
		
		//更新界面数据
		getModel().directlyUpdate(successVos.toArray(new AggregatedValueObject[] {}));
		
		//界面提示
		ErUiUtil.showBatchResults(getModel().getContext(), returnMsgs);
	}
	
	private MessageVO[] invalidOneByOne(JKBXVO[] vos) throws Exception {
		List<MessageVO> resultList = new ArrayList<MessageVO>();
		for (JKBXVO aggVo : vos) {
			MessageVO msgReturn = invalidSingle(aggVo);
			resultList.add(msgReturn);
		}
		return resultList.toArray(new MessageVO[] {});
	}

	private MessageVO invalidSingle(JKBXVO jkbxvo) throws Exception {
		
		MessageVO result = null;
		try {
			JKBXVO returnVo = NCLocator.getInstance().lookup(IBXBillPublic.class).invalidBill(jkbxvo);
			result = new MessageVO(returnVo, ActionUtils.INVALID);
		} catch (Exception e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(jkbxvo, ActionUtils.INVALID, false, errMsg);
		}
		return result;
	}

	/**
	 * 保存未生效的单据
	 */
	@Override
	protected boolean isActionEnable() {
		boolean inenable = false;
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		if (vos != null && vos.length != 0) {
			for (int i = 0; i < vos.length; i++) {
				if ((BXStatusConst.DJZT_Saved == ((JKBXVO) vos[i]).getParentVO().getDjzt().intValue())) {
					inenable = true;
					break;
				}
			}
		}
		return inenable;
	}
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}
	
	
	@Override
	public boolean beforeStartDoAction(ActionEvent actionEvent) throws Exception {
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
	
	@Override
	public void doAfterSuccess(ActionEvent actionEvent) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
	}

	public TPAProgressUtil getTpaProgressUtil() {
		if (this.tpaProgressUtil == null) {
			tpaProgressUtil = new TPAProgressUtil();
			tpaProgressUtil.setContext(getModel().getContext());
		}
		return tpaProgressUtil;
	}

	@Override
	public boolean doAfterFailure(ActionEvent actionEvent, Throwable ex) {
		if (monitor != null) {
			monitor.done();
			monitor = null;
		}
		return true;
	}
	
	public int showConfirmDisableDialog(Container parent){
		String TITLE = "确认作废";
		String QUESTION = "是否确认作废?";
		return MessageDialog.showYesNoDlg(parent, TITLE, QUESTION, UIDialog.ID_NO);
	}
}
