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
	 * ���б������ӻ��ʡ�����
	 * 
	 * @param listPanel
	 * @param model
	 */
	public static void addDigitListenerToListpanel(BillListPanel listPanel) {
		// ����
		new DefaultCurrTypeBizDecimalListener(listPanel.getHeadBillModel(),
				MatterAppVO.PK_CURRTYPE, AggMatterAppVO.getHeadYbAmounts());
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadOrgAmounts(), ListHeadAmountDigitListener.RATE_TYPE_LOCAL);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadGroupAmounts(), ListHeadAmountDigitListener.RATE_TYPE_GROUP);
		new ListHeadAmountDigitListener(listPanel.getHeadBillModel(), AggMatterAppVO
				.getHeadGlobalAmounts(), ListHeadAmountDigitListener.RATE_TYPE_GLOBAL);

		// ���ʾ���
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

		// ������Ӿ���
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
	 * ���ñ�ͷ���� ����ʱ���� AddAction�У� MatterAppMNBillForm��ʾ��Ƭ showmeup�У��л�������
	 * 
	 * @param panel
	 * @param pk_org
	 * @param currency
	 *            ԭ�ұ���
	 * @throws Exception
	 */
	public static void resetHeadDigit(BillCardPanel panel, String pk_org, String currency)
			throws Exception {
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

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));//ȫ�ֱ���

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
	 * @param cardPanel
	 */
	public static UFDouble setHeadAmountByBodyAmounts(BillCardPanel cardPanel) {
		UFDouble newYbje = null;
		BillModel billModel = cardPanel.getBillModel();
		MtAppDetailVO[] items = (MtAppDetailVO[]) billModel
				.getBodyValueVOs(MtAppDetailVO.class.getName());
		
		int length = items.length;
		
		for (int i = 0; i < length; i++) {
			if (items[i].getOrig_amount() != null) {// �������д��ڿ���ʱ��ԭ�ҽ��Ϊ�գ������������п�
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
	 * @param pk_org
	 * @param pk_currtype
	 * @return boolean[] 0�����һ��� 1�����Ż��� 2��ȫ�ֻ���
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

			// �����ܷ�༭
			boolean flag = true;
			if (orgLocalCurrPK.equals(pk_currtype)) {
				flag = false;
			}
			
			result[0] = flag;
			
			// ���Ż����ܷ�༭
			final String groupMod = SysInitQuery.getParaString(BXUiUtil.getPK_group(), "NC001");
			if (BXConstans.GROUP_DISABLE.equals(groupMod)) {
				// �����ã��򲻿ɱ༭
				result[1] = false;
			} else {
				// ���ű����Ƿ����ԭ�Ҽ���
				boolean isGroupByCurrtype = BXConstans.BaseOriginal.equals(groupMod);
				if (isGroupByCurrtype) {
					// ԭ�Һͼ��ű�����ͬ
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

			// ȫ�ֻ����ܷ�༭
			final String globalMod = SysInitQuery.getParaString("GLOBLE00000000000000", "NC002");
			if (BXConstans.GLOBAL_DISABLE.equals(globalMod)) {
				// �����ã��򲻿ɱ༭
				result[2] = false;
			} else {
				// ȫ�ֱ����Ƿ����ԭ�Ҽ���
				boolean isGlobalByCurrtype = BXConstans.BaseOriginal.equals(globalMod);
				if (isGlobalByCurrtype) {
					// ȫ�ֱ��Һ�ԭ����ͬ
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
