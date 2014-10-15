package nc.ui.arap.bx.refbill;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import nc.bs.pf.pub.PfDataCache;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pubapp.billref.src.IRefPanelInit;
import nc.ui.pubapp.billref.src.RefContext;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.billtype.BilltypeVO;

@SuppressWarnings("restriction")
public class MatterRefPanelInit implements IRefPanelInit {

	private RefContext context;

	@Override
	public void refMasterPanelInit(BillListPanel masterPanel) {
		MatterAppUiUtil.addDigitListenerToListpanel(masterPanel);
//		resetTradeTypeName1(masterPanel);
//		masterPanel.getHeadBillModel().getTotalTableModel().setValueAt(null, 0, masterPanel.getHeadBillModel().getItemIndex(MatterAppVO.PK_TRADETYPE));
		
	}

	@Override
	public void refSinglePanelInit(BillListPanel singlePanel) {
		MatterAppUiUtil.addDigitListenerToListpanel(singlePanel);
//		resetTradeTypeName2(singlePanel);
	}

	/**
	 * 设置交易类型名称
	 * 
	 */
	private void resetTradeTypeName1(BillListPanel listpane) {
		final BillItem item = listpane.getBillListData().getHeadItem(MatterAppVO.PK_TRADETYPE);
		listpane.getHeadTable().getColumn(item.getName()).setCellRenderer(new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				
					AggMatterAppVO[] vos = (AggMatterAppVO[]) getContext().getRefBill().getRefBillModel().getAllBillVOs();
					if (vos != null && vos.length > row && vos[row] != null) {
						BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(vos[row].getParentVO().getPk_tradetype());
						if (billtypevo != null) {
							setValue(billtypevo.getBilltypenameOfCurrLang());
						}
					}
				return this;
			}
		});
	}
	private void resetTradeTypeName2(BillListPanel listpane) {
		final BillItem item = listpane.getBillListData().getHeadItem(MatterAppVO.PK_TRADETYPE);
		listpane.getHeadTable().getColumn(item.getName()).setCellRenderer(new DefaultTableCellRenderer() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				AggMatterAppVO[] vos = (AggMatterAppVO[]) getContext().getRefBill().getRefBillModel().getAllBillVOs();
				String tradetypename = null;
				MatterAppVO headvo = (MatterAppVO) getContext().getRefBill().getRefSingleListView().getRefSingleTableListPanel().getHeadBillModel().getBodyValueRowVO(row, "nc.vo.erm.matterapp.MatterAppVO");
				for (int i = 0; i < vos.length; i++) {
					if(vos[i].getParentVO().getPk_mtapp_bill().equals(headvo.getPk_mtapp_bill())){
						tradetypename = vos[i].getParentVO().getPk_tradetype();
					}
				}
				BilltypeVO billtypevo = PfDataCache.getBillTypeInfo(tradetypename);
				if (billtypevo != null) {
					setValue(billtypevo.getBilltypenameOfCurrLang());
				}
				return this;
			}
		});
	}

	public RefContext getContext() {
		return context;
	}

	public void setContext(RefContext context) {
		this.context = context;
	}

}
