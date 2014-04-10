package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.erm.matterapp.MtAppDetailVO;

/**
 * ճ����β��
 * @author chenshuaia
 * 
 */
public class PasteLineToTailAction extends nc.ui.uif2.actions.PasteLineToTailAction {
	private static final long serialVersionUID = 4947342009924551006L;

	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		int rownum = bsp.getTable().getRowCount();
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