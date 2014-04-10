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
	 * 给列表界面添加汇率、金额精度
	 * 
	 * @param listPanel
	 * @param model
	 */
	public static void addDigitListenerToListpanel(BillListPanel listPanel) {
		// 金额精度
		new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(), MatterAppVO.PK_CURRTYPE,
				AggMatterAppVO.getHeadYbAmounts());
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadOrgAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_LOCAL);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadGroupAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_GROUP);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO.getHeadGlobalAmounts(),
				ListHeadAmountDigitListener.RATE_TYPE_GLOBAL);

		// 汇率精度
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

		// 表体添加精度
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
	 * 给列表界面添加汇率、金额精度
	 * 
	 * @param cardPanel
	 * @param model
	 */
	public static void addDigitListenerToCardPanel(BillCardPanel cardPanel, AbstractAppModel model) {
		// 表体添加精度
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

					// 表体汇率
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
	 * 设置表头精度 发生时机： AddAction中， MatterAppMNBillForm显示卡片 showmeup中，切换币种中
	 * 
	 * @param panel
	 * @param pk_org
	 * @param currency
	 *            原币币种
	 * @throws Exception
	 */
	public static void resetHeadDigit(BillCardPanel panel, String pk_org, String currency) throws Exception {
		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// 原币
		String pk_currtype = currency;

		if (pk_currtype != null && pk_currtype.trim().length() > 0) {
			// 汇率精度
			int hlPrecision = 0;
			// 全局本币汇率精度
			int globalhlPrecision = 0;
			// 集团本币汇率精度
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

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// 原币精度
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// 组织本币精度
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(getPK_group()));// 集团本币精度
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));// 全局本币

			// 设置原币，本币，集团本币，全局本币金额字段字段精度
			resetCardDecimalDigit(panel, ybDecimalDigit, AggMatterAppVO.getHeadYbAmounts());
			resetCardDecimalDigit(panel, orgBbDecimalDigit, AggMatterAppVO.getHeadOrgAmounts());
			resetCardDecimalDigit(panel, groupByDecimalDigit, AggMatterAppVO.getHeadGroupAmounts());
			resetCardDecimalDigit(panel, globalByDecimalDigit, AggMatterAppVO.getHeadGlobalAmounts());
		}
	}

	private static void resetCardDecimalDigit(BillCardPanel panel, int digit, String[] keys) {
		// 表头精度
		if (keys != null && keys.length > 0) {
			for (String key : keys) {
				if (panel.getHeadItem(key) != null) {
					panel.getHeadItem(key).setDecimalDigits(digit);
				}
			}
		}

	}

	/**
	 * 根据表体金额计算表头总金额，并设置总金额
	 * 
	 * @param cardPanel
	 */
	public static UFDouble setHeadAmountByBodyAmounts(BillCardPanel cardPanel) {
		UFDouble newYbje = null;
		BillModel billModel = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);
		MtAppDetailVO[] items = (MtAppDetailVO[]) billModel.getBodyValueVOs(MtAppDetailVO.class.getName());

		int length = items.length;

		for (int i = 0; i < length; i++) {
			if (items[i].getOrig_amount() != null) {// 当表体中存在空行时，原币金额为空，所以在这里判空
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
	 * 获取当前集团
	 * 
	 * @return
	 */
	public static String getPK_group() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
	}

	/**
	 * 获取缓存中值
	 * 
	 * @param key
	 * @return
	 */
	public static Object getCacheValue(final String key) {
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * 获取业务时间
	 * 
	 * @return
	 */
	public static UFDate getBusiDate() {
		return WorkbenchEnvironment.getInstance().getBusiDate();
	}

	/**
	 * 获取服务端时间
	 * 
	 * @return
	 */
	public static UFDate getSysdate() {
		return WorkbenchEnvironment.getServerTime().getDate();
	}

	/**
	 * 返回当前登录的用户
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}

	/**
	 * 获取本币汇率可编辑状态
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @return boolean[] 0：本币汇率 1：集团汇率 2：全局汇率
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

			// 集团汇率能否编辑
			final String groupMod = SysInitQuery.getParaString(ErUiUtil.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// 不启用，则不可编辑
				return false;
			} else {
				// 集团本币是否基于原币计算
				boolean isGroupByCurrtype = BXConstans.BaseOriginal.equals(groupMod);
				if (isGroupByCurrtype) {
					// 原币和集团本币相同
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
			// 全局汇率能否编辑
			final String globalMod = SysInitQuery.getParaString("GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// 不启用，则不可编辑
				result = false;
			} else {
				// 全局本币是否基于原币计算
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal.equals(globalMod);
				if (isGlobalByCurrtype) {
					// 全局本币和原币相同
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
	 * 根据表体金额重算表体比例
	 * 
	 * @param cardPanel
	 */
	public static void setBodyShareRatio(BillCardPanel cardPanel) {
		UFDouble ori_amount = (UFDouble) cardPanel.getHeadItem(MatterAppVO.ORIG_AMOUNT).getValueObject();
		ori_amount = ori_amount == null ? UFDouble.ZERO_DBL : ori_amount;

		UFDouble uf100 = new UFDouble(100);
		UFDouble differ_ratio = new UFDouble(100).setScale(2, UFDouble.ROUND_HALF_UP);// 占比尾差

		int rows = cardPanel.getBillModel().getRowCount();
		if (rows == 0) {
			return;
		}

		int lastRow = 0;// 最后有效行
		for (int row = 0; row < rows; row++) {
			// 计算各行占比
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
			} else {// 合计值大于100时，差额为负数
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
	 * 最大报销金额补尾差
	 * 
	 * @param currentRow
	 */
	public static void fillLastRowAmount(BillCardPanel cardPanel) {
		BillModel billModel = cardPanel.getBillModel(ErmMatterAppConst.MatterApp_MDCODE_DETAIL);

		BillItem maxAmountHeadItem = cardPanel.getHeadItem(MatterAppVO.MAX_AMOUNT);
		if (maxAmountHeadItem == null) {//表头最大金额
			return;
		}

		UFDouble maxAmount = (UFDouble) maxAmountHeadItem.getValueObject();//最大金额不为0
		if (maxAmount == null || maxAmount.compareTo(UFDouble.ZERO_DBL) == 0) {
			return;
		}

		UFDouble sumMaxAmount = UFDouble.ZERO_DBL;
		int rowCount = billModel.getRowCount();
		for (int row = 0; row < rowCount; row++) {//计算表体最大总金额合计
			UFDouble rowMaxAmount = (UFDouble) billModel.getValueAt(row, MtAppDetailVO.MAX_AMOUNT);
			if (rowMaxAmount != null && rowMaxAmount.compareTo(UFDouble.ZERO_DBL) != 0) {
				sumMaxAmount = sumMaxAmount.add(rowMaxAmount);
			}
		}

		if (maxAmount.compareTo(sumMaxAmount) != 0) {//不一致时，进行补尾差
			UFDouble diffAmount = maxAmount.sub(sumMaxAmount);// 差额

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
