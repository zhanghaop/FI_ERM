package nc.vo.erm.termendtransact;

/**
 * 过滤条件vo。
 * 创建日期：(2001-8-21 10:10:27)
 * 最后修改日期：(2001-8-21 10:10:27)
 * @author：wyan
 */
 import java.util.Vector;

import nc.vo.pub.lang.UFDouble;
 
public class FilterCondVO extends nc.vo.pub.ValueObject {
	/**
	 */
	private static final long serialVersionUID = 1L;
	private String m_sDwbm;/*单位编码*/
	private String m_sSfbz;/*収付标志*/
	private UFDouble m_dFbhl;/*辅币汇率*/
	private UFDouble m_dBbhl;/*本币汇率*/
	private String m_sYear;/*结账年*/
	private String m_sQj;/*结账月*/
	private String m_sBegDate;/*结账月的起始日期*/
	private String m_sEndDate;/*结账月的结束日期*/
	private Vector m_vResultData;/*检查数据*/
	private	String m_sMode1;/*截止到本月单据全部审核检查模式：null-不检查，check－检查不控制，control-检查并控制*/
	private	String m_sMode2; /*截止到本月收款单全部核销*/
	private	String m_sMode3; /*截止到本月单据全部生成会计凭证*/
	private	String m_sMode4; /*本月单据是否计算汇兑损益*/
	private String m_sPeriodState;/*本月是否已经结账*/
	private String pk_org; //财务组织pk
	
	public String getPk_org() {
			return pk_org;
		}
		public void setPk_org(String pkOrg) {
			pk_org = pkOrg;
		}
	/**
	 * FilterCondVO 构造子注解。
	 */
	public FilterCondVO() {
		super();
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-10-17 10:51:33)
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getBbhl() {
	    return m_dBbhl;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-20 19:52:45)
	 * 最后修改日期：(2001-9-20 19:52:45)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getBegDate() {
		return m_sBegDate;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:14:07)
	 * 最后修改日期：(2001-9-21 13:14:07)
	 * @author：wyan
	 * @return java.util.Vector
	 */
	public Vector getData() {
		return m_vResultData;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:21:17)
	 * 最后修改日期：(2001-8-21 16:21:17)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getDwbm() {
		return m_sDwbm;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-20 19:52:45)
	 * 最后修改日期：(2001-9-20 19:52:45)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getEndDate() {
		return m_sEndDate;
	}
	/**
	 * 返回数值对象的显示名称。
	 * 
	 * 创建日期：(2001-2-15 14:18:08)
	 * @return java.lang.String 返回数值对象的显示名称。
	 */
	public String getEntityName() {
		return null;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-10-17 10:51:33)
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getFbhl() {
		return m_dFbhl;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:33:42)
	 * 最后修改日期：(2001-9-21 13:33:42)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getMode1() {
		return m_sMode1;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:33:42)
	 * 最后修改日期：(2001-9-21 13:33:42)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getMode2() {
		return m_sMode2;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:33:42)
	 * 最后修改日期：(2001-9-21 13:33:42)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getMode3() {
		return m_sMode3;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:33:42)
	 * 最后修改日期：(2001-9-21 13:33:42)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getMode4() {
		return m_sMode4;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:33:42)
	 * 最后修改日期：(2001-9-21 13:33:42)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getPeriodState() {
		return m_sPeriodState;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:24:47)
	 * 最后修改日期：(2001-8-21 16:24:47)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getQj() {
		return m_sQj;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:23:48)
	 * 最后修改日期：(2001-8-21 16:23:48)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getSfbz() {
		return m_sSfbz;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 17:14:49)
	 * 最后修改日期：(2001-8-21 17:14:49)
	 * @author：wyan
	 * @return java.lang.String
	 */
	public String getYear() {
		return m_sYear;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-10-17 10:50:46)
	 * @param fbhl nc.vo.pub.lang.UFDouble
	 */
	public void setBbhl(UFDouble bbhl) {
		m_dBbhl = bbhl;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-20 19:51:49)
	 * 最后修改日期：(2001-9-20 19:51:49)
	 * @author：wyan
	 * @param begDate java.lang.String
	 */
	public void setBegDate(String begDate) {
	    m_sBegDate = begDate;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:12:41)
	 * 最后修改日期：(2001-9-21 13:12:41)
	 * @author：wyan
	 * @param vData java.util.Vector
	 */
	public void setData(Vector vData) {
		m_vResultData = vData;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:20:55)
	 * 最后修改日期：(2001-8-21 16:20:55)
	 * @author：wyan
	 * @param dwbm java.lang.String
	 */
	public void setDwbm(String dwbm) {
	    m_sDwbm = dwbm;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-20 19:51:49)
	 * 最后修改日期：(2001-9-20 19:51:49)
	 * @author：wyan
	 * @param begDate java.lang.String
	 */
	public void setEndDate(String endDate) {
		m_sEndDate = endDate;
	}
	/**
	 * 此处插入方法说明。
	 * 创建日期：(2001-10-17 10:50:46)
	 * @param fbhl nc.vo.pub.lang.UFDouble
	 */
	public void setFbhl(UFDouble fbhl) {
	    m_dFbhl = fbhl;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:32:49)
	 * 最后修改日期：(2001-9-21 13:32:49)
	 * @author：wyan
	 * @param mode java.lang.String
	 */
	public void setMode1(String mode) {
	    m_sMode1 = mode;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:32:49)
	 * 最后修改日期：(2001-9-21 13:32:49)
	 * @author：wyan
	 * @param mode java.lang.String
	 */
	public void setMode2(String mode) {
		m_sMode2 = mode;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:32:49)
	 * 最后修改日期：(2001-9-21 13:32:49)
	 * @author：wyan
	 * @param mode java.lang.String
	 */
	public void setMode3(String mode) {
		m_sMode3 = mode;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:32:49)
	 * 最后修改日期：(2001-9-21 13:32:49)
	 * @author：wyan
	 * @param mode java.lang.String
	 */
	public void setMode4(String mode) {
		m_sMode4 = mode;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-9-21 13:32:49)
	 * 最后修改日期：(2001-9-21 13:32:49)
	 * @author：wyan
	 * @param mode java.lang.String
	 */
	public void setPeriodSate(String state) {
		m_sPeriodState = state;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:24:22)
	 * 最后修改日期：(2001-8-21 16:24:22)
	 * @author：wyan
	 * @param qj java.lang.String
	 */
	public void setQj(String qj) {
	    m_sQj = qj;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 16:23:10)
	 * 最后修改日期：(2001-8-21 16:23:10)
	 * @author：wyan
	 * @param sfbz java.lang.String
	 */
	public void setSfbz(String sfbz) {
	    m_sSfbz = sfbz;
	}
	/**
	 * 主要功能：
	 * 主要算法：
	 * 异常描述：
	 * 创建日期：(2001-8-21 17:14:33)
	 * 最后修改日期：(2001-8-21 17:14:33)
	 * @author：wyan
	 * @param year java.lang.String
	 */
	public void setYear(String year) {
	    m_sYear = year;
	}
	/**
	 * 验证对象各属性之间的数据逻辑正确性。
	 * 
	 * 创建日期：(2001-2-15 11:47:35)
	 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
	 *     ValidationException，对错误进行解释。
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
