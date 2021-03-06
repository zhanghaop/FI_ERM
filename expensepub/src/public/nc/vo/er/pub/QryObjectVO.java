package nc.vo.er.pub;

/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product.                              *
\***************************************************************/

import java.util.ArrayList;
import java.util.List;

import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.control.QryCondVO;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
/**
 * 功能：节点查询对象数据结构
 *
 * 创建日期：(2001-9-27)
 * @author：
 */
public class QryObjectVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 721081255531370385L;
	public String m_obj_oid;
	public String m_point;
	public String m_disp_tab;
	public String m_disp_fld;
	public String m_cond_tab;
	public String m_cond_fld;
	public String m_obj_name;
	public Integer m_obj_datatype;
	public String m_refname;
	public Integer m_disp_order;
	public String m_value_tab;
	public String m_value_fld;
	public String m_pk_defdef;
	public String m_bdname;
	public String m_selfrefclass;
	public String m_pk_doclist;
	public String m_pk_bdbdinfo;
	private Integer m_valuetype;
	
	private List<QryCondVO> condvos = new ArrayList<QryCondVO>();
	
	public List<QryCondVO> getQryCondvos() {
		return condvos;
	}
	public Integer getValueType(){
		return m_valuetype;
	}
	public void setValueType(Integer inte){
		m_valuetype = inte;
	}

	
/**
 * 使用主键字段进行初始化的构造子。
 *
 * 创建日期：(2001-9-27)
 */
public QryObjectVO() {

}
/**
 * 使用主键进行初始化的构造子。
 *
 * 创建日期：(2001-9-27)
 * @param ??fieldNameForMethod?? 主键值
 */
public QryObjectVO(String newObj_oid) {

	// 为主键字段赋值:
	m_obj_oid = newObj_oid;
}
/**
 * 根类Object的方法,克隆这个VO对象。
 *
 * 创建日期：(2001-9-27)
 */
public Object clone() {

	// 复制基类内容并创建新的VO对象：
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {
		ExceptionHandler.handleExceptionRuntime(e);
	}
	
	if(o == null){
		throw new AssertionError();
	}
	QryObjectVO qryobj = (QryObjectVO)o;
	// 你在下面复制本VO对象的所有属性：
	return qryobj;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-2 20:30:44)
 * @return java.lang.String
 */
public String getBdInfo() {
	return m_bdname;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-2 20:30:44)
 * @return java.lang.String
 */
public String getBdName() {
	return m_bdname;
}
/**
 * 属性m_cond_fld的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getCond_fld() {
	return m_cond_fld;
}

/**
 * 属性m_cond_tab的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getCond_tab() {
	return m_cond_tab;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return nc.vo.pub.FieldObject
 */

/**
 * 属性m_disp_fld的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getDisp_fld() {
	return m_disp_fld;
}

/**
 * 属性m_disp_order的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return Integer
 */
public Integer getDisp_order() {
	return m_disp_order;
}

/**
 * 属性m_disp_tab的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getDisp_tab() {
	return m_disp_tab;
}

/**
 * 返回数值对象的显示名称。
 *
 * 创建日期：(2001-9-27)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {

	return "Qryobj";
}

/**
 * 属性m_obj_datatype的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return Integer
 */
public Integer getObj_datatype() {
	return m_obj_datatype;
}

/**
 * 属性m_obj_name的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getObj_name() {
	return m_obj_name;
}

/**
 * 属性m_obj_oid的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getObj_oid() {
	return m_obj_oid;
}

/**
 * 属性m_pk_defdef的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getPk_defdef() {
	return m_pk_defdef;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-3 9:49:53)
 * @return java.lang.String
 */
public String getPk_Doclist() {
	return m_pk_doclist;
}
/**
 * 属性m_point的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getPoint() {
	return m_point;
}

/**
 * 返回对象标识，用来唯一定位对象。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getPrimaryKey() {

	return m_obj_oid;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-2 20:32:17)
 * @return java.lang.String
 */
public String getRefClass() {
	return m_selfrefclass;
}
/**
 * 属性m_refname的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getRefname() {
	return m_refname;
}

/**
 * 属性m_value_fld的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getValue_fld() {
	return m_value_fld;
}

/**
 * 属性m_value_tab的Getter方法。
 *
 * 创建日期：(2001-9-27)
 * @return String
 */
public String getValue_tab() {
	return m_value_tab;
}

/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-2 20:30:06)
 * @param info java.lang.String
 */
public void setBdName(String info) {
    m_bdname = info;
}
/**
 * 属性m_cond_fld的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_cond_fld String
 */
public void setCond_fld(String newCond_fld) {

	m_cond_fld = newCond_fld;
}
/**
 * 属性m_cond_tab的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_cond_tab String
 */
