/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.billcontrast;
	
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
public class BillcontrastVO extends SuperVO {
	private java.lang.String pk_billcontrast;
	private java.lang.String src_billtypeid;
	private java.lang.String src_tradetypeid;
	private java.lang.String des_billtypeid;
	private java.lang.String des_tradetypeid;
	private java.lang.Integer app_scene;
	private java.lang.String src_billtype;
	private java.lang.String src_tradetype;
	private java.lang.String des_billtype;
	private java.lang.String des_tradetype;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.String creator;
	private nc.vo.pub.lang.UFDateTime creationtime;
	private java.lang.String modifier;
	private nc.vo.pub.lang.UFDateTime modifiedtime;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_BILLCONTRAST = "pk_billcontrast";
	public static final String SRC_BILLTYPEID = "src_billtypeid";
	public static final String SRC_TRADETYPEID = "src_tradetypeid";
	public static final String DES_BILLTYPEID = "des_billtypeid";
	public static final String DES_TRADETYPEID = "des_tradetypeid";
	public static final String APP_SCENE = "app_scene";
	public static final String SRC_BILLTYPE = "src_billtype";
	public static final String SRC_TRADETYPE = "src_tradetype";
	public static final String DES_BILLTYPE = "des_billtype";
	public static final String DES_TRADETYPE = "des_tradetype";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
			
	/**
	 * 属性pk_billcontrast的Getter方法.属性名：主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billcontrast () {
		return pk_billcontrast;
	}   
	/**
	 * 属性pk_billcontrast的Setter方法.属性名：主键
	 * 创建日期:
	 * @param newPk_billcontrast java.lang.String
	 */
	public void setPk_billcontrast (java.lang.String newPk_billcontrast ) {
	 	this.pk_billcontrast = newPk_billcontrast;
	} 	  
	/**
	 * 属性src_billtypeid的Getter方法.属性名：来源单据类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getSrc_billtypeid () {
		return src_billtypeid;
	}   
	/**
	 * 属性src_billtypeid的Setter方法.属性名：来源单据类型
	 * 创建日期:
	 * @param newSrc_billtypeid java.lang.String
	 */
	public void setSrc_billtypeid (java.lang.String newSrc_billtypeid ) {
	 	this.src_billtypeid = newSrc_billtypeid;
	} 	  
	/**
	 * 属性src_tradetypeid的Getter方法.属性名：来源交易类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getSrc_tradetypeid () {
		return src_tradetypeid;
	}   
	/**
	 * 属性src_tradetypeid的Setter方法.属性名：来源交易类型
	 * 创建日期:
	 * @param newSrc_tradetypeid java.lang.String
	 */
	public void setSrc_tradetypeid (java.lang.String newSrc_tradetypeid ) {
	 	this.src_tradetypeid = newSrc_tradetypeid;
	} 	  
	/**
	 * 属性des_billtypeid的Getter方法.属性名：目标单据类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDes_billtypeid () {
		return des_billtypeid;
	}   
	/**
	 * 属性des_billtypeid的Setter方法.属性名：目标单据类型
	 * 创建日期:
	 * @param newDes_billtypeid java.lang.String
	 */
	public void setDes_billtypeid (java.lang.String newDes_billtypeid ) {
	 	this.des_billtypeid = newDes_billtypeid;
	} 	  
	/**
	 * 属性des_tradetypeid的Getter方法.属性名：目标交易类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDes_tradetypeid () {
		return des_tradetypeid;
	}   
	/**
	 * 属性des_tradetypeid的Setter方法.属性名：目标交易类型
	 * 创建日期:
	 * @param newDes_tradetypeid java.lang.String
	 */
	public void setDes_tradetypeid (java.lang.String newDes_tradetypeid ) {
	 	this.des_tradetypeid = newDes_tradetypeid;
	} 	  
	/**
	 * 属性app_scene的Getter方法.属性名：应用场景
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getApp_scene () {
		return app_scene;
	}   
	/**
	 * 属性app_scene的Setter方法.属性名：应用场景
	 * 创建日期:
	 * @param newApp_scene java.lang.Integer
	 */
	public void setApp_scene (java.lang.Integer newApp_scene ) {
	 	this.app_scene = newApp_scene;
	} 	  
	/**
	 * 属性src_billtype的Getter方法.属性名：来源单据类型编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getSrc_billtype () {
		return src_billtype;
	}   
	/**
	 * 属性src_billtype的Setter方法.属性名：来源单据类型编码
	 * 创建日期:
	 * @param newSrc_billtype java.lang.String
	 */
	public void setSrc_billtype (java.lang.String newSrc_billtype ) {
	 	this.src_billtype = newSrc_billtype;
	} 	  
	/**
	 * 属性src_tradetype的Getter方法.属性名：来源交易类型编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getSrc_tradetype () {
		return src_tradetype;
	}   
	/**
	 * 属性src_tradetype的Setter方法.属性名：来源交易类型编码
	 * 创建日期:
	 * @param newSrc_tradetype java.lang.String
	 */
	public void setSrc_tradetype (java.lang.String newSrc_tradetype ) {
	 	this.src_tradetype = newSrc_tradetype;
	} 	  
	/**
	 * 属性des_billtype的Getter方法.属性名：目标单据类型编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDes_billtype () {
		return des_billtype;
	}   
	/**
	 * 属性des_billtype的Setter方法.属性名：目标单据类型编码
	 * 创建日期:
	 * @param newDes_billtype java.lang.String
	 */
	public void setDes_billtype (java.lang.String newDes_billtype ) {
	 	this.des_billtype = newDes_billtype;
	} 	  
	/**
	 * 属性des_tradetype的Getter方法.属性名：目标交易类型编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDes_tradetype () {
		return des_tradetype;
	}   
	/**
	 * 属性des_tradetype的Setter方法.属性名：目标交易类型编码
	 * 创建日期:
	 * @param newDes_tradetype java.lang.String
	 */
	public void setDes_tradetype (java.lang.String newDes_tradetype ) {
	 	this.des_tradetype = newDes_tradetype;
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
	  return "pk_billcontrast";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_billcontrast";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_billcontrast";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public BillcontrastVO() {
		super();	
	}    
} 


