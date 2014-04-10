package nc.vo.er.reimrule;
	
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.model.meta.entity.vo.VOMetaFactory;
	
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
public class ReimRuleDimVO extends SuperVO {
	
	private transient java.lang.String pk_reimdimension;
	//单据类型
	private transient java.lang.String pk_billtype;
	private transient java.lang.String displayname;
	
	
	//数据类型  选择实体
	private transient java.lang.String datatype;
	//数据类型名称
	private transient java.lang.String datatypename;
	//元数据名称
	private transient java.lang.String beanname;
	
	
	private transient java.lang.Integer orders;
	//表示该维度对应单据模板上哪一列
	private transient java.lang.String correspondingitem;
	//参照下拉框
	private transient java.lang.String referential;
	//对应单据上的哪一项
	private transient java.lang.String billref;
	private transient java.lang.String billrefcode;
	
	//单据显示项
	private transient UFBoolean showflag;
	//核心控制项
	private transient UFBoolean controlflag;
	private transient java.lang.String pk_org;
	private transient java.lang.String pk_group;
	private transient java.lang.Integer dr = 0;
	private transient nc.vo.pub.lang.UFDateTime ts;


	public static final String PK_REIMDIMENSION = "pk_reimdimension";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String DISPLAYNAME = "displayname";
	public static final String DATATYPE = "datatype";
	public static final String DATASTYLE = "datastyle";
	public static final String DATATYPENAME = "datatypename";
	public static final String BEANNAME = "beanname";
	public static final String ORDERS = "orders";
	public static final String CORRESPONDINGITEM = "correspondingitem";
	public static final String REFERENTIAL = "referential";
	public static final String BILLREF = "billref";
	public static final String BILLREFCODE = "billrefcode";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String SHOWFLAG = "showflag";
	public static final String CONTROLFLAG = "controlflag";
			
	/**
	 * 属性pk_reimdimension的Getter方法.属性名：主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_reimdimension () {
		return pk_reimdimension;
	}   
	/**
	 * 属性pk_reimdimension的Setter方法.属性名：主键
	 * 创建日期:
	 * @param newPk_reimdimension java.lang.String
	 */
	public void setPk_reimdimension (java.lang.String newPk_reimdimension ) {
		this.pk_reimdimension = newPk_reimdimension;
	} 	  
	/**
	 * 属性pk_billtype的Getter方法.属性名：单据类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_billtype () {
		return pk_billtype;
	}   
	/**
	 * 属性pk_billtype的Setter方法.属性名：单据类型
	 * 创建日期:
	 * @param newPk_billtype java.lang.String
	 */
	public void setPk_billtype (java.lang.String newPk_billtype ) {
		this.pk_billtype = newPk_billtype;
	} 	  
	/**
	 * 属性displayname的Getter方法.属性名：显示名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDisplayname () {
		return displayname;
	}   
	/**
	 * 属性displayname的Setter方法.属性名：显示名称
	 * 创建日期:
	 * @param newDisplayname java.lang.String
	 */
	public void setDisplayname (java.lang.String newDisplayname ) {
		this.displayname = newDisplayname;
	} 	  
	/**
	 * 属性datatype的Getter方法.属性名：数据类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDatatype () {
		return datatype;
	}   
	/**
	 * 属性datatype的Setter方法.属性名：数据类型
	 * 创建日期:
	 * @param newDatatype java.lang.String
	 */
	public void setDatatype (java.lang.String newDatatype ) {
		this.datatype = newDatatype;
	} 	  
	public java.lang.String getDatatypename() {
		return datatypename;
	}
	public void setDatatypename(java.lang.String datatypename) {
		this.datatypename = datatypename;
	}
	public java.lang.String getBeanname() {
		return beanname;
	}
	public void setBeanname(java.lang.String beanname) {
		this.beanname = beanname;
	}
	/**
	 * 属性orders的Getter方法.属性名：顺序
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getOrders () {
		return orders;
	}   
	/**
	 * 属性orders的Setter方法.属性名：顺序
	 * 创建日期:
	 * @param newOrders java.lang.Integer
	 */
	public void setOrders (java.lang.Integer newOrders ) {
		this.orders = newOrders;
	} 	  
	/**
	 * 属性correspondingitem的Getter方法.属性名：对应项
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getCorrespondingitem () {
		return correspondingitem;
	}   
	/**
	 * 属性correspondingitem的Setter方法.属性名：对应项
	 * 创建日期:
	 * @param newCorrespondingitem java.lang.String
	 */
	public void setCorrespondingitem (java.lang.String newCorrespondingitem ) {
		this.correspondingitem = newCorrespondingitem;
	} 	  
	/**
	 * 属性referential的Getter方法.属性名：参照类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getReferential () {
		return referential;
	}   
	/**
	 * 属性referential的Setter方法.属性名：参照类型
	 * 创建日期:
	 * @param newReferential java.lang.String
	 */
	public void setReferential (java.lang.String newReferential ) {
		this.referential = newReferential;
	} 	  
	/**
	 * 属性billref的Getter方法.属性名：单据对应项
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getBillref () {
		return billref;
	}   
	/**
	 * 属性billref的Setter方法.属性名：单据对应项
	 * 创建日期:
	 * @param newBillref java.lang.String
	 */
	public void setBillref (java.lang.String newBillref ) {
		this.billref = newBillref;
	} 	  
	/**
	 * 属性billrefcode的Getter方法.属性名：单据对应项编码
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getBillrefcode () {
		return billrefcode;
	}   
	/**
	 * 属性billrefcode的Setter方法.属性名：单据对应项编码
	 * 创建日期:
	 * @param newBillrefcode java.lang.String
	 */
	public void setBillrefcode (java.lang.String newBillrefcode ) {
		this.billrefcode = newBillrefcode;
	} 	  
	public UFBoolean getShowflag() {
		return showflag;
	}
	public void setShowflag(UFBoolean showflag) {
		this.showflag = showflag;
	}
	public UFBoolean getControlflag() {
		return controlflag;
	}
	public void setControlflag(UFBoolean controlflag) {
		this.controlflag = controlflag;
	}
	/**
	 * 属性pk_org的Getter方法.属性名：组织
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_org () {
		return pk_org;
	}   
	/**
	 * 属性pk_org的Setter方法.属性名：组织
	 * 创建日期:
	 * @param newPk_org java.lang.String
	 */
	public void setPk_org (java.lang.String newPk_org ) {
		this.pk_org = newPk_org;
	} 	  
	/**
	 * 属性pk_group的Getter方法.属性名：集团
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_group () {
		return pk_group;
	}   
	/**
	 * 属性pk_group的Setter方法.属性名：集团
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
	  return "pk_reimdimension";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_reimdimension";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public ReimRuleDimVO() {
		super();	
	}  
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.er.reimrule.ReimRuleDimVO" )
	public IVOMeta getMetaData() {
    	IVOMeta meta = VOMetaFactory.getInstance().getVOMeta("erm.ReimDimension");
   		return meta;
  	}
} 
