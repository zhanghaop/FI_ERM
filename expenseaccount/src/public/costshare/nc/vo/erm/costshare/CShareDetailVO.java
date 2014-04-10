/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product. It is based on Velocity.     *
\***************************************************************/
package nc.vo.erm.costshare;
	
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
public class CShareDetailVO extends SuperVO {
	private java.lang.String pk_costshare;
	private java.lang.String pk_cshare_detail;
	private java.lang.String billno;
	private java.lang.String pk_billtype;
	private java.lang.String pk_tradetype;
	private java.lang.Integer billstatus;
	private java.lang.String assume_org;
	private java.lang.String assume_dept;
	private java.lang.String pk_pcorg;
	private java.lang.String pk_resacostcenter;
	private java.lang.String pk_iobsclass;
	private java.lang.String jobid;
	private java.lang.String projecttask;
	private java.lang.String pk_checkele;
	private java.lang.String customer;
	private java.lang.String hbbm;
	private nc.vo.pub.lang.UFDouble assume_amount;
	private nc.vo.pub.lang.UFDouble share_ratio;
	private java.lang.Integer src_type;
	private java.lang.String src_id;
	private java.lang.String bzbm;
	private nc.vo.pub.lang.UFDouble bbhl;
	private nc.vo.pub.lang.UFDouble bbje;
	private nc.vo.pub.lang.UFDouble globalbbje;
	private nc.vo.pub.lang.UFDouble groupbbje;
	private nc.vo.pub.lang.UFDouble globalbbhl;
	private nc.vo.pub.lang.UFDouble groupbbhl;
	private java.lang.String defitem30;
	private java.lang.String defitem29;
	private java.lang.String defitem28;
	private java.lang.String defitem27;
	private java.lang.String defitem26;
	private java.lang.String defitem25;
	private java.lang.String defitem24;
	private java.lang.String defitem23;
	private java.lang.String defitem22;
	private java.lang.String defitem21;
	private java.lang.String defitem20;
	private java.lang.String defitem19;
	private java.lang.String defitem18;
	private java.lang.String defitem17;
	private java.lang.String defitem16;
	private java.lang.String defitem15;
	private java.lang.String defitem14;
	private java.lang.String defitem13;
	private java.lang.String defitem12;
	private java.lang.String defitem11;
	private java.lang.String defitem10;
	private java.lang.String defitem9;
	private java.lang.String defitem8;
	private java.lang.String defitem7;
	private java.lang.String defitem6;
	private java.lang.String defitem5;
	private java.lang.String defitem4;
	private java.lang.String defitem3;
	private java.lang.String defitem2;
	private java.lang.String defitem1;
	private java.lang.String pk_org;
	private java.lang.String pk_group;
	private java.lang.Integer dr = 0;
	private nc.vo.pub.lang.UFDateTime ts;
	private java.lang.String pk_jkbx;

