package nc.vo.erm.termendtransact;

/**
 * 未结账会计月份VO
 * 创建日期：(2001-11-16 15:33:57)
 * @author：wyan
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
	 * NotClosedAccountMonthVO 构造子注解。
	 */
	public FirstNotClosedAccountMonthVO() {
		super();
	}
	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2001-2-15 14:18:08)
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public String getEntityName() {
		return null;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2003-6-13 15:25:14)
	 * @return boolean
	 */
	public boolean getIsAccounted() {
		return m_bIsAccounted;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-16 16:48:38)
	 * @return java.lang.String
	 */
	public String getNotAccMonth() {
		return m_sNotAccMonth;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-16 16:48:38)
	 * @return java.lang.String
	 */
	public String getNotAccYear() {
		return m_sNotAccYear;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2003-6-13 15:25:39)
	 * @param isAcc boolean
	 */
	public void setIsAccounted(boolean isAcc) {
	    m_bIsAccounted = isAcc;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-16 16:47:18)
	 * @param month java.lang.String
	 */
	public void setNotAccMonth(String month) {
		m_sNotAccMonth = month;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-11-16 16:47:18)
	 * @param month java.lang.String
	 */
	public void setNotAccYear(String year) {
		m_sNotAccYear = year;
	}
	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * 创建日期：(2001-2-15 11:47:35)
	 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
	 *     ValidationException，对错误进行解释。
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
