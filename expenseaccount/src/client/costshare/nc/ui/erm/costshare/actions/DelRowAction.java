package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.uif2.actions.DelLineAction;

/**
 * 
 * ����ɾ���� Action��
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

			// ɾ��ʱ�Զ��������ı��ҽ��,����޸Ĺ�����Ľ��Ͳ�����ƽ����̯
			for (int i = 0; i < billCardPanel.getRowCount(); i++) {
				ErmForCShareUiUtil.setRateAndAmount(i, billCardPanel);
			}
		}
	}
}
