/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.er.reimrule;
	
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
@SuppressWarnings({ "serial", "unchecked" })
public class ReimRulerVO extends SuperVO implements Comparable{
	public static final String PKORG = "group";
	public static final String REMRULE_SPLITER = "\\.";
	public static final String Reim_body_key = "body";
	public static final String Reim_head_key = "head";
	public static final String Reim_deptid_key = "deptid";
	public static final String Reim_fydeptid_key = "fydeptid";
	public static final String Reim_jkbxr_key = "jkbxr";
	public static final String Reim_receiver_key = "receiver";
	
	private java.lang.String pk_reimrule;
	private Object pk_expensetype;
	private java.lang.String pk_expensetype_name;
	private Object pk_reimtype;
	private java.lang.String pk_reimtype_name;
	private Object pk_deptid;
	private java.lang.String pk_deptid_name;
	private Object pk_position;
	private java.lang.String pk_position_name;
	private Object pk_currtype;
	private Object pk_currtype_name;
	private nc.vo.pub.lang.UFDouble amount;
	private java.lang.String amount_name;
	private Object memo;
	private java.lang.String memo_name;
	private Object def1;
	private java.lang.String def1_name;
	private Object def2;
	private java.lang.String def2_name;
	private Object def3;
	private java.lang.String def3_name;
	private Object def4;
	private java.lang.String def4_name;
	private Object def5;
	private java.lang.String def5_name;
	private Object def6;
	private java.lang.String def6_name;
	private Object def7;
	private java.lang.String def7_name;
	private Object def8;
	private java.lang.String def8_name;
	private Object def9;
	private java.lang.String def9_name;
	private Object def10;
	private java.lang.String def10_name;
	private Object def11;
	private java.lang.String def11_name;
	private Object def12;
	private java.lang.String def12_name;
	private Object def13;
	private java.lang.String def13_name;
	private Object def14;
	private java.lang.String def14_name;
	private Object def15;
	private java.lang.String def15_name;
	private Object def16;
	private java.lang.String def16_name;
	private Object def17;
	private java.lang.String def17_name;
	private Object def18;
	private java.lang.String def18_name;
	private Object def19;
	private java.lang.String def19_name;
	private Object def20;
	private java.lang.String def20_name;
	//记录该标准应该显示在单据模板的哪一项上
	private java.lang.String showitem;
	private java.lang.String showitem_name;
	//记录该标准应该控制单据模板的哪一项
	private java.lang.String controlitem;
	private java.lang.String controlitem_name;
	private java.lang.Integer controlflag;
	private java.lang.String controlformula;
	private java.lang.Integer priority;
	private java.lang.String pk_group;
	private java.lang.String pk_org;
	private java.lang.String pk_billtype;
	private java.lang.String defformula;
	private java.lang.String validateformula;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;

