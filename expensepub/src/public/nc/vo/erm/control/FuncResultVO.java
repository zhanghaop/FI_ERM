package nc.vo.erm.control;

/**
 * �˴���������������
 * �������ڣ�(2004-3-8 9:44:25)
 * @author������
 */
public class FuncResultVO extends nc.vo.pub.ValueObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8715178439613761124L;
	private Object m_SourceVO;
    private Object m_Result;
	/**
	 * FuncResultVO ������ע�⡣
	 */
	public FuncResultVO() {
		super();
	}
	/**
	 * ������ֵ�������ʾ���ơ�
	 * 
	 * �������ڣ�(2001-2-15 14:18:08)
	 * @return java.lang.String ������ֵ�������ʾ���ơ�
	 */
	public java.lang.String getEntityName() {
		return null;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:42:40)
	 * @return java.lang.Object
	 */
	public java.lang.Object getResult() {
		return m_Result;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:42:40)
	 * @return nc.vo.pub.ValueObject
	 */
	public Object getSourceVO() {
		return m_SourceVO;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:42:40)
	 * @param newResult java.lang.Object
	 */
	public void setResult(java.lang.Object newResult) {
		m_Result = newResult;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:42:40)
	 * @param newSourceVO nc.vo.pub.ValueObject
	 */
	public void setSourceVO(Object newSourceVO) {
		m_SourceVO = newSourceVO;
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
