package nc.arap.mobile.impl;

import nc.arap.mobile.itf.ICommonUtilCtrl;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.uap.sf.facility.SFServiceFacility;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.itf.uap.rbac.IUserLockService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.am.proxy.AMProxy;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelFinder;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelVO;
import uap.json.JSONObject;

public class CommonMobileCtrlImpl implements ICommonUtilCtrl{
	
	private boolean changeUserPassword(UserVO user,String password)
	{
	    try {
	      user = SFServiceFacility.getUserQueryService().findUserByCode(user.getUser_code(), WorkbenchEnvironment.getInstance().getDSName());
	    }
	    catch (Exception e) {
	      Logger.error(e.getMessage(), e);
	      return false;
	    }
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
		//查找用户
		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(user == null?null:user.getPk_group());
	    
	    PasswordSecurityLevelVO pwdLevel = PasswordSecurityLevelFinder.getPWDLV(user);
	    
	    String oldPassword = password.trim();
	    
	    password = password1.trim();
	    
	    password2 = password2.trim();
	    
	    if (StringUtil.isEmpty(password)) {
	      return "旧密码不能为空";
	    }
	    if (password.equals(oldPassword)) {
	      return "新密码不能与旧密码相同";
	    }
	    IUserPasswordManage userPWDManage = (IUserPasswordManage)NCLocator.getInstance().lookup(IUserPasswordManage.class);
	    boolean isOldPwdCorrect = userPWDManage.checkUserPassWord(user.getPrimaryKey(), oldPassword);
	    if (isOldPwdCorrect) {
	    	if(changeUserPassword(user,password)){
	    		retJson.put("success", "true");
			    retJson.put("message", "修改密码成功！");
			    return retJson.toString();
	    	}
	    	retJson.put("success", "false");
		    retJson.put("message", "修改密码出错，请联系管理员！");
		    return retJson.toString();
	    }
	    else {
	        //密码错误
	    	int pwdError = Integer.parseInt(pwdErrorCount);
	    	pwdError++;
		    int maxErrorCount = 3;
		    if (pwdError >= maxErrorCount) {
		      //锁定用户
		      IUserLockService userLockService = (IUserLockService)NCLocator.getInstance().lookup(IUserLockService.class);
		      userLockService.updateLockedTag(user.getPrimaryKey(), true);
		      retJson.put("success", "false");
		      retJson.put("pwdErrorCount", pwdError);
		      retJson.put("message", "您已输错三次密码，用户已锁定！");
		      return retJson.toString();
		    }
		    retJson.put("pwdErrorCount", pwdError);
		    retJson.put("success", "false");
		    retJson.put("message", "您已输错"+pwdError+"次密码，超过3次用户将被锁定！");
		    return retJson.toString();
	    }
	    
	}

	/**
	 * 根据用户id 功能号, 获取有权限的节点.
	 * 
	 * @param userid
	 * @param funccode
	 * @param groupid
	 * @return
	 * @throws BusinessException
	 *             作者 zhangg
	 */
	public static String[] getUserPermissionFuncNode(String userid,String groupid) throws BusinessException {
		return AMProxy.lookup(IFunctionPermissionPubService.class)
		.getUserPermissionFuncNode(userid, groupid);
	}
}
