package nc.vo.ep.bx;

import java.util.ArrayList;

import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

/**
 * 借款报销表体业务信息VO
 * 
 * @author twei modified by chendya since v6.1
 *         将财务行VO删除，将财务行所有有用字段移植到业务行(报销单早些时候的版本是没有表体行的，所以特意构造了财务行金额传结算，会计平台，预算等)
 * 
 *         nc.vo.ep.bx.BXBusItemVO
 */
public class BXBusItemVO extends SuperVO {

	private static final long serialVersionUID = -5576693230695877687L;

	private static String getErromsg() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
			"UPP2011-000281")/*
							 * @res "表体下列字段不能为空:\n"
							 */;
    }

	// ---------------------------------begin added by lvhj
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的借款单明细pk
	 */
	private String jk_busitemPK;
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的报销单明细pk
	 */
	private String bx_busitemPK;

	// 增加项目管理、责任会计支持的固定业务字段---------------------------------

	/**
	 * 成本中心
	 */
	public static final String PK_RESACOSTCENTER = "pk_resacostcenter";
	/**
	 * 核算要素
	 */
	public static final String PK_CHECKELE = "pk_checkele";
	/**
	 * 利润中心
	 */
	public static final String PK_PCORG = "pk_pcorg";
	/**
	 * 利润中心版本化
	 */
	public static final String PK_PCORG_V = "pk_pcorg_v";
	/**
	 * 项目
	 */
	public static final String JOBID = "jobid";
	/**
	 * 项目任务
	 */
	public static final String PROJECTTASK = "projecttask";
	/**
	 * 费用申请单
	 */
	public static final String PK_ITEM = "pk_item";
	/**
	 * 费用申请单明细
	 */
	public static final String PK_MTAPP_DETAIL = "pk_mtapp_detail";
	/**
	 * 来源交易类型
	 */
	public static final String SRCBILLTYPE = "srcbilltype";
	/**
	 * 来源类型，默认为费用申请单
	 */
	public static final String SRCTYPE = "srctype";

	private String pk_resacostcenter;// 成本中心
	private String pk_checkele; // 核算要素
	private String pk_pcorg; // 利润中心
	private String pk_pcorg_v;// 利润中心版本化
	private String projecttask;// 项目任务
	//ehp2新增字段
	private String dwbm;//报销人单位
	private String deptid; //报销人部门
	//private String bxr; //报销人
	public Integer paytarget; //收款对象
	private String receiver;//收款人
	private String skyhzh ;//个人银行帐户
	private String hbbm ; //供应商
	private String customer ;//客户
	private String custaccount;//客商银行帐户
	private String freecust ; //散户
	private String freeaccount;//散户银行帐户

	public Integer getPaytarget() {
		return paytarget;
	}

	public void setPaytarget(Integer paytarget) {
		this.paytarget = paytarget;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSkyhzh() {
		return skyhzh;
	}

	public void setSkyhzh(String skyhzh) {
		this.skyhzh = skyhzh;
	}

	public String getHbbm() {
		return hbbm;
	}

	public void setHbbm(String hbbm) {
		this.hbbm = hbbm;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getCustaccount() {
		return custaccount;
	}

	public void setCustaccount(String custaccount) {
		this.custaccount = custaccount;
	}

	public String getFreecust() {
		return freecust;
	}

	public void setFreecust(String freecust) {
		this.freecust = freecust;
	}

	public String getFreeaccount() {
		return freeaccount;
	}

	public void setFreeaccount(String freeaccount) {
		this.freeaccount = freeaccount;
	}

	public String getDwbm() {
		return dwbm;
	}

	public void setDwbm(String dwbm) {
		this.dwbm = dwbm;
	}

	public String getDeptid() {
		return deptid;
	}

	public void setDeptid(String deptid) {
		this.deptid = deptid;
	}

//	public String getBxr() {
//		return bxr;
//	}
//
//	public void setBxr(String bxr) {
//		this.bxr = bxr;
//	}

	public String getPk_bxcontrast() {
		return pk_bxcontrast;
	}

	public void setPk_bxcontrast(String pkBxcontrast) {
		pk_bxcontrast = pkBxcontrast;
	}

	private String pk_item;
	private String pk_mtapp_detail;
	/**
	 * 来源单据类型
	 */
	private String srcbilltype;
	private String srctype;
	// 拉单相关的字段
	public static final String[] MTAPP_FIELDS = new String[] { PK_ITEM, SRCBILLTYPE, SRCTYPE };

	private String pk_bxcontrast;// 冲销行pk

	// ---------------------------------begin added by chendya
	// 移植财务行---------------------------------
	public static final String[] MONEY_FIELDS = new String[] { "ybje", "bbje", "ybye", "bbye", "hkybje", "hkbbje",
			"zfybje", "zfbbje", "cjkybje", "cjkbbje" };
	public static final String YJYE = "yjye";
	public static final String YBYE = "ybye";
	public static final String BBYE = "bbye";
	public static final String YBJE = "ybje";
	public static final String BBJE = "bbje";

	public static final String CJKYBJE = "cjkybje";
	public static final String CJKBBJE = "cjkbbje";

	public static final String ZFYBJE = "zfybje";
	public static final String ZFBBJE = "zfbbje";

	public static final String HKYBJE = "hkybje";
	public static final String HKBBJE = "hkbbje";

	public static final String FYYBJE = "fyybje";
	public static final String FYBBJE = "fybbje";

	/**
	 * 全局本币金额
	 */
	public static final String GLOBALBBJE = "globalbbje";
	/**
	 * 全局本币余额
	 */
	public static final String GLOBALBBYE = "globalbbye";
	/**
	 * 全局还款本币金额
	 */
	public static final String GLOBALHKBBJE = "globalhkbbje";
	/**
	 * 全局支付本币金额
	 */
	public static final String GLOBALZFBBJE = "globalzfbbje";
	/**
	 * 全局冲借款本币金额
	 */
	public static final String GLOBALCJKBBJE = "globalcjkbbje";
	/**
	 * 集团本币金额
	 */
	public static final String GROUPBBJE = "groupbbje";
	/**
	 * 集团本币余额
	 */
	public static final String GROUPBBYE = "groupbbye";
	/**
	 * 集团还款本币金额
	 */
	public static final String GROUPHKBBJE = "grouphkbbje";
	/**
	 * 集团支付本币金额
	 */
	public static final String GROUPZFBBJE = "groupzfbbje";
	/**
	 * 集团冲借款本币金额
	 */
	public static final String GROUPCJKBBJE = "groupcjkbbje";

	/**
	 * 结算信息表体前缀
	 */
	public static String SETTLE_BODY_PREFIX = "fb.";
	
	/**
	 * 结算信息表体前缀
	 */
	public static String SELECTED = "selected";

	public String cashproj;
	public String jkbxr;
	public String jobid;
	public String cashitem;
	public String pk_proline;
	
	public String getPk_proline() {
		return pk_proline;
	}

	public void setPk_proline(String pkProline) {
		pk_proline = pkProline;
	}

	public String getPk_brand() {
		return pk_brand;
	}

	public void setPk_brand(String pkBrand) {
		pk_brand = pkBrand;
	}

	public String pk_brand;

	public UFDouble hkbbje;
	public UFDouble hkybje;

	public UFDouble cjkbbje;
	public UFDouble cjkybje;

	public UFDouble zfybje;
	public UFDouble zfbbje;

	public UFDouble ybye;
	public UFDouble bbye;

	/**
	 * 借款单使用的预计余额
	 */
	public UFDouble yjye;

	// 新增计算属性
	public UFBoolean selected;

	public UFBoolean getSelected() {
		return selected;
	}

	public void setSelected(UFBoolean selected) {
		this.selected = selected;
	}

	/**
	 * 原币金额
	 */
	public UFDouble ybje;
	/**
	 * 本币金额
	 */
	public UFDouble bbje;

	// v6.0 增加全局，集团币种金额
	public UFDouble globalbbje;
	public UFDouble globalbbye;
	public UFDouble globalhkbbje;
	public UFDouble globalzfbbje;
	public UFDouble globalcjkbbje;
	public UFDouble groupbbje;
	public UFDouble groupbbye;
	public UFDouble grouphkbbje;
	public UFDouble groupzfbbje;
	public UFDouble groupcjkbbje;

	/**
	 * 结算信息表体VO
	 */
	private SuperVO settleBodyVO = null;

	public SuperVO getSettleBodyVO() {
		return settleBodyVO;
	}

	public void setSettleBodyVO(SuperVO settleBodyVO) {
		this.settleBodyVO = settleBodyVO;
	}

	public UFDouble getBbje() {
		return bbje;
	}

	public void setBbje(UFDouble bbje) {
		this.bbje = bbje;
	}

	public UFDouble getBbye() {
		return bbye;
	}

	public void setBbye(UFDouble bbye) {
		this.bbye = bbye;
	}

	public UFDouble getCjkbbje() {
		return cjkbbje;
	}

	public void setCjkbbje(UFDouble cjkbbje) {
		this.cjkbbje = cjkbbje;
	}

	public UFDouble getCjkybje() {
		return cjkybje;
	}

	public void setCjkybje(UFDouble cjkybje) {
		this.cjkybje = cjkybje;
	}

	public UFDouble getHkbbje() {
		return hkbbje;
	}

	public void setHkbbje(UFDouble hkbbje) {
		this.hkbbje = hkbbje;
	}

	public UFDouble getHkybje() {
		return hkybje;
	}

	public void setHkybje(UFDouble hkybje) {
		this.hkybje = hkybje;
	}

	public String getJkbxr() {
		return jkbxr;
	}

	public void setJkbxr(String jkbxr) {
		this.jkbxr = jkbxr;
	}

	public String getJobid() {
		return jobid;
	}

	public void setJobid(String jobid) {
		this.jobid = jobid;
	}

	public UFDouble getYbje() {
		return ybje;
	}

	public void setYbje(UFDouble ybje) {
		this.ybje = ybje;
	}

	public UFDouble getYbye() {
		return ybye;
	}

	public void setYbye(UFDouble ybye) {
		this.ybye = ybye;
	}

	public UFDouble getYjye() {
		return yjye;
	}

	public void setYjye(UFDouble yjye) {
		this.yjye = yjye;
	}

	public UFDouble getZfbbje() {
		return zfbbje;
	}

	public void setZfbbje(UFDouble zfbbje) {
		this.zfbbje = zfbbje;
	}

	public UFDouble getZfybje() {
		return zfybje;
	}

	public void setZfybje(UFDouble zfybje) {
		this.zfybje = zfybje;
	}

	public String getCashproj() {
		return cashproj;
	}

	public void setCashproj(String cashproj) {
		this.cashproj = cashproj;
	}

	/**
	 * 属性globalbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbje() {
		return globalbbje;
	}

	/**
	 * 属性globalbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalbbje
	 *            UFDouble
	 */
	public void setGlobalbbje(UFDouble newGlobalbbje) {
		this.globalbbje = newGlobalbbje;
	}

	/**
	 * 属性globalbbye的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbye() {
		return globalbbye;
	}

	/**
	 * 属性globalbbye的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalbbye
	 *            UFDouble
	 */
	public void setGlobalbbye(UFDouble newGlobalbbye) {
		this.globalbbye = newGlobalbbye;
	}

	/**
	 * 属性globalhkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalhkbbje() {
		return globalhkbbje;
	}

	/**
	 * 属性globalhkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalhkbbje
	 *            UFDouble
	 */
	public void setGlobalhkbbje(UFDouble newGlobalhkbbje) {
		this.globalhkbbje = newGlobalhkbbje;
	}

	/**
	 * 属性globalzfbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalzfbbje() {
		return globalzfbbje;
	}

	/**
	 * 属性globalzfbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalzfbbje
	 *            UFDouble
	 */
	public void setGlobalzfbbje(UFDouble newGlobalzfbbje) {
		this.globalzfbbje = newGlobalzfbbje;
	}

	/**
	 * 属性globalcjkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalcjkbbje() {
		return globalcjkbbje;
	}

	/**
	 * 属性globalcjkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGlobalcjkbbje
	 *            UFDouble
	 */
	public void setGlobalcjkbbje(UFDouble newGlobalcjkbbje) {
		this.globalcjkbbje = newGlobalcjkbbje;
	}

	/**
	 * 属性groupbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbje() {
		return groupbbje;
	}

	/**
	 * 属性groupbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupbbje
	 *            UFDouble
	 */
	public void setGroupbbje(UFDouble newGroupbbje) {
		this.groupbbje = newGroupbbje;
	}

	/**
	 * 属性groupbbye的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbye() {
		return groupbbye;
	}

	/**
	 * 属性groupbbye的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupbbye
	 *            UFDouble
	 */
	public void setGroupbbye(UFDouble newGroupbbye) {
		this.groupbbye = newGroupbbye;
	}

	/**
	 * 属性grouphkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGrouphkbbje() {
		return grouphkbbje;
	}

	/**
	 * 属性grouphkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGrouphkbbje
	 *            UFDouble
	 */
	public void setGrouphkbbje(UFDouble newGrouphkbbje) {
		this.grouphkbbje = newGrouphkbbje;
	}

	/**
	 * 属性groupzfbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupzfbbje() {
		return groupzfbbje;
	}

	/**
	 * 属性groupzfbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupzfbbje
	 *            UFDouble
	 */
	public void setGroupzfbbje(UFDouble newGroupzfbbje) {
		this.groupzfbbje = newGroupzfbbje;
	}

	/**
	 * 属性groupcjkbbje的Getter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupcjkbbje() {
		return groupcjkbbje;
	}

	/**
	 * 属性groupcjkbbje的Setter方法. 创建日期:2010-01-18 10:54:54
	 * 
	 * @param newGroupcjkbbje
	 *            UFDouble
	 */
	public void setGroupcjkbbje(UFDouble newGroupcjkbbje) {
		this.groupcjkbbje = newGroupcjkbbje;
	}

	@Override
	public Object getAttributeValue(String key) {
		// 如果包含结算信息字段，从结算vo中取值
		if (key.startsWith(SETTLE_BODY_PREFIX)) {
			if (getSettleBodyVO() != null && key.indexOf(SETTLE_BODY_PREFIX) == 0) {
				String atrr = key.substring(key.indexOf(SETTLE_BODY_PREFIX) + SETTLE_BODY_PREFIX.length());
				return getSettleBodyVO().getAttributeValue(atrr);
			}
		}
		return super.getAttributeValue(key);
	}

	// ---------------------------------end by chendya
	// 移植财务行---------------------------------

	private static String getFieldName(String field) {
		if (field.equals(AMOUNT))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		else if (field.equals(YBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		return field;
	}

	public Object defitem8;

	public Object defitem2;

	public Object defitem21;

	public Object defitem36;

	public Object defitem40;

	public Object defitem23;

	public Object defitem12;

	public Object defitem31;

	public Object defitem34;

	public Object defitem3;

	public String tablecode;

	public Object defitem11;

	public Object defitem17;

	public Object defitem28;

	public Object defitem33;

	public Object defitem48;

	public String pk_jkbx;

	public Object defitem45;

	public Object defitem1;

	public Object defitem50;

	public Object defitem42;

	public Object defitem35;

	public Object defitem30;

	public Object defitem41;

	public Object defitem9;

	public Object defitem29;

	public Object defitem5;

	public Object defitem19;

	public Object defitem14;

	public Object defitem49;

	public Object defitem26;

	public Object defitem43;

	public Object defitem46;

	public Object defitem4;

	public Object defitem24;

	public Object defitem16;

	public Object defitem38;

	public Object defitem27;

	public Object defitem22;

	public Object defitem6;

	public String pk_busitem;

	public Object defitem39;

	public Object defitem47;

	public Object defitem25;

	public Object defitem44;

	public Object defitem18;

	public Object defitem10;

	public Object defitem32;

	public Object defitem37;

	public Object defitem15;

	public Object defitem20;

	public Object defitem13;

	public Object defitem7;

	public Integer rowno;

	public static final String DEFITEM8 = "defitem8";

	public static final String DEFITEM2 = "defitem2";

	public static final String DEFITEM21 = "defitem21";

	public static final String DEFITEM36 = "defitem36";

	public static final String DEFITEM40 = "defitem40";

	public static final String DEFITEM23 = "defitem23";

	public static final String DEFITEM12 = "defitem12";

	public static final String DEFITEM31 = "defitem31";

	public static final String DEFITEM34 = "defitem34";

	public static final String DEFITEM3 = "defitem3";

	public static final String TABLECODE = "tablecode";

	public static final String DEFITEM11 = "defitem11";

	public static final String DEFITEM17 = "defitem17";

	public static final String DEFITEM28 = "defitem28";

	public static final String DEFITEM33 = "defitem33";

	public static final String DEFITEM48 = "defitem48";

	public static final String PK_JKBX = "pk_jkbx";

	public static final String DEFITEM45 = "defitem45";

	public static final String DEFITEM1 = "defitem1";

	public static final String DEFITEM50 = "defitem50";

	public static final String DEFITEM42 = "defitem42";

	public static final String DEFITEM35 = "defitem35";

	public static final String DEFITEM30 = "defitem30";

	public static final String DEFITEM41 = "defitem41";

	public static final String DEFITEM9 = "defitem9";

	public static final String DEFITEM29 = "defitem29";

	public static final String DEFITEM5 = "defitem5";

	public static final String DEFITEM19 = "defitem19";

	public static final String DEFITEM14 = "defitem14";

	public static final String DEFITEM49 = "defitem49";

	public static final String DEFITEM26 = "defitem26";

	public static final String DEFITEM43 = "defitem43";

	public static final String DEFITEM46 = "defitem46";

	public static final String DEFITEM4 = "defitem4";

	public static final String DEFITEM24 = "defitem24";

	public static final String DEFITEM16 = "defitem16";

	public static final String DEFITEM38 = "defitem38";

	public static final String DEFITEM27 = "defitem27";

	public static final String DEFITEM22 = "defitem22";

	public static final String DEFITEM6 = "defitem6";

	public static final String PK_BUSITEM = "pk_busitem";

	public static final String DEFITEM39 = "defitem39";

	public static final String DEFITEM47 = "defitem47";

	public static final String DEFITEM25 = "defitem25";

	public static final String DEFITEM44 = "defitem44";

	public static final String DEFITEM18 = "defitem18";

	public static final String DEFITEM10 = "defitem10";

	public static final String DEFITEM32 = "defitem32";

	public static final String DEFITEM37 = "defitem37";

	public static final String DEFITEM15 = "defitem15";

	public static final String DEFITEM20 = "defitem20";

	public static final String DEFITEM13 = "defitem13";

	public static final String DEFITEM7 = "defitem7";

	public static final String AMOUNT = "amount";

	public static final String SZXMID = "szxmid";

	public static final String ROWNO = "rowno";
	
	public static final String PK_PROLINE = "pk_proline";
	public static final String PK_BRAND = "pk_brand";
	public static final String DWBM = "dwbm";
	public static final String DEPTID = "deptid";
	public static final String JKBXR	 = "jkbxr";
	public static final String PAYTARGET = "paytarget";
	public static final String RECEIVER = "receiver";
	public static final String SKYHZH = "skyhzh";
	public static final String HBBM = "hbbm";
	public static final String CUSTOMER = "customer";
	public static final String CUSTACCOUNT = "custaccount";
	public static final String FREECUST = "freecust";
	public static final String FREEACCOUNT= "freeaccount";

	public static final String SZXMMC = "szxmmc";
	public static final String BZBM = "bzbm";
	public static final String PK_EXPENSETYPE = "pk_expensetype";
	public static final String PK_REIMTYPE = "pk_reimtype";
	private UFDateTime ts;
	private Integer dr;
	private String szxmid;
	private String pk_reimtype;

	public String getPk_reimtype() {
		return pk_reimtype;
	}

	public void setPk_reimtype(String pk_reimtype) {
		this.pk_reimtype = pk_reimtype;
	}

	public String getSzxmid() {
		return szxmid;
	}

	public void setSzxmid(String szxmid) {
		this.szxmid = szxmid;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public boolean isNullItem() {
		boolean isNull = true;
		String[] attributeNames = getAttributeNames();
		for (String name : attributeNames) {
			if (getAttributeValue(name) != null) {
				isNull = false;
				break;
			}
		}
		return isNull;
	}

	/**
	 * 原币金额
	 */
	public UFDouble amount;

	/**
	 * 属性defitem8的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem8() {
		return defitem8;
	}

	/**
	 * 属性defitem8的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem8
	 *            Object
	 */
	public void setDefitem8(Object newDefitem8) {

		defitem8 = newDefitem8;
	}

	/**
	 * 属性defitem2的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem2() {
		return defitem2;
	}

	/**
	 * 属性defitem2的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem2
	 *            Object
	 */
	public void setDefitem2(Object newDefitem2) {

		defitem2 = newDefitem2;
	}

	/**
	 * 属性defitem21的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem21() {
		return defitem21;
	}

	/**
	 * 属性defitem21的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem21
	 *            Object
	 */
	public void setDefitem21(Object newDefitem21) {
		defitem21 = newDefitem21;
	}

	/**
	 * 属性defitem36的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem36() {
		return defitem36;
	}

	/**
	 * 属性defitem36的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem36
	 *            Object
	 */
	public void setDefitem36(Object newDefitem36) {
		defitem36 = newDefitem36;
	}

	/**
	 * 属性defitem40的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem40() {
		return defitem40;
	}

	/**
	 * 属性defitem40的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem40
	 *            Object
	 */
	public void setDefitem40(Object newDefitem40) {

		defitem40 = newDefitem40;
	}

	/**
	 * 属性defitem23的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem23() {
		return defitem23;
	}

	/**
	 * 属性defitem23的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem23
	 *            Object
	 */
	public void setDefitem23(Object newDefitem23) {
		defitem23 = newDefitem23;
	}

	/**
	 * 属性defitem12的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem12() {
		return defitem12;
	}

	/**
	 * 属性defitem12的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem12
	 *            Object
	 */
	public void setDefitem12(Object newDefitem12) {

		defitem12 = newDefitem12;
	}

	/**
	 * 属性defitem31的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem31() {
		return defitem31;
	}

	/**
	 * 属性defitem31的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem31
	 *            Object
	 */
	public void setDefitem31(Object newDefitem31) {
		defitem31 = newDefitem31;
	}

	/**
	 * 属性defitem34的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem34() {
		return defitem34;
	}

	/**
	 * 属性defitem34的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem34
	 *            Object
	 */
	public void setDefitem34(Object newDefitem34) {
		defitem34 = newDefitem34;
	}

	/**
	 * 属性defitem3的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem3() {
		return defitem3;
	}

	/**
	 * 属性defitem3的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem3
	 *            Object
	 */
	public void setDefitem3(Object newDefitem3) {
		defitem3 = newDefitem3;
	}

	/**
	 * 属性tablecode的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getTablecode() {
		return tablecode;
	}

	/**
	 * 属性tablecode的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newTablecode
	 *            Object
	 */
	public void setTablecode(String newTablecode) {
		tablecode = newTablecode;
	}

	/**
	 * 属性defitem11的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem11() {
		return defitem11;
	}

	/**
	 * 属性defitem11的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem11
	 *            Object
	 */
	public void setDefitem11(Object newDefitem11) {
		defitem11 = newDefitem11;
	}

	/**
	 * 属性defitem17的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem17() {
		return defitem17;
	}

	/**
	 * 属性defitem17的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem17
	 *            Object
	 */
	public void setDefitem17(Object newDefitem17) {
		defitem17 = newDefitem17;
	}

	/**
	 * 属性defitem28的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem28() {
		return defitem28;
	}

	/**
	 * 属性defitem28的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem28
	 *            Object
	 */
	public void setDefitem28(Object newDefitem28) {
		defitem28 = newDefitem28;
	}

	/**
	 * 属性defitem33的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem33() {
		return defitem33;
	}

	/**
	 * 属性defitem33的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem33
	 *            Object
	 */
	public void setDefitem33(Object newDefitem33) {
		defitem33 = newDefitem33;
	}

	/**
	 * 属性defitem48的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem48() {
		return defitem48;
	}

	/**
	 * 属性defitem48的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem48
	 *            Object
	 */
	public void setDefitem48(Object newDefitem48) {
		defitem48 = newDefitem48;
	}

	/**
	 * 属性pk_jkbxzb的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getPk_jkbx() {
		return pk_jkbx;
	}

	/**
	 * 属性pk_jkbxzb的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_jkbxzb
	 *            Object
	 */
	public void setPk_jkbx(String newPk_jkbxzb) {
		pk_jkbx = newPk_jkbxzb;
	}

	/**
	 * 属性defitem45的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem45() {
		return defitem45;
	}

	/**
	 * 属性defitem45的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem45
	 *            Object
	 */
	public void setDefitem45(Object newDefitem45) {
		defitem45 = newDefitem45;
	}

	/**
	 * 属性defitem1的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem1() {
		return defitem1;
	}

	/**
	 * 属性defitem1的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem1
	 *            Object
	 */
	public void setDefitem1(Object newDefitem1) {
		defitem1 = newDefitem1;
	}

	/**
	 * 属性defitem50的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem50() {
		return defitem50;
	}

	/**
	 * 属性defitem50的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem50
	 *            Object
	 */
	public void setDefitem50(Object newDefitem50) {
		defitem50 = newDefitem50;
	}

	/**
	 * 属性defitem42的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem42() {
		return defitem42;
	}

	/**
	 * 属性defitem42的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem42
	 *            Object
	 */
	public void setDefitem42(Object newDefitem42) {
		defitem42 = newDefitem42;
	}

	/**
	 * 属性defitem35的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem35() {
		return defitem35;
	}

	/**
	 * 属性defitem35的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem35
	 *            Object
	 */
	public void setDefitem35(Object newDefitem35) {
		defitem35 = newDefitem35;
	}

	/**
	 * 属性defitem30的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem30() {
		return defitem30;
	}

	/**
	 * 属性defitem30的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem30
	 *            Object
	 */
	public void setDefitem30(Object newDefitem30) {
		defitem30 = newDefitem30;
	}

	/**
	 * 属性defitem41的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem41() {
		return defitem41;
	}

	/**
	 * 属性defitem41的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem41
	 *            Object
	 */
	public void setDefitem41(Object newDefitem41) {
		defitem41 = newDefitem41;
	}

	/**
	 * 属性defitem9的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem9() {
		return defitem9;
	}

	/**
	 * 属性defitem9的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem9
	 *            Object
	 */
	public void setDefitem9(Object newDefitem9) {
		defitem9 = newDefitem9;
	}

	/**
	 * 属性defitem29的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem29() {
		return defitem29;
	}

	/**
	 * 属性defitem29的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem29
	 *            Object
	 */
	public void setDefitem29(Object newDefitem29) {
		defitem29 = newDefitem29;
	}

	/**
	 * 属性defitem5的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem5() {
		return defitem5;
	}

	/**
	 * 属性defitem5的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem5
	 *            Object
	 */
	public void setDefitem5(Object newDefitem5) {
		defitem5 = newDefitem5;
	}

	/**
	 * 属性defitem19的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem19() {
		return defitem19;
	}

	/**
	 * 属性defitem19的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem19
	 *            Object
	 */
	public void setDefitem19(Object newDefitem19) {
		defitem19 = newDefitem19;
	}

	/**
	 * 属性defitem14的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem14() {
		return defitem14;
	}

	/**
	 * 属性defitem14的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem14
	 *            Object
	 */
	public void setDefitem14(Object newDefitem14) {
		defitem14 = newDefitem14;
	}

	/**
	 * 属性defitem49的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem49() {
		return defitem49;
	}

	/**
	 * 属性defitem49的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem49
	 *            Object
	 */
	public void setDefitem49(Object newDefitem49) {
		defitem49 = newDefitem49;
	}

	/**
	 * 属性defitem26的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem26() {
		return defitem26;
	}

	/**
	 * 属性defitem26的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem26
	 *            Object
	 */
	public void setDefitem26(Object newDefitem26) {
		defitem26 = newDefitem26;
	}

	/**
	 * 属性defitem43的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem43() {
		return defitem43;
	}

	/**
	 * 属性defitem43的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem43
	 *            Object
	 */
	public void setDefitem43(Object newDefitem43) {
		defitem43 = newDefitem43;
	}

	/**
	 * 属性defitem46的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem46() {
		return defitem46;
	}

	/**
	 * 属性defitem46的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem46
	 *            Object
	 */
	public void setDefitem46(Object newDefitem46) {
		defitem46 = newDefitem46;
	}

	/**
	 * 属性defitem4的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem4() {
		return defitem4;
	}

	/**
	 * 属性defitem4的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem4
	 *            Object
	 */
	public void setDefitem4(Object newDefitem4) {
		defitem4 = newDefitem4;
	}

	/**
	 * 属性defitem24的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem24() {
		return defitem24;
	}

	/**
	 * 属性defitem24的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem24
	 *            Object
	 */
	public void setDefitem24(Object newDefitem24) {
		defitem24 = newDefitem24;
	}

	/**
	 * 属性defitem16的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem16() {
		return defitem16;
	}

	/**
	 * 属性defitem16的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem16
	 *            Object
	 */
	public void setDefitem16(Object newDefitem16) {
		defitem16 = newDefitem16;
	}

	/**
	 * 属性defitem38的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem38() {
		return defitem38;
	}

	/**
	 * 属性defitem38的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem38
	 *            Object
	 */
	public void setDefitem38(Object newDefitem38) {
		defitem38 = newDefitem38;
	}

	/**
	 * 属性defitem27的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem27() {
		return defitem27;
	}

	/**
	 * 属性defitem27的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem27
	 *            Object
	 */
	public void setDefitem27(Object newDefitem27) {
		defitem27 = newDefitem27;
	}

	/**
	 * 属性defitem22的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem22() {
		return defitem22;
	}

	/**
	 * 属性defitem22的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem22
	 *            Object
	 */
	public void setDefitem22(Object newDefitem22) {
		defitem22 = newDefitem22;
	}

	/**
	 * 属性defitem6的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem6() {
		return defitem6;
	}

	/**
	 * 属性defitem6的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem6
	 *            Object
	 */
	public void setDefitem6(Object newDefitem6) {
		defitem6 = newDefitem6;
	}

	/**
	 * 属性pk_bxbusitem的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getPk_busitem() {
		return pk_busitem;
	}

	/**
	 * 属性pk_bxbusitem的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            Object
	 */
	public void setPk_busitem(String newPk_bxbusitem) {
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 属性defitem39的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem39() {
		return defitem39;
	}

	/**
	 * 属性defitem39的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem39
	 *            Object
	 */
	public void setDefitem39(Object newDefitem39) {

		defitem39 = newDefitem39;
	}

	/**
	 * 属性defitem47的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem47() {
		return defitem47;
	}

	/**
	 * 属性defitem47的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem47
	 *            Object
	 */
	public void setDefitem47(Object newDefitem47) {
		defitem47 = newDefitem47;
	}

	/**
	 * 属性defitem25的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem25() {
		return defitem25;
	}

	/**
	 * 属性defitem25的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem25
	 *            Object
	 */
	public void setDefitem25(Object newDefitem25) {
		defitem25 = newDefitem25;
	}

	/**
	 * 属性defitem44的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem44() {
		return defitem44;
	}

	/**
	 * 属性defitem44的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem44
	 *            Object
	 */
	public void setDefitem44(Object newDefitem44) {
		defitem44 = newDefitem44;
	}

	/**
	 * 属性defitem18的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem18() {
		return defitem18;
	}

	/**
	 * 属性defitem18的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem18
	 *            Object
	 */
	public void setDefitem18(Object newDefitem18) {
		defitem18 = newDefitem18;
	}

	/**
	 * 属性defitem10的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem10() {
		return defitem10;
	}

	/**
	 * 属性defitem10的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem10
	 *            Object
	 */
	public void setDefitem10(Object newDefitem10) {
		defitem10 = newDefitem10;
	}

	/**
	 * 属性defitem32的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem32() {
		return defitem32;
	}

	/**
	 * 属性defitem32的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem32
	 *            Object
	 */
	public void setDefitem32(Object newDefitem32) {
		defitem32 = newDefitem32;
	}

	/**
	 * 属性defitem37的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem37() {
		return defitem37;
	}

	/**
	 * 属性defitem37的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem37
	 *            Object
	 */
	public void setDefitem37(Object newDefitem37) {
		defitem37 = newDefitem37;
	}

	/**
	 * 属性defitem15的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem15() {
		return defitem15;
	}

	/**
	 * 属性defitem15的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem15
	 *            Object
	 */
	public void setDefitem15(Object newDefitem15) {
		defitem15 = newDefitem15;
	}

	/**
	 * 属性defitem20的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem20() {
		return defitem20;
	}

	/**
	 * 属性defitem20的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem20
	 *            Object
	 */
	public void setDefitem20(Object newDefitem20) {
		defitem20 = newDefitem20;
	}

	/**
	 * 属性defitem13的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem13() {
		return defitem13;
	}

	/**
	 * 属性defitem13的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem13
	 *            Object
	 */
	public void setDefitem13(Object newDefitem13) {
		defitem13 = newDefitem13;
	}

	/**
	 * 属性defitem7的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public Object getDefitem7() {
		return defitem7;
	}

	/**
	 * 属性defitem7的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDefitem7
	 *            Object
	 */
	public void setDefitem7(Object newDefitem7) {
		defitem7 = newDefitem7;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	public void validate() throws ValidationException {
		ArrayList<String> notNullFieldNames = new ArrayList<String>(); // errFields
		ArrayList<String> notNullFieldKeys = new ArrayList<String>(); // errFields
		notNullFieldKeys.add(BXBusItemVO.AMOUNT);
		notNullFieldKeys.add(BXBusItemVO.YBJE);

		for (String field : notNullFieldKeys) {
			if (getAttributeValue(field) == null)
				notNullFieldNames.add(getFieldName(field));
		}

		if (notNullFieldNames.size() > 0) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append(getErromsg());//vaildate是长调用的方法，应避免耗时的代码
			String[] temp = notNullFieldNames.toArray(new String[0]);
			errorMsg.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				errorMsg.append(",");
				errorMsg.append(temp[i]);
			}
			throw new NullFieldException(errorMsg.toString());
		}
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getParentPKFieldName() {
		return "pk_jkbx";
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getPKFieldName() {
		return "pk_busitem";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object
	 */
	public java.lang.String getTableName() {
		return getDefaultTableName();
	}

	public static java.lang.String getDefaultTableName() {
		return "er_busitem";
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2007-6-13
	 */
	public BXBusItemVO() {
		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            主键值
	 */
	public BXBusItemVO(String newPk_bxbusitem) {
		// 为主键字段赋值:
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Object
	 */
	public String getPrimaryKey() {
		return pk_busitem;
	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_bxbusitem
	 *            Object
	 */
	public void setPrimaryKey(String newPk_bxbusitem) {
		pk_busitem = newPk_bxbusitem;
	}

	/**
	 * 返回数值对象的显示名称.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.Object 返回数值对象的显示名称.
	 */
	public String getEntityName() {
		return "er_busitem";
	}

	public UFDouble getAmount() {
		return amount;
	}

	public void setAmount(UFDouble amount) {
		this.amount = amount;
	}

	public Integer getRowno() {
		return rowno;
	}

	public void setRowno(Integer rowno) {
		this.rowno = rowno;
	}

	/**
	 * 返回表体集团本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyGroupBbjeField() {
		return new String[] { GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * 
	 * 返回表体全局本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyGlobalBbjeField() {
		return new String[] { GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE };
	}

	/**
	 * 
	 * 返回表体组织本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getBodyOrgBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE,BBYE};
	}

	/**
	 * 返回原币金额字段
	 * 
	 * @return
	 */
	public static String[] getYbjeField() {
		return new String[] { CJKYBJE, ZFYBJE, HKYBJE, YBJE, YBYE, FYYBJE, AMOUNT,YJYE };
	}

	/**
	 * 为精度设置操作提供的方法，返回表体金额字段的key值
	 * 
	 * @return
	 */
	public static String[] getBodyJeFieldForDecimal() {
		return new String[] { YBJE, CJKYBJE, ZFYBJE, HKYBJE, FYYBJE, BBJE, CJKBBJE, ZFBBJE, HKBBJE, AMOUNT, YBYE,
				GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE,
				GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	public String getPk_resacostcenter() {
		return pk_resacostcenter;
	}

	public void setPk_resacostcenter(String pk_resacostcenter) {
		this.pk_resacostcenter = pk_resacostcenter;
	}

	public String getPk_checkele() {
		return pk_checkele;
	}

	public void setPk_checkele(String pk_checkele) {
		this.pk_checkele = pk_checkele;
	}

	public String getPk_pcorg() {
		return pk_pcorg;
	}

	public void setPk_pcorg(String pk_pcorg) {
		this.pk_pcorg = pk_pcorg;
	}

	public String getPk_pcorg_v() {
		return pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pk_pcorg_v) {
		this.pk_pcorg_v = pk_pcorg_v;
	}

	public String getProjecttask() {
		return projecttask;
	}

	public void setProjecttask(String projecttask) {
		this.projecttask = projecttask;
	}

	public String getPk_item() {
		return pk_item;
	}

	public void setPk_item(String pkItem) {
		pk_item = pkItem;
	}

	public String getSrcbilltype() {
		return srcbilltype;
	}

	public void setSrcbilltype(String srcbilltype) {
		this.srcbilltype = srcbilltype;
	}

	public String getSrctype() {
		return srctype;
	}

	public void setSrctype(String srctype) {
		this.srctype = srctype;
	}

	public String getJk_busitemPK() {
		return jk_busitemPK;
	}

	public void setJk_busitemPK(String jk_busitemPK) {
		this.jk_busitemPK = jk_busitemPK;
	}

	public String getBx_busitemPK() {
		return bx_busitemPK;
	}

	public void setBx_busitemPK(String bx_busitemPK) {
		this.bx_busitemPK = bx_busitemPK;
	}

	public String getPk_mtapp_detail() {
		return pk_mtapp_detail;
	}

	public void setPk_mtapp_detail(String pk_mtapp_detail) {
		this.pk_mtapp_detail = pk_mtapp_detail;
	}

}