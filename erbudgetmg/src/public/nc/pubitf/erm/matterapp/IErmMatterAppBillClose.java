package nc.pubitf.erm.matterapp;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 事项审批单关闭服务
 * 
 * @author lvhj
 *
 */
public interface IErmMatterAppBillClose {
	
	/**
	 * 关闭单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */

	public AggMatterAppVO[] closeVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * 重启单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] openVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * 自动关闭单据
	 * @param vos
	 * @param isSelf 是否申请单节点本身维护
	 * @return
	 * @throws BusinessException
	 */
	
	public AggMatterAppVO[] autoCloseVOs(AggMatterAppVO[] vos) throws BusinessException;
	/**
	 * 自动重启单据
	 * @param vos
	 * @param isSelf 是否申请单节点本身维护
	 * @return
	 * @throws BusinessException
	 */
	public AggMatterAppVO[] autoOpenVOs(AggMatterAppVO[] vos) throws BusinessException;

}
