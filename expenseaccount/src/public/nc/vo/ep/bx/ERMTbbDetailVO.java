/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.ep.bx;
	
import nc.vo.pub.*;

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
public class ERMTbbDetailVO extends SuperVO {
	private java.lang.String pk_bill;
	private java.lang.String pk_tbb_detail;
	private java.lang.Integer tbb_year;
	private java.lang.String tbb_month;
	private nc.vo.pub.lang.UFDouble tbb_amount;
	private nc.vo.pub.lang.UFDouble ratio;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_BILL = "pk_bill";
	public static final String PK_TBB_DETAIL = "pk_tbb_detail";
	public static final String TBB_YEAR = "tbb_year";
	public static final String TBB_MONTH = "tbb_month";
	public static final String TBB_AMOUNT = "tbb_amount";
	public static final String RATIO = "ratio";
			
	/**
	 * 属性pk_bill的Getter方法.属性名：报销单标识
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_bill () {
		return pk_bill;
	}   
	/**
	 * 属性pk_bill的Setter方法.属性名：报销单标识
	 * 创建日期:
	 * @param newPk_bill java.lang.String
	 */
	public void setPk_bill (java.lang.String newPk_bill ) {
	 	this.pk_bill = newPk_bill;
	} 	  
	/**
	 * 属性pk_tbb_detail的Getter方法.属性名：预算占用业务行
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tbb_detail () {
		return pk_tbb_detail;
	}   
	/**
	 * 属性pk_tbb_detail的Setter方法.属性名：预算占用业务行
	 * 创建日期:
	 * @param newPk_tbb_detail java.lang.String
	 */
	public void setPk_tbb_detail (java.lang.String newPk_tbb_detail ) {
	 	this.pk_tbb_detail = newPk_tbb_detail;
	} 	  
	/**
	 * 属性tbb_year的Getter方法.属性名：预算占用年度
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getTbb_year () {
		return tbb_year;
	}   
	/**
	 * 属性tbb_year的Setter方法.属性名：预算占用年度
	 * 创建日期:
	 * @param newTbb_year java.lang.Integer
	 */
	public void setTbb_year (java.lang.Integer newTbb_year ) {
	 	this.tbb_year = newTbb_year;
	} 	  
	/**
	 * 属性tbb_month的Getter方法.属性名：预算占用月份
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTbb_month () {
		return tbb_month;
	}   
	/**
	 * 属性tbb_month的Setter方法.属性名：预算占用月份
	 * 创建日期:
	 * @param newTbb_month java.lang.String
	 */
	public void setTbb_month (java.lang.String newTbb_month ) {
	 	this.tbb_month = newTbb_month;
	} 	  
	/**
	 * 属性tbb_amount的Getter方法.属性名：预算占用金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getTbb_amount () {
		return tbb_amount;
	}   
	/**
	 * 属性tbb_amount的Setter方法.属性名：预算占用金额
	 * 创建日期:
	 * @param newTbb_amount nc.vo.pub.lang.UFDouble
	 */
	public void setTbb_amount (nc.vo.pub.lang.UFDouble newTbb_amount ) {
	 	this.tbb_amount = newTbb_amount;
	} 	  
	/**
	 * 属性ratio的Getter方法.属性名：比例
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getRatio () {
		return ratio;
	}   
	/**
	 * 属性ratio的Setter方法.属性名：比例
	 * 创建日期:
	 * @param newRatio nc.vo.pub.lang.UFDouble
	 */
	public void setRatio (nc.vo.pub.lang.UFDouble newRatio ) {
	 	this.ratio = newRatio;
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
	  return "pk_tbb_detail";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_tbbdetail";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_tbbdetail";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public ERMTbbDetailVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.ep.bx.ERMTbbDetailVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
} 


