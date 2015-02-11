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
		String pk_org = null;
		try {
			PsndocVO person = CacheUtil.getVOByPk(PsndocVO.class, pk_psndoc);
			if (person != null) {
				// ��Ա������֯
				pk_org = person.getPk_org();
			}
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
		return pk_org;

	}
	/**
	 * ���ص�ǰ�û��ڵ�ǰ���ŵ�Ĭ��ҵ��Ԫ 1.������Ի�����������ҵ��Ԫ�������ȷ��ظ��Ի��������õ�ҵ��Ԫ
	 * 2.������Ի�����δ���ã���ȡ��ǰ��¼�û�������֯��ΪĬ��ҵ��Ԫ
	 * 
	 * @author chendya
	 * @throws BusinessException 
	 * 
	 * @throws Exception
	 */
	public static String getBXDefaultOrgUnit() throws BusinessException {
		String pk_org = getSettingValue("org_df_biz");
		if (pk_org == null || pk_org.length() == 0) {
			// ���Ի�����ȡ��������ȡ��ǰ��½��������֯��ΪĬ��ҵ��Ԫ
			pk_org = getPsnPk_org(getPk_psndoc());
		}
		return pk_org;
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
	/**
	 * ���ص�ǰ��½�û�����Ӧ��ҵ��Ա
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
