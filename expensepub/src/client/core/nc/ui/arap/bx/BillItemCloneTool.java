package nc.ui.arap.bx;

import nc.ui.pub.bill.BillItem;

public class BillItemCloneTool {
	
	public static BillItem clone(BillItem item) {
		BillItem cloned = new BillItem();
		cloned.setTableCode(item.getTableCode());
		cloned.setPos(item.getPos());
		cloned.setEnabled(true);
		cloned.setDataType(item.getDataType());
		cloned.setWidth(item.getWidth());
		cloned.setEdit(true);
		
		return cloned;
		
	}

}
