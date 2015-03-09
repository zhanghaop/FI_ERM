package nc.arap.mobile.itf;

import nc.vo.pub.BusinessException;

public interface IPasswordValidateCtrl {
	/**
	 * ��ȡͼƬ��֤��
	 */
	public String getValidateCode(String userid) throws BusinessException;
	
	/**
	 * �����û���Ϣ��ȡ�绰���������
	 */
	public String getTelAndEmailByUser(String userid,String usercode) throws BusinessException;

	/**
	 * ������֤�뵽�ֻ�/����
	 */
	public String sendCaptcha(String userid,String usercode,String telephone,String email,String value) throws BusinessException;
	/**
	 * ��������
	 */
	public String updatePassword(String userid,String password1,String password2) throws BusinessException;


}
