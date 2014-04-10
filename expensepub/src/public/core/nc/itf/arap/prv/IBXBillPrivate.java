package nc.itf.arap.prv;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import nc.bs.dao.DAOException;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MessageVO;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.reimrule.ReimRuleVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.billtype.BilltypeVO;
import nc.vo.pub.lang.UFDate;
/**
 * �����൥���ڲ�ʹ�õ�ҵ����ӿ�.
 *	nc.itf.arap.prv.IBXBillPrivate
 */
public interface IBXBillPrivate {
	
	/**
	 * ����where������ѯ��ͷ����
	 * @param sql
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql,String djdl)throws BusinessException;
	
	/**
	 * ������������͵��ݴ����ѯ��ͷ����
	 * @param keys
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys,String djdl)throws BusinessException;
	
	/**
	 * ����where������ѯVO����
	 * @param sql
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOsByWhereSql(String sql,String djdl)throws BusinessException;
	
	/**
	 * ������������͵��ݴ����ѯVO����
	 * @param keys
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOsByPrimaryKeys(String[] keys,String djdl)throws BusinessException;
	
	/**
	 * ����DjCondVO��ѯ��ͷ����
	 * @param start
	 * @param count
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO)throws BusinessException;
	
	/**
	 * ����DjCondVO��ѯ����
	 * @param start
	 * @param count
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOs(Integer start, Integer count, DjCondVO condVO)throws BusinessException;
	
	/**
	 * ��ѯ��������
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public BXBusItemVO[] queryItems(JKBXHeaderVO header) throws BusinessException;
	

	
	/**
	 * ����������Ϣ��������Ϣ��ҵ����Ϣ)
	 * @param name
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> retriveItems(List<JKBXHeaderVO> name) throws BusinessException ;
	
	/**
	 * ����������Ϣ��������Ϣ��ҵ����Ϣ)
	 * @param name
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO retriveItems(JKBXHeaderVO name) throws BusinessException ;
	
	/**
	 * ��ѯ��������
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public BXBusItemVO[] queryItems(JKBXHeaderVO[] header) throws BusinessException;

	/**
	 * ��˵���
	 */
	public MessageVO[] audit(JKBXVO[] bxvos) throws BusinessException;
	
	/**
	 * ����˵���
	 */
	public MessageVO[] unAudit(JKBXVO[] bxvos) throws BusinessException;

	/**
	 * ��ѯ�����������
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO header) throws BusinessException;

	/**
	 * ��ѯ�����������
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header) throws BusinessException;

	/**
	 * ��ѯ��������
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public int querySize(DjCondVO condVO) throws BusinessException;

	/**
	 * 
	 * �������ӿ�
	 * 
	 * @param selBxvos
	 * @param mode_data
	 * @param param 
	 */
	public List<BxcontrastVO> batchContrast(JKBXVO[] selBxvos, List<String> mode_data, BatchContratParam param) throws BusinessException ;
	
	
	public void saveBatchContrast(List<BxcontrastVO> selectedData,boolean delete)throws BusinessException ;

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos,boolean isBatch) throws BusinessException;
	
	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs,  Map<String, String[]> defMap)throws BusinessException ;

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles) throws BusinessException ;

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException ;
	
	/**
	 * ���Ҳ���Ա�ڹ�˾�Ľ�ɫ�����ݽ�ɫ���ҷ�����Ȩ�����˼��ϣ�ͨ��user������Ӧ��˾��ҵ��Ա
	 * @param pk_user ����Աpk
	 * @param user_corp ����Ա��˾��Ϊ��ʱ
	 * @param ywy_corp ҵ��Ա���ڹ�˾
	 * @return ��Ȩ�����˼���
	 * @throws BusinessException
	 */
	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp, String ywy_corp) throws BusinessException ;

	/**
	 * �����Զ������Ȩ
	 * @param roles
	 * @param map
	 */
	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap) throws BusinessException ;
	/**
	 * 
	 * @param source
	 * @param target
	 */
	public void copyDefused(BilltypeVO source,BilltypeVO target) throws DAOException;
	/**
	 * @param key
	 * @param tableName
	 * @param pkfield
	 */
	public Map<String, String> getTsByPrimaryKey(String[] key, String tableName,String pkfield) throws BusinessException;
	/**
	 * @param billtype
	 * @param pk_corp
	 */
	public List<ReimRuleVO> queryReimRule(String billtype,String pk_corp) throws BusinessException;
	/**
	 * @param pk_billtype
	 * @param pk_corp
	 * @param reimRuleVOs
	 */	
	public List<ReimRuleVO> saveReimRule(String pk_billtype,String pk_corp, ReimRuleVO[] reimRuleVOs) throws BusinessException;
	/**
	 * �жϷ��������Ƿ��ڱ�����׼����
	 * @param pk_expensetype
	 */
	public boolean getIsExpensetypeUsed(String pk_expensetype) throws BusinessException;
	
	/**
	 * �жϱ��������Ƿ��ڱ�����׼�Լ�����������
	 * @param pk_expensetype
	 */
	public boolean getIsReimtypeUsed(String pk_reimtype) throws BusinessException;	
	/**
	 * @param userid
	 * @param pk_group
	 * @return String[]{��Ա������pk����Ա��������pk�����ŵ���pk}
	 * @throws BusinessException
	 */
	public String[] queryPsnidAndDeptid(String userid,String pk_group)throws BusinessException;
	
	/**
	 * ������������Ȩ����
	 * @param jkbxr
	 * @param rolersql
	 * @param billtype
	 * @param user
	 * @param date
	 * @param pk_org
	 * @return
	 * @throws BusinessException
	 */
	public String getAgentWhereString(String jkbxr, String rolersql,
			String billtype, String user, String date, String pk_org) throws BusinessException ;
	
	/**
	 * ������Ȩ����VO
	 * @param preSaveVOList
	 * @return
	 * @throws BusinessException
	 */
	public void saveSqdlVO(List<SqdlrVO> preSaveVOList,String condition) throws BusinessException;

	/**
	 * ��ѯ��Ȩ����VO
	 * ��=��ɫpk��ֵ=List<SqdlrVO>
	 * @param sql
	 * @return
	 * @throws BusinessException
	 */
	public Map<String,List<SqdlrVO>> querySqdlrVO(String sql) throws BusinessException;
	
	/**
	 * ��ѯ��֯���ڳ��ڼ�
	 * @author chendya
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> queryDefaultOrgAndQcrq(String pk_psndoc) throws BusinessException;
	
	/**
	 * ���ر����б�״̬����ģ��VO
	 * @author chendya
	 * @throws BusinessException
	 */
	public BillTempletVO[] getBillListTplData(BillOperaterEnvVO[] envo) throws BusinessException;
		
	/**
	 * ��ѯ�����������ɱ�����
	 * @param pk_group ��ǰ����
	 * @return
	 * @throws BusinessException
	 */
	public Map<String,SuperVO> getDeptRelCostCenterMap(String pk_group) throws BusinessException;
	
	/**
	 * ����VOԶ�̵���ר��
	 * @param voClassName
	 * @param whereSql
	 * @return
	 * @throws BusinessException
	 */
	public List<SuperVO> getVORemoteCall(Class<?> voClassName,String conditon,String[] fields) throws BusinessException;
	
	/**
	 * ������Ȩ�޵���֯
	 * 
	 * @param pk_user
	 * @param nodeCode
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getPermissonOrgMapCall(String pk_user,
			String nodeCode, String pk_group, UFDate date)
			throws BusinessException;

}
