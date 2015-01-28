package nc.arap.mobile.impl;

import nc.arap.mobile.itf.ICommonUtilCtrl;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.itf.print.out.pdfhtml.IOutPdfHtml;
import nc.itf.uap.rbac.IUserLockService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelFinder;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelVO;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class CommonMobileCtrlImpl implements ICommonUtilCtrl{
	
	private boolean changeUserPassword(UserVO user,String password)
	{
//	    try {
//	      user = SFServiceFacility.getUserQueryService().findUserByCode(user.getUser_code(), WorkbenchEnvironment.getInstance().getDSName());
//	    }
//	    catch (Exception e) {
//	      Logger.error(e.getMessage(), e);
//	      return false;
//	    }
	    String expresslyPwd = password.trim();
	    String stmp = EnvironmentInit.getServerTime().toString();
	    user.setPwdparam(stmp.substring(0, stmp.indexOf(" ")).trim());
	    try {
	      IUserPasswordManage passWordManageService = (IUserPasswordManage)NCLocator.getInstance().lookup(IUserPasswordManage.class);
	      passWordManageService.changeUserPassWord(user, expresslyPwd);
	    }
	    catch (Exception e) {
	      Logger.error(e.getMessage(), e);
	      return false;
	    }
	    return true;
	  }
	
	@Override
	public String changePassword(String pwdErrorCount,String userid, String password,
			String password1, String password2) throws BusinessException {
		JSONObject retJson = new JSONObject();
		//�����û�
		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(user == null?null:user.getPk_group());
	    
	    PasswordSecurityLevelVO pwdLevel = PasswordSecurityLevelFinder.getPWDLV(user);
	    
	    String oldPassword = password.trim();
	    
	    password = password1.trim();
	    
	    password2 = password2.trim();
	    
	    if (StringUtil.isEmpty(password)) {
	      return "�����벻��Ϊ��";
	    }
	    if (password.equals(oldPassword)) {
	      return "�����벻�����������ͬ";
	    }
	    IUserPasswordManage userPWDManage = (IUserPasswordManage)NCLocator.getInstance().lookup(IUserPasswordManage.class);
	    boolean isOldPwdCorrect = userPWDManage.checkUserPassWord(user.getPrimaryKey(), oldPassword);
	    if (isOldPwdCorrect) {
	    	if(changeUserPassword(user,password)){
	    		try {
					retJson.put("success", "true");
					retJson.put("message", "�޸�����ɹ���");
				} catch (JSONException e) {
					
				}
			    return retJson.toString();
	    	}
	    	try {
				retJson.put("success", "false");
				retJson.put("message", "�޸������������ϵ����Ա��");
			} catch (JSONException e) {
				
			}
		    return retJson.toString();
	    }
	    else {
	        //�������
	    	int pwdError = Integer.parseInt(pwdErrorCount);
	    	pwdError++;
		    int maxErrorCount = 3;
		    if (pwdError >= maxErrorCount) {
		      //�����û�
		      IUserLockService userLockService = (IUserLockService)NCLocator.getInstance().lookup(IUserLockService.class);
		      userLockService.updateLockedTag(user.getPrimaryKey(), true);
		      try {
				retJson.put("success", "false");
				retJson.put("pwdErrorCount", pwdError);
				retJson.put("message", "��������������룬�û���������");
			} catch (JSONException e) {
				
			}
		      return retJson.toString();
		    }
		    try {
				retJson.put("pwdErrorCount", pwdError);
				retJson.put("success", "false");
				retJson.put("message", "�������"+pwdError+"�����룬����3���û�����������");
			} catch (JSONException e) {
				
			}
		    return retJson.toString();
	    }
	    
	}

	/**
	 * �����û�id ���ܺ�, ��ȡ��Ȩ�޵Ľڵ�.
	 * 
	 * @param userid
	 * @param funccode
	 * @param groupid
	 * @return
	 * @throws BusinessException
	 *             ���� zhangg
	 */
	public static String[] getUserPermissionFuncNode(String userid,String groupid) throws BusinessException {
		return NCLocator.getInstance().lookup(IFunctionPermissionPubService.class)
		.getUserPermissionFuncNode(userid, groupid);
	}

	@Override
	public String getBillPdfByPk(String billpk, String djlxbm, String funcode,
			String userid) throws BusinessException {
		//�����û�
		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(user == null?null:user.getPk_group());
		IOutPdfHtml IoutPdfHtml = (IOutPdfHtml)NCLocator.getInstance().lookup(IOutPdfHtml.class);
		String rs = IoutPdfHtml.generateBillHtml(funcode, billpk, djlxbm,user.getPk_group());
		return rs;
	}
	
	
	
}
