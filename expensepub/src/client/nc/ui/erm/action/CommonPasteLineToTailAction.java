package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.PasteLineToTailAction;

/**
 * 粘贴行到表尾，功能主要是置空指定字段的值
 * @author chenshuaia
 *
 */
public class CommonPasteLineToTailAction extends PasteLineToTailAction {
	private static final long serialVersionUID = 7790054095805886384L;

	/**
	 * 要置空的字段集合
	 */
	private List<String> nullPkNames;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getRowCount();
		
		super.doAction(e);
		
		//需特殊处理黏贴下来的行,将pk设为null,防止保存时,出现的唯一性约束错误
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		if (nullPkNames != null && nullPkNames.size() > 0) {
			for (int i = 0; i < pasteLineCont; i++) {
				for (String pkName : nullPkNames) {
					billCardPanel.setBodyValueAt(null, rownum + i, pkName);
				}
			}
		}
	}

	public List<String> getNullPkNames() {
		return nullPkNames;
	}

	public void setNullPkNames(List<String> nullPkNames) {
		this.nullPkNames = nullPkNames;
	}
}
