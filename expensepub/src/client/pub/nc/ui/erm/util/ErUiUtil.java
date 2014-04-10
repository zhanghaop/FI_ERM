package nc.ui.erm.util;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;

import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.individuation.property.pub.IndividuationManager;
import nc.itf.erm.prv.IErmBsCommonService;
import nc.itf.fi.pub.Currency;
import nc.pubitf.accperiod.AccountCalendar;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.org.cache.IOrgUnitPubService_C;
import nc.pubitf.setting.defaultdata.OrgSettingAccessor;
import nc.pubitf.uapbd.IPsndocPubService;
import nc.ui.arap.bx.remote.IPermissionOrgVoCallConst;
import nc.ui.arap.bx.remote.PsnVoCall;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.bd.ref.IFilterStrategy;
import nc.vo.erm.common.MessageVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.fipub.utils.uif2.FiUif2MsgUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
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
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getPermissionOrgMap(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_MAP + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
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

    public static void setRefFilterPks(AbstractRefModel refModel,
            String[] pk_vids) {
        setRefFilterPks(refModel, pk_vids, IFilterStrategy.INSECTION);
    }

    public static void setRefFilterPks(AbstractRefModel refModel,
            String[] pk_vids, int filterStrategy) {
        @SuppressWarnings("rawtypes")
        Vector vector = refModel.getSelectedData();
        refModel.setSelectedData(null);
        refModel.setFilterPks(pk_vids);
        refModel.setSelectedData(vector);
        refModel.fireChange();
    }

	/**
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgs(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null){
			try {
				Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class).getPermissonOrgMapCall(ErUiUtil.getPk_user(), nodeCode,
						ErUiUtil.getPK_group());
				values = map.values().toArray(new String[0]);
				WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);
				
				String orgVCacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
				WorkbenchEnvironment.getInstance().putClientCache(orgVCacheKey, map.keySet().toArray(new String[0]));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return values;
	}

	/**
	 * 返回当前登录用户的功能节点的权限
	 * 
	 * @author chendya
	 * @param nodeCode
	 * @return
	 */
	public static String[] getPermissionOrgVs(String nodeCode) {
		final String cacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null){
			try {
				Map<String, String> map = NCLocator.getInstance().lookup(IErmBsCommonService.class).getPermissonOrgMapCall(ErUiUtil.getPk_user(), nodeCode,
						ErUiUtil.getPK_group());
				values = map.keySet().toArray(new String[0]);
				WorkbenchEnvironment.getInstance().putClientCache(cacheKey, values);
				
				String orgCacheKey = nodeCode + IPermissionOrgVoCallConst.PERMISSION_PK_ORG + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
				WorkbenchEnvironment.getInstance().putClientCache(orgCacheKey, map.values().toArray(new String[0]));
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		return values;
	}

	/**
	 * 返回当前登录用户的功能节点的权限  多版本
	 * 
	 * @param nodeCode
	 * @param date
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public static String[] getPermissionOrgVs(LoginContext context,UFDate date) {
		String nodeCode = context.getNodeCode();
		if(nodeCode .equals(BXConstans.BXINIT_NODECODE_G)){
			nodeCode = BXConstans.BXINIT_NODECODE_U;
		}
        final String cacheKey = nodeCode + date.toStdString()
                + IPermissionOrgVoCallConst.PERMISSION_PK_ORG_V
                + ErUiUtil.getPk_user() + ErUiUtil.getPK_group();
		String[] values = (String[]) WorkbenchEnvironment.getInstance().getClientCache(cacheKey);
		if (values == null){
			try {
				String[] permissionOrgs = null;
				if(BXConstans.BXINIT_NODECODE_G.equals(context.getNodeCode())){
					permissionOrgs = getPermissionOrgs(nodeCode);
				}else{
					permissionOrgs = context.getPkorgs();
				}
				
				if(!ArrayUtils.isEmpty(permissionOrgs)){
                    HashMap<String, String> newVIDSByOrgIDSAndDate = NCLocator
                            .getInstance().lookup(IOrgUnitPubService_C.class)
                            .getNewVIDSByOrgIDSAndDate(permissionOrgs, date);
					values = newVIDSByOrgIDSAndDate.values().toArray(new String[0]);
				}
				
				if(values == null){
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
	 * 根据单据类型别名获取单据类型名称
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
	 * 返回个性化中心设置的业务单元,没有设置，返回null
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
	 * 根据key返回个性化中心设置的value
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	private static String getSettingValue(String key) throws BusinessException {
		return IndividuationManager.getIndividualSetting("nc.individuation.defaultData.DefaultConfigPage", false).getString(key);
	}

	public static String getPK_group() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getPk_group();
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
		return OrgSettingAccessor.getDefaultOrgUnit(pk_user, pk_group);
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
		return WorkbenchEnvironment.getInstance().getClientCache(key);
	}

	/**
	 * 返回指定对应的集团的名称
	 * 
	 * @return
	 */
	public static String getGroupName() {
		return WorkbenchEnvironment.getInstance().getGroupVO().getName();
	}

	/**
	 * 返回当前登录的用户
	 * 
	 * @return
	 */
	public static String getPk_user() {
		return WorkbenchEnvironment.getInstance().getLoginUser().getCuserid();
	}
	
	public static UserVO getUserVo(){
		return WorkbenchEnvironment.getInstance().getLoginUser();
	}

	
	/**
	 * 返回当前登陆用户所对应的业务员
	 * 
	 * @return
	 */
	public static String getPk_psndoc() {
		if (WorkbenchEnvironment.getInstance().getLoginUser().getBase_doc_type().equals(UserIdentityTypeEnumFactory.TYPE_PERSON)) {
			return WorkbenchEnvironment.getInstance().getLoginUser().getPk_base_doc();
		} else {
			return null;
		}

	}

	

	/**
	 * 返回当前会计年度
	 * 
	 * @return 暂时下面方法取？？
	 * 
	 */
	public static String getAccountYear() {
		return (Integer.valueOf(WorkbenchEnvironment.getServerTime().getYear())).toString();
	}

	/**
	 * 返回当前日期
	 * 
	 * @return
	 * 
	 */
	public static UFDate getSysdate() {
		return WorkbenchEnvironment.getServerTime().getDate();
	}

	/**
	 * 返回业务日期
	 * 
	 * @return
	 * 
	 */
	public static UFDate getBusiDate() {
		return WorkbenchEnvironment.getInstance().getBusiDate();
	}

	/**
	 * 返回当前日期时间
	 * 
	 * @return
	 * 
	 */
	public static UFDateTime getSysdatetime() {
		return WorkbenchEnvironment.getServerTime();
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
		// 原币对本币汇率精度
		int iDecimalDigits = 0;
		try {
			iDecimalDigits = Currency.getRateDigit(pk_org, currency, Currency.getGlobalCurrPk(pk_org));
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return iDecimalDigits;
	}

	
	/**
	 * 消除字符串中多余的符号
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
	 * 返回组织的会计日历 modified by chendya 不频繁变化的数据,修改为缓存方式,减少远程过程调用次数
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
	 * 前台公式
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
	 * 前台公式
	 * 
	 * @author chendya
	 */
	public static String getColValue2(String table, String column, Object pk1, Object value1, Object pk2, Object value2) {
		FormulaParse parser = getUIFormularParser();
		parser.setExpress("getColValue2" + "(" + table + "," + column + "," + pk1 + "," + "var1" + "," + pk2 + "," + "var2" + ")");
		parser.addVariable("var1", value1);
		parser.addVariable("var2", value2);
		return parser.getValue();
	}

	/**
	 * 前台公式解析器
	 * 
	 * @author chendya
	 * @return
	 */
	private static FormulaParse getUIFormularParser() {
		return new FormulaParse();
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
		String pk_org = getDefaultOrgUnit();
		if (pk_org == null || pk_org.length() == 0) {
			// 个性化中心取不到，则取当前登陆人所属组织作为默认业务单元
			pk_org = getPsnPk_org(getPk_psndoc());
		}
		return pk_org;
	}
	
	/**
	 * 获取当前登录用户所属组织
	 * @return
	 */
	public static String getDefaultPsnOrg(){
		return getPsnPk_org(getPk_psndoc());
	}
	/**
	 * 人员所属组织
	 * 
	 * @param pk_psndoc
	 *            人员主键
	 * @return
	 */
	public static String getPsnPk_org(String pk_psndoc) {
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}
		if (WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group()) == null) {
			try {
				PsndocVO[] persons = NCLocator.getInstance().lookup(IPsndocPubService.class).queryPsndocByPks(new String[] { pk_psndoc },
						new String[] { PsndocVO.PK_ORG });
				// 人员所属组织
				String pk_org = persons[0].getPk_org();
				WorkbenchEnvironment.getInstance().putClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group(), pk_org);
			} catch (Exception e) {
				ExceptionHandler.consume(e);
			}
		}
		return (String) WorkbenchEnvironment.getInstance().getClientCache(PsnVoCall.FIORG_PK_ + pk_psndoc + getPK_group());
	}
	

	/**
	 * 反射调用，显示类似Uif2类似的错误消息
	 * 
	 * @author chendya
	 */
	public static void showUif2DetailMessage(JComponent comp, String statusBarErrMsg, java.lang.Throwable ex) {
		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, ex);
	}

	/**
	 * 反射调用，显示类似Uif2类似的错误消息 对话框中报错
	 * 
	 * @author chenshuai
	 */
	public static void showUif2DetailMessage(Component comp, String statusBarErrMsg, String msg) {
		FiUif2MsgUtil.showUif2DetailMessage(comp, statusBarErrMsg, msg);
	}

    /**
     * 批处理结果显示
     * @param context
     * @param messageVos
     * @throws BusinessException 
     */
    public static void showBatchResults(LoginContext context, MessageVO[] messageVos) throws BusinessException {
        if (ArrayUtil.isEmpty(messageVos))
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
        
        if(sbValue.length() > 0){
        	sbValue.delete(sbValue.length() - 2, sbValue.length());
        }
        
        String operationName = ActionUtils.getOperationName(messageVos[0].getMessageType());
        String title = null;
		if (bHasSuccess) {
			title = operationName + nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0039")/*
																											 * @
																											 * res
																											 * "成功！"
																											 */;
		} else {
			title = operationName + nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0040")/*
																											 * @
																											 * res
																											 * "失败！"
																											 */;
		}
        if (!bHasFail && bHasSuccess) {//全部成功
            ShowStatusBarMsgUtil.showStatusBarMsg(title, context);
        } else {
            ShowStatusBarMsgUtil.showErrorMsg(title, sbValue.toString(), context);
        }
    }
    
    /**
	 * 将结果集合并,并返回执行成功vo的集合
	 *
	 * @param msgs
	 * @param msgReturn
	 * @return
	 * @throws BusinessException
	 */
	public static List<AggregatedValueObject> combineMsgs(MessageVO[] msgs, MessageVO[] msgReturn) throws BusinessException {
		List<AggregatedValueObject> resultVos = new ArrayList<AggregatedValueObject>();
		if (msgReturn != null) {
			for (int i = 0; i < msgReturn.length; i++) {
				if (msgReturn[i] != null && msgReturn[i].isSuccess()) {
					resultVos.add(msgReturn[i].getSuccessVO());
				}

				for (int j = 0; j < msgs.length; j++) {
					if (msgReturn[i] != null &&  msgs[j]!=null
							&& msgs[j].getSuccessVO().getParentVO().getPrimaryKey().equals(msgReturn[i].getSuccessVO().getParentVO().getPrimaryKey())) {
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
}
