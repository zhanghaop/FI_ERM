package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.bs.erm.util.action.ErmActionConst;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.UIState;
import nc.ui.uif2.model.AbstractUIAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;

public class AccPrintOfficalCancelAction extends NCAction {

	private static final long serialVersionUID = 1L;

	protected AbstractUIAppModel model;

	public AccPrintOfficalCancelAction() {
		super();
		setCode(ErmActionConst.CANCELPRINT);
		setBtnName(ErmActionConst.getCancelprintName());
	}

	@Override
	public void doAction(ActionEvent e) throws Exception {
		AggAccruedBillVO aggVO = (AggAccruedBillVO) getModel().getSelectedData();

		if (aggVO != null) {
			aggVO.getParentVO().setPrinter(null);
			aggVO.getParentVO().setPrintdate(null);

			AccruedVO parent = NCLocator.getInstance().lookup(IErmAccruedBillManage.class)
					.updatePrintInfo(aggVO.getParentVO());

			aggVO.setParentVO(parent);
			((BillManageModel) getModel()).directlyUpdate(aggVO);
		}
	}

	public AbstractUIAppModel getModel() {
		return model;
	}

	public void setModel(AbstractUIAppModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		if (getModel().getSelectedData() == null) {
			return false;
		}

		if (model.getUiState() != UIState.NOT_EDIT) {
			return false;
		}

		AggAccruedBillVO aggVO = (AggAccruedBillVO) getModel().getSelectedData();

		if (aggVO.getParentVO().getPrinter() != null) {
			return true;
		}

		return false;
	}
}
