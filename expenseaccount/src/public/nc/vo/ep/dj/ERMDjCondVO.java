package nc.vo.ep.dj;

/**
 * ���ò�ѯ����VO
 * 
 * @author wangled
 * 
 */
public class ERMDjCondVO implements java.io.Serializable {
	private static final long serialVersionUID = 7641757221432876655L;
	public final static Integer Voucher_All = Integer.valueOf(-1);
	public final static Integer Voucher_NotCreated = Integer.valueOf(1);
	public final static Integer Voucher_Created = Integer.valueOf(2);
	public final static Integer Voucher_Singed = Integer.valueOf(3);

	public Integer[] VoucherFlags = null;
	public boolean isLinkPz = false;
	public boolean isjs = true;// �Ƿ����

	public Integer[] getVoucherFlags() {
		return VoucherFlags;
	}

	public void setVoucherFlags(Integer[] voucherFlags) {
		VoucherFlags = voucherFlags;
	}

	public boolean isLinkPz() {
		return isLinkPz;
	}

	public void setLinkPz(boolean isLinkPz) {
		this.isLinkPz = isLinkPz;
	}

	public boolean isIsjs() {
		return isjs;
	}

	public void setIsjs(boolean isjs) {
		this.isjs = isjs;
	}
}
