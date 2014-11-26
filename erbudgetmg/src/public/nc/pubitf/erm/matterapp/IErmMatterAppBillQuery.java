package nc.pubitf.erm.matterapp;

import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.vo.er.djlx.DjLXVO;
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
	 * 按照申请人过滤，查询可拉申请单
	 * @param condition
	 * @param djlxbm
	 * @param pk_org
	 * @param pk_psndoc
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryBillFromMtappByPsn(String condition,String djlxbm,String pk_org,String pk_psndoc,String rolerSql) throws BusinessException;
	
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
	
	/**
	 * 获取新增时带默认值的初始申请VO <br>
	 * <li>单据日期
	 * <li>人员信息 
	 * <li>创建人
	 * <li>币种、汇率 
	 * <li>金额默认值
	 * <li>单据初始状态
	 * <li>财务组织、集团
	 * 
	 * @param djlx
	 *            交易类型
	 * @param funnode
	 *            节点功能号
	 * @param vo
	 *            默认VO，可以为null
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO getAddInitAggMatterVo(DjLXVO djlx, String funnode, AggMatterAppVO vo) throws BusinessException;
	
	/**
	 * 营销费用拉单根据流程平台
	 * @param queryScheme
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] queryMaFor35ByQueryScheme(IQueryScheme queryScheme)throws BusinessException;
}
