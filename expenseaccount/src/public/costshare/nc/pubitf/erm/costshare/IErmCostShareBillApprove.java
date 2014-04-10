package nc.pubitf.erm.costshare;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 费用结转单确认活动
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareBillApprove {
	
	/**
	 * 确认费用结转单
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] approveVOs(AggCostShareVO[] vos,UFDate buDate) throws BusinessException;
	/**
	 * 取消确认费用结转单
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unapproveVOs(AggCostShareVO[] vos) throws BusinessException;

}
