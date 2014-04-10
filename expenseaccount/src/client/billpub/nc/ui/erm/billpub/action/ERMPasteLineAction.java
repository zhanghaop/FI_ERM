package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.PasteLineAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CShareDetailVO;

public class ERMPasteLineAction extends PasteLineAction {

	private static final long serialVersionUID = 1L;
	
    /**
     * ����������ĵ����ǲ�����ճ����
     */
	@Override
	protected boolean isActionEnable() { 
		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			String tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
		Object mtAppPk=null;
		if(headItem!=null){
			 mtAppPk = headItem.getValueObject();
		}
		
		return (getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT)&&mtAppPk==null ;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();

		int rownum = bsp.getTable().getSelectedRow();
		if (rownum < 0) {//��ѡ�����򲻽���
			return;
		}
		
		super.doAction(e);
		
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			if (billCardPanel.getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
				billCardPanel.setBodyValueAt(null, rownum + i, CShareDetailVO.PK_CSHARE_DETAIL);
			} else {
				billCardPanel.setBodyValueAt(null, rownum + i, BXBusItemVO.PK_BUSITEM);
				billCardPanel.setBodyValueAt(null, rownum + i, BXBusItemVO.ROWNO);
				billCardPanel.setBodyValueAt(null, pasteLineCont, BXBusItemVO.ROWNO);
				// ��������ֶ���Ϣ���ܱ�����
				for (String mtapp_filed : BXBusItemVO.MTAPP_FIELDS) {
					billCardPanel.setBodyValueAt(null, rownum + i, mtapp_filed);
				}
			}
		}
		((ErmBillBillForm)getCardpanel()).getbodyEventHandle().resetJeAfterModifyRow();
	}
}
