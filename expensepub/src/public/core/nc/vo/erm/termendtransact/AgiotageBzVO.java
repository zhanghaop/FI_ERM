package nc.vo.erm.termendtransact;

/**
 * 损益币种VO。
 * 创建日期：(2001-8-10 17:21:55)
 * 最后修改日期：(2001-8-10 17:21:55)
 * @author：wyan
 */
 import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.builder.HashCodeBuilder;
 
public class AgiotageBzVO extends nc.vo.pub.ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6450301131572579174L;
	private String m_sBzbm;/*币种编码*/
	private String m_sBzmc;/*币种名称*/
	private int m_iYbDig;/*原币小数位数*/
	private int m_iBbDig;/*本币小数位数*/
	private String m_sDjlxmc;/*单据类型名称*/
	private String m_sDjbh;/*单据编号*/
	private UFDouble m_dBbhl;/*对本币汇率*/
	private String m_sMsg;/*币种计算汇兑损益结果*/
	private String m_sCurrErrMsg;/*币种汇率非法信息*/
	private boolean m_bState;/*币种损益检查是否通过(没有汇率非法和损益为零等情况)*/
	private UFDate lastCalDate;
	
	private String dwbm;/*单位编码*/
	private String caldate;/*损益日期*/
	private String locaCurr;/*本币币种*/
	
	private int groupDig;/*集团本币精度*/
	private int globalDig;/*全局本币精度*/

	private int groupRateDig;/*集团汇率精度*/
	private int globalRateDig;/*全局汇率精度*/
	
	
    public int getGroupRateDig() {
		return groupRateDig;
	}

	public void setGroupRateDig(int groupRateDig) {
		this.groupRateDig = groupRateDig;
	}

	public int getGlobalRateDig() {
		return globalRateDig;
	}

	public void setGlobalRateDig(int globalRateDig) {
		this.globalRateDig = globalRateDig;
	}

	public String getLocaCurr() {
		return locaCurr;
	}

	public int getGroupDig() {
		return groupDig;
	}

	public void setGroupDig(int groupDig) {
		this.groupDig = groupDig;
	}

	public int getGlobalDig() {
		return globalDig;
	}

	public void setGlobalDig(int globalDig) {
		this.globalDig = globalDig;
	}

	public void setLocaCurr(String locaCurr) {
		this.locaCurr = locaCurr;
	}

	public String getDwbm() {
		return dwbm;
	}

	public void setDwbm(String dwbm) {
		this.dwbm = dwbm;
	}

	public String getCaldate() {
		return caldate;
	}

	public void setCaldate(String caldate) {
		this.caldate = caldate;
	}

	public UFDate getLastCalDate() {
		return lastCalDate;
	}

	public void setLastCalDate(UFDate lastCalDate) {
		this.lastCalDate = lastCalDate;
	}

/**
 * AgiotageBz 构造子注解。
 */
public AgiotageBzVO() {
	super();
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-7 16:14:29)
 * @return boolean
 */

/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-2 19:21:43)
 * @return int
 */
public int getBbDig() {
	return m_iBbDig;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:27:35)
 * 最后修改日期：(2001-8-10 17:27:35)
 * @author：wyan
 * @return nc.vo.pub.lang.UFDouble
 */
public UFDouble getBbhl() {
	return m_dBbhl;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:26:49)
 * 最后修改日期：(2001-8-10 17:26:49)
 * @author：wyan
 * @return java.lang.String
 */
public String getBzbm() {
	return m_sBzbm;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:27:03)
 * 最后修改日期：(2001-8-10 17:27:03)
 * @author：wyan
 * @return java.lang.String
 */
public String getBzmc() {
	return m_sBzmc;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-25 15:51:25)
 * @return java.lang.String
 */
public String getCurrErrMsg() {
	return m_sCurrErrMsg;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-17 15:04:46)
 * @return java.lang.String
 */
public String getDjbh() {
	return m_sDjbh;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-17 15:05:29)
 * @return java.lang.String
 */
public String getDjlxmc() {
	return m_sDjlxmc;
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
 * 创建日期：(2001-11-7 16:14:50)
 * @return boolean
 */

/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:25:52)
 * @return java.lang.String
 */
public String getMsg() {
	return m_sMsg;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 17:10:34)
 * @return boolean
 */
public boolean getState() {
	return m_bState;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-2 19:22:05)
 * @return int
 */
public int getYbDig() {
	return m_iYbDig;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-7 16:15:54)
 * @param ismulti boolean
 */

/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-2 19:21:00)
 * @param bbdig int
 */
public void setBbDig(int bbdig) {
    m_iBbDig = bbdig;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:26:30)
 * 最后修改日期：(2001-8-10 17:26:30)
 * @author：wyan
 * @param bbhl nc.vo.pub.lang.UFDouble
 */
public void setBbhl(UFDouble bbhl) {
	m_dBbhl = bbhl;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:25:31)
 * 最后修改日期：(2001-8-10 17:25:31)
 * @author：wyan
 * @param bzbm java.lang.String
 */
public void setBzbm(String bzbm) {
	m_sBzbm = bzbm;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 17:25:48)
 * 最后修改日期：(2001-8-10 17:25:48)
 * @author：wyan
 * @param bzmc java.lang.String
 */
public void setBzmc(String bzmc) {
	m_sBzmc = bzmc;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-25 15:50:52)
 * @param errMsg java.lang.String
 */
public void setCurrErrMsg(String errMsg) {
	m_sCurrErrMsg = errMsg;

}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-17 15:05:05)
 * @param djbh java.lang.String
 */
public void setDjbh(String djbh) {
	m_sDjbh = djbh;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-12-17 15:05:51)
 * @param djlxmc java.lang.String
 */
public void setDjlxmc(String djlxmc) {
	m_sDjlxmc = djlxmc;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-7 16:15:18)
 * @param ismulti boolean
 */
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 13:26:12)
 * @param msg java.lang.String
 */
public void setMsg(String msg) {
	m_sMsg = msg;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-10-16 17:10:07)
 * @param state boolean
 */
public void setState(boolean state) {
	m_bState = state;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-2 19:20:12)
 * @param ybdig int
 */
public void setYbDig(int ybdig) {
    m_iYbDig = ybdig;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 * 
 * 创建日期：(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws nc.vo.pub.ValidationException {}

@Override
public boolean equals(Object obj) {
	if (obj instanceof AgiotageBzVO) {
		AgiotageBzVO vo = (AgiotageBzVO) obj;
		
		if(vo.getBzbm().equals(this.getBzbm()))
			return true;
		else
			return false;
		
	}
	return super.equals(obj);
}

@Override
public int hashCode() {
	 return (new HashCodeBuilder()).append(getBzbm()).toHashCode();
}
}
