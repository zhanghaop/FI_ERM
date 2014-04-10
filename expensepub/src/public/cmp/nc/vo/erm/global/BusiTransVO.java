package nc.vo.erm.global;

import java.util.ArrayList;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
/**
 * �˴���������˵����
 *
 * �������ڣ�(2004-3-15)
 * @author��
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
    /*��Դ�ڵ�*/
    public String m_sysname_resourceid;
    /*ϵͳ������Դ*/
    public String m_usesysname_resourceid;
    /*ʹ��ϵͳ������Դ*/
    public String m_note_resourceid; 

//	/**
//	 *     �����������Ե�FieldObjects����Ҫ����ϵͳ�����У�
//	 * ҵ������в����õ������FieldObjects��
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
 * ʹ�������ֶν��г�ʼ���Ĺ����ӡ�
 *
 * �������ڣ�(2004-3-15)
 */
public BusiTransVO() {

}
/**
 * ʹ���������г�ʼ���Ĺ����ӡ�
 *
 * �������ڣ�(2004-3-15)
 * @param ??fieldNameForMethod?? ����ֵ
 */
public BusiTransVO(String newPk_id) {

	// Ϊ�����ֶθ�ֵ:
	m_pk_id = newPk_id;
}
/**
 * ����Object�ķ���,��¡���VO����
 *
 * �������ڣ�(2004-3-15)
 */
public Object clone() {

	// ���ƻ������ݲ������µ�VO����
	Object o = null;
	try {
		o = super.clone();
	} catch (Exception e) {}
	BusiTransVO busiTrans = (BusiTransVO)o;

	// �������渴�Ʊ�VO������������ԣ�

	return busiTrans;
}
/**
 * ����m_actName��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getActName() {
	return m_actName;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * <p>��Ҫ��һ��ѭ���з��ʵ����Ե��������顣
 * <p>
 * �������ڣ�(??Date??)
 * @return java.lang.String[]
 */
public java.lang.String[] getAttributeNames() {

	return new String[]{ "syscode", "isUsed", "className", "usesyscode", "actName", "iSerial", "OpType", "TransactionType", "condClassName", "version", "timeTrans", "systemname", "usesystemname", "note" };
}
/**
 *  <p>����һ�����������ַ��������Ե�ֵ��
 *  <p>
 * �������ڣ�(2004-3-15)
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
 * �˴����뷽��������
 * �������ڣ�(2004-4-8 8:46:22)
 * @author:�·�
 * @return nc.ui.pub.ButtonObject[]
 */
public nc.ui.pub.ButtonObject getBtnAssistant() {
	return m_BtnAssistant;
}
/**
 * ����m_className��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getClassName() {
	return m_className;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_condClassName��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getCondClassName() {
	return m_condClassName;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ������ֵ�������ʾ���ơ�
 *
 * �������ڣ�(2004-3-15)
 * @return java.lang.String ������ֵ�������ʾ���ơ�
 */
public String getEntityName() {

	return "BusiTrans";
}
/**
 * �������ValueObject�������FieldObject����ļ��ϡ�
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject[]
 */
/**
 * �˴����뷽��������
 * �������ڣ�(2004-3-22 20:53:21)
 * @author��chenf
 * @return java.lang.Object
 */
public java.lang.Object getInfClass() {
	return m_InfClass;
}
/**
 * ����m_iSerial��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return Integer
 */
