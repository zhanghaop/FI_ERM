package nc.arap.mobile.itf;

import nc.vo.pub.BusinessException;

public interface ICommonUtilCtrl {
	/**
	 * �޸�����
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public abstract String changePassword(String pwdErrorCount,String userid,
			String password,String password1,String password2) throws BusinessException;
	
	/**
	 * ��ѯ�������ɵ�pdf
	 */
	public String getBillPdfByPk(String billpk,String djlxbm,String funcode,String userid) throws BusinessException;

	/**
	 * ��ȡ��֤��
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

}
