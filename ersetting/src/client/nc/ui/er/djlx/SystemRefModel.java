package nc.ui.er.djlx;

import java.util.Vector;

/**
 * 功能：查询方向参照的模型
 * 作者：宋涛
 * 创建时间：(2002-03-29 11:27:31)
 * 使用说明：以及别人可能感兴趣的介绍
 * 注意：现存Bug
 */
public class SystemRefModel extends nc.ui.bd.ref.AbstractRefModel {
	/**参照的列编码*/
	private String[] m_strFieldCode = null;
	/**参照的列名称*/
	private String[] m_strFieldName = null;
	/**参照标题*/
	private String m_strTitle = "";
	private String syscode="CMP";
/**
 * WldxrefModel 构造子注解。
 */
public SystemRefModel() {
	super();
	initModel();
}
/**
 * 获取缓存或数据库表中的参照数据－－二维Vector。
 * 创建日期：(2001-8-23 18:39:24)
 * @return java.util.Vector
 */
public java.util.Vector getData() {
	Vector<Object> vetData = new Vector<Object>();
	Vector<Object> vetLine = new Vector<Object>();	
	vetLine.addElement(BilltypeSystemenum.ER.getSyscode());
	vetLine.addElement(BilltypeSystemenum.ER.getSysname());
	vetData.addElement(vetLine);
	
	vetLine = new Vector<Object>();
	vetLine.addElement(BilltypeSystemenum.ALL.getSyscode());
	vetLine.addElement(BilltypeSystemenum.ALL.getSysname());
	vetData.addElement(vetLine);
	return vetData;
}

/**
 * 显示字段列表
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldCode() {
	return m_strFieldCode;
}
/**
 * 显示字段中文名
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldName() {
	return m_strFieldName;
}
/**
 * 主键字段名
 * @return java.lang.String
 */
public String getPkFieldCode() {
	return getFieldCode()[0];
}
/**
 * 参照标题
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getRefTitle() {
	return m_strTitle;
}
/**
 * 获取缓存或数据库表中的参照数据－－二维Vector。
 * 创建日期：(2001-8-23 18:39:24)
 * @return java.util.Vector
 */
public java.util.Vector getVecData() {

	return getData();
}
/**
 * 功能：初始化模型
 * 作者：宋涛
 * 创建时间：(2001-12-20 13:42:42)
 * 参数：<|>
 * 返回值：
 * 算法：
 */
private void initModel() {
	String[] sNames = new String[]{nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0003279")/*@res "编码"*/,nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0001155")/*@res "名称"*/};
	String[] sCodes = new String[]{"code","name"};
	setFieldCode(sCodes);
	setFieldName(sNames);
	setRefTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("prealerttype","ec-system")/*@res "系统"*/);
}
/**
 * 获取缓存或数据库表中的参照数据－－二维Vector。
 * 创建日期：(2001-8-23 18:39:24)
 * @return java.util.Vector
 */
public java.util.Vector reloadData() {

	return getData();
}
/**
 * 显示字段列表
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public void setFieldCode(String[] newFieldCode) {
	m_strFieldCode = newFieldCode;
}
/**
 * 显示字段中文名
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public void setFieldName(String[] newFieldName) {
	m_strFieldName = newFieldName;
}
/**
 * 参照标题
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public void setRefTitle(String newTitle) {
	m_strTitle = newTitle;
}
private String getSyscode() {
	// TODO Auto-generated method stub
	return syscode;
}
public void setSyscode(String value){
	syscode=value;
}
}