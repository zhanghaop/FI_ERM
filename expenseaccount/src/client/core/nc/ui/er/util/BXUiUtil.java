package nc.ui.er.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.fi.pub.Currency;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.ui.arap.bx.listeners.BxYbjeDecimalListener;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.erm.billpub.remote.QcDateCall;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.erm.billpub.view.eventhandler.ERMCurrencyDecimalListener;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillListPanel;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.pub.BDCacheQueryUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.report.PubCommonReportMethod;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.enumfactory.UserIdentityTypeEnumFactory;

import org.apache.commons.lang.ArrayUtils;

public class BXUiUtil {

	/**
	 * ���ݵ������ͱ�����ȡ������������
	 * 
	 * @param djlxbm
	 * @return
	 */
	public static String getDjlxNameMultiLang(String djlxbm) {
		return ErUiUtil.getDjlxNameMultiLang(djlxbm);
//		String strByID = null;
//		if (djlxbm != null) {
//			String billtypecode = djlxbm;
//			// �Զ����Ҳ����
//			String resId = "D" + billtypecode;
//			strByID = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("billtype", resId);
//
//			if (resId.equals(strByID)) {// ��������ȡ������˵�����Զ���ģ���û���ö��ȡĬ�ϵ�name
//				strByID = PfDataCache.getBillTypeInfo(billtypecode).getBilltypenameOfCurrLang();
//			}
//		}
//		return strByID;
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static Map<String, String> getPermissionOrgMap(String nodeCode) {
		return ErUiUtil.getPermissionOrgMap(nodeCode);
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgs(String nodeCode) {
		return ErUiUtil.getPermissionOrgs(nodeCode);
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgVs(String nodeCode) {
		return ErUiUtil.getPermissionOrgVs(nodeCode);
	}

	/**
	 * �����û�����������Ա��Ϣ(array[0]=
	 * ��Ա������array[1]=��Ա��������,array[2]=��Ա������֯,array[3]=��Ա���ڼ���)
	 * 
	 * @param cuserid
	 *            �û�id
	 * @return
	 */
	public static String[] getPsnDocInfo(String cuserid) {
		String[] retValues = new String[4];
		// ��Ա����
		final String value = getPk_psndoc(cuserid);
		if (value == null || value.length() == 0) {
			return retValues;
		}
		return getPsnDocInfoById(value);
	}

	/**
	 * ������Ա��Ϣ(array[0]= ��Ա������array[1]=��Ա��������,array[2]=��Ա������֯,array[3]=��Ա���ڼ���)
	 * 
	 * @param pk_psndoc
	 *            ��Ա���
	 * @return
	 */
	public static String[] getPsnDocInfoById(String pk_psndoc) {
		String[] retValues = new String[4];
		// ��Ա
		retValues[0] = pk_psndoc;
		// ����
		retValues[1] = getPsnPk_dept(pk_psndoc);
		// ��֯
		retValues[2] = getPsnPk_org(pk_psndoc);
		// ����
		retValues[3] = getPsnPk_group(pk_psndoc);

		return retValues;
	}

	/**
	 * ��Ա���ڼ��ţ�����������֯
	 * 
	 * @param pk_psndoc
	 *            ��Ա����
	 * @return
	 */
	public static String getPsnPk_group(String pk_psndoc) {
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}
		
		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_psngroup = (String) instance.getClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + getPK_group());
		if (pk_psngroup == null) {
			try {
				PsndocVO[] persons = NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(new String[] { pk_psndoc },
						new String[] { PsndocVO.PK_ORG ,PsndocVO.PK_GROUP});
				// ��Ա������֯
				pk_psngroup = persons[0].getPk_group();
				instance.putClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + getPK_group(), pk_psngroup);
				instance.putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group(), persons[0].getPk_org());
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return pk_psngroup;
	}

	/**
	 * ��Ա������֯������������֯
	 * 
	 * @param pk_psndoc
	 *            ��Ա����
	 * @return
	 */
	public static String getPsnPk_org(String pk_psndoc) {
//		return ErUiUtil.getPsnPk_org(pk_psndoc);
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}
		
		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_org = (String) instance.getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group());
		if (pk_org == null) {
			try {
				PsndocVO[] persons = NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(new String[] { pk_psndoc },
						new String[] { PsndocVO.PK_ORG ,PsndocVO.PK_GROUP});
				// ��Ա������֯
				pk_org = persons[0].getPk_org();
				instance.putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group(), pk_org);
				instance.putClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + getPK_group(), persons[0].getPk_group());
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}
		return pk_org;
	
	}

	/**
	 * ������Ա���ڲ���
	 * 
	 * @author chendya
	 * @param pk_psndoc
	 *            ��Ա����
	 * @return
	 */
	public static String getPsnPk_dept(String pk_psndoc) {
		if (WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group()) == null) {
			final String pk_psndept = getColValue2("bd_psnjob", PsnjobVO.PK_DEPT, PsnjobVO.PK_PSNDOC, pk_psndoc, PsnjobVO.ISMAINJOB, "Y");
			WorkbenchEnvironment.getInstance().putClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group(), pk_psndept);
		}
		return (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group());
	}

	/**
	 * ��Ȩ����������
	 * 
	 * @author chendya
	 * @param billtype
	 * @param pk_user
	 * @param date
	 * @param pk_org
	 * @return
	 */
	public static String getAgentWhereString(String billtype, String pk_user, String date, String pk_org) {
		String pk_psndoc = "";
		String roleSql = null;
		String cacheKey = PsnVoCall.PSN_PK_ + pk_user + getPK_group();
		if (WorkbenchEnvironment.getInstance().getClientCache(cacheKey) != null) {
			pk_psndoc = (String) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		}
		if (WorkbenchEnvironment.getInstance().getClientCache(RoleVoCall.PK_ROLE_IN_SQL_BUSI + getPK_group()) != null) {
			roleSql = (String) WorkbenchEnvironment.getInstance().getClientCache(RoleVoCall.PK_ROLE_IN_SQL_BUSI + getPK_group());
		}
		return getAgentWhereStr(pk_psndoc, roleSql, billtype, pk_user, date, pk_org);

	}

	/**
	 * @param billtype
	 *            ������������
	 * @param user
	 *            ��½�û�
	 * @param date
	 *            ����
	 * @return ����һ��sql��䣬׷�ӵ������˵�ҵ��Ա���յ�wherepart�Ͻ��й���
	 */
	public static String getAgentWhereStr(String billtype, String user, String date, String pk_corp) {

		return getAgentWhereStr(null, null, billtype, user, date, pk_corp);
	}

	public static String getAgentWhereStr(String jkbxr, String rolersql, String billtype, String user, String date, String pk_org) {
		try {
			return BxUIControlUtil.getAgentWhereString(jkbxr, rolersql, billtype, user, date, pk_org);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return null;
	}

	/**
	 * ���ظ��Ի��������õ�ҵ��Ԫ,û�����ã�����null
	 * 
	 * @return
	 */
	public static String getDefaultOrgUnit() {
		return ErUiUtil.getDefaultOrgUnit();
//		try {
//			return getSettingValue("org_df_biz");
//		} catch (BusinessException e) {
//			ExceptionHandler.consume(e);
//		}
//		return null;
	}

	/**
	 * ���ص�ǰ�û��ڵ�ǰ���ŵ�Ĭ��ҵ��Ԫ 1.������Ի�����������ҵ��Ԫ�������ȷ��ظ��Ի��������õ�ҵ��Ԫ
	 * 2.������Ի�����δ���ã���ȡ��ǰ��¼�û�������֯��ΪĬ��ҵ��Ԫ
	 * 
	 * @author chendya
	 * 
	 * @throws Exception
	 */
	public static String getBXDefaultOrgUnit() {
		return ErUiUtil.getBXDefaultOrgUnit();
//		String pk_org = getDefaultOrgUnit();
//		if (pk_org == null || pk_org.length() == 0) {
//			// ���Ի�����ȡ��������ȡ��ǰ��½��������֯��ΪĬ��ҵ��Ԫ
//			pk_org = getPsnPk_org(getPk_psndoc());
//		}
//		return pk_org;
	}

	/**
	 * ����key���ظ��Ի��������õ�value
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
//	private static String getSettingValue(String key) throws BusinessException {
//		return IndividuationManager.getIndividualSetting("nc.individuation.defaultData.DefaultConfigPage", false).getString(key);
//	}

	public static String getPK_group() {
		return ErUiUtil.getPK_group();
//		return WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
	}

	/**
	 * ����ָ���û���ָ�����ŵ�Ĭ��ҵ��Ԫ����
	 * 
	 * @param pk_user
	 *            �û�����
	 * @param pk_group
	 *            ��������
	 */
	public static String getDefaultOrgUnit(String pk_user, String pk_group) throws Exception {
		return ErUiUtil.getDefaultOrgUnit(pk_user, pk_group);
//		return OrgSettingAccessor.getDefaultOrgUnit(pk_user, pk_group);
	}

	/**
	 * �ü����һ��ȫ�ֱ�����ֵ��
	 * 
	 * 
	 * @param key
	 *            ȫ�ֱ����ļ�
	 * @return ȫ�ֱ�����ֵ
	 */
	public static Object getValue(Object key) {
		return ErUiUtil.getValue(key);
//		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * ����ָ����Ӧ�ļ��ŵ�����
	 * 
	 * @return
	 */
	public static String getGroupName() {
		return ErUiUtil.getGroupName();

//		return WorkbenchEnvironment.getInstance().getGroupVO().getName();
	}

	/**
	 * ���ص�ǰ��¼���û�
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return ErUiUtil.getPk_user();
//		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}

	/**
	 * ����ָ���û�����Ӧ��ҵ��Ա
	 * 
	 * @return
	 */
	public static String getPk_psndoc(String cuserid) {
		final String pk_psn = getColValue2("sm_user", "pk_psndoc", "pk_base_doc", cuserid, "base_doc_type",
				UserIdentityTypeEnumFactory.TYPE_PERSON);
		if (pk_psn != null) {
			WorkbenchEnvironment.getInstance().putClientCache(PsnVoCall.PSN_PK_ + pk_psn + getPK_group(), pk_psn);
		}
		return pk_psn;
	}

	/**
	 * ���ص�ǰ��½�û�����Ӧ��ҵ��Ա
	 * 
	 * @return
	 */
	public static String getPk_psndoc() {
		return ErUiUtil.getPk_psndoc();
	}

	/**
	 * ������Ա���ڲ���
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	public static String getPk_dept(String pk_psndoc) {
		return (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group());
	}

	/**
	 * ���ص�ǰ������
	 * 
	 * @return ��ʱ���淽��ȡ����
	 * 
	 */
	public static String getAccountYear() {
		return ErUiUtil.getAccountYear();
	}

	/**
	 * ���ص�ǰ����
	 * 
	 * @return
	 * 
	 */
	public static UFDate getSysdate() {
		return ErUiUtil.getSysdate();
	}

	/**
	 * ����ҵ������
	 * 
	 * @return
	 * 
	 */
	public static UFDate getBusiDate() {
		return ErUiUtil.getBusiDate();
	}

	/**
	 * ���ص�ǰ����ʱ��
	 * 
	 * @return
	 * 
	 */
	public static UFDateTime getSysdatetime() {
		return ErUiUtil.getSysdatetime();
	}

	
	/**
	 * ui2����
	 * 
	 * @return
	 * 
	 */
	public static void addDecimalListenerToListpanel(BillListPanel listPanel) {
		// ��ӻ��ʾ��ȼ�����
		if (listPanel.getHeadItem(JKBXHeaderVO.BBHL) != null) {
			ERMCurrencyDecimalListener listener = new ERMCurrencyDecimalListener(listPanel);
			listPanel.getHeadBillModel().addDecimalListener(listener);

		}
		// ���ԭ�ҽ����־��ȼ�����
		String[] targets = null;
		ArrayList<String> values = new ArrayList<String>();
		String[] keys = JKBXHeaderVO.getYbjeField();
		for (String key : keys) {
			if (listPanel.getHeadItem(key) != null) {
				values.add(key);
			}
		}
		targets = new String[values.size()];
		values.toArray(targets);
		BxYbjeDecimalListener listener2 = new BxYbjeDecimalListener();
		listener2.setTarget(targets);
		listPanel.getHeadBillModel().addDecimalListener(listener2);
		// ���ñ��ҽ����־���
		String bbjeCurrency = "";
		int bbkeCurrencyPrecision = 0;// ���ұ��־���
		try {
			// bbjeCurrency =
			// Currency.getLocalCurrPK(ClientEnvironment.getInstance().getCorporation().getPk_corp());
			bbjeCurrency = Currency.getOrgLocalCurrPK(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group());
			bbkeCurrencyPrecision = Currency.getCurrDigit(bbjeCurrency);
		} catch (Exception e) {
		}
		String[] bbjeKeys = JKBXHeaderVO.getBbjeField();
		for (String key : bbjeKeys) {
			if (listPanel.getHeadItem(key) != null) {
				listPanel.getHeadItem(key).setDecimalDigits(bbkeCurrencyPrecision);
			}
		}
	}
	/**
	 * ����
	 * 
	 * @return
	 * 
	 */
//	public static void addDecimalListenerToListpanel(BillListPanel listPanel, VOCache cache) {
//		// ��ӻ��ʾ��ȼ�����
//		if (listPanel.getHeadItem(JKBXHeaderVO.BBHL) != null) {
//			BxCurrencyDecimalListener listener = new BxCurrencyDecimalListener(cache);
//			listPanel.getHeadBillModel().addDecimalListener(listener);
//
//		}
//		// ���ԭ�ҽ����־��ȼ�����
//		String[] targets = null;
//		ArrayList<String> values = new ArrayList<String>();
//		String[] keys = JKBXHeaderVO.getYbjeField();
//		for (String key : keys) {
//			if (listPanel.getHeadItem(key) != null) {
//				values.add(key);
//			}
//		}
//		targets = new String[values.size()];
//		values.toArray(targets);
//		BxYbjeDecimalListener listener2 = new BxYbjeDecimalListener();
//		listener2.setTarget(targets);
//		listPanel.getHeadBillModel().addDecimalListener(listener2);
//		// ���ñ��ҽ����־���
//		String bbjeCurrency = "";
//		int bbkeCurrencyPrecision = 0;// ���ұ��־���
//		try {
//			// bbjeCurrency =
//			// Currency.getLocalCurrPK(ClientEnvironment.getInstance().getCorporation().getPk_corp());
//			bbjeCurrency = Currency.getOrgLocalCurrPK(WorkbenchEnvironment.getInstance().getGroupVO().getPk_group());
//			bbkeCurrencyPrecision = Currency.getCurrDigit(bbjeCurrency);
//		} catch (Exception e) {
//		}
//		String[] bbjeKeys = JKBXHeaderVO.getBbjeField();
//		for (String key : bbjeKeys) {
//			if (listPanel.getHeadItem(key) != null) {
//				listPanel.getHeadItem(key).setDecimalDigits(bbkeCurrencyPrecision);
//			}
//		}
//	}

	/**
	 * ��Ƭ���棬���ݵ��ݵı��֣����豾�һ��ʵľ��ȣ�����������ֶεı��־��ȡ� ���������¶����б����ã�<br>
	 * 1.�������ݣ�AddAction.java��<br>
	 * 2.�޸ı����ֶΣ�BxCardHeadEditListener.java -> Line66��<br>
	 * 3.��Ƭ�����ʼ��ʱ������VO֮ǰ��BXBillCardPanel.setBillValueVO() -> Line271��<br>
	 * 
	 * @param panel
	 *            BillCardPanel
	 * @param currency
	 *            String �ڽ�������VO֮ǰ���ӽ���ȡ��������ֵ��Ҫ����VO�еı��ֱ������úþ���������VO��
	 *            ������ñ�����ʱ��������������VO���ӽ�����Եõ����ֵ�ֵ����ô�˲�������մ���null���ɡ�
	 * @return void
	 * @author zhangxiao1
	 * @param string
	 * @throws Exception
	 */
	public static void resetDecimal(BillCardPanel panel, String pk_org, String currency) throws Exception {
		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// ԭ��
		String pk_currtype = currency;
//		if (panel.getHeadItem(JKBXHeaderVO.BZBM) != null && panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null
//				&& !panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString().equals("")) {
//			pk_currtype = panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
//		} else {
//			pk_currtype = currency;
//		}
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
			panel.getHeadItem(JKBXHeaderVO.BBHL).setDecimalDigits(hlPrecision);
			panel.getHeadItem(JKBXHeaderVO.GLOBALBBHL).setDecimalDigits(globalhlPrecision);
			panel.getHeadItem(JKBXHeaderVO.GROUPBBHL).setDecimalDigits(grouphlPrecision);
			
			// ������֯���һ��ʾ���
			resetCardBodyDecimalDigit(panel, hlPrecision, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.BBHL});
			// ���輯�ű��һ��ʾ���
			resetCardBodyDecimalDigit(panel, grouphlPrecision, BXConstans.CSHARE_PAGE,  new String[]{CShareDetailVO.GLOBALBBHL});
			// ����ȫ�ֱ��һ��ʾ���
			resetCardBodyDecimalDigit(panel, globalhlPrecision, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.GLOBALBBHL});
		
			

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// ԭ�Ҿ���
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// ��֯���Ҿ���
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// ���ű��Ҿ���
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));

			// ���ñ���ҵ��ҳǩԭ�ҽ���
			resetCardDecimalDigit(panel, ybDecimalDigit, JKBXHeaderVO.getYbjeField(), BXBusItemVO.getYbjeField());
			// ���ñ���ҵ��ҳǩ��֯���ҽ���
			resetCardDecimalDigit(panel, orgBbDecimalDigit, JKBXHeaderVO.getOrgBbjeField(), BXBusItemVO.getBodyOrgBbjeField());
			// �������ҵ��ҳǩ���ű��Ҿ���
			resetCardDecimalDigit(panel, groupByDecimalDigit, JKBXHeaderVO.getHeadGroupBbjeField(), BXBusItemVO.getBodyGroupBbjeField());
			// �������ҵ��ҳǩȫ�ֱ��Ҿ���
			resetCardDecimalDigit(panel, globalByDecimalDigit, JKBXHeaderVO.getHeadGlobalBbjeField(), BXBusItemVO.getBodyGlobalBbjeField());

			String[] tableCodes = panel.getBillData().getBodyTableCodes();
			if (tableCodes != null && tableCodes.length > 0) {
				for (String tableCode : tableCodes) {
					// �����Զ������У���ֵ�����ֶεľ���
					resetBodyDefDecimalDigits(panel, tableCode, ybDecimalDigit);
				}
			}
			
			// ���ñ����̯ҳǩԭ�ҽ���
			resetCardBodyDecimalDigit(panel, ybDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.ASSUME_AMOUNT});
			// ���ñ����̯ҳǩ��֯���ҽ���
			resetCardBodyDecimalDigit(panel, orgBbDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.BBJE});
			// ��������̯ҳǩ���ű��Ҿ���
			resetCardBodyDecimalDigit(panel, groupByDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.GROUPBBJE});
			// ��������̯ҳǩȫ�ֱ��Ҿ���
			resetCardBodyDecimalDigit(panel, globalByDecimalDigit, BXConstans.CSHARE_PAGE,  new String[]{CShareDetailVO.GLOBALBBJE});

			
			
			// // ����ԭ�ҽ��ȣ�deleted at 2012-3-13��
			// resetYBDecimal(panel, ybDecimalDigit);
			//
			// // ������֯���ҽ��ȣ�deleted at 2012-3-13��
			// resetBBDecimal(panel, orgBbDecimalDigit);
			//			
			// //���輯�ű��Ҿ��ȣ�deleted at 2012-3-13��
			// resetGroupBbDecimalDigits(panel, groupByDecimalDigit);
			//			
			// //����ȫ�ֱ��Ҿ��ȣ�deleted at 2012-3-13��
			// resetGlobalBbDecimalDigits(panel, globalByDecimalDigit);
		}
	}

	/**
	 * ����ԭ�Ҿ��� ���ñ��ҽ��� ���ü��ű��Ҿ��� ����ȫ�ֱ��Ҿ���
	 */
	public static void resetDecimalDigits(BillItem item, JKBXVO zbvo) {
		// ����ԭ�Ҿ���
		if (item.getKey().equals(BXBusItemVO.AMOUNT) || (item.getIDColName() != null && item.getIDColName().equals(BXBusItemVO.AMOUNT))
				|| item.getKey().equals(BXBusItemVO.YBJE) || item.getKey().equals(BXBusItemVO.HKYBJE) || item.getKey().equals(BXBusItemVO.ZFYBJE)
				|| item.getKey().equals(BXBusItemVO.CJKYBJE)|| item.getKey().equals(CShareDetailVO.ASSUME_AMOUNT)||item.getKey().equals(BxcontrastVO.FYYBJE)) {
			String bzbm = zbvo.getParentVO().getBzbm();
			if (bzbm != null) {
				int precision = 2;
				try {
					precision = Currency.getCurrDigit(bzbm);
				} catch (Exception e1) {
					Log.getInstance("BXuiUtil").error(e1);
				}
				item.setDecimalDigits(precision);
			}
		}

		// ������֯���Ҿ���
		else if (item.getKey().equals(BXBusItemVO.BBJE) || item.getKey().equals(BXBusItemVO.CJKBBJE) || item.getKey().equals(BXBusItemVO.HKBBJE)
				|| item.getKey().equals(BXBusItemVO.ZFBBJE)) {
			int bbkeCurrencyPrecision = 0;
			try {
				bbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(zbvo.getParentVO().getPk_org()));
				item.setDecimalDigits(bbkeCurrencyPrecision);
			} catch (BusinessException e1) {
				Log.getInstance("BXuiUtil").error(e1);
			}
		}
		// ���ü��ű��Ҿ���
		else if (item.getKey().equals(BXBusItemVO.GROUPBBJE) || item.getKey().equals(BXBusItemVO.GROUPCJKBBJE) || item.getKey().equals(BXBusItemVO.GROUPHKBBJE)
				|| item.getKey().equals(BXBusItemVO.GROUPZFBBJE)) {
			int GroupbbkeCurrencyPrecision = 0;
			try {
				GroupbbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getGroupCurrpk(zbvo.getParentVO().getPk_group()));
				item.setDecimalDigits(GroupbbkeCurrencyPrecision);
			} catch (BusinessException e1) {
				Log.getInstance("BXuiUtil").error(e1);
			}
		}
		// ����ȫ�ֱ��Ҿ���
		else if (item.getKey().equals(BXBusItemVO.GLOBALBBJE) || item.getKey().equals(BXBusItemVO.GLOBALCJKBBJE) || item.getKey().equals(BXBusItemVO.GLOBALHKBBJE)
				|| item.getKey().equals(BXBusItemVO.GLOBALZFBBJE)) {
			int GroupbbkeCurrencyPrecision = 0;
			try {
				GroupbbkeCurrencyPrecision = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));
				item.setDecimalDigits(GroupbbkeCurrencyPrecision);
			} catch (BusinessException e1) {
				Log.getInstance("BXuiUtil").error(e1);
			}
		}
	}

	/**
	 * ���ÿ�Ƭ����б�ͷ�������
	 * 
	 * @author chenshuai
	 * @param panel
	 *            ��Ƭ
	 * @param decimalDigits
	 *            ����
	 * @param headJeKeys
	 *            ��ͷ���key����
	 * @param bodyJeKeys
	 *            ������key����
	 */
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
//				BillModel model = panel.getBillModel(tableCode);
//				int rowCount = model.getRowCount();
				if (bodyJeKeys != null && bodyJeKeys.length > 0) {
					for (String key : bodyJeKeys) {
						if (panel.getBodyItem(tableCode, key) != null) {
							panel.getBodyItem(tableCode, key).setDecimalDigits(decimalDigits);
//							for (int i = 0; i < rowCount; i++) {//�����ڱ����ж�ʱ�������ʱ����ΪҪ����ϼ�ֵ��Ч�ʺܵ�
//								Object valueAt = model.getValueAt(i, key);
//								if (valueAt != null) {
//									UFDouble value = new UFDouble(valueAt.toString());
//									model.setValueAt(value, i, key);
//								}
//							}
						}
					}
				}
			}
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
	private static void resetCardBodyDecimalDigit(BillCardPanel panel, int decimalDigits, String tableCode, String[] bodyJeKeys) {

		// ���徫��
		BillModel model = panel.getBillModel(tableCode);
		if(model==null)return;
		int rowCount = model.getRowCount();
		if (bodyJeKeys != null && bodyJeKeys.length > 0) {
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
		}
	}

	/**
	 * �����Զ������У���ֵ�����ֶξ���
	 * 
	 * @param panel
	 * @param tableCode
	 * @param decimalDigits
	 */
	private static void resetBodyDefDecimalDigits(BillCardPanel panel, String tableCode, int decimalDigits) {
		// �����Զ�����
		BillItem[] jeItemKeys = panel.getBillModel(tableCode).getBodyItems();
		String[] defItems = new String[jeItemKeys.length];
		int num = 0;
		for (BillItem item : jeItemKeys) {
			if (null != item.getKey() && item.getKey().toString().toUpperCase().contains("DEF") && item.getDataType() == IBillItem.DECIMAL
			// ��ֵ���Ͷ�����
			/* && item.getIDColName().toLowerCase().equalsIgnoreCase("amount") */) {
				// ����û������ƣ��������е���ģ��,������������

				defItems[num++] = item.getKey().toString();
			}
		}
		if (null != defItems && defItems.length > 0) {
			for (String key : defItems) {
				if (null != key) {
					if (panel.getBodyItem(tableCode, key) != null) {
						panel.getBodyItem(tableCode, key).setDecimalDigits(decimalDigits);
						BillModel model = panel.getBillModel(tableCode);
						int rowCount = model.getRowCount();
						for (int j = 0; j < rowCount; j++) {
							Object valueAt = model.getValueAt(j, key);
							if (valueAt != null) {
								UFDouble value = new UFDouble(valueAt.toString());
								model.setValueAt(value, j, key);
							}
						}
					}
				}
			}
		}
		// --end �����Զ�����
	}

	/**
	 * ����vo�����б���汾�һ��ʾ���
	 * 
	 * @author chendya@ufida.com.cn
	 * @param listPanel
	 * @param headerVOs
	 */
	public static void resetBBHLDecimal(BillListPanel listPanel, JKBXHeaderVO[] headerVOs) {
		if (headerVOs == null || headerVOs.length == 0) {
			return;
		}
		int iMaxDecimalDigits = 0;
		for (int i = 0; i < headerVOs.length; i++) {
			int decimalDigits = getBBHLDecimal(headerVOs[i].getPk_org(), headerVOs[i].getBzbm());
			if (decimalDigits > iMaxDecimalDigits) {
				iMaxDecimalDigits = decimalDigits;
			}
		}
		// ȡ���ľ�������
		listPanel.getHeadItem(JKBXHeaderVO.BBHL).setDecimalDigits(iMaxDecimalDigits);
	}

	/**
	 * ���ر��һ��ʾ���
	 * 
	 * @author chendya@ufida.com.cn
	 * @param listPanel
	 * @param pk_org
	 * @param currency
	 */
	public static int getBBHLDecimal(String pk_org, String currency) {
		return ErUiUtil.getBBHLDecimal(pk_org, currency);
	}

	public static void resetDecimalForContrast(BillListPanel panel, String currency) {
		String djCurrency = "";
		if (panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null && !panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString().equals("")) {
			djCurrency = panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
		} else {
			djCurrency = currency;
		}
		if (djCurrency != null && !djCurrency.equals("")) {
			int currencyPrecision = 0;// ԭ�ұ��־���
			try {
				currencyPrecision = Currency.getCurrDigit(djCurrency);
			} catch (Exception e) {
			}
			panel.getHeadItem(JKBXHeaderVO.YBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.YBYE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.CJKYBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.HKYBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.YJYE).setDecimalDigits(currencyPrecision);
			
			//���ñ���ľ���
			panel.getBodyItem(JKBXHeaderVO.YBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.YBYE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.CJKYBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.HKYBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.YJYE).setDecimalDigits(currencyPrecision);
		}
	}

	/**
	 * �����ַ����ж���ķ���
	 * 
	 * @param digitValue
	 * @return
	 */
	public static String convertToTrueString(String digitValue) {
		return ErUiUtil.convertToTrueString(digitValue);
//		if (digitValue.contains(",")) {
//			digitValue = StringUtil.replaceAllString(digitValue, ",", "");
//		}
//		if (digitValue.contains("@")) {
//			digitValue = StringUtil.replaceAllString(digitValue, "@", "");
//		}
//		if (digitValue.startsWith("\"") && digitValue.endsWith("\"")) {
//			digitValue = digitValue.substring(1, digitValue.length() - 1);
//		}
//		return digitValue;
	}

	/**
	 * ����ģ���Ӧ����ʼ�ڼ�(UFDate)<br/>
	 * modified by chendya ��Ƶ���仯������,�޸�Ϊ���淽ʽ,����Զ�̹��̵��ô���
	 * 
	 * @author chendya
	 * @param productID
	 *            ģ���
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public static UFDate getStartDate(String pk_org) throws BusinessException {
		final String key = QcDateCall.QcDate_Date_PK_ + pk_org;
		if (WorkbenchEnvironment.getInstance().getClientCache(key) == null) {
			UFDate startDate = null;
            try {
                startDate = NCLocator.getInstance()
                        .lookup(IBXBillPrivate.class).queryOrgStartDate(pk_org);
            } catch (Throwable e) {
                Logger.error(e.getMessage(), e);
            }
			WorkbenchEnvironment.getInstance().putClientCache(key, startDate);
		}
		return (UFDate) WorkbenchEnvironment.getInstance().getClientCache(key);

	}

	/**
	 * ����ģ���Ӧ����ʼ�ڼ�(UFDateTime)<br/>
	 * modified by chendya ��Ƶ���仯������,�޸�Ϊ���淽ʽ,����Զ�̹��̵��ô���
	 * 
	 * @author chendya
	 * @param productID
	 *            ģ���
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public static UFDateTime getStartDateTime(String productID, String pk_org) throws BusinessException {
		return ErUiUtil.getStartDateTime(productID, pk_org);
//		final String key = BXConstans.ACC_PERIORD_PK_DATETIME + productID + pk_org;
//		if (WorkbenchEnvironment.getInstance().getClientCache(key) == null) {
//			String pk_accperiod = getOrgUnitPubService().getOrgModulePeriodByOrgIDAndModuleID(pk_org, productID);
//			UFDateTime accperiod = null;
//			if (pk_accperiod != null && pk_accperiod.length() != 0) {
//				accperiod = new UFDateTime(pk_accperiod.trim() + "-01");
//				WorkbenchEnvironment.getInstance().putClientCache(key, accperiod);
//			}
//		}
//		return (UFDateTime) WorkbenchEnvironment.getInstance().getClientCache(key);

	}

	/**
	 * ������֯�Ļ������ modified by chendya ��Ƶ���仯������,�޸�Ϊ���淽ʽ,����Զ�̹��̵��ô���
	 * 
	 * @author chendya
	 * @param pk_org
	 * @return
	 */
	public static AccountCalendar getOrgAccountCalendar(String pk_org) {
		return ErUiUtil.getOrgAccountCalendar(pk_org);
//		final String key = BXConstans.ORG_ACCOUNT_CALENDAR;
//		if (WorkbenchEnvironment.getInstance().getClientCache(key) == null) {
//			AccountCalendar value = AccountCalendar.getInstanceByPk_org(pk_org);
//			if (value != null) {
//				WorkbenchEnvironment.getInstance().putClientCache(key, value);
//			}
//		}
//		return (AccountCalendar) WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	public static UFBoolean isGroup(String node) {
		return ErUiUtil.isGroup(node);
//		FuncRegisterVO registerVO = WorkbenchEnvironment.getInstance().getFuncRegisterVO(node);
//		String orgtype = registerVO.getOrgtypecode();
//		if (orgtype.equals(BXConstans.GLOBALORGTYPE)) {
//			return UFBoolean.TRUE;
//		} else {
//			return UFBoolean.FALSE;
//		}
	}

	/**
	 * ǰ̨��ʽ
	 * 
	 * @author chendya
	 */
	public static String getColValue(String table, String column, String pk, String pkValue) {
		return ErUiUtil.getColValue(table, column, pk, pkValue);
//		FormulaParse parser = getUIFormularParser();
//		parser.setExpress("getColValue" + "(" + table + "," + column + "," + pk + "," + "var" + ")");
//		parser.addVariable("var", pkValue);
//		return parser.getValue();
	}

	/**
	 * ǰ̨��ʽ
	 * 
	 * @author chendya
	 */
	public static String getColValue2(String table, String column, Object pk1, Object value1, Object pk2, Object value2) {
		return ErUiUtil.getColValue2(table, column, pk1, value1, pk2, value2);
		//		FormulaParse parser = getUIFormularParser();
//		parser.setExpress("getColValue2" + "(" + table + "," + column + "," + pk1 + "," + "var1" + "," + pk2 + "," + "var2" + ")");
//		parser.addVariable("var1", value1);
//		parser.addVariable("var2", value2);
//		return parser.getValue();
	}

	/**
	 * ǰ̨��ʽ������
	 * 
	 * @author chendya
	 * @return
	 */