public void setCond_tab(String newCond_tab) {

	m_cond_tab = newCond_tab;
}
/**
 * 属性m_disp_fld的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_disp_fld String
 */
public void setDisp_fld(String newDisp_fld) {

	m_disp_fld = newDisp_fld;
}
/**
 * 属性m_disp_order的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_disp_order Integer
 */
public void setDisp_order(Integer newDisp_order) {

	m_disp_order = newDisp_order;
}
/**
 * 属性m_disp_tab的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_disp_tab String
 */
public void setDisp_tab(String newDisp_tab) {

	m_disp_tab = newDisp_tab;
}
/**
 * 属性m_obj_datatype的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_obj_datatype Integer
 */
public void setObj_datatype(Integer newObj_datatype) {

	m_obj_datatype = newObj_datatype;
}
/**
 * 属性m_obj_name的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_obj_name String
 */
public void setObj_name(String newObj_name) {

	m_obj_name = newObj_name;
}
/**
 * 属性m_obj_oid的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_obj_oid String
 */
public void setObj_oid(String newObj_oid) {

	m_obj_oid = newObj_oid;
}
/**
 * 属性m_pk_defdef的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_value_tab String
 */
public void setpk_defdef(String newpk_defdef) {

	m_pk_defdef = newpk_defdef;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-3 9:50:19)
 * @param doclist java.lang.String
 */
public void setPk_Doclist(String doclist) {
    m_pk_doclist = doclist;
}
/**
 * 属性m_point的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_point String
 */
public void setPoint(String newPoint) {

	m_point = newPoint;
}
/**
 * 设置对象标识，用来唯一定位对象。
 *
 * 创建日期：(2001-9-27)
 * @param m_obj_oid String
 */
public void setPrimaryKey(String newObj_oid) {

	m_obj_oid = newObj_oid;
}
/**
 * 属性m_refname的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_refname String
 */
public void setRefname(String newRefname) {

	m_refname = newRefname;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-12-2 20:31:50)
 * @param refclass java.lang.String
 */
public void setselfrefclass(String refclass) {
    m_selfrefclass = refclass;
}
/**
 * 属性m_value_fld的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_value_fld String
 */
public void setValue_fld(String newValue_fld) {

	m_value_fld = newValue_fld;
}
/**
 * 属性m_value_tab的setter方法。
 *
 * 创建日期：(2001-9-27)
 * @param newM_value_tab String
 */
public void setValue_tab(String newValue_tab) {

	m_value_tab = newValue_tab;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 *
 * 创建日期：(2001-9-27)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws ValidationException {

	ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null fields that cannot be null.
	// 检查是否为不允许空的字段赋了空值，你可能需要修改下面的提示信息：
	if (m_obj_oid == null) {
		errFields.add("m_obj_oid");
	}
	if (m_point == null) {
		errFields.add("m_point");
	}
	if (m_disp_tab == null) {
		errFields.add("m_disp_tab");
	}
	if (m_disp_fld == null) {
		errFields.add("m_disp_fld");
	}
	if (m_cond_tab == null) {
		errFields.add("m_cond_tab");
	}
	if (m_cond_fld == null) {
		errFields.add("m_cond_fld");
	}
	if (m_obj_name == null) {
		errFields.add("m_obj_name");
	}
	if (m_obj_datatype == null) {
		errFields.add("m_obj_datatype");
	}
	if (m_disp_order == null) {
		errFields.add("m_disp_order");
	}
	if (m_value_tab == null) {
		errFields.add("m_value_tab");
	}
	if (m_value_fld == null) {
		errFields.add("m_value_fld");
	}
	StringBuffer message = new StringBuffer();
	if (errFields.size() > 0) {
		String[] temp = (String[]) errFields.toArray(new String[0]);
		message.append(temp[0]);
		
		StringBuffer msg = new StringBuffer();
		for ( int i= 1; i < temp.length; i++ ) {
			msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060504","UPP20060504-000276"));
			msg.append(temp[i]);
		}
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060504","UPP20060504-000275",null,new String[]{msg.toString()})/*@res "下列字段不能为空："*/);
		throw new NullFieldException(message.toString());
	}
}
	/**
	 * @return 返回 m_pk_bdbdinfo。
	 */
	public String getPk_bdbdinfo() {
		return m_pk_bdbdinfo;
	}
	/**
	 * @param m_pk_bdbdinfo 要设置的 m_pk_bdbdinfo。
	 */
	public void setPk_bdbdinfo(String m_pk_bdbdinfo) {
		this.m_pk_bdbdinfo = m_pk_bdbdinfo;
	}
}