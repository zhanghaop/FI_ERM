package nc.vo.erm.global;

import java.util.ArrayList;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
/**
 * 此处插入类型说明。
 *
 * 创建日期：(2004-3-15)
 * @author：
 */
public class BusiTransVO extends CircularlyAccessibleValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2414713048329576233L;
	public String m_pk_id;
	public String m_syscode;
	public UFBoolean m_isUsed;
	public String m_className;
	public String m_usesyscode;
	public String m_actName;
	public Integer m_iSerial;
	public String m_OpType;
	public Integer m_TransactionType;
	public String m_condClassName;
	public Integer m_version;
	public Integer m_timeTrans;
	public String m_systemname;
	public String m_usesystemname;
	public String m_note;
	
	public String m_sourcenode;
    /*资源节点*/
    public String m_sysname_resourceid;
    /*系统名称资源*/
    public String m_usesysname_resourceid;
    /*使用系统名称资源*/
    public String m_note_resourceid; 

//	/**
//	 *     描述上面属性的FieldObjects。主要用于系统工具中，
//	 * 业务代码中不会用到下面的FieldObjects。
//	 */
//	private static StringField m_pk_idField;
//	private static StringField m_syscodeField;
//	private static UFBooleanField m_isUsedField;
//	private static StringField m_classNameField;
//	private static StringField m_usesyscodeField;
//	private static StringField m_actNameField;
//	private static IntegerField m_iSerialField;
//	private static StringField m_OpTypeField;
//	private static IntegerField m_TransactionTypeField;
//	private static StringField m_condClassNameField;
//	private static IntegerField m_versionField;
//	private static IntegerField m_timeTransField;
//	private static StringField m_systemnameField;
//	private static StringField m_usesystemnameField;
//	private static StringField m_noteField;
	private java.lang.Object m_InfClass;
	public nc.ui.pub.ButtonObject m_BtnAssistant;
/**
 * 使用主键字段进行初始化的构造子。
 *
 * 创建日期：(2004-3-15)
 */
public BusiTransVO() {

}
/**
 * 使用主键进行初始化的构造子。
 *
 * 创建日期：(2004-3-15)
 * @param ??fieldNameForMethod?? 主键值
 */
public BusiTransVO(String newPk_id) {

	// 为主键字段赋值:
	m_pk_id = newPk_id;
}
/**
 * 根类Object的方法,克隆这个VO对象。
 *
 * 创建日期：(2004-3-15)
 */
public Object clone() {

	// 复制基类内容并创建新的VO对象：
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {}
	BusiTransVO busiTrans = (BusiTransVO)o;

	// 你在下面复制本VO对象的所有属性：

	return busiTrans;
}
/**
 * 属性m_actName的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getActName() {
	return m_actName;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * <p>需要在一个循环中访问的属性的名称数组。
 * <p>
 * 创建日期：(??Date??)
 * @return java.lang.String[]
 */
public java.lang.String[] getAttributeNames() {

	return new String[]{ "syscode", "isUsed", "className", "usesyscode", "actName", "iSerial", "OpType", "TransactionType", "condClassName", "version", "timeTrans", "systemname", "usesystemname", "note" };
}
/**
 *  <p>根据一个属性名称字符串该属性的值。
 *  <p>
 * 创建日期：(2004-3-15)
 * @param key java.lang.String
 */
public Object getAttributeValue(String attributeName) {

	if (attributeName.equals("pk_id")) {
		return m_pk_id;
	}
	else if (attributeName.equals("syscode")) {
		return m_syscode;
	}
	else if (attributeName.equals("isUsed")) {
		return m_isUsed;
	}
	else if (attributeName.equals("className")) {
		return m_className;
	}
	else if (attributeName.equals("usesyscode")) {
		return m_usesyscode;
	}
	else if (attributeName.equals("actName")) {
		return m_actName;
	}
	else if (attributeName.equals("iSerial")) {
		return m_iSerial;
	}
	else if (attributeName.equals("OpType")) {
		return m_OpType;
	}
	else if (attributeName.equals("TransactionType")) {
		return m_TransactionType;
	}
	else if (attributeName.equals("condClassName")) {
		return m_condClassName;
	}
	else if (attributeName.equals("version")) {
		return m_version;
	}
	else if (attributeName.equals("timeTrans")) {
		return m_timeTrans;
	}
	else if (attributeName.equals("systemname")) {
		return m_systemname;
	}
	else if (attributeName.equals("usesystemname")) {
		return m_usesystemname;
	}
	else if (attributeName.equals("note")) {
		return m_note;
	}
	return null;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-4-8 8:46:22)
 * @author:陈飞
 * @return nc.ui.pub.ButtonObject[]
 */
