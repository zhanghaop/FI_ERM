package nc.ui.erm.accruedexpense.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

public class AccListHeadRateDecimalListener implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String source;
	private String[] target;
	private String rateType;
	private BillModel model;

	public AccListHeadRateDecimalListener(BillModel model, String[] target, String rateType,
			String source) {
		this.target = target;
		this.rateType = rateType;
		this.source = source;
		this.model = model;
		model.addDecimalListener(this);
	}

	public String[] getTarget() {
		return this.target;
	}

	public int getDecimalFromSource(int row, Object value) {
		int hlPrecision = 8;
		String pk_currtype = (String) model.getValueAt(row, AccruedVO.PK_CURRTYPE);
		String pk_org = (String) value;

		try {
			if (RATE_TYPE_LOCAL.equals(rateType)) {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				hlPrecision = Currency.getGroupRateDigit(pk_org, ErUiUtil.getPK_group(), pk_currtype);
			} else if (RATE_TYPE_GLOBAL.equals(rateType)) {
				// 全局汇率精度
				hlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return hlPrecision;

	}


	public String getSource() {
		return source;
	}

	public boolean isTarget(BillItem item) {
		boolean result = false;
		for (int i = 0; i < target.length; i++) {
			if (target[i].equals(item.getKey())) {
				result = true;
				break;
			}
		}
		return result;
	}
}
