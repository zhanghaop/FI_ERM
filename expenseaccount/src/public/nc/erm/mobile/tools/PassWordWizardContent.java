package nc.erm.mobile.tools;

import java.util.Date;
import java.util.Random;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.itf.uap.rbac.userpassword.IUserPasswordManage;
import nc.message.pub.mobile.SMSAndMailUtil;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.ui.ml.NCLangRes;
import nc.vo.pub.lang.UFDate;
import nc.vo.sm.UserVO;

public class PassWordWizardContent{

	/** 界面内容是否校验通过,第一步初始化时为true，点击下一步开始变换值 **/
	private boolean isCheckPass = true;

	private String userCode = "";

	private String email = "";
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	private String mobile = "";
	
	private char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N',  'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z',  '1', '2', '3', '4', '5', '6', '7', '8', '9' };


    /**
     * 发送验证码到邮箱
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public void doCaptchaEmail(String userCode,String email){
		if (userCode.equals("") || email.equals("")) {
			/* 请填写完整信息 */
			
        } else {
			setUserCode(userCode);
			setEmail(email);
			// 获取验证码
			String captcha = getCaptcha();
			// 发送邮件,验证码不为空并发送邮件成功进入下一步
			if (captcha != null && !captcha.equals("")) {
				setClock(captcha);
			} else {
				/* 生成验证码失败 */
			}
		}
	
	}
    
    /**
     * 响应第一步操作
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public void doCaptchaMobile(String userCode,String email){
		if (userCode.equals("") || email.equals("")) {
			/* 请填写完整信息 */
			
        } else {
			setUserCode(userCode);
			setEmail(email);
			// 获取验证码
			String captcha = getCaptcha();
			// 发送邮件,验证码不为空并发送邮件成功进入下一步
			if (captcha != null && !captcha.equals("")) {
				setClock(captcha);
			} else {
				/* 生成验证码失败 */
			}
		}
	
	}
    
	/**
	 * 发送邮件
	 * 
	 * @param email
	 */
	private boolean sendEmail(String email, String captcha) {
		boolean isSuccess = true;
		MessageVO vo= new MessageVO();
		String con = "来自"+
				email.substring(0,email.indexOf("@"))+
				"验证码"+captcha+
				"有效时间1分钟";
		String end = NCLangRes.getInstance().getStrByID("loginui","retrievepwd-000044");
		String time = "&nbsp;&nbsp;&nbsp;&nbsp;"+UFDate.getDate(new Date()).toString();
		vo.setContent(con+end+time);
		vo.setContenttype("html");
		vo.setReceiver(email);
		vo.setSubject(NCLangRes.getInstance().getStrByID("loginui",
				"retrievepwd-000021"));
		NCMessage msg = new NCMessage();
		msg.setMessage(vo);

		try {
			SMSAndMailUtil.sendMailSMS(new NCMessage[]{msg});
		} catch (Exception e) {
			isSuccess = false;
			Logger.error("验证码邮件发送失败");/*-=notranslate=-*/ 
			Logger.error(e.getMessage(), e);
		}
		return isSuccess;
	}
	
	
	/**
	 * 获取验证码
	 * @param comp
	 * @param event
	 * @param preStepIndex
	 * @return
	 */
	private String getCaptcha(){
		// 生成随机数
		Random random = new Random();
		// randomCode记录随机产生的验证码
		StringBuffer randomCode = new StringBuffer();
		// 随机产生codeCount个字符的验证码。
		for (int i = 0; i < 6; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// 将产生的随机数组合在一起。
			randomCode.append(strRand);
		}
		// 将四位数字的验证码保存到Session中。
		return randomCode.toString();	
	}

	/**
	 * 发送验证码邮件并设置倒计时
	 * @param captcha
	 * @param comp
	 * @param event
	 * @param curStep
	 * @param preStepIndex
	 */
	private void setClock(String captcha) {
		if (sendEmail(email, captcha)) {
//			second.getTimer().setFlag(true);
//			second.getTimer().getTimer(Integer.parseInt(second.getMinute()),
//					Integer.parseInt(second.getSecond()));
		} else {
			/* 发送验证码失败 */
		}

	}
	
	/**
	 * 修改密码
	 * @param tcomp
	 * @param event
	 * @param curStep
	 * @param preStepIndex
	 */
	private boolean updatePwd(UserVO user,String password){
		// 修改密码
		String expresslyPwd = password.trim();
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
	      return false;
	    }
	    return true;
	}
	

	public boolean isCheckPass() {
		return isCheckPass;
	}

	public void setCheckPass(boolean isCheckPass) {
		this.isCheckPass = isCheckPass;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
