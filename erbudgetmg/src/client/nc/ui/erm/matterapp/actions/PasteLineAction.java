package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.erm.matterapp.view.MatterAppMNBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.erm.matterapp.MtAppDetailVO;

/**
 * 粘贴行
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
		if (rownum < 0) {//无选择行则不进行
			return;
		}
		
		super.doAction(e);
		
		//需特殊处理黏贴下来的行,将pk设为null,防止保存时,出现的唯一性约束错误
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			billCardPanel.setBodyValueAt(null, rownum + i, MtAppDetailVO.PK_MTAPP_DETAIL);
		}
		
		MatterAppUiUtil.setHeadAmountByBodyAmounts(getBillCardPanel());//表体金额相加结果放入表头
		((MatterAppMNBillForm)getCardpanel()).resetHeadAmounts();
		MatterAppUiUtil.setBodyShareRatio(getCardpanel().getBillCardPanel());
		MatterAppUiUtil.fillLastRowAmount(getCardpanel().getBillCardPanel());//最大金额补尾差
	}

	private BillCardPanel getBillCardPanel() {
		return getCardpanel().getBillCardPanel();
	}
}