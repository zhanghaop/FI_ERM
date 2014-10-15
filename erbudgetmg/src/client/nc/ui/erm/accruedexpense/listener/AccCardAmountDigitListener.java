package nc.ui.erm.accruedexpense.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class AccCardAmountDigitListener implements IBillModelDecimalListener2 {

	public static final String RATE_TYPE_YB = "yb";

	public static final String RATE_TYPE_LOCAL = "local";

	public static final String RATE_TYPE_GROUP = "group";

	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;
	private String source;
	private BillCardPanel cardPanel;
	private BillModel model;

	public AccCardAmountDigitListener(BillModel billModel, BillCardPanel cardPanel, String[] targetKeys,
			String rateType, String source) {
		this.cardPanel = cardPanel;
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		this.source = source;
		model = billModel;
		billModel.addDecimalListener(this);
	}

	/**
	 * 该值不能随便返回，如果返回的值对应的字段没有值，则不会设置该字段的精度
	 */
	public String getSource() {
		return source;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {
		int digit = 8;
		try {
			if (model.isImporting()) {//导入时不需要计算精度
				return digit;
			}
			
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
		BillItem headItem = cardPanel.getHeadItem(AccruedVO.PK_CURRTYPE);
		String pk_currtype = headItem == null ? null : (String) headItem.getValueObject();
		return pk_currtype;
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
