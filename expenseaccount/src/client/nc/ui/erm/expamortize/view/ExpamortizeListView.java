package nc.ui.erm.expamortize.view;

import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.uif2.AppEvent;
import nc.ui.uif2.editor.BillListView;
import nc.ui.uif2.model.AppEventConst;
import nc.vo.erm.expamortize.ExpamtinfoVO;

@SuppressWarnings("serial")
public class ExpamortizeListView extends BillListView {
	// 需要调整表头精度的字段
	private final String[] decimalFields = new String[] { ExpamtinfoVO.TOTAL_AMOUNT, ExpamtinfoVO.CURR_AMOUNT,
			ExpamtinfoVO.ACCU_AMOUNT, ExpamtinfoVO.RES_AMOUNT };

	private BillEditListener2 billListHeadBeforeEditlistener = null;

	public ExpamortizeListView() {
		super();
	}

	public void initUI() {
		super.initUI();
		this.getBillListPanel().setEnabled(true);
		addListener();
	}

	/**
	 * 为界面增加监听
	 */
	protected void addListener() {
		new DefaultCurrTypeBizDecimalListener(getBillListPanel().getHeadBillModel(), ExpamtinfoVO.BZBM, decimalFields);
		
		this.getBillListPanel().getParentListPanel().addEditListener2(getBillListHeadBeforeEditlistener());
	}

	@Override
	public void handleEvent(AppEvent event) {
		if (AppEventConst.MULTI_SELECTION_CHANGED == event.getType()) {
			Integer[] rows = getModel().getSelectedOperaRows();
			for (int i = 0; i < getModel().getRowCount(); i++)
				this.getBillListPanel().getHeadBillModel().setRowState(i, BillModel.UNSTATE);
			if (rows != null && rows.length > 0) {
				for (Integer row : rows)
					this.getBillListPanel().getHeadBillModel().setRowState(row.intValue(), BillModel.SELECTED);
			}
			this.getBillListPanel().getParent().repaint();
		} 
		
		super.handleEvent(event);

//		if (AppEventConst.DATA_UPDATED == event.getType()) {
//			synchronizeDataFromModel();
//		}
	}

	public BillEditListener2 getBillListHeadBeforeEditlistener() {
		return billListHeadBeforeEditlistener;
	}

	public void setBillListHeadBeforeEditlistener(BillEditListener2 billListHeadBeforeEditlistener) {
		this.billListHeadBeforeEditlistener = billListHeadBeforeEditlistener;
	}

	@Override
	public void afterEdit(BillEditEvent e) {
		super.afterEdit(e);
	}
}
