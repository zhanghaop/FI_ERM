package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单审批服务
 * @author shengqy
 *
 */
public interface IErmAccruedBillApprove {
	
	/**
	 * 审批单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] approveVOs(AggAccruedBillVO[] vos) throws BusinessException;
	

	/**
	 * 取消审批单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unApproveVOs(AggAccruedBillVO[] vos) throws BusinessException;
	
	/**
	 * 更新VO单据状态
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO updateVOBillStatus(AggAccruedBillVO vo)  throws BusinessException;
}
