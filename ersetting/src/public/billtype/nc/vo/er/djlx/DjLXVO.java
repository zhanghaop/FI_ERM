package nc.vo.er.djlx;

/***************************************************************\
 *     The skeleton of this class is generated by an automatic *
 * code generator for NC product.                              *
 \***************************************************************/

import java.util.ArrayList;
import java.util.List;


import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDateTime;

/**
 * 此处插入类型说明。
 * 
 * 创建日期：(2003-3-18)
 * 
 * @author：tianhb
 */
public class DjLXVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 152419565786206838L;

	private String djlxoid;

	private String dwbm;

	private String sfbz;

	private String djdl;

	private String djlxjc;

	private String djlxmc;
	
	private String pk_group;
	

	private UFBoolean isftspay ;

	private UFBoolean fcbz;

	private UFBoolean mjbz = UFBoolean.TRUE;

	private String scomment;

	private String djmboid;

	private String djlxbm;

	private UFDateTime ts;

	private Integer dr;

	private String defcurrency;

	private UFBoolean isbankrecive;

	private UFBoolean isqr;

	private UFBoolean iscorresp;

	private UFBoolean iscontrast = UFBoolean.TRUE;
	
	private UFBoolean isloadtemplate = UFBoolean.TRUE;
	
	private Integer sysid;

	private Integer isjszxzf;

	private String djlxjc_remark;

	private String djlxmc_remark;
	private String usesystem;
	private UFBoolean isloan;//是否借款报销
	private UFBoolean ischangedeptpsn;//是否带出
	private UFBoolean isControlled = UFBoolean.FALSE;
	private UFBoolean iscasign = UFBoolean.FALSE;//是否数字签名
	//付款单生效点是否在单据支付（网银）前 
	private UFBoolean isSXBeforeWszz ;
	private UFBoolean isautocombinse;
	private UFBoolean issettleshow;
	/**
	 * 是否限额支票
	 */
	private UFBoolean limitCheck;
	/**
	 * 是否生成收付款单
	 */
	private UFBoolean creatCashflows;
	private UFBoolean ispreparenetbank = UFBoolean.FALSE;//是否网银补录（保存时）
	private UFBoolean isidvalidated= UFBoolean.FALSE;//是否身份认证
	/**
	 * 是否还款单
	 */

	private UFBoolean reimbursement;
	/**
	 * 借款报销单扩展单据类型属性(此属性不能支持SuperVO)
	 */
	private BusiTypeVO busitypeVOe ;
	
	public UFBoolean getCreatCashflows() {
		return creatCashflows;
	}
	public void setCreatCashflows(UFBoolean creatCashflows) {
		this.creatCashflows = creatCashflows;
	}
	public UFBoolean getLimitCheck() {
		return limitCheck;
	}
	public void setLimitCheck(UFBoolean limitCheck) {
		this.limitCheck = limitCheck;
	}
	public UFBoolean getReimbursement() {
		return reimbursement;
	}
	public void setReimbursement(UFBoolean reimbursement) {
		this.reimbursement = reimbursement;
	}
	public UFBoolean getIsControlled() {
		return isControlled;
	}

	public void setIsControlled(UFBoolean isControlled) {
		this.isControlled = isControlled;
	}
	
	public String getPk_group() {
		return pk_group;
	}
	public void setPk_group(String pkGroup) {
		pk_group = pkGroup;
	}

	/**
	 * 使用主键字段进行初始化的构造子。
	 * 
	 * 创建日期：(2003-3-18)
	 */
	public DjLXVO() {
		super();
	}

	/**
	 * 使用主键进行初始化的构造子。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param ??fieldNameForMethod??
	 *            主键值
	 */
	public DjLXVO(String newDjlxoid) {
		super();
		// 为主键字段赋值:
		djlxoid = newDjlxoid;
	}

	/**
	 * 根类Object的方法,克隆这个VO对象。
	 * 
	 * 创建日期：(2003-3-18)
	 */
	public Object clone() {

		// 复制基类内容并创建新的VO对象：
		Object o = null;
		try {
			o = super.clone();
		} catch (Exception e) {
		    throw new RuntimeException(e.getMessage());
		}
		DjLXVO djlx = (DjLXVO) o;

		// 你在下面复制本VO对象的所有属性：
		if(this.busitypeVOe!=null)
			djlx.setBusitypeVO((BusiTypeVO)this.busitypeVOe.clone());
		return djlx;
	}

	/**
	 * 属性m_defcurrency的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDefcurrency() {
		return defcurrency;
	}

	/**
	 * 属性m_djdl的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjdl() {
		return djdl;
	}

	/**
	 * 属性m_djlxbm的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjlxbm() {
		return djlxbm;
	}

	/**
	 * 属性m_djlxjc的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjlxjc() {
		return djlxjc;
	}

	/**
	 * 属性m_djlxmc的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjlxmc() {
		return djlxmc;
	}

	/**
	 * 属性m_djlxoid的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjlxoid() {
		return djlxoid;
	}

	/**
	 * 属性m_djmboid的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDjmboid() {
		return djmboid;
	}

	/**
	 * 属性m_dr的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return Integer
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性m_dwbm的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getDwbm() {
		return dwbm;
	}

	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public String getEntityName() {

		return "Djlx";
	}

	/**
	 * 属性m_fcbz的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return UFBoolean
	 */
	public UFBoolean getFcbz() {
		return fcbz;
	}



	/**
	 * 属性m_isbankrecive的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public UFBoolean getIsbankrecive() {
		return isbankrecive;
	}

	// /**
	// * 此处插入方法描述。
	// * 创建日期：(2004-2-23 13:23:13)
	// * @return java.lang.String
	// */
	// public java.lang.String getIscommit() {
	// return m_iscommit;
	// }
	/**
	 * 此处插入方法说明。 创建日期：(2003-7-1 9:43:47)
	 * 
	 * @return nc.vo.pub.lang.UFBoolean
	 */
	public UFBoolean getIscorresp() {
		return iscorresp;
	}

	/**
	 * 属性m_mjbz的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return UFBoolean
	 */
	public UFBoolean getMjbz() {
		return mjbz;
	}

	/**
	 * 返回对象标识，用来唯一定位对象。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getPrimaryKey() {

		return djlxoid;
	}

	/**
	 * 属性m_scomment的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getScomment() {
		return scomment;
	}

	/**
	 * 属性m_sfbz的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return String
	 */
	public String getSfbz() {
		return sfbz;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2003-10-21 15:23:09)
	 * 
	 * @return java.lang.Integer
	 */
	public Integer getSysid() {
		return sysid;
	}

	/**
	 * 属性m_ts的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return UFDateTime
	 */
	public UFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性m_zdbmws的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return Integer
	 */
	public Integer getZdbmws() {
		return null;
	}

	/**
	 * 属性m_zdbmyz的Getter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @return Integer
	 */
	public Integer getZdbmyz() {
		return null;
	}

	/**
	 * 属性m_defcurrency的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_defcurrency
	 *            String
	 */
	public void setDefcurrency(String newDefcurrency) {

		defcurrency = newDefcurrency;
	}

	/**
	 * 属性m_djdl的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djdl
	 *            String
	 */
	public void setDjdl(String newDjdl) {

		djdl = newDjdl;
	}

	/**
	 * 属性m_djlxbm的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djlxbm
	 *            String
	 */
	public void setDjlxbm(String newDjlxbm) {

		djlxbm = newDjlxbm;
	}

	/**
	 * 属性m_djlxjc的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djlxjc
	 *            String
	 */
	public void setDjlxjc(String newDjlxjc) {

		djlxjc = newDjlxjc;
	}

	/**
	 * 属性m_djlxmc的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djlxmc
	 *            String
	 */
	public void setDjlxmc(String newDjlxmc) {

		djlxmc = newDjlxmc;
	}

	/**
	 * 属性m_djlxoid的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djlxoid
	 *            String
	 */
	public void setDjlxoid(String newDjlxoid) {

		djlxoid = newDjlxoid;
	}

	/**
	 * 属性m_djmboid的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_djmboid
	 *            String
	 */
	public void setDjmboid(String newDjmboid) {

		djmboid = newDjmboid;
	}

	/**
	 * 属性m_dr的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_dr
	 *            Integer
	 */
	public void setDr(Integer newDr) {

		dr = newDr;
	}

	/**
	 * 属性m_dwbm的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_dwbm
	 *            String
	 */
	public void setDwbm(String newDwbm) {

		dwbm = newDwbm;
	}

	/**
	 * 属性m_fcbz的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_fcbz
	 *            UFBoolean
	 */
	public void setFcbz(UFBoolean newFcbz) {

		fcbz = newFcbz;
	}


	/**
	 * 属性m_isbankrecive的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_isbankrecive
	 *            String
	 */
	public void setIsbankrecive(UFBoolean newIsbankrecive) {

		isbankrecive = newIsbankrecive;
	}

	// /**
	// * 此处插入方法描述。
	// * 创建日期：(2004-2-23 13:23:13)
	// * @param newM_iscommit java.lang.String
	// */
	// public void setIscommit(java.lang.String newM_iscommit) {
	// m_iscommit = newM_iscommit;
	// }
	/**
	 * 此处插入方法说明。 创建日期：(2003-7-1 9:44:56)
	 * 
	 * @param flag
	 *            nc.vo.pub.lang.UFBoolean
	 */
	public void setIscorresp(UFBoolean flag) {
		iscorresp = flag;
	}

	/**
	 * 属性m_mjbz的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_mjbz
	 *            UFBoolean
	 */
	public void setMjbz(UFBoolean newMjbz) {

		mjbz = newMjbz;
	}

	/**
	 * 设置对象标识，用来唯一定位对象。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param djlxoid
	 *            String
	 */
	public void setPrimaryKey(String newDjlxoid) {

		djlxoid = newDjlxoid;
	}

	/**
	 * 属性m_scomment的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_scomment
	 *            String
	 */
	public void setScomment(String newScomment) {

		scomment = newScomment;
	}

	/**
	 * 属性m_sfbz的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_sfbz
	 *            String
	 */
	public void setSfbz(String newSfbz) {

		sfbz = newSfbz;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2003-10-21 15:22:34)
	 * 
	 * @param id
	 *            java.lang.Integer
	 */
	public void setSysid(Integer id) {
		sysid = id;
	}

	/**
	 * 属性m_ts的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_ts
	 *            UFDateTime
	 */
	public void setTs(UFDateTime newTs) {

		ts = newTs;
	}

	/**
	 * 属性m_zdbmws的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_zdbmws
	 *            Integer
	 */
	public void setZdbmws(Integer newZdbmws) {

	}

	/**
	 * 属性m_zdbmyz的setter方法。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @param newM_zdbmyz
	 *            Integer
	 */
	public void setZdbmyz(Integer newZdbmyz) {

	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * 创建日期：(2003-3-18)
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败，抛出 ValidationException，对错误进行解释。
	 */
	public void validate() throws ValidationException {

		List<String> errFields = new ArrayList<String>(); // errFields record those null
												// fields that cannot be null.
		// 检查是否为不允许空的字段赋了空值，你可能需要修改下面的提示信息：
		if (djlxoid == null) {
			errFields.add("m_djlxoid");
		}
		if (dwbm == null) {
			errFields.add("m_dwbm");
		}
		if (sfbz == null) {
			errFields.add("m_sfbz");
		}
		if (djdl == null) {
			errFields.add("m_djdl");
		}
		if (djlxmc == null) {
			errFields.add("m_djlxmc");
		}

		if (fcbz == null) {
			errFields.add("m_fcbz");
		}
		if (mjbz == null) {
			errFields.add("m_mjbz");
		}
		// if (m_djmboid == null) {
		// errFields.add("m_djmboid"));
		// }
		if (djlxbm == null) {
			errFields.add("m_djlxbm");
		}
		// construct the exception message:
		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
				"20060101", "UPP20060101-000088")/* @res "下列字段不能为空：" */);
		if (errFields.size() > 0) {
			String[] temp = (String[]) errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message
						.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
								.getStrByID("20060101", "UPP20060101-000089")/*
																				 * @res
																				 * "、"
																				 */);
				message.append(temp[i]);
			}
			// throw the exception:
			throw new NullFieldException(message.toString());
		}
	}

