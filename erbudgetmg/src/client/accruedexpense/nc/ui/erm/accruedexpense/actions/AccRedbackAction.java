package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.lang.UFDouble;

public class AccRedbackAction extends NCAction {

	private static final long serialVersionUID = 1L;

	protected AbstractAppModel model;

	public AccRedbackAction() {
		this.setBtnName(ErmActionConst.getRedbackName());
		this.setCode(ErmActionConst.Redback);
	}

	@Override
	public void doAction(ActionEvent arg0) throws Exception {

		AggAccruedBillVO aggvo = (AggAccruedBillVO) getModel().getSelectedData();
		if (aggvo != null && aggvo.getParent() != null) {
			AggregatedValueObject billvo = NCLocator.getInstance().lookup(IErmAccruedBillManage.class).redbackVO(aggvo);
			getModel().directlyAdd(billvo);
			ShowStatusBarMsgUtil.showStatusBarMsg("操作成功", getModel().getContext());
		}
	}


	@Override
	protected boolean isActionEnable() {
		if (getModel().getUiState() != UIState.NOT_EDIT || getModel().getSelectedData() == null) {
			return false;
		}
		// 红冲按钮可用条件：已生效、余额不为零、红冲标志为否
		AggAccruedBillVO aggVo = (AggAccruedBillVO) getModel().getSelectedData();
		AccruedVO parentVo = aggVo.getParentVO();

		if (ErmAccruedBillConst.EFFECTSTATUS_VALID != parentVo.getEffectstatus()) {
			return false;
		}
		if (UFDouble.ZERO_DBL.compareTo(parentVo.getRest_amount()) == 0) {
			return false;
		}
		if (parentVo.getRedflag() != null
				&& (ErmAccruedBillConst.REDFLAG_REDED == parentVo.getRedflag() || ErmAccruedBillConst.REDFLAG_RED == parentVo
						.getRedflag())) {
			return false;
		}
		
		return true;
	}

	public AbstractAppModel getModel() {
		return model;
	}

	public void setModel(AbstractAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

}
