/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.mactrlschema;
	
import nc.vo.pub.SuperVO;

/**
 * <b> 在此处简要描述此类的功能 </b>
 * <p>
 *     在此处添加此类的描述信息
 * </p>
 * 创建日期:
 * @author 
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
public class MtappCtrlfieldVO extends SuperVO {
	private java.lang.String pk_mtapp_cfield;
	private java.lang.String pk_tradetype;
	private java.lang.String fieldcode;
	private java.lang.String fieldname;
	private java.lang.String fieldname2;
	private java.lang.String fieldname3;
	private java.lang.String fieldname4;
	private java.lang.String fieldname5;
	private java.lang.String fieldname6;
	private nc.vo.pub.lang.UFBoolean adjust_enable;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_MTAPP_CFIELD = "pk_mtapp_cfield";
	public static final String PK_TRADETYPE = "pk_tradetype";
	public static final String FIELDCODE = "fieldcode";
	public static final String FIELDNAME = "fieldname";
	public static final String FIELDNAME2 = "fieldname2";
	public static final String FIELDNAME3 = "fieldname3";
	public static final String FIELDNAME4 = "fieldname4";
	public static final String FIELDNAME5 = "fieldname5";
	public static final String FIELDNAME6 = "fieldname6";
	public static final String ADJUST_ENABLE = "adjust_enable";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
			
	/**
	 * 属性pk_mtapp_cfield的Getter方法.属性名：主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_mtapp_cfield () {
		return pk_mtapp_cfield;
	}   
	/**
	 * 属性pk_mtapp_cfield的Setter方法.属性名：主键
	 * 创建日期:
	 * @param newPk_mtapp_cfield java.lang.String
	 */
	public void setPk_mtapp_cfield (java.lang.String newPk_mtapp_cfield ) {
	 	this.pk_mtapp_cfield = newPk_mtapp_cfield;
	} 	  
	/**
	 * 属性pk_tradetype的Getter方法.属性名：费用申请单交易类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tradetype () {
		return pk_tradetype;
	}   
	/**
	 * 属性pk_tradetype的Setter方法.属性名：费用申请单交易类型
	 * 创建日期:
	 * @param newPk_tradetype java.lang.String
	 */
	public void setPk_tradetype (java.lang.String newPk_tradetype ) {
	 	this.pk_tradetype = newPk_tradetype;
	} 	  
	/**
	 * 属性fieldcode的Getter方法.属性名：字段编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldcode () {
		return fieldcode;
	}   
	/**
	 * 属性fieldcode的Setter方法.属性名：字段编码
	 * 创建日期:
	 * @param newFieldcode java.lang.String
	 */
	public void setFieldcode (java.lang.String newFieldcode ) {
	 	this.fieldcode = newFieldcode;
	} 	  
	/**
	 * 属性fieldname的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname () {
		return fieldname;
	}   
	/**
	 * 属性fieldname的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname java.lang.String
	 */
	public void setFieldname (java.lang.String newFieldname ) {
	 	this.fieldname = newFieldname;
	} 	  
	/**
	 * 属性fieldname2的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname2 () {
		return fieldname2;
	}   
	/**
	 * 属性fieldname2的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname2 java.lang.String
	 */
	public void setFieldname2 (java.lang.String newFieldname2 ) {
	 	this.fieldname2 = newFieldname2;
	} 	  
	/**
	 * 属性fieldname3的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname3 () {
		return fieldname3;
	}   
	/**
	 * 属性fieldname3的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname3 java.lang.String
	 */
	public void setFieldname3 (java.lang.String newFieldname3 ) {
	 	this.fieldname3 = newFieldname3;
	} 	  
	/**
	 * 属性fieldname4的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname4 () {
		return fieldname4;
	}   
	/**
	 * 属性fieldname4的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname4 java.lang.String
	 */
	public void setFieldname4 (java.lang.String newFieldname4 ) {
	 	this.fieldname4 = newFieldname4;
	} 	  
	/**
	 * 属性fieldname5的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname5 () {
		return fieldname5;
	}   
	/**
	 * 属性fieldname5的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname5 java.lang.String
	 */
	public void setFieldname5 (java.lang.String newFieldname5 ) {
	 	this.fieldname5 = newFieldname5;
	} 	  
	/**
	 * 属性fieldname6的Getter方法.属性名：$map.displayName
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname6 () {
		return fieldname6;
	}   
	/**
	 * 属性fieldname6的Setter方法.属性名：$map.displayName
	 * 创建日期:
	 * @param newFieldname6 java.lang.String
	 */
	public void setFieldname6 (java.lang.String newFieldname6 ) {
	 	this.fieldname6 = newFieldname6;
	} 	  
	/**
	 * 属性adjust_enable的Getter方法.属性名：可调剂
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public nc.vo.pub.lang.UFBoolean getAdjust_enable () {
		return adjust_enable;
	}   
	/**
	 * 属性adjust_enable的Setter方法.属性名：可调剂
	 * 创建日期:
	 * @param newAdjust_enable nc.vo.pub.lang.UFBoolean
	 */
	public void setAdjust_enable (nc.vo.pub.lang.UFBoolean newAdjust_enable ) {
	 	this.adjust_enable = newAdjust_enable;
	} 	  
	/**
	 * 属性creator的Getter方法.属性名：创建人
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getCreator () {
		return creator;
	}   
	/**
	 * 属性creator的Setter方法.属性名：创建人
	 * 创建日期:
	 * @param newCreator java.lang.String
	 */
	public void setCreator (java.lang.String newCreator ) {
	 	this.creator = newCreator;
	} 	  
	/**
	 * 属性creationtime的Getter方法.属性名：创建时间
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getCreationtime () {
		return creationtime;
	}   
	/**
	 * 属性creationtime的Setter方法.属性名：创建时间
	 * 创建日期:
	 * @param newCreationtime nc.vo.pub.lang.UFDateTime
	 */
	public void setCreationtime (nc.vo.pub.lang.UFDateTime newCreationtime ) {
	 	this.creationtime = newCreationtime;
	} 	  
	/**
	 * 属性modifier的Getter方法.属性名：最后修改人
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getModifier () {
		return modifier;
	}   
	/**
	 * 属性modifier的Setter方法.属性名：最后修改人
	 * 创建日期:
	 * @param newModifier java.lang.String
	 */
	public void setModifier (java.lang.String newModifier ) {
	 	this.modifier = newModifier;
	} 	  
	/**
	 * 属性modifiedtime的Getter方法.属性名：最后修改时间
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getModifiedtime () {
		return modifiedtime;
	}   
	/**
	 * 属性modifiedtime的Setter方法.属性名：最后修改时间
	 * 创建日期:
	 * @param newModifiedtime nc.vo.pub.lang.UFDateTime
	 */
	public void setModifiedtime (nc.vo.pub.lang.UFDateTime newModifiedtime ) {
	 	this.modifiedtime = newModifiedtime;
	} 	  
	/**
	 * 属性pk_org的Getter方法.属性名：所属组织
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 属性pk_org的Setter方法.属性名：所属组织
	 * 创建日期:
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
	 	this.pk_org = newPk_org;
	} 	  
	/**
	 * 属性pk_group的Getter方法.属性名：所属集团
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * 属性pk_group的Setter方法.属性名：所属集团
	 * 创建日期:
	 * @param newPk_group java.lang.String
	 */
	public void setPk_group (java.lang.String newPk_group ) {
	 	this.pk_group = newPk_group;
	} 	  
	/**
	 * 属性dr的Getter方法.属性名：dr
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getDr () {
		return dr;
	}   
	/**
	 * 属性dr的Setter方法.属性名：dr
	 * 创建日期:
	 * @param newDr java.lang.Integer
	 */
	public void setDr (java.lang.Integer newDr ) {
	 	this.dr = newDr;
	} 	  
	/**
	 * 属性ts的Getter方法.属性名：ts
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public nc.vo.pub.lang.UFDateTime getTs () {
		return ts;
	}   
	/**
	 * 属性ts的Setter方法.属性名：ts
	 * 创建日期:
	 * @param newTs nc.vo.pub.lang.UFDateTime
	 */
	public void setTs (nc.vo.pub.lang.UFDateTime newTs ) {
	 	this.ts = newTs;
	} 	  
 
	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:
	  * @return java.lang.String
	  */
	public java.lang.String getParentPKFieldName() {
	    return null;
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_mtapp_cfield";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_mtapp_cfield";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_mtapp_cfield";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public MtappCtrlfieldVO() {
		super();	
	}    
} 


