package nc.ui.erm.expamortize.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.expamortize.ExpAmoritizeConst;
import nc.ui.erm.expamortize.model.ExpamorizeManageModel;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.lang.UFBoolean;

/**
 * 
 * @author wangled
 * 
 */
public class LinkVoucherAction extends NCAction {
	private static final long serialVersionUID = 169239625827544737L;
	
	private BillManageModel model;

	public LinkVoucherAction() {
		super();
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0073")/*
																										 * @
																										 * res
																										 * "联查凭证"
																										 */);
		setCode("LinkVoucher");
	}

	@SuppressWarnings("restriction")
	@Override
	public void doAction(ActionEvent e) throws Exception {
		ExpamtinfoVO expamtinfoVo = (ExpamtinfoVO) getModel().getSelectedData();
		// 摊销完成后联查
		if (expamtinfoVo.getAmt_status().equals(UFBoolean.TRUE)) {
			FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
			srcinfovo.setPk_group(expamtinfoVo.getPk_group());
			srcinfovo.setPk_org(expamtinfoVo.getPk_org());
			srcinfovo.setRelationID(expamtinfoVo.getPk_expamtinfo() + "_"  + ((ExpamorizeManageModel)getModel()).getPeriod());
			srcinfovo.setPk_billtype(ExpAmoritizeConst.Expamoritize_BILLTYPE);
			nc.ui.pub.link.FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getModel().getContext().getEntranceUI(),
					srcinfovo);
		}
	}
	
	protected boolean isActionEnable() {
		ExpamtinfoVO expamtinfoVo = (ExpamtinfoVO) getModel().getSelectedData();
		if (expamtinfoVo == null || (expamtinfoVo != null && expamtinfoVo.getAmt_status().equals(UFBoolean.FALSE))) {
			return false;
		} else {
			return true;
		}
	}

	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

}