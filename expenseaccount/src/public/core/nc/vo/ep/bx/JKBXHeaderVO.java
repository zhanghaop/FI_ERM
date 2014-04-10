package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.pub.ErCorpUtil;
import nc.vo.er.pub.IFYControl;
import nc.vo.er.util.StringUtils;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BeanHelper;
import nc.vo.pub.BusinessException;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;

/**
 * 借款报销类单据表体VO
 * 
 * @author ROCKING
 * @author twei
 * 
 *         nc.vo.ep.bx.BXHeaderVO
 */
public abstract class JKBXHeaderVO extends SuperVO implements IFYControl {

	private static final long serialVersionUID = -936531187472578799L;
	
	/**
	 * 是否加载了常用单据
	 */
	private boolean isLoadInitBill = false;
	
	/**
	 * 按行回写费用申请单使用，记录回写申请单的明细行pk
	 */
	private String pk_mtapp_detail = null;

	/**
	 * 回写费用申请单使用，记录业务行pk
	 */
	private String pk_busitem = null;
	
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的借款单明细pk
	 */
	private String jk_busitemPK;
	/**
	 * 回写费用申请单-冲销明细数据包装使用，冲销行中的报销单明细pk
	 */
	private String bx_busitemPK;
	
	/**
	 * 支付组织
	 */
	public static String PK_PAYORG = "pk_payorg";
	public static String PK_PAYORG_V = "pk_payorg_v";
	/**
	 * 成本中心
	 */
	public static String PK_RESACOSTCENTER = "pk_resacostcenter";

	/**
	 * 现金帐户
	 */
	public static String PK_CASHACCOUNT = "pk_cashaccount";

	/**
	 * 现金帐户v6.1新增字段，和单位银行帐号二者其一传结算
	 */
	protected String pk_cashaccount;

	/**
	 * 成本中心v6.1新增字段，预算控制纬度
	 */
	protected String pk_resacostcenter;

	/**
	 * 结算信息表头前缀
	 */
	public static String SETTLE_HEAD_PREFIX = "zb.";

	/**
	 * 结算表头VO
	 */
	protected SuperVO settleHeadVO;

	/**
	 * 审核日期显示日期，不显示时间
	 */
	public static final String SHRQ_SHOW = "shrq_show";

	/**
	 * 审核日期显示日期，不显示时间
	 */
	public UFDate shrq_show;

	/**
	 * 不进行初始化的字段, 用于控制常用单据的加载
	 * 
	 * @return
	 */
	public static String[] getFieldNotInit() {
		return new String[] { JKBXR, DJLXBM, PK_GROUP, OPERATOR, MODIFIER, MODIFIEDTIME, CREATOR, CREATIONTIME,
				PK_JKBX, DJBH, DJRQ, TS, DJZT, KJND, KJQJ, PAYMAN, PAYDATE, PAYFLAG, PK_ORG_V, FYDWBM_V, DWBM_V,
				PK_PCORG_V, DEPTID_V, FYDEPTID_V, DWBM, DEPTID, BBHL, SKYHZH, JSR, APPROVER, START_PERIOD,
				RECEIVER};
	}

	public String getApprover() {
		return approver;
	}

	public void setApprover(String approver) {
		this.approver = approver;
	}

	public void setGroupbbye(UFDouble groupbbye) {
		this.groupbbye = groupbbye;
	}

	/**
	 * 不进行拷贝的字段,用于控制单据的复制功能
	 * 
	 * @return
	 */
	public static String[] getFieldNotCopy() {
		return new String[] { ZHRQ, SXBZ, JSH, DJZT, SPZT, TS, DR, MODIFIER, OPERATOR, APPROVER, MODIFIEDTIME,VOUCHER,
				PK_JKBX, DJBH, DJRQ, KJND, KJQJ, JSRQ, SHRQ, JSR, CONTRASTENDDATE, PAYMAN, PAYDATE, PAYFLAG ,BBHL,GROUPBBHL,GLOBALBBHL,
				START_PERIOD,SHRQ_SHOW,VOUCHERTAG};
	}

	/**
	 * 金额字段, 用于进行单据的合并
	 * 
	 * @return
	 */
	public static String[] getJeField() {
		return new String[] { CJKYBJE, CJKBBJE, ZFYBJE, ZFBBJE, HKYBJE, HKBBJE, YBJE, BBJE, TOTAL, YBYE, BBYE,
				GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE,
				GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * 返回原币金额字段
	 * 
	 * @return
	 */
	public static String[] getYbjeField() {
		return new String[] { CJKYBJE, ZFYBJE, HKYBJE, YBJE, TOTAL, YBYE, YJYE };
	}

	/**
	 * 返回本币金额字段
	 * 
	 * @return
	 */
	public static String[] getBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE, GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE,
				GLOBALBBJE, GLOBALBBYE, GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * 返回组织本币金额字段
	 * 
	 * @return
	 */
	public static String[] getOrgBbjeField() {
		return new String[] { CJKBBJE, ZFBBJE, HKBBJE, BBJE, BBYE };
	}

	/**
	 * 返回表头集团本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGroupBbjeField() {
		return new String[] { GROUPCJKBBJE, GROUPZFBBJE, GROUPHKBBJE, GROUPBBJE, GROUPBBYE };
	}

	/**
	 * 
	 * 返回表头全局本币金额字段
	 * 
	 * @author chendya
	 * @return
	 */
	public static String[] getHeadGlobalBbjeField() {
		return new String[] { GLOBALCJKBBJE, GLOBALZFBBJE, GLOBALHKBBJE, GLOBALBBJE, GLOBALBBYE };
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getPKFieldName() {
		return PK_JKBX;
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getTableName() {

		if (isInit)
			return "er_jkbx_init";

		String tableName = "er_bxzb";

		if (djdl != null) {
			if (djdl.equals(BXConstans.JK_DJDL))
				tableName = "er_jkzb";
		}
		return tableName;

	}

	/**
	 * 返回数值对象的显示名称.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.String 返回数值对象的显示名称.
	 */
	@Override
	public String getEntityName() {

		return "er_bxzb";

	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败,抛出 ValidationException,对错误进行解释.
	 */
	@Override
	public void validate() throws ValidationException {

		validateNullField();
		validateNotNullField2();
	}

	protected void validateNotNullField2() {

		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.YBJE, JKBXHeaderVO.ZPXE);

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000285")/*
																									 * @
																									 * res
																									 * "表头下列字段不能同时为空:"
																									 */);

		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (getFieldName(key) == null && null == getFieldName(value)) {
				message.append("\n");
				message.append(key + "-" + value);
			}
		}
	}

	protected String djlxmc;

	public String getDjlxmc() {
		return djlxmc;
	}

	public void setDjlxmc(String djlxmc) {
		this.djlxmc = djlxmc;
	}

	protected void validateNullField() throws NullFieldException {
		ArrayList<String> errFields = new ArrayList<String>(); // errFields
																// record those
																// null
		List<String> notNullFields = null; // errFields record those null
		// FIXME 暂时注销掉
		String[] str = { JKBXHeaderVO.DJRQ, JKBXHeaderVO.DWBM, JKBXHeaderVO.JKBXR, JKBXHeaderVO.BZBM,
				JKBXHeaderVO.BBHL, JKBXHeaderVO.YBJE, JKBXHeaderVO.BBJE, JKBXHeaderVO.YBYE, JKBXHeaderVO.BBYE,
				JKBXHeaderVO.FYDWBM, JKBXHeaderVO.PK_ORG, JKBXHeaderVO.OPERATOR, JKBXHeaderVO.PK_GROUP, };
		notNullFields = Arrays.asList(str);

		for (String field : notNullFields) {
			if (getAttributeValue(field) == null)
				errFields.add(getFieldName(field));
		}

		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000286")/*
																									 * @
																									 * res
																									 * "表头下列字段不能为空:\n"
																									 */);
		if (errFields.size() > 0) {
			String[] temp = errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message.append(",");
				message.append(temp[i]);
			}
			throw new NullFieldException(message.toString());
		}
	}

