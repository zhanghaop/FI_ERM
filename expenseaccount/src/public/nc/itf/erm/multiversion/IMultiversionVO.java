package nc.itf.erm.multiversion;

/**
 * 多版本VO接口
 * @author chendya
 *
 */
public interface IMultiversionVO {

	/**
	 * 返回非多版本字段和多版本字段对照map
	 * 
	 * @return
	 */
	public java.util.Map<String, String> getFieldMap();

	/**
	 * 返回多版本VO类的类型
	 * 
	 * @return
	 */
	public Class<?> getVersionVOClass();

	/**
	 * 返回非多版本VO类的类型
	 * 
	 * @return
	 */
	public Class<?> getVOClass();

	/**
	 * 返回多版本数据库表名和多版本数据库表名
	 * 
	 * @return
	 */
	public String[] getTableNames();

	/**
	 * 返回Oid对应字段
	 * 
	 * @return
	 */
	public String getOidField();

	/**
	 * 返回Vid对应字段
	 * 
	 * @return
	 */
	public String getVidField();

	/**
	 * 返回多版本起始日期字段
	 * 
	 * @return
	 */
	public String getVstartdateField();

}
