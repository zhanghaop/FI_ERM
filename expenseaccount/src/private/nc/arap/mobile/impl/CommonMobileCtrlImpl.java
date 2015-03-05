package nc.arap.mobile.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import nc.arap.mobile.itf.ICommonUtilCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.erm.mobile.tools.CheckMobileAndEmail;
import nc.erm.mobile.tools.PassWordWizardContent;
import nc.erm.mobile.util.RandomValidateCode;
import nc.itf.print.out.pdfhtml.IOutPdfHtml;
import nc.itf.uap.rbac.IUserLockService;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelFinder;
import nc.vo.uap.rbac.userpassword.PasswordSecurityLevelVO;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sun.misc.BASE64Encoder;

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
	    		try {
					retJson.put("success", "true");
					retJson.put("message", "修改密码成功！");
				} catch (JSONException e) {
					
				}
			    return retJson.toString();
	    	}
	    	try {
				retJson.put("success", "false");
				retJson.put("message", "修改密码出错，请联系管理员！");
			} catch (JSONException e) {
				
			}
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
		      try {
				retJson.put("success", "false");
				retJson.put("pwdErrorCount", pwdError);
				retJson.put("message", "您已输错三次密码，用户已锁定！");
			} catch (JSONException e) {
				
			}
		      return retJson.toString();
		    }
		    try {
				retJson.put("pwdErrorCount", pwdError);
				retJson.put("success", "false");
				retJson.put("message", "您已输错"+pwdError+"次密码，超过3次用户将被锁定！");
			} catch (JSONException e) {
				
			}
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
		return NCLocator.getInstance().lookup(IFunctionPermissionPubService.class)
		.getUserPermissionFuncNode(userid, groupid);
	}

	@Override
	public String getBillPdfByPk(String billpk, String djlxbm, String funcode,
			String userid) throws BusinessException {
		//查找用户
		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		InvocationInfoProxy.getInstance().setUserId(userid);
		InvocationInfoProxy.getInstance().setGroupId(user == null?null:user.getPk_group());
		IOutPdfHtml IoutPdfHtml = (IOutPdfHtml)NCLocator.getInstance().lookup(IOutPdfHtml.class);
		String rs = IoutPdfHtml.generateBillHtml(funcode, billpk, djlxbm,user.getPk_group());
		return rs;
	}
	
	/**
	 * 获取验证码
	 */
	@Override
	public String getValidateCode(String userid) throws BusinessException {
		//查找用户
//		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
//		InvocationInfoProxy.getInstance().setUserId(userid);
//		InvocationInfoProxy.getInstance().setGroupId(user == null?null:user.getPk_group());
		
		JSONObject retJson = new JSONObject();
		RandomValidateCode vCode = new RandomValidateCode(120,40,5,50);  
		try {
			retJson.put("code", vCode.getCode());
			OutputStream out = new ByteArrayOutputStream();
			vCode.write(out);
			byte[] downloaded = ((ByteArrayOutputStream) out).toByteArray(); // 附件内容
			BASE64Encoder encoder = new BASE64Encoder();
			String content = encoder.encodeBuffer(downloaded);
			retJson.put("content", content); 
		} catch (JSONException e) {
			ExceptionUtils.wrappBusinessException("添加数据到json异常：" + e.getMessage());
		} catch (IOException e) {
			ExceptionUtils.wrappBusinessException("生成验证码图片异常：" + e.getMessage());
		} 
		return retJson.toString();
	}

	@Override
	public String getTelAndEmailByUser(String userid, String usercode)
			throws BusinessException {
		final JSONObject retJson = new JSONObject();
		String sql;
		if(CheckMobileAndEmail.checkEmail(usercode)){
			sql = "select cuserid,mobile,email,code,user_code from bd_psndoc,sm_user "
					+ "where bd_psndoc.pk_psndoc = sm_user.pk_psndoc and bd_psndoc.email='"
					+ usercode +"'";
		}else if(CheckMobileAndEmail.isMobileNO(usercode)){
			sql = "select cuserid,mobile,email,code,user_code from bd_psndoc,sm_user " +
					"where bd_psndoc.pk_psndoc = sm_user.pk_psndoc and bd_psndoc.mobile='"
					+ usercode +"'";
		}else{
			sql = "select cuserid,mobile,email,code,user_code from bd_psndoc,sm_user " +
					"where bd_psndoc.pk_psndoc = sm_user.pk_psndoc and sm_user.user_code='"
					+ usercode +"'";
		}
		BaseDAO dao = new BaseDAO();
		try {
			dao.executeQuery(sql, new BaseProcessor(){
				private static final long serialVersionUID = 1L;

				@Override
				public Object processResultSet(ResultSet resultset)
						throws SQLException {
					 while (resultset.next()) {
						 String mobile = (String) resultset.getObject(2);
						 String email = (String) resultset.getObject(3);
						 String user_code = (String) resultset.getObject(5);
						 try {
							retJson.put("usercode", user_code);
							retJson.put("telephone", mobile);
							String sub = mobile.substring(3, 7);
							retJson.put("info", mobile.replaceFirst(sub, "****"));
							retJson.put("email", email);
						} catch (JSONException e) {
							ExceptionUtils.wrappBusinessException("添加数据到json异常：" + e.getMessage());
						}
					}
					return null;
				}
				
			});
			
		} catch (DAOException e) {
			ExceptionUtils.wrappBusinessException("添加数据到json异常：" + e.getMessage());
		}
		return retJson.toString();
	}

	@Override
	public String sendCaptcha(String userid, String usercode, String telephone,
			String email,String value) throws BusinessException {
		PassWordWizardContent wizard = new PassWordWizardContent();
		if("1".equals(value)){
			wizard.doCaptchaMobile(usercode, telephone);
		}else if("2".equals(value)){
			wizard.doCaptchaEmail(usercode, email);
		}
		return value;
	}
	
	
}