	public static final String PK_COSTSHARE = "pk_costshare";
	public static final String PK_CSHARE_DETAIL = "pk_cshare_detail";
	public static final String BILLNO = "billno";
	public static final String PK_BILLTYPE = "pk_billtype";
	public static final String PK_TRADETYPE = "pk_tradetype";
	public static final String BILLSTATUS = "billstatus";
	public static final String ASSUME_ORG = "assume_org";
	public static final String ASSUME_DEPT = "assume_dept";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String PK_RESACOSTCENTER = "pk_resacostcenter";
	public static final String PK_IOBSCLASS = "pk_iobsclass";
	public static final String JOBID = "jobid";
	public static final String PROJECTTASK = "projecttask";
	public static final String PK_CHECKELE = "pk_checkele";
	public static final String CUSTOMER = "customer";
	public static final String HBBM = "hbbm";
	public static final String ASSUME_AMOUNT = "assume_amount";
	public static final String SHARE_RATIO = "share_ratio";
	public static final String SRC_TYPE = "src_type";
	public static final String SRC_ID = "src_id";
	public static final String BZBM = "bzbm";
	public static final String BBHL = "bbhl";
	public static final String BBJE = "bbje";
	public static final String GLOBALBBJE = "globalbbje";
	public static final String GROUPBBJE = "groupbbje";
	public static final String GLOBALBBHL = "globalbbhl";
	public static final String GROUPBBHL = "groupbbhl";
	public static final String DEFITEM30 = "defitem30";
	public static final String DEFITEM29 = "defitem29";
	public static final String DEFITEM28 = "defitem28";
	public static final String DEFITEM27 = "defitem27";
	public static final String DEFITEM26 = "defitem26";
	public static final String DEFITEM25 = "defitem25";
	public static final String DEFITEM24 = "defitem24";
	public static final String DEFITEM23 = "defitem23";
	public static final String DEFITEM22 = "defitem22";
	public static final String DEFITEM21 = "defitem21";
	public static final String DEFITEM20 = "defitem20";
	public static final String DEFITEM19 = "defitem19";
	public static final String DEFITEM18 = "defitem18";
	public static final String DEFITEM17 = "defitem17";
	public static final String DEFITEM16 = "defitem16";
	public static final String DEFITEM15 = "defitem15";
	public static final String DEFITEM14 = "defitem14";
	public static final String DEFITEM13 = "defitem13";
	public static final String DEFITEM12 = "defitem12";
	public static final String DEFITEM11 = "defitem11";
	public static final String DEFITEM10 = "defitem10";
	public static final String DEFITEM9 = "defitem9";
	public static final String DEFITEM8 = "defitem8";
	public static final String DEFITEM7 = "defitem7";
	public static final String DEFITEM6 = "defitem6";
	public static final String DEFITEM5 = "defitem5";
	public static final String DEFITEM4 = "defitem4";
	public static final String DEFITEM3 = "defitem3";
	public static final String DEFITEM2 = "defitem2";
	public static final String DEFITEM1 = "defitem1";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String PK_JKBX = "pk_jkbx";
	
