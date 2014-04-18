package nc.pubitf.erm.accruedexpense;

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
	

}
