package nc.ui.erm.event;

import nc.ui.uif2.AppEvent;

/**
 * �������͸ı���¼�
 * @author wangled
 *
 */
public class BillTypeChangeEvent extends AppEvent {
	 private String newbilltype;

	  private String oldbilltype;

	
	public BillTypeChangeEvent(String oldbilltype,String newbilltype) {
		super(BillTypeChangeEvent.class.getName());
		this.oldbilltype = oldbilltype;
		this.newbilltype = newbilltype;
	}

	public String getNewbilltype() {
		return newbilltype;
	}


	public void setNewbilltype(String newbilltype) {
		this.newbilltype = newbilltype;
	}


	public String getOldbilltype() {
		return oldbilltype;
	}


	public void setOldbilltype(String oldbilltype) {
		this.oldbilltype = oldbilltype;
	}

	
	
}
