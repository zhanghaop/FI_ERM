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

	/**
	 * 获取验证码
	 */
	public String getValidateCode(String userid) throws BusinessException;
	
	/**
	 * 根据用户信息获取电话号码和邮箱
	 */
	public String getTelAndEmailByUser(String userid,String usercode) throws BusinessException;

	/**
	 * 发送验证码到手机/邮箱
	 */
	public String sendCaptcha(String userid,String usercode,String telephone,String email,String value) throws BusinessException;

}
