package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * 表体金额精度监听
 * 
 * @author chenshuaia
 * 
 */
public class ListBodyAmountDigitListener implements IBillModelDecimalListener2 {
	/**
	 * 原币
	 */
	public static final String RATE_TYPE_YB = "yb";
	
	/**
	 * 组织
	 */
	public static final String RATE_TYPE_LOCAL = "local";
	
	/**
	 * 集团
	 */
	public static final String RATE_TYPE_GROUP = "group";
	
	/**
	 * 全局
	 */
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;
	private BillModel billModel;

	public ListBodyAmountDigitListener(BillModel billModel, String[] targetKeys,
			String rateType) {
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		this.billModel = billModel;
		this.billModel.addDecimalListener(this);
	}
	
	/**
	 * 该值不能随便返回，如果返回的值对应的字段没有值，则不会设置该字段的精度
	 */
	public String getSource() {
		return MtAppDetailVO.ASSUME_ORG;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {
		int digit = 2;
		try {
			if (RATE_TYPE_YB.equals(rateType)) {
				String currtype = (String) billModel.getBodyRowValueByMetaData(row).get(MatterAppVO.PK_CURRTYPE);
				return Currency.getCurrDigit(currtype);
			} else if (RATE_TYPE_LOCAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getOrgLocalCurrPK(okValue == null ? null : okValue.toString()));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getGroupCurrpk(MatterAppUiUtil.getPK_group()));// 集团本币精度
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
