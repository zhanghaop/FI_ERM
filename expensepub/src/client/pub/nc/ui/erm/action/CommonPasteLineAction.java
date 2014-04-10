package nc.ui.erm.action;

import java.awt.event.ActionEvent;
import java.util.List;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.actions.PasteLineAction;

/**
 * ճ���й����࣬��Ҫ����ʱճ��ʱ����ճ�����е��ֶ���գ�����pk�ȣ�
 * 
 * @author chenshuaia
 */
public class CommonPasteLineAction extends PasteLineAction {
	private static final long serialVersionUID = 1L;

	/**
	 * Ҫ�ÿյ��ֶμ���
	 */
	private List<String> nullPkNames;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getSelectedRow();
		if (rownum < 0) {// ��ѡ�����򲻽���
			return;
		}

		super.doAction(e);

		// �����⴦�������������,��pk��Ϊnull,��ֹ����ʱ,���ֵ�Ψһ��Լ������
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
