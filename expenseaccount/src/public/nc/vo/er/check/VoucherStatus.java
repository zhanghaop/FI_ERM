package nc.vo.er.check;

/**
 * <p>
 * 状态枚举。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>

 * @version V6.0
 * @since V6.0 创建时间：2009-8-19 下午02:40:55
 */
public enum VoucherStatus {
	
	SAVED(1), 		/* 暂存 */
	COMMIT(2), 		/* 保存 */
	APPROVED(3), 	/* 审核 */
	ABANDONED(4);	/* 作废 */
	
	

	private int innerV;

	
	private VoucherStatus(int innerV) {
		this.innerV = innerV;
	}
	
	public int value(){
		return innerV;
	}

	@Override
	public String toString() {
		return String.valueOf(innerV);
	}
	
	

}
