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
			// 插入时自动算出表体的本币金额,如果修改过表体的金额就不重新平均分摊
			for (int i = 0; i < billCardPanel.getRowCount(); i++) {
				//ErmForCShareUiUtil.setRateAndAmount(i, this.getCardpanel().getBillCardPanel());
				ErmForCShareUiUtil.setRateAndAmountNEW(i, this.getCardpanel().getBillCardPanel(),"INSERT_"+rownum);
			}
		} else {
			billCardPanel.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.ASSUME_AMOUNT);
			billCardPanel.setBodyValueAt(UFDouble.ZERO_DBL, rownum, CShareDetailVO.SHARE_RATIO);
			ErmForCShareUiUtil.setRateAndAmountNEW(rownum, billCardPanel,"INSERT_"+rownum);
		}

		ErmForCShareUiUtil.afterAddOrInsertRowCsharePage(rownum, getCardpanel().getBillCardPanel());// 带入默认值

		billCardPanel.getBillModel().loadLoadRelationItemValue(rownum);
	}
}