package nc.pubitf.erm.costshare;

import nc.vo.ep.bx.BXHeaderVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 费用结转单查询活动
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillQuery {
	
	/**
	 * 根据PK查询
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO queryBillByPK(String pk) throws BusinessException;
	/**
	 * 根据PK数组查询
	 * 
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] queryBillByPKs(String[] pks) throws BusinessException;
	
	
	/**
	 * 根据查询条件查询
	 * 
	 * @param sqlWhere
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] queryBillByWhere(String sqlWhere) throws BusinessException;
	
	/**
	 * 根据报销单表头查询费用明细
	 * @param header
	 * @return
	 * @throws BusinessException
	 */
	public CShareDetailVO[] queryCShareDetailVOSByBxVoHead(BXHeaderVO header) throws BusinessException;
	
	/**
	 * 根据报销单表头查询费用结转单表头
	 * @param header
	 * @param from  true表示报销单、false表示费用结转单、null表示全部
	 * @return
	 * @throws BusinessException
	 */
	public CostShareVO queryCShareVOByBxVoHead(JKBXHeaderVO header, UFBoolean from) throws BusinessException;
	/**
	 * 根据组织，开始期间和结束期间查询费用结转单状态
	 * @param pk_org
	 * @param begindate
	 * @param enddate
	 * @return
	 * @throws BusinessException
	 */
	public CostShareVO[] getShareByMthPk(String pk_org, String begindate,
			String enddate) throws BusinessException;
}
