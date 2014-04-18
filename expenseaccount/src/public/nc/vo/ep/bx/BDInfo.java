package nc.vo.ep.bx;

import nc.vo.pub.SuperVO;

public class BDInfo extends SuperVO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_bdinfo";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "bd_bdinfo";
	}
	public String pk_bdinfo;
	public String refnodename;
	public String getPk_bdinfo() {
		return pk_bdinfo;
	}
	public void setPk_bdinfo(String pk_bdinfo) {
		this.pk_bdinfo = pk_bdinfo;
	}
	public String getRefnodename() {
		return refnodename;
	}
	public void setRefnodename(String refnodename) {
		this.refnodename = refnodename;
	}
}
