/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.sharerule;
	
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
public class ShareruleObjVO extends SuperVO {
	private java.lang.String pk_sharerule;
	private java.lang.String pk_sruleobj;
	private java.lang.String fieldcode;
	private java.lang.String fieldname;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_SHARERULE = "pk_sharerule";
	public static final String PK_SRULEOBJ = "pk_sruleobj";
	public static final String FIELDCODE = "fieldcode";
	public static final String FIELDNAME = "fieldname";
			
	/**
	 * 属性pk_sharerule的Getter方法.属性名：parentPK
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_sharerule () {
		return pk_sharerule;
	}   
	/**
	 * 属性pk_sharerule的Setter方法.属性名：parentPK
	 * 创建日期:
	 * @param newPk_sharerule java.lang.String
	 */
	public void setPk_sharerule (java.lang.String newPk_sharerule ) {
	 	this.pk_sharerule = newPk_sharerule;
	} 	  
	/**
	 * 属性pk_sruleobj的Getter方法.属性名：主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_sruleobj () {
		return pk_sruleobj;
	}   
	/**
	 * 属性pk_sruleobj的Setter方法.属性名：主键
	 * 创建日期:
	 * @param newPk_sruleobj java.lang.String
	 */
	public void setPk_sruleobj (java.lang.String newPk_sruleobj ) {
	 	this.pk_sruleobj = newPk_sruleobj;
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
	 * 属性fieldname的Getter方法.属性名：字段名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getFieldname () {
		return fieldname;
	}   
	/**
	 * 属性fieldname的Setter方法.属性名：字段名称
	 * 创建日期:
	 * @param newFieldname java.lang.String
	 */
	public void setFieldname (java.lang.String newFieldname ) {
	 	this.fieldname = newFieldname;
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
		return "pk_sharerule";
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_sruleobj";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_sruleobj";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_sruleobj";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public ShareruleObjVO() {
		super();	
	}    
} 


