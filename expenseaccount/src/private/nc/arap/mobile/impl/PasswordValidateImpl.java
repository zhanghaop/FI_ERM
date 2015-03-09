package nc.arap.mobile.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import nc.arap.mobile.itf.IPasswordValidateCtrl;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.erm.mobile.tools.CheckMobileAndEmail;
import nc.erm.mobile.tools.PassWordWizardContent;
import nc.erm.mobile.util.RandomValidateCode;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.vo.pub.BusinessException;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;
import nc.vo.sm.UserVO;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import sun.misc.BASE64Encoder;

public class PasswordValidateImpl implements IPasswordValidateCtrl{
	
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

	//根据用户输入的用户名/邮箱/手机对用户进行校验
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

	//发送验证码到手机/邮箱
	@Override
	public String sendCaptcha(String userid, String usercode, String telephone,
			String email,String value) throws BusinessException {
		PassWordWizardContent wizard = new PassWordWizardContent();
		if("1".equals(value)){
			//发送到手机
			return wizard.doCaptchaMobile(usercode, telephone);
		}else if("2".equals(value)){
			//发送到邮箱
			return wizard.doCaptchaEmail(usercode, email);
		}
		return null;
	}

	@Override
	public String updatePassword(String userid, String password1,
			String password2) throws BusinessException {
		UserVO user = NCLocator.getInstance().lookup(IUserManageQuery.class).getUser(userid);
		// 修改密码
		String expresslyPwd = password1.trim();
	    String stmp = EnvironmentInit.getServerTime().toString();
	    user.setPwdparam(stmp.substring(0, stmp.indexOf(" ")).trim());
	    try {
	      IUserPasswordManage passWordManageService = (IUserPasswordManage)NCLocator.getInstance().lookup(IUserPasswordManage.class);
	      passWordManageService.changeUserPassWord(user, expresslyPwd);
	    }
	    catch (Exception e) {
	    	/* 修改密码错误，请联系管理员！ */
			Logger.error("修改密码错误");/*-=notranslate=-*/ 
			Logger.error(e.getMessage(), e);
	      return "";
	    }
	    return "";
	}
	

}
