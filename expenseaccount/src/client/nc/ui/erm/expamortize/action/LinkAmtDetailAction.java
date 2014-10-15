package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeprocQuery;
import nc.ui.erm.expamortize.view.ExpamtprocDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

/**
 *
 * @author wangled
 *
 */
@SuppressWarnings("serial")
public class LinkAmtDetailAction extends NCAction {
	private BillManageModel model;
	private ExpamtprocDialog dialog;

	public LinkAmtDetailAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0070")/*@res "联查摊销记录"*/);
		setCode("LinkAmtDetail");
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	private UIDialog getAmtPeriodDialog() {
		if (dialog == null) {
			dialog = new ExpamtprocDialog(getModel().getContext());
		}
		return dialog;
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] selectedDatas = getModel().getSelectedOperaDatas();
		if(selectedDatas!=null && selectedDatas.length!=1){
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl
					.getNCLangRes().getStrByID("201107_0", "0201107-0097")/** @res* "请选择一行数据进行联查"*/);
		}
		ExpamtinfoVO vo = (ExpamtinfoVO) getModel().getSelectedData();
		LoginContext context = getModel().getContext();
		if (vo != null) {
			ExpamtprocVO vos[] = getExpAmortizeprocService().linkProcByInfoVo(vo);
			if (vos.length != 0) {
				((ExpamtprocDialog) getAmtPeriodDialog()).getBillListPanel().setHeaderValueVO(vos);
				dialog.showModal();
			} else {
				ShowStatusBarMsgUtil.showErrorMsg(
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0071")/*
																										 * @
																										 * res
																										 * "联查失败"
																										 */,
						nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0072")/*
																										 * @
																										 * res
																										 * "没有联查摊销过程信息"
																										 */, context);
			}
		}
	}

	private IExpAmortizeprocQuery getExpAmortizeprocService() {
		return NCLocator.getInstance().lookup(IExpAmortizeprocQuery.class);
	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null&&model.getUiState()== UIState.NOT_EDIT;
	}

}