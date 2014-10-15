package nc.ui.erm.accruedexpense.view;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;

import nc.ui.erm.accruedexpense.common.AccUiUtil;
import nc.ui.erm.view.ERMBillListView;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillMouseEnent;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.BillTableMouseListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class AccMNListView extends ERMBillListView implements BillTableMouseListener {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void initUI() {
		super.initUI();

		billListPanel.addMouseListener(new BillTableMouseListener() {
			@Override
			public void mouse_doubleclick(BillMouseEnent e) {
				if (e.getPos() == BillItem.HEAD)
					onHeadMouseDBClick(e);
				else if (e.getPos() == BillItem.BODY)
					onBodyMouseDBClick(e);

			}
		});
		/**
		 * 单据编号增加超链接
		 */
		BillItem item = billListPanel.getHeadItem(AccruedVO.BILLNO);
		item.addBillItemHyperlinkListener(getLinklistener());

		AccUiUtil.addDigitListenerToListpanel(getBillListPanel());

		// 设置事由的显示
		resetSpecialItemCellRender();

	}
	
	@SuppressWarnings("serial")
	private void resetSpecialItemCellRender() {
		try {
			//pk_tradetype存交易类型编号，pk_tradetypeid存交易类型id，不再使用渲染
//			String name = getBillListPanel().getBillListData().getHeadItem(AccruedVO.PK_TRADETYPE).getName();
//			getBillListPanel().getHeadTable().getColumn(name).setCellRenderer(new BillTableCellRenderer() {
//				@SuppressWarnings("unchecked")
//				@Override
//				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//					List data = getModel().getData();
//					if(data != null){
//						if(row < data.size()){
//							AggAccruedBillVO aggvo = (AggAccruedBillVO) data.get(row);
//							if(aggvo != null){
//								setValue(ErUiUtil.getDjlxNameMultiLang((aggvo.getParentVO().getPk_tradetype())));
//							}
//						}
//					}
//					return this;
//				}
//			});
			
			String reasonName = getBillListPanel().getBillListData().getHeadItem(AccruedVO.REASON).getName();
			
			getBillListPanel().getHeadTable().getColumn(reasonName).setCellRenderer(new BillTableCellRenderer() {
				@SuppressWarnings("unchecked")
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					List data = getModel().getData();
					if(data != null){
						if(row < data.size()){
							AggAccruedBillVO aggvo = (AggAccruedBillVO) data.get(row);
							if(aggvo != null){
								setValue((String)aggvo.getParentVO().getReason());
							}
						}
					}
					return this;
				}
			});
		} catch (IllegalArgumentException e) {
			ExceptionHandler.consume(e);
		}
	}
	@Override
	public void handleEvent(AppEvent event) {
		super.handleEvent(event);
		if (AppEventConst.SELECTION_CHANGED == event.getType()) {
			// 重新设置核销明细页签的金额精度
			AggAccruedBillVO aggvo = (AggAccruedBillVO) getModel().getSelectedData();
			if (aggvo == null) {
				return;
			}
			String pk_org = aggvo.getParentVO().getPk_org();
			String currency = aggvo.getParentVO().getPk_currtype();
			try {
				AccUiUtil.resetListVerifyBodyAmountDigit(getBillListPanel(), pk_org, currency);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

}
