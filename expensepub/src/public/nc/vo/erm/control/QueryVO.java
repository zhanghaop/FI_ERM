package nc.vo.erm.control;

import java.util.ArrayList;
import java.util.Vector;

import nc.vo.er.pub.QryCondArrayVO;
import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;
import nc.vo.tb.obj.NtbParamVO;

/**
 * <p>
 * TODO �ӿ�/�๦��˵����ʹ��˵�����ӿ��Ƿ�Ϊ�������������ʹ���ߣ����Ƿ��̰߳�ȫ�ȣ���
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author 
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-3-15 ����10:25:09
 */
public class QueryVO extends ValueObject {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1013699677779294590L;
	private ArrayList<Object> m_SourceArr = new ArrayList<Object>();
    private String m_Sqls[]/* = new String[3]*/;//Ŀǰ������sql���
    private int m_GroupCount = 0;
// Ԥ��ȡ���� false��QueryFuncBO.queryFunc(nc.vo.arap.func.QueryVO qvo, java.sql.Connection con)����HashMap
// �ո���Ԥ���ѯ������true:QueryFuncBO.queryFunc(nc.vo.arap.func.QueryVO qvo, java.sql.Connection con)����HashMap
    private boolean m_IsDetail = false;
    private boolean isFromTB = false;  //�Ƿ���Ԥ�����鷢��
    private String[] sGroupFields;
    private String sNormalCond="";
    private Vector<QryObjVO> vQryObj=new Vector<QryObjVO>();
    
    private QryCondArrayVO[] m_voConditions;
    
    private Integer dataRangeindex = null;//Ӧ�����ݷ�Χ
    
    public Vector<QryObjVO> getQryObj(){
    	return vQryObj;
    }
    public void setQryObj(Vector<QryObjVO> v){
    	vQryObj=v;
    }
    public String[] getGroupFields(){
    	return sGroupFields;
    }
    public void setGroupFields(String[] fields){
    	sGroupFields = fields;
    }
    
    public String getNormalCond(){
    	return sNormalCond;
    }
    public void setNormalCond(String sNormal){
    	sNormalCond = sNormal;
    }
    public boolean isDetail(){
    	return m_IsDetail;
    }   
	 public void setIsDetail(boolean b){
	 	m_IsDetail = b;
	 }
	 /**
	 * QueryVO ������ע�⡣
	 */
	public QueryVO() {
		super();
	}
	/**
	 * �˴����뷽��˵����
	 * �������ڣ�(01-3-20 17:32:28)
	 * @return nc.vo.pub.ValueObject
	 */
	public boolean addSource(Object o) {
	    return getSourceArr().add(o);
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-31 10:51:39)
	 * @return int
	 */
	public int getGroupCount() {
		return m_GroupCount;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:26:41)
	 * @return nc.vo.pub.ValueObject
	 */
	public ArrayList<Object> getSourceArr() {
		return m_SourceArr;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-31 10:18:37)
	 * @return java.lang.String
	 */
	public java.lang.String[] getSqls() {
		return m_Sqls;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-31 10:51:39)
	 * @param newGroupCount int
	 */
	public void setGroupCount(int newGroupCount) {
		m_GroupCount = newGroupCount;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-23 13:26:41)
	 * @param newSourceVO nc.vo.pub.ValueObject
	 */
	public void setSourceArr(ArrayList<Object> newSourceVO) {
		m_SourceArr = newSourceVO;
	}
	/**
	 * �˴����뷽��������
	 * �������ڣ�(2004-3-31 10:18:37)
	 * @param newSql java.lang.String
	 */
	public void setSql(java.lang.String[] newSqls) {
		m_Sqls = newSqls;
	}
	public NtbParamVO getFirstNtbVO() {
	    if (getSourceArr() == null
	            || getSourceArr().size() <= 0) {
	        return null;
	    }
	    return (NtbParamVO) getSourceArr().get(0);
	}
	/**
	 * @see nc.vo.pub.ValueObject#getEntityName()
	 */
	public String getEntityName() {
		// 
		return null;
	}
	/**
	 * @see nc.vo.pub.ValueObject#validate()
	 */
	public void validate() throws ValidationException {
		// 
		
	}
	public Integer getDataRangeindex() {
		return dataRangeindex;
	}
	public void setDataRangeindex(Integer dataRangeindex) {
		this.dataRangeindex = dataRangeindex;
	}
	public QryCondArrayVO[] getVoConditions() {
		return m_voConditions;
	}
	public void setVoConditions(QryCondArrayVO[] conditions) {
		m_voConditions = conditions;
	}
	public boolean isFromTB() {
		return isFromTB;
	}
	public void setFromTB(boolean isFromTB) {
		this.isFromTB = isFromTB;
	}
}
