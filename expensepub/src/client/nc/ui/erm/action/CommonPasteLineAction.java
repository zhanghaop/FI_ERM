package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.PasteLineAction;

/**
 * 粘贴行公共类，主要功能时粘贴时，将粘贴行中的字段清空（例如pk等）
 * 
 * @author chenshuaia
 */
public class CommonPasteLineAction extends PasteLineAction {
	private static final long serialVersionUID = 1L;

	/**
	 * 要置空的字段集合
	 */
	private List<String> nullPkNames;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getSelectedRow();
		if (rownum < 0) {// 无选择行则不进行
			return;
		}

		super.doAction(e);

		// 需特殊处理黏贴下来的行,将pk设为null,防止保存时,出现的唯一性约束错误
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();

		if (nullPkNames != null && nullPkNames.size() > 0) {
			for (int i = 0; i < pasteLineCont; i++) {
				for (String name : nullPkNames) {
					billCardPanel.setBodyValueAt(null, rownum + i, name);
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
