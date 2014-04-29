package nc.ui.erm.billpub.action;

import java.awt.event.ActionEvent;

import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.erm.billpub.view.eventhandler.BodyEventHandleUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
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
		String tradeType = null;
		if (getModel() instanceof ErmBillBillManageModel) {
			ErmBillBillManageModel model = (ErmBillBillManageModel) getModel();
			tradeType = model.getSelectBillTypeCode();
			if (BXConstans.BILLTYPECODE_RETURNBILL.equals(tradeType)) {
				return false;
			}
		}
		// 当前单据类型是否是报销单
		boolean isBX = tradeType != null? tradeType.startsWith(BXConstans.BX_PREFIX):false;
		return (getModel().getUiState()==UIState.ADD||getModel().getUiState()==UIState.EDIT)&& (mtAppPk==null || isBX);
	}
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		BillCardPanel billCardPanel = getCardpanel().getBillCardPanel();
		// 防止界面上，最后编辑的内容不生效
		billCardPanel.stopEditing();
		BillScrollPane bsp = billCardPanel.getBodyPanel();
		//billCardPanel.showBodyTableCol(BXBusItemVO.PK_BUSITEM);
		int rownum = bsp.getTable().getRowCount();
		super.doAction(e);
		
        //拉单行操作自动带出表头费用申请单pk、来源单据类型、来源类型
        Object pk_item = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ITEM).getValueObject();
        Object srcbilltype = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCBILLTYPE).getValueObject();
        Object srctype = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.SRCTYPE).getValueObject();
        
		int pasteLineCont = bsp.getTableModel().getPasteLineNumer();
		for (int i = 0; i < pasteLineCont; i++) {
			if (billCardPanel.getCurrentBodyTableCode().equals(BXConstans.CSHARE_PAGE)) {
				// 拉分摊的申请单后，分摊明细页签与申请单关联，否则拉不分摊的申请单则与申请单无关
				Boolean ismashare = getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE) == null ? false
						: (Boolean) getCardpanel().getBillCardPanel().getHeadItem(JKBXHeaderVO.ISMASHARE).getValueObject();
				if (ismashare) {
					getCardpanel().getBillCardPanel().setBodyValueAt(pk_item, rownum + i, BXBusItemVO.PK_ITEM);
				}
				billCardPanel.setBodyValueAt(null, rownum + i, CShareDetailVO.PK_CSHARE_DETAIL);
			} else {
				getCardpanel().getBillCardPanel().setBodyValueAt(pk_item, rownum + i, BXBusItemVO.PK_ITEM);
				getCardpanel().getBillCardPanel().setBodyValueAt(srcbilltype, rownum + i, BXBusItemVO.SRCBILLTYPE);
				getCardpanel().getBillCardPanel().setBodyValueAt(srctype, rownum + i, BXBusItemVO.SRCTYPE);
				
				billCardPanel.setBodyValueAt(null, rownum + i, BXBusItemVO.PK_BUSITEM);
			}
		}
		((ErmBillBillForm)getCardpanel()).getbodyEventHandle().resetJeAfterModifyRow();
		
		BillModel contrastBillModel = getCardpanel().getBillCardPanel().getBillModel(BXConstans.CONST_PAGE);
		if(contrastBillModel != null){
			int rows = contrastBillModel.getRowCount();
			if (rows > 0) {
				BXUiUtil.doContract((ErmBillBillForm) getCardpanel());
			}
		}
		
		new BodyEventHandleUtil((ErmBillBillForm) getCardpanel()).exeBodyUserdefine2();
	}
}
