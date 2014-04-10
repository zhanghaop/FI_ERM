package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.erm.matterapp.MtAppDetailVO;

/**
 * ճ����
 * 
 * @author chenshuaia
 */
public class PasteLineAction extends nc.ui.uif2.actions.PasteLineAction {
	private static final long serialVersionUID = 4947342009924551006L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getSelectedRow();
		if (rownum < 0) {//��ѡ�����򲻽���
			return;
		}
		
		super.doAction(e);
		
		//�����⴦�������������,��pk��Ϊnull,��ֹ����ʱ,���ֵ�Ψһ��Լ������
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			billCardPanel.setBodyValueAt(null, rownum + i, MtAppDetailVO.PK_MTAPP_DETAIL);
		}
		
		MatterAppUiUtil.setHeadAmountByBodyAmounts(getBillCardPanel());//��������ӽ�������ͷ
		((MatterAppMNBillForm)getCardpanel()).resetHeadAmounts();
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}
}