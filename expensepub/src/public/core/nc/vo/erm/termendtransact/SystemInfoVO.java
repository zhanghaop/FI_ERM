package nc.vo.erm.termendtransact;

import java.util.List;

/**
 * ϵͳ��ϢVO:�����ո���־�ȡ� �������ڣ�(2001-5-24 13:31:30)
 * 
 * @author��wyan
 */

public class SystemInfoVO extends nc.vo.pub.ValueObject {

	private static final long serialVersionUID = 1738475165565406459L;
	private String m_Sfbz; /* �ո���־ */
	private String m_sProdID;/* ģ������ */
	private String m_CurSystem; /* ��ǰϵͳ�Զ������ֹ����� */
	private String m_CurDwbm; /* ��ǰ��λ���� */
	private String m_CurNd; /* ��ǰ��� */
	private String m_CurQj; /* ��ǰ�ڼ� */
	private String m_CurRq; /* ��ǰ���� */
	private String m_CurUser; /* ��ǰ�û� */
	private boolean m_HxMode; /* ������ʽ:������true������Ʒfalse */
	private int m_AgiotageMode; /*
								 * ���淽ʽ����ҽ���false����ĩtrue =>���Ϊ
								 * ���淽ʽ����ҽ���0����ĩ1,��ʵ��2
								 */
	private boolean m_HsMode;/* �Ƿ������Һ��� */
	private boolean m_HxSeq; /* ����˳���������true��������false */
	private boolean m_ZkShow; /* �ֽ��ۿ��Ƿ���ʾ����true����false */
	private String m_checkMode1; /* ��ĩ������˼�鲽��1ģʽ */
	private String m_checkMode2; /* ��ĩ������˼�鲽��2ģʽ */
	private String m_checkMode3; /* ��ĩ������˼�鲽��3ģʽ */
	private String m_checkMode4; /* ��ĩ������˼�鲽��4ģʽ */
	private String m_checkMode5;/* ���»�������Ƿ���� */
	// private Currency m_Curr;/**/
	private boolean m_IsMultiV;
	private List<String> bzbm; // ���ֱ���
	private String clrq; // ��������
	private boolean iszgagiotage; // �ݹ������Ƿ����������
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
	 * SystemInfo ������ע�⡣
	 */
	public SystemInfoVO() {
		super();
	}

	/**
	 * ��Ҫ���ܣ������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:22:31) ����޸����ڣ�(2001-9-20 16:22:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCheckMode1() {
		return m_checkMode1;
	}

	/**
	 * ��Ҫ���ܣ������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:22:31) ����޸����ڣ�(2001-9-20 16:22:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCheckMode2() {
		return m_checkMode2;
	}

	/**
	 * ��Ҫ���ܣ������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:22:31) ����޸����ڣ�(2001-9-20 16:22:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCheckMode3() {
		return m_checkMode3;
	}

	/**
	 * ��Ҫ���ܣ������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:22:31) ����޸����ڣ�(2001-9-20 16:22:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCheckMode4() {
		return m_checkMode4;
	}

	/**
	 * ��Ҫ���ܣ������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:22:31) ����޸����ڣ�(2001-9-20 16:22:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCheckMode5() {
		return m_checkMode5;
	}

	/**
	 * ��λ���롣 �������ڣ�(2001-5-24 13:57:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCurDwbm() {
		return m_CurDwbm;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 19:00:01)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCurNd() {
		return m_CurNd;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 19:00:28)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCurQj() {
		return m_CurQj;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 19:00:55)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCurRq() {
		return m_CurRq;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 19:00:41)
	 * 
	 * @author��wyan
	 */
	public String getCurSystem() {
		return m_CurSystem;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 19:01:43)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getCurUser() {
		return m_CurUser;
	}

	/**
	 * ������ֵ�������ʾ���ơ�
	 * 
	 * �������ڣ�(2001-2-15 14:18:08)
	 * 
	 * @return java.lang.String ������ֵ�������ʾ���ơ�
	 */
	public String getEntityName() {
		return null;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-11-6 16:44:24)
	 * 
	 * @return boolean
	 */
	public boolean getHsMode() {
		return m_HsMode;
	}

	/**
	 * �����ǣ������ݺ����� �񣺰���Ʒ���� �������ڣ�(2001-6-4 18:59:38)
	 * 
	 * @author��wyan
	 */
	public boolean getIsDocument() {

		return m_HxMode;
	}

	/**
	 * �����ǣ���ĩ���������� ����ҽ���ʱ���������� �쳣������ �������ڣ�(2001-9-14 14:24:37)
	 * ����޸����ڣ�(2001-9-14 14:24:37)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public int getModeType() {
		return m_AgiotageMode;
	}

	/**
	 * �����ǣ��������������� �񣺰���������� �쳣������ �������ڣ�(2001-9-14 14:26:01)
	 * ����޸����ڣ�(2001-9-14 14:26:01)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public boolean getIsMostEarly() {
		return m_HxSeq;
	}

	/**
	 * �����ǣ������ݺ����� �񣺰���Ʒ���� �������ڣ�(2001-6-4 18:59:38)
	 * 
	 * @author��wyan
	 */
	public boolean getIsMultiV() {

		return m_IsMultiV;
	}

	/**
	 * �����ǣ���ʾ�ۿ� �񣺲���ʾ�ۿ� �쳣������ �������ڣ�(2001-9-14 14:27:05) ����޸����ڣ�(2001-9-14
	 * 14:27:05)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public boolean getIsZkShow() {
		return m_ZkShow;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-11-16 16:52:14)
	 * 
	 * @return java.lang.String
	 */
	public String getProdID() {
		return m_sProdID;
	}

