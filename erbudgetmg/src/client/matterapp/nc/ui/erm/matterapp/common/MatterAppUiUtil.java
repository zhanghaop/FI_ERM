package nc.ui.erm.matterapp.common;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.pubitf.para.SysInitQuery;
import nc.ui.erm.matterapp.listener.CardBodyAmountDigitListener;
import nc.ui.erm.matterapp.listener.CardBodyRateDecimalListener;
import nc.ui.erm.matterapp.listener.ListBodyAmountDigitListener;
import nc.ui.erm.matterapp.listener.ListHeadAmountDigitListener;
import nc.ui.erm.matterapp.listener.ListRateDigitListener;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.DefaultCurrTypeBizDecimalListener;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

@SuppressWarnings("restriction")
public class MatterAppUiUtil {

	/**
	 * ���б������ӻ��ʡ�����
	 * 
	 * @param listPanel
	 * @param model
	 */
	public static void addDigitListenerToListpanel(BillListPanel listPanel) {
		// ����
		new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(), MatterAppVO.PK_CURRTYPE,
				AggMatterAppVO.getHeadYbAmounts());
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadOrgAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_LOCAL);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadGroupAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_GROUP);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadGlobalAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_GLOBAL);

		// ���ʾ���
		new ListRateDigitListener(listPanel.getHeadBillModel(), new String[] { MatterAppVO.ORG_CURRINFO },
				ListRateDigitListener.RATE_TYPE_LOCAL);
		new ListRateDigitListener(listPanel.getHeadBillModel(), new String[] { MatterAppVO.GROUP_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GROUP);
		new ListRateDigitListener(listPanel.getHeadBillModel(), new String[] { MatterAppVO.GLOBAL_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GLOBAL);
		new ListRateDigitListener(listPanel.getBodyBillModel(), new String[] { MatterAppVO.ORG_CURRINFO },
				ListRateDigitListener.RATE_TYPE_LOCAL);
		new ListRateDigitListener(listPanel.getBodyBillModel(), new String[] { MatterAppVO.GROUP_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GROUP);
		new ListRateDigitListener(listPanel.getBodyBillModel(), new String[] { MatterAppVO.GLOBAL_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GLOBAL);

		// ������Ӿ���
		try {
			String[] tables = listPanel.getBillListData().getBodyTableCodes();
			if (tables != null && tables.length > 0) {
				for (int i = 0; i < tables.length; i++) {
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyYbAmounts(), ListBodyAmountDigitListener.RATE_TYPE_YB);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyOrgAmounts(), ListBodyAmountDigitListener.RATE_TYPE_LOCAL);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyGroupAmounts(), ListBodyAmountDigitListener.RATE_TYPE_GROUP);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyGlobalAmounts(), ListBodyAmountDigitListener.RATE_TYPE_GLOBAL);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}

	/**
	 * ���б������ӻ��ʡ�����
	 * 
	 * @param cardPanel
	 * @param model
	 */
	public static void addDigitListenerToCardPanel(BillCardPanel cardPanel, AbstractAppModel model) {
		// ������Ӿ���
		try {
			try {
				String[] tables = cardPanel.getBillData().getBodyTableCodes();

				for (int i = 0; i < tables.length; i++) {
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyYbAmounts(), ListBodyAmountDigitListener.RATE_TYPE_YB);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyOrgAmounts(), ListBodyAmountDigitListener.RATE_TYPE_LOCAL);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyGroupAmounts(), ListBodyAmountDigitListener.RATE_TYPE_GROUP);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyGlobalAmounts(), ListBodyAmountDigitListener.RATE_TYPE_GLOBAL);

					// �������
					new CardBodyRateDecimalListener(cardPanel.getBillModel(tables[i]), MtAppDetailVO.ASSUME_ORG,
							new String[] { MtAppDetailVO.ORG_CURRINFO }, CardBodyRateDecimalListener.RATE_TYPE_LOCAL);
					new CardBodyRateDecimalListener(cardPanel.getBillModel(tables[i]), MtAppDetailVO.ASSUME_ORG,
							new String[] { MtAppDetailVO.GROUP_CURRINFO }, CardBodyRateDecimalListener.RATE_TYPE_GROUP);
					new CardBodyRateDecimalListener(cardPanel.getBillModel(tables[i]), MtAppDetailVO.ASSUME_ORG,
							new String[] { MtAppDetailVO.GLOBAL_CURRINFO },
							CardBodyRateDecimalListener.RATE_TYPE_GLOBAL);

				}
			} catch (Exception e) {
			}
		} catch (Exception e) {
		}
	}

	/**
	 * ���ñ�ͷ���� ����ʱ���� AddAction�У� MatterAppMNBillForm��ʾ��Ƭ showmeup�У��л�������
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
		String pk_currtype = currency;

		if (pk_currtype != null && pk_currtype.trim().length() > 0) {
			// ���ʾ���
			int hlPrecision = 0;
			// ȫ�ֱ��һ��ʾ���
			int globalhlPrecision = 0;
			// ���ű��һ��ʾ���
			int grouphlPrecision = 0;
			try {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
				grouphlPrecision = Currency.getGroupRateDigit(pk_org, ErUiUtil.getPK_group(), pk_currtype);
				globalhlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			panel.getHeadItem(MatterAppVO.ORG_CURRINFO).setDecimalDigits(hlPrecision);
			panel.getHeadItem(MatterAppVO.GROUP_CURRINFO).setDecimalDigits(grouphlPrecision);
			panel.getHeadItem(MatterAppVO.GLOBAL_CURRINFO).setDecimalDigits(globalhlPrecision);

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));// ȫ�ֱ���

			// ����ԭ�ң����ң����ű��ң�ȫ�ֱ��ҽ���ֶ��ֶξ���
			resetCardDecimalDigit(panel, ybDecimalDigit, AggMatterAppVO.getHeadYbAmounts());
			resetCardDecimalDigit(panel, orgBbDecimalDigit, AggMatterAppVO.getHeadOrgAmounts());
			resetCardDecimalDigit(panel, groupByDecimalDigit, AggMatterAppVO.getHeadGroupAmounts());
			resetCardDecimalDigit(panel, globalByDecimalDigit, AggMatterAppVO.getHeadGlobalAmounts());
		}
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
	 * ���ݱ���������ͷ�ܽ��������ܽ��
	 * 
	 * @param cardPanel
	 */
	public static UFDouble setHeadAmountByBodyAmounts(BillCardPanel cardPanel) {
		UFDouble newYbje = null;
		BillModel billModel = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		MtAppDetailVO[] items = (MtAppDetailVO[]) billModel.getBodyValueVOs(MtAppDetailVO.class.getName());

		int length = items.length;

		for (int i = 0; i < length; i++) {
			if (items[i].getOrig_amount() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
				if (newYbje == null) {
					newYbje = items[i].getOrig_amount();
				} else {
					newYbje = newYbje.add(items[i].getOrig_amount());
				}
			}
		}

		cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).setValue(newYbje);

		return newYbje;
	}

	/**
	 * ��ȡ��ǰ����
	 * 
	 * @return
	 */
	public static String getPK_group() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
	}

	/**
	 * ��ȡ������ֵ
	 * 
	 * @param key
	 * @return
	 */
	public static Object getCacheValue(final String key) {
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * ��ȡҵ��ʱ��
	 * 
	 * @return
	 */
	public static UFDate getBusiDate() {
		return WorkbenchEnvironment.getInstance().getBusiDate();
	}

	/**
	 * ��ȡ�����ʱ��
	 * 
	 * @return
	 */
	public static UFDate getSysdate() {
		return WorkbenchEnvironment.getServerTime().getDate();
	}

	/**
	 * ���ص�ǰ��¼���û�
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}

	/**
	 * ��ȡ���һ��ʿɱ༭״̬
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @return boolean[] 0�����һ��� 1�����Ż��� 2��ȫ�ֻ���
	 */
	public static boolean[] getCurrRateEnableStatus(String pk_org, String pk_currtype) {
		boolean[] result = new boolean[] { false, false, false };

		if (pk_org == null || pk_currtype == null) {
			return result;
		}

		result[0] = getOrgRateEnableStatus(pk_org, pk_currtype);
		result[1] = getGroupRateEnableStatus(pk_org, pk_currtype);
		result[2] = getGlobalRateEnableStatus(pk_org, pk_currtype);

		return result;
	}

	public static boolean getGroupRateEnableStatus(String pk_org, String pk_currtype) {
		if (pk_org == null || pk_currtype == null) {
			return false;
		}

		try {
			final String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
			final String groupCurrpk = Currency.getGroupCurrpk(ErUiUtil.getPK_group());

			// ���Ż����ܷ�༭
			final String groupMod = SysInitQuery.getParaString(ErUiUtil.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// �����ã��򲻿ɱ༭
				return false;
			} else {
				// ���ű����Ƿ����ԭ�Ҽ���
				boolean isGroupByCurrtype = BXConstans.BaseOriginal.equals(groupMod);
				if (isGroupByCurrtype) {
					// ԭ�Һͼ��ű�����ͬ
					if (groupCurrpk.equals(pk_currtype)) {
						return false;
					} else {
						return true;
					}
				} else {
					if (orgLocalCurrPK.equals(groupCurrpk)) {
						return false;
					} else {
						return true;
					}
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return false;
	}

	public static boolean getOrgRateEnableStatus(String pk_org, String pk_currtype) {
		if (pk_org == null || pk_currtype == null) {
			return false;
		}

		try {
			String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);

			if (orgLocalCurrPK.equals(pk_currtype)) {
				return false;
			} else {
				return true;
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return false;
	}

	public static boolean getGlobalRateEnableStatus(String pk_org, String pk_currtype) {
		if (pk_org == null || pk_currtype == null) {
			return false;
		}

		try {
			boolean result = false;

			final String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
			final String globalCurrPk = Currency.getGlobalCurrPk(null);
			// ȫ�ֻ����ܷ�༭
			final String globalMod = SysInitQuery.getParaString("GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// �����ã��򲻿ɱ༭
				result = false;
			} else {
				// ȫ�ֱ����Ƿ����ԭ�Ҽ���
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal.equals(globalMod);
				if (isGlobalByCurrtype) {
					// ȫ�ֱ��Һ�ԭ����ͬ
					if (globalCurrPk.equals(pk_currtype)) {
						result = false;
					} else {
						result = true;
					}
				} else {
					if (orgLocalCurrPK.equals(globalCurrPk)) {
						result = false;
					} else {
						result = true;
					}
				}
			}

			return result;
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}

		return false;
	}

	public static void crossCheck(String itemKey, BillForm editor, String headOrBody) throws BusinessException {
		String currentBillTypeCode = ((MAppModel) editor.getModel()).getDjlxbm();
		CrossCheckBeforeUtil util = new CrossCheckBeforeUtil(editor.getBillCardPanel(), currentBillTypeCode);
		util.handler(itemKey, MatterAppVO.PK_ORG, headOrBody.equals("Y"));
	}

	/**
	 * ���ݱ���������������
	 * 
	 * @param cardPanel
	 */
	public static void setBodyShareRatio(BillCardPanel cardPanel) {
		UFDouble ori_amount = (UFDouble) cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		ori_amount = ori_amount == null ? UFDouble.ZERO_DBL : ori_amount;

		UFDouble uf100 = new UFDouble(100);
		UFDouble differ_ratio = new UFDouble(100).setScale(2, UFDouble.ROUND_HALF_UP);// ռ��β��

		int rows = cardPanel.getBillModel().getRowCount();
		if (rows == 0) {
			return;
		}

		int lastRow = 0;// �����Ч��
		for (int row = 0; row < rows; row++) {
			// �������ռ��
			UFDouble rowAmount = (UFDouble) cardPanel.getBillModel().getValueObjectAt(row, MtAppDetailVO.ORIG_AMOUNT);
			rowAmount = rowAmount == null ? UFDouble.ZERO_DBL : rowAmount;

			if (rowAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
//				cardPanel.getBillModel().setValueAt(UFDouble.ZERO_DBL, row, MtAppDetailVO.SHARE_RATIO);
				setBodyValue(cardPanel.getBillModel(), UFDouble.ZERO_DBL, row, MtAppDetailVO.SHARE_RATIO);
			} else {
				UFDouble shareRatio = (ori_amount.compareTo(UFDouble.ZERO_DBL) == 0 ? UFDouble.ZERO_DBL : rowAmount
						.div(ori_amount)).multiply(uf100);
				shareRatio = shareRatio.setScale(2, UFDouble.ROUND_HALF_UP);
				
				setBodyValue(cardPanel.getBillModel(), shareRatio, row, MtAppDetailVO.SHARE_RATIO);

				differ_ratio = differ_ratio.sub(shareRatio);
				lastRow = row;
			}
		}

		if (lastRow > 0 && differ_ratio.compareTo(UFDouble.ZERO_DBL) != 0) {
			if (differ_ratio.compareTo(UFDouble.ZERO_DBL) > 0) {
				UFDouble lastrow_ratio = (UFDouble) cardPanel.getBillModel().getValueObjectAt(lastRow,
						MtAppDetailVO.SHARE_RATIO);
				
				setBodyValue(cardPanel.getBillModel(), differ_ratio.add(lastrow_ratio), lastRow, MtAppDetailVO.SHARE_RATIO);
			} else {// �ϼ�ֵ����100ʱ�����Ϊ����
				for (int row = lastRow; row > 0; row--) {
					UFDouble oriAmount = (UFDouble) cardPanel.getBillModel().getValueObjectAt(row,
							MtAppDetailVO.ORIG_AMOUNT);
					if (oriAmount == null || oriAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
						continue;
					}
					
					UFDouble lastrow_ratio = (UFDouble) cardPanel.getBillModel().getValueObjectAt(row,
							MtAppDetailVO.SHARE_RATIO);
					if (lastrow_ratio.compareTo(differ_ratio.abs()) >= 0) {
						setBodyValue(cardPanel.getBillModel(), differ_ratio.add(lastrow_ratio), row,
								MtAppDetailVO.SHARE_RATIO);
						break;
					} else {
						setBodyValue(cardPanel.getBillModel(), UFDouble.ZERO_DBL, row, MtAppDetailVO.SHARE_RATIO);
						differ_ratio = lastrow_ratio.add(differ_ratio);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param model
	 * @param value
	 * @param row
	 * @param key
	 */
	private static void setBodyValue(BillModel model ,Object value, int row, String key ){
		if(model != null){
			if(model.getRowState(row) == BillModel.NORMAL){
				model.setRowState(row, BillModel.MODIFICATION);
			}
			model.setValueAt(value, row, key);
		}
	}

	/**
	 * �������β��
	 * 
	 * @param currentRow
	 */
	public static void fillLastRowAmount(BillCardPanel cardPanel) {
		BillModel billModel = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);

		BillItem maxAmountHeadItem = cardPanel.getHeadItem(MatterAppVO.MAX_AMOUNT);
		if (maxAmountHeadItem == null) {//��ͷ�����
			return;
		}

		UFDouble maxAmount = (UFDouble) maxAmountHeadItem.getValueObject();//����Ϊ0
		if (maxAmount == null || maxAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
			return;
		}

		UFDouble sumMaxAmount = UFDouble.ZERO_DBL;
		int rowCount = billModel.getRowCount();
		for (int row = 0; row < rowCount; row++) {//�����������ܽ��ϼ�
			UFDouble rowMaxAmount = (UFDouble) billModel.getValueAt(row, MtAppDetailVO.MAX_AMOUNT);
			if (rowMaxAmount != null && rowMaxAmount.compareTo(UFDouble.ZERO_DBL) != 0) {
				sumMaxAmount = sumMaxAmount.add(rowMaxAmount);
			}
		}

		if (maxAmount.compareTo(sumMaxAmount) != 0) {//��һ��ʱ�����в�β��
			UFDouble diffAmount = maxAmount.sub(sumMaxAmount);// ���

			for (int row = rowCount - 1; row >= 0; row--) {
				UFDouble rowMaxAmount = (UFDouble) billModel.getValueAt(row, MtAppDetailVO.MAX_AMOUNT);
				UFDouble rowAmount = (UFDouble) billModel.getValueAt(row, MtAppDetailVO.ORIG_AMOUNT);

				if (rowAmount != null && rowAmount.compareTo(UFDouble.ZERO_DBL) != 0) {
					if (diffAmount.compareTo(UFDouble.ZERO_DBL) > 0) {
						billModel.setValueAt(rowMaxAmount.add(diffAmount), row, MtAppDetailVO.MAX_AMOUNT);
						break;
					} else {
						UFDouble tempDiffAmount = rowMaxAmount.sub(rowAmount);
						if (diffAmount.abs().compareTo(tempDiffAmount) <= 0) {
							billModel.setValueAt(rowMaxAmount.add(diffAmount), row, MtAppDetailVO.MAX_AMOUNT);
							break;
						} else {
							billModel.setValueAt(rowAmount, row, MtAppDetailVO.MAX_AMOUNT);
							diffAmount = diffAmount.add(tempDiffAmount);
						}
					}
				}
			}
		}
	}
}
