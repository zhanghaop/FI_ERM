package nc.erm.mobile.util;

import nc.bs.erm.util.CacheUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.individuation.property.pub.IndividuationManager;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.vo.bd.psn.PsndocVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.sm.UserVO;
import nc.vo.sm.enumfactory.UserIdentityTypeEnumFactory;

public class ErMobileUtil {
	
	/**
	 * 人员所属组织，连带缓存组织
	 * 
	 * @param pk_psndoc
	 *            人员主键
	 * @return
	 */
	public static String getPsnPk_org(String pk_psndoc) {
		if (StringUtil.isEmpty(pk_psndoc)) {
			return null;
		}
		String pk_org = null;
		try {
			PsndocVO person = CacheUtil.getVOByPk(PsndocVO.class, pk_psndoc);
			if (person != null) {
				// 人员所属组织
				pk_org = person.getPk_org();
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		return pk_org;

	}
	/**
	 * 返回当前用户在当前集团的默认业务单元 1.如果个性化中心设置了业务单元，则优先返回个性化中心设置的业务单元
	 * 2.如果个性化中心未设置，则取当前登录用户所属组织作为默认业务单元
	 * 
	 * @author chendya
	 * @throws BusinessException 
	 * 
	 * @throws Exception
	 */
	public static String getBXDefaultOrgUnit() throws BusinessException {
		String pk_org = getSettingValue("org_df_biz");
		if (pk_org == null || pk_org.length() == 0) {
			// 个性化中心取不到，则取当前登陆人所属组织作为默认业务单元
			pk_org = getPsnPk_org(getPk_psndoc());
		}
		return pk_org;
	}
	/**
	 * 根据key返回个性化中心设置的value
	 * 
	 * @param key
	 * @return
	 * @throws BusinessException
	 */
	private static String getSettingValue(String key) throws BusinessException {
		return IndividuationManager.getIndividualSetting("nc.individuation.defaultData.DefaultConfigPage", false)
				.getString(key);
	}
	/**
	 * 返回当前登陆用户所对应的业务员
	 * 
	 * @return
	 * @throws BusinessException 
	 */
	public static String getPk_psndoc() throws BusinessException {
		String userid =InvocationInfoProxy.getInstance().getUserId();
		UserVO uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		if (uservo.getBase_doc_type()
				.equals(UserIdentityTypeEnumFactory.TYPE_PERSON)) {
			return uservo.getPk_base_doc();
		} else {
			return null;
		}

	}
	public static UFDate getSysdate() {
		return new UFDateTime(System.currentTimeMillis()).getDate();
	}
	
}
