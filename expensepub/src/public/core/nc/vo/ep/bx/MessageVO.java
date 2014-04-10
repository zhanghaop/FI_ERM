package nc.vo.ep.bx;

import java.io.Serializable;

import nc.vo.er.util.StringUtils;


/**
 * @author twei
 *
 * nc.vo.ep.bx.MessageVO
 *
 * ��ϢVO
 */
public class MessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean success;

	private String msg;

	private int messageType;

	private JKBXVO bxvo;

	private int spzt;

	public static final int ADD = 0;

	public static final int AUDIT = 1;

	public static final int UNAUDIT = 2;

	public static final int SETTLE = 3;

	public static final int UNSETTLE = 4;

	public static final int EDIT = 5;

	public static final int DELETE = 6;

	public static final int CONTRAST = 7;


	public MessageVO(int messageType) {
		setMessageType(messageType);
	}

	public MessageVO(int messageType, JKBXVO vo, String msg, boolean success) {
		setMessageType(messageType);
		setBxvo(vo);
		setMsg(msg);
		setSuccess(success);
	}

	public static String getOperationName(int oper){
		String name = "";
		switch (oper) {
		case ADD:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000061")/*@res "����"*/;
			break;
		case AUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000194")/*@res "���"*/;
			break;
		case UNAUDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000304")/*@res "�����"*/;
			break;
		case SETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000305")/*@res "ǩ��"*/;
			break;
		case UNSETTLE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000306")/*@res "��ǩ��"*/;
			break;
		case EDIT:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000055")/*@res "�޸�"*/;
			break;
		case DELETE:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000307")/*@res "ɾ��"*/;
			break;
		case CONTRAST:
			name=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000075")/*@res "��������"*/;
			break;
		default:
			break;
		}

		return name;
	}

	public String toString() {

		StringBuffer msg = new StringBuffer();

		String key = getOperationName(messageType);

		if (success)
			msg .append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000308",null,new String[]{key,getDjbh()==null?".":getDjbh()})/*@res " ���ݳɹ�, "*/);
		else
			msg .append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000309",null,new String[]{key,getDjbh()==null?".":getDjbh()})/*@res " ���ݽ���, "*/);


		if(!StringUtils.isNullWithTrim(getMsg())){
			msg .append(" : " + getMsg());
		}

		return msg.toString();
	}

	public String getDjbh() {
		return getBxvo().getParentVO().getDjbh();
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPk() {
		return getBxvo().getParentVO().getPk_jkbx();
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public JKBXVO getBxvo() {
		return bxvo;
	}

	public void setBxvo(JKBXVO bxvo) {
		this.bxvo = bxvo;
	}




	public int getSpzt() {
		return spzt;
	}

	public void setSpzt(int spzt) {
		this.spzt = spzt;
	}

}