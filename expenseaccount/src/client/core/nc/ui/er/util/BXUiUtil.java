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
	 * 根据单据类型别名获取单据类型名称
	 * 
	 * @param djlxbm
	 * @return
	 */
	public static String getDjlxNameMultiLang(String djlxbm) {
		return ErUiUtil.getDjlxNameMultiLang(djlxbm);
//		String strByID = null;
//		if (djlxbm != null) {
//			String billtypecode = djlxbm;
//			// 自定义的也处理
//			String resId = "D" + billtypecode;
//			strByID = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("billtype", resId);
//
//			if (resId.equals(strByID)) {// 多语内容取不到，说明是自定义的，且没设置多语，取默认的name
//				strByID = PfDataCache.getBillTypeInfo(billtypecode).getBilltypenameOfCurrLang();
//			}
//		}
//		return strByID;
	}

	/**
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static Map<String, String> getPermissionOrgMap(String nodeCode) {
		return ErUiUtil.getPermissionOrgMap(nodeCode);
	}

	/**
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgs(String nodeCode) {
		return ErUiUtil.getPermissionOrgs(nodeCode);
	}

	/**
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgVs(String nodeCode) {
		return ErUiUtil.getPermissionOrgVs(nodeCode);
	}

	/**
	 * 返回用户所关联的人员信息(array[0]=
	 * 人员主键，array[1]=人员所属部门,array[2]=人员所属组织,array[3]=人员所在集团)
	 * 
	 * @param cuserid
	 *            用户id
	 * @return
	 */
	public static String[] getPsnDocInfo(String cuserid) {
		String[] retValues = new String[4];
		// 人员主键
		final String value = getPk_psndoc(cuserid);
		if (value == null || value.length() == 0) {
			return retValues;
		}
		return getPsnDocInfoById(value);
	}

	/**
	 * 返回人员信息(array[0]= 人员主键，array[1]=人员所属部门,array[2]=人员所属组织,array[3]=人员所在集团)
	 * 
	 * @param pk_psndoc
	 *            人员组件
	 * @return
	 */
	public static String[] getPsnDocInfoById(String pk_psndoc) {
		String[] retValues = new String[4];
		// 人员
		retValues[0] = pk_psndoc;
		// 部门
		retValues[1] = getPsnPk_dept(pk_psndoc);
		// 组织
		retValues[2] = getPsnPk_org(pk_psndoc);
		// 集团
		retValues[3] = getPsnPk_group(pk_psndoc);

		return retValues;
	}

	/**
	 * 人员所在集团，连带缓存组织
	 * 
	 * @param pk_psndoc
	 *            人员主键
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
				// 人员所属组织
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
	 * 人员所属组织，连带缓存组织
	 * 
	 * @param pk_psndoc
	 *            人员主键
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
				// 人员所属组织
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
	 * 返回人员所在部门
	 * 
	 * @author chendya
	 * @param pk_psndoc
	 *            人员主键
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
	 * 授权代理人设置
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
	 *            单据类型主键
	 * @param user
	 *            登陆用户
	 * @param date
	 *            日期
	 * @return 返回一个sql语句，追加到借款报销人的业务员参照的wherepart上进行过滤
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
	 * 返回个性化中心设置的业务单元,没有设置，返回null
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
	 * 返回当前用户在当前集团的默认业务单元 1.如果个性化中心设置了业务单元，则优先返回个性化中心设置的业务单元
	 * 2.如果个性化中心未设置，则取当前登录用户所属组织作为默认业务单元
	 * 
	 * @author chendya
	 * 
	 * @throws Exception
	 */
	public static String getBXDefaultOrgUnit() {
		return ErUiUtil.getBXDefaultOrgUnit();
//		String pk_org = getDefaultOrgUnit();
//		if (pk_org == null || pk_org.length() == 0) {
//			// 个性化中心取不到，则取当前登陆人所属组织作为默认业务单元
//			pk_org = getPsnPk_org(getPk_psndoc());
//		}
//		return pk_org;
	}

	/**
	 * 根据key返回个性化中心设置的value
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
	 * 返回指定用户在指定集团的默认业务单元主键
	 * 
	 * @param pk_user
	 *            用户主键
	 * @param pk_group
	 *            集团主键
	 */
	public static String getDefaultOrgUnit(String pk_user, String pk_group) throws Exception {
		return ErUiUtil.getDefaultOrgUnit(pk_user, pk_group);
//		return OrgSettingAccessor.getDefaultOrgUnit(pk_user, pk_group);
	}

	/**
	 * 用键获得一个全局变量的值。
	 * 
	 * 
	 * @param key
	 *            全局变量的键
	 * @return 全局变量的值
	 */
	public static Object getValue(Object key) {
		return ErUiUtil.getValue(key);
//		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * 返回指定对应的集团的名称
	 * 
	 * @return
	 */
	public static String getGroupName() {
		return ErUiUtil.getGroupName();

//		return WorkbenchEnvironment.getInstance().getGroupVO().getName();
	}

	/**
	 * 返回当前登录的用户
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return ErUiUtil.getPk_user();
//		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}

	/**
	 * 返回指定用户所对应的业务员
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
	 * 返回当前登陆用户所对应的业务员
	 * 
	 * @return
	 */
	public static String getPk_psndoc() {
		return ErUiUtil.getPk_psndoc();
	}

	/**
	 * 返回人员所在部门
	 * 
	 * @param pk_psndoc
	 * @return
	 */
	public static String getPk_dept(String pk_psndoc) {
		return (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group());
	}

	/**
	 * 返回当前会计年度
	 * 
	 * @return 暂时下面方法取？？
	 * 
	 */
	public static String getAccountYear() {
		return ErUiUtil.getAccountYear();
	}

	/**
	 * 返回当前日期
	 * 
	 * @return
	 * 
	 */
	public static UFDate getSysdate() {
		return ErUiUtil.getSysdate();
	}

	/**
	 * 返回业务日期
	 * 
	 * @return
	 * 
	 */
	public static UFDate getBusiDate() {
		return ErUiUtil.getBusiDate();
	}

	/**
	 * 返回当前日期时间
	 * 
	 * @return
	 * 
	 */
	public static UFDateTime getSysdatetime() {
		return ErUiUtil.getSysdatetime();
	}

	
	/**
	 * ui2监听
	 * 
	 * @return
	 * 
	 */
	public static void addDecimalListenerToListpanel(BillListPanel listPanel) {
		// 添加汇率精度监听器
		if (listPanel.getHeadItem(JKBXHeaderVO.BBHL) != null) {
			ERMCurrencyDecimalListener listener = new ERMCurrencyDecimalListener(listPanel);
			listPanel.getHeadBillModel().addDecimalListener(listener);

		}
		// 添加原币金额币种精度监听器
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
		// 设置本币金额币种精度
		String bbjeCurrency = "";
		int bbkeCurrencyPrecision = 0;// 本币币种精度
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
	 * 监听
	 * 
	 * @return
	 * 
	 */
//	public static void addDecimalListenerToListpanel(BillListPanel listPanel, VOCache cache) {
//		// 添加汇率精度监听器
//		if (listPanel.getHeadItem(JKBXHeaderVO.BBHL) != null) {
//			BxCurrencyDecimalListener listener = new BxCurrencyDecimalListener(cache);
//			listPanel.getHeadBillModel().addDecimalListener(listener);
//
//		}
//		// 添加原币金额币种精度监听器
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
//		// 设置本币金额币种精度
//		String bbjeCurrency = "";
//		int bbkeCurrencyPrecision = 0;// 本币币种精度
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
	 * 卡片界面，根据单据的币种，重设本币汇率的精度，和其他金额字段的币种精度。 可能在以下动作中被调用：<br>
	 * 1.新增单据（AddAction.java）<br>
	 * 2.修改币种字段（BxCardHeadEditListener.java -> Line66）<br>
	 * 3.卡片界面初始化时，设置VO之前（BXBillCardPanel.setBillValueVO() -> Line271）<br>
	 * 
	 * @param panel
	 *            BillCardPanel
	 * @param currency
	 *            String 在界面设置VO之前，从界面取不到币种值，要先用VO中的币种编码设置好精度再填入VO。
	 *            如果调用本方法时，界面中已填入VO，从界面可以得到币种的值，那么此参数传入空串或null即可。
	 * @return void
	 * @author zhangxiao1
	 * @param string
	 * @throws Exception
	 */
	public static void resetDecimal(BillCardPanel panel, String pk_org, String currency) throws Exception {
		if (pk_org == null || pk_org.trim().length() == 0)
			return;
		// 原币
		String pk_currtype = currency;
//		if (panel.getHeadItem(JKBXHeaderVO.BZBM) != null && panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject() != null
//				&& !panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString().equals("")) {
//			pk_currtype = panel.getHeadItem(JKBXHeaderVO.BZBM).getValueObject().toString();
//		} else {
//			pk_currtype = currency;
//		}
		if (pk_currtype != null && pk_currtype.trim().length() > 0) {
			// 汇率精度
			int hlPrecision = 0;
			// 全局本币汇率精度
			int globalhlPrecision = 0;
			// 集团本币汇率精度
			int grouphlPrecision = 0;
			try {
				hlPrecision = Currency.getRateDigit(pk_org, pk_currtype, Currency.getOrgLocalCurrPK(pk_org));
				// 集团汇率精度
				grouphlPrecision = Currency.getGroupRateDigit(pk_org, BXUiUtil.getPK_group(), pk_currtype);
				// 全局汇率精度
				globalhlPrecision = Currency.getGlobalRateDigit(pk_org, pk_currtype);
				
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
			panel.getHeadItem(JKBXHeaderVO.BBHL).setDecimalDigits(hlPrecision);
			panel.getHeadItem(JKBXHeaderVO.GLOBALBBHL).setDecimalDigits(globalhlPrecision);
			panel.getHeadItem(JKBXHeaderVO.GROUPBBHL).setDecimalDigits(grouphlPrecision);
			
			// 重设组织本币汇率精度
			resetCardBodyDecimalDigit(panel, hlPrecision, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.BBHL});
			// 重设集团本币汇率精度
			resetCardBodyDecimalDigit(panel, grouphlPrecision, BXConstans.CSHARE_PAGE,  new String[]{CShareDetailVO.GLOBALBBHL});
			// 重设全局本币汇率精度
			resetCardBodyDecimalDigit(panel, globalhlPrecision, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.GLOBALBBHL});
		
			

			int ybDecimalDigit = Currency.getCurrDigit(pk_currtype);// 原币精度
			int orgBbDecimalDigit = Currency.getCurrDigit(Currency.getOrgLocalCurrPK(pk_org));// 组织本币精度
			int groupByDecimalDigit = Currency.getCurrDigit(Currency.getGroupCurrpk(BXUiUtil.getPK_group()));// 集团本币精度
			int globalByDecimalDigit = Currency.getCurrDigit(Currency.getGlobalCurrPk(null));

			// 设置表体业务页签原币金额精度
			resetCardDecimalDigit(panel, ybDecimalDigit, JKBXHeaderVO.getYbjeField(), BXBusItemVO.getYbjeField());
			// 设置表体业务页签组织本币金额精度
			resetCardDecimalDigit(panel, orgBbDecimalDigit, JKBXHeaderVO.getOrgBbjeField(), BXBusItemVO.getBodyOrgBbjeField());
			// 重设表体业务页签集团本币精度
			resetCardDecimalDigit(panel, groupByDecimalDigit, JKBXHeaderVO.getHeadGroupBbjeField(), BXBusItemVO.getBodyGroupBbjeField());
			// 重设表体业务页签全局本币精度
			resetCardDecimalDigit(panel, globalByDecimalDigit, JKBXHeaderVO.getHeadGlobalBbjeField(), BXBusItemVO.getBodyGlobalBbjeField());

			String[] tableCodes = panel.getBillData().getBodyTableCodes();
			if (tableCodes != null && tableCodes.length > 0) {
				for (String tableCode : tableCodes) {
					// 重设自定义项中，数值类型字段的精度
					resetBodyDefDecimalDigits(panel, tableCode, ybDecimalDigit);
				}
			}
			
			// 设置表体分摊页签原币金额精度
			resetCardBodyDecimalDigit(panel, ybDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.ASSUME_AMOUNT});
			// 设置表体分摊页签组织本币金额精度
			resetCardBodyDecimalDigit(panel, orgBbDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.BBJE});
			// 重设表体分摊页签集团本币精度
			resetCardBodyDecimalDigit(panel, groupByDecimalDigit, BXConstans.CSHARE_PAGE, new String[]{CShareDetailVO.GROUPBBJE});
			// 重设表体分摊页签全局本币精度
			resetCardBodyDecimalDigit(panel, globalByDecimalDigit, BXConstans.CSHARE_PAGE,  new String[]{CShareDetailVO.GLOBALBBJE});

			
			
			// // 设置原币金额精度（deleted at 2012-3-13）
			// resetYBDecimal(panel, ybDecimalDigit);
			//
			// // 设置组织本币金额精度（deleted at 2012-3-13）
			// resetBBDecimal(panel, orgBbDecimalDigit);
			//			
			// //重设集团本币精度（deleted at 2012-3-13）
			// resetGroupBbDecimalDigits(panel, groupByDecimalDigit);
			//			
			// //重设全局本币精度（deleted at 2012-3-13）
			// resetGlobalBbDecimalDigits(panel, globalByDecimalDigit);
		}
	}

	/**
	 * 设置原币精度 设置本币金额精度 设置集团本币精度 设置全局本币精度
	 */
	public static void resetDecimalDigits(BillItem item, JKBXVO zbvo) {
		// 设置原币精度
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

		// 设置组织本币精度
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
		// 设置集团本币精度
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
		// 设置全局本币精度
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
	 * 设置卡片面板中表头表体金额精度
	 * 
	 * @author chenshuai
	 * @param panel
	 *            卡片
	 * @param decimalDigits
	 *            精度
	 * @param headJeKeys
	 *            表头金额key集合
	 * @param bodyJeKeys
	 *            表体金额key集合
	 */
	private static void resetCardDecimalDigit(BillCardPanel panel, int decimalDigits, String[] headJeKeys, String[] bodyJeKeys) {

		// 表头精度
		if (headJeKeys != null && headJeKeys.length > 0) {
			for (String key : headJeKeys) {
				if (panel.getHeadItem(key) != null) {
					panel.getHeadItem(key).setDecimalDigits(decimalDigits);
				}
			}
		}

		// 表体精度
		String[] tableCodes = panel.getBillData().getBodyTableCodes();
		if (tableCodes != null && tableCodes.length > 0) {
			for (String tableCode : tableCodes) {
//				BillModel model = panel.getBillModel(tableCode);
//				int rowCount = model.getRowCount();
				if (bodyJeKeys != null && bodyJeKeys.length > 0) {
					for (String key : bodyJeKeys) {
						if (panel.getBodyItem(tableCode, key) != null) {
							panel.getBodyItem(tableCode, key).setDecimalDigits(decimalDigits);
//							for (int i = 0; i < rowCount; i++) {//这里在表体行多时，极其耗时，因为要计算合计值，效率很低
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
	 * 设置卡片面板中表体中具体字段精度
	 * 
	 * @author chenshuai
	 * @param panel
	 *            卡片
	 * @param decimalDigits
	 *            精度
	 * @param tableCode
	 *            标签code
	 * @param bodyJeKeys
	 *            表体金额key集合
	 */
	private static void resetCardBodyDecimalDigit(BillCardPanel panel, int decimalDigits, String tableCode, String[] bodyJeKeys) {

		// 表体精度
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
	 * 控制自定义项中，数值类型字段精度
	 * 
	 * @param panel
	 * @param tableCode
	 * @param decimalDigits
	 */
	private static void resetBodyDefDecimalDigits(BillCardPanel panel, String tableCode, int decimalDigits) {
		// 处理自定义项
		BillItem[] jeItemKeys = panel.getBillModel(tableCode).getBodyItems();
		String[] defItems = new String[jeItemKeys.length];
		int num = 0;
		for (BillItem item : jeItemKeys) {
			if (null != item.getKey() && item.getKey().toString().toUpperCase().contains("DEF") && item.getDataType() == IBillItem.DECIMAL
			// 数值类型都控制
			/* && item.getIDColName().toLowerCase().equalsIgnoreCase("amount") */) {
				// 如果用户不控制，可以自行调整模版,换成其它类型

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
		// --end 处理自定义项
	}

	/**
	 * 根据vo设置列表界面本币汇率精度
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
		// 取最大的精度设置
		listPanel.getHeadItem(JKBXHeaderVO.BBHL).setDecimalDigits(iMaxDecimalDigits);
	}

	/**
	 * 返回本币汇率精度
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
			int currencyPrecision = 0;// 原币币种精度
			try {
				currencyPrecision = Currency.getCurrDigit(djCurrency);
			} catch (Exception e) {
			}
			panel.getHeadItem(JKBXHeaderVO.YBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.YBYE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.CJKYBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.HKYBJE).setDecimalDigits(currencyPrecision);
			panel.getHeadItem(JKBXHeaderVO.YJYE).setDecimalDigits(currencyPrecision);
			
			//设置表体的精度
			panel.getBodyItem(JKBXHeaderVO.YBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.YBYE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.CJKYBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.HKYBJE).setDecimalDigits(currencyPrecision);
			panel.getBodyItem(JKBXHeaderVO.YJYE).setDecimalDigits(currencyPrecision);
		}
	}

	/**
	 * 消除字符串中多余的符号
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
	 * 返回模块对应的起始期间(UFDate)<br/>
	 * modified by chendya 不频繁变化的数据,修改为缓存方式,减少远程过程调用次数
	 * 
	 * @author chendya
	 * @param productID
	 *            模块号
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
	 * 返回模块对应的起始期间(UFDateTime)<br/>
	 * modified by chendya 不频繁变化的数据,修改为缓存方式,减少远程过程调用次数
	 * 
	 * @author chendya
	 * @param productID
	 *            模块号
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
	 * 返回组织的会计日历 modified by chendya 不频繁变化的数据,修改为缓存方式,减少远程过程调用次数
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
	 * 前台公式
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
	 * 前台公式
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
	 * 前台公式解析器
	 * 
	 * @author chendya
	 * @return
	 */
//	private static FormulaParse getUIFormularParser() {
//		return new FormulaParse();
//	}

	/**
	 * 事由(常用摘要)字段特殊处理工具
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
	 * 反射调用，显示类似Uif2类似的错误消息
	 * 
	 * @author chendya
	 */
	public static void showUif2DetailMessage(JComponent comp, String statusBarErrMsg, java.lang.Throwable ex) {
		ErUiUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
//		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
	}

	/**
	 * 反射调用，显示类似Uif2类似的错误消息 对话框中报错
	 * 
	 * @author chenshuai
	 */
	public static void showUif2DetailMessage(Component comp, String statusBarErrMsg, String msg) {
		ErUiUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
//		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
	}
	
	public static void modifyLoadFormula(BillItem billItem, String updateStr) {
		// 修改单据类型名称加载公式
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