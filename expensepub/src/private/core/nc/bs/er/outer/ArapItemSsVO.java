package nc.bs.er.outer;

import nc.vo.pub.SuperVO;

public class ArapItemSsVO extends SuperVO {

	public ArapItemSsVO() {
	}

	private String vouchid;

	private String djbh;

	private static final long serialVersionUID = 1L;

	@Override
	public String getPKFieldName() {
		return "vouchid";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "arap_item";
	}

	public String getDjbh() {
		return djbh;
	}

	public void setDjbh(String djbh) {
		this.djbh = djbh;
	}

	public String getVouchid() {
		return vouchid;
	}

	public void setVouchid(String vouchid) {
		this.vouchid = vouchid;
	}

}