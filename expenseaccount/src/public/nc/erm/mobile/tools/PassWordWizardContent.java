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
     * ������֤�뵽����
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
				/* ����д������Ϣ */
				retJson.put("success", "false");
				retJson.put("message", "����д������Ϣ");
	        } else {
				setUserCode(userCode);
				setEmail(email);
				// ��ȡ��֤��
				String captcha = getCaptcha();
				// �����ʼ�,��֤�벻Ϊ�ղ������ʼ��ɹ�������һ��
				if (captcha != null && !captcha.equals("")) {
					if (sendEmail(email, captcha)) {
						retJson.put("success", "true");
						retJson.put("captcha", captcha);
						UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
						retJson.put("time", busiDate.toString());
					}else{
						retJson.put("success", "false");
						retJson.put("message", "������֤��ʧ��"+message);
					}
				} else {
					/* ������֤��ʧ�� */
					retJson.put("success", "false");
					retJson.put("message", "������֤��ʧ��");
				}
			}
			return retJson.toString();
    	} catch (JSONException e) {
    		return retJson.toString();
    	}
	}
    
    /**
     * ���ֻ�������֤��
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public String doCaptchaMobile(String userCode,String mobile){
    	JSONObject retJson = new JSONObject();
    	try {
			if ("".equals(userCode) || "".equals(mobile)) {
				/* ����д������Ϣ */
				retJson.put("success", "false");
				retJson.put("message", "����д������Ϣ");
	        } else {
				setUserCode(userCode);
				setMobile(mobile);
				// ��ȡ��֤��
				String captcha = getCaptcha();
				// �����ʼ�,��֤�벻Ϊ�ղ������ʼ��ɹ�������һ��
				if (captcha != null && !captcha.equals("")) {
					if(sendMessage(mobile,captcha)){
						retJson.put("success", "true");
						retJson.put("captcha", captcha);
						UFDate busiDate = new UFDate(InvocationInfoProxy.getInstance().getBizDateTime());
						retJson.put("time", busiDate.toString());
					}else{
						retJson.put("success", "false");
						retJson.put("message", "������֤��ʧ��,"+message);
					}
				} else {
					/* ������֤��ʧ�� */
					retJson.put("success", "false");
					retJson.put("message", "������֤��ʧ��");
				}
			}
			return retJson.toString();
    	} catch (JSONException e) {
    		return retJson.toString();
    	}
	
	}
    private String message;
	/**
	 * �����ʼ�
	 * 
	 * @param email
	 */
	private boolean sendEmail(String email, String captcha) {
		boolean isSuccess = true;
		MessageVO vo= new MessageVO();
		String con = "����"+
				email.substring(0,email.indexOf("@"))+
				"��֤��"+captcha+
				"��Чʱ��1����";
		//String end = NCLangRes.getInstance().getStrByID("loginui","retrievepwd-000044");
		String time = "&nbsp;&nbsp;&nbsp;&nbsp;"+UFDate.getDate(new Date()).toString();
		vo.setContent(con+time);
		vo.setContenttype("html");
		vo.setReceiver(email);
		vo.setSubject("����nc��֤��");
		NCMessage msg = new NCMessage();
		msg.setMessage(vo);

		try {
			SMSAndMailUtil.sendMailSMS(new NCMessage[]{msg});
		} catch (Exception e) {
			isSuccess = false;
			message = e.getMessage();
			Logger.error("��֤���ʼ�����ʧ��");/*-=notranslate=-*/ 
			Logger.error(e.getMessage(), e);
		}
		return isSuccess;
	}
	
	/**
	 * ���Ͷ���
	 * 
	 * @param email
	 */
	private boolean sendMessage(String mobile, String captcha) {
		boolean isSuccess = true;
		String content = "��֤��"+captcha+"��Чʱ��1����";
		try {
			SMSAndMailUtil.sendSMS(new String[]{mobile}, content);
		} catch (Exception e) {
			isSuccess = false;
			message = e.getMessage();
			if(e instanceof NullPointerException){
				message = "δ���ö��ŷ�����";
			}
			Logger.error("��֤���ʼ�����ʧ��");/*-=notranslate=-*/ 
			Logger.error(e.getMessage(), e);
		}
		return isSuccess;
	}
	
	
	/**
	 * ��ȡ��֤��
	 * @param comp
	 * @param event
	 * @param preStepIndex
	 * @return
	 */
	private String getCaptcha(){
		// ���������
		Random random = new Random();
		// randomCode��¼�����������֤��
		StringBuffer randomCode = new StringBuffer();
		// �������codeCount���ַ�����֤�롣
		for (int i = 0; i < 6; i++) {
			String strRand = String.valueOf(codeSequence[random.nextInt(codeSequence.length)]);
			// ������������������һ��
			randomCode.append(strRand);
		}
		// ����λ���ֵ���֤�뱣�浽Session�С�
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
