package nc.ui.arap.bx;

import java.util.List;

import nc.bs.logging.Logger;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.vo.bd.balatype.BalaTypeVO;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.lang.UFBoolean;

/**
 * @author twei
 * 
 *         报销单据列表面板
 * 
 *         nc.ui.arap.bx.BXBillListPanel
 */
public class BXBillListPanel extends BillListPanel {

	private static final long serialVersionUID = -5616632058617552629L;

	public void setHeadValueVos(List<JKBXHeaderVO> bxvos) {

		getHeadBillModel().clearBodyData();
		getBodyBillModel().clearBodyData();

		try {
			setHeaderValueVO(bxvos.toArray(new JKBXHeaderVO[] {}));
		} catch (Throwable e) {
			ExceptionHandler.consume(e);
		}

		// 加载公式
		try {
			BillItem jsfsBillItem = getBillListData().getHeadItem(JKBXHeaderVO.JSFSMC);
			BXUiUtil.modifyLoadFormula(jsfsBillItem, BalaTypeVO.NAME);
			BillItem bzBillItem = getBillListData().getHeadItem(JKBXHeaderVO.BZMC);
			BXUiUtil.modifyLoadFormula(bzBillItem, CurrtypeVO.NAME);
			getHeadBillModel().execLoadFormula();
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	public void setBodyValueVos(List<JKBXHeaderVO> bxvos) {

		getBodyBillModel().clearBodyData();

		try {
			setBodyValueVO(bxvos.toArray(new JKBXHeaderVO[] {}));
			getBodyBillModel().loadLoadRelationItemValue();
			getBodyBillModel().execLoadFormula();

		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	public void selectedAll() {
		for (int row = 0; row < getHeadBillModel().getRowCount(); row++) {

			getHeadBillModel().setValueAt(UFBoolean.TRUE, row,
					JKBXHeaderVO.SELECTED);

			this.getParentListPanel().getEditListener()
					.afterEdit(
							new BillEditEvent(this, true,
									JKBXHeaderVO.SELECTED, row, 0));
		}
	}

	public void unSelectedAll() {
		for (int row = 0; row < getHeadBillModel().getRowCount(); row++) {

			getHeadBillModel().setValueAt(UFBoolean.FALSE, row,
					JKBXHeaderVO.SELECTED);

			this.getParentListPanel().getEditListener().afterEdit(
					new BillEditEvent(this, false, JKBXHeaderVO.SELECTED, row,
							0));
		}
	}

	public void unSelectedAll(int exceptRow) {
		for (int row = 0; row < getHeadBillModel().getRowCount(); row++) {
			if (row == exceptRow) {
				getHeadBillModel().setValueAt(UFBoolean.TRUE, row,
						JKBXHeaderVO.SELECTED);
			} else {
				getHeadBillModel().setValueAt(UFBoolean.FALSE, row,
						JKBXHeaderVO.SELECTED);
			}
		}
	}

	public Object getHeadColumnValue(String key, int row) {
		return getHeadBillModel().getValueAt(row, key);
	}

	public void setHeadColumnValue(String key, int row, Object value) {
		getHeadBillModel().setValueAt(value, row, key);
	}

	public void setBodyColumnValue(String key, Object value) {

		for (int i = 0; i < getBodyBillModel().getRowCount(); i++) {
			getBodyBillModel().setValueAt(value, i, key);
		}
	}

	/**
	 * 按照compKey,compValue找到对应的单据行 将key列设置为值value
	 * 
	 * @param key
	 * @param compKey
	 * @param compKey
	 * @param value
	 */
	public void setHeadColumnValue(String key, String compKey,
			Object compValue, Object value) {

		int row = 0;

		boolean equal = false;

		for (; row < getHeadBillModel().getRowCount(); row++) {
			if (getHeadBillModel().getValueAt(row, compKey).equals(compValue)) {
				equal = true;
				break;
			}
		}

		if (equal) {
			getHeadBillModel().setValueAt(value, row, key);
		}
	}

	public Object getHeadColumnValue(String key, String compKey,
			Object compValue) {

		int row = 0;

		boolean equal = false;

		for (; row < getHeadBillModel().getRowCount(); row++) {
			if (getHeadBillModel().getValueAt(row, compKey).equals(compValue)) {
				equal = true;
				break;
			}
		}

		if (equal) {
			return getHeadBillModel().getValueAt(row, key);
		}

		return null;
	}

}
