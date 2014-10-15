package nc.pubitf.erm.matterapp;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批单审批接口
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillApprove {

	/**
	 * 提交单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] commitVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * 收回单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos) throws BusinessException;

	/**
	 * 审批单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public MessageVO[] approveVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * 取消审批单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] unApproveVOs(AggMatterAppVO[] vos) throws BusinessException;
	
	/**
	 * 更新VO单据状态
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO vo)  throws BusinessException;
}
