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

	/** ���������Ƿ�У��ͨ��,��һ����ʼ��ʱΪtrue�������һ����ʼ�任ֵ **/
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
     * ������֤�뵽����
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public void doCaptchaEmail(String userCode,String email){
		if (userCode.equals("") || email.equals("")) {
			/* ����д������Ϣ */
			
        } else {
			setUserCode(userCode);
			setEmail(email);
			// ��ȡ��֤��
			String captcha = getCaptcha();
			// �����ʼ�,��֤�벻Ϊ�ղ������ʼ��ɹ�������һ��
			if (captcha != null && !captcha.equals("")) {
				setClock(captcha);
			} else {
				/* ������֤��ʧ�� */
			}
		}
	
	}
    
    /**
     * ��Ӧ��һ������
     * @param event
     * @param curStep
     * @param preStep
     * @param preStepIndex
     */
    public void doCaptchaMobile(String userCode,String email){
		if (userCode.equals("") || email.equals("")) {
			/* ����д������Ϣ */
			
        } else {
			setUserCode(userCode);
			setEmail(email);
			// ��ȡ��֤��
			String captcha = getCaptcha();
			// �����ʼ�,��֤�벻Ϊ�ղ������ʼ��ɹ�������һ��
			if (captcha != null && !captcha.equals("")) {
				setClock(captcha);
			} else {
				/* ������֤��ʧ�� */
			}
		}
	
	}
    
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

	/**
	 * ������֤���ʼ������õ���ʱ
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
			/* ������֤��ʧ�� */
		}

	}
	
	/**
	 * �޸�����
	 * @param tcomp
	 * @param event
	 * @param curStep
	 * @param preStepIndex
	 */
	private boolean updatePwd(UserVO user,String password){
		// �޸�����
		String expresslyPwd = password.trim();
	    String stmp = EnvironmentInit.getServerTime().toString();
	    user.setPwdparam(stmp.substring(0, stmp.indexOf(" ")).trim());
	    try {
	      IUserPasswordManage passWordManageService = (IUserPasswordManage)NCLocator.getInstance().lookup(IUserPasswordManage.class);
	      passWordManageService.changeUserPassWord(user, expresslyPwd);
	    }
	    catch (Exception e) {
	    	/* �޸������������ϵ����Ա�� */
			Logger.error("�޸��������");/*-=notranslate=-*/ 
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
