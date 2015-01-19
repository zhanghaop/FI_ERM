package nc.ui.erm.accruedexpense.actions;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.actions.CopyLineAction;
import nc.vo.erm.accruedexpense.AccruedVO;

public class AccCopyLineAction extends CopyLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean isActionEnable() {
		BillItem redFlagItem = getCardpanel().getBillCardPanel().getHeadItem(AccruedVO.REDFLAG);
		if(redFlagItem != null && redFlagItem.getValueObject() != null && redFlagItem.getValueObject().equals(ErmAccruedBillConst.REDFLAG_RED)){
			return false;
		}
		return super.isActionEnable();
	}
}
