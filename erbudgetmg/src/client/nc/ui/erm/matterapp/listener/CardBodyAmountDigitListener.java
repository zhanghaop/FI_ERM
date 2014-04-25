package nc.ui.erm.matterapp.listener;

import nc.itf.fi.pub.Currency;
import nc.ui.erm.matterapp.common.MatterAppUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelDecimalListener2;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;

/**
 * ������ȼ���
 * 
 * @author chenshuaia
 * 
 */
public class CardBodyAmountDigitListener implements IBillModelDecimalListener2 {
	/**
	 * ԭ��
	 */
	public static final String RATE_TYPE_YB = "yb";
	
	/**
	 * ��֯
	 */
	public static final String RATE_TYPE_LOCAL = "local";
	
	/**
	 * ����
	 */
	public static final String RATE_TYPE_GROUP = "group";
	
	/**
	 * ȫ��
	 */
	public static final String RATE_TYPE_GLOBAL = "global";

	private String[] targetKeys;
	private String rateType;
	private BillCardPanel cardPanel;
	
	private BillModel model;

	public CardBodyAmountDigitListener(BillModel billmodel,BillCardPanel cardPanel, String[] targetKeys,
			String rateType) {
		this.targetKeys = targetKeys;
		this.rateType = rateType;
		this.cardPanel = cardPanel;
		this.model = billmodel;
		billmodel.addDecimalListener(this);
	}
	
	/**
	 * ��ֵ������㷵�أ�������ص�ֵ��Ӧ���ֶ�û��ֵ���򲻻����ø��ֶεľ���
	 */
	public String getSource() {
		return MtAppDetailVO.ASSUME_ORG;
	}

	@Override
	public int getDecimalFromSource(int row, Object okValue) {
		int digit = 8;
		if (model.isImporting()) {//����ʱ����Ҫ���㾫��
			return digit;
		}
		try {
			if (RATE_TYPE_YB.equals(rateType)) {
				return Currency.getCurrDigit(getHeadItemStrValue(MatterAppVO.PK_CURRTYPE));
			}else if (RATE_TYPE_LOCAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getOrgLocalCurrPK((String)okValue));
			} else if (RATE_TYPE_GROUP.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getGroupCurrpk(MatterAppUiUtil.getPK_group()));// ���ű��Ҿ���
			} else if (RATE_TYPE_GLOBAL.equals(rateType)) {
				return Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return digit;
	}
	
	/**
	 * ��ȡ��ͷָ���ֶ��ַ���Value
	 * 
	 * @param itemKey
	 * @return
	 */
	private String getHeadItemStrValue(String itemKey) {
		BillItem headItem = cardPanel.getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
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