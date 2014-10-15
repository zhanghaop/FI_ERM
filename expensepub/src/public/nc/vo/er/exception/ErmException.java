package nc.vo.er.exception;

import nc.vo.pub.BusinessException;

public class ErmException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String pk_settlement;
	
	public ErmException(String msg) {
		super(msg);
	}

	public String getPk_settlement() {
		return pk_settlement;
	}

	public void setPk_settlement(String pk_settlement) {
		this.pk_settlement = pk_settlement;
	}

}
