package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * 列表表头金额精度监听
 * 
 * @author chendya
 * 
 */
public class ListHeadAmountDigitListener implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;

	public ListHeadAmountDigitListener(BillModel model, String[] targetKeys,
			String rateType) {
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		model.addDecimalListener(this);
	}

	public String getSource() {
		return MatterAppVO.PK_ORG;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {

		int digit = 2;
		String pk_org = (String)okValue;
		
		try {
			if (RATE_TYPE_LOCAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));
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
