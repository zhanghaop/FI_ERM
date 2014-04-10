package nc.vo.ep.bx;

import java.io.Serializable;

import nc.vo.pub.lang.UFDate;

public class BatchContratParam implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private UFDate cxrq;

	public UFDate getCxrq() {
		return cxrq;
	}

	public void setCxrq(UFDate cxrq) {
		this.cxrq = cxrq;
	}
	
	

}
