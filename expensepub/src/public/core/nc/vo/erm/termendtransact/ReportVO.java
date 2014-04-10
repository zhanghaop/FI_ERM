package nc.vo.erm.termendtransact;

/**
 * 月末结账报告VO。
 */
public class ReportVO extends nc.vo.pub.ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int m_iBh;/*结账检查编号*/
	private String m_sInfo;/*不符合结账检查条件的单据类型名称*/
	private String m_sCount;/*不符合检查条件的单据数*/
	private boolean m_bPassed;/*本项检查是否可以通过*/
	/**
	 * ReportVO 构造子注解。
	 */
	public ReportVO() {
		super();
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @return java.lang.String
	 */
	public int getBh() {
		return m_iBh;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @return java.lang.String
	 */
	public String getCount() {
		return m_sCount;
	}
	/**
	 * 返回数值对象的显示名称。
	 * 
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public String getEntityName() {
		return null;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @return java.lang.String
	 */
	public String getInfo() {
		return m_sInfo;
	}
	/**
	 * 此处插入方法说明。

	 * @return boolean
	 */
	public boolean getState() {
		return m_bPassed;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @param bh java.lang.String
	 */
	public void setBh(int bh) {
		m_iBh = bh;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @param bh java.lang.String
	 */
	public void setCount(String count) {
		m_sCount = count;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * @param bh java.lang.String
	 */
	public void setInfo(String info) {
		m_sInfo = info;
	}
	/**
	 * 此处插入方法说明。

	 * @param passed boolean
	 */
	public void setState(boolean passed) {
	    m_bPassed = passed;
	}
	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
	 *     ValidationException，对错误进行解释。
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
