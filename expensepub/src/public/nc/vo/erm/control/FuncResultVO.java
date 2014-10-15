package nc.vo.erm.control;

/**
 * 此处插入类型描述。
 * 创建日期：(2004-3-8 9:44:25)
 * @author：钟悦
 */
public class FuncResultVO extends nc.vo.pub.ValueObject {
    /**
	 * 
	 */
	private static final long serialVersionUID = -8715178439613761124L;
	private Object m_SourceVO;
    private Object m_Result;
	/**
	 * FuncResultVO 构造子注解。
	 */
	public FuncResultVO() {
		super();
	}
	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2001-2-15 14:18:08)
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public java.lang.String getEntityName() {
		return null;
	}
	/**
	 * 此处插入方法描述。
	 * 创建日期：(2004-3-23 13:42:40)
	 * @return java.lang.Object
	 */
	public java.lang.Object getResult() {
		return m_Result;
	}
	/**
	 * 此处插入方法描述。
	 * 创建日期：(2004-3-23 13:42:40)
	 * @return nc.vo.pub.ValueObject
	 */
	public Object getSourceVO() {
		return m_SourceVO;
	}
	/**
	 * 此处插入方法描述。
	 * 创建日期：(2004-3-23 13:42:40)
	 * @param newResult java.lang.Object
	 */
	public void setResult(java.lang.Object newResult) {
		m_Result = newResult;
	}
	/**
	 * 此处插入方法描述。
	 * 创建日期：(2004-3-23 13:42:40)
	 * @param newSourceVO nc.vo.pub.ValueObject
	 */
	public void setSourceVO(Object newSourceVO) {
		m_SourceVO = newSourceVO;
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
