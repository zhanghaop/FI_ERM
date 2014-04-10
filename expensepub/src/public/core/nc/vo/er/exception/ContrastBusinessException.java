package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

/**
 * 必须冲借款异常
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
		 * 强制冲借款
		 */
		FORCE,

		/**
		 * 提示冲借款
		 */
		NOTICE
	}
}