//	private static FormulaParse getUIFormularParser() {
//		return new FormulaParse();
//	}

	/**
	 * ����(����ժҪ)�ֶ����⴦����
	 * 
	 * @author chendya
	 * @param refPane
	 * @param inputTxt
	 * @return
	 * @throws BusinessException
	 */
	public static String getMatchPk(UIRefPane refPane, String inputTxt) throws BusinessException {
		nc.ui.erm.ref.BXSummaryRefModel model = (nc.ui.erm.ref.BXSummaryRefModel) refPane.getRefModel();
		List<String> allFields = new ArrayList<String>();
		allFields.addAll(Arrays.asList(model.getFieldCode()));
		allFields.addAll(Arrays.asList(model.getHiddenFieldCode()));
		final String[] fields = allFields.toArray(new String[0]);
		for (int i = 0; i < fields.length; i++) {
			Object[] values = BDCacheQueryUtil.queryVOs(SummaryVO.class, new String[] { SummaryVO.PK_SUMMARY }, new String[] { fields[i], "dr" }, new Object[][] { {
					inputTxt, 0 } });
			if (values != null && values.length > 0 && values[0] != null) {
				return ((SummaryVO) values[0]).getPk_summary();
			}
		}
		return null;
	}

	/**
	 * ������ã���ʾ����Uif2���ƵĴ�����Ϣ
	 * 
	 * @author chendya
	 */
	public static void showUif2DetailMessage(JComponent comp, String statusBarErrMsg, java.lang.Throwable ex) {
		ErUiUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
//		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
	}

	/**
	 * ������ã���ʾ����Uif2���ƵĴ�����Ϣ �Ի����б���
	 * 
	 * @author chenshuai
	 */
	public static void showUif2DetailMessage(Component comp, String statusBarErrMsg, String msg) {
		ErUiUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
//		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
	}
	
	public static void modifyLoadFormula(BillItem billItem, String updateStr) {
		// �޸ĵ����������Ƽ��ع�ʽ
		if (billItem != null && !ArrayUtils.isEmpty(billItem.getLoadFormula())) {
			String[] loadFormulas = billItem.getLoadFormula();
			for (int i = 0; i < loadFormulas.length; i++) {
				if (!loadFormulas[i].matches("(.*)" + updateStr+ "[1-9]{1,}(.*)")) {
					loadFormulas[i] = loadFormulas[i].replace(updateStr, updateStr+ PubCommonReportMethod.getMultiLangIndex());
				}
			}
			billItem.setLoadFormula(loadFormulas);
		}
	}

}