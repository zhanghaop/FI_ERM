package nc.vo.erm.termendtransact;

import java.util.List;

/**
 * 系统信息VO:例如收付标志等。 创建日期：(2001-5-24 13:31:30)
 * 
 * @author：wyan
 */

public class SystemInfoVO extends nc.vo.pub.ValueObject {

	private static final long serialVersionUID = 1738475165565406459L;
	private String m_Sfbz; /* 收付标志 */
	private String m_sProdID;/* 模块名称 */
	private String m_CurSystem; /* 当前系统自动还是手工核销 */
	private String m_CurDwbm; /* 当前单位编码 */
	private String m_CurNd; /* 当前年度 */
	private String m_CurQj; /* 当前期间 */
	private String m_CurRq; /* 当前日期 */
	private String m_CurUser; /* 当前用户 */
	private boolean m_HxMode; /* 核销方式:按单据true，按产品false */
	private int m_AgiotageMode; /*
								 * 损益方式：外币结清false，月末true =>变更为
								 * 损益方式：外币结清0，月末1,已实现2
								 */
	private boolean m_HsMode;/* 是否主辅币核算 */
	private boolean m_HxSeq; /* 核销顺序：最早余额true，最近余额false */
	private boolean m_ZkShow; /* 现金折扣是否显示：是true，否false */
	private String m_checkMode1; /* 月末处理结账检查步骤1模式 */
	private String m_checkMode2; /* 月末处理结账检查步骤2模式 */
	private String m_checkMode3; /* 月末处理结账检查步骤3模式 */
	private String m_checkMode4; /* 月末处理结账检查步骤4模式 */
	private String m_checkMode5;/* 本月汇兑损益是否计算 */
	// private Currency m_Curr;/**/
	private boolean m_IsMultiV;
	private List<String> bzbm; // 币种编码
	private String clrq; // 操作日期
	private boolean iszgagiotage; // 暂估单据是否计算汇兑损益
	private String pk_org;

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}

	public boolean getIszgagiotage() {
		return iszgagiotage;
	}

	public void setIszgagiotage(boolean iszgagiotage) {
		this.iszgagiotage = iszgagiotage;
	}

	public List<String> getBzbm() {
		return bzbm;
	}

	public void setBzbm(List<String> bzbm) {
		this.bzbm = bzbm;
	}

	/**
	 * SystemInfo 构造子注解。
	 */
	public SystemInfoVO() {
		super();
	}

	/**
	 * 主要功能：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:22:31) 最后修改日期：(2001-9-20 16:22:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCheckMode1() {
		return m_checkMode1;
	}

	/**
	 * 主要功能：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:22:31) 最后修改日期：(2001-9-20 16:22:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCheckMode2() {
		return m_checkMode2;
	}

	/**
	 * 主要功能：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:22:31) 最后修改日期：(2001-9-20 16:22:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCheckMode3() {
		return m_checkMode3;
	}

	/**
	 * 主要功能：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:22:31) 最后修改日期：(2001-9-20 16:22:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCheckMode4() {
		return m_checkMode4;
	}

	/**
	 * 主要功能：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:22:31) 最后修改日期：(2001-9-20 16:22:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCheckMode5() {
		return m_checkMode5;
	}

	/**
	 * 单位编码。 创建日期：(2001-5-24 13:57:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCurDwbm() {
		return m_CurDwbm;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 19:00:01)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCurNd() {
		return m_CurNd;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 19:00:28)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCurQj() {
		return m_CurQj;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 19:00:55)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCurRq() {
		return m_CurRq;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 19:00:41)
	 * 
	 * @author：wyan
	 */
	public String getCurSystem() {
		return m_CurSystem;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 19:01:43)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getCurUser() {
		return m_CurUser;
	}

	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2001-2-15 14:18:08)
	 * 
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public String getEntityName() {
		return null;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-6 16:44:24)
	 * 
	 * @return boolean
	 */
	public boolean getHsMode() {
		return m_HsMode;
	}

	/**
	 * 返回是：按单据核销。 否：按产品核销 创建日期：(2001-6-4 18:59:38)
	 * 
	 * @author：wyan
	 */
	public boolean getIsDocument() {

		return m_HxMode;
	}

	/**
	 * 返回是：月末计算汇兑损益 否：外币结清时计算汇兑损益 异常描述： 创建日期：(2001-9-14 14:24:37)
	 * 最后修改日期：(2001-9-14 14:24:37)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public int getModeType() {
		return m_AgiotageMode;
	}

	/**
	 * 返回是：按最早余额法核销。 否：按最近余额法核销 异常描述： 创建日期：(2001-9-14 14:26:01)
	 * 最后修改日期：(2001-9-14 14:26:01)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public boolean getIsMostEarly() {
		return m_HxSeq;
	}

	/**
	 * 返回是：按单据核销。 否：按产品核销 创建日期：(2001-6-4 18:59:38)
	 * 
	 * @author：wyan
	 */
	public boolean getIsMultiV() {

		return m_IsMultiV;
	}

	/**
	 * 返回是：显示折扣 否：不显示折扣 异常描述： 创建日期：(2001-9-14 14:27:05) 最后修改日期：(2001-9-14
	 * 14:27:05)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public boolean getIsZkShow() {
		return m_ZkShow;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-16 16:52:14)
	 * 
	 * @return java.lang.String
	 */
	public String getProdID() {
		return m_sProdID;
	}

	/**
	 * 收付标志。 创建日期：(2001-5-24 13:57:31)
	 * 
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getSfbz() {
		return m_Sfbz;
	}

	/**
	 * 主要功能：是：月末计算汇兑损益 否：外币结清时计算汇兑损益 主要算法： 异常描述： 创建日期：(2001-9-14 14:25:16)
	 * 最后修改日期：(2001-9-14 14:25:16)
	 * 
	 * @author：wyan
	 * @param mode
	 *            java.lang.String
	 */
	public void setAgiotageMode(int mode) {
		m_AgiotageMode = mode;
	}

	/**
	 * 主要功能：：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:19:46) 最后修改日期：(2001-9-20 16:19:46)
	 * 
	 * @author：wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode1(String mode1) {
		m_checkMode1 = mode1;
	}

	/**
	 * 主要功能：：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:19:46) 最后修改日期：(2001-9-20 16:19:46)
	 * 
	 * @author：wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode2(String mode2) {
		m_checkMode2 = mode2;
	}

	/**
	 * 主要功能：：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:19:46) 最后修改日期：(2001-9-20 16:19:46)
	 * 
	 * @author：wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode3(String mode3) {
		m_checkMode3 = mode3;
	}

	/**
	 * 主要功能：：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:19:46) 最后修改日期：(2001-9-20 16:19:46)
	 * 
	 * @author：wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode4(String mode4) {
		m_checkMode4 = mode4;
	}

	/**
	 * 主要功能：：不检查(null)，检查但不控制(check)，检查并且控制(control) 主要算法： 异常描述： 创建日期：(2001-9-20
	 * 16:19:46) 最后修改日期：(2001-9-20 16:19:46)
	 * 
	 * @author：wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode5(String mode5) {
		m_checkMode5 = mode5;
	}

	/**
	 * 单位编码。 创建日期：(2001-5-24 13:57:59)
	 * 
	 * @author：wyan
	 * @param sfbz
	 *            java.lang.String
	 */
	public void setCurDwbm(String dwbm) {
		m_CurDwbm = dwbm;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-4 18:59:38)
	 * 
	 * @author：wyan
	 */
	public void setCurNd(String curnd) {
		m_CurNd = curnd;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-22 15:38:42)
	 * 
	 * @author：wyan
	 * @param curqj
	 *            java.lang.String
	 */
	public void setCurQj(String curqj) {
		m_CurQj = curqj;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-22 15:39:47)
	 * 
	 * @author：wyan
	 * @param currq
	 *            java.lang.String
	 */
	public void setCurRq(String currq) {
		m_CurRq = currq;
	}

	/**
	 * 主要功能： 主要算法： 异常描述： 创建日期：(2001-9-3 22:05:53) 最后修改日期：(2001-9-3 22:05:53)
	 * 
	 * @author：wyan
	 * @param system
	 *            java.lang.String
	 */
	public void setCurSystem(String system) {
		m_CurSystem = system;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-6-22 15:39:06)
	 * 
	 * @author：wyan
	 * @param curuser
	 *            java.lang.String
	 */
	public void setCurUser(String curuser) {
		m_CurUser = curuser;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-6 16:43:57)
	 * 
	 * @param mode
	 *            boolean
	 */
	public void setHsMode(boolean mode) {
		m_HsMode = mode;
	}

	/**
	 * 主要功能：是：按单据核销 否：按产品核销 主要算法： 异常描述： 创建日期：(2001-9-3 22:05:05)
	 * 最后修改日期：(2001-9-3 22:05:05)
	 * 
	 * @author：wyan
	 * @param mode
	 *            java.lang.String
	 */
	public void setHxMode(boolean mode) {
		m_HxMode = mode;
	}

	/**
	 * 主要功能： 是：按最早余额法核销 否：按最近余额法核销 主要算法： 异常描述： 创建日期：(2001-9-14 14:26:34)
	 * 最后修改日期：(2001-9-14 14:26:34)
	 * 
	 * @author：wyan
	 * @param seq
	 *            java.lang.String
	 */
	public void setHxSeq(boolean seq) {
		m_HxSeq = seq;
	}

	/**
	 * 此处插入方法描述。 创建日期：(2003-11-5 15:56:51)
	 * 
	 * @param m_IsMultiV
	 *            boolean
	 */
	public void setIsMultiV(boolean newIsMultiV) {
		m_IsMultiV = newIsMultiV;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-11-16 16:51:35)
	 * 
	 * @param id
	 *            java.lang.String
	 */
	public void setProdID(String id) {
		m_sProdID = id;
	}

	/**
	 * 收付标志。 创建日期：(2001-5-24 13:57:59)
	 * 
	 * @author：wyan
	 * @param sfbz
	 *            java.lang.String
	 */
	public void setSfbz(String sfbz) {
		m_Sfbz = sfbz;
	}

	/**
	 * 主要功能：是：显示折扣 否：不显示折扣 主要算法： 异常描述： 创建日期：(2001-9-14 14:27:31)
	 * 最后修改日期：(2001-9-14 14:27:31)
	 * 
	 * @author：wyan
	 * @param show
	 *            java.lang.String
	 */
	public void setZkShow(boolean show) {
		m_ZkShow = show;
	}

	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * 创建日期：(2001-2-15 11:47:35)
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                如果验证失败，抛出 ValidationException，对错误进行解释。
	 */
	public void validate() throws nc.vo.pub.ValidationException {
	}

	public String getClrq() {
		return clrq;
	}

	public void setClrq(String clrq) {
		this.clrq = clrq;
	}
}
