package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * 汇率精度监听
 * 
 * @author chendya
 * 
 */
public class ListRateDigitListener implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private BillModel model;
	private String rateType;

	public ListRateDigitListener(BillModel model, String[] targetKeys,
			String rateType) {
		this.model = model;
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		model.addDecimalListener(this);
	}

	public String getSource() {
		return MatterAppVO.PK_ORG;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {

		int hlPrecision = 5;
		String pk_org = (String)okValue;//这里有一个，当model.getValueAt(row, MatterAppVO.PK_ORG); 字段在添加该监听的字段后时，无法通过model.getValue获取值
		
		String pk_currtype = (String)model.getValueAt(row, MatterAppVO.PK_CURRTYPE);
		
		try {
			if (RATE_TYPE_LOCAL.equals(rateType)) {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency
						.getOrgLocalCurrPK(pk_org));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				hlPrecision = Currency.getGroupRateDigit(pk_org, BXUiUtil.getPK_group(), pk_currtype);
			} else if (RATE_TYPE_GLOBAL.equals(rateType)) {
				// 全局汇率精度
				hlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return hlPrecision;
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
