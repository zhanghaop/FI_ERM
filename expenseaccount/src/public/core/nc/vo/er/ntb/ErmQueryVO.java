package nc.vo.er.ntb;

/**
 * 与预算接口，查询用vo
 *
 */
public class ErmQueryVO {
	
	private static final long serialVersionUID = 1L;
	
	/*是否是会计期间,对于自然日期,形式为:YYYY-MM-DD;对于会计日期,形式为:YYYY-MM*/
	private boolean iskjqj = false;
	/*起始日期,对于只提供单个时间点,进行周期匹配的,将时间点放置在此字段,并且要保证m_enddate为null*/
	private String begdate = null;
	/*终止日期*/
	private String enddate = null;
	/*业务系统的单据类型,必须和各个业务系统注册的VO中提供的业务类型进行匹配,如:收款*/
	private String djlxbm = null;
	/*对应的VO属性，对应基础档案类型的VO属性值"*/
	private String[] busiAttrs = null;
	/*辅助对象类型,为基础档案类型常量,对应于预算中的各个维度,取值来源于"nc.vo.ntb.outer.IBudgetConst"*/
	private String[] pkdim = null;
	/*返回的币种类型  0：原币      1：全局本币    2：集团本币     3:组织本币*/
	private int curr_type = 0;
	
	/*日期类型*/
	private String datetype = null;
	/*单据状态  -1：保存   1：审核     0：保存和审核*/
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

	/*是否包含期初,默认为false*/
	private boolean isIncludeInit = false;
	
	/*币种PK*/
	private String bzbm = null;
	/*财务组织*/
	private String[] pk_org = null;
	/*交易类型*/
	private String[] transType = null;

	/*客商*/
	private String[] customer = null;
	/*供应商*/
	private String[] hbbm = null;
	/*部门*/
	private String[] deptid = null;
	/*业务员*/
	private String[] ywy = null;
	/*收支项目*/
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
