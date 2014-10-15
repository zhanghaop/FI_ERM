package nc.ui.erm.costshare.common;

import nc.itf.fi.pub.Currency;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillModel;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.lang.UFDouble;
/**
 * ���ý�ת�����ȵ���������
 * @author wangled
 *
 */
public class CSDecimalUtil {
	public static void resetDecimal(BillCardPanel panel, String pk_org, String currency) throws Exception {
		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// ԭ��
		String pk_currtype = "";
		if (panel.getHeadItem(CostShareVO.BZBM) != null && panel.getHeadItem(CostShareVO.BZBM).getValueObject() != null
				&& !panel.getHeadItem(CostShareVO.BZBM).getValueObject().toString().equals("")) {
			pk_currtype = panel.getHeadItem(CostShareVO.BZBM).getValueObject().toString();
		} else {
			pk_currtype = currency;
		}
		if (pk_currtype != null && pk_currtype.trim().length() > 0) {
			// ���ʾ���
			int hlPrecision = 0;
			// ȫ�ֱ��һ��ʾ���
			int globalhlPrecision = 0;
			// ���ű��һ��ʾ���
			int grouphlPrecision = 0;
			try {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
				// ���Ż��ʾ���
				grouphlPrecision = Currency.getGroupRateDigit(pk_org, BXUiUtil.getPK_group(), pk_currtype);

				// ȫ�ֻ��ʾ���
				globalhlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
				
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			//���ñ�ͷ���ʾ���
			panel.getHeadItem(CostShareVO.BBHL).setDecimalDigits(hlPrecision);
			panel.getHeadItem(CostShareVO.GROUPBBHL).setDecimalDigits(grouphlPrecision);
			panel.getHeadItem(CostShareVO.GLOBALBBHL).setDecimalDigits(globalhlPrecision);
			
			//���ñ�����ʾ���
			resetCardDecimalDigit(panel, hlPrecision, null, new String[]{CShareDetailVO.BBHL});
			resetCardDecimalDigit(panel, grouphlPrecision, null, new String[]{CShareDetailVO.GROUPBBHL});
			resetCardDecimalDigit(panel, grouphlPrecision, null, new String[]{CShareDetailVO.GLOBALBBHL});
			
			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));//ȫ�ֱ��Ҿ���

			// ����ԭ�ҽ���
			resetCardDecimalDigit(panel, ybDecimalDigit, new String[] { CostShareVO.YBJE, CostShareVO.TOTAL }, null);
			// ������֯���ҽ���
			resetCardDecimalDigit(panel, orgBbDecimalDigit, new String[] {CostShareVO.BBJE}, new String[]{CShareDetailVO.BBJE});
			// ���輯�ű��Ҿ���
			resetCardDecimalDigit(panel, groupByDecimalDigit, new String[] {CostShareVO.GROUPBBJE}, new String[]{CShareDetailVO.GROUPBBJE});
			// ����ȫ�ֱ��Ҿ���
			resetCardDecimalDigit(panel, globalByDecimalDigit, new String[] {CostShareVO.GLOBALBBJE },  new String[]{CShareDetailVO.GLOBALBBJE});

			}
	}
	
	private static void resetCardDecimalDigit(BillCardPanel panel, int decimalDigits, String[] headJeKeys, String[] bodyJeKeys) {

		// ��ͷ����
		if (headJeKeys != null && headJeKeys.length > 0) {
			for (String key : headJeKeys) {
				if (panel.getHeadItem(key) != null) {
					panel.getHeadItem(key).setDecimalDigits(decimalDigits);
				}
			}
		}

		// ���徫��
		String[] tableCodes = panel.getBillData().getBodyTableCodes();
		if (tableCodes != null && tableCodes.length > 0) {
			for (String tableCode : tableCodes) {
				BillModel model = panel.getBillModel(tableCode);
				int rowCount = model.getRowCount();
				if (bodyJeKeys != null && bodyJeKeys.length > 0) {
					model.setNeedCalculate(false);
					for (String key : bodyJeKeys) {
						if (panel.getBodyItem(tableCode, key) != null) {
							panel.getBodyItem(tableCode, key).setDecimalDigits(decimalDigits);

							for (int i = 0; i < rowCount; i++) {
								Object valueAt = model.getValueAt(i, key);
								if (valueAt != null) {
									UFDouble value = new UFDouble(valueAt.toString());
									model.setValueAt(value, i, key);
								}
							}
						}
					}
					model.setNeedCalculate(true);
				}
				
			}
		}
	}
}
