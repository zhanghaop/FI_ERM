package nc.ui.erm.accruedexpense.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class AccListBodyAmountDigitListner implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_YB = "yb";
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;
	private String source;
	private BillListPanel listPanel;

	public AccListBodyAmountDigitListner(BillModel model, BillListPanel listPanel, String[] targetKeys, String rateType,
			String source) {
		this.listPanel = listPanel;
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		this.source = source;
		model.addDecimalListener(this);
	}

	public String getSource() {
		return source;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {

		int digit = 2;

		try {
			if (RATE_TYPE_YB.equals(rateType)) {
				return Currency.getCurrDigit(getPk_currtype());
			} else if (RATE_TYPE_LOCAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getOrgLocalCurrPK((String) okValue));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getGroupCurrpk(ErUiUtil.getPK_group()));// 集团本币精度
			} else if (RATE_TYPE_GLOBAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return digit;
	}

	private String getPk_currtype() {
		int headRow = listPanel.getHeadTable().getSelectedRow();
		Object obj = listPanel.getHeadBillModel().getValueAt(headRow, AccruedVO.PK_CURRTYPE+IBillItem.ID_SUFFIX);
		if (obj != null) {
			return (String) obj;
		}
		return null;
	}

	public String[] getTarget() {
		return this.targetKeys;
	}

	public boolean isTarget(BillItem item) {
		for (int i = 0; i < targetKeys.length; i++) {
			if (targetKeys[i].equals(item.getKey())) {
				return true;
			}
		}
		return false;
	}
}
