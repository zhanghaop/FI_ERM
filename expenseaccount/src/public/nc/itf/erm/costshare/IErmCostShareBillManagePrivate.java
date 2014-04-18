package nc.itf.erm.costshare;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 费用结转单私有，内部使用
 * 
 * @author luolch
 *
 */
public interface IErmCostShareBillManagePrivate {
	
	/**
	 * 费用结转单暂存处理
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO tempSaveVO(AggCostShareVO vo) throws BusinessException;
	
	
	/**
	 * 费用结转单打印更新
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO[] printNormal(String[] pks,String businDate,String pk_user) throws BusinessException;
	
	/**
	 * 查询费用结转单的交易类型是否封存
	 */
	public boolean queryFcbz(String group,String tradetype) throws BusinessException;

}
