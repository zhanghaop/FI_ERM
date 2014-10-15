package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.PasteLineToTailAction;

/**
 * ճ���е���β��������Ҫ���ÿ�ָ���ֶε�ֵ
 * @author chenshuaia
 *
 */
public class CommonPasteLineToTailAction extends PasteLineToTailAction {
	private static final long serialVersionUID = 7790054095805886384L;

	/**
	 * Ҫ�ÿյ��ֶμ���
	 */
	private List<String> nullPkNames;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getRowCount();
		
		super.doAction(e);
		
		//�����⴦�������������,��pk��Ϊnull,��ֹ����ʱ,���ֵ�Ψһ��Լ������
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
