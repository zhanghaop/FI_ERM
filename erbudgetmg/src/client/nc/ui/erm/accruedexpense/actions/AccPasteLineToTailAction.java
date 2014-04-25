package nc.ui.erm.accruedexpense.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.accruedexpense.view.AccMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.PasteLineToTailAction;
import nc.vo.erm.accruedexpense.AccruedDetailVO;

public class AccPasteLineToTailAction extends PasteLineToTailAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getRowCount();
		super.doAction(e);
		// �����⴦�������������,��pk��Ϊnull,��ֹ����ʱ,���ֵ�Ψһ��Լ������
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			billCardPanel.setBodyValueAt(null, rownum + i, AccruedDetailVO.PK_ACCRUED_DETAIL);
		}

		AccUiUtil.setHeadAmountByBodyAmounts(billCardPanel);// ��������ӽ�������ͷ
		((AccMNBillForm) getCardpanel()).resetHeadAmounts();
	}

}