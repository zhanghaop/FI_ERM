package nc.ui.er.djlx;

import java.util.Vector;

/**
 * ���ܣ���ѯ������յ�ģ��
 * ���ߣ�����
 * ����ʱ�䣺(2002-03-29 11:27:31)
 * ʹ��˵�����Լ����˿��ܸ���Ȥ�Ľ���
 * ע�⣺�ִ�Bug
 */
public class SystemRefModel extends nc.ui.bd.ref.AbstractRefModel {
	/**���յ��б���*/
	private String[] m_strFieldCode = null;
	/**���յ�������*/
	private String[] m_strFieldName = null;
	/**���ձ���*/
	private String m_strTitle = "";
	private String syscode="CMP";
/**
 * WldxrefModel ������ע�⡣
 */
public SystemRefModel() {
	super();
	initModel();
}
/**
 * ��ȡ��������ݿ���еĲ������ݣ�����άVector��
 * �������ڣ�(2001-8-23 18:39:24)
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
 * ��ʾ�ֶ��б�
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldCode() {
	return m_strFieldCode;
}
/**
 * ��ʾ�ֶ�������
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldName() {
	return m_strFieldName;
}
/**
 * �����ֶ���
 * @return java.lang.String
 */
public String getPkFieldCode() {
	return getFieldCode()[0];
}
/**
 * ���ձ���
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getRefTitle() {
	return m_strTitle;
}
/**
 * ��ȡ��������ݿ���еĲ������ݣ�����άVector��
 * �������ڣ�(2001-8-23 18:39:24)
 * @return java.util.Vector
 */
public java.util.Vector getVecData() {

	return getData();
}
/**
 * ���ܣ���ʼ��ģ��
 * ���ߣ�����
 * ����ʱ�䣺(2001-12-20 13:42:42)
 * ������<|>
 * ����ֵ��
 * �㷨��
 */
private void initModel() {
	String[] sNames = new String[]{nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0003279")/*@res "����"*/,nc.ui.ml.NCLangRes.getInstance().getStrByID("common","UC000-0001155")/*@res "����"*/};
	String[] sCodes = new String[]{"code","name"};
	setFieldCode(sCodes);
	setFieldName(sNames);
	setRefTitle(nc.ui.ml.NCLangRes.getInstance().getStrByID("prealerttype","ec-system")/*@res "ϵͳ"*/);
}
/**
 * ��ȡ��������ݿ���еĲ������ݣ�����άVector��
 * �������ڣ�(2001-8-23 18:39:24)
 * @return java.util.Vector
 */
public java.util.Vector reloadData() {

	return getData();
}
/**
 * ��ʾ�ֶ��б�
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public void setFieldCode(String[] newFieldCode) {
	m_strFieldCode = newFieldCode;
}
/**
 * ��ʾ�ֶ�������
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public void setFieldName(String[] newFieldName) {
	m_strFieldName = newFieldName;
}
/**
 * ���ձ���
 * �������ڣ�(01-4-4 0:57:23)
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