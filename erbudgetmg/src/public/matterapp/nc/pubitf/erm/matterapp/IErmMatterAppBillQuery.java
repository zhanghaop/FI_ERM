package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批单查询服务
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillQuery {
	
	/**
	 * 根据pk查询事项审批VO
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO queryBillByPK(String pk) throws BusinessException;
	
	/**
	 * 根据PK集合查询出来VO集合
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByPKs(String[] pks) throws BusinessException;
	/**
	 * 根据PK集合查询出来VO集合
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByPKs(String[] pks,boolean lazyLoad) throws BusinessException;
	
	/**
	 * 查询VO集合
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillFromMtapp(String condition,String djlxbm,String pk_org,String pk_psndoc) throws BusinessException;
	
	/**
	 * 拉单查询VO集合
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillByWhere(String condition) throws BusinessException;
	
	/**
	 * 根据表头pk集合查询表头VO
	 * @param pks
	 * @return
	 * @throws BusinessException
	 */
	public MatterAppVO[] queryMatterAppVoByPks(String[] pks) throws BusinessException;
	
	/**
	 * 根据表体pk集合查询表体VO集合
	 * @param pks 表体Pk数组
	 * @return
	 * @throws BusinessException
	 */
	public MtAppDetailVO[] queryMtAppDetailVOVoByPks(String[] pks) throws BusinessException;
}
