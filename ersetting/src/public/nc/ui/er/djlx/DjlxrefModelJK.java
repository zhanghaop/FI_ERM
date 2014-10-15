package nc.ui.er.djlx;

import java.util.Vector;
import nc.vo.ml.NCLangRes4VoTransl;

public class DjlxrefModelJK extends nc.ui.bd.ref.AbstractRefModel {
//	private String pk_corp = null;
//	private String m_swhere = "";
/**
 * BillTempRefModel ������ע�⡣
 */
public DjlxrefModelJK() {
	super();
	setWherePart(" and (pk_group = '" + getPk_group() +"' and djdl='jk' ) ");
	//setHiddenFieldCode(new String[]{"pk_billtemplet"});
}

public DjlxrefModelJK(String pk_corp) {
	super();
	addWherePart(" and (pk_group = '" + getPk_group() +"' and djdl='jk' ) ");
	//setHiddenFieldCode(new String[]{"pk_billtemplet"});
}
/**
 * getDefaultFieldCount ����ע�⡣
 */
public int getDefaultFieldCount() {
	return 3;
}
/**
 * �������ݿ��ֶ�������
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldCode() {
	return new String[]{"djlxbm","djlxmc","djdl","scomment","dwbm"};
}
/**
 * �����ݿ��ֶ��������Ӧ��������������
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldName() {
	return new String[]{NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000811")/*@res "�������ͱ���"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000015")/*@res "������������"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000016")/*@res "���ݴ���"*/,
			//NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000017")/*@res "����ģ��pk"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000018")/*@res "����ģ��"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001650")/*@res "����־"*/,
			//NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001376")/*@res "��ע"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000019")/*@res "ĩ����־"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000020")/*@res "����ԭ��"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000021")/*@res "����λ��"*/,
			NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000745")};/*@res "��λ"*/
}
public java.lang.String[] getHiddenFieldCode() {
	return new String[]{"djlxoid"};
}
/**
 * Ҫ���ص������ֶ���i.e. pk_deptdoc
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getPkFieldCode() {
	return "djlxbm";
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-8 10:36:45)
 * @return java.lang.String
 */
public java.lang.String getRefNodeName() {
	return "��������"; 	/*-=notranslate=-*/
}
/**
 * ���ձ���
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getRefTitle() {
	return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000427")/*@res "��������"*/;
}
/**
 * �������ݿ�������ͼ��
 * �������ڣ�(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getTableName() {
	return "er_djlx";
}

@SuppressWarnings("unchecked")
public java.util.Vector getData(){
	Vector v = super.getData();
    if(v==null)
    	return v;
    for(int i=0;i<v.size();i++){
        Vector<Object> vo=(Vector)v.get(i);
       vo.set(1,NCLangRes4VoTransl.getNCLangRes().getStrByID("arapinitdata",(String)vo.get(1)));
    }
    return v;
}
}