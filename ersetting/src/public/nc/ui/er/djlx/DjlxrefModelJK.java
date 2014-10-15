package nc.ui.er.djlx;

import java.util.Vector;
import nc.vo.ml.NCLangRes4VoTransl;

public class DjlxrefModelJK extends nc.ui.bd.ref.AbstractRefModel {
//	private String pk_corp = null;
//	private String m_swhere = "";
/**
 * BillTempRefModel 构造子注解。
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
 * getDefaultFieldCount 方法注解。
 */
public int getDefaultFieldCount() {
	return 3;
}
/**
 * 参照数据库字段名数组
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldCode() {
	return new String[]{"djlxbm","djlxmc","djdl","scomment","dwbm"};
}
/**
 * 和数据库字段名数组对应的中文名称数组
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public java.lang.String[] getFieldName() {
	return new String[]{NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000811")/*@res "单据类型编码"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000015")/*@res "单据类型名称"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000016")/*@res "单据大类"*/,
			//NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000017")/*@res "单据模板pk"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000018")/*@res "单据模板"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001650")/*@res "封存标志"*/,
			//NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001376")/*@res "备注"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000019")/*@res "末级标志"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000020")/*@res "编码原则"*/,NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030501","UPP2006030501-000021")/*@res "编码位数"*/,
			NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000745")};/*@res "单位"*/
}
public java.lang.String[] getHiddenFieldCode() {
	return new String[]{"djlxoid"};
}
/**
 * 要返回的主键字段名i.e. pk_deptdoc
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getPkFieldCode() {
	return "djlxbm";
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-8 10:36:45)
 * @return java.lang.String
 */
public java.lang.String getRefNodeName() {
	return "借款交易类型"; 	/*-=notranslate=-*/
}
/**
 * 参照标题
 * 创建日期：(01-4-4 0:57:23)
 * @return java.lang.String
 */
public String getRefTitle() {
	return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000427")/*@res "借款交易类型"*/;
}
/**
 * 参照数据库表或者视图名
 * 创建日期：(01-4-4 0:57:23)
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