/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.matterapp.ext;
	
import nc.vo.pub.IVOMeta;
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
public class MtappMonthExtVO extends SuperVO {
	private java.lang.String pk_mtapp_month;
	private java.lang.String pk_mtapp_bill;
	private java.lang.String pk_mtapp_detail;
	private java.lang.String assume_org;
	private java.lang.String pk_pcorg;
	private nc.vo.pub.lang.UFDate billdate;
	private nc.vo.pub.lang.UFDouble orig_amount;
	private nc.vo.pub.lang.UFDouble org_amount;
	private nc.vo.pub.lang.UFDouble group_amount;
	private nc.vo.pub.lang.UFDouble global_amount;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_MTAPP_MONTH = "pk_mtapp_month";
	public static final String PK_MTAPP_BILL = "pk_mtapp_bill";
	public static final String PK_MTAPP_DETAIL = "pk_mtapp_detail";
	public static final String ASSUME_ORG = "assume_org";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String BILLDATE = "billdate";
	public static final String ORIG_AMOUNT = "orig_amount";
	public static final String ORG_AMOUNT = "org_amount";
	public static final String GROUP_AMOUNT = "group_amount";
	public static final String GLOBAL_AMOUNT = "global_amount";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
			
	/**
	 * 属性pk_mtapp_month的Getter方法.属性名：唯一标识
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_mtapp_month () {
		return pk_mtapp_month;
	}   
	/**
	 * 属性pk_mtapp_month的Setter方法.属性名：唯一标识
	 * 创建日期:
	 * @param newPk_mtapp_month java.lang.String
	 */
	public void setPk_mtapp_month (java.lang.String newPk_mtapp_month ) {
	 	this.pk_mtapp_month = newPk_mtapp_month;
	} 	  
	/**
	 * 属性pk_mtapp_bill的Getter方法.属性名：费用申请单
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_mtapp_bill () {
		return pk_mtapp_bill;
	}   
	/**
	 * 属性pk_mtapp_bill的Setter方法.属性名：费用申请单
	 * 创建日期:
	 * @param newPk_mtapp_bill java.lang.String
	 */
	public void setPk_mtapp_bill (java.lang.String newPk_mtapp_bill ) {
	 	this.pk_mtapp_bill = newPk_mtapp_bill;
	} 	  
	/**
	 * 属性pk_mtapp_detail的Getter方法.属性名：费用申请单明细
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_mtapp_detail () {
		return pk_mtapp_detail;
	}   
	/**
	 * 属性pk_mtapp_detail的Setter方法.属性名：费用申请单明细
	 * 创建日期:
	 * @param newPk_mtapp_detail java.lang.String
	 */
	public void setPk_mtapp_detail (java.lang.String newPk_mtapp_detail ) {
	 	this.pk_mtapp_detail = newPk_mtapp_detail;
	} 	  
	/**
	 * 属性assume_org的Getter方法.属性名：费用承担单位
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getAssume_org () {
		return assume_org;
	}   
	/**
	 * 属性assume_org的Setter方法.属性名：费用承担单位
	 * 创建日期:
	 * @param newAssume_org java.lang.String
	 */
	public void setAssume_org (java.lang.String newAssume_org ) {
	 	this.assume_org = newAssume_org;
	} 	  
	/**
	 * 属性pk_pcorg的Getter方法.属性名：利润中心
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_pcorg () {
		return pk_pcorg;
	}   
	/**
	 * 属性pk_pcorg的Setter方法.属性名：利润中心
	 * 创建日期:
	 * @param newPk_pcorg java.lang.String
	 */
	public void setPk_pcorg (java.lang.String newPk_pcorg ) {
	 	this.pk_pcorg = newPk_pcorg;
	} 	  
	/**
	 * 属性billdate的Getter方法.属性名：分摊日期
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDate
	 */
	public nc.vo.pub.lang.UFDate getBilldate () {
		return billdate;
	}   
	/**
	 * 属性billdate的Setter方法.属性名：分摊日期
	 * 创建日期:
	 * @param newBilldate nc.vo.pub.lang.UFDate
	 */
	public void setBilldate (nc.vo.pub.lang.UFDate newBilldate ) {
	 	this.billdate = newBilldate;
	} 	  
	/**
	 * 属性orig_amount的Getter方法.属性名：原币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrig_amount () {
		return orig_amount;
	}   
	/**
	 * 属性orig_amount的Setter方法.属性名：原币金额
	 * 创建日期:
	 * @param newOrig_amount nc.vo.pub.lang.UFDouble
	 */
	public void setOrig_amount (nc.vo.pub.lang.UFDouble newOrig_amount ) {
	 	this.orig_amount = newOrig_amount;
	} 	  
	/**
	 * 属性org_amount的Getter方法.属性名：组织本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getOrg_amount () {
		return org_amount;
	}   
	/**
	 * 属性org_amount的Setter方法.属性名：组织本币金额
	 * 创建日期:
	 * @param newOrg_amount nc.vo.pub.lang.UFDouble
	 */
	public void setOrg_amount (nc.vo.pub.lang.UFDouble newOrg_amount ) {
	 	this.org_amount = newOrg_amount;
	} 	  
	/**
	 * 属性group_amount的Getter方法.属性名：集团本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroup_amount () {
		return group_amount;
	}   
	/**
	 * 属性group_amount的Setter方法.属性名：集团本币金额
	 * 创建日期:
	 * @param newGroup_amount nc.vo.pub.lang.UFDouble
	 */
	public void setGroup_amount (nc.vo.pub.lang.UFDouble newGroup_amount ) {
	 	this.group_amount = newGroup_amount;
	} 	  
	/**
	 * 属性global_amount的Getter方法.属性名：全局本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobal_amount () {
		return global_amount;
	}   
	/**
	 * 属性global_amount的Setter方法.属性名：全局本币金额
	 * 创建日期:
	 * @param newGlobal_amount nc.vo.pub.lang.UFDouble
	 */
	public void setGlobal_amount (nc.vo.pub.lang.UFDouble newGlobal_amount ) {
	 	this.global_amount = newGlobal_amount;
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
	  return "pk_mtapp_month";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_mtapp_month";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_mtapp_month";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public MtappMonthExtVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.erm.matterapp.ext.MtappMonthExtVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
} 


