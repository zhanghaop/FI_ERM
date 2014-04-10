package nc.ui.erm.matterapp.actions;

import java.awt.event.ActionEvent;

import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.uif2.actions.AddLineAction;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.lang.UFDouble;

/**
 * 单据增行 Action　
 * @author chenshuaia
 */
@SuppressWarnings("serial")
public class AddRowAction extends AddLineAction {
	@Override
	public void doAction(ActionEvent e) throws Exception {
		int rownum = getBillCardPanel().getRowCount();

		super.doAction(e);
		setDefaultValue(rownum);//设置默认值
		getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
	}
	
	private void setDefaultValue(int rownum) {
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_GROUP),rownum, MtAppDetailVO.PK_GROUP);
		setBodyValue(getHeadItemStrValue(MatterAppVO.PK_ORG),rownum, MtAppDetailVO.PK_ORG);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORIG_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_EXE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.ORG_PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GROUP_PRE_AMOUNT);
		setBodyValue(UFDouble.ZERO_DBL,rownum, MtAppDetailVO.GLOBAL_PRE_AMOUNT);
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
