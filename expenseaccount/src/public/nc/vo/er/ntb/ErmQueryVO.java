package nc.vo.er.ntb;

/**
 * ��Ԥ��ӿڣ���ѯ��vo
 *
 */
public class ErmQueryVO {
	
	private static final long serialVersionUID = 1L;
	
	/*�Ƿ��ǻ���ڼ�,������Ȼ����,��ʽΪ:YYYY-MM-DD;���ڻ������,��ʽΪ:YYYY-MM*/
	private boolean iskjqj = false;
	/*��ʼ����,����ֻ�ṩ����ʱ���,��������ƥ���,��ʱ�������ڴ��ֶ�,����Ҫ��֤m_enddateΪnull*/
	private String begdate = null;
	/*��ֹ����*/
	private String enddate = null;
	/*ҵ��ϵͳ�ĵ�������,����͸���ҵ��ϵͳע���VO���ṩ��ҵ�����ͽ���ƥ��,��:�տ�*/
	private String djlxbm = null;
	/*��Ӧ��VO���ԣ���Ӧ�����������͵�VO����ֵ"*/
	private String[] busiAttrs = null;
	/*������������,Ϊ�����������ͳ���,��Ӧ��Ԥ���еĸ���ά��,ȡֵ��Դ��"nc.vo.ntb.outer.IBudgetConst"*/
	private String[] pkdim = null;
	/*���صı�������  0��ԭ��      1��ȫ�ֱ���    2�����ű���     3:��֯����*/
	private int curr_type = 0;
	
	/*��������*/
	private String datetype = null;
	/*����״̬  -1������   1�����     0����������*/
	private String billstatus = "1";
	
	public String getBillstatus() {
		return billstatus;
	}
	public void setBillstatus(String billstatus) {
		this.billstatus = billstatus;
	}
	public String getDatetype() {
		return datetype;
	}
	public void setDatetype(String datetype) {
		this.datetype = datetype;
	}

	/*�Ƿ�����ڳ�,Ĭ��Ϊfalse*/
	private boolean isIncludeInit = false;
	
	/*����PK*/
	private String bzbm = null;
	/*������֯*/
	private String[] pk_org = null;
	/*��������*/
	private String[] transType = null;

	/*����*/
	private String[] customer = null;
	/*��Ӧ��*/
	private String[] hbbm = null;
	/*����*/
	private String[] deptid = null;
	/*ҵ��Ա*/
	private String[] ywy = null;
	/*��֧��Ŀ*/
	private String[] szxmid = null;	
	
	public boolean isIncludeInit() {
		return isIncludeInit;
	}
	public void setIncludeInit(boolean isIncludeInit) {
		this.isIncludeInit = isIncludeInit;
	}
	
	public String[] getPk_org() {
		return pk_org;
	}
	public void setPk_org(String[] pkOrg) {
		pk_org = pkOrg;
	}
	public String[] getTransType() {
		return transType;
	}
	public void setTransType(String[] transType) {
		this.transType = transType;
	}
	public String[] getCustomer() {
		return customer;
	}
	public void setCustomer(String[] customer) {
		this.customer = customer;
	}
	public String[] getHbbm() {
		return hbbm;
	}
	public void setHbbm(String[] hbbm) {
		this.hbbm = hbbm;
	}
	public String[] getDept() {
		return deptid;
	}
	public void setDept(String[] dept) {
		this.deptid = dept;
	}
	public String[] getYwy() {
		return ywy;
	}
	public void setYwy(String[] ywy) {
		this.ywy = ywy;
	}
	public String[] getSzxmid() {
		return szxmid;
	}
	public void setSzxmid(String[] szxmid) {
		this.szxmid = szxmid;
	}
	public int getCurr_type() {
		return curr_type;
	}
	public void setCurr_type(int currType) {
		curr_type = currType;
	}

	public String getBzbm() {
		return bzbm;
	}
	public void setBzbm(String pkCurrency) {
		bzbm = pkCurrency;
	}
	public boolean isIskjqj() {
		return iskjqj;
	}
	public void setIskjqj(boolean iskjqj) {
		this.iskjqj = iskjqj;
	}
	public String getBegdate() {
		return begdate;
	}
	public void setBegdate(String begdate) {
		this.begdate = begdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getDjlxbm() {
		return djlxbm;
	}
	public void setDjlxbm(String djlxbm) {
		this.djlxbm = djlxbm;
	}
	public String[] getBusiAttrs() {
		return busiAttrs;
	}
	public void setBusiAttrs(String[] busiAttrs) {
		this.busiAttrs = busiAttrs;
	}
	public String[] getPkdim() {
		return pkdim;
	}
	public void setPkdim(String[] pkdim) {
		this.pkdim = pkdim;
	}


}