	/**
	 * �ո���־�� �������ڣ�(2001-5-24 13:57:31)
	 * 
	 * @author��wyan
	 * @return java.lang.String
	 */
	public String getSfbz() {
		return m_Sfbz;
	}

	/**
	 * ��Ҫ���ܣ��ǣ���ĩ���������� ����ҽ���ʱ���������� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-14 14:25:16)
	 * ����޸����ڣ�(2001-9-14 14:25:16)
	 * 
	 * @author��wyan
	 * @param mode
	 *            java.lang.String
	 */
	public void setAgiotageMode(int mode) {
		m_AgiotageMode = mode;
	}

	/**
	 * ��Ҫ���ܣ��������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:19:46) ����޸����ڣ�(2001-9-20 16:19:46)
	 * 
	 * @author��wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode1(String mode1) {
		m_checkMode1 = mode1;
	}

	/**
	 * ��Ҫ���ܣ��������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:19:46) ����޸����ڣ�(2001-9-20 16:19:46)
	 * 
	 * @author��wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode2(String mode2) {
		m_checkMode2 = mode2;
	}

	/**
	 * ��Ҫ���ܣ��������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:19:46) ����޸����ڣ�(2001-9-20 16:19:46)
	 * 
	 * @author��wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode3(String mode3) {
		m_checkMode3 = mode3;
	}

	/**
	 * ��Ҫ���ܣ��������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:19:46) ����޸����ڣ�(2001-9-20 16:19:46)
	 * 
	 * @author��wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode4(String mode4) {
		m_checkMode4 = mode4;
	}

	/**
	 * ��Ҫ���ܣ��������(null)����鵫������(check)����鲢�ҿ���(control) ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-20
	 * 16:19:46) ����޸����ڣ�(2001-9-20 16:19:46)
	 * 
	 * @author��wyan
	 * @param mode1
	 *            java.lang.String
	 */
	public void setCheckMode5(String mode5) {
		m_checkMode5 = mode5;
	}

	/**
	 * ��λ���롣 �������ڣ�(2001-5-24 13:57:59)
	 * 
	 * @author��wyan
	 * @param sfbz
	 *            java.lang.String
	 */
	public void setCurDwbm(String dwbm) {
		m_CurDwbm = dwbm;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-4 18:59:38)
	 * 
	 * @author��wyan
	 */
	public void setCurNd(String curnd) {
		m_CurNd = curnd;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-22 15:38:42)
	 * 
	 * @author��wyan
	 * @param curqj
	 *            java.lang.String
	 */
	public void setCurQj(String curqj) {
		m_CurQj = curqj;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-22 15:39:47)
	 * 
	 * @author��wyan
	 * @param currq
	 *            java.lang.String
	 */
	public void setCurRq(String currq) {
		m_CurRq = currq;
	}

	/**
	 * ��Ҫ���ܣ� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-3 22:05:53) ����޸����ڣ�(2001-9-3 22:05:53)
	 * 
	 * @author��wyan
	 * @param system
	 *            java.lang.String
	 */
	public void setCurSystem(String system) {
		m_CurSystem = system;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-6-22 15:39:06)
	 * 
	 * @author��wyan
	 * @param curuser
	 *            java.lang.String
	 */
	public void setCurUser(String curuser) {
		m_CurUser = curuser;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-11-6 16:43:57)
	 * 
	 * @param mode
	 *            boolean
	 */
	public void setHsMode(boolean mode) {
		m_HsMode = mode;
	}

	/**
	 * ��Ҫ���ܣ��ǣ������ݺ��� �񣺰���Ʒ���� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-3 22:05:05)
	 * ����޸����ڣ�(2001-9-3 22:05:05)
	 * 
	 * @author��wyan
	 * @param mode
	 *            java.lang.String
	 */
	public void setHxMode(boolean mode) {
		m_HxMode = mode;
	}

	/**
	 * ��Ҫ���ܣ� �ǣ������������� �񣺰���������� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-14 14:26:34)
	 * ����޸����ڣ�(2001-9-14 14:26:34)
	 * 
	 * @author��wyan
	 * @param seq
	 *            java.lang.String
	 */
	public void setHxSeq(boolean seq) {
		m_HxSeq = seq;
	}

	/**
	 * �˴����뷽�������� �������ڣ�(2003-11-5 15:56:51)
	 * 
	 * @param m_IsMultiV
	 *            boolean
	 */
	public void setIsMultiV(boolean newIsMultiV) {
		m_IsMultiV = newIsMultiV;
	}

	/**
	 * �˴����뷽��˵���� �������ڣ�(2001-11-16 16:51:35)
	 * 
	 * @param id
	 *            java.lang.String
	 */
	public void setProdID(String id) {
		m_sProdID = id;
	}

	/**
	 * �ո���־�� �������ڣ�(2001-5-24 13:57:59)
	 * 
	 * @author��wyan
	 * @param sfbz
	 *            java.lang.String
	 */
	public void setSfbz(String sfbz) {
		m_Sfbz = sfbz;
	}

	/**
	 * ��Ҫ���ܣ��ǣ���ʾ�ۿ� �񣺲���ʾ�ۿ� ��Ҫ�㷨�� �쳣������ �������ڣ�(2001-9-14 14:27:31)
	 * ����޸����ڣ�(2001-9-14 14:27:31)
	 * 
	 * @author��wyan
	 * @param show
	 *            java.lang.String
	 */
	public void setZkShow(boolean show) {
		m_ZkShow = show;
	}

	/**
	 * ��֤���������֮��������߼���ȷ�ԡ�
	 * 
	 * �������ڣ�(2001-2-15 11:47:35)
	 * 
	 * @exception nc.vo.pub.ValidationException
	 *                �����֤ʧ�ܣ��׳� ValidationException���Դ�����н��͡�
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