	public static final String PK_REIMRULE = "pk_reimrule";
	public static final String PK_EXPENSETYPE = "pk_expensetype";
	public static final String PK_EXPENSETYPE_NAME = "pk_expensetype_name";
	public static final String PK_REIMTYPE = "pk_reimtype";
	public static final String PK_REIMTYPE_NAME = "pk_reimtype_name";
	public static final String PK_DEPTID = "pk_deptid";
	public static final String PK_DEPTID_NAME = "pk_deptid_name";
	public static final String PK_POSITION = "pk_position";
	public static final String PK_POSITION_NAME = "pk_position_name";
	public static final String PK_CURRTYPE = "pk_currtype";
	public static final String PK_CURRTYPE_NAME = "pk_currtype_name";
	public static final String AMOUNT = "amount";
	public static final String AMOUNT_NAME = "amount_name";
	public static final String MEMO = "memo";
	public static final String MEMO_NAME = "memo_name";
	public static final String DEF1 = "def1";
	public static final String DEF1_NAME = "def1_name";
	public static final String DEF2 = "def2";
	public static final String DEF2_NAME = "def2_name";
	public static final String DEF3 = "def3";
	public static final String DEF3_NAME = "def3_name";
	public static final String DEF4 = "def4";
	public static final String DEF4_NAME = "def4_name";
	public static final String DEF5 = "def5";
	public static final String DEF5_NAME = "def5_name";
	public static final String DEF6 = "def6";
	public static final String DEF6_NAME = "def6_name";
	public static final String DEF7 = "def7";
	public static final String DEF7_NAME = "def7_name";
	public static final String DEF8 = "def8";
	public static final String DEF8_NAME = "def8_name";
	public static final String DEF9 = "def9";
	public static final String DEF9_NAME = "def9_name";
	public static final String DEF10 = "def10";
	public static final String DEF10_NAME = "def10_name";
	public static final String DEF11 = "def11";
	public static final String DEF11_NAME = "def11_name";
	public static final String DEF12 = "def12";
	public static final String DEF12_NAME = "def12_name";
	public static final String DEF13 = "def13";
	public static final String DEF13_NAME = "def13_name";
	public static final String DEF14 = "def14";
	public static final String DEF14_NAME = "def14_name";
	public static final String DEF15 = "def15";
	public static final String DEF15_NAME = "def15_name";
	public static final String DEF16 = "def16";
	public static final String DEF16_NAME = "def16_name";
	public static final String DEF17 = "def17";
	public static final String DEF17_NAME = "def17_name";
	public static final String DEF18 = "def18";
	public static final String DEF18_NAME = "def18_name";
	public static final String DEF19 = "def19";
	public static final String DEF19_NAME = "def19_name";
	public static final String DEF20 = "def20";
	public static final String DEF20_NAME = "def20_name";
	public static final String PRIORITY = "priority";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_ORG = "pk_org";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String DEFFORMULA = "defformula";
	public static final String VALIDATEFORMULA = "validateformula";
	public static final String SHOWITEM = "showitem";
	public static final String SHOWITEM_NAME = "showitem_name";
	public static final String CONTROLITEM = "controlitem";
	public static final String CONTROLITEM_NAME = "controlitem_name";
	public static final String CONTROLFLAG = "controlflag";
	public static final String CONTROLFORMULA = "controlformula";
			
