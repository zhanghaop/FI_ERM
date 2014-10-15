package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

public class CrossControlMsgException extends BusinessException{
	private static final long serialVersionUID = 1L;

	public CrossControlMsgException(String mes){
		super(mes);
	}
}
