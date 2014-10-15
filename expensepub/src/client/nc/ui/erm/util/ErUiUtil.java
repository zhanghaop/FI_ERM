package nc.ui.erm.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bs.bd.service.ValueObjWithErrLog;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.individuation.property.pub.IndividuationManager;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.itf.fi.pub.Currency;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.pubitf.para.SysInitQuery;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.ui.arap.bx.remote.IPermissionOrgVoCallConst;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefGridTreeModel;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.IFilterCommonDataVec;
import nc.ui.dbcache.DBCacheFacade;
import nc.ui.erm.billpub.remote.RoleVoCall;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.psn.PsnjobVO;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.utils.uif2.FiUif2MsgUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;
import nc.vo.sm.enumfactory.UserIdentityTypeEnumFactory;
import nc.vo.sm.funcreg.FuncRegisterVO;
import nc.vo.uif2.LoginContext;

import org.apache.commons.lang.ArrayUtils;

@SuppressWarnings("restriction")
public class ErUiUtil {

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPermissionOrgMap(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_MAP + ErUiUtil.getPk_user()
				+ ErUiUtil.getPK_group();
		Map<String, String> map = (Map<String, String>) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (map == null)
			try {
				map = NCLocator.getInstance().lookup(IErmBsCommonService.class)
						.getPermissonOrgMapCall(ErUiUtil.getPk_user(), nodeCode, ErUiUtil.getPK_group());
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		return map;
	}

	public static void setRefFilterPks(AbstractRefModel refModel, String[] pk_vids) {
		setRefFilterPks(refModel, pk_vids, IFilterStrategy.INSECTION);
	}

	public static void setRefFilterPks(AbstractRefModel refModel, String[] pk_vids, int filterStrategy) {
		refModel.setSelectedData(null);
		refModel.setFilterPks(pk_vids);
		refModel.setSelectedData(refModel.getSelectedData());
		refModel.fireChange();
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgs(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG + ErUiUtil.getPk_user()
				+ ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null) {
			try {
				Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class)
						.getPermissonOrgMapCall(ErUiUtil.getPk_user(), nodeCode, ErUiUtil.getPK_group());
				values = map.values().toArray(new String[0]);
				WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);

				String orgVCacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V + ErUiUtil.getPk_user()
						+ ErUiUtil.getPK_group();
				WorkbenchEnvironment.getInstance().putClientCache(orgVCacheKey, map.keySet().toArray(new String[0]));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return values;
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ��
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgVs(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V + ErUiUtil.getPk_user()
				+ ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null) {
			try {
				Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class)
						.getPermissonOrgMapCall(ErUiUtil.getPk_user(), nodeCode, ErUiUtil.getPK_group());
				values = map.keySet().toArray(new String[0]);
				WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);

				String orgCacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG + ErUiUtil.getPk_user()
						+ ErUiUtil.getPK_group();
				WorkbenchEnvironment.getInstance().putClientCache(orgCacheKey, map.values().toArray(new String[0]));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return values;
	}

	/**
	 * ���ص�ǰ��¼�û��Ĺ��ܽڵ��Ȩ�� ��汾
	 * 
	 * @param nodeCode
	 * @param date
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static String[] getPermissionOrgVs(LoginContext context, UFDate date) {
		String nodeCode = context.getNodeCode();
		if (nodeCode.equals(BXConstans.BXINIT_NODECODE_G)) {
			nodeCode = BXConstans.BXINIT_NODECODE_U;
		}

		if (date == null) {
			date = getSysdate();
		}
		final String cacheKey = nodeCode + date.toStdString() + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V
				+ ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null) {
			try {
				String[] permissionOrgs = null;
				if (BXConstans.BXINIT_NODECODE_G.equals(context.getNodeCode())) {
					permissionOrgs = getPermissionOrgs(nodeCode);
				} else {
					permissionOrgs = context.getFuncInfo().getFuncPermissionPkorgs();
				}

				if (!ArrayUtils.isEmpty(permissionOrgs)) {
					HashMap<String, String> newVIDSByOrgIDSAndDate = NCLocator.getInstance()
							.lookup(IOrgUnitPubService_C.class).getNewVIDSByOrgIDSAndDate(permissionOrgs, date);
					values = newVIDSByOrgIDSAndDate.values().toArray(new String[0]);
				}

				if (values == null) {
					values = new String[0];
				}

				WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return values;
	}

	/**
	 * ���ݵ������ͱ�����ȡ������������
	 * 
	 * @param djlxbm
	 * @return
	 */
	public static String getDjlxNameMultiLang(String djlxbm) {
		String strByID = null;
		if (djlxbm != null) {
			BilltypeVO billtypeVo = PfDataCache.getBillTypeInfo(djlxbm);
			if (billtypeVo != null) {
				strByID = billtypeVo.getBilltypenameOfCurrLang();
			}
		}
		return strByID;
	}

	/**
	 * ���ظ��Ի��������õ�ҵ��Ԫ,û�����ã�����null
	 * 
	 * @return
	 */
	public static String getDefaultOrgUnit() {
		try {
			return getSettingValue("org_df_biz");
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return null;
	}

	/**
	 * ����key���ظ��Ի��������õ�value
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	private static String getSettingValue(String key) throws BusinessException {
		return IndividuationManager.getIndividualSetting("nc.individuation.defaultData.DefaultConfigPage", false)
				.getString(key);
	}

	public static String getPK_group() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
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
		return OrgSettingAccessor.getDefaultOrgUnit(pk_user, pk_group);
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
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * ����ָ����Ӧ�ļ��ŵ�����
	 * 
	 * @return
	 */
	public static String getGroupName() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getName();
	}

	/**
	 * ���ص�ǰ��¼���û�
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}

	public static UserVO getUserVo() {
		return WorkbenchEnvironment.getInstance().getLoginUser();
	}

	/**
	 * ���ص�ǰ��½�û�����Ӧ��ҵ��Ա
	 * 
	 * @return
	 */
	public static String getPk_psndoc() {
		if (WorkbenchEnvironment.getInstance().getLoginUser().getBase_doc_type()
				.equals(UserIdentityTypeEnumFactory.TYPE_PERSON)) {
			return WorkbenchEnvironment.getInstance().getLoginUser().getPk_base_doc();
		} else {
			return null;
		}

	}

	/**
	 * ���ص�ǰ������
	 * 
	 * @return ��ʱ���淽��ȡ����
	 * 
	 */
	public static String getAccountYear() {
		return (Integer.valueOf(WorkbenchEnvironment.getServerTime().getYear())).toString();
	}

	/**
	 * ���ص�ǰ����
	 * 
	 * @return
	 * 
	 */
	public static UFDate getSysdate() {
		return WorkbenchEnvironment.getServerTime().getDate();
	}

	/**
	 * ����ҵ������
	 * 
	 * @return
	 * 
	 */
	public static UFDate getBusiDate() {
		return WorkbenchEnvironment.getInstance().getBusiDate();
	}

	/**
	 * ���ص�ǰ����ʱ��
	 * 
	 * @return
	 * 
	 */
	public static UFDateTime getSysdatetime() {
		return WorkbenchEnvironment.getServerTime();
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
		// ԭ�ҶԱ��һ��ʾ���
		int iDecimalDigits = 0;
		try {
			iDecimalDigits = Currency.getRateDigit(pk_org, currency, Currency.getGlobalCurrPk(pk_org));
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return iDecimalDigits;
	}

	/**
	 * �����ַ����ж���ķ���
	 * 
	 * @param digitValue
	 * @return
	 */
	public static String convertToTrueString(String digitValue) {
		if (digitValue.contains(",")) {
			digitValue = StringUtil.replaceAllString(digitValue, ",", "");
		}
		if (digitValue.contains("@")) {
			digitValue = StringUtil.replaceAllString(digitValue, "@", "");
		}
		if (digitValue.startsWith("\"") && digitValue.endsWith("\"")) {
			digitValue = digitValue.substring(1, digitValue.length() - 1);
		}
		return digitValue;
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
		final String key = BXConstans.ACC_PERIORD_PK_DATETIME + productID + pk_org;
		if (WorkbenchEnvironment.getInstance().getClientCache(key) == null) {
			String pk_accperiod = getOrgUnitPubService().getOrgModulePeriodByOrgIDAndModuleID(pk_org, productID);
			UFDateTime accperiod = null;
			if (pk_accperiod != null && pk_accperiod.length() != 0) {
				accperiod = new UFDateTime(pk_accperiod.trim() + "-01");
				WorkbenchEnvironment.getInstance().putClientCache(key, accperiod);
			}
		}
		return (UFDateTime) WorkbenchEnvironment.getInstance().getClientCache(key);

	}

	/**
	 * ������֯�Ļ������ modified by chendya ��Ƶ���仯������,�޸�Ϊ���淽ʽ,����Զ�̹��̵��ô���
	 * 
	 * @author chendya
	 * @param pk_org
	 * @return
	 */
	public static AccountCalendar getOrgAccountCalendar(String pk_org) {
		final String key = BXConstans.ORG_ACCOUNT_CALENDAR;
		if (WorkbenchEnvironment.getInstance().getClientCache(key) == null) {
			AccountCalendar value = AccountCalendar.getInstanceByPk_org(pk_org);
			if (value != null) {
				WorkbenchEnvironment.getInstance().putClientCache(key, value);
			}
		}
		return (AccountCalendar) WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	private static IOrgUnitPubService getOrgUnitPubService() {
		return NCLocator.getInstance().lookup(IOrgUnitPubService.class);
	}

	public static UFBoolean isGroup(String node) {
		FuncRegisterVO registerVO = WorkbenchEnvironment.getInstance().getFuncRegisterVO(node);
		String orgtype = registerVO.getOrgtypecode();
		if (orgtype.equals(BXConstans.GLOBALORGTYPE)) {
			return UFBoolean.TRUE;
		} else {
			return UFBoolean.FALSE;
		}
	}

	/**
	 * ǰ̨��ʽ
	 * 
	 * @author chendya
	 */
	public static String getColValue(String table, String column, String pk, String pkValue) {
		FormulaParse parser = getUIFormularParser();
		parser.setExpress("getColValue" + "(" + table + "," + column + "," + pk + "," + "var" + ")");
		parser.addVariable("var", pkValue);
		return parser.getValue();
	}

	/**
	 * ǰ̨��ʽ
	 * 
	 * @author chendya
	 */
	public static String getColValue2(String table, String column, Object pk1, Object value1, Object pk2, Object value2) {
		FormulaParse parser = getUIFormularParser();
		parser.setExpress("getColValue2" + "(" + table + "," + column + "," + pk1 + "," + "var1" + "," + pk2 + ","
				+ "var2" + ")");
		parser.addVariable("var1", value1);
		parser.addVariable("var2", value2);
		return parser.getValue();
	}

	/**
	 * ǰ̨��ʽ������
	 * 
	 * @author chendya
	 * @return
	 */
	private static FormulaParse getUIFormularParser() {
		return new FormulaParse();
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
		String pk_org = getDefaultOrgUnit();
		if (pk_org == null || pk_org.length() == 0) {
			// ���Ի�����ȡ��������ȡ��ǰ��½��������֯��ΪĬ��ҵ��Ԫ
			pk_org = getPsnPk_org(getPk_psndoc());
		}
		return pk_org;
	}

	public static String getReportDefaultOrgUnit() {
		String pk_org = getPsnPk_org(getPk_psndoc());
		if (pk_org == null || pk_org.length() == 0) {
			// ��ǰ��½��������֯ȡ��������ȡ���Ի�������ΪĬ��ҵ��Ԫ
			pk_org = getDefaultOrgUnit();
		}
		return pk_org;
	}

	/**
	 * ��ȡ��ǰ��¼�û�������֯
	 * 
	 * @return
	 */
	public static String getDefaultPsnOrg() {
		return getPsnPk_org(getPk_psndoc());
	}

	/**
	 * ������ã���ʾ����Uif2���ƵĴ�����Ϣ
	 * 
	 * @author chendya
	 */
	public static void showUif2DetailMessage(JComponent comp, String statusBarErrMsg, java.lang.Throwable ex) {
		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
	}

	/**
	 * ������ã���ʾ����Uif2���ƵĴ�����Ϣ �Ի����б���
	 * 
	 * @author chenshuai
	 */
	public static void showUif2DetailMessage(Component comp, String statusBarErrMsg, String msg) {
		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
	}

	/**
	 * ���˺�ȡ�����˵����������ʾ
	 * 
	 * @param context
	 * @param messageVos
	 * @throws BusinessException
	 */
	public static void showBatchBookAccResults(LoginContext context, ValueObjWithErrLog[] ObjWithErrLog,
			String ActionCommand) throws BusinessException {
		if (ArrayUtils.isEmpty(ObjWithErrLog))
			return;

		boolean existSuccess = false;
		boolean existFail = false;
		StringBuffer sbValue = new StringBuffer();
		for (ValueObjWithErrLog valueObjWithErrLog : ObjWithErrLog) {
			if (valueObjWithErrLog == null || valueObjWithErrLog.getErrLogList() == null) {
				existSuccess = true;
			} else if (valueObjWithErrLog.getErrLogList() != null) {
				existFail = true;
				String errorMsg = (String) valueObjWithErrLog.getErrLogList().get(0).getErrReason();
				sbValue.append(errorMsg).append("\r");
			}
		}

		String title = null;
		if ("EndAcc"/* -=notranslate=- */.equals(ActionCommand)) {
			if (existSuccess && !existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0", "0201109-0002");// ���˳ɹ�
			} else if (existSuccess && existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0158");// ���ֽ���ʧ��
			} else if (!existSuccess && existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0159");// ����ʧ��
			}
		} else {
			if (existSuccess && !existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201109_0", "0201109-0093");// ȡ�����˳ɹ�
			} else if (existSuccess && existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0160");// ����ȡ������ʧ��
			} else if (!existSuccess && existFail) {
				title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0161");// ȡ������ʧ��
			}
		}

		if (!existFail && existSuccess) {// ȫ���ɹ�
			ShowStatusBarMsgUtil.showStatusBarMsg(title, context);
		} else {
			ShowStatusBarMsgUtil.showErrorMsg(title, sbValue.toString(), context);
		}
	}

	/**
	 * ����������ʾ
	 * 
	 * @param context
	 * @param messageVos
	 * @throws BusinessException
	 */
	public static void showBatchResults(LoginContext context, MessageVO[] messageVos) throws BusinessException {
		if (ArrayUtils.isEmpty(messageVos))
			return;

		boolean bHasSuccess = false;
		boolean bHasFail = false;
		StringBuilder sbValue = new StringBuilder();
		for (MessageVO messagevo : messageVos) {
			if (messagevo == null)
				continue;
			if (messagevo.isSuccess()) {
				bHasSuccess = true;
			} else {
				bHasFail = true;
				sbValue.append(messagevo.toString()).append("\r\n");
			}
		}

		if (sbValue.length() > 0) {
			sbValue.delete(sbValue.length() - 2, sbValue.length());
		}

		String operationName = ActionUtils.getOperationName(messageVos[0].getMessageType());
		String title = null;
		if (bHasSuccess) {
			title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0", null, "02011000-0039", null,
					new String[] { operationName });
			/** @* res* "�ɹ���" */
			;

			// title = operationName +
			// nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0",
			// "02011000-0039")
		} else {
			title = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0", null, "02011000-0040", null,
					new String[] { operationName });
			/** @* res* ""ʧ�ܣ�" */
			;
			// title = operationName +
			// nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0",
			// "02011000-0040")
			/*																		 */
		}
		if (!bHasFail && bHasSuccess) {// ȫ���ɹ�
			ShowStatusBarMsgUtil.showStatusBarMsg(title, context);
		} else {
			ShowStatusBarMsgUtil.showErrorMsg(title, sbValue.toString(), context);
		}
	}

	/**
	 * ��������ϲ�,������ִ�гɹ�vo�ļ���
	 * 
	 * @param msgs
	 * @param msgReturn
	 * @return
	 * @throws BusinessException
	 */
	public static List<AggregatedValueObject> combineMsgs(MessageVO[] msgs, MessageVO[] msgReturn)
			throws BusinessException {
		List<AggregatedValueObject> resultVos = new ArrayList<AggregatedValueObject>();
		if (msgReturn != null) {
			for (int i = 0; i < msgReturn.length; i++) {
				if (msgReturn[i] != null && msgReturn[i].isSuccess()) {
					resultVos.add(msgReturn[i].getSuccessVO());
				}

				for (int j = 0; j < msgs.length; j++) {
					if (msgReturn[i] != null
							&& msgs[j] != null
							&& msgs[j].getSuccessVO().getParentVO().getPrimaryKey()
									.equals(msgReturn[i].getSuccessVO().getParentVO().getPrimaryKey())) {
						msgs[j] = msgReturn[i];
					}
				}
			}
		}

		return resultVos;
	}

	public static void setPkOrg(AbstractRefModel model, String pkOrg) {
		if (model.getPk_org() != null && !model.getPk_org().equals(pkOrg)) {
			model.setPk_org(pkOrg);
		}
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
				// PsndocVO[] persons =
				// NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(
				// new String[] { pk_psndoc }, new String[] { PsndocVO.PK_ORG,
				// PsndocVO.PK_GROUP });
				PsndocVO person = CacheUtil.getVOByPk(PsndocVO.class, pk_psndoc);
				// ��Ա������֯
				if (person != null) {
					pk_psngroup = person.getPk_group();
					instance.putClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + getPK_group(), pk_psngroup);
					instance.putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group(), person.getPk_org());
				}
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
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}

		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		String pk_org = (String) instance.getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group());
		if (pk_org == null) {
			try {
				// PsndocVO[] persons =
				// NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(
				// new String[] { pk_psndoc }, new String[] { PsndocVO.PK_ORG,
				// PsndocVO.PK_GROUP });
				PsndocVO person = CacheUtil.getVOByPk(PsndocVO.class, pk_psndoc);
				if (person != null) {
					// ��Ա������֯
					pk_org = person.getPk_org();
					instance.putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group(), pk_org);
					instance.putClientCache(PsnVoCall.GROUP_PK_ + pk_psndoc + getPK_group(), person.getPk_group());
				}
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
			final String pk_psndept = getColValue2("bd_psnjob", PsnjobVO.PK_DEPT, PsnjobVO.PK_PSNDOC, pk_psndoc,
					PsnjobVO.ISMAINJOB, "Y");
			WorkbenchEnvironment.getInstance().putClientCache(PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group(),
					pk_psndept);
		}
		return (String) WorkbenchEnvironment.getInstance().getClientCache(
				PsnVoCall.DEPT_PK_ + pk_psndoc + getPK_group());
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
		return ErUiUtil.getPsnDocInfoById(value);
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
	 * ���˽����� ���⴦��
	 * 
	 * @param panel
	 * @param jkbxr
	 * @param billtype
	 * @param dwbm
	 * @throws BusinessException
	 */
	public static void initSqdlr(BillForm editor, BillItem jkbxr, String billtype, String dwbm, UFDate billDate)
			throws BusinessException {
		if (jkbxr == null) {
			return;
		}
		String refType = jkbxr.getRefType();
		if (refType == null) {
			return;
		}

		UIRefPane refPane = (UIRefPane) jkbxr.getComponent();
		final AbstractRefGridTreeModel model = (AbstractRefGridTreeModel) refPane.getRefModel();

		if (jkbxr == null || dwbm == null) {
			model.setWherePart("1=0");
			return;
		}

		String pk_psndoc = getPk_psndoc(ErUiUtil.getPk_user());
		if (pk_psndoc == null) {
			model.setWherePart("1=0");
			return;
		}

		if (billDate == null) {
			billDate = ErUiUtil.getSysdate();
		}

		String roleSql = null;
		if (WorkbenchEnvironment.getInstance().getClientCache(RoleVoCall.PK_ROLE_IN_SQL_BUSI + getPK_group()) != null) {
			roleSql = (String) WorkbenchEnvironment.getInstance().getClientCache(
					RoleVoCall.PK_ROLE_IN_SQL_BUSI + getPK_group());
		}

		final String wherePart = ErUtil.getAgentWhereString(pk_psndoc, roleSql, billtype, ErUiUtil.getPk_user(),
				billDate.toString(), dwbm);

		String newWherePart = "1=1 " + wherePart;

		model.setPk_org(dwbm);
		model.setWherePart(newWherePart);
		// ����������
		model.setFilterCommonDataVec(new IFilterCommonDataVec() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void filterCommonDataVec(Vector vec) {
				if (vec == null || vec.isEmpty()) {
					return;
				}
				String sql = model.getRefSql();
				Vector<Vector<String>> vers = (Vector<Vector<String>>) DBCacheFacade.getFromDBCache(sql);
				if (vers == null || vers.isEmpty()) {
					vec.removeAllElements();
					return;
				}

				Set<String> jkbxrdata = new HashSet<String>();
				for (Vector<String> ve : vers) {
					jkbxrdata.add(ve.get(2));
				}
				Vector removed = new Vector();
				for (Object data : vec) {
					Vector<Object> ve = (Vector<Object>) data;
					String pk_psndoc = (String) ve.get(2);
					if (jkbxrdata.contains(pk_psndoc))
						continue;
					removed.addElement(ve);
				}
				if (removed.size() > 0) {
					vec.removeAll(removed);
				}
			}
		});
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

	/**
	 * ��ȡ���Ż����ܷ�༭״̬
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @return
	 */
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

	/**
	 * ��ȡ��֯�����ܷ�༭״̬
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @return
	 */
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

	/**
	 * ��ȡȫ�ֻ����ܷ�༭״̬
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @return
	 */
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
}
