package nc.vo.erm.termendtransact;

/**
 * ��ĩ���˱���VO��
 */
public class ReportVO extends nc.vo.pub.ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int m_iBh;/*���˼����*/
	private String m_sInfo;/*�����Ͻ��˼�������ĵ�����������*/
	private String m_sCount;/*�����ϼ�������ĵ�����*/
	private boolean m_bPassed;/*�������Ƿ����ͨ��*/
	/**
	 * ReportVO ������ע�⡣
	 */
	public ReportVO() {
		super();
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @return java.lang.String
	 */
	public int getBh() {
		return m_iBh;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @return java.lang.String
	 */
	public String getCount() {
		return m_sCount;
	}
	/**
	 * ������ֵ�������ʾ���ơ�
	 * 
	 * @return java.lang.String ������ֵ�������ʾ���ơ�
	 */
	public String getEntityName() {
		return null;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @return java.lang.String
	 */
	public String getInfo() {
		return m_sInfo;
	}
	/**
	 * �˴����뷽��˵����

	 * @return boolean
	 */
	public boolean getState() {
		return m_bPassed;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @param bh java.lang.String
	 */
	public void setBh(int bh) {
		m_iBh = bh;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @param bh java.lang.String
	 */
	public void setCount(String count) {
		m_sCount = count;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * @param bh java.lang.String
	 */
	public void setInfo(String info) {
		m_sInfo = info;
	}
	/**
	 * �˴����뷽��˵����

	 * @param passed boolean
	 */
	public void setState(boolean passed) {
	    m_bPassed = passed;
	}
	/**
	 * ��֤���������֮��������߼���ȷ�ԡ�
	 * 
	 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
	 *     ValidationException���Դ�����н��͡�
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
