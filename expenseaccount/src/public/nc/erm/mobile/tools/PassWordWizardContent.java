package nc.erm.mobile.tools;

import java.util.Date;
import java.util.Random;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Logger;
import nc.message.pub.mobile.SMSAndMailUtil;
import nc.message.vo.MessageVO;
import nc.message.vo.NCMessage;
import nc.vo.pub.lang.UFDate;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class PassWordWizardContent{

	private String userCode;

	private String email;
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
     * @throws JSONException 
     */
    public String doCaptchaEmail(String userCode,String email){
    	JSONObject retJson = new JSONObject();
    	try {
			if (userCode.equals("") || email.equals("")) {
				/* 请填写完整信息 */
				retJson.put("success", "false");
				retJson.put("message", "请填写完整信息");
	        } else {
				setUserCode(userCode);
				setEmail(email);
				// 获取验证码
				String captcha = getCaptcha();
				// 发送邮件,验证码不为空并发送邮件成功进入下一步
				if (captcha != null && !captcha.equals("")) {
					if (sendEmail(email, captcha)) {
						retJson.put("success", "true");
						retJson.put("captcha", captcha);
						UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
						retJson.put("time", busiDate.toString());
					}else{
						retJson.put("success", "false");
						retJson.put("message", "发送验证码失败"+message);
					}
				} else {
					/* 生成验证码失败 */
					retJson.put("success", "false");
					retJson.put("message", "生成验证码失败");
				}
			}
			return retJson.toString();
    	} catch (JSONException e) {
    		return retJson.toString();
    	}
	}
    
    /**
     * 给手机发送验证码
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public String doCaptchaMobile(String userCode,String mobile){
    	JSONObject retJson = new JSONObject();
    	try {
			if ("".equals(userCode) || "".equals(mobile)) {
				/* 请填写完整信息 */
				retJson.put("success", "false");
				retJson.put("message", "请填写完整信息");
	        } else {
				setUserCode(userCode);
				setMobile(mobile);
				// 获取验证码
				String captcha = getCaptcha();
				// 发送邮件,验证码不为空并发送邮件成功进入下一步
				if (captcha != null && !captcha.equals("")) {
					if(sendMessage(mobile,captcha)){
						retJson.put("success", "true");
						retJson.put("captcha", captcha);
						UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
						retJson.put("time", busiDate.toString());
					}else{
						retJson.put("success", "false");
						retJson.put("message", "发送验证码失败,"+message);
					}
				} else {
					/* 生成验证码失败 */
					retJson.put("success", "false");
					retJson.put("message", "生成验证码失败");
				}
			}
			return retJson.toString();
    	} catch (JSONException e) {
    		return retJson.toString();
    	}
	
	}
    private String message;
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
		//String end = NCLangRes.getInstance().getStrByID("loginui","retrievepwd-000044");
		String time = "&nbsp;&nbsp;&nbsp;&nbsp;"+UFDate.getDate(new Date()).toString();
		vo.setContent(con+time);
		vo.setContenttype("html");
		vo.setReceiver(email);
		vo.setSubject("来自nc验证码");
		NCMessage msg = new NCMessage();
		msg.setMessage(vo);

		try {
			SMSAndMailUtil.sendMailSMS(new NCMessage[]{msg});
		} catch (Exception e) {
			isSuccess = false;
			message = e.getMessage();
			Logger.error("验证码邮件发送失败");/*-=notranslate=-*/ 
			Logger.error(e.getMessage(), e);
		}
		return isSuccess;
	}
	
	/**
	 * 发送短信
	 * 
	 * @param email
	 */
	private boolean sendMessage(String mobile, String captcha) {
		boolean isSuccess = true;
		String content = "验证码"+captcha+"有效时间1分钟";
		try {
			SMSAndMailUtil.sendSMS(new String[]{mobile}, content);
		} catch (Exception e) {
			isSuccess = false;
			message = e.getMessage();
			if(e instanceof NullPointerException){
				message = "未设置短信服务器";
			}
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
