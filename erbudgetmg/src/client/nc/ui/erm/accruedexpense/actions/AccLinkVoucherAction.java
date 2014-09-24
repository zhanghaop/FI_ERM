package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fip.service.FipRelationInfoVO;

@SuppressWarnings("restriction")
public class AccLinkVoucherAction extends NCAction {

	private static final long serialVersionUID = 1L;

	private BillForm editor;
	private BillManageModel model;

	public AccLinkVoucherAction() {
		super();
		setCode("LinkVoucher");
		setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPTcommon-000288")/*
																									 * @res
																									 * "联查凭证"
																									 */);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {
		AggAccruedBillVO selectedVO = (AggAccruedBillVO) getModel().getSelectedData();
		AccruedVO parentVO = selectedVO.getParentVO();

		// 利润中心+分期日期分组申请单，包装FipRelationInfoVO
		List<FipRelationInfoVO> querylist = new ArrayList<FipRelationInfoVO>();

		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(parentVO.getPk_group());
		srcinfovo.setPk_org(parentVO.getPk_org());
		srcinfovo.setRelationID(parentVO.getPrimaryKey());
		srcinfovo.setPk_billtype(parentVO.getPk_tradetype());

		querylist.add(srcinfovo);

		try {
			FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getEditor(), querylist.toArray(new FipRelationInfoVO[0]));
		} catch (Exception ex) {
			ExceptionHandler.handleException(ex);
		}

	}

	protected boolean isActionEnable() {
		return getModel().getSelectedData() != null && model.getUiState() == UIState.NOT_EDIT;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}

	public BillForm getEditor() {
		return editor;
	}

	public void setEditor(BillForm editor) {
		this.editor = editor;
	}

}
