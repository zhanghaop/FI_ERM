package nc.vo.erm.termendtransact;

import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.pub.lang.UFDouble;

/**
 * �汾�ţ� v1.0 ���������� ��� ���ߣ� ���� �������ڣ� 2001-06-20 �޸��ˣ� ����޸����ڣ� �޸�ԭ��
 */
public class Je implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	private String m_Bzbm = null;
	private UFDouble m_Yb = null;
	private UFDouble m_Bb = null;

	private UFDouble group_Bb = null;
	private UFDouble global_Bb = null;

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param bzbm
	 *            java.lang.String
	 */
	public Je(String bzbm) {
		super();
		setBzbm(bzbm);
		setYb(UFDouble.ZERO_DBL);
		setBb(UFDouble.ZERO_DBL);
		setGroupBb(UFDouble.ZERO_DBL);
		setGlobalBb(UFDouble.ZERO_DBL);
	}

	/**
	 * ���ֱ��룬ԭ�ҽ����ҽ����ҽ��
	 */
	public Je(String bzbm, UFDouble yb, UFDouble bb, UFDouble groupbb,
			UFDouble globalbb) {
		setBzbm(bzbm);

		if (yb == null)
			yb = UFDouble.ZERO_DBL;
		if (bb == null)
			bb = UFDouble.ZERO_DBL;

		setYb(yb);
		setBb(bb);
		setGroupBb(groupbb);
		setGlobalBb(globalbb);
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @param aJe
	 *            yy.global.Je
	 */
	public Je(Je aJe) {
		this(aJe.getBzbm(), aJe.getYb(), aJe.getBb(), aJe.getGroupBb(), aJe
				.getGlobalBb());
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return yy.global.Je
	 */
	public Je abs() {

		return new Je(this.getBzbm(), this.getYb().abs(), this.getBb().abs(),
				this.getGroupBb().abs(), this.getGlobalBb().abs());
	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return yy.global.Je
	 * @param je
	 *            yy.global.Je
	 */
	public Je add(Je je) {
		Je aJe = new Je(getBzbm());
		aJe.setYb(getYb().add(je.getYb()));
		aJe.setBb(getBb().add(je.getBb()));
		aJe.setGroupBb(getGroupBb().add(je.getGroupBb()));
		aJe.setGlobalBb(getGlobalBb().add(je.getGlobalBb()));
		return aJe;
	}

	/**
	 * �õ����ֱ���
	 */
	public String getBzbm() {
		if (m_Bzbm == null) {
			try {
				m_Bzbm = new String();
			} catch (Throwable exception) {
				ExceptionHandler.consume(exception);
			}
		}
		return m_Bzbm;
	}

	// /**
	// * ԭ�ҽ����ҽ����ҽ��ȫ��Ϊ0
	// */
	public boolean isAllZero() {
		return UFDoubleTool.isZero(getYb()) && UFDoubleTool.isZero(getBb())
				&& UFDoubleTool.isZero(getGroupBb())
				&& UFDoubleTool.isZero(getGlobalBb());
	}

	/**
	 * ���ñ��ֱ���
	 */
	public void setBzbm(String bzbm) {
		this.m_Bzbm = bzbm;
		return;
	}

	/**
	 * ����
	 */
	public void setZero() {

		// dsf
		setYb(UFDouble.ZERO_DBL);
		setBb(UFDouble.ZERO_DBL);
		setGroupBb(UFDouble.ZERO_DBL);
		setGlobalBb(UFDouble.ZERO_DBL);
		return;
	}

	/**
	 * �ڴ˴����뷽��˵���� �������ڣ�(00-11-15 13:13:32)
	 * 
	 * @return int
	 */
	public static int signum(UFDouble str) {
		// return str.signum();
		return str.toBigDecimal().signum();
	}

	/**
	 * �ڴ˴����뷽��˵���� �������ڣ�(00-11-15 16:56:17)
	 * 
	 * @return java.lang.String
	 * @param num1
	 *            java.lang.String
	 * @param num2
	 *            java.lang.String
	 */
	public static UFDouble subtract(UFDouble num1, UFDouble num2) {

		return num1.sub(num2);

	}

	/**
	 * This method was created by a SmartGuide.
	 * 
	 * @return yy.global.Je
	 * @param je
	 *            yy.global.Je
	 */
	public Je subtract(Je je) {
		Je aJe = new Je(getBzbm());
		aJe.setYb(subtract(getYb(), je.getYb()));
		aJe.setBb(subtract(getBb(), je.getBb()));
		aJe.setGroupBb(subtract(getGroupBb(), je.getGroupBb()));
		aJe.setGlobalBb(subtract(getGlobalBb(), je.getGlobalBb()));
		return aJe;
	}

	public UFDouble getGroupBb() {
		return group_Bb;
	}

	public void setGroupBb(UFDouble groupbb) {
		group_Bb = groupbb;
	}

	public UFDouble getGlobalBb() {
		return global_Bb;
	}

	public void setGlobalBb(UFDouble globalbb) {
		global_Bb = globalbb;
	}

	public UFDouble getBb() {
		return m_Bb;
	}

	public void setBb(UFDouble bb) {
		m_Bb = bb;
	}

	public UFDouble getYb() {
		return m_Yb;
	}

	public void setYb(UFDouble yb) {
		m_Yb = yb;
	}
}
