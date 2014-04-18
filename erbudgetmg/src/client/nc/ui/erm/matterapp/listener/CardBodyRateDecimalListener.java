package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * 汇率精度监听卡片界面表体监听
 * 
 * @author wangled
 * 
 */
public class CardBodyRateDecimalListener implements IBillModelDecimalListener2 {
	public static final String RATE_TYPE_LOCAL = "local";
	public static final String RATE_TYPE_GROUP = "group";
	public static final String RATE_TYPE_GLOBAL = "global";

	private String orgField;
	private String[] target;
	private String rateType;
	private BillModel model;
	private BillCardPanel cardPanel;

	public CardBodyRateDecimalListener(BillModel model, String orgField, String[] target, String rateType) {
		this.model = model;
		this.orgField = orgField;
		this.target = target;
		this.rateType = rateType;
		this.model.addDecimalListener(this);
	}

	public CardBodyRateDecimalListener(BillModel model, String orgField, String[] target, String rateType,
			BillCardPanel cardPanel) {
		this.model = model;
		this.orgField = orgField;
		this.target = target;
		this.rateType = rateType;
		this.model.addDecimalListener(this);
		this.cardPanel = cardPanel;
	}

	public String[] getTarget() {
		return this.target;
	}

	public int getDecimalFromSource(int row, Object value) {
		int hlPrecision = 8;
		if (model.isImporting()) {// 导入时不需要计算精度
			return hlPrecision;
		}

		String pk_org = (String) this.cardPanel.getHeadItem(MatterAppVO.PK_ORG).getValueObject();
		String pk_currtype = (String) this.cardPanel.getHeadItem(MatterAppVO.PK_CURRTYPE).getValueObject();
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
		return orgField;
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
