package nc.ui.erm.accruedexpense.common;

import java.util.Vector;

import nc.bs.erm.accruedexpense.common.ErmAccruedBillConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.itf.resa.costcenter.ICostCenterQueryOpt;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.accruedexpense.listener.AccCardAmountDigitListener;
import nc.ui.erm.accruedexpense.listener.AccListBodyAmountDigitListner;
import nc.ui.erm.accruedexpense.listener.AccCardRateDecimalListener;
import nc.ui.erm.accruedexpense.listener.AccListBodyRateDecimalListener;
import nc.ui.erm.accruedexpense.listener.AccListHeadAmountDigitListner;
import nc.ui.erm.accruedexpense.listener.AccListHeadRateDecimalListener;
import nc.ui.erm.accruedexpense.model.AccManageAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.uif2.editor.BillForm;
import nc.vo.erm.accruedexpense.AccruedDetailVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.resa.costcenter.CostCenterVO;

@SuppressWarnings("restriction")
public class AccUiUtil {

	/**
	 * ���ñ�ͷ�����ʾ��� ����ʱ���� AddAction�У� BillForm��ʾ��Ƭ showmeup�У��л�������
	 * 
	 * @param panel
	 * @param pk_org
	 * @param currency
	 *            ԭ�ұ���
	 * @throws Exception
	 */
	public static void resetHeadDigit(BillCardPanel panel, String pk_org, String currency) throws Exception {
		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// ԭ��

		if (!StringUtil.isEmptyWithTrim(currency)) {
			// ���ʾ���
			int hlPrecision = 0;
			// ȫ�ֱ��һ��ʾ���
			int globalhlPrecision = 0;
			// ���ű��һ��ʾ���
			int grouphlPrecision = 0;
			try {
				hlPrecision = Currency.getRateDigit(pk_org, currency, Currency.getOrgLocalCurrPK(pk_org));
				grouphlPrecision = Currency.getGroupRateDigit(pk_org, ErUiUtil.getPK_group(), currency);
				globalhlPrecision = Currency.getGlobalRateDigit(pk_org, currency);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			// ���ñ�ͷ���ʾ���
			panel.getHeadItem(AccruedVO.ORG_CURRINFO).setDecimalDigits(hlPrecision);
			panel.getHeadItem(AccruedVO.GROUP_CURRINFO).setDecimalDigits(grouphlPrecision);
			panel.getHeadItem(AccruedVO.GLOBAL_CURRINFO).setDecimalDigits(globalhlPrecision);

			int ybDecimalDigit = Currency.getCurrDigit(currency);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(ErUiUtil.getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));// ȫ�ֱ���

			// ���ñ�ͷԭ�ң����ң����ű��ң�ȫ�ֱ��ҽ���ֶ��ֶξ���
			resetCardDecimalDigit(panel, ybDecimalDigit, AggAccruedBillVO.getHeadYbAmounts());
			resetCardDecimalDigit(panel, orgBbDecimalDigit, AggAccruedBillVO.getHeadOrgAmounts());
			resetCardDecimalDigit(panel, groupByDecimalDigit, AggAccruedBillVO.getHeadGroupAmounts());
			resetCardDecimalDigit(panel, globalByDecimalDigit, AggAccruedBillVO.getHeadGlobalAmounts());
		}
	}
	
	
	/**
	 * ���С�����ʱ���������л���
	 * @param rownum
	 */
	public static void resetBodyCurrInfo(BillCardPanel card,int rownum){
		Object orgObj =  card.getBodyValueAt(rownum, AccruedDetailVO.ASSUME_ORG);
		Object currtypeObj =  card.getHeadItem(AccruedVO.PK_CURRTYPE).getValueObject();// ����
		Object dateObj = card.getHeadItem(AccruedVO.BILLDATE).getValueObject();// ��������

		if (orgObj != null && currtypeObj != null && dateObj != null) {
			String assume_org = (String)orgObj;
			String pk_currtype = (String)currtypeObj;
			UFDate date = (UFDate)dateObj;
			try {
				// ����(���ң����ű��ң�ȫ�ֱ��һ���)
				UFDouble orgRate = Currency.getRate(assume_org, pk_currtype, date);
				UFDouble groupRate = Currency.getGroupRate(assume_org, ErUiUtil.getPK_group(), pk_currtype, date);
				UFDouble globalRate = Currency.getGlobalRate(assume_org, pk_currtype, date);
				
				card.setBodyValueAt(orgRate, rownum, AccruedVO.ORG_CURRINFO);
				card.setBodyValueAt(groupRate, rownum, AccruedVO.GROUP_CURRINFO);
				card.setBodyValueAt(globalRate, rownum, AccruedVO.GLOBAL_CURRINFO);
				
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}

	}
	

	/**
	 * �������ÿ�Ƭ������������ϸҳǩ����
	 * 
	 * @param panel
	 * @param pk_org
	 * @param currency
	 * @throws BusinessException
	 */
	public static void resetCardVerifyBodyAmountDigit(BillCardPanel panel, String pk_org, String currency)
			throws BusinessException {

		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// ԭ��
		String pk_currtype = currency;
		if (pk_currtype != null && pk_currtype.trim().length() > 0) {

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));

			// ���ñ��������ϸ����
			resetCardBodyDecimalDigit(panel, ybDecimalDigit, ErmAccruedBillConst.Accrued_MDCODE_VERIFY,
					new String[] { AccruedVerifyVO.VERIFY_AMOUNT });
			resetCardBodyDecimalDigit(panel, orgBbDecimalDigit, ErmAccruedBillConst.Accrued_MDCODE_VERIFY,
					new String[] { AccruedVerifyVO.ORG_VERIFY_AMOUNT });
			resetCardBodyDecimalDigit(panel, groupByDecimalDigit, ErmAccruedBillConst.Accrued_MDCODE_VERIFY,
					new String[] { AccruedVerifyVO.GROUP_VERIFY_AMOUNT });
			resetCardBodyDecimalDigit(panel, globalByDecimalDigit, ErmAccruedBillConst.Accrued_MDCODE_VERIFY,
					new String[] { AccruedVerifyVO.GLOBAL_VERIFY_AMOUNT });

		}

	}

	/**
	 * ���������б������������ϸҳǩ����
	 * 
	 * @param panel
	 * @param pk_org
	 * @param currency
	 * @throws BusinessException
	 */
	public static void resetListVerifyBodyAmountDigit(BillListPanel panel, String pk_org, String currency)
			throws BusinessException {
		if (panel == null || panel.getBodyBillModel(ErmAccruedBillConst.Accrued_MDCODE_VERIFY) == null) {
			return;
		}
		BillItem ybItem = panel.getBodyBillModel(ErmAccruedBillConst.Accrued_MDCODE_VERIFY).getItemByKey(
				AccruedVerifyVO.VERIFY_AMOUNT);
		BillItem orgItem = panel.getBodyBillModel(ErmAccruedBillConst.Accrued_MDCODE_VERIFY).getItemByKey(
				AccruedVerifyVO.ORG_VERIFY_AMOUNT);
		BillItem groupItem = panel.getBodyBillModel(ErmAccruedBillConst.Accrued_MDCODE_VERIFY).getItemByKey(
				AccruedVerifyVO.GROUP_VERIFY_AMOUNT);
		BillItem globalItem = panel.getBodyBillModel(ErmAccruedBillConst.Accrued_MDCODE_VERIFY).getItemByKey(
				AccruedVerifyVO.GLOBAL_VERIFY_AMOUNT);
		// ԭ��
		if (currency != null && currency.trim().length() > 0) {
			int ybDecimalDigit = Currency.getCurrDigit(currency);// ԭ�Ҿ���
			ybItem.setDecimalDigits(ybDecimalDigit);
			int orgByDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			orgItem.setDecimalDigits(orgByDecimalDigit);
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// ���ű��Ҿ���
			groupItem.setDecimalDigits(groupByDecimalDigit);
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
			globalItem.setDecimalDigits(globalByDecimalDigit);
		}
	}

	/**
	 * ���ÿ�Ƭ����б����о����ֶξ���
	 * 
	 * @author chenshuai
	 * @param panel
	 *            ��Ƭ
	 * @param decimalDigits
	 *            ����
	 * @param tableCode
	 *            ��ǩcode
	 * @param bodyJeKeys
	 *            ������key����
	 */
	private static void resetCardBodyDecimalDigit(BillCardPanel panel, int decimalDigits, String tableCode,
			String[] bodyJeKeys) {
		// ���徫��
		BillModel model = panel.getBillModel(tableCode);
		if (model == null)
			return;
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

	/**
	 * ���ݱ���������ͷ�ܽ��������ܽ��
	 * 
	 * @param cardPanel
	 */
	public static UFDouble setHeadAmountByBodyAmounts(BillCardPanel cardPanel) {
		UFDouble newYbje = null;
		BillModel billModel = cardPanel.getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL);
		AccruedDetailVO[] items = (AccruedDetailVO[]) billModel.getBodyValueVOs(AccruedDetailVO.class.getName());

		int length = items.length;

		for (int i = 0; i < length; i++) {
			if (items[i].getAmount() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
				if (newYbje == null) {
					newYbje = items[i].getAmount();
				} else {
					newYbje = newYbje.add(items[i].getAmount());
				}
			}
		}

		cardPanel.getHeadItem(AccruedVO.AMOUNT).setValue(newYbje);

		return newYbje;
	}

	private static void resetCardDecimalDigit(BillCardPanel panel, int digit, String[] keys) {
		// ��ͷ����
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				if (panel.getHeadItem(key) != null) {
					panel.getHeadItem(key).setDecimalDigits(digit);
				}
			}
		}

	}

	/**
	 * ���ݳе����Ŵ����ɱ�����
	 * 
	 * @param rowNum
	 * @param cardPanel
	 */
	@SuppressWarnings("unchecked")
	public static void setCostCenter(int rowNum, BillCardPanel cardPanel) {
		Object dept = cardPanel.getBillModel(ErmAccruedBillConst.Accrued_MDCODE_DETAIL).getValueObjectAt(rowNum,
				AccruedDetailVO.ASSUME_DEPT);

		// ����Ϊ��ʱ�������������ֵ���
		if (dept == null || ((DefaultConstEnum) dept).getValue().toString() == null) {
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CHECKELE + "_ID");
			return;
		}

		String pk_dept = ((DefaultConstEnum) dept).getValue().toString();
		String pk_pcorg = null;
		String pk_costcenter = null;
		CostCenterVO[] vos = null;
		try {
			vos = NCLocator.getInstance().lookup(ICostCenterQueryOpt.class).queryCostCenterVOByDept(
					new String[] { pk_dept });
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		if (vos != null) {
			for (CostCenterVO costCenterVO : vos) {
				pk_costcenter = costCenterVO.getPk_costcenter();
				pk_pcorg = costCenterVO.getPk_profitcenter();
				break;
			}
		}
		if (pk_pcorg == null) {
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CHECKELE + "_ID");
			return;
		}

		BillItem pcorgBodyItem = cardPanel.getBodyItem(ErmAccruedBillConst.Accrued_MDCODE_DETAIL,
				AccruedDetailVO.PK_PCORG);

		UIRefPane pcorgRefPane = (UIRefPane) pcorgBodyItem.getComponent();
		cardPanel.setBodyValueAt(pk_pcorg, rowNum, AccruedDetailVO.PK_PCORG + "_ID");

		AbstractRefModel model = (AbstractRefModel) pcorgRefPane.getRefModel();
		model.setMatchPkWithWherePart(true);
		Vector vec = model.matchPkData(pk_pcorg);
		if (vec == null || vec.isEmpty()) {
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_PCORG + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_RESACOSTCENTER + "_ID");
			cardPanel.setBodyValueAt(null, rowNum, AccruedDetailVO.PK_CHECKELE + "_ID");
			return;// ��������Ϊ�յ�����£��������óɱ�����ֵ
		}

		cardPanel.setBodyValueAt(pk_costcenter, rowNum, AccruedDetailVO.PK_RESACOSTCENTER + "_ID");
	}

	/**
	 * 
	 * 
	 * @param cardPanel
	 */
	public static void addDigitListenerToCardPanel(BillCardPanel cardPanel) {
		// ������Ӿ���
		try {
			try {
				String[] tables = cardPanel.getBillData().getBodyTableCodes();
				for (int i = 0; i < tables.length; i++) {
					new AccCardAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel, AggAccruedBillVO
							.getBodyYbAmounts(), AccCardAmountDigitListener.RATE_TYPE_YB, AccruedDetailVO.ASSUME_ORG);
					new AccCardAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel, AggAccruedBillVO
							.getBodyOrgAmounts(), AccCardAmountDigitListener.RATE_TYPE_LOCAL,
							AccruedDetailVO.ASSUME_ORG);
					new AccCardAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel, AggAccruedBillVO
							.getBodyGroupAmounts(), AccCardAmountDigitListener.RATE_TYPE_GROUP,
							AccruedDetailVO.ASSUME_ORG);
					new AccCardAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel, AggAccruedBillVO
							.getBodyGlobalAmounts(), AccCardAmountDigitListener.RATE_TYPE_GLOBAL,
							AccruedDetailVO.ASSUME_ORG);

					// �������
					new AccCardRateDecimalListener(cardPanel.getBillModel(tables[i]), cardPanel,
							new String[] { AccruedDetailVO.ORG_CURRINFO }, AccCardRateDecimalListener.RATE_TYPE_LOCAL,
							AccruedDetailVO.ASSUME_ORG);
					new AccCardRateDecimalListener(cardPanel.getBillModel(tables[i]), cardPanel,
							new String[] { AccruedDetailVO.GROUP_CURRINFO },
							AccCardRateDecimalListener.RATE_TYPE_GROUP, AccruedDetailVO.ASSUME_ORG);
					new AccCardRateDecimalListener(cardPanel.getBillModel(tables[i]), cardPanel,
							new String[] { AccruedDetailVO.GLOBAL_CURRINFO },
							AccCardRateDecimalListener.RATE_TYPE_GLOBAL, AccruedDetailVO.ASSUME_ORG);

				}
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
	}

	public static void addDigitListenerToListpanel(BillListPanel listPanel) {
		// ����
		new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(), AccruedVO.PK_CURRTYPE, AggAccruedBillVO
				.getHeadYbAmounts());
		new AccListHeadAmountDigitListner(listPanel.getHeadBillModel(), AggAccruedBillVO.getHeadOrgAmounts(),
				AccListHeadAmountDigitListner.RATE_TYPE_LOCAL, AccruedVO.PK_ORG);
		new AccListHeadAmountDigitListner(listPanel.getHeadBillModel(), AggAccruedBillVO.getHeadGroupAmounts(),
				AccListHeadAmountDigitListner.RATE_TYPE_GROUP, AccruedVO.PK_ORG);
		new AccListHeadAmountDigitListner(listPanel.getHeadBillModel(), AggAccruedBillVO.getHeadGlobalAmounts(),
				AccListHeadAmountDigitListner.RATE_TYPE_GLOBAL, AccruedVO.PK_ORG);

		// ���ʾ���
		new AccListHeadRateDecimalListener(listPanel.getHeadBillModel(), new String[] { AccruedVO.ORG_CURRINFO },
				AccListHeadRateDecimalListener.RATE_TYPE_LOCAL, AccruedVO.PK_ORG);
		new AccListHeadRateDecimalListener(listPanel.getHeadBillModel(), new String[] { AccruedVO.GROUP_CURRINFO },
				AccListHeadRateDecimalListener.RATE_TYPE_GROUP, AccruedVO.PK_ORG);
		new AccListHeadRateDecimalListener(listPanel.getHeadBillModel(), new String[] { AccruedVO.GLOBAL_CURRINFO },
				AccListHeadRateDecimalListener.RATE_TYPE_GLOBAL, AccruedVO.PK_ORG);
		new AccListBodyRateDecimalListener(listPanel.getBodyBillModel(), listPanel,
				new String[] { AccruedDetailVO.ORG_CURRINFO }, AccListBodyRateDecimalListener.RATE_TYPE_LOCAL,
				AccruedDetailVO.ASSUME_ORG);
		new AccListBodyRateDecimalListener(listPanel.getBodyBillModel(), listPanel,
				new String[] { AccruedDetailVO.GROUP_CURRINFO }, AccListBodyRateDecimalListener.RATE_TYPE_GROUP,
				AccruedDetailVO.ASSUME_ORG);
		new AccListBodyRateDecimalListener(listPanel.getBodyBillModel(), listPanel,
				new String[] { AccruedDetailVO.GLOBAL_CURRINFO }, AccListBodyRateDecimalListener.RATE_TYPE_GLOBAL,
				AccruedDetailVO.ASSUME_ORG);

		// ������Ӿ���
		try {
			String[] tables = listPanel.getBillListData().getBodyTableCodes();
			if (tables != null && tables.length > 0) {
				for (int i = 0; i < tables.length; i++) {
					new AccListBodyAmountDigitListner(listPanel.getBodyBillModel(tables[i]), listPanel, AggAccruedBillVO
							.getBodyYbAmounts(), AccListBodyAmountDigitListner.RATE_TYPE_YB, AccruedDetailVO.ASSUME_ORG);
					new AccListBodyAmountDigitListner(listPanel.getBodyBillModel(tables[i]), listPanel, AggAccruedBillVO
							.getBodyOrgAmounts(), AccListBodyAmountDigitListner.RATE_TYPE_LOCAL,
							AccruedDetailVO.ASSUME_ORG);
					new AccListBodyAmountDigitListner(listPanel.getBodyBillModel(tables[i]), listPanel, AggAccruedBillVO
							.getBodyGroupAmounts(), AccListBodyAmountDigitListner.RATE_TYPE_GROUP,
							AccruedDetailVO.ASSUME_ORG);
					new AccListBodyAmountDigitListner(listPanel.getBodyBillModel(tables[i]), listPanel, AggAccruedBillVO
							.getBodyGlobalAmounts(), AccListBodyAmountDigitListner.RATE_TYPE_GLOBAL,
							AccruedDetailVO.ASSUME_ORG);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	public static void crossCheck(String itemKey, BillForm editor, String headOrBody) throws BusinessException {
		String currentBillTypeCode = ((AccManageAppModel) editor.getModel()).getCurrentTradeTypeCode();
		CrossCheckBeforeUtil util = new CrossCheckBeforeUtil(editor.getBillCardPanel(), currentBillTypeCode);
		util.handler(itemKey, AccruedVO.PK_ORG, headOrBody.equals("Y"));
	}

}
