package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.actions.DelLineAction;

/**
 * 
 * ����ɾ���� Action��
 * 
 * @author chenshuaia
 * 
 */
@SuppressWarnings("serial")
public class DelRowAction extends DelLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		super.doAction(e);
		MatterAppUiUtil.setHeadAmountByBodyAmounts(getBillCardPanel());// ��������ӽ�������ͷ
		((MatterAppMNBillForm) getCardpanel()).resetHeadAmounts();
		 MatterAppUiUtil.setBodyShareRatio(getCardpanel().getBillCardPanel());
		 MatterAppUiUtil.fillLastRowAmount(getCardpanel().getBillCardPanel());
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}

}