public Integer getISerial() {
	return m_iSerial;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_isUsed��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return UFBoolean
 */
public UFBoolean getIsUsed() {
	return m_isUsed;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_note��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getNote() {
	return m_note;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_OpType��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getOpType() {
	return m_OpType;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_pk_id��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getPk_id() {
	return m_pk_id;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ���ض����ʶ������Ψһ��λ����
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getPrimaryKey() {

	return m_pk_id;
}
/**
 * ����m_syscode��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getSyscode() {
	return m_syscode;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_systemname��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getSystemname() {
	return m_systemname;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_timeTrans��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return Integer
 */
public Integer getTimeTrans() {
	return m_timeTrans;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_TransactionType��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return Integer
 */
public Integer getTransactionType() {
	return m_TransactionType;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_usesyscode��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getUsesyscode() {
	return m_usesyscode;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_usesystemname��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return String
 */
public String getUsesystemname() {
	return m_usesystemname;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_version��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return Integer
 */
public Integer getVersion() {
	return m_version;
}
/**
 * FieldObject��Getter������
 *
 * �������ڣ�(2004-3-15)
 * @return nc.vo.pub.FieldObject
 */
/**
 * ����m_actName��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_actName String
 */
public void setActName(String newActName) {

	m_actName = newActName;
}
/**
 *  <p>�Բ���name���͵���������ֵ��
 *  <p>
 * �������ڣ�(2004-3-15)
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
		throw new ClassCastException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000040")/*@res "setAttributeValue������Ϊ "*/ + name + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000041")/*@res " ��ֵʱ����ת�����󣡣�ֵ��"*/ + value + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000042")/*@res "��"*/);
	}
}
/**
 * �˴����뷽��������
 * �������ڣ�(2004-4-8 8:46:22)
 * @author:�·�
 * @param newBtnAssistant nc.ui.pub.ButtonObject[]
 */
public void setBtnAssistant(nc.ui.pub.ButtonObject newBtnAssistant) {
	m_BtnAssistant = newBtnAssistant;
}
/**
 * ����m_className��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_className String
 */
public void setClassName(String newClassName) {

	m_className = newClassName;
}
/**
 * ����m_condClassName��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_condClassName String
 */
public void setCondClassName(String newCondClassName) {

	m_condClassName = newCondClassName;
}
/**
 * �˴����뷽��������
 * �������ڣ�(2004-3-22 20:53:21)
 * @author��chenf
 * @param newInfClass java.lang.Object
 */
public void setInfClass(java.lang.Object newInfClass) {
	m_InfClass = newInfClass;
}
/**
 * ����m_iSerial��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_iSerial Integer
 */
public void setISerial(Integer newISerial) {

	m_iSerial = newISerial;
}
/**
 * ����m_isUsed��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_isUsed UFBoolean
 */
public void setIsUsed(UFBoolean newIsUsed) {

	m_isUsed = newIsUsed;
}
/**
 * ����m_note��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_note String
 */
public void setNote(String newNote) {

	m_note = newNote;
}
/**
 * ����m_OpType��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_OpType String
 */
public void setOpType(String newOpType) {

	m_OpType = newOpType;
}
/**
 * ����m_pk_id��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_pk_id String
 */
public void setPk_id(String newPk_id) {

	m_pk_id = newPk_id;
}
/**
 * ���ö����ʶ������Ψһ��λ����
 *
 * �������ڣ�(2004-3-15)
 * @param m_pk_id String
 */
public void setPrimaryKey(String newPk_id) {

	m_pk_id = newPk_id;
}
/**
 * ����m_syscode��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_syscode String
 */
public void setSyscode(String newSyscode) {

	m_syscode = newSyscode;
}
/**
 * ����m_systemname��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_systemname String
 */
public void setSystemname(String newSystemname) {

	m_systemname = newSystemname;
}
/**
 * ����m_timeTrans��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_timeTrans Integer
 */
public void setTimeTrans(Integer newTimeTrans) {

	m_timeTrans = newTimeTrans;
}
/**
 * ����m_TransactionType��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_TransactionType Integer
 */
public void setTransactionType(Integer newTransactionType) {

	m_TransactionType = newTransactionType;
}
/**
 * ����m_usesyscode��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_usesyscode String
 */
public void setUsesyscode(String newUsesyscode) {

	m_usesyscode = newUsesyscode;
}
/**
 * ����m_usesystemname��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_usesystemname String
 */
public void setUsesystemname(String newUsesystemname) {

	m_usesystemname = newUsesystemname;
}
/**
 * ����m_version��setter������
 *
 * �������ڣ�(2004-3-15)
 * @param newM_version Integer
 */
public void setVersion(Integer newVersion) {

	m_version = newVersion;
}
/**
 * ��֤���������֮��������߼���ȷ�ԡ�
 *
 * �������ڣ�(2004-3-15)
 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
 *     ValidationException���Դ�����н��͡�
 */
public void validate() throws ValidationException {

	ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null fields that cannot be null.
	// ����Ƿ�Ϊ������յ��ֶθ��˿�ֵ���������Ҫ�޸��������ʾ��Ϣ��
	if (m_pk_id == null) {
		errFields.add(new String("m_pk_id"));
	}
	// construct the exception message:
	StringBuffer message = new StringBuffer();
	message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000043")/*@res "�����ֶβ���Ϊ�գ�"*/);
	if (errFields.size() > 0) {
		String[] temp = (String[]) errFields.toArray(new String[0]);
		message.append(temp[0]);
		for ( int i= 1; i < temp.length; i++ ) {
			message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("200602","UPP200602-000044")/*@res "��"*/);
			message.append(temp[i]);
		}
		// throw the exception:
		throw new NullFieldException(message.toString());
	}
}
    /**
     * @return ���� note_resourceid��
     */
    public String getNote_resourceid() {
        return m_note_resourceid;
    }
    /**
     * @param note_resourceid Ҫ���õ� note_resourceid��
     */
    public void setNote_resourceid(String note_resourceid) {
        this.m_note_resourceid = note_resourceid;
    }
    /**
     * @return ���� sourcenode��
     */
    public String getSourcenode() {
        return m_sourcenode;
    }
    /**
     * @param sourcenode Ҫ���õ� sourcenode��
     */
    public void setSourcenode(String sourcenode) {
        this.m_sourcenode = sourcenode;
    }
    /**
     * @return ���� sysname_resourceid��
     */
    public String getSysname_resourceid() {
        return m_sysname_resourceid;
    }
    /**
     * @param sysname_resourceid Ҫ���õ� sysname_resourceid��
     */
    public void setSysname_resourceid(String sysname_resourceid) {
        this.m_sysname_resourceid = sysname_resourceid;
    }
    /**
     * @return ���� usesysname_resourceid��
     */
    public String getUsesysname_resourceid() {
        return m_usesysname_resourceid;
    }
    /**
     * @param usesysname_resourceid Ҫ���õ� usesysname_resourceid��
     */
    public void setUsesysname_resourceid(String usesysname_resourceid) {
        this.m_usesysname_resourceid = usesysname_resourceid;
    }
}