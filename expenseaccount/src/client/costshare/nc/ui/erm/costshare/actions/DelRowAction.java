package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.actions.DelLineAction;

/**
 * 
 * 单据删除行 Action　
 * 
 * @author luolch
 * 
 */
@SuppressWarnings("serial")
public class DelRowAction extends DelLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();

		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(billCardPanel);
		super.doAction(e);

		if (isNeedAvg) {
			ErmForCShareUiUtil.reComputeAllJeByAvg(billCardPanel);

			// 删除时自动算出表体的本币金额,如果修改过表体的金额就不重新平均分摊
			for (int i = 0; i < billCardPanel.getRowCount(); i++) {
				ErmForCShareUiUtil.setRateAndAmount(i, billCardPanel);
			}
		}
	}
}
