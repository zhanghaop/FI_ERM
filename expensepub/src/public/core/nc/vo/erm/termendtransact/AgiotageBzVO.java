package nc.vo.erm.termendtransact;

/**
 * �������VO��
 * �������ڣ�(2001-8-10 17:21:55)
 * ����޸����ڣ�(2001-8-10 17:21:55)
 * @author��wyan
 */
 import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

import org.apache.commons.lang.builder.HashCodeBuilder;
 
public class AgiotageBzVO extends nc.vo.pub.ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6450301131572579174L;
	private String m_sBzbm;/*���ֱ���*/
	private String m_sBzmc;/*��������*/
	private int m_iYbDig;/*ԭ��С��λ��*/
	private int m_iBbDig;/*����С��λ��*/
	private String m_sDjlxmc;/*������������*/
	private String m_sDjbh;/*���ݱ��*/
	private UFDouble m_dBbhl;/*�Ա��һ���*/
	private String m_sMsg;/*���ּ�����������*/
	private String m_sCurrErrMsg;/*���ֻ��ʷǷ���Ϣ*/
	private boolean m_bState;/*�����������Ƿ�ͨ��(û�л��ʷǷ�������Ϊ������)*/
	private UFDate lastCalDate;
	
	private String dwbm;/*��λ����*/
	private String caldate;/*��������*/
	private String locaCurr;/*���ұ���*/
	
	private int groupDig;/*���ű��Ҿ���*/
	private int globalDig;/*ȫ�ֱ��Ҿ���*/

	private int groupRateDig;/*���Ż��ʾ���*/
	private int globalRateDig;/*ȫ�ֻ��ʾ���*/
	
	
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
 * AgiotageBz ������ע�⡣
 */
public AgiotageBzVO() {
	super();
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-7 16:14:29)
 * @return boolean
 */

/**
 * �˴����뷽��˵����
 * �������ڣ�(2002-7-2 19:21:43)
 * @return int
 */
public int getBbDig() {
	return m_iBbDig;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:27:35)
 * ����޸����ڣ�(2001-8-10 17:27:35)
 * @author��wyan
 * @return nc.vo.pub.lang.UFDouble
 */
public UFDouble getBbhl() {
	return m_dBbhl;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:26:49)
 * ����޸����ڣ�(2001-8-10 17:26:49)
 * @author��wyan
 * @return java.lang.String
 */
public String getBzbm() {
	return m_sBzbm;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:27:03)
 * ����޸����ڣ�(2001-8-10 17:27:03)
 * @author��wyan
 * @return java.lang.String
 */
public String getBzmc() {
	return m_sBzmc;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-25 15:51:25)
 * @return java.lang.String
 */
public String getCurrErrMsg() {
	return m_sCurrErrMsg;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-17 15:04:46)
 * @return java.lang.String
 */
public String getDjbh() {
	return m_sDjbh;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-17 15:05:29)
 * @return java.lang.String
 */
public String getDjlxmc() {
	return m_sDjlxmc;
}
/**
 * ������ֵ�������ʾ���ơ�
 * 
 * �������ڣ�(2001-2-15 14:18:08)
 * @return java.lang.String ������ֵ�������ʾ���ơ�
 */
public String getEntityName() {
	return null;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-7 16:14:50)
 * @return boolean
 */

/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-16 13:25:52)
 * @return java.lang.String
 */
public String getMsg() {
	return m_sMsg;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-16 17:10:34)
 * @return boolean
 */
public boolean getState() {
	return m_bState;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2002-7-2 19:22:05)
 * @return int
 */
public int getYbDig() {
	return m_iYbDig;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-7 16:15:54)
 * @param ismulti boolean
 */

/**
 * �˴����뷽��˵����
 * �������ڣ�(2002-7-2 19:21:00)
 * @param bbdig int
 */
public void setBbDig(int bbdig) {
    m_iBbDig = bbdig;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:26:30)
 * ����޸����ڣ�(2001-8-10 17:26:30)
 * @author��wyan
 * @param bbhl nc.vo.pub.lang.UFDouble
 */
public void setBbhl(UFDouble bbhl) {
	m_dBbhl = bbhl;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:25:31)
 * ����޸����ڣ�(2001-8-10 17:25:31)
 * @author��wyan
 * @param bzbm java.lang.String
 */
public void setBzbm(String bzbm) {
	m_sBzbm = bzbm;
}
/**
 * ��Ҫ���ܣ�
 * ��Ҫ�㷨��
 * �쳣������
 * �������ڣ�(2001-8-10 17:25:48)
 * ����޸����ڣ�(2001-8-10 17:25:48)
 * @author��wyan
 * @param bzmc java.lang.String
 */
public void setBzmc(String bzmc) {
	m_sBzmc = bzmc;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-25 15:50:52)
 * @param errMsg java.lang.String
 */
public void setCurrErrMsg(String errMsg) {
	m_sCurrErrMsg = errMsg;

}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-17 15:05:05)
 * @param djbh java.lang.String
 */
public void setDjbh(String djbh) {
	m_sDjbh = djbh;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-12-17 15:05:51)
 * @param djlxmc java.lang.String
 */
public void setDjlxmc(String djlxmc) {
	m_sDjlxmc = djlxmc;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-11-7 16:15:18)
 * @param ismulti boolean
 */
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-16 13:26:12)
 * @param msg java.lang.String
 */
public void setMsg(String msg) {
	m_sMsg = msg;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2001-10-16 17:10:07)
 * @param state boolean
 */
public void setState(boolean state) {
	m_bState = state;
}
/**
 * �˴����뷽��˵����
 * �������ڣ�(2002-7-2 19:20:12)
 * @param ybdig int
 */
public void setYbDig(int ybdig) {
    m_iYbDig = ybdig;
}
/**
 * ��֤���������֮��������߼���ȷ�ԡ�
 * 
 * �������ڣ�(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
 *     ValidationException���Դ�����н��͡�
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
