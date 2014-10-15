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
		//需特殊处理黏贴下来的行,将pk设为null,防止保存时,出现的唯一性约束错误
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			billCardPanel.setBodyValueAt(null, rownum + i, ShareruleDataVO.PK_CSHARE_DETAIL);
		}
	}

}