//	/**
//	 * @return 返回 templets。
//	 */
//	public Hashtable getTemplets() {
//		return templets;
//	}
//
//	/**
//	 * @param templets
//	 *            要设置的 templets。
//	 */
//	public void setTemplets(Hashtable templets) {
//		this.templets = templets;
//	}

	/**
	 * @return 返回 m_isjszxzf。
	 */
	public Integer getIsjszxzf() {
		return isjszxzf;
	}

	/**
	 * @param isjszxzf
	 *            要设置的 m_isjszxzf。
	 */
	public void setIsjszxzf(Integer isjszxzf) {
		if(isjszxzf==null){
			isjszxzf = getIsFTSPay().booleanValue()?1:2;
		}
		this.isjszxzf = isjszxzf;
		if(isjszxzf.equals(0)){
			isftspay = UFBoolean.FALSE;			
		}else{
			isftspay = UFBoolean.TRUE;
		}
	}
	
	

	/**
	 * @return 返回 djlxjc_remark。
	 */
	public String getDjlxjc_remark() {
		return djlxjc_remark;
	}

	/**
	 * @param djlxjc_remark
	 *            要设置的 djlxjc_remark。
	 */
	public void setDjlxjc_remark(String djlxjc_remark) {
		this.djlxjc_remark = djlxjc_remark;
	}

	/**
	 * @return 返回 djlxmc_remark。
	 */
	public String getDjlxmc_remark() {
		return djlxmc_remark;
	}

	/**
	 * @param djlxmc_remark
	 *            要设置的 djlxmc_remark。
	 */
	public void setDjlxmc_remark(String djlxmc_remark) {
		this.djlxmc_remark = djlxmc_remark;
	}

	/**
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 */
	public String getParentPKFieldName() {
		// 
		return null;
	}

	/**
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	public String getPKFieldName() {
		// 
		return "djlxoid";
	}

	/**
	 * @see nc.vo.pub.SuperVO#getTableName()
	 */
	public String getTableName() {
		// 
		return "er_djlx";
	}
	
	public static String getDefaultTableName() {
		return "er_djlx";
	}

	public UFBoolean getIsqr() {
		return isqr;
	}

	public void setIsqr(UFBoolean isqr) {
		this.isqr = isqr;
	}

	public UFBoolean getIsFTSPay() {
		return isftspay;
	}

	public UFBoolean getIsftspay() {
		return isftspay;
	}

	public void setIsftspay(UFBoolean isftspay) {
		this.isftspay = isftspay;
		if(isftspay.booleanValue()){
			isjszxzf = 1;
		}else{
			isjszxzf = 2;
		}
	}

	public String getUsesystem() {
		return usesystem;
	}

	public void setUsesystem(String usesystem) {
		this.usesystem = usesystem;
	}

	public UFBoolean getIsloan() {
		return isloan;
	}

	public void setIsloan(UFBoolean isloan) {
		this.isloan = isloan;
	}

	public UFBoolean getIschangedeptpsn() {
		return ischangedeptpsn;
	}

	public void setIschangedeptpsn(UFBoolean isChangeDeptPsn) {
		this.ischangedeptpsn = isChangeDeptPsn;
	}

	public UFBoolean getIscasign() {
		return iscasign;
	}
	
	public void setIscasign(UFBoolean iscasign) {
		this.iscasign = iscasign;
	}
	public BusiTypeVO getBusitypeVO() {
		return busitypeVOe;
	}
	public void setBusitypeVO(BusiTypeVO busitypeVO) {
		this.busitypeVOe = busitypeVO;
	}

	public UFBoolean getIsSXBeforeWszz() {
		return isSXBeforeWszz;
	}
	public void setIsSXBeforeWszz(UFBoolean isSXBeforeWszz) {
		this.isSXBeforeWszz = isSXBeforeWszz;
	}
	public UFBoolean getIsidvalidated() {
		return isidvalidated;
	}

	public void setIsidvalidated(UFBoolean isidvalidated) {
		this.isidvalidated = isidvalidated;
	}

	public UFBoolean getIspreparenetbank() {
		return ispreparenetbank;
	}

	public void setIspreparenetbank(UFBoolean ispreparenetbank) {
		this.ispreparenetbank = ispreparenetbank;
	}
	public UFBoolean getIsautocombinse() {
		return isautocombinse;
	}
	public void setIsautocombinse(UFBoolean isautocombinse) {
		this.isautocombinse = isautocombinse;
	}
	public UFBoolean getIssettleshow() {
		return issettleshow;
	}
	public void setIssettleshow(UFBoolean issettleshow) {
		this.issettleshow = issettleshow;
	}
	public UFBoolean getIscontrast() {
		return iscontrast;
	}
	public void setIscontrast(UFBoolean iscontrast) {
		this.iscontrast = iscontrast;
	}
	public UFBoolean getIsloadtemplate() {
		return isloadtemplate;
	}
	public void setIsloadtemplate(UFBoolean isloadtemplate) {
		this.isloadtemplate = isloadtemplate;
	}

}
