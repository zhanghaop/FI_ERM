package nc.ui.erm.billcontrast.view;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCellEditor;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.IFunNodeClosingListener;
import nc.ui.uif2.editor.BatchBillTable;
import nc.vo.erm.billcontrast.BillcontrastVO;

@SuppressWarnings("serial")
public class BillContrastBatchBillTable extends BatchBillTable implements
		BillEditListener2 {
	
	private IFunNodeClosingListener closingListener = null;
	
	@Override
	public void initUI() {
		super.initUI();
		// 交易类型的过滤
		BillItem srctradTypeItem = getBillCardPanel().getBillModel()
				.getItemByKey(BillcontrastVO.SRC_TRADETYPEID);
		((UIRefPane) srctradTypeItem.getComponent())
				.setWhereString("  parentbilltype='264X' and pk_billtypecode <>'2647'");

		BillItem destradTypeItem = getBillCardPanel().getBillModel()
				.getItemByKey(BillcontrastVO.DES_TRADETYPEID);
		((UIRefPane) destradTypeItem.getComponent())
				.setWhereString("  parentbilltype='265X'");
		if("ORG_NODE".equalsIgnoreCase(getModel().getContext().getNodeType().toString())){
			this.getBillCardPanel().addBodyEditListener2(new BeforeEditListener());
		}
	}

	@Override
	protected void doAfterEdit(BillEditEvent e) {
		AbstractRefModel refModel = ((UIRefPane) ((BillCellEditor) e
				.getSource()).getComponent()).getRefModel();
		if (e.getKey().equals(BillcontrastVO.SRC_TRADETYPEID)) {
			getBillCardPanel().getBillModel().setValueAt(
					refModel.getRefCodeValue(), e.getRow(),
					BillcontrastVO.SRC_TRADETYPE);
		} else if (e.getKey().equals(BillcontrastVO.DES_TRADETYPEID)) {
			getBillCardPanel().getBillModel().setValueAt(
					refModel.getRefCodeValue(), e.getRow(),
					BillcontrastVO.DES_TRADETYPE);
		}
	}
	
	/**
	 * 组织节点集团数据是不可以编辑的
	 * @author wangled
	 *
	 */
	private final class BeforeEditListener implements BillEditListener2 {
		@Override
		public boolean beforeEdit(BillEditEvent e) {
			if (e.getKey().equals(BillcontrastVO.SRC_TRADETYPEID)
					|| e.getKey().equals(BillcontrastVO.DES_TRADETYPEID)) {
				int row = e.getRow();
				String pk_org = (String) getBillCardPanel().getBodyValueAt(
						row, BillcontrastVO.PK_ORG);
				
				String pk_group = (String) getBillCardPanel().getBodyValueAt(
						row, BillcontrastVO.PK_GROUP);
				if (pk_org.equals(pk_group)) {
					return false;
				}
			}
			return true;
			
		}
	}

	public IFunNodeClosingListener getClosingListener() {
		return closingListener;
	}

	public void setClosingListener(IFunNodeClosingListener closingListener) {
		this.closingListener = closingListener;
	}
	
	
}
