package nc.ui.erm.costshare.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.costshare.common.ErmForCShareUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pub.lang.UFDouble;

@SuppressWarnings("serial")
public class InsertRowAction extends nc.ui.uif2.actions.InsertLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();

		boolean isNeedAvg = ErmForCShareUiUtil.isNeedBalanceJe(billCardPanel);
		int rownum = billCardPanel.getBodyPanel().getTable().getSelectedRow();

		super.doAction(e);
		if (isNeedAvg) {
			ErmForCShareUiUtil.reComputeAllJeByAvg(billCardPanel);
			// ����ʱ�Զ��������ı��ҽ��,����޸Ĺ�����Ľ��Ͳ�����ƽ����̯
			for (int i = 0; i < billCardPanel.getRowCount(); i++) {
				ErmForCShareUiUtil.setRateAndAmount(i, this.getCardpanel().getBillCardPanel());
			}
		} else {
			billCardPanel.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.ASSUME_AMOUNT);
			billCardPanel.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.SHARE_RATIO);
			ErmForCShareUiUtil.setRateAndAmount(rownum, billCardPanel);
		}

		ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(rownum, getCardpanel().getBillCardPanel());// ����Ĭ��ֵ

		billCardPanel.getBillModel().loadLoadRelationItemValue(rownum);
	}
}