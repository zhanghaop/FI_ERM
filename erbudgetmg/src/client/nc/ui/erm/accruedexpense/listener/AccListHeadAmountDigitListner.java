package nc.ui.erm.accruedexpense.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class AccListHeadAmountDigitListner implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_YB = "yb";
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;
	private String source;
	private BillModel model;

	public AccListHeadAmountDigitListner(BillModel model, String[] targetKeys, String rateType,
			String source) {
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		this.source = source;
		this.model = model;
		model.addDecimalListener(this);
	}

	public String getSource() {
		return source;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {

		int digit = 2;
		
		String pk_currtype = (String) model.getValueAt(row, AccruedVO.PK_CURRTYPE);

		try {
			if (RATE_TYPE_YB.equals(rateType)) {
				return Currency.getCurrDigit(pk_currtype);
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
