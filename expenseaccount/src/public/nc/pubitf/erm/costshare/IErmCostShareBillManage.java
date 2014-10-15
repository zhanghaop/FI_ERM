package nc.pubitf.erm.costshare;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 费用结转单维护活动
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillManage {
	
	/**
	 * 费用结转单新增
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO insertVO(AggCostShareVO vo) throws BusinessException;
	
	/**
	 * 费用结转单修改
	 * 
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggCostShareVO updateVO(AggCostShareVO vo) throws BusinessException;
	/**
	 * 费用结转单删除
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] deleteVOs(AggCostShareVO[] vos) throws BusinessException;
	
}