	/**
	 * 属性pk_reimrule的Getter方法.属性名：报销标准主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_reimrule () {
		return pk_reimrule;
	}   
	/**
	 * 属性pk_reimrule的Setter方法.属性名：报销标准主键
	 * 创建日期:
	 * @param newPk_reimrule java.lang.String
	 */
	public void setPk_reimrule (java.lang.String newPk_reimrule ) {
	 	this.pk_reimrule = newPk_reimrule;
	} 	  
	/**
	 * 属性pk_expensetype的Getter方法.属性名：费用类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_expensetype () {
		return pk_expensetype;
	}   
	/**
	 * 属性pk_expensetype的Setter方法.属性名：费用类型
	 * 创建日期:
	 * @param newPk_expensetype java.lang.String
	 */
	public void setPk_expensetype (Object newPk_expensetype ) {
	 	this.pk_expensetype = newPk_expensetype;
	} 	  
	/**
	 * 属性pk_expensetype_name的Getter方法.属性名：费用类型名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_expensetype_name () {
		return pk_expensetype_name;
	}   
	/**
	 * 属性pk_expensetype_name的Setter方法.属性名：费用类型名称
	 * 创建日期:
	 * @param newPk_expensetype_name java.lang.String
	 */
	public void setPk_expensetype_name (java.lang.String newPk_expensetype_name ) {
	 	this.pk_expensetype_name = newPk_expensetype_name;
	} 	  
	/**
	 * 属性pk_reimtype的Getter方法.属性名：报销类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_reimtype () {
		return pk_reimtype;
	}   
	/**
	 * 属性pk_reimtype的Setter方法.属性名：报销类型
	 * 创建日期:
	 * @param newPk_reimtype java.lang.String
	 */
	public void setPk_reimtype (Object newPk_reimtype ) {
	 	this.pk_reimtype = newPk_reimtype;
	} 	  
	/**
	 * 属性pk_reimtype_name的Getter方法.属性名：报销类型名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_reimtype_name () {
		return pk_reimtype_name;
	}   
	/**
	 * 属性pk_reimtype_name的Setter方法.属性名：报销类型名称
	 * 创建日期:
	 * @param newPk_reimtype_name java.lang.String
	 */
	public void setPk_reimtype_name (java.lang.String newPk_reimtype_name ) {
	 	this.pk_reimtype_name = newPk_reimtype_name;
	} 	  
	/**
	 * 属性pk_deptid的Getter方法.属性名：部门
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_deptid () {
		return pk_deptid;
	}   
	/**
	 * 属性pk_deptid的Setter方法.属性名：部门
	 * 创建日期:
	 * @param newPk_deptid java.lang.String
	 */
	public void setPk_deptid (Object newPk_deptid ) {
	 	this.pk_deptid = newPk_deptid;
	} 	  
	/**
	 * 属性pk_deptid_name的Getter方法.属性名：部门名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_deptid_name () {
		return pk_deptid_name;
	}   
	/**
	 * 属性pk_deptid_name的Setter方法.属性名：部门名称
	 * 创建日期:
	 * @param newPk_deptid_name java.lang.String
	 */
	public void setPk_deptid_name (java.lang.String newPk_deptid_name ) {
	 	this.pk_deptid_name = newPk_deptid_name;
	} 	  
	/**
	 * 属性pk_position的Getter方法.属性名：职位
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_position () {
		return pk_position;
	}   
	/**
	 * 属性pk_position的Setter方法.属性名：职位
	 * 创建日期:
	 * @param newPk_position java.lang.String
	 */
	public void setPk_position (Object newPk_position ) {
	 	this.pk_position = newPk_position;
	} 	  
	/**
	 * 属性pk_position_name的Getter方法.属性名：职位名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_position_name () {
		return pk_position_name;
	}   
	/**
	 * 属性pk_position_name的Setter方法.属性名：职位名称
	 * 创建日期:
	 * @param newPk_position_name java.lang.String
	 */
	public void setPk_position_name (java.lang.String newPk_position_name ) {
	 	this.pk_position_name = newPk_position_name;
	} 	  
	/**
	 * 属性pk_currtype的Getter方法.属性名：币种
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_currtype () {
		return pk_currtype;
	}   
	/**
	 * 属性pk_currtype的Setter方法.属性名：币种
	 * 创建日期:
	 * @param newPk_currtype java.lang.String
	 */
	public void setPk_currtype (Object newPk_currtype ) {
	 	this.pk_currtype = newPk_currtype;
	} 	  
	/**
	 * 属性pk_currtype_name的Getter方法.属性名：币币种名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getPk_currtype_name () {
		return pk_currtype_name;
	}   
	/**
	 * 属性pk_currtype_name的Setter方法.属性名：币币种名称
	 * 创建日期:
	 * @param newPk_currtype_name java.lang.String
	 */
	public void setPk_currtype_name (java.lang.String newPk_currtype_name ) {
	 	this.pk_currtype_name = newPk_currtype_name;
	} 	  
	/**
	 * 属性amount的Getter方法.属性名：金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getAmount () {
		return amount;
	}   
	/**
	 * 属性amount的Setter方法.属性名：金额
	 * 创建日期:
	 * @param newAmount nc.vo.pub.lang.UFDouble
	 */
	public void setAmount (nc.vo.pub.lang.UFDouble newAmount ) {
	 	this.amount = newAmount;
	} 	  
	/**
	 * 属性amount_name的Getter方法.属性名：金额名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getAmount_name () {
		return amount_name;
	}   
	/**
	 * 属性amount_name的Setter方法.属性名：金额名称
	 * 创建日期:
	 * @param newAmount_name java.lang.String
	 */
	public void setAmount_name (java.lang.String newAmount_name ) {
	 	this.amount_name = newAmount_name;
	} 	  
	/**
	 * 属性memo的Getter方法.属性名：备注
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getMemo () {
		return memo;
	}   
	/**
	 * 属性memo的Setter方法.属性名：备注
	 * 创建日期:
	 * @param newMemo java.lang.String
	 */
	public void setMemo (Object newMemo ) {
	 	this.memo = newMemo;
	} 	  
	/**
	 * 属性memo_name的Getter方法.属性名：备注名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getMemo_name () {
		return memo_name;
	}   
	/**
	 * 属性memo_name的Setter方法.属性名：备注名称
	 * 创建日期:
	 * @param newMemo_name java.lang.String
	 */
	public void setMemo_name (java.lang.String newMemo_name ) {
	 	this.memo_name = newMemo_name;
	} 	  
	/**
	 * 属性def1的Getter方法.属性名：自定义项1
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef1 () {
		return def1;
	}   
	/**
	 * 属性def1的Setter方法.属性名：自定义项1
	 * 创建日期:
	 * @param newDef1 java.lang.String
	 */
	public void setDef1 (Object newDef1 ) {
	 	this.def1 = newDef1;
	} 	  
	/**
	 * 属性def1_name的Getter方法.属性名：自定义项1名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef1_name () {
		return def1_name;
	}   
	/**
	 * 属性def1_name的Setter方法.属性名：自定义项1名称
	 * 创建日期:
	 * @param newDef1_name java.lang.String
	 */
	public void setDef1_name (java.lang.String newDef1_name ) {
	 	this.def1_name = newDef1_name;
	} 	  
	/**
	 * 属性def2的Getter方法.属性名：自定义项2
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef2 () {
		return def2;
	}   
	/**
	 * 属性def2的Setter方法.属性名：自定义项2
	 * 创建日期:
	 * @param newDef2 java.lang.String
	 */
	public void setDef2 (Object newDef2 ) {
	 	this.def2 = newDef2;
	} 	  
	/**
	 * 属性def2_name的Getter方法.属性名：自定义项2名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef2_name () {
		return def2_name;
	}   
	/**
	 * 属性def2_name的Setter方法.属性名：自定义项2名称
	 * 创建日期:
	 * @param newDef2_name java.lang.String
	 */
	public void setDef2_name (java.lang.String newDef2_name ) {
	 	this.def2_name = newDef2_name;
	} 	  
	/**
	 * 属性def3的Getter方法.属性名：自定义项3
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef3 () {
		return def3;
	}   
	/**
	 * 属性def3的Setter方法.属性名：自定义项3
	 * 创建日期:
	 * @param newDef3 java.lang.String
	 */
	public void setDef3 (Object newDef3 ) {
	 	this.def3 = newDef3;
	} 	  
	/**
	 * 属性def3_name的Getter方法.属性名：自定义项3名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef3_name () {
		return def3_name;
	}   
	/**
	 * 属性def3_name的Setter方法.属性名：自定义项3名称
	 * 创建日期:
	 * @param newDef3_name java.lang.String
	 */
	public void setDef3_name (java.lang.String newDef3_name ) {
	 	this.def3_name = newDef3_name;
	} 	  
	/**
	 * 属性def4的Getter方法.属性名：自定义项4
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef4 () {
		return def4;
	}   
	/**
	 * 属性def4的Setter方法.属性名：自定义项4
	 * 创建日期:
	 * @param newDef4 java.lang.String
	 */
	public void setDef4 (Object newDef4 ) {
	 	this.def4 = newDef4;
	} 	  
	/**
	 * 属性def4_name的Getter方法.属性名：自定义项4名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef4_name () {
		return def4_name;
	}   
	/**
	 * 属性def4_name的Setter方法.属性名：自定义项4名称
	 * 创建日期:
	 * @param newDef4_name java.lang.String
	 */
	public void setDef4_name (java.lang.String newDef4_name ) {
	 	this.def4_name = newDef4_name;
	} 	  
	/**
	 * 属性def5的Getter方法.属性名：自定义项5
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef5 () {
		return def5;
	}   
	/**
	 * 属性def5的Setter方法.属性名：自定义项5
	 * 创建日期:
	 * @param newDef5 java.lang.String
	 */
	public void setDef5 (Object newDef5 ) {
	 	this.def5 = newDef5;
	} 	  
	/**
	 * 属性def5_name的Getter方法.属性名：自定义项5名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef5_name () {
		return def5_name;
	}   
	/**
	 * 属性def5_name的Setter方法.属性名：自定义项5名称
	 * 创建日期:
	 * @param newDef5_name java.lang.String
	 */
	public void setDef5_name (java.lang.String newDef5_name ) {
	 	this.def5_name = newDef5_name;
	} 	  
	/**
	 * 属性def6的Getter方法.属性名：自定义项6
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef6 () {
		return def6;
	}   
	/**
	 * 属性def6的Setter方法.属性名：自定义项6
	 * 创建日期:
	 * @param newDef6 java.lang.String
	 */
	public void setDef6 (Object newDef6 ) {
	 	this.def6 = newDef6;
	} 	  
	/**
	 * 属性def6_name的Getter方法.属性名：自定义项6名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef6_name () {
		return def6_name;
	}   
	/**
	 * 属性def6_name的Setter方法.属性名：自定义项6名称
	 * 创建日期:
	 * @param newDef6_name java.lang.String
	 */
	public void setDef6_name (java.lang.String newDef6_name ) {
	 	this.def6_name = newDef6_name;
	} 	  
	/**
	 * 属性def7的Getter方法.属性名：自定义项7
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef7 () {
		return def7;
	}   
	/**
	 * 属性def7的Setter方法.属性名：自定义项7
	 * 创建日期:
	 * @param newDef7 java.lang.String
	 */
	public void setDef7 (Object newDef7 ) {
	 	this.def7 = newDef7;
	} 	  
	/**
	 * 属性def7_name的Getter方法.属性名：自定义项7名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef7_name () {
		return def7_name;
	}   
	/**
	 * 属性def7_name的Setter方法.属性名：自定义项7名称
	 * 创建日期:
	 * @param newDef7_name java.lang.String
	 */
	public void setDef7_name (java.lang.String newDef7_name ) {
	 	this.def7_name = newDef7_name;
	} 	  
	/**
	 * 属性def8的Getter方法.属性名：自定义项8
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef8 () {
		return def8;
	}   
	/**
	 * 属性def8的Setter方法.属性名：自定义项8
	 * 创建日期:
	 * @param newDef8 java.lang.String
	 */
	public void setDef8 (Object newDef8 ) {
	 	this.def8 = newDef8;
	} 	  
	/**
	 * 属性def8_name的Getter方法.属性名：自定义项8名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef8_name () {
		return def8_name;
	}   
	/**
	 * 属性def8_name的Setter方法.属性名：自定义项8名称
	 * 创建日期:
	 * @param newDef8_name java.lang.String
	 */
	public void setDef8_name (java.lang.String newDef8_name ) {
	 	this.def8_name = newDef8_name;
	} 	  
	/**
	 * 属性def9的Getter方法.属性名：自定义项9
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef9 () {
		return def9;
	}   
	/**
	 * 属性def9的Setter方法.属性名：自定义项9
	 * 创建日期:
	 * @param newDef9 java.lang.String
	 */
	public void setDef9 (Object newDef9 ) {
	 	this.def9 = newDef9;
	} 	  
	/**
	 * 属性def9_name的Getter方法.属性名：自定义项9名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef9_name () {
		return def9_name;
	}   
	/**
	 * 属性def9_name的Setter方法.属性名：自定义项9名称
	 * 创建日期:
	 * @param newDef9_name java.lang.String
	 */
	public void setDef9_name (java.lang.String newDef9_name ) {
	 	this.def9_name = newDef9_name;
	} 	  
	/**
	 * 属性def10的Getter方法.属性名：自定义项10
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef10 () {
		return def10;
	}   
	/**
	 * 属性def10的Setter方法.属性名：自定义项10
	 * 创建日期:
	 * @param newDef10 java.lang.String
	 */
	public void setDef10 (Object newDef10 ) {
	 	this.def10 = newDef10;
	} 	  
	/**
	 * 属性def10_name的Getter方法.属性名：自定义项10名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef10_name () {
		return def10_name;
	}   
	/**
	 * 属性def10_name的Setter方法.属性名：自定义项10名称
	 * 创建日期:
	 * @param newDef10_name java.lang.String
	 */
	public void setDef10_name (java.lang.String newDef10_name ) {
	 	this.def10_name = newDef10_name;
	} 	  
	/**
	 * 属性def11的Getter方法.属性名：自定义项11
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef11 () {
		return def11;
	}   
	/**
	 * 属性def11的Setter方法.属性名：自定义项11
	 * 创建日期:
	 * @param newDef11 java.lang.String
	 */
	public void setDef11 (Object newDef11 ) {
	 	this.def11 = newDef11;
	} 	  
	/**
	 * 属性def11_name的Getter方法.属性名：自定义项11名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef11_name () {
		return def11_name;
	}   
	/**
	 * 属性def11_name的Setter方法.属性名：自定义项11名称
	 * 创建日期:
	 * @param newDef11_name java.lang.String
	 */
	public void setDef11_name (java.lang.String newDef11_name ) {
	 	this.def11_name = newDef11_name;
	} 	  
	/**
	 * 属性def12的Getter方法.属性名：自定义项12
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef12 () {
		return def12;
	}   
	/**
	 * 属性def12的Setter方法.属性名：自定义项12
	 * 创建日期:
	 * @param newDef12 java.lang.String
	 */
	public void setDef12 (Object newDef12 ) {
	 	this.def12 = newDef12;
	} 	  
	/**
	 * 属性def12_name的Getter方法.属性名：自定义项12名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef12_name () {
		return def12_name;
	}   
	/**
	 * 属性def12_name的Setter方法.属性名：自定义项12名称
	 * 创建日期:
	 * @param newDef12_name java.lang.String
	 */
	public void setDef12_name (java.lang.String newDef12_name ) {
	 	this.def12_name = newDef12_name;
	} 	  
	/**
	 * 属性def13的Getter方法.属性名：自定义项13
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef13 () {
		return def13;
	}   
	/**
	 * 属性def13的Setter方法.属性名：自定义项13
	 * 创建日期:
	 * @param newDef13 java.lang.String
	 */
	public void setDef13 (Object newDef13 ) {
	 	this.def13 = newDef13;
	} 	  
	/**
	 * 属性def13_name的Getter方法.属性名：自定义项13名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef13_name () {
		return def13_name;
	}   
	/**
	 * 属性def13_name的Setter方法.属性名：自定义项13名称
	 * 创建日期:
	 * @param newDef13_name java.lang.String
	 */
	public void setDef13_name (java.lang.String newDef13_name ) {
	 	this.def13_name = newDef13_name;
	} 	  
	/**
	 * 属性def14的Getter方法.属性名：自定义项14
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef14 () {
		return def14;
	}   
	/**
	 * 属性def14的Setter方法.属性名：自定义项14
	 * 创建日期:
	 * @param newDef14 java.lang.String
	 */
	public void setDef14 (Object newDef14 ) {
	 	this.def14 = newDef14;
	} 	  
	/**
	 * 属性def14_name的Getter方法.属性名：自定义项14名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef14_name () {
		return def14_name;
	}   
	/**
	 * 属性def14_name的Setter方法.属性名：自定义项14名称
	 * 创建日期:
	 * @param newDef14_name java.lang.String
	 */
	public void setDef14_name (java.lang.String newDef14_name ) {
	 	this.def14_name = newDef14_name;
	} 	  
	/**
	 * 属性def15的Getter方法.属性名：自定义项15
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef15 () {
		return def15;
	}   
	/**
	 * 属性def15的Setter方法.属性名：自定义项15
	 * 创建日期:
	 * @param newDef15 java.lang.String
	 */
	public void setDef15 (Object newDef15 ) {
	 	this.def15 = newDef15;
	} 	  
	/**
	 * 属性def15_name的Getter方法.属性名：自定义项15名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef15_name () {
		return def15_name;
	}   
	/**
	 * 属性def15_name的Setter方法.属性名：自定义项15名称
	 * 创建日期:
	 * @param newDef15_name java.lang.String
	 */
	public void setDef15_name (java.lang.String newDef15_name ) {
	 	this.def15_name = newDef15_name;
	} 	  
	/**
	 * 属性def16的Getter方法.属性名：自定义项16
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef16 () {
		return def16;
	}   
	/**
	 * 属性def16的Setter方法.属性名：自定义项16
	 * 创建日期:
	 * @param newDef16 java.lang.String
	 */
	public void setDef16 (Object newDef16 ) {
	 	this.def16 = newDef16;
	} 	  
	/**
	 * 属性def16_name的Getter方法.属性名：自定义项16名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef16_name () {
		return def16_name;
	}   
	/**
	 * 属性def16_name的Setter方法.属性名：自定义项16名称
	 * 创建日期:
	 * @param newDef16_name java.lang.String
	 */
	public void setDef16_name (java.lang.String newDef16_name ) {
	 	this.def16_name = newDef16_name;
	} 	  
	/**
	 * 属性def17的Getter方法.属性名：自定义项17
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef17 () {
		return def17;
	}   
	/**
	 * 属性def17的Setter方法.属性名：自定义项17
	 * 创建日期:
	 * @param newDef17 java.lang.String
	 */
	public void setDef17 (Object newDef17 ) {
	 	this.def17 = newDef17;
	} 	  
	/**
	 * 属性def17_name的Getter方法.属性名：自定义项17名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef17_name () {
		return def17_name;
	}   
	/**
	 * 属性def17_name的Setter方法.属性名：自定义项17名称
	 * 创建日期:
	 * @param newDef17_name java.lang.String
	 */
	public void setDef17_name (java.lang.String newDef17_name ) {
	 	this.def17_name = newDef17_name;
	} 	  
	/**
	 * 属性def18的Getter方法.属性名：自定义项18
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef18 () {
		return def18;
	}   
	/**
	 * 属性def18的Setter方法.属性名：自定义项18
	 * 创建日期:
	 * @param newDef18 java.lang.String
	 */
	public void setDef18 (Object newDef18 ) {
	 	this.def18 = newDef18;
	} 	  
	/**
	 * 属性def18_name的Getter方法.属性名：自定义项18名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef18_name () {
		return def18_name;
	}   
	/**
	 * 属性def18_name的Setter方法.属性名：自定义项18名称
	 * 创建日期:
	 * @param newDef18_name java.lang.String
	 */
	public void setDef18_name (java.lang.String newDef18_name ) {
	 	this.def18_name = newDef18_name;
	} 	  
	/**
	 * 属性def19的Getter方法.属性名：自定义项19
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef19 () {
		return def19;
	}   
	/**
	 * 属性def19的Setter方法.属性名：自定义项19
	 * 创建日期:
	 * @param newDef19 java.lang.String
	 */
	public void setDef19 (Object newDef19 ) {
	 	this.def19 = newDef19;
	} 	  
	/**
	 * 属性def19_name的Getter方法.属性名：自定义项19名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef19_name () {
		return def19_name;
	}   
	/**
	 * 属性def19_name的Setter方法.属性名：自定义项19名称
	 * 创建日期:
	 * @param newDef19_name java.lang.String
	 */
	public void setDef19_name (java.lang.String newDef19_name ) {
	 	this.def19_name = newDef19_name;
	} 	  
	/**
	 * 属性def20的Getter方法.属性名：自定义项20
	 * 创建日期:
	 * @return java.lang.String
	 */
	public Object getDef20 () {
		return def20;
	}   
	/**
	 * 属性def20的Setter方法.属性名：自定义项20
	 * 创建日期:
	 * @param newDef20 java.lang.String
	 */
	public void setDef20 (Object newDef20 ) {
	 	this.def20 = newDef20;
	} 	  
	/**
	 * 属性def20_name的Getter方法.属性名：自定义项20名称
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDef20_name () {
		return def20_name;
	}   
	/**
	 * 属性def20_name的Setter方法.属性名：自定义项20名称
	 * 创建日期:
	 * @param newDef20_name java.lang.String
	 */
	public void setDef20_name (java.lang.String newDef20_name ) {
	 	this.def20_name = newDef20_name;
	} 	  
	/**
	 * 属性priority的Getter方法.属性名：优先级
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getPriority () {
		return priority;
	}   
	/**
	 * 属性priority的Setter方法.属性名：优先级
	 * 创建日期:
	 * @param newPriority java.lang.Integer
	 */
	public void setPriority (java.lang.Integer newPriority ) {
	 	this.priority = newPriority;
	} 	  
	public java.lang.String getShowitem() {
		return showitem;
	}
	public void setShowitem(java.lang.String showitem) {
		this.showitem = showitem;
	}
	public java.lang.String getControlitem() {
		return controlitem;
	}
	public void setControlitem(java.lang.String controlitem) {
		this.controlitem = controlitem;
	}
	public java.lang.String getShowitem_name() {
		return showitem_name;
	}
	public void setShowitem_name(java.lang.String showitemName) {
		showitem_name = showitemName;
	}
	public java.lang.String getControlitem_name() {
		return controlitem_name;
	}
	public void setControlitem_name(java.lang.String controlitemName) {
		controlitem_name = controlitemName;
	}
	public java.lang.Integer getControlflag() {
		return controlflag;
	}
	public void setControlflag(java.lang.Integer controlflag) {
		this.controlflag = controlflag;
	}
	public java.lang.String getControlformula() {
		return controlformula;
	}
	public void setControlformula(java.lang.String controlformula) {
		this.controlformula = controlformula;
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
	 * 属性defformula的Getter方法.属性名：自定义公式
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefformula () {
		return defformula;
	}   
	/**
	 * 属性defformula的Setter方法.属性名：自定义公式
	 * 创建日期:
	 * @param newDefformula java.lang.String
	 */
	public void setDefformula (java.lang.String newDefformula ) {
	 	this.defformula = newDefformula;
	} 	  
	/**
	 * 属性validateformula的Getter方法.属性名：验证公式
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getValidateformula () {
		return validateformula;
	}   
	/**
	 * 属性validateformula的Setter方法.属性名：验证公式
	 * 创建日期:
	 * @param newValidateformula java.lang.String
	 */
	public void setValidateformula (java.lang.String newValidateformula ) {
	 	this.validateformula = newValidateformula;
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
	  return "pk_reimrule";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_reimruler";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_reimruler";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public ReimRulerVO() {
		super();	
	}    
	
	@nc.vo.annotation.MDEntityInfo(beanFullclassName =  "nc.vo.er.reimrule.ReimRulerVO" )
	public IVOMeta getMetaData() {
   		return null;
  	}
	
	@Override
	public int compareTo(Object o) {
		if(o ==null || ! (o instanceof ReimRulerVO))
			return -1;
		ReimRulerVO rule=(ReimRulerVO)o;
		if(priority==null)
			priority=new Integer(0);
		Integer priority2 = rule.getPriority();
		if(priority2==null)
			priority2=new Integer(0);
		return priority-priority2;
//		if(rule.getPk_expensetype().equals(getPk_expensetype())){
//			if(priority==null)
//				priority=new Integer(0);
//			Integer priority2 = rule.getPriority();
//			if(priority2==null)
//				priority2=new Integer(0);
//			return priority-priority2;
//		}else{
//			return rule.getPk_expensetype().compareTo(getPk_expensetype());
//		}
	}
} 