	public String getFieldName(String field) {
		// 注意下面组织和集团加多语
		if (field.equals(PK_ORG))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0131")/*
																											 * @
																											 * res
																											 * "报销单位"
																											 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0132")/*
																											 * @
																											 * res
																											 * "借款单位"
																											 */;
		else if (field.equals(PK_GROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC001-0000072")/*
																									 * @
																									 * res
																									 * "集团"
																									 */;
		// public static final String CUSTACCOUNT = "custaccount";
		// public static final String FREECUST = "freecust";
		else if (field.equals(CUSTACCOUNT))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0133")/*
																										 * @
																										 * res
																										 * "客商银行账号"
																										 */;
		else if (field.equals(FREECUST))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0002272")/*
																									 * @
																									 * res
																									 * "散户"
																									 */;
		else if (field.equals(FYDEPTID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000223")/*
																								 * @
																								 * res
																								 * "费用承担部门"
																								 */;
		else if (field.equals(FYDWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000287")/*
																								 * @
																								 * res
																								 * "费用承担公司"
																								 */;
		else if (field.equals(DWBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000288")/*
																								 * @
																								 * res
																								 * "报销人公司"
																								 */;
		else if (field.equals(YBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000280")/*
																								 * @
																								 * res
																								 * "原币金额"
																								 */;
		else if (field.equals(BBJE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000245")/*
																								 * @
																								 * res
																								 * "本币金额"
																								 */;
		else if (field.equals(ISCHECK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000289")/*
																								 * @
																								 * res
																								 * "支票额度"
																								 */;
		else if (field.equals(ISINITGROUP))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0134")/*
																										 * @
																										 * res
																										 * "集团常用单据"
																										 */;
		else if (field.equals(ZPXE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000290")/*
																								 * @
																								 * res
																								 * "支票限额"
																								 */;
		else if (field.equals(BZBM))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000291")/*
																								 * @
																								 * res
																								 * "币种编码"
																								 */;
		else if (field.equals(JKBXR))
			if (djdl.equals(BXConstans.BX_DJDL))
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000292")/*
																									 * @
																									 * res
																									 * "报销人"
																									 */;
			else
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000249")/*
																									 * @
																									 * res
																									 * "借款人"
																									 */;
		else if (field.equals(PJH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000293")/*
																								 * @
																								 * res
																								 * "票据号"
																								 */;
		else if (field.equals(CHECKTYPE))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0003020")/*
																									 * @
																									 * res
																									 * "票据类型"
																									 */;
		else if (field.equals(BBHL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000294")/*
																								 * @
																								 * res
																								 * "本币汇率"
																								 */;
		else if (field.equals(SKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000295")/*
																								 * @
																								 * res
																								 * "收款银行账号"
																								 */;
		else if (field.equals(FKYHZH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000296")/*
																								 * @
																								 * res
																								 * "付款银行账号"
																								 */;
		else if (field.equals(JSH))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000297")/*
																								 * @
																								 * res
																								 * "结算号"
																								 */;
		else if (field.equals(JSFS))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000047")/*
																								 * @
																								 * res
																								 * "结算方式"
																								 */;
		else if (field.equals(TOTAL))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000298")/*
																								 * @
																								 * res
																								 * "合计金额"
																								 */;
		else if (field.equals(DJRQ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000248")/*
																								 * @
																								 * res
																								 * "单据日期"
																								 */;
		else if (field.equals(KJND))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000709")/*
																								 * @
																								 * res
																								 * "会计年度"
																								 */;
		else if (field.equals(KJQJ))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPT2011-000715")/*
																								 * @
																								 * res
																								 * "会计期间"
																								 */;

		else if (field.equals(SZXMID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000224")/*
																								 * @
																								 * res
																								 * "收支项目"
																								 */;
		else if (field.equals(JOBID))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000221")/*
																								 * @
																								 * res
																								 * "项目"
																								 */;
		else if (field.equals(PROJECTTASK))
			return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UCMD1-000312")/*
																								 * @
																								 * res
																								 * "项目任务"
																								 */;
		else
			return field;
	}

	public String pk_payorg;// 支付组织
	public String pk_payorg_v;// 支付组织

	public Integer payflag; // 支付标志
	public String cashproj; // 资金计划项目
	public String busitype; // 业务类型
	public String payman; // 支付人
	public UFDate paydate; // 支付日期
	public String receiver; // 收款人
	public String reimrule; // 报销规则

	public String dwbm;

	public String zyx30;

	public UFDateTime shrq;

	public String zyx4;

	public String zyx20;

	public UFDouble hkbbje;

	public String zyx14;

	public UFBoolean ischeck;

	public UFBoolean isinitgroup; // 常用单据是否集团级

	public UFDouble bbhl;

	public Integer fjzs;

	public String zyx21;

	public String zyx3;

	public String zyx15;

	public String zy;

	public String zyx16;

	public String zyx5;

	public String skyhzh;

	public String zyx25;

	public String zyx18;

	public String zyx9;

	public String zyx13;

	public String zyx24;

	public String jsh;

	public String zyx17;

	public String zyx8;

	public String cashitem;

	public Integer sxbz;

	public String bzbm;

	public UFDouble hkybje;

	public String fydwbm;

	public String zyx6;

	public String zyx11;

	public String fydeptid;

	public UFDouble zpxe;

	public String jobid;

	public String projecttask;

	public String jsfs;

	public String approver;

	public String zyx26;

	public String szxmid;

	public String zyx12;

	public String pk_item;

	public String modifier;

	public String zyx29;

	public String djlxbm;

	public String fkyhzh;

	public UFDouble cjkybje;

	public UFDate jsrq;

	public String zyx23;

	public String operator;

	public String zyx7;

	public String jkbxr;

	public String zyx2;

	public UFDouble zfbbje;

	public String zyx27;

	public String zyx22;

	public String pk_jkbx;

	public String djdl;

	public String zyx10;

	public String pjh;

	public String checktype;

	public String zyx19;

	public String hbbm; // 供应商

	public String customer; // 客户

	public UFDate djrq;

	public String deptid;

	public String zyx28;

	public String djbh;

	public Integer djzt;

	public String zyx1;

	public UFDouble cjkbbje;

	public UFDouble zfybje;

	public UFBoolean qcbz;

	public Integer spzt;

	public UFDateTime ts;

	public Integer dr;

	public UFDate zhrq;

	public UFDouble ybye;

	public UFDouble bbye;

	public UFDate contrastenddate;

	public Integer qzzt;

	public String kjnd;

	public String kjqj;

	public String jsr;

	public UFDate officialprintdate;

	public String officialprintuser;

	public String auditman;

	public UFDouble yjye; // 借款单预计余额

	public UFDouble jsybye; // 结算原币余额

	public String mngaccid;

	public UFDouble ybje;
	public UFDouble bbje;

	public UFDouble total;
	public Integer loantype;

	// v6新增
	public String pk_checkele; // 核算要素
	public String pk_pcorg; // 利润中心
	public String pk_group; // 集团
	public String pk_org; // 业务单元
	public String pk_fiorg; // 财务组织
	public String pk_org_v; // 业务单元版本

	// begin-- added by chendya@ufida.com.cn 组织和部门新增版本化信息
	

	/**
	 * 利润中心版本化
	 */
	public static String PK_PCORG_V = "pk_pcorg_v";
	/**
	 * (借款/报销)部门版本化
	 */
	public static String DEPTID_V = "deptid_v";
	/**
	 * (借款/报销)费用承担部门版本化
	 */
	public static String FYDEPTID_V = "fydeptid_v";
	/**
	 * (借款/报销)单位版本化
	 */
	public static String DWBM_V = "dwbm_v";
	/**
	 * (借款/报销)费用承担单位版本化
	 */
	public static String FYDWBM_V = "fydwbm_v";

	/**
	 * 利润中心版本化
	 */
	public String pk_pcorg_v;

	/**
	 * (借款/报销)部门版本化
	 */
	public String deptid_v;

	/**
	 * (借款/报销)费用承担部门版本化
	 */
	public String fydeptid_v;

	/**
	 * (借款/报销)单位版本化
	 */
	public String dwbm_v;

	/**
	 * (借款/报销)费用承担单位版本化
	 */
	public String fydwbm_v;

	// --end

	// v6新增
	public UFDouble globalcjkbbje; // 全局冲借款本币金额
	public UFDouble globalhkbbje; // 全局还款本币金额
	public UFDouble globalzfbbje; // 全局支付本币金额
	public UFDouble globalbbje; // 全局借款/报销本币金额
	public UFDouble globalbbye; // 全局本币余额。。
	public UFDouble groupbbye; // 集团本币余额。。
	public UFDouble groupcjkbbje; // 集团冲借款本币金额
	public UFDouble grouphkbbje; // 集团还款本币金额
	public UFDouble groupzfbbje; // 集团支付本币金额
	public UFDouble groupbbje; // 集团借款/报销本币金额
	public UFDouble globalbbhl; // 全局本币汇率
	public UFDouble groupbbhl; // 集团本币汇率

	public String creator; // 创建人
	public UFDateTime creationtime; // 创建时间
	public UFDateTime modifiedtime; // 修改时间
	public String custaccount; // 客商银行账号
	public String freecust; // 散户
	public String setorg;

	public UFBoolean iscostshare = UFBoolean.FALSE;// 分摊标志
	public UFBoolean isexpamt = UFBoolean.FALSE;// 摊销标志
	public String start_period;// 开始摊销期间
	public java.lang.Integer total_period;// 总摊销期

	public UFBoolean flexible_flag = UFBoolean.FALSE;// 项目预算-是否柔性控制
	
	public UFBoolean iscusupplier = UFBoolean.FALSE;//对公支付
	public static final String ISCUSUPPLIER = "iscusupplier";
	//v631加入
	public String pk_proline;//产品线
	public String pk_brand;//品牌
	
	public static final String PK_PROLINE = "pk_proline";//产品线
	public static final String PK_BRAND = "pk_brand";//品牌
	
	//ehp2加入
	public Integer paytarget; //收款对象
	public Integer vouchertag; //凭证标志
	public Integer getVouchertag() {
		return vouchertag;
	}

	public void setVouchertag(Integer vouchertag) {
		this.vouchertag = vouchertag;
	}

	public UFDate  tbb_period ;//预算占用期间
	public UFDate getTbb_period() {
		return tbb_period;
	}

	public void setTbb_period(UFDate tbbPeriod) {
		tbb_period = tbbPeriod;
	}

	public Integer getPaytarget() {
		return paytarget;
	}
	
	public void setPaytarget(Integer paytarget) {
		this.paytarget = paytarget;
	}
	public static final String PAYTARGET = "paytarget";
	public static final String TBB_PERIOD = "tbb_period";
	public static final String VOUCHERTAG = "vouchertag";
	
	// v6新增

	public static final String GLOBALCJKBBJE = "globalcjkbbje";
	public static final String GLOBALHKBBJE = "globalhkbbje";
	public static final String GLOBALZFBBJE = "globalzfbbje";
	public static final String GLOBALBBJE = "globalbbje";
	public static final String GLOBALBBYE = "globalbbye";
	public static final String GROUPBBYE = "groupbbye";
	public static final String GROUPCJKBBJE = "groupcjkbbje";
	public static final String GROUPHKBBJE = "grouphkbbje";
	public static final String GROUPZFBBJE = "groupzfbbje";
	public static final String GROUPBBJE = "groupbbje";
	public static final String GLOBALBBHL = "globalbbhl";
	public static final String GROUPBBHL = "groupbbhl";
	public static final String CUSTOMER = "customer";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIEDTIME = "modifiedtime";
	public static final String CUSTACCOUNT = "custaccount";
	public static final String FREECUST = "freecust";

	public static final String PK_CHECKELE = "pk_checkele";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String PK_FIORG = "pk_fiorg";
	public static final String PK_ORG_V = "pk_org_v";

	public static final String PK_GROUP = "pk_group";

	public static final String RECEIVER = "receiver";

	public static final String AMOUNT = "amount";

	public static final String TOTAL = "total";

	public static final String OFFICIALPRINTDATE = "officialprintdate";

	public static final String OFFICIALPRINTUSER = "officialprintuser";

	public static final String KJND = "kjnd";

	public static final String KJQJ = "kjqj";

	public static final String QZZT = "qzzt";

	public static final String CONTRASTENDDATE = "contrastenddate";

	public static final String YBYE = "ybye";

	public static final String BBYE = "bbye";

	public static final String ZHRQ = "zhrq";

	public static final String SPZT = "spzt";

	public static final String TS = "ts";

	public static final String DR = "dr";

	public static final String QCBZ = "qcbz";

	public static final String DWBM = "dwbm";

	public static final String BUSITYPE = "busitype";

	public static final String ZYX30 = "zyx30";

	public static final String SHRQ = "shrq";

	public static final String ZYX4 = "zyx4";

	public static final String ZYX20 = "zyx20";

	public static final String HKBBJE = "hkbbje";

	public static final String ZYX14 = "zyx14";

	public static final String BBHL = "bbhl";

	public static final String FJZS = "fjzs";

	public static final String ZYX21 = "zyx21";

	public static final String ZYX3 = "zyx3";

	public static final String ZYX15 = "zyx15";

	public static final String ZY = "zy";

	public static final String ZYX16 = "zyx16";

	public static final String ZYX5 = "zyx5";

	public static final String SKYHZH = "skyhzh";

	public static final String ZYX25 = "zyx25";

	public static final String ZYX18 = "zyx18";

	public static final String ZYX9 = "zyx9";

	public static final String ZYX13 = "zyx13";

	public static final String YBJE = "ybje";

	public static final String ZYX24 = "zyx24";

	public static final String JSH = "jsh";

	public static final String ZYX17 = "zyx17";

	public static final String ZYX8 = "zyx8";

	public static final String CASHITEM = "cashitem";

	public static final String SXBZ = "sxbz";

	public static final String BZBM = "bzbm";

	public static final String HKYBJE = "hkybje";

	public static final String FYYBJE = "fyybje";

	public static final String FYDWBM = "fydwbm";

	public static final String PK_ORG = "pk_org";

	public static final String ZYX6 = "zyx6";

	public static final String ZYX11 = "zyx11";

	public static final String FYDEPTID = "fydeptid";

	public static final String ZPXE = "zpxe";

	public static final String JOBID = "jobid";

	public static final String PROJECTTASK = "projecttask";

	public static final String JSFS = "jsfs";

	public static final String APPROVER = "approver";

	public static final String ZYX26 = "zyx26";

	public static final String SZXMID = "szxmid";

	public static final String ZYX12 = "zyx12";

	public static final String PK_ITEM = "pk_item";
	
	/**
	 * 费用申请单编号
	 */
	public static final String PK_ITEM_BILLNO = "pk_item.billno";

	public static final String MODIFIER = "modifier";

	public static final String ZYX29 = "zyx29";

	public static final String DJLXBM = "djlxbm";
	public static final String DJLXMC = "djlxmc";

	public static final String FKYHZH = "fkyhzh";

	public static final String CJKYBJE = "cjkybje";

	public static final String JSRQ = "jsrq";

	public static final String ZYX23 = "zyx23";

	public static final String OPERATOR = "operator";

	public static final String ZYX7 = "zyx7";

	public static final String JKBXR = "jkbxr";

	public static final String ZYX2 = "zyx2";

	public static final String ZFBBJE = "zfbbje";

	public static final String BBJE = "bbje";

	public static final String ZYX27 = "zyx27";

	public static final String ZYX22 = "zyx22";

	public static final String PK_JKBX = "pk_jkbx";

	public static final String DJDL = "djdl";

	public static final String ZYX10 = "zyx10";

	public static final String PJH = "pjh";

	public static final String CHECKTYPE = "checktype";

	public static final String ZYX19 = "zyx19";

	public static final String HBBM = "hbbm";

	public static final String DJRQ = "djrq";

	public static final String DEPTID = "deptid";

	public static final String ZYX28 = "zyx28";

	public static final String DJBH = "djbh";

	public static final String DJZT = "djzt";

	public static final String ZYX1 = "zyx1";

	public static final String CJKBBJE = "cjkbbje";

	public static final String ZFYBJE = "zfybje";

	public static final String JSR = "jsr";

	public static final String ISCHECK = "ischeck";

	public static final String ISINITGROUP = "isinitgroup"; // 常用单据是否集团级

	public static final String CASHPROJ = "cashproj";

	public static final String JK = "jk";

	public static final String ZPJE = "zpje";

	public static final String PAYFLAG = "payflag";
	public static final String PAYDATE = "paydate";
	public static final String PAYMAN = "payman";

	public static final String MNGACCID_MC = "mngaccid_mc";
	public static final String YJYE = "yjye";
	public static final String MNGACCID = "mngaccid";
	public static final String SETORG = "setorg";

	public static final String ISCOSTSHARE = "iscostshare";
	public static final String ISEXPAMT = "isexpamt";
	public static final String START_PERIOD = "start_period";
	public static final String TOTAL_PERIOD = "total_period";
	public static final String FLEXIBLE_FLAG = "flexible_flag";
	public static final String CENTER_DEPT = "center_dept";
	
	/**
	 * 来源交易类型
	 */
	public static final String SRCBILLTYPE = "srcbilltype";
	/**
	 * 来源类型，默认为费用申请单
	 */
	public static final String SRCTYPE = "srctype";
	/**
	 * 是否拉单的申请单分摊，从费用申请单上拉过来
	 */
	public static final String ISMASHARE = "ismashare";

	/**
	 * 审批流起点人
	 */
	public static final String AUDITMAN = "auditman";
	
	/**
	 * 归口管理部门
	 */
	private java.lang.String center_dept;
	
	/**
	 * 来源单据类型
	 */
	private String srcbilltype;
	private String srctype;
	
	/**
	 * 是否申请单分摊拉单
	 */
	private UFBoolean ismashare;

	public UFDate getOfficialprintdate() {
		return officialprintdate;
	}

	public void setOfficialprintdate(UFDate officialprintdate) {
		this.officialprintdate = officialprintdate;
	}

	public String getOfficialprintuser() {
		return officialprintuser;
	}

	public void setOfficialprintuser(String officialprintuser) {
		this.officialprintuser = officialprintuser;
	}

	public String getJsr() {
		return jsr;
	}

	public void setJsr(String jsr) {
		this.jsr = jsr;
	}

	/**
	 * 属性pk_corp的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDwbm() {
		return dwbm;
	}

	/**
	 * 属性pk_corp的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_corp
	 *            String
	 */
	public void setDwbm(String dwbm) {

		this.dwbm = dwbm;
	}

	/**
	 * 属性zyx30的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx30() {
		return zyx30;
	}

	/**
	 * 属性zyx30的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx30
	 *            String
	 */
	public void setZyx30(String newZyx30) {

		zyx30 = newZyx30;
	}

	/**
	 * 属性shrq的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDateTime
	 */
	public UFDateTime getShrq() {
		return shrq;
	}

	/**
	 * 属性shrq的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newShrq
	 *            UFDate
	 */
	public void setShrq(UFDateTime newShrq) {

		shrq = newShrq;
	}

	/**
	 * 属性zyx4的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx4() {
		return zyx4;
	}

	/**
	 * 属性zyx4的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx4
	 *            String
	 */
	public void setZyx4(String newZyx4) {

		zyx4 = newZyx4;
	}

	/**
	 * 属性zyx20的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx20() {
		return zyx20;
	}

	/**
	 * 属性zyx20的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx20
	 *            String
	 */
	public void setZyx20(String newZyx20) {

		zyx20 = newZyx20;
	}

	/**
	 * 属性hkbbje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getHkbbje() {
		if (hkbbje == null)
			return UFDouble.ZERO_DBL;
		return hkbbje;
	}

	/**
	 * 属性hkbbje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newHkbbje
	 *            UFDouble
	 */
	public void setHkbbje(UFDouble newHkbbje) {

		hkbbje = newHkbbje;
	}

	/**
	 * 属性zyx14的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx14() {
		return zyx14;
	}

	/**
	 * 属性zyx14的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx14
	 *            String
	 */
	public void setZyx14(String newZyx14) {

		zyx14 = newZyx14;
	}

	public UFBoolean getIscheck() {
		if (ischeck == null)
			return UFBoolean.FALSE;
		return ischeck;
	}

	public void setIscheck(UFBoolean newisCheck) {

		ischeck = newisCheck;
	}

	public UFBoolean getIsinitgroup() {
		if (isinitgroup == null)
			isinitgroup = UFBoolean.FALSE;
		return isinitgroup;
	}

	public void setIsinitgroup(UFBoolean isinitgroup) {
		this.isinitgroup = isinitgroup;
	}

	/**
	 * 属性bbhl的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getBbhl() {
		return bbhl;
	}

	/**
	 * 属性bbhl的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newBbhl
	 *            UFDouble
	 */
	public void setBbhl(UFDouble newBbhl) {

		bbhl = newBbhl;
	}

	/**
	 * 属性fjzs的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getFjzs() {
		return fjzs;
	}

	/**
	 * 属性fjzs的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newFjzs
	 *            Integer
	 */
	public void setFjzs(Integer newFjzs) {

		fjzs = newFjzs;
	}

	/**
	 * 属性zyx21的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx21() {
		return zyx21;
	}

	/**
	 * 属性zyx21的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx21
	 *            String
	 */
	public void setZyx21(String newZyx21) {

		zyx21 = newZyx21;
	}

	/**
	 * 属性zyx3的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx3() {
		return zyx3;
	}

	/**
	 * 属性zyx3的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx3
	 *            String
	 */
	public void setZyx3(String newZyx3) {

		zyx3 = newZyx3;
	}

	/**
	 * 属性zyx15的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx15() {
		return zyx15;
	}

	/**
	 * 属性zyx15的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx15
	 *            String
	 */
	public void setZyx15(String newZyx15) {

		zyx15 = newZyx15;
	}

	/**
	 * 属性zy的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZy() {
		return zy;
	}

	/**
	 * 属性zy的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZy
	 *            String
	 */
	public void setZy(String newZy) {

		zy = newZy;
	}

	/**
	 * 属性zyx16的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx16() {
		return zyx16;
	}

	/**
	 * 属性zyx16的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx16
	 *            String
	 */
	public void setZyx16(String newZyx16) {

		zyx16 = newZyx16;
	}

	/**
	 * 属性zyx5的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx5() {
		return zyx5;
	}

	/**
	 * 属性zyx5的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx5
	 *            String
	 */
	public void setZyx5(String newZyx5) {

		zyx5 = newZyx5;
	}

	/**
	 * 属性skyhzh的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getSkyhzh() {
		return skyhzh;
	}

	/**
	 * 属性skyhzh的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newSkyhzh
	 *            String
	 */
	public void setSkyhzh(String newSkyhzh) {

		skyhzh = newSkyhzh;
	}

	/**
	 * 属性zyx25的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx25() {
		return zyx25;
	}

	/**
	 * 属性zyx25的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx25
	 *            String
	 */
	public void setZyx25(String newZyx25) {

		zyx25 = newZyx25;
	}

	/**
	 * 属性zyx18的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx18() {
		return zyx18;
	}

	/**
	 * 属性zyx18的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx18
	 *            String
	 */
	public void setZyx18(String newZyx18) {

		zyx18 = newZyx18;
	}

	/**
	 * 属性zyx9的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx9() {
		return zyx9;
	}

	/**
	 * 属性zyx9的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx9
	 *            String
	 */
	public void setZyx9(String newZyx9) {

		zyx9 = newZyx9;
	}

	/**
	 * 属性zyx13的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx13() {
		return zyx13;
	}

	/**
	 * 属性zyx13的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx13
	 *            String
	 */
	public void setZyx13(String newZyx13) {

		zyx13 = newZyx13;
	}

	/**
	 * 属性zyx24的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx24() {
		return zyx24;
	}

	/**
	 * 属性zyx24的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx24
	 *            String
	 */
	public void setZyx24(String newZyx24) {

		zyx24 = newZyx24;
	}

	/**
	 * 属性jsh的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getJsh() {
		return jsh;
	}

	/**
	 * 属性jsh的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newJsh
	 *            String
	 */
	public void setJsh(String newJsh) {

		jsh = newJsh;
	}

	/**
	 * 属性zyx17的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx17() {
		return zyx17;
	}

	/**
	 * 属性zyx17的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx17
	 *            String
	 */
	public void setZyx17(String newZyx17) {

		zyx17 = newZyx17;
	}

	/**
	 * 属性zyx8的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx8() {
		return zyx8;
	}

	/**
	 * 属性zyx8的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx8
	 *            String
	 */
	public void setZyx8(String newZyx8) {

		zyx8 = newZyx8;
	}

	/**
	 * 属性cashitem的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getCashitem() {
		return cashitem;
	}

	/**
	 * 属性cashitem的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newCashitem
	 *            String
	 */
	public void setCashitem(String newCashitem) {

		cashitem = newCashitem;
	}

	/**
	 * 属性sxbz的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getSxbz() {
		return sxbz;
	}

	/**
	 * 属性sxbz的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newSxbz
	 *            Integer
	 */
	public void setSxbz(Integer newSxbz) {

		sxbz = newSxbz;
	}

	/**
	 * 属性bzbm的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getBzbm() {
		return bzbm;
	}

	/**
	 * 属性bzbm的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newBzbm
	 *            String
	 */
	public void setBzbm(String newBzbm) {

		bzbm = newBzbm;
	}

	/**
	 * 属性hkybje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getHkybje() {
		if (hkybje == null)
			return UFDouble.ZERO_DBL;
		return hkybje;
	}

	/**
	 * 属性hkybje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newHkybje
	 *            UFDouble
	 */
	public void setHkybje(UFDouble newHkybje) {

		hkybje = newHkybje;
	}

	/**
	 * 属性fydwbm的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getFydwbm() {
		return fydwbm;
	}

	/**
	 * 属性fydwbm的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newFydwbm
	 *            String
	 */
	public void setFydwbm(String newFydwbm) {

		fydwbm = newFydwbm;
	}

	/**
	 * 属性zyx6的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx6() {
		return zyx6;
	}

	/**
	 * 属性zyx6的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx6
	 *            String
	 */
	public void setZyx6(String newZyx6) {

		zyx6 = newZyx6;
	}

	/**
	 * 属性zyx11的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx11() {
		return zyx11;
	}

	/**
	 * 属性zyx11的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx11
	 *            String
	 */
	public void setZyx11(String newZyx11) {

		zyx11 = newZyx11;
	}

	/**
	 * 属性fydeptid的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getFydeptid() {
		return fydeptid;
	}

	/**
	 * 属性fydeptid的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newFydeptid
	 *            String
	 */
	public void setFydeptid(String newFydeptid) {

		fydeptid = newFydeptid;
	}

	/**
	 * 属性zpxe的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZpxe() {
		return zpxe;
	}

	/**
	 * 属性zpxe的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZpxe
	 *            UFDouble
	 */
	public void setZpxe(UFDouble newZpxe) {

		zpxe = newZpxe;
	}

	/**
	 * 属性jobid的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getJobid() {
		return jobid;
	}

	/**
	 * 属性jobid的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newJobid
	 *            String
	 */
	public void setJobid(String newJobid) {

		jobid = newJobid;
	}

	public String getProjecttask() {
		return projecttask;
	}

	public void setProjecttask(String projecttask) {
		this.projecttask = projecttask;
	}

	/**
	 * 属性jsfs的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getJsfs() {
		return jsfs;
	}

	/**
	 * 属性jsfs的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newJsfs
	 *            String
	 */
	public void setJsfs(String newJsfs) {

		jsfs = newJsfs;
	}

	/**
	 * 属性zyx26的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx26() {
		return zyx26;
	}

	/**
	 * 属性zyx26的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx26
	 *            String
	 */
	public void setZyx26(String newZyx26) {

		zyx26 = newZyx26;
	}

	/**
	 * 属性szxmid的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getSzxmid() {
		return szxmid;
	}

	/**
	 * 属性szxmid的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newSzxmid
	 *            String
	 */
	public void setSzxmid(String newSzxmid) {

		szxmid = newSzxmid;
	}

	/**
	 * 属性zyx12的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx12() {
		return zyx12;
	}

	/**
	 * 属性zyx12的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx12
	 *            String
	 */
	public void setZyx12(String newZyx12) {

		zyx12 = newZyx12;
	}

	/**
	 * 属性pk_item的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_item() {
		return pk_item;
	}

	/**
	 * 属性pk_item的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_item
	 *            String
	 */
	public void setPk_item(String newPk_item) {

		pk_item = newPk_item;
	}

	/**
	 * 属性modifier的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getModifier() {
		return modifier;
	}

	/**
	 * 属性modifier的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newModifier
	 *            String
	 */
	public void setModifier(String newModifier) {

		modifier = newModifier;
	}

	/**
	 * 属性zyx29的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx29() {
		return zyx29;
	}

	/**
	 * 属性zyx29的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx29
	 *            String
	 */
	public void setZyx29(String newZyx29) {

		zyx29 = newZyx29;
	}

	/**
	 * 属性djlxbm的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjlxbm() {
		return djlxbm;
	}

	/**
	 * 属性djlxbm的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDjlxbm
	 *            String
	 */
	public void setDjlxbm(String newDjlxbm) {

		djlxbm = newDjlxbm;
	}

	/**
	 * 属性fkyhzh的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getFkyhzh() {
		return fkyhzh;
	}

	/**
	 * 属性fkyhzh的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newFkyhzh
	 *            String
	 */
	public void setFkyhzh(String newFkyhzh) {

		fkyhzh = newFkyhzh;
	}

	/**
	 * 属性cjkybje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getCjkybje() {
		if (cjkybje == null)
			return UFDouble.ZERO_DBL;
		return cjkybje;
	}

	/**
	 * 属性cjkybje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newCjkybje
	 *            UFDouble
	 */
	public void setCjkybje(UFDouble newCjkybje) {

		cjkybje = newCjkybje;
	}

	/**
	 * 属性jsrq的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDate
	 */
	public UFDate getJsrq() {
		return jsrq;
	}

	/**
	 * 属性jsrq的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param jsrq
	 *            UFDate
	 */
	public void setJsrq(UFDate jsrq) {

		this.jsrq = jsrq;
	}

	/**
	 * 属性zyx23的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx23() {
		return zyx23;
	}

	/**
	 * 属性zyx23的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx23
	 *            String
	 */
	public void setZyx23(String newZyx23) {

		zyx23 = newZyx23;
	}

	/**
	 * 录入人 属性operator的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * 属性operator的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newOperator
	 *            String
	 */
	public void setOperator(String newOperator) {

		operator = newOperator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	/**
	 * 属性zyx7的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx7() {
		return zyx7;
	}

	/**
	 * 属性zyx7的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx7
	 *            String
	 */
	public void setZyx7(String newZyx7) {

		zyx7 = newZyx7;
	}

	/**
	 * 属性jkbxr的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getJkbxr() {
		return jkbxr;
	}

	/**
	 * 属性jkbxr的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newJkbxr
	 *            String
	 */
	public void setJkbxr(String newJkbxr) {

		jkbxr = newJkbxr;
	}

	/**
	 * 属性zyx2的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx2() {
		return zyx2;
	}

	/**
	 * 属性zyx2的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx2
	 *            String
	 */
	public void setZyx2(String newZyx2) {

		zyx2 = newZyx2;
	}

	/**
	 * 属性zfbbje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZfbbje() {
		if (zfbbje == null)
			return UFDouble.ZERO_DBL;
		return zfbbje;
	}

	/**
	 * 属性zfbbje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZfbbje
	 *            UFDouble
	 */
	public void setZfbbje(UFDouble newZfbbje) {

		zfbbje = newZfbbje;
	}

	/**
	 * 属性zyx27的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx27() {
		return zyx27;
	}

	/**
	 * 属性zyx27的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx27
	 *            String
	 */
	public void setZyx27(String newZyx27) {

		zyx27 = newZyx27;
	}

	/**
	 * 属性zyx22的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx22() {
		return zyx22;
	}

	/**
	 * 属性zyx22的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx22
	 *            String
	 */
	public void setZyx22(String newZyx22) {

		zyx22 = newZyx22;
	}

	/**
	 * 属性pk_jkbx的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getPk_jkbx() {
		return pk_jkbx;
	}

	/**
	 * 属性pk_jkbx的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            String
	 */
	public void setPk_jkbx(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;
	}

	/**
	 * 属性djdl的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjdl() {
		return djdl;
	}

	/**
	 * 属性djdl的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDjdl
	 *            String
	 */
	public void setDjdl(String newDjdl) {

		djdl = newDjdl;
	}

	/**
	 * 属性zyx10的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx10() {
		return zyx10;
	}

	/**
	 * 属性zyx10的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx10
	 *            String
	 */
	public void setZyx10(String newZyx10) {

		zyx10 = newZyx10;
	}

	/**
	 * 属性pjh的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getPjh() {
		return pjh;
	}

	/**
	 * 属性pjh的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPjh
	 *            String
	 */
	public void setPjh(String newPjh) {

		pjh = newPjh;
	}

	/**
	 * 属性checktype的Getter方法.
	 * 
	 * 创建日期:2011-05-24
	 * 
	 * @return String
	 */
	public String getChecktype() {
		return checktype;
	}

	/**
	 * 属性checktype的Setter方法.
	 * 
	 * 创建日期:2011-05-24
	 * 
	 * @param newChecktype
	 *            String
	 */
	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	/**
	 * 属性zyx19的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx19() {
		return zyx19;
	}

	/**
	 * 属性zyx19的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx19
	 *            String
	 */
	public void setZyx19(String newZyx19) {

		zyx19 = newZyx19;
	}

	/**
	 * 属性hbbm的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getHbbm() {
		return hbbm;
	}

	/**
	 * 属性hbbm的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newHbbm
	 *            String
	 */
	public void setHbbm(String newHbbm) {

		hbbm = newHbbm;
	}

	/**
	 * 属性djrq的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDate
	 */
	public UFDate getDjrq() {
		return djrq;
	}

	/**
	 * 属性djrq的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDjrq
	 *            UFDate
	 */
	public void setDjrq(UFDate newDjrq) {

		djrq = newDjrq;
	}

	/**
	 * 属性deptid的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDeptid() {
		return deptid;
	}

	/**
	 * 属性deptid的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDeptid
	 *            String
	 */
	public void setDeptid(String newDeptid) {

		deptid = newDeptid;
	}

	/**
	 * 属性zyx28的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx28() {
		return zyx28;
	}

	/**
	 * 属性zyx28的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx28
	 *            String
	 */
	public void setZyx28(String newZyx28) {

		zyx28 = newZyx28;
	}

	/**
	 * 属性djbh的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getDjbh() {
		return djbh;
	}

	/**
	 * 属性djbh的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDjbh
	 *            String
	 */
	public void setDjbh(String newDjbh) {

		djbh = newDjbh;
	}

	/**
	 * 属性djzt的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return Integer
	 */
	public Integer getDjzt() {
		return djzt;
	}

	/**
	 * 属性djzt的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newDjzt
	 *            Integer
	 */
	public void setDjzt(Integer newDjzt) {

		djzt = newDjzt;
	}

	/**
	 * 属性zyx1的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	public String getZyx1() {
		return zyx1;
	}

	/**
	 * 属性zyx1的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZyx1
	 *            String
	 */
	public void setZyx1(String newZyx1) {

		zyx1 = newZyx1;
	}

	/**
	 * 属性cjkbbje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getCjkbbje() {
		if (cjkbbje == null)
			return UFDouble.ZERO_DBL;
		return cjkbbje;
	}

	/**
	 * 属性cjkbbje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newCjkbbje
	 *            UFDouble
	 */
	public void setCjkbbje(UFDouble newCjkbbje) {

		cjkbbje = newCjkbbje;
	}

	/**
	 * 属性zfybje的Getter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return UFDouble
	 */
	public UFDouble getZfybje() {
		if (zfybje == null)
			return UFDouble.ZERO_DBL;
		return zfybje;
	}

	/**
	 * 属性zfybje的Setter方法.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newZfybje
	 *            UFDouble
	 */
	public void setZfybje(UFDouble newZfybje) {

		zfybje = newZfybje;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-6-13
	 * 
	 * @return java.lang.String
	 */
	@Override
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2007-6-13
	 */
	public JKBXHeaderVO() {

		super();
	}

	/**
	 * 使用主键进行初始化的构造子.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            主键值
	 */
	public JKBXHeaderVO(String newPk_jkbx) {

		// 为主键字段赋值:
		pk_jkbx = newPk_jkbx;

	}

	/**
	 * 返回对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @return String
	 */
	@Override
	public String getPrimaryKey() {

		return pk_jkbx;

	}

	/**
	 * 设置对象标识,用来唯一定位对象.
	 * 
	 * 创建日期:2007-6-13
	 * 
	 * @param newPk_jkbx
	 *            String
	 */
	@Override
	public void setPrimaryKey(String newPk_jkbx) {

		pk_jkbx = newPk_jkbx;

	}

	public UFBoolean getQcbz() {
		if (qcbz == null)
			return UFBoolean.FALSE;
		return qcbz;
	}

	public void setQcbz(UFBoolean qcbz) {
		this.qcbz = qcbz;
	}

	public Integer getSpzt() {
		if(spzt == null){
			spzt = IPfRetCheckInfo.NOSTATE;
		}
		return spzt;
	}

	public void setSpzt(Integer spzt) {
		this.spzt = spzt;
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

	public UFDate getZhrq() {
		return zhrq;
	}

	public void setZhrq(UFDate zhrq) {
		this.zhrq = zhrq;
	}

	public UFDouble getBbye() {
		return bbye;
	}

	public void setBbye(UFDouble bbye) {
		this.bbye = bbye;
	}

	public UFDouble getYbye() {
		return ybye;
	}

	public void setYbye(UFDouble ybye) {
		this.ybye = ybye;
	}

	/**
	 * VO增加字段, 供界面展示用, 不固化到数据库. VO增加字段, 供界面展示用, 不固化到数据库.
	 */
	public static final String SELECTED = "selected";
	protected UFBoolean selected = UFBoolean.FALSE;

	public UFBoolean getSelected() {
		return selected;
	}

	public void setSelected(UFBoolean selected) {
		this.selected = selected;
	}

	public static final String VOUCHER = "voucher";
	protected String voucher;

	public String getVoucher() {
		return voucher;
	}

	public void setVoucher(String voucher) {
		this.voucher = voucher;
	}

	/**
	 * 业务数据, 供逻辑控制使用, 不固化到数据库. 业务数据, 供逻辑控制使用, 不固化到数据库.
	 */

	protected boolean isInit; // 是否常用单据

	public boolean isInit() {
		return isInit;
	}

	public void setInit(boolean isInit) {
		this.isInit = isInit;
	}

	/**
	 * 显示函数, 供界面展示用, 不使用于业务逻辑. 显示函数, 供界面展示用, 不使用于业务逻辑.
	 */
	protected String jkr, bxr;

	public void setBxr(String bxr) {
		this.bxr = bxr;
	}

	public void setJkr(String jkr) {
		this.jkr = jkr;
	}

	public String getJkr() {
		if (jkr != null)
			return jkr;
		if (djdl != null && djdl.equals(BXConstans.JK_DJDL))
			return getJkbxr();
		else
			return null;
	}

	public String getBxr() {
		if (bxr != null)
			return bxr;
		if (djdl != null && djdl.equals(BXConstans.BX_DJDL))
			return getJkbxr();
		else
			return null;
	}

	/**
	 * 逻辑函数, 供逻辑控制用, 不进行数据修改. 逻辑函数, 供逻辑控制用, 不进行数据修改.
	 */
	public boolean isRepayBill() { // 是否还款单据
		return getHkybje() != null && getHkybje().doubleValue() > 0;
	}

	public boolean isXeBill() { // 是否限额支票型单据
		return getIscheck().booleanValue();
	}

	protected String hyflag;

	protected boolean isFySaveControl = false; // 费用控制审核时控制

	public boolean isFySaveControl() {
		return isFySaveControl;
	}

	public void setFySaveControl(boolean isFySaveControl) {
		this.isFySaveControl = isFySaveControl;
	}

	protected boolean isunAudit;

	public boolean isIsunAudit() {
		return isunAudit;
	}

	public void setIsunAudit(boolean isunAudit) {
		this.isunAudit = isunAudit;
	}

	/**
	 * @return 是否不需要需要走其他影响接口
	 */
	public boolean isNoOtherEffectItf() {

		boolean status = false;

		if (isInit())
			status = true;
		// if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
		// status=true;

		return status;
	}

	public UFDate getContrastenddate() {
		return contrastenddate;
	}

	public void setContrastenddate(UFDate contrastEndDate) {
		this.contrastenddate = contrastEndDate;
	}

	public Integer getQzzt() {
		return qzzt;
	}

	public void setQzzt(Integer qzzt) {
		this.qzzt = qzzt;
	}

	public String getKjnd() {
		return kjnd;
	}

	public void setKjnd(String kjnd) {
		this.kjnd = kjnd;
	}

	public String getKjqj() {
		return kjqj;
	}

	public void setKjqj(String kjqj) {
		this.kjqj = kjqj;
	}

	// /////////////////事项审批控制接口需要实现的方法/////////////////

	public boolean isSaveControl() {
		return isFySaveControl();
	}

	public UFDate getOperationDate() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getDjrq();
			// FIXME 审核日期注销
			// }else if(getDjzt().intValue()==BXStatusConst.DJZT_Verified){
			// return getShrq();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsrq();
		}
		return null;
	}

	public String getOperationUser() {
		if (getDjzt() == null)
			return null;
		if (getDjzt().intValue() == BXStatusConst.DJZT_Saved) {
			return getOperator();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Verified) {
			return getApprover();
		} else if (getDjzt().intValue() == BXStatusConst.DJZT_Sign) {
			return getJsr();
		}
		return null;
	}

	public boolean isSSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (StringUtils.isNullWithTrim(getPk_item()))
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isYSControlAble() {

		boolean status = true;

		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public boolean isJKControlAble() {

		boolean status = true;

		if (getDjdl() == null || !getDjdl().equals(BXConstans.JK_DJDL))
			status = false;
		if (getQcbz() != null && getQcbz().booleanValue())
			status = false;
		if (getDjzt().equals(BXStatusConst.DJZT_TempSaved))
			status = false;

		return status;
	}

	public UFDouble[] getItemHl() {
		return new UFDouble[] { getGlobalbbhl(), getGroupbbhl(), getBbhl() };
	}

	public UFDouble[] getItemJe() {
		return new UFDouble[] { getGlobalbbje(), getGroupbbje(), getBbje(), getYbje() };
	}
	
	private UFDouble[] preItemJe;

	@Override
	public UFDouble[] getPreItemJe() {
		return preItemJe;
	}

	public void setPreItemJe(UFDouble[] preItemJe) {
		this.preItemJe = preItemJe;
	}

	public Object getItemValue(String key) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("zrdeptid", "fydeptid");
		map.put("xmbm2", "jobid");
		map.put("ywybm", "jkbxr");
		return getAttributeValue(map.get(key) == null ? key : map.get(key));
	}

	public String getDdlx() {
		return null;
	}

	public Integer getFx() {
		return isRepayBill() ? Integer.valueOf(-1) : Integer.valueOf(1);
	}

	// /////////////////事项审批控制接口需要实现的方法/////////////////

	public String getBusitype() {
		return busitype;
	}

	public void setBusitype(String busitype) {
		this.busitype = busitype;
	}

	/**
	 * 
	 * 返回审批流起点人
	 */
	public String getAuditman() throws BusinessException {
		String result = "";
		result = ErCorpUtil.getBxCtlMan(this);
		return result;
	}

	/* 重写此方法的目的是为了clone的时候，不克隆auditman，因为getAuditman有多次远程调用 */
	@Override
	public String[] getAttributeNames() {
		List<String> retValues = new ArrayList<String>();
		final String[] names = super.getAttributeNames();
		for (int i = 0; i < names.length; i++) {
			if (AUDITMAN.equals(names[i])) {
				continue;
			}
			retValues.add(names[i]);
		}
		return retValues.toArray(new String[0]);
	}

	public void setAuditman(String man) {

	}

	public UFDouble getYjye() {
		return yjye;
	}

	public void setYjye(UFDouble yjye) {
		this.yjye = yjye;
	}

	public UFDouble getJsybye() {
		return jsybye;
	}

	public void setJsybye(UFDouble jsybye) {
		this.jsybye = jsybye;
	}

	public UFDouble getBbje() {
		return bbje;
	}

	public void setBbje(UFDouble bbje) {
		this.bbje = bbje;
	}

	public UFDouble getYbje() {
		return ybje;
	}

	public void setYbje(UFDouble ybje) {
		this.ybje = ybje;
	}

	public String getCashproj() {
		return cashproj;
	}

	public void setCashproj(String cashproj) {
		this.cashproj = cashproj;
	}

	public Integer getPayflag() {
		return payflag;
	}

	public void setPayflag(Integer payflag) {
		this.payflag = payflag;
	}

	public UFDouble getTotal() {
		return total;
	}

	public void setTotal(UFDouble total) {
		this.total = total;
	}

	public UFDate getPaydate() {
		return paydate;
	}

	public void setPaydate(UFDate paydate) {
		this.paydate = paydate;
	}

	public String getPayman() {
		return payman;
	}

	public void setPayman(String payman) {
		this.payman = payman;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getLoantype() {
		return loantype;
	}

	public void setLoantype(Integer loantype) {
		this.loantype = loantype;
	}

	@Override
	public Object getAttributeValue(String key) {
		// 如果包含结算信息字段，从结算vo中取值
		if (key.startsWith(SETTLE_HEAD_PREFIX)) {
			if (getSettleHeadVO() != null) {
				String attribute = key.substring(key.indexOf(SETTLE_HEAD_PREFIX) + SETTLE_HEAD_PREFIX.length());
				return getSettleHeadVO().getAttributeValue(attribute);
			}
		}
		
		String name = null;
		Object result = null;
		String[] tokens = StringUtil.split(key, ".");
		if (tokens.length == 1) {
			name = key;
		}else{
			name = tokens[1];
		}
		if(BeanHelper.getMethod(this, name) != null){
			result = BeanHelper.getProperty(this, name);//用来处理借款报销中字段不一致问题
		}else{
			result = super.getAttributeValue(name);
		}

		return result;
	}
	
	@Override
	public void setAttributeValue(String name, Object value) {
		if (BeanHelper.getMethod(this, name) != null) {
			BeanHelper.setProperty(this, name, value);
		} else {
			super.setAttributeValue(name, value);
		}
	}

	public String getPk_org_v() {
		return pk_org_v;
	}

	public void setPk_org_v(String pkOrgV) {
		pk_org_v = pkOrgV;
	}

	public String getHyflag() {
		return hyflag;
	}

	public void setHyflag(String hyflag) {
		this.hyflag = hyflag;
	}

	public String getMngaccid() {
		return mngaccid;
	}

	public void setMngaccid(String mngaccid) {
		this.mngaccid = mngaccid;
	}

	public String getReimrule() {
		return reimrule;
	}

	public void setReimrule(String reimrule) {
		this.reimrule = reimrule;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
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

	public String getPk_fiorg() {
		return pk_fiorg;
	}

	public void setPk_fiorg(String pk_fiorg) {
		this.pk_fiorg = pk_fiorg;
	}

	/**
	 * 属性globalcjkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getGlobalcjkbbje() {
		return globalcjkbbje;
	}

	/**
	 * 属性globalcjkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalcjkbbje
	 *            UFDouble
	 */
	public void setGlobalcjkbbje(UFDouble newGlobalcjkbbje) {
		this.globalcjkbbje = newGlobalcjkbbje;
	}

	/**
	 * 属性globalhkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalhkbbje() {
		return globalhkbbje;
	}

	/**
	 * 属性globalhkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalhkbbje
	 *            UFDouble
	 */
	public void setGlobalhkbbje(UFDouble newGlobalhkbbje) {
		this.globalhkbbje = newGlobalhkbbje;
	}

	/**
	 * 属性globalzfbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalzfbbje() {
		return globalzfbbje;
	}

	/**
	 * 属性globalzfbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalzfbbje
	 *            UFDouble
	 */
	public void setGlobalzfbbje(UFDouble newGlobalzfbbje) {
		this.globalzfbbje = newGlobalzfbbje;
	}

	/**
	 * 属性globalbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbje() {
		return globalbbje;
	}

	/**
	 * 属性globalbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbje
	 *            UFDouble
	 */
	public void setGlobalbbje(UFDouble newGlobalbbje) {
		this.globalbbje = newGlobalbbje;
	}

	/**
	 * 属性globalbbye的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbye() {
		return globalbbye;
	}

	/**
	 * 属性globalbbye的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbye
	 *            UFDouble
	 */
	public void setGlobalbbye(UFDouble newGlobalbbye) {
		this.globalbbye = newGlobalbbye;
	}

	/**
	 * 属性groupbbye的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbye() {
		return groupbbye;
	}

	/**
	 * 属性groupbbye的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbye
	 *            UFDouble
	 */
	public void setgroupbbye(UFDouble newGroupbbye) {
		this.groupbbye = newGroupbbye;
	}

	/**
	 * 属性groupcjkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupcjkbbje() {
		return groupcjkbbje;
	}

	/**
	 * 属性groupcjkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGroupcjkbbje
	 *            UFDouble
	 */
	public void setGroupcjkbbje(UFDouble newGroupcjkbbje) {
		this.groupcjkbbje = newGroupcjkbbje;
	}

	/**
	 * 属性grouphkbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGrouphkbbje() {
		return grouphkbbje;
	}

	/**
	 * 属性grouphkbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGrouphkbbje
	 *            UFDouble
	 */
	public void setGrouphkbbje(UFDouble newGrouphkbbje) {
		this.grouphkbbje = newGrouphkbbje;
	}

	/**
	 * 属性groupzfbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupzfbbje() {
		return groupzfbbje;
	}

	/**
	 * 属性groupzfbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGroupzfbbje
	 *            UFDouble
	 */
	public void setGroupzfbbje(UFDouble newGroupzfbbje) {
		this.groupzfbbje = newGroupzfbbje;
	}

	/**
	 * 属性groupbbje的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbje() {
		return groupbbje;
	}

	/**
	 * 属性groupbbje的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbje
	 *            UFDouble
	 */
	public void setGroupbbje(UFDouble newGroupbbje) {
		this.groupbbje = newGroupbbje;
	}

	/**
	 * 属性globalbbhl的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGlobalbbhl() {
		return globalbbhl;
	}

	/**
	 * 属性globalbbhl的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGlobalbbhl
	 *            UFDouble
	 */
	public void setGlobalbbhl(UFDouble newGlobalbbhl) {
		this.globalbbhl = newGlobalbbhl;
	}

	/**
	 * 属性groupbbhl的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return UFDouble
	 */
	public UFDouble getGroupbbhl() {
		return groupbbhl;
	}

	/**
	 * 属性groupbbhl的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newGroupbbhl
	 *            UFDouble
	 */
	public void setGroupbbhl(UFDouble newGroupbbhl) {
		this.groupbbhl = newGroupbbhl;
	}

	/**
	 * 属性customer的Getter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @return java.lang.String
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * 属性customer的Setter方法. 创建日期:2010-01-18 09:32:58
	 * 
	 * @param newCustomer
	 *            java.lang.String
	 */
	public void setCustomer(String newCustomer) {
		this.customer = newCustomer;
	}

	public UFDateTime getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(UFDateTime creationtime) {
		this.creationtime = creationtime;
	}

	public UFDateTime getModifiedtime() {
		return modifiedtime;
	}

	public void setModifiedtime(UFDateTime modifiedtime) {
		this.modifiedtime = modifiedtime;
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

	/**
	 * 属性setorg的Setter方法. 创建日期:2010-01-18 09:32:58 @ 实现列表界面集团，业务单元显示
	 */
	public String getSetorg() {
		return setorg;
	}

	public void setSetorg(String setorg) {
		this.setorg = setorg;
	}

	/**
	 * 借款报销多版本字段对照表
	 * 
	 * @return
	 */
	public static Map<String, String> getOrgMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.PK_ORG, JKBXHeaderVO.PK_ORG_V);
		map.put(JKBXHeaderVO.FYDWBM, JKBXHeaderVO.FYDWBM_V);
		map.put(JKBXHeaderVO.DWBM, JKBXHeaderVO.DWBM_V);
		map.put(JKBXHeaderVO.PK_PCORG, JKBXHeaderVO.PK_PCORG_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.PK_PAYORG, JKBXHeaderVO.PK_PAYORG_V);// 支付单位
		return map;
	}

	public static String getDeptFieldByVField(String vField) {
		Map<String, String> map = getDeptMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getDeptVFieldByField(String field) {
		return getDeptMultiVersionFieldMap().get(field);
	}

	public static String getOrgFieldByVField(String vField) {
		Map<String, String> map = getOrgMultiVersionFieldMap();
		Set<Entry<String, String>> entrySet = map.entrySet();
		for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<String, String> entry = (Entry<String, String>) iterator.next();
			if (vField.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static String getOrgVFieldByField(String field) {
		return getOrgMultiVersionFieldMap().get(field);
	}

	/**
	 * 借款报销多版本字段数组
	 * 
	 * @return
	 */
	public static String[] getOrgMultiVersionFieldArray() {
		return (String[]) getOrgMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * 借款报销多版本字段列表
	 * 
	 * @return
	 */
	public static List<String> getOrgMultiVersionFieldList() {
		return Arrays.asList(getOrgMultiVersionFieldArray());
	}

	/**
	 * 借款报销多版本字段对照表
	 * 
	 * @return
	 */
	public static Map<String, String> getDeptMultiVersionFieldMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put(JKBXHeaderVO.DEPTID, JKBXHeaderVO.DEPTID_V);
		map.put(JKBXHeaderVO.FYDEPTID, JKBXHeaderVO.FYDEPTID_V);
		return map;
	}

	/**
	 * 借款报销多版本字段数组
	 * 
	 * @return
	 */
	public static String[] getDeptMultiVersionFieldArray() {
		return (String[]) getDeptMultiVersionFieldMap().values().toArray(new String[0]);
	}

	/**
	 * 借款报销多版本字段列表
	 * 
	 * @return
	 */
	public static List<String> getDeptMultiVersionFieldList() {
		return Arrays.asList(getDeptMultiVersionFieldArray());
	}

	@Override
	public String getParentBillType() {
		if (BXConstans.BX_DJDL.equals(this.getDjdl())) {
			return BXConstans.BX_DJLXBM;
		} else if (BXConstans.JK_DJDL.equals(this.getDjdl())) {
			return BXConstans.JK_DJLXBM;
		}
		return null;
	}

	public UFBoolean getIscostshare() {
		if (iscostshare == null) {
			return UFBoolean.FALSE;
		}
		return iscostshare;
	}

	public void setIscostshare(UFBoolean iscostshare) {
		this.iscostshare = iscostshare;
	}

	public UFBoolean getIsexpamt() {
		if (isexpamt == null) {
			return UFBoolean.FALSE;
		}
		return isexpamt;
	}

	public void setIsexpamt(UFBoolean isexpamt) {
		this.isexpamt = isexpamt;
	}
	
	public UFBoolean getIscusupplier() {
		if (iscusupplier == null){
			return UFBoolean.FALSE;
		}
		return iscusupplier;
	}

	public void setIscusupplier(UFBoolean iscusupplier) {
		this.iscusupplier = iscusupplier;
	}

	public String getStart_period() {
		return start_period;
	}

	public void setStart_period(String startPeriod) {
		start_period = startPeriod;
	}

	public java.lang.Integer getTotal_period() {
		return total_period;
	}

	public void setTotal_period(java.lang.Integer totalPeriod) {
		total_period = totalPeriod;
	}

	public String getPk_payorg() {
		return pk_payorg;
	}

	public void setPk_payorg(String pk_payorg) {
		this.pk_payorg = pk_payorg;
	}

	public String getPk_payorg_v() {
		return pk_payorg_v;
	}

	public void setPk_payorg_v(String pk_payorg_v) {
		this.pk_payorg_v = pk_payorg_v;
	}

	public UFBoolean getFlexible_flag() {
		return flexible_flag;
	}

	public void setFlexible_flag(UFBoolean flexible_flag) {
		this.flexible_flag = flexible_flag;
	}
	
	public void setPk_cashaccount(String pkCashaccount) {
		pk_cashaccount = pkCashaccount;
	}

	public String getPk_cashaccount() {
		return pk_cashaccount;
	}

	public void setPk_resacostcenter(String pkResacostcenter) {
		pk_resacostcenter = pkResacostcenter;
	}

	public String getPk_resacostcenter() {
		return pk_resacostcenter;
	}

	public void setSettleHeadVO(SuperVO settleHeadVO) {
		this.settleHeadVO = settleHeadVO;
	}

	public SuperVO getSettleHeadVO() {
		return settleHeadVO;
	}

	/**
	 * 此方法不公开
	 * 
	 * @param shrqShow
	 */
	public void setShrq_show(UFDate shrqShow) {
		shrq_show = shrqShow;
	}

	public UFDate getShrq_show() {
		if(getShrq() != null){
			shrq_show = getShrq().getDate();
		}
		return shrq_show;
	}

	public String getPk_pcorg_v() {
		return pk_pcorg_v;
	}

	public void setPk_pcorg_v(String pkPcorgV) {
		pk_pcorg_v = pkPcorgV;
	}

	public String getDeptid_v() {
		return deptid_v;
	}

	public void setDeptid_v(String deptidV) {
		deptid_v = deptidV;
	}

	public String getFydeptid_v() {
		return fydeptid_v;
	}

	public void setFydeptid_v(String fydeptidV) {
		fydeptid_v = fydeptidV;
	}

	public String getDwbm_v() {
		return dwbm_v;
	}

	public void setDwbm_v(String dwbmV) {
		dwbm_v = dwbmV;
	}

	public String getFydwbm_v() {
		return fydwbm_v;
	}

	public void setFydwbm_v(String fydwbmV) {
		fydwbm_v = fydwbmV;
	}

	public String getPk_busitem() {
		return pk_busitem;
	}

	public void setPk_busitem(String pk_busitem) {
		this.pk_busitem = pk_busitem;
	}

	@Override
	public String getPk() {
		return getPk_jkbx();
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

	public java.lang.String getCenter_dept() {
		return center_dept;
	}

	public void setCenter_dept(java.lang.String center_dept) {
		this.center_dept = center_dept;
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

	public String getPk_mtapp_detail() {
		return pk_mtapp_detail;
	}

	public void setPk_mtapp_detail(String pk_mtapp_detail) {
		this.pk_mtapp_detail = pk_mtapp_detail;
	}

	public UFBoolean getIsmashare() {
		return ismashare;
	}

	public void setIsmashare(UFBoolean ismashare) {
		this.ismashare = ismashare;
	}

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
	
	@Override
	public String getWorkFlowBillPk() {
		return getPk();
	}

	@Override
	public String getWorkFolwBillType() {
		return getDjlxbm();
	}
	
	public void combineVO(JKBXHeaderVO vo){
		if(this.pk_item == null && this.isLoadInitBill == false){
			// 非拉单且不加载常用单据情况，根据单据模板的设置冲掉vo中的默认值
				if(!this.getIscostshare().booleanValue()){
					setIscostshare(vo.getIscostshare());
				}
				if(!this.getIsexpamt().booleanValue()){
					setIsexpamt(vo.getIsexpamt());
				}
				if(!this.getIscusupplier().booleanValue()){
					setIscusupplier(vo.getIscusupplier());
				}
		}
		String[] attributeNames = this.getAttributeNames();
		for (String attribute : attributeNames) {
			// 初始值中未设置的值，根据单据模板默认值设置
			if (this.getAttributeValue(attribute) == null) {
				Object newValue = vo.getAttributeValue(attribute);
				if (newValue != null) {
					setAttributeValue(attribute,newValue);
				}
			}
		}
	}

	public boolean isLoadInitBill() {
		return isLoadInitBill;
	}

	public void setLoadInitBill(boolean isLoadInitBill) {
		this.isLoadInitBill = isLoadInitBill;
	}

	/**
	 * 当前报销单是否是费用调整类型
	 * 
	 * @return
	 */
	public boolean isAdjustBxd() {
		boolean isAdjust = false;
		try {
			isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(getPk_group(), getDjlxbm(),ErmDjlxConst.BXTYPE_ADJUST);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		return isAdjust;
	}

}