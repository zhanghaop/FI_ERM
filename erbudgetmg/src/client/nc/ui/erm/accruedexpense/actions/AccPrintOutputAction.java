package nc.ui.erm.accruedexpense.actions;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.uif2.actions.OutputAction;

public class AccPrintOutputAction extends OutputAction {
	private static final long serialVersionUID = 1L;

	@Override
	public String getNodeKey() {
		String nodeCode = getModel().getContext().getNodeCode();
		if (ErmAccruedBillConst.ACC_NODECODE_MN.equals(nodeCode)
				|| ErmAccruedBillConst.ACC_NODECODE_QRY.equals(nodeCode)) {
			return ((AccManageAppModel) getModel()).getCurrentTradeTypeCode();
		} else {
			return null;
		}
	}
}
