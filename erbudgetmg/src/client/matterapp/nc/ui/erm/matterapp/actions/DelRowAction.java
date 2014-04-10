package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.actions.DelLineAction;

/**
 * 
 * 单据删除行 Action　
 * 
 * @author chenshuaia
 * 
 */
@SuppressWarnings("serial")
public class DelRowAction extends DelLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		MatterAppUiUtil.setHeadAmountByBodyAmounts(getBillCardPanel());// 表体金额相加结果放入表头
		((MatterAppMNBillForm) getCardpanel()).resetHeadAmounts();
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}

}
