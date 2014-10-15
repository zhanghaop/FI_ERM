package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

/**
 * ��������쳣
 * 
 * @author chendya
 * 
 */
@SuppressWarnings("serial")
public class ContrastBusinessException extends BusinessException {

	public ContrastBusinessExceptionType type;

	public ContrastBusinessException(ContrastBusinessExceptionType type, String mes) {
		super(mes);
		this.type = type;
	}
	
	public void setType(ContrastBusinessExceptionType type) {
		this.type = type;
	}
	
	public ContrastBusinessExceptionType getType() {
		return type;
	}

	public static enum ContrastBusinessExceptionType {
		/**
		 * ǿ�Ƴ���
		 */
		FORCE,

		/**
		 * ��ʾ����
		 */
		NOTICE
	}
}
