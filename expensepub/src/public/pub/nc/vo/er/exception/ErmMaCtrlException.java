package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

/**
 * ���뵥�����쳣
 * 
 * @author lvhj
 *
 */
public class ErmMaCtrlException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * �Ƿ񳬳����뵥
	 */
	private boolean isExceed;
	
	public ErmMaCtrlException(String msg) {
		super(msg);
	}
	public ErmMaCtrlException(String msg,boolean isExceed) {
		super(msg);
		this.isExceed = isExceed;
	}

	public boolean isExceed() {
		return isExceed;
	}

	public void setExceed(boolean isExceed) {
		this.isExceed = isExceed;
	}

	

}
