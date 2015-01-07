package nc.arap.mobile.itf;

import nc.vo.pub.BusinessException;

public interface ICommonUtilCtrl {
	/**
	 * ÐÞ¸ÄÃÜÂë
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public abstract String changePassword(String pwdErrorCount,String userid,
			String password,String password1,String password2) throws BusinessException;
}
