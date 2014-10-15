package nc.ui.erm.erminitbill.action;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.erminit.IErminitCloseService;
import nc.ui.erm.erminitbill.view.ErmInitDialog;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIDialog;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.pub.BusinessException;

public class UnCloseAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;

	public UnCloseAction() {
		setCode(ErmActionConst.INITUNCLOSE);
		setBtnName(ErmActionConst.getInitUnCLose());
		putValue(SHORT_DESCRIPTION, ErmActionConst.getInitUnCLose());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		ErmInitDialog ermInitDialog = new ErmInitDialog(getModel());
		ermInitDialog.getRefOrg().setPK(ErUiUtil.getDefaultOrgUnit());//����Ĭ��ֵ
		if (ermInitDialog.showModal() == UIDialog.ID_OK) {
			String pk_org = ermInitDialog.getRefOrg().getRefPK();
		     if(pk_org==null)
                 throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0136")/*@res "��ѡ�����֯���ٲ���"*/);
			boolean flag=NCLocator.getInstance().lookup(IErminitCloseService.class).unclose(pk_org);
			if(flag==false){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0062")/*@res "�ڳ�δ�رգ�����ȡ���ر�"*/);
			}else{
				ShowStatusBarMsgUtil.showStatusBarMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0063")/*@res "ȡ���ڳ��رճɹ�"*/,getModel().getContext());
			}
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