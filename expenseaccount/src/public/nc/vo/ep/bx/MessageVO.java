package nc.vo.ep.bx;

import java.io.Serializable;

import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.er.util.StringUtils;


/**
 * @author twei
 *
 * nc.vo.ep.bx.MessageVO
 *
 * 消息VO
 */
public class MessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean success;

	private String msg;

	private int messageType;

	private JKBXVO bxvo;

	private int spzt;
	
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
		return ActionUtils.getOperationName(oper);
	}

	public String toString() {

		StringBuffer msg = new StringBuffer();

		String key = getOperationName(messageType);

		if (success)
			msg .append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000308",null,new String[]{key,getDjbh()==null?".":getDjbh()})/*@res " 单据成功, "*/);
		else
			msg .append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000309",null,new String[]{key,getDjbh()==null?".":getDjbh()})/*@res " 单据结束, "*/);


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