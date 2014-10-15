package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.ui.pub.link.FipBillLinkQueryCenter;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fip.service.FipRelationInfoVO;

/**
 * @author luolch
 *
 *         联查凭证
 *
 */
@SuppressWarnings("restriction")
public class LinkVoucherAction extends NCAction {
	private static final long serialVersionUID = 1L;
	private BillManageModel model;

	public LinkVoucherAction() {
		super();
		setCode(ErmActionConst.LINKVOUCHER);
		setBtnName(ErmActionConst.getLinkVoucherName());
	}

	public void doAction(ActionEvent e) throws Exception {
		AggCostShareVO selectedData = (AggCostShareVO) getModel().getSelectedData();
		CostShareVO selectParentVO = (CostShareVO) selectedData.getParentVO();
		FipRelationInfoVO srcinfovo = new FipRelationInfoVO();
		srcinfovo.setPk_group(selectParentVO.getPk_group());
		srcinfovo.setPk_org(selectParentVO.getPk_org());
		srcinfovo.setRelationID(selectParentVO.getPrimaryKey());
		srcinfovo.setPk_billtype(selectParentVO.getPk_tradetype());
		FipBillLinkQueryCenter.queryDesBillBySrcInfoInDlg(getModel().getContext().getEntranceUI(),
				srcinfovo);
	}

	@Override
	protected boolean isActionEnable() {
		// 选择的结转单，是生效的结转单
		AggCostShareVO selectedData = (AggCostShareVO) getModel()
				.getSelectedData();
		return model.getUiState()== UIState.NOT_EDIT&&selectedData != null
				&& ((CostShareVO) selectedData.getParentVO()).getEffectstate()
						.intValue() == IErmCostShareConst.CostShare_Bill_Effectstate_Y;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		model.addAppEventListener(this);
	}

	public BillManageModel getModel() {
		return model;
	}

}