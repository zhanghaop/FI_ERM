package nc.ui.erm.erminitbill.action;

import java.awt.event.ActionEvent;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.erminit.IErminitCloseService;
import nc.ui.erm.erminitbill.view.ErmInitDialog;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.BusinessException;

public class CloseAction extends NCAction{

	private static final long serialVersionUID = 1L;
	private BillManageModel model;

	public CloseAction() {
		super();
		setCode("Cancel");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0049")/*@res "关闭"*/);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		UIDialog ermInitDialog = getErmInitDialog();
		((ErmInitDialog) ermInitDialog).getRefOrg().setPK(ErUiUtil.getDefaultOrgUnit());
//		((ErmInitDialog) ermInitDialog).getRefOrg().setRefValue();
		if (ermInitDialog.showModal() == UIDialog.ID_OK) { 
			String pk_org = ((ErmInitDialog) ermInitDialog).getRefOrg()
					.getRefPK();
			if(pk_org==null)
	             throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0136")/*@res "请选择个组织，再操作"*/);
			boolean flag = getExpAmortize().close(pk_org);
			if(flag == false){
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0050")/*@res "期初已经关闭，不用重复关闭"*/);
			}
			else{
				ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0051")/*@res "期初关闭成功"*/,getModel().getContext());
			}
		}
	}

	private UIDialog getErmInitDialog() {
		return new ErmInitDialog(getModel());
	}

	private IErminitCloseService getExpAmortize() {
		return NCLocator.getInstance().lookup(IErminitCloseService.class);
	}
	public BillManageModel getModel() {
		return model;
	}
	public void setModel(BillManageModel model) {
		this.model = model;
	}

}