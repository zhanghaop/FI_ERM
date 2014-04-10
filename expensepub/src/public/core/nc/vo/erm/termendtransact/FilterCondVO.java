package nc.vo.erm.termendtransact;

/**
 * ��������vo��
 * �������ڣ�(2001-8-21 10:10:27)
 * ����޸����ڣ�(2001-8-21 10:10:27)
 * @author��wyan
 */
 import java.util.Vector;

import nc.vo.pub.lang.UFDouble;
 
public class FilterCondVO extends nc.vo.pub.ValueObject {
	/**
	 */
	private static final long serialVersionUID = 1L;
	private String m_sDwbm;/*��λ����*/
	private String m_sSfbz;/*������־*/
	private UFDouble m_dFbhl;/*���һ���*/
	private UFDouble m_dBbhl;/*���һ���*/
	private String m_sYear;/*������*/
	private String m_sQj;/*������*/
	private String m_sBegDate;/*�����µ���ʼ����*/
	private String m_sEndDate;/*�����µĽ�������*/
	private Vector m_vResultData;/*�������*/
	private	String m_sMode1;/*��ֹ�����µ���ȫ����˼��ģʽ��null-����飬check����鲻���ƣ�control-��鲢����*/
	private	String m_sMode2; /*��ֹ�������տȫ������*/
	private	String m_sMode3; /*��ֹ�����µ���ȫ�����ɻ��ƾ֤*/
	private	String m_sMode4; /*���µ����Ƿ����������*/
	private String m_sPeriodState;/*�����Ƿ��Ѿ�����*/
	private String pk_org; //������֯pk
	
	public String getPk_org() {
			return pk_org;
		}
		public void setPk_org(String pkOrg) {
			pk_org = pkOrg;
		}
	/**
	 * FilterCondVO ������ע�⡣
	 */
	public FilterCondVO() {
		super();
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-10-17 10:51:33)
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getBbhl() {
	    return m_dBbhl;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-20 19:52:45)
	 * ����޸����ڣ�(2001-9-20 19:52:45)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getBegDate() {
		return m_sBegDate;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:14:07)
	 * ����޸����ڣ�(2001-9-21 13:14:07)
	 * @author��wyan
	 * @return java.util.Vector
	 */
	public Vector getData() {
		return m_vResultData;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:21:17)
	 * ����޸����ڣ�(2001-8-21 16:21:17)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getDwbm() {
		return m_sDwbm;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-20 19:52:45)
	 * ����޸����ڣ�(2001-9-20 19:52:45)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getEndDate() {
		return m_sEndDate;
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
	 * �������ڣ�(2001-10-17 10:51:33)
	 * @return nc.vo.pub.lang.UFDouble
	 */
	public UFDouble getFbhl() {
		return m_dFbhl;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:33:42)
	 * ����޸����ڣ�(2001-9-21 13:33:42)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getMode1() {
		return m_sMode1;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:33:42)
	 * ����޸����ڣ�(2001-9-21 13:33:42)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getMode2() {
		return m_sMode2;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:33:42)
	 * ����޸����ڣ�(2001-9-21 13:33:42)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getMode3() {
		return m_sMode3;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:33:42)
	 * ����޸����ڣ�(2001-9-21 13:33:42)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getMode4() {
		return m_sMode4;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:33:42)
	 * ����޸����ڣ�(2001-9-21 13:33:42)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getPeriodState() {
		return m_sPeriodState;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:24:47)
	 * ����޸����ڣ�(2001-8-21 16:24:47)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getQj() {
		return m_sQj;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:23:48)
	 * ����޸����ڣ�(2001-8-21 16:23:48)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getSfbz() {
		return m_sSfbz;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 17:14:49)
	 * ����޸����ڣ�(2001-8-21 17:14:49)
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getYear() {
		return m_sYear;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-10-17 10:50:46)
	 * @param fbhl nc.vo.pub.lang.UFDouble
	 */
	public void setBbhl(UFDouble bbhl) {
		m_dBbhl = bbhl;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-20 19:51:49)
	 * ����޸����ڣ�(2001-9-20 19:51:49)
	 * @author��wyan
	 * @param begDate java.lang.String
	 */
	public void setBegDate(String begDate) {
	    m_sBegDate = begDate;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:12:41)
	 * ����޸����ڣ�(2001-9-21 13:12:41)
	 * @author��wyan
	 * @param vData java.util.Vector
	 */
	public void setData(Vector vData) {
		m_vResultData = vData;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:20:55)
	 * ����޸����ڣ�(2001-8-21 16:20:55)
	 * @author��wyan
	 * @param dwbm java.lang.String
	 */
	public void setDwbm(String dwbm) {
	    m_sDwbm = dwbm;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-20 19:51:49)
	 * ����޸����ڣ�(2001-9-20 19:51:49)
	 * @author��wyan
	 * @param begDate java.lang.String
	 */
	public void setEndDate(String endDate) {
		m_sEndDate = endDate;
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(2001-10-17 10:50:46)
	 * @param fbhl nc.vo.pub.lang.UFDouble
	 */
	public void setFbhl(UFDouble fbhl) {
	    m_dFbhl = fbhl;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:32:49)
	 * ����޸����ڣ�(2001-9-21 13:32:49)
	 * @author��wyan
	 * @param mode java.lang.String
	 */
	public void setMode1(String mode) {
	    m_sMode1 = mode;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:32:49)
	 * ����޸����ڣ�(2001-9-21 13:32:49)
	 * @author��wyan
	 * @param mode java.lang.String
	 */
	public void setMode2(String mode) {
		m_sMode2 = mode;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:32:49)
	 * ����޸����ڣ�(2001-9-21 13:32:49)
	 * @author��wyan
	 * @param mode java.lang.String
	 */
	public void setMode3(String mode) {
		m_sMode3 = mode;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:32:49)
	 * ����޸����ڣ�(2001-9-21 13:32:49)
	 * @author��wyan
	 * @param mode java.lang.String
	 */
	public void setMode4(String mode) {
		m_sMode4 = mode;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-9-21 13:32:49)
	 * ����޸����ڣ�(2001-9-21 13:32:49)
	 * @author��wyan
	 * @param mode java.lang.String
	 */
	public void setPeriodSate(String state) {
		m_sPeriodState = state;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:24:22)
	 * ����޸����ڣ�(2001-8-21 16:24:22)
	 * @author��wyan
	 * @param qj java.lang.String
	 */
	public void setQj(String qj) {
	    m_sQj = qj;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 16:23:10)
	 * ����޸����ڣ�(2001-8-21 16:23:10)
	 * @author��wyan
	 * @param sfbz java.lang.String
	 */
	public void setSfbz(String sfbz) {
	    m_sSfbz = sfbz;
	}
	/**
	 * ��Ҫ���ܣ�
	 * ��Ҫ�㷨��
	 * �쳣������
	 * �������ڣ�(2001-8-21 17:14:33)
	 * ����޸����ڣ�(2001-8-21 17:14:33)
	 * @author��wyan
	 * @param year java.lang.String
	 */
	public void setYear(String year) {
	    m_sYear = year;
	}
	/**
	 * ��֤���������֮��������߼���ȷ�ԡ�
	 * 
	 * �������ڣ�(2001-2-15 11:47:35)
	 * @exception nc.vo.pub.ValidationException �����֤ʧ�ܣ��׳�
	 *     ValidationException���Դ�����н��͡�
	 */
	public void validate() throws nc.vo.pub.ValidationException {}
}
