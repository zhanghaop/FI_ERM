package nc.arap.mobile.itf;

import nc.vo.pub.BusinessException;

public interface ICommonUtilCtrl {
	/**
	 * 修改密码
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public abstract String changePassword(String pwdErrorCount,String userid,
			String password,String password1,String password2) throws BusinessException;
	
	/**
	 * 查询单据生成的pdf
	 */
	public String getBillPdfByPk(String billpk,String djlxbm,String funcode,String userid) throws BusinessException;

}
