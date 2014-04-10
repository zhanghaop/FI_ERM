package nc.ui.erm.sharerule.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.erm.sharerule.ShareruleDataVO;

public class SharerulePasteLineToTailAction extends nc.ui.uif2.actions.PasteLineToTailAction {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getRowCount();
		super.doAction(e);
		//�����⴦�������������,��pk��Ϊnull,��ֹ����ʱ,���ֵ�Ψһ��Լ������
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			billCardPanel.setBodyValueAt(null, rownum + i, ShareruleDataVO.PK_CSHARE_DETAIL);
		}
	}

}
