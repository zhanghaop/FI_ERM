package nc.vo.erm.termendtransact;

/**
 * δ���˻���·�VO
 * �������ڣ�(2001-11-16 15:33:57)
 * @author��wyan
 */
public class FirstNotClosedAccountMonthVO extends nc.vo.pub.ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2645743806974626970L;
	private String m_sNotAccMonth;
	private String m_sNotAccYear;
	private boolean m_bIsAccounted;

	/**
	 * NotClosedAccountMonthVO ������ע�⡣
	 */
	public FirstNotClosedAccountMonthVO() {
		super();
	}
	/**
	 * ������ֵ�������ʾ���ơ�
	 * 
	 * �������ڣ�(2001-2-15 14:18:08)
	 * @return java.lang.String ������ֵ�������ʾ���ơ�
	 */
	public String getEntityName() {
		return null;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2003-6-13 15:25:14)
	 * @return boolean
	 */
	public boolean getIsAccounted() {
		return m_bIsAccounted;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-16 16:48:38)
	 * @return java.lang.String
	 */
	public String getNotAccMonth() {
		return m_sNotAccMonth;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-16 16:48:38)
	 * @return java.lang.String
	 */
	public String getNotAccYear() {
		return m_sNotAccYear;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2003-6-13 15:25:39)
	 * @param isAcc boolean
	 */
	public void setIsAccounted(boolean isAcc) {
	    m_bIsAccounted = isAcc;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-16 16:47:18)
	 * @param month java.lang.String
	 */
	public void setNotAccMonth(String month) {
		m_sNotAccMonth = month;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-11-16 16:47:18)
	 * @param month java.lang.String
	 */
	public void setNotAccYear(String year) {
		m_sNotAccYear = year;
	}
	/**
	 * ��֤���������֮��������߼���ȷ�ԡ�
	 * 
	 * �������ڣ�(2001-2-15 11:47:35)
	 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
	 *     ValidationException���Դ�����н��͡�
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
