package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.uif2.actions.DelLineAction;

public class AccDelLineAction extends DelLineAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		AccUiUtil.setHeadAmountByBodyAmounts(getCardpanel().getBillCardPanel());// 表体金额相加结果放入表头
		((AccMNBillForm) getCardpanel()).resetHeadAmounts();
	}

	
}
