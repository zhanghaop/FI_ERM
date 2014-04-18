package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

public class ProjBudgetAlarmBusinessException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ProjBudgetAlarmBusinessException(String errMsg){
		super(errMsg);
	}

}
