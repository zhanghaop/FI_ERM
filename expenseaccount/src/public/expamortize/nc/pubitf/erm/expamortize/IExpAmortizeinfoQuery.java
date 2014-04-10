package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtDetailVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * 费用摊销信息查询
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeinfoQuery {

	/**
	 * 根据报销PKs查询摊销信息
	 * 
	 * @param bxPks
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO[] queryByBxPks(String[] bxPks, String currentAccMonth) throws BusinessException;
	
	
	/**
	 * 根据摊销信息PK查询摊销信息
	 * 
	 * @param pks
	 * @param currentAccMonth 当前会计月
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO queryByPk(String pk, String currentAccMonth) throws BusinessException;
	/**
	 * 根据摊销信息PK数组查询摊销信息
	 * 
	 * @param pks
	 * @param currentAccMonth 当前会计月
	 * @return
	 * @throws BusinessException
	 */
	public AggExpamtinfoVO[] queryByPks(String[] pks,String currentAccMonth) throws BusinessException;
	
	/**
	 * 根据摊销PK数组查询摊销表头信息信息
	 * 
	 * @param pks 摊销pk数组
	 * @param currentAccMonth 当前会计月
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryExpamtinfoByPks(String[] pks,String currentAccMonth) throws BusinessException;
	
	/**
	 * 根据组织、会计期间，查询摊销信息
	 * 
	 * @param pk_org
	 * @param currentAccMonth
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryByOrg(String pk_org,String currentAccMonth) throws BusinessException;
	
	/**
	 * 根据组织、会计期间，查询摊销信息的PK
	 * 
	 * @param pk_org
	 * @param period 当前会计月
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryPksByCond(String pk_org,String period) throws BusinessException;
	
	/**
	 * 查询全部摊销中的待摊信息(包括初始态、摊销中的待摊信息，初始态的摊销信息是否可以进行摊销在摊销服务中校验及更新)
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryAllAmtingVOs() throws BusinessException;
	
	/**
	 * 根据组织和报销单据号查询摊销信息的PK
	 * 
	 */
	
	public String[] queryByOrgBillNo(String pk_org, String billno)throws BusinessException;
	
	/**
	 * 根据组织和报销单据号查询摊销信息
	 * @param pk_org
	 * @param billno
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtinfoVO[] queryByOrgAndBillNo(String pk_org, String billno) throws BusinessException;
	
	/**
	 * 根据查询条件查询摊销信息的PK
	 * @param whereSql
	 * @return
	 * @throws BusinessException
	 */
	public String[] queryPksByWhereSql(String whereSql)throws BusinessException;
	
	/**
	 * 根据摊销信息主表的PK查询子表
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtDetailVO[] queryAllDetailVOs(String pk_expamtinfo) throws BusinessException;
}
