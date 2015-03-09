package nc.arap.mobile.itf;

import nc.vo.pub.BusinessException;

public interface IPasswordValidateCtrl {
	/**
	 * 获取图片验证码
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
	/**
	 * 更改密码
	 */
	public String updatePassword(String userid,String password1,String password2) throws BusinessException;


}
