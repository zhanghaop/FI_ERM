package nc.vo.ep.dj;

/**
 * �ڴ˴���������˵����
 * �������ڣ�(01-3-2 8:35:49)
 * @author��Administrator
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
	//ϵͳ 0ΪӦ��,1Ӧ��,2��������
	public Integer m_Wldx;
	//��������
	
	public String m_Dwbm="";
	//��λ����
	
	public String m_Djrq1="";
	//��ʼ��������
	
	public String m_Djrq2="";
	//������������

	public String m_Djdqr1="";
	//��ʼ���ݵ�����
	
	public String m_Djdqr2="";
	//�������ݵ�����
	
	public String m_Djbh1="";
	//��ʼ���ݱ��� 
	
	public String m_Djbh2="";
	//�������ݱ���
	
	public String m_Ywbm="";
	//��������
	
	public String m_Djmboid="";
	//����ģ��oid
	
	public String m_DjzbOid1="";
	//��������oid>=
	
	public String m_DjzbOid2="";
	//��������oid<=
	
	public String m_Zyx11="";
	//�Զ���	��1
	
	public String m_Zyx12="";
	//�Զ���	��1
	public String m_Djh="";
	public nc.vo.pub.lang.UFDouble m_Je1;
	public nc.vo.pub.lang.UFDouble m_Je2;
	public String m_Bz;
	//����
	public String m_Jsfs="";
	public String m_Ksbm_cl="";//׼��ɾ��
	public String m_hbbm="";
	
	//�ͻ�
	public String m_Djrq="";
	//��������
	public String m_deptid="";
	//����
	public String m_ywybm="";
	//ҵ��Ա����
	public int m_ShenHeFlag=1;
	//1 ������ʾδ��˵ĵ���,2������ʾ����˵���,3ȫ����ʾ
	 
	public int m_ZdFlag=1;
	//1 ������ʾδ�Ƶ��ĵ���,2������ʾ���Ƶ��ĵ���,3ȫ����ʾ
	
	public boolean m_IsYhqr=false;
	//�Ƿ�ǩ��ȷ�ϣ�trueǩ��ȷ��
	public int m_UseFlag=0;
	//ʹ��ģ���  useFlag=13 ǩ��ȷ��,useFlag=14�Ƶ�
	private String m_SqlWhere="";
	//��ѯ����
	
    public nc.vo.er.pub.QryCondArrayVO[] m_NorCondVos=null;
    public nc.vo.pub.query.ConditionVO[] m_DefCondVos=null;
    public boolean isCHz=false;
    public String operator=null;
    public String psndoc=null;
    public int syscode=0;
    public String[] pk_group=null;
    public String[] pk_org = null;
    
    
    public boolean isLinkPz=false;
    //ƾ֤�Ƿ��Ѿ�����
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
	 * CondEO ������ע�⡣
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
	 * ��ѯ����Ȩ��sql
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
