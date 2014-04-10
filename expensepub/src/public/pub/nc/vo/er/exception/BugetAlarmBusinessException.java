package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

public class BugetAlarmBusinessException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public BugetAlarmBusinessException(String mes){
		super(mes);
	}
}