public nc.ui.pub.ButtonObject getBtnAssistant() {
	return m_BtnAssistant;
}
/**
 * 属性m_className的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getClassName() {
	return m_className;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_condClassName的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getCondClassName() {
	return m_condClassName;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 返回数值对象的显示名称。
 *
 * 创建日期：(2004-3-15)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {

	return "BusiTrans";
}
/**
 * 返回这个ValueObject类的所有FieldObject对象的集合。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject[]
 */
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-22 20:53:21)
 * @author：chenf
 * @return java.lang.Object
 */
public java.lang.Object getInfClass() {
	return m_InfClass;
}
/**
 * 属性m_iSerial的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return Integer
 */
public Integer getISerial() {
	return m_iSerial;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_isUsed的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return UFBoolean
 */
public UFBoolean getIsUsed() {
	return m_isUsed;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_note的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getNote() {
	return m_note;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_OpType的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getOpType() {
	return m_OpType;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_pk_id的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getPk_id() {
	return m_pk_id;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 返回对象标识，用来唯一定位对象。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getPrimaryKey() {

	return m_pk_id;
}
/**
 * 属性m_syscode的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getSyscode() {
	return m_syscode;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_systemname的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getSystemname() {
	return m_systemname;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_timeTrans的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return Integer
 */
public Integer getTimeTrans() {
	return m_timeTrans;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_TransactionType的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return Integer
 */
public Integer getTransactionType() {
	return m_TransactionType;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_usesyscode的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getUsesyscode() {
	return m_usesyscode;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_usesystemname的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return String
 */
public String getUsesystemname() {
	return m_usesystemname;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_version的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return Integer
 */
public Integer getVersion() {
	return m_version;
}
/**
 * FieldObject的Getter方法。
 *
 * 创建日期：(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * 属性m_actName的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_actName String
 */
public void setActName(String newActName) {

	m_actName = newActName;
}
/**
 *  <p>对参数name对型的属性设置值。
 *  <p>
 * 创建日期：(2004-3-15)
 * @param key java.lang.String
 */
public void setAttributeValue(String name, Object value) {

	try {
		if (name.equals("pk_id")) {
			m_pk_id = (String) value;
		}
		else if (name.equals("syscode")) {
			m_syscode = (String) value;
		}
		else if (name.equals("isUsed")) {
			m_isUsed = (UFBoolean) value;
		}
		else if (name.equals("className")) {
			m_className = (String) value;
		}
		else if (name.equals("usesyscode")) {
			m_usesyscode = (String) value;
		}
		else if (name.equals("actName")) {
			m_actName = (String) value;
		}
		else if (name.equals("iSerial")) {
			m_iSerial = (Integer) value;
		}
		else if (name.equals("OpType")) {
			m_OpType = (String) value;
		}
		else if (name.equals("TransactionType")) {
			m_TransactionType = (Integer) value;
		}
		else if (name.equals("condClassName")) {
			m_condClassName = (String) value;
		}
		else if (name.equals("version")) {
			m_version = (Integer) value;
		}
		else if (name.equals("timeTrans")) {
			m_timeTrans = (Integer) value;
		}
		else if (name.equals("systemname")) {
			m_systemname = (String) value;
		}
		else if (name.equals("usesystemname")) {
			m_usesystemname = (String) value;
		}
		else if (name.equals("note")) {
			m_note = (String) value;
		}
	}
	catch (ClassCastException e) {
		throw new ClassCastException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000040")/*@res "setAttributeValue方法中为 "*/ + name + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000041")/*@res " 赋值时类型转换错误！（值："*/ + value + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000042")/*@res "）"*/);
	}
}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-4-8 8:46:22)
 * @author:陈飞
 * @param newBtnAssistant nc.ui.pub.ButtonObject[]
 */
public void setBtnAssistant(nc.ui.pub.ButtonObject newBtnAssistant) {
	m_BtnAssistant = newBtnAssistant;
}
/**
 * 属性m_className的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_className String
 */
public void setClassName(String newClassName) {

	m_className = newClassName;
}
/**
 * 属性m_condClassName的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_condClassName String
 */
public void setCondClassName(String newCondClassName) {

	m_condClassName = newCondClassName;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-22 20:53:21)
 * @author：chenf
 * @param newInfClass java.lang.Object
 */
public void setInfClass(java.lang.Object newInfClass) {
	m_InfClass = newInfClass;
}
/**
 * 属性m_iSerial的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_iSerial Integer
 */
public void setISerial(Integer newISerial) {

	m_iSerial = newISerial;
}
/**
 * 属性m_isUsed的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_isUsed UFBoolean
 */
public void setIsUsed(UFBoolean newIsUsed) {

	m_isUsed = newIsUsed;
}
/**
 * 属性m_note的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_note String
 */
public void setNote(String newNote) {

	m_note = newNote;
}
/**
 * 属性m_OpType的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_OpType String
 */
public void setOpType(String newOpType) {

	m_OpType = newOpType;
}
/**
 * 属性m_pk_id的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_pk_id String
 */
public void setPk_id(String newPk_id) {

	m_pk_id = newPk_id;
}
/**
 * 设置对象标识，用来唯一定位对象。
 *
 * 创建日期：(2004-3-15)
 * @param m_pk_id String
 */
public void setPrimaryKey(String newPk_id) {

	m_pk_id = newPk_id;
}
/**
 * 属性m_syscode的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_syscode String
 */
public void setSyscode(String newSyscode) {

	m_syscode = newSyscode;
}
/**
 * 属性m_systemname的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_systemname String
 */
public void setSystemname(String newSystemname) {

	m_systemname = newSystemname;
}
/**
 * 属性m_timeTrans的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_timeTrans Integer
 */
public void setTimeTrans(Integer newTimeTrans) {

	m_timeTrans = newTimeTrans;
}
/**
 * 属性m_TransactionType的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_TransactionType Integer
 */
public void setTransactionType(Integer newTransactionType) {

	m_TransactionType = newTransactionType;
}
/**
 * 属性m_usesyscode的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_usesyscode String
 */
public void setUsesyscode(String newUsesyscode) {

	m_usesyscode = newUsesyscode;
}
/**
 * 属性m_usesystemname的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_usesystemname String
 */
public void setUsesystemname(String newUsesystemname) {

	m_usesystemname = newUsesystemname;
}
/**
 * 属性m_version的setter方法。
 *
 * 创建日期：(2004-3-15)
 * @param newM_version Integer
 */
public void setVersion(Integer newVersion) {

	m_version = newVersion;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 *
 * 创建日期：(2004-3-15)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws ValidationException {

	ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null fields that cannot be null.
	// 检查是否为不允许空的字段赋了空值，你可能需要修改下面的提示信息：
	if (m_pk_id == null) {
		errFields.add(new String("m_pk_id"));
	}
	// construct the exception message:
	StringBuffer message = new StringBuffer();
	message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000043")/*@res "下列字段不能为空："*/);
	if (errFields.size() > 0) {
		String[] temp = (String[]) errFields.toArray(new String[0]);
		message.append(temp[0]);
		for ( int i= 1; i < temp.length; i++ ) {
			message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000044")/*@res "、"*/);
			message.append(temp[i]);
		}
		// throw the exception:
		throw new NullFieldException(message.toString());
	}
}
    /**
     * @return 返回 note_resourceid。
     */
    public String getNote_resourceid() {
        return m_note_resourceid;
    }
    /**
     * @param note_resourceid 要设置的 note_resourceid。
     */
    public void setNote_resourceid(String note_resourceid) {
        this.m_note_resourceid = note_resourceid;
    }
    /**
     * @return 返回 sourcenode。
     */
    public String getSourcenode() {
        return m_sourcenode;
    }
    /**
     * @param sourcenode 要设置的 sourcenode。
     */
    public void setSourcenode(String sourcenode) {
        this.m_sourcenode = sourcenode;
    }
    /**
     * @return 返回 sysname_resourceid。
     */
    public String getSysname_resourceid() {
        return m_sysname_resourceid;
    }
    /**
     * @param sysname_resourceid 要设置的 sysname_resourceid。
     */
    public void setSysname_resourceid(String sysname_resourceid) {
        this.m_sysname_resourceid = sysname_resourceid;
    }
    /**
     * @return 返回 usesysname_resourceid。
     */
    public String getUsesysname_resourceid() {
        return m_usesysname_resourceid;
    }
    /**
     * @param usesysname_resourceid 要设置的 usesysname_resourceid。
     */
    public void setUsesysname_resourceid(String usesysname_resourceid) {
        this.m_usesysname_resourceid = usesysname_resourceid;
    }
}