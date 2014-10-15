package nc.vo.er.check;

/**
 * <p>
 * ״̬ö�١�
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>

 * @version V6.0
 * @since V6.0 ����ʱ�䣺2009-8-19 ����02:40:55
 */
public enum VoucherStatus {
	
	SAVED(1), 		/* �ݴ� */
	COMMIT(2), 		/* ���� */
	APPROVED(3), 	/* ��� */
	ABANDONED(4);	/* ���� */
	
	

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
