package nc.ui.erm.costshare.ui;

import java.awt.Component;
import java.util.List;

import javax.swing.JTable;

import nc.ui.erm.costshare.common.CsListView;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillTableCellRenderer;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;

@SuppressWarnings("serial")
public class CostShareView extends CsListView {
	@Override
	public void initUI() {
		super.initUI();

		BillItem item = getBillListPanel().getHeadItem(CostShareVO.BILLNO);
		item.addBillItemHyperlinkListener(getLinklistener());
		
		//添加进度监听
		addDecimalListener();
		
		//为特殊字段设置渲染器（事由等）
		resetSpecialItemCellRender();
	}

	private void addDecimalListener() {
		// 表体精度
		new DefaultCurrTypeBizDecimalListener(getBillListPanel().getBodyBillModel(), CShareDetailVO.BZBM,
				CShareDetailVO.ASSUME_AMOUNT);

		new DefaultCurrTypeBizDecimalListener(getBillListPanel().getBodyBillModel(), CShareDetailVO.BZBM,
				CShareDetailVO.SHARE_RATIO);
		// 表头精度
		new DefaultCurrTypeBizDecimalListener(getBillListPanel().getHeadBillModel(), CostShareVO.BZBM,
				CostShareVO.TOTAL);
	}

	private void resetSpecialItemCellRender() {
		try {
			String tradeName = getBillListPanel().getBillListData().getHeadItem(CostShareVO.PK_TRADETYPE).getName();
			getBillListPanel().getHeadTable().getColumn(tradeName).setCellRenderer(new BillTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if(data != null&&!data.isEmpty()){
						AggCostShareVO vo = (AggCostShareVO) data.get(row);
						if(vo != null){
							setValue(ErUiUtil.getDjlxNameMultiLang((String)vo.getParentVO().getAttributeValue(CostShareVO.PK_TRADETYPE)));
						}
					}
					return this;
				}
			});
			
			String reasonName = getBillListPanel().getBillListData().getHeadItem(CostShareVO.ZY).getName();
			getBillListPanel().getHeadTable().getColumn(reasonName).setCellRenderer(new BillTableCellRenderer() {
				private static final long serialVersionUID = -7709616533529134473L;

				@Override
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
						boolean hasFocus, int row, int column) {
					super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					@SuppressWarnings("rawtypes")
					List data = getModel().getData();
					if (data != null&&!data.isEmpty()) {
						AggCostShareVO vo = (AggCostShareVO) data.get(row);
						if (vo != null) {
							setValue(((CostShareVO) vo.getParentVO()).getZy());
						}
					}
					return this;
				}
			});
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		
	}
}
