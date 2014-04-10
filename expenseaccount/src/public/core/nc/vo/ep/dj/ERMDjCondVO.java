package nc.vo.ep.dj;

/**
 * 费用查询条件VO
 * @author wangled
 *
 */
public class ERMDjCondVO implements java.io.Serializable {
	private static final long serialVersionUID = 7641757221432876655L;
	public final static Integer Voucher_All=new Integer(-1);
    public final static Integer Voucher_NotCreated=new Integer(1);
    public final static Integer Voucher_Created=new Integer(2);
    public final static Integer Voucher_Singed=new Integer(3);
	
    public Integer[] VoucherFlags = null;
    public boolean isLinkPz=false;

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
	
	
	

}
