package nc.pubitf.erm.accruedexpense;

import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单查询服务
 * 
 * @author shengqy
 * 
 */
public interface IErmAccruedBillQuery {

	/**
	 * 根据pk查询预提vo
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO queryBillByPk(String pk) throws BusinessException;

	/**
	 * 根据pks查询预提vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks) throws BusinessException;

	/**
	 * 根据pks查询预提vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks, boolean lazyLoad) throws BusinessException;

	/**
	 * 根据条件查询预提vos
	 * 
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByWhere(String condition) throws BusinessException;
	

	/**
	 * 获取新增时带默认值的初始预提VO <br>
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
	public AggAccruedBillVO getAddInitAccruedBillVO(DjLXVO djlx, String funnode, AggAccruedBillVO vo) throws BusinessException;
}
