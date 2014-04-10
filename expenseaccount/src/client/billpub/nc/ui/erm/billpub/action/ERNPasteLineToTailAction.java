package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.UIState;
import nc.ui.uif2.actions.PasteLineToTailAction;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.CShareDetailVO;
/**
 * 
 * @author wangled
 *
 */
public class ERNPasteLineToTailAction extends PasteLineToTailAction{
	private static final long serialVersionUID = 1L;
	
    /**
     * 如果是拉单的单据是不可以粘贴行
     */
	@Override
	protected boolean isActionEnable() { 
		BillItem headItem = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM);
		Object mtAppPk=null;
		if(headItem!=null){
			 mtAppPk = headItem.getValueObject();
		}
		
		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			String tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		
		return (getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT)&&mtAppPk==null ;
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		//billCardPanel.showBodyTableCol(BXBusItemVO.PK_BUSITEM);
		int rownum = bsp.getTable().getRowCount();
		super.doAction(e);
		
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			if (billCardPanel.getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE)) {
				billCardPanel.setBodyValueAt(null, rownum + i, CShareDetailVO.PK_CSHARE_DETAIL);
			} else {
				billCardPanel.setBodyValueAt(null, rownum + i, BXBusItemVO.PK_BUSITEM);
				// 拉单相关字段信息不能被复制
				for (String mtapp_filed : BXBusItemVO.MTAPP_FIELDS) {
					billCardPanel.setBodyValueAt(null, rownum + i, mtapp_filed);
				}
			}
		}
		((ErmBillBillForm)getCardpanel()).getbodyEventHandle().resetJeAfterModifyRow();
	}
}