	/**
	 * 属性pk_costshare的Getter方法.属性名：parentPK
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_costshare () {
		return pk_costshare;
	}   
	/**
	 * 属性pk_costshare的Setter方法.属性名：parentPK
	 * 创建日期:
	 * @param newPk_costshare java.lang.String
	 */
	public void setPk_costshare (java.lang.String newPk_costshare ) {
	 	this.pk_costshare = newPk_costshare;
	} 	  
	/**
	 * 属性pk_cshare_detail的Getter方法.属性名：主键
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_cshare_detail () {
		return pk_cshare_detail;
	}   
	/**
	 * 属性pk_cshare_detail的Setter方法.属性名：主键
	 * 创建日期:
	 * @param newPk_cshare_detail java.lang.String
	 */
	public void setPk_cshare_detail (java.lang.String newPk_cshare_detail ) {
	 	this.pk_cshare_detail = newPk_cshare_detail;
	} 	  
	/**
	 * 属性billno的Getter方法.属性名：单据编号
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getBillno () {
		return billno;
	}   
	/**
	 * 属性billno的Setter方法.属性名：单据编号
	 * 创建日期:
	 * @param newBillno java.lang.String
	 */
	public void setBillno (java.lang.String newBillno ) {
	 	this.billno = newBillno;
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
	 * 属性pk_tradetype的Getter方法.属性名：交易类型
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_tradetype () {
		return pk_tradetype;
	}   
	/**
	 * 属性pk_tradetype的Setter方法.属性名：交易类型
	 * 创建日期:
	 * @param newPk_tradetype java.lang.String
	 */
	public void setPk_tradetype (java.lang.String newPk_tradetype ) {
	 	this.pk_tradetype = newPk_tradetype;
	} 	  
	/**
	 * 属性billstatus的Getter方法.属性名：单据状态
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getBillstatus () {
		return billstatus;
	}   
	/**
	 * 属性billstatus的Setter方法.属性名：单据状态
	 * 创建日期:
	 * @param newBillstatus java.lang.Integer
	 */
	public void setBillstatus (java.lang.Integer newBillstatus ) {
	 	this.billstatus = newBillstatus;
	} 	  
	/**
	 * 属性assume_org的Getter方法.属性名：承担单位
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getAssume_org () {
		return assume_org;
	}   
	/**
	 * 属性assume_org的Setter方法.属性名：承担单位
	 * 创建日期:
	 * @param newAssume_org java.lang.String
	 */
	public void setAssume_org (java.lang.String newAssume_org ) {
	 	this.assume_org = newAssume_org;
	} 	  
	/**
	 * 属性assume_dept的Getter方法.属性名：承担部门
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getAssume_dept () {
		return assume_dept;
	}   
	/**
	 * 属性assume_dept的Setter方法.属性名：承担部门
	 * 创建日期:
	 * @param newAssume_dept java.lang.String
	 */
	public void setAssume_dept (java.lang.String newAssume_dept ) {
	 	this.assume_dept = newAssume_dept;
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
	 * 属性pk_resacostcenter的Getter方法.属性名：成本中心
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_resacostcenter () {
		return pk_resacostcenter;
	}   
	/**
	 * 属性pk_resacostcenter的Setter方法.属性名：成本中心
	 * 创建日期:
	 * @param newPk_resacostcenter java.lang.String
	 */
	public void setPk_resacostcenter (java.lang.String newPk_resacostcenter ) {
	 	this.pk_resacostcenter = newPk_resacostcenter;
	} 	  
	/**
	 * 属性pk_iobsclass的Getter方法.属性名：收支项目
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_iobsclass () {
		return pk_iobsclass;
	}   
	/**
	 * 属性pk_iobsclass的Setter方法.属性名：收支项目
	 * 创建日期:
	 * @param newPk_iobsclass java.lang.String
	 */
	public void setPk_iobsclass (java.lang.String newPk_iobsclass ) {
	 	this.pk_iobsclass = newPk_iobsclass;
	} 	  
	/**
	 * 属性jobid的Getter方法.属性名：项目
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getJobid () {
		return jobid;
	}   
	/**
	 * 属性jobid的Setter方法.属性名：项目
	 * 创建日期:
	 * @param newJobid java.lang.String
	 */
	public void setJobid (java.lang.String newJobid ) {
	 	this.jobid = newJobid;
	} 	  
	/**
	 * 属性projecttask的Getter方法.属性名：项目任务
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getProjecttask () {
		return projecttask;
	}   
	/**
	 * 属性projecttask的Setter方法.属性名：项目任务
	 * 创建日期:
	 * @param newProjecttask java.lang.String
	 */
	public void setProjecttask (java.lang.String newProjecttask ) {
	 	this.projecttask = newProjecttask;
	} 	  
	/**
	 * 属性pk_checkele的Getter方法.属性名：核算要素
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getPk_checkele () {
		return pk_checkele;
	}   
	/**
	 * 属性pk_checkele的Setter方法.属性名：核算要素
	 * 创建日期:
	 * @param newPk_checkele java.lang.String
	 */
	public void setPk_checkele (java.lang.String newPk_checkele ) {
	 	this.pk_checkele = newPk_checkele;
	} 	  
	/**
	 * 属性customer的Getter方法.属性名：客户
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getCustomer () {
		return customer;
	}   
	/**
	 * 属性customer的Setter方法.属性名：客户
	 * 创建日期:
	 * @param newCustomer java.lang.String
	 */
	public void setCustomer (java.lang.String newCustomer ) {
	 	this.customer = newCustomer;
	} 	  
	/**
	 * 属性hbbm的Getter方法.属性名：供应商
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getHbbm () {
		return hbbm;
	}   
	/**
	 * 属性hbbm的Setter方法.属性名：供应商
	 * 创建日期:
	 * @param newHbbm java.lang.String
	 */
	public void setHbbm (java.lang.String newHbbm ) {
	 	this.hbbm = newHbbm;
	} 	  
	/**
	 * 属性assume_amount的Getter方法.属性名：承担金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getAssume_amount () {
		return assume_amount;
	}   
	/**
	 * 属性assume_amount的Setter方法.属性名：承担金额
	 * 创建日期:
	 * @param newAssume_amount nc.vo.pub.lang.UFDouble
	 */
	public void setAssume_amount (nc.vo.pub.lang.UFDouble newAssume_amount ) {
	 	this.assume_amount = newAssume_amount;
	} 	  
	/**
	 * 属性share_ratio的Getter方法.属性名：分摊比例
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getShare_ratio () {
		return share_ratio;
	}   
	/**
	 * 属性share_ratio的Setter方法.属性名：分摊比例
	 * 创建日期:
	 * @param newShare_ratio nc.vo.pub.lang.UFDouble
	 */
	public void setShare_ratio (nc.vo.pub.lang.UFDouble newShare_ratio ) {
	 	this.share_ratio = newShare_ratio;
	} 	  
	/**
	 * 属性src_type的Getter方法.属性名：来源方式
	 * 创建日期:
	 * @return java.lang.Integer
	 */
	public java.lang.Integer getSrc_type () {
		return src_type;
	}   
	/**
	 * 属性src_type的Setter方法.属性名：来源方式
	 * 创建日期:
	 * @param newSrc_type java.lang.Integer
	 */
	public void setSrc_type (java.lang.Integer newSrc_type ) {
	 	this.src_type = newSrc_type;
	} 	  
	/**
	 * 属性src_id的Getter方法.属性名：来源单据ID
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getSrc_id () {
		return src_id;
	}   
	/**
	 * 属性src_id的Setter方法.属性名：来源单据ID
	 * 创建日期:
	 * @param newSrc_id java.lang.String
	 */
	public void setSrc_id (java.lang.String newSrc_id ) {
	 	this.src_id = newSrc_id;
	} 	  
	/**
	 * 属性bzbm的Getter方法.属性名：币种
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getBzbm () {
		return bzbm;
	}   
	/**
	 * 属性bzbm的Setter方法.属性名：币种
	 * 创建日期:
	 * @param newBzbm java.lang.String
	 */
	public void setBzbm (java.lang.String newBzbm ) {
	 	this.bzbm = newBzbm;
	} 	  
	/**
	 * 属性bbhl的Getter方法.属性名：组织本币汇率
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getBbhl () {
		return bbhl;
	}   
	/**
	 * 属性bbhl的Setter方法.属性名：组织本币汇率
	 * 创建日期:
	 * @param newBbhl nc.vo.pub.lang.UFDouble
	 */
	public void setBbhl (nc.vo.pub.lang.UFDouble newBbhl ) {
	 	this.bbhl = newBbhl;
	} 	  
	/**
	 * 属性bbje的Getter方法.属性名：组织本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getBbje () {
		return bbje;
	}   
	/**
	 * 属性bbje的Setter方法.属性名：组织本币金额
	 * 创建日期:
	 * @param newBbje nc.vo.pub.lang.UFDouble
	 */
	public void setBbje (nc.vo.pub.lang.UFDouble newBbje ) {
	 	this.bbje = newBbje;
	} 	  
	/**
	 * 属性globalbbje的Getter方法.属性名：全局报销本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobalbbje () {
		return globalbbje;
	}   
	/**
	 * 属性globalbbje的Setter方法.属性名：全局报销本币金额
	 * 创建日期:
	 * @param newGlobalbbje nc.vo.pub.lang.UFDouble
	 */
	public void setGlobalbbje (nc.vo.pub.lang.UFDouble newGlobalbbje ) {
	 	this.globalbbje = newGlobalbbje;
	} 	  
	/**
	 * 属性groupbbje的Getter方法.属性名：集团报销本币金额
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroupbbje () {
		return groupbbje;
	}   
	/**
	 * 属性groupbbje的Setter方法.属性名：集团报销本币金额
	 * 创建日期:
	 * @param newGroupbbje nc.vo.pub.lang.UFDouble
	 */
	public void setGroupbbje (nc.vo.pub.lang.UFDouble newGroupbbje ) {
	 	this.groupbbje = newGroupbbje;
	} 	  
	/**
	 * 属性globalbbhl的Getter方法.属性名：全局本币汇率
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGlobalbbhl () {
		return globalbbhl;
	}   
	/**
	 * 属性globalbbhl的Setter方法.属性名：全局本币汇率
	 * 创建日期:
	 * @param newGlobalbbhl nc.vo.pub.lang.UFDouble
	 */
	public void setGlobalbbhl (nc.vo.pub.lang.UFDouble newGlobalbbhl ) {
	 	this.globalbbhl = newGlobalbbhl;
	} 	  
	/**
	 * 属性groupbbhl的Getter方法.属性名：集团本币汇率
	 * 创建日期:
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public nc.vo.pub.lang.UFDouble getGroupbbhl () {
		return groupbbhl;
	}   
	/**
	 * 属性groupbbhl的Setter方法.属性名：集团本币汇率
	 * 创建日期:
	 * @param newGroupbbhl nc.vo.pub.lang.UFDouble
	 */
	public void setGroupbbhl (nc.vo.pub.lang.UFDouble newGroupbbhl ) {
	 	this.groupbbhl = newGroupbbhl;
	} 	  
	/**
	 * 属性defitem30的Getter方法.属性名：自定义项30
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem30 () {
		return defitem30;
	}   
	/**
	 * 属性defitem30的Setter方法.属性名：自定义项30
	 * 创建日期:
	 * @param newDefitem30 java.lang.String
	 */
	public void setDefitem30 (java.lang.String newDefitem30 ) {
	 	this.defitem30 = newDefitem30;
	} 	  
	/**
	 * 属性defitem29的Getter方法.属性名：自定义项29
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem29 () {
		return defitem29;
	}   
	/**
	 * 属性defitem29的Setter方法.属性名：自定义项29
	 * 创建日期:
	 * @param newDefitem29 java.lang.String
	 */
	public void setDefitem29 (java.lang.String newDefitem29 ) {
	 	this.defitem29 = newDefitem29;
	} 	  
	/**
	 * 属性defitem28的Getter方法.属性名：自定义项28
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem28 () {
		return defitem28;
	}   
	/**
	 * 属性defitem28的Setter方法.属性名：自定义项28
	 * 创建日期:
	 * @param newDefitem28 java.lang.String
	 */
	public void setDefitem28 (java.lang.String newDefitem28 ) {
	 	this.defitem28 = newDefitem28;
	} 	  
	/**
	 * 属性defitem27的Getter方法.属性名：自定义项27
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem27 () {
		return defitem27;
	}   
	/**
	 * 属性defitem27的Setter方法.属性名：自定义项27
	 * 创建日期:
	 * @param newDefitem27 java.lang.String
	 */
	public void setDefitem27 (java.lang.String newDefitem27 ) {
	 	this.defitem27 = newDefitem27;
	} 	  
	/**
	 * 属性defitem26的Getter方法.属性名：自定义项26
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem26 () {
		return defitem26;
	}   
	/**
	 * 属性defitem26的Setter方法.属性名：自定义项26
	 * 创建日期:
	 * @param newDefitem26 java.lang.String
	 */
	public void setDefitem26 (java.lang.String newDefitem26 ) {
	 	this.defitem26 = newDefitem26;
	} 	  
	/**
	 * 属性defitem25的Getter方法.属性名：自定义项25
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem25 () {
		return defitem25;
	}   
	/**
	 * 属性defitem25的Setter方法.属性名：自定义项25
	 * 创建日期:
	 * @param newDefitem25 java.lang.String
	 */
	public void setDefitem25 (java.lang.String newDefitem25 ) {
	 	this.defitem25 = newDefitem25;
	} 	  
	/**
	 * 属性defitem24的Getter方法.属性名：自定义项24
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem24 () {
		return defitem24;
	}   
	/**
	 * 属性defitem24的Setter方法.属性名：自定义项24
	 * 创建日期:
	 * @param newDefitem24 java.lang.String
	 */
	public void setDefitem24 (java.lang.String newDefitem24 ) {
	 	this.defitem24 = newDefitem24;
	} 	  
	/**
	 * 属性defitem23的Getter方法.属性名：自定义项23
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem23 () {
		return defitem23;
	}   
	/**
	 * 属性defitem23的Setter方法.属性名：自定义项23
	 * 创建日期:
	 * @param newDefitem23 java.lang.String
	 */
	public void setDefitem23 (java.lang.String newDefitem23 ) {
	 	this.defitem23 = newDefitem23;
	} 	  
	/**
	 * 属性defitem22的Getter方法.属性名：自定义项22
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem22 () {
		return defitem22;
	}   
	/**
	 * 属性defitem22的Setter方法.属性名：自定义项22
	 * 创建日期:
	 * @param newDefitem22 java.lang.String
	 */
	public void setDefitem22 (java.lang.String newDefitem22 ) {
	 	this.defitem22 = newDefitem22;
	} 	  
	/**
	 * 属性defitem21的Getter方法.属性名：自定义项21
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem21 () {
		return defitem21;
	}   
	/**
	 * 属性defitem21的Setter方法.属性名：自定义项21
	 * 创建日期:
	 * @param newDefitem21 java.lang.String
	 */
	public void setDefitem21 (java.lang.String newDefitem21 ) {
	 	this.defitem21 = newDefitem21;
	} 	  
	/**
	 * 属性defitem20的Getter方法.属性名：自定义项20
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem20 () {
		return defitem20;
	}   
	/**
	 * 属性defitem20的Setter方法.属性名：自定义项20
	 * 创建日期:
	 * @param newDefitem20 java.lang.String
	 */
	public void setDefitem20 (java.lang.String newDefitem20 ) {
	 	this.defitem20 = newDefitem20;
	} 	  
	/**
	 * 属性defitem19的Getter方法.属性名：自定义项19
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem19 () {
		return defitem19;
	}   
	/**
	 * 属性defitem19的Setter方法.属性名：自定义项19
	 * 创建日期:
	 * @param newDefitem19 java.lang.String
	 */
	public void setDefitem19 (java.lang.String newDefitem19 ) {
	 	this.defitem19 = newDefitem19;
	} 	  
	/**
	 * 属性defitem18的Getter方法.属性名：自定义项18
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem18 () {
		return defitem18;
	}   
	/**
	 * 属性defitem18的Setter方法.属性名：自定义项18
	 * 创建日期:
	 * @param newDefitem18 java.lang.String
	 */
	public void setDefitem18 (java.lang.String newDefitem18 ) {
	 	this.defitem18 = newDefitem18;
	} 	  
	/**
	 * 属性defitem17的Getter方法.属性名：自定义项17
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem17 () {
		return defitem17;
	}   
	/**
	 * 属性defitem17的Setter方法.属性名：自定义项17
	 * 创建日期:
	 * @param newDefitem17 java.lang.String
	 */
	public void setDefitem17 (java.lang.String newDefitem17 ) {
	 	this.defitem17 = newDefitem17;
	} 	  
	/**
	 * 属性defitem16的Getter方法.属性名：自定义项16
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem16 () {
		return defitem16;
	}   
	/**
	 * 属性defitem16的Setter方法.属性名：自定义项16
	 * 创建日期:
	 * @param newDefitem16 java.lang.String
	 */
	public void setDefitem16 (java.lang.String newDefitem16 ) {
	 	this.defitem16 = newDefitem16;
	} 	  
	/**
	 * 属性defitem15的Getter方法.属性名：自定义项15
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem15 () {
		return defitem15;
	}   
	/**
	 * 属性defitem15的Setter方法.属性名：自定义项15
	 * 创建日期:
	 * @param newDefitem15 java.lang.String
	 */
	public void setDefitem15 (java.lang.String newDefitem15 ) {
	 	this.defitem15 = newDefitem15;
	} 	  
	/**
	 * 属性defitem14的Getter方法.属性名：自定义项14
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem14 () {
		return defitem14;
	}   
	/**
	 * 属性defitem14的Setter方法.属性名：自定义项14
	 * 创建日期:
	 * @param newDefitem14 java.lang.String
	 */
	public void setDefitem14 (java.lang.String newDefitem14 ) {
	 	this.defitem14 = newDefitem14;
	} 	  
	/**
	 * 属性defitem13的Getter方法.属性名：自定义项13
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem13 () {
		return defitem13;
	}   
	/**
	 * 属性defitem13的Setter方法.属性名：自定义项13
	 * 创建日期:
	 * @param newDefitem13 java.lang.String
	 */
	public void setDefitem13 (java.lang.String newDefitem13 ) {
	 	this.defitem13 = newDefitem13;
	} 	  
	/**
	 * 属性defitem12的Getter方法.属性名：自定义项12
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem12 () {
		return defitem12;
	}   
	/**
	 * 属性defitem12的Setter方法.属性名：自定义项12
	 * 创建日期:
	 * @param newDefitem12 java.lang.String
	 */
	public void setDefitem12 (java.lang.String newDefitem12 ) {
	 	this.defitem12 = newDefitem12;
	} 	  
	/**
	 * 属性defitem11的Getter方法.属性名：自定义项11
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem11 () {
		return defitem11;
	}   
	/**
	 * 属性defitem11的Setter方法.属性名：自定义项11
	 * 创建日期:
	 * @param newDefitem11 java.lang.String
	 */
	public void setDefitem11 (java.lang.String newDefitem11 ) {
	 	this.defitem11 = newDefitem11;
	} 	  
	/**
	 * 属性defitem10的Getter方法.属性名：自定义项10
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem10 () {
		return defitem10;
	}   
	/**
	 * 属性defitem10的Setter方法.属性名：自定义项10
	 * 创建日期:
	 * @param newDefitem10 java.lang.String
	 */
	public void setDefitem10 (java.lang.String newDefitem10 ) {
	 	this.defitem10 = newDefitem10;
	} 	  
	/**
	 * 属性defitem9的Getter方法.属性名：自定义项9
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem9 () {
		return defitem9;
	}   
	/**
	 * 属性defitem9的Setter方法.属性名：自定义项9
	 * 创建日期:
	 * @param newDefitem9 java.lang.String
	 */
	public void setDefitem9 (java.lang.String newDefitem9 ) {
	 	this.defitem9 = newDefitem9;
	} 	  
	/**
	 * 属性defitem8的Getter方法.属性名：自定义项8
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem8 () {
		return defitem8;
	}   
	/**
	 * 属性defitem8的Setter方法.属性名：自定义项8
	 * 创建日期:
	 * @param newDefitem8 java.lang.String
	 */
	public void setDefitem8 (java.lang.String newDefitem8 ) {
	 	this.defitem8 = newDefitem8;
	} 	  
	/**
	 * 属性defitem7的Getter方法.属性名：自定义项7
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem7 () {
		return defitem7;
	}   
	/**
	 * 属性defitem7的Setter方法.属性名：自定义项7
	 * 创建日期:
	 * @param newDefitem7 java.lang.String
	 */
	public void setDefitem7 (java.lang.String newDefitem7 ) {
	 	this.defitem7 = newDefitem7;
	} 	  
	/**
	 * 属性defitem6的Getter方法.属性名：自定义项6
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem6 () {
		return defitem6;
	}   
	/**
	 * 属性defitem6的Setter方法.属性名：自定义项6
	 * 创建日期:
	 * @param newDefitem6 java.lang.String
	 */
	public void setDefitem6 (java.lang.String newDefitem6 ) {
	 	this.defitem6 = newDefitem6;
	} 	  
	/**
	 * 属性defitem5的Getter方法.属性名：自定义项5
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem5 () {
		return defitem5;
	}   
	/**
	 * 属性defitem5的Setter方法.属性名：自定义项5
	 * 创建日期:
	 * @param newDefitem5 java.lang.String
	 */
	public void setDefitem5 (java.lang.String newDefitem5 ) {
	 	this.defitem5 = newDefitem5;
	} 	  
	/**
	 * 属性defitem4的Getter方法.属性名：自定义项4
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem4 () {
		return defitem4;
	}   
	/**
	 * 属性defitem4的Setter方法.属性名：自定义项4
	 * 创建日期:
	 * @param newDefitem4 java.lang.String
	 */
	public void setDefitem4 (java.lang.String newDefitem4 ) {
	 	this.defitem4 = newDefitem4;
	} 	  
	/**
	 * 属性defitem3的Getter方法.属性名：自定义项3
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem3 () {
		return defitem3;
	}   
	/**
	 * 属性defitem3的Setter方法.属性名：自定义项3
	 * 创建日期:
	 * @param newDefitem3 java.lang.String
	 */
	public void setDefitem3 (java.lang.String newDefitem3 ) {
	 	this.defitem3 = newDefitem3;
	} 	  
	/**
	 * 属性defitem2的Getter方法.属性名：自定义项2
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem2 () {
		return defitem2;
	}   
	/**
	 * 属性defitem2的Setter方法.属性名：自定义项2
	 * 创建日期:
	 * @param newDefitem2 java.lang.String
	 */
	public void setDefitem2 (java.lang.String newDefitem2 ) {
	 	this.defitem2 = newDefitem2;
	} 	  
	/**
	 * 属性defitem1的Getter方法.属性名：自定义项1
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getDefitem1 () {
		return defitem1;
	}   
	/**
	 * 属性defitem1的Setter方法.属性名：自定义项1
	 * 创建日期:
	 * @param newDefitem1 java.lang.String
	 */
	public void setDefitem1 (java.lang.String newDefitem1 ) {
	 	this.defitem1 = newDefitem1;
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
		return "pk_costshare";
	}   
    
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:
	  * @return java.lang.String
	  */
	public java.lang.String getPKFieldName() {
	  return "pk_cshare_detail";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public java.lang.String getTableName() {
		return "er_cshare_detail";
	}    
	
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:
	 * @return java.lang.String
	 */
	public static java.lang.String getDefaultTableName() {
		return "er_cshare_detail";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:
	  */
     public CShareDetailVO() {
		super();	
	}
	public void setPk_jkbx(java.lang.String pk_jkbx) {
		this.pk_jkbx = pk_jkbx;
	}
	public java.lang.String getPk_jkbx() {
		return pk_jkbx;
	}    
} 


