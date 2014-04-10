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
 * 借款报销类单据内部使用的业务处理接口.
 *	nc.itf.arap.prv.IBXBillPrivate
 */
public interface IBXBillPrivate {
	
	/**
	 * 根据where条件查询表头数据
	 * @param sql
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql,String djdl)throws BusinessException;
	
	/**
	 * 根据主键数组和单据大类查询表头数据
	 * @param keys
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys,String djdl)throws BusinessException;
	
	/**
	 * 根据where条件查询VO数据
	 * @param sql
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOsByWhereSql(String sql,String djdl)throws BusinessException;
	
	/**
	 * 根据主键数组和单据大类查询VO数据
	 * @param keys
	 * @param djdl
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOsByPrimaryKeys(String[] keys,String djdl)throws BusinessException;
	
	/**
	 * 根据DjCondVO查询表头数据
	 * @param start
	 * @param count
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO)throws BusinessException;
	
	/**
	 * 根据DjCondVO查询数据
	 * @param start
	 * @param count
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> queryVOs(Integer start, Integer count, DjCondVO condVO)throws BusinessException;
	
	/**
	 * 查询表体数据
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public BXBusItemVO[] queryItems(JKBXHeaderVO header) throws BusinessException;
	

	
	/**
	 * 补充表体的信息（财务信息，业务信息)
	 * @param name
	 * @return
	 * @throws BusinessException
	 */
	public List<JKBXVO> retriveItems(List<JKBXHeaderVO> name) throws BusinessException ;
	
	/**
	 * 补充表体的信息（财务信息，业务信息)
	 * @param name
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO retriveItems(JKBXHeaderVO name) throws BusinessException ;
	
	/**
	 * 查询表体数据
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public BXBusItemVO[] queryItems(JKBXHeaderVO[] header) throws BusinessException;

	/**
	 * 审核单据
	 */
	public MessageVO[] audit(JKBXVO[] bxvos) throws BusinessException;
	
	/**
	 * 反审核单据
	 */
	public MessageVO[] unAudit(JKBXVO[] bxvos) throws BusinessException;

	/**
	 * 查询冲借款对照数据
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO header) throws BusinessException;

	/**
	 * 查询结算对照数据
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header) throws BusinessException;

	/**
	 * 查询数据条数
	 * @param condVO
	 * @return
	 * @throws BusinessException
	 */
	public int querySize(DjCondVO condVO) throws BusinessException;

	/**
	 * 
	 * 批冲销接口
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
	 * 查找操作员在公司的角色，根据角色查找返回授权代理人集合，通过user属性相应公司的业务员
	 * @param pk_user 操作员pk
	 * @param user_corp 操作员公司，为空时
	 * @param ywy_corp 业务员所在公司
	 * @return 授权代理人集合
	 * @throws BusinessException
	 */
	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp, String ywy_corp) throws BusinessException ;

	/**
	 * 保存自定义的授权
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
	 * 判断费用类型是否在报销标准引用
	 * @param pk_expensetype
	 */
	public boolean getIsExpensetypeUsed(String pk_expensetype) throws BusinessException;
	
	/**
	 * 判断报销类型是否在报销标准以及单据中引用
	 * @param pk_expensetype
	 */
	public boolean getIsReimtypeUsed(String pk_reimtype) throws BusinessException;	
	/**
	 * @param userid
	 * @param pk_group
	 * @return String[]{人员管理档案pk，人员基本档案pk，部门档案pk}
	 * @throws BusinessException
	 */
	public String[] queryPsnidAndDeptid(String userid,String pk_group)throws BusinessException;
	
	/**
	 * 借款报销人设置授权代理
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
	 * 保存授权代理VO
	 * @param preSaveVOList
	 * @return
	 * @throws BusinessException
	 */
	public void saveSqdlVO(List<SqdlrVO> preSaveVOList,String condition) throws BusinessException;

	/**
	 * 查询授权代理VO
	 * 键=角色pk，值=List<SqdlrVO>
	 * @param sql
	 * @return
	 * @throws BusinessException
	 */
	public Map<String,List<SqdlrVO>> querySqdlrVO(String sql) throws BusinessException;
	
	/**
	 * 查询组织的期初期间
	 * @author chendya
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> queryDefaultOrgAndQcrq(String pk_psndoc) throws BusinessException;
	
	/**
	 * 返回报销列表状态单据模版VO
	 * @author chendya
	 * @throws BusinessException
	 */
	public BillTempletVO[] getBillListTplData(BillOperaterEnvVO[] envo) throws BusinessException;
		
	/**
	 * 查询部门所关联成本中心
	 * @param pk_group 当前集团
	 * @return
	 * @throws BusinessException
	 */
	public Map<String,SuperVO> getDeptRelCostCenterMap(String pk_group) throws BusinessException;
	
	/**
	 * 报销VO远程调用专用
	 * @param voClassName
	 * @param whereSql
	 * @return
	 * @throws BusinessException
	 */
	public List<SuperVO> getVORemoteCall(Class<?> voClassName,String conditon,String[] fields) throws BusinessException;
	
	/**
	 * 缓存有权限的组织
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
