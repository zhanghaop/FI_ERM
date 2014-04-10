package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 插入行
 * @author chenshuaia
 *
 */
public class InsertLineAction extends nc.ui.uif2.actions.InsertLineAction {
	private static final long serialVersionUID = 4947342009924551006L;
	
	@Override
	public void doAction(ActionEvent e) throws Exception {
		int rownum = getBillCardPanel().getBodyPanel().getTable().getSelectedRow();

		super.doAction(e);
		setDefaultValue(rownum);//设置默认值
		getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
	}

	
	private void setDefaultValue(int selectRow) {
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_GROUP), selectRow, MtAppDetailVO.PK_GROUP);
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_ORG), selectRow, MtAppDetailVO.PK_ORG);

		setBodyValue(UFDouble.ZERO_DBL, selectRow, MtAppDetailVO.ORIG_AMOUNT);

		if (selectRow > 0) {
			Integer rorNum = (Integer) getBillCardPanel().getBodyValueAt(selectRow - 1, BXBusItemVO.ROWNO);
			if (rorNum != null) {
				getBillCardPanel().setBodyValueAt(rorNum + 1, selectRow, BXBusItemVO.ROWNO);
			}
		}
	}


	private void setBodyValue(Object value , int rownum, String key) {
		getBillCardPanel().setBodyValueAt(value, rownum, key);
	}
	
	private BillCardPanel getBillCardPanel(){
		return getCardpanel().getBillCardPanel();
	}
	
	/**
	 * 获取表头指定字段字符串Value
	 * 
	 * @param itemKey
	 * @return
	 */
	protected String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
}