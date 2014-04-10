package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fip.service.FipRelationInfoVO;

@SuppressWarnings("restriction")
public class LinkVoucherAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillManageModel model;
	private BillForm editor;

	public LinkVoucherAction() {
		super();
		setCode("LinkVoucher");
	    setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UPTcommon-000288")/*@res "联查凭证"*/);
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		JKBXVO selectedVO = (JKBXVO) getModel().getSelectedData();

		if (selectedVO == null || selectedVO.getParentVO().getPk_jkbx() == null)
			return;

		// 构造FipRelationInfoVO
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(selectedVO.getParentVO().getPk_group());
		srcinfovo.setPk_org(selectedVO.getParentVO().getPk_payorg());
		srcinfovo.setRelationID(selectedVO.getParentVO().getPk());
		srcinfovo.setPk_billtype(selectedVO.getParentVO().getDjlxbm());

		try {
			CShareDetailVO[] csharedetailvos = selectedVO.getcShareDetailVo();
			if (csharedetailvos != null && csharedetailvos.length > 0) {
				// 存在分摊明细情况下，也需要联查到对应费用结转单生成的凭证
				FipRelationInfoVO costinfovo = new FipRelationInfoVO();
				costinfovo.setPk_group(csharedetailvos[0].getPk_group());
				costinfovo.setPk_org(csharedetailvos[0].getPk_org());
				costinfovo.setRelationID(csharedetailvos[0].getPk_costshare());
				costinfovo.setPk_billtype(csharedetailvos[0].getPk_tradetype());

				FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), new FipRelationInfoVO[] { srcinfovo,
						costinfovo });

			} else {
				FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), srcinfovo);
				// --end
			}
		} catch (Exception ex) {
			ExceptionHandler.consume(ex);
		}

	}
	
	@Override
	protected boolean isActionEnable() {
		JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
		if (selectedData == null) {
			return false;
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

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}
	
	

}
