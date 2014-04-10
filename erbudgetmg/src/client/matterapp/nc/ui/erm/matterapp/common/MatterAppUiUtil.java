package nc.ui.erm.matterapp.common;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.fi.pub.Currency;
import nc.pubitf.para.SysInitQuery;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.matterapp.listener.CardBodyAmountDigitListener;
import nc.ui.erm.matterapp.listener.ListBodyAmountDigitListener;
import nc.ui.erm.matterapp.listener.ListHeadAmountDigitListener;
import nc.ui.erm.matterapp.listener.ListRateDigitListener;
import nc.ui.erm.matterapp.model.MAppModel;
import nc.ui.fipub.crossrule.CrossCheckBeforeUtil;
import nc.ui.pub.bill.BillCardPanel;
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
		new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(),
				MatterAppVO.PK_CURRTYPE, AggMatterAppVO.getHeadYbAmounts());
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadOrgAmounts(), ListHeadAmountDigitListener.RATE_TYPE_LOCAL);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadGroupAmounts(), ListHeadAmountDigitListener.RATE_TYPE_GROUP);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadGlobalAmounts(), ListHeadAmountDigitListener.RATE_TYPE_GLOBAL);

		// 汇率精度
		new ListRateDigitListener(listPanel.getHeadBillModel(),
				new String[] { MatterAppVO.ORG_CURRINFO }, ListRateDigitListener.RATE_TYPE_LOCAL);
		new ListRateDigitListener(listPanel.getHeadBillModel(),
				new String[] { MatterAppVO.GROUP_CURRINFO }, ListRateDigitListener.RATE_TYPE_GROUP);
		new ListRateDigitListener(listPanel.getHeadBillModel(),
				new String[] { MatterAppVO.GLOBAL_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GLOBAL);
		new ListRateDigitListener(listPanel.getBodyBillModel(),
				new String[] { MatterAppVO.ORG_CURRINFO }, ListRateDigitListener.RATE_TYPE_LOCAL);
		new ListRateDigitListener(listPanel.getBodyBillModel(),
				new String[] { MatterAppVO.GROUP_CURRINFO }, ListRateDigitListener.RATE_TYPE_GROUP);
		new ListRateDigitListener(listPanel.getBodyBillModel(),
				new String[] { MatterAppVO.GLOBAL_CURRINFO },
				ListRateDigitListener.RATE_TYPE_GLOBAL);

		// 表体添加精度
		try {
			String[] tables = listPanel.getBillListData().getBodyTableCodes();
			if(tables != null && tables.length>0){
				for (int i = 0; i < tables.length; i++) {
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyYbAmounts(), ListBodyAmountDigitListener.RATE_TYPE_YB);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyOrgAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_LOCAL);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyGroupAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_GROUP);
					new ListBodyAmountDigitListener(listPanel.getBodyBillModel(tables[i]),
							AggMatterAppVO.getBodyGlobalAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_GLOBAL);
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
							AggMatterAppVO.getBodyYbAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_YB);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyOrgAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_LOCAL);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyGroupAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_GROUP);
					new CardBodyAmountDigitListener(cardPanel.getBillModel(tables[i]), cardPanel,
							AggMatterAppVO.getBodyGlobalAmounts(),
							ListBodyAmountDigitListener.RATE_TYPE_GLOBAL);
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
	public static void resetHeadDigit(BillCardPanel panel, String pk_org, String currency)
			throws Exception {
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
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency
						.getOrgLocalCurrPK(pk_org));
				grouphlPrecision = Currency.getGroupRateDigit(pk_org, BXUiUtil.getPK_group(), pk_currtype);
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
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));//全局本币

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
	 * @param cardPanel
	 */
	public static UFDouble setHeadAmountByBodyAmounts(BillCardPanel cardPanel) {
		UFDouble newYbje = null;
		BillModel billModel = cardPanel.getBillModel();
		MtAppDetailVO[] items = (MtAppDetailVO[]) billModel
				.getBodyValueVOs(MtAppDetailVO.class.getName());
		
		int length = items.length;
		
		for (int i = 0; i < length; i++) {
			if (items[i].getOrig_amount() != null) {// 当表体中存在空行时，原币金额为空，所以在这里判空
				if(newYbje == null){
					newYbje = items[i].getOrig_amount();
				}else{
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
	 * @param pk_org
	 * @param pk_currtype
	 * @return boolean[] 0：本币汇率 1：集团汇率 2：全局汇率
	 */
    public static boolean[] getCurrRateEnableStatus(String pk_org, String pk_currtype){
    	boolean[] result = new boolean[]{false, false, false};
    	
    	if(pk_org == null || pk_currtype == null){
    		return result;
    	}
    	
    	try {
			final String orgLocalCurrPK = Currency.getOrgLocalCurrPK(pk_org);
			final String groupCurrpk = Currency.getGroupCurrpk(BXUiUtil.getPK_group());
			final String globalCurrPk = Currency.getGlobalCurrPk(null);

			// 汇率能否编辑
			boolean flag = true;
			if (orgLocalCurrPK.equals(pk_currtype)) {
				flag = false;
			}
			
			result[0] = flag;
			
			// 集团汇率能否编辑
			final String groupMod = SysInitQuery.getParaString(BXUiUtil.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// 不启用，则不可编辑
				result[1] = false;
			} else {
				// 集团本币是否基于原币计算
				boolean isGroupByCurrtype = BXConstans.BaseOriginal.equals(groupMod);
				if (isGroupByCurrtype) {
					// 原币和集团本币相同
					if (groupCurrpk.equals(pk_currtype)) {
						result[1] = false;
					} else {
						result[1] = true;
					}
				} else {
					flag = true;
					if (orgLocalCurrPK.equals(groupCurrpk)) {
						flag = false;
					}
					result[1] = flag;
				}
			}

			// 全局汇率能否编辑
			final String globalMod = SysInitQuery.getParaString("GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// 不启用，则不可编辑
				result[2] = false;
			} else {
				// 全局本币是否基于原币计算
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal.equals(globalMod);
				if (isGlobalByCurrtype) {
					// 全局本币和原币相同
					if (globalCurrPk.equals(pk_currtype)) {
						result[2] = false;
					} else {
						result[2] = true;
					}
				} else {
					flag = true;
					if (orgLocalCurrPK.equals(globalCurrPk)) {
						flag = false;
					}
					
					result[2] = flag;
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return result;
    }
	
	public static void crossCheck(String itemKey, BillForm editor, String headOrBody) throws BusinessException{
		String currentBillTypeCode = ((MAppModel)editor.getModel()).getDjlxbm();
		CrossCheckBeforeUtil util = new CrossCheckBeforeUtil(editor.getBillCardPanel(), currentBillTypeCode);
		util.handler(itemKey, MatterAppVO.PK_ORG, headOrBody.equals("Y"));
//		ERMCrossCheckUtil.checkRule(headOrBody, orgKey, editor, currentBillTypeCode, ErmMatterAppConst.MatterApp_DJDL, new String[]{MatterAppVO.PK_ORG,MatterAppVO.PK_ORG,MatterAppVO.PK_ORG,MatterAppVO.PK_ORG});
	}
}
