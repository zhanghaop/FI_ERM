package nc.vo.er.pub;

/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product.                              *
\***************************************************************/

import java.util.ArrayList;
import java.util.List;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
import nc.vo.pub.lang.UFBoolean;

/**
 * 查询对象VO
 * 结构：[查询对象字段、字段来源标志,是否小计]
 * 字段来源标志用以区分查询字段所从属的表信息。
 *
 * 创建日期：(2001-5-23)
 * @author：宋涛
 */
public class QryObjVO extends ValueObject implements PubConstData{

	/**
	 *
	 */
	private static final long serialVersionUID = 5156415671631443498L;
	public String m_qryfld;//查询对象字段
	public String m_fldorigin;//字段来源标志
	public UFBoolean m_isSum;//是否小计
	public Integer m_fldtype = Integer.valueOf(STRING);//字段类型编码
	private String m_strDisplayName; //查询对象显示名称
	private String m_fldCode;/*查询对象编码*/
	private boolean m_isBhxj;/*是否包含下级，供查询分析使用*/
	private String pk_bdinfo;//基本档案类型主键，用于整理公式显示

	private List<QryCondVO> qryCondvos = new ArrayList<QryCondVO>();

	public List<QryCondVO> getQryCondvos() {
		return qryCondvos;
	}

	public void setQryCondvos(List<QryCondVO> qryCondvos) {
		this.qryCondvos = qryCondvos;
	}
/**
 * 使用主键字段进行初始化的构造子。
 *
 * 创建日期：(2001-5-17)
 */
public QryObjVO() {

}
/**
 * 根类Object的方法,克隆这个VO对象。
 *
 * 创建日期：(2001-5-17)
 */
public Object clone() {

	// 复制基类内容并创建新的VO对象：
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {
		nc.bs.logging.Log.getInstance(this.getClass()).error("@@@arapdebug-gyl@@@:"+e);
		throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0149")/*@res "数据克隆失败!"*/,e);
	}

	if(o==null){
		throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0149")/*@res "数据克隆失败!"*/);
	}

	QryObjVO qryCond = (QryObjVO)o;

	// 你在下面复制本VO对象的所有属性：

	return qryCond;
}
/**
 * 返回数值对象的显示名称。
 *
 * 创建日期：(2001-5-17)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {

	return "QryCond";
}

/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-23 13:20:22)
 * @return java.lang.String
 */
public String getFldCode() {
	return m_fldCode;
}
/**
 * 属性m_fldorigin的Getter方法。
 *
 * 创建日期：(2001-5-17)
 * @return String
 */
public String getFldorigin() {
	return m_fldorigin;
}

/**
 * 属性m_fldtype的Getter方法。
 *
 * 创建日期：(2001-5-17)
 * @return Integer
 */
public Integer getFldtype() {
	return m_fldtype;
}

/**
 * 属性m_boolopr的Getter方法。
 *
 * 创建日期：(2001-5-17)
 * @return String
 */
public UFBoolean getIsSum() {
	return m_isSum;
}

/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 11:25:50)
 *  参数：<|>
 *  返回值：
 *  算法：
 *  异常描述：
 * @return java.lang.String
 */
public java.lang.String getM_strDisplayName() {
	return m_strDisplayName;
}
/**
 * 属性m_qryfld的Getter方法。
 *
 * 创建日期：(2001-5-17)
 * @return String
 */
public String getQryfld() {
	return m_qryfld;
}


/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-23 13:20:58)
 * @param code java.lang.String
 */
public void setFldCode(String code) {
    m_fldCode = code;
}
/**
 * 属性m_fldorigin的setter方法。
 *
 * 创建日期：(2001-5-17)
 * @param newM_fldorigin String
 */
public void setFldorigin(String newFldorigin) {

	m_fldorigin = newFldorigin;
}
/**
 * 属性m_fldtype的setter方法。
 *
 * 创建日期：(2001-5-17)
 * @param newM_fldtype Integer
 */
public void setFldtype(Integer newFldtype) {

	m_fldtype = newFldtype;
}
/**
 * 属性m_isqryobj的setter方法。
 *
 * 创建日期：(2001-5-17)
 * @param newM_isqryobj UFBoolean
 */
public void setIsSum(UFBoolean newIsSum) {

	m_isSum = newIsSum;
}
/**
 * a功能：
 *  作者：宋涛
 *  创建时间：(2001-8-6 11:25:50)
 *  参数：<|>
 *  返回值：
 *  算法：
 *  异常描述：
 * @param newM_strDisplayName java.lang.String
 */
public void setM_strDisplayName(java.lang.String newM_strDisplayName) {
	m_strDisplayName = newM_strDisplayName;
}
/**
 * 属性m_qryfld的setter方法。
 *
 * 创建日期：(2001-5-17)
 * @param newM_qryfld String
 */
public void setQryfld(String newQryfld) {

	m_qryfld = newQryfld;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 *
 * 创建日期：(2001-5-17)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws ValidationException {

	ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null fields that cannot be null.
	// 检查是否为不允许空的字段赋了空值，你可能需要修改下面的提示信息：
	// construct the exception message:
	StringBuffer message = new StringBuffer();
	message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060504","UPP20060504-000275")/*@res "下列字段不能为空："*/);
	if (errFields.size() > 0) {
		String[] temp = (String[]) errFields.toArray(new String[0]);
		message.append(temp[0]);
		for ( int i= 1; i < temp.length; i++ ) {
			message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("20060504","UPP20060504-000276")/*@res "、"*/);
			message.append(temp[i]);
		}
		// throw the exception:
		throw new NullFieldException(message.toString());
	}
}
public boolean isM_isBhxj() {
	return m_isBhxj;
}
public void setM_isBhxj(boolean bhxj) {
	m_isBhxj = bhxj;
}
public String getPk_bdinfo() {
	return pk_bdinfo;
}
public void setPk_bdinfo(String pk_bdinfo) {
	this.pk_bdinfo = pk_bdinfo;
}
}