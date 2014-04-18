package nc.vo.ep.dj;

/**
 * 在此处插入类型说明。
 * 创建日期：(01-3-2 8:35:49)
 * @author：Administrator
 */
public class DjCondVO implements java.io.Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7641757221432876655L;
	public final static Integer Voucher_All=new Integer(-1);
    public final static Integer Voucher_NotCreated=new Integer(1);
    public final static Integer Voucher_Created=new Integer(2);
    public final static Integer Voucher_Singed=new Integer(3);
	
	
	public int m_Syscode;
	//系统 0为应收,1应付,2报账中心
	public Integer m_Wldx;
	//往来对象
	
	public String m_Dwbm="";
	//单位编码
	
	public String m_Djrq1="";
	//开始单据日期
	
	public String m_Djrq2="";
	//结束单据日期

	public String m_Djdqr1="";
	//开始单据到期日
	
	public String m_Djdqr2="";
	//结束单据到期日
	
	public String m_Djbh1="";
	//开始单据编码 
	
	public String m_Djbh2="";
	//结束单据编码
	
	public String m_Ywbm="";
	//单据类型
	
	public String m_Djmboid="";
	//单据模板oid
	
	public String m_DjzbOid1="";
	//单据主表oid>=
	
	public String m_DjzbOid2="";
	//单据主表oid<=
	
	public String m_Zyx11="";
	//自定义	项1
	
	public String m_Zyx12="";
	//自定义	项1
	public String m_Djh="";
	public nc.vo.pub.lang.UFDouble m_Je1;
	public nc.vo.pub.lang.UFDouble m_Je2;
	public String m_Bz;
	//币种
	public String m_Jsfs="";
	public String m_Ksbm_cl="";//准备删除
	public String m_hbbm="";
	
	//客户
	public String m_Djrq="";
	//单据日期
	public String m_deptid="";
	//部门
	public String m_ywybm="";
	//业务员编码
	public int m_ShenHeFlag=1;
	//1 仅仅显示未审核的单据,2仅仅显示已审核单据,3全部显示
	 
	public int m_ZdFlag=1;
	//1 仅仅显示未制单的单据,2仅仅显示已制单的单据,3全部显示
	
	public boolean m_IsYhqr=false;
	//是否签字确认，true签字确认
	public int m_UseFlag=0;
	//使用模块号  useFlag=13 签字确认,useFlag=14制单
	private String m_SqlWhere="";
	//查询条件
	
    public nc.vo.er.pub.QryCondArrayVO[] m_NorCondVos=null;
    public nc.vo.pub.query.ConditionVO[] m_DefCondVos=null;
    public boolean isCHz=false;
    public String operator=null;
    public String psndoc=null;
    public int syscode=0;
    public String[] pk_group=null;
    public String[] pk_org = null;
    
    
    public boolean isLinkPz=false;
    //凭证是否已经记帐
    public Integer VoucherFlag = Voucher_All;
    public boolean isUsedGL=false;
    public nc.vo.pub.query.RefResultVO[] refs=null;
    public String defWhereSQL="";
    public String djdl;
    public String isPause=null;
    
    //twei add
    public boolean isInit=false;
    public Integer[] VoucherFlags = null;
    public boolean isAppend = false;
    
    public boolean isJustQryDel = false;
    
    public String nodecode;
    
//  private ErmDataPermissionVO powerVO;

//	public ErmDataPermissionVO getPowerVO() {
//		return powerVO;
//	}
//
//	public void setPowerVO(ErmDataPermissionVO powerVO) {
//		this.powerVO = powerVO;
//	}
	/**
	 * CondEO 构造子注解。
	 */
	public DjCondVO() {
		super();
	}
	public String getSqlWhere()
	{
		return this.m_SqlWhere;
	}
	public void setSqlWhere(String newSqlWhere)
	{
		this.m_SqlWhere=newSqlWhere;
	}
	public String getDefWhereSQL() {
		return defWhereSQL;
	}
	public void setDefWhereSQL(String defWhereSQL) {
		this.defWhereSQL = defWhereSQL;
	}
//added by chendya 
	/**
	 * 查询数据权限sql
	 */
	private String dataPowerSql;
	
	public String getDataPowerSql() {
		return dataPowerSql;
	}
	public void setDataPowerSql(String dataPowerSql) {
		this.dataPowerSql = dataPowerSql;
	}
//--end	
	
	
}
