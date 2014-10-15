package nc.bs.erm.matterapp.control;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 事项审批单控制、回写接口
 * 
 * @author lvhj
 * 
 */
public interface IErmMatterAppYsControlService {

	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            事项审批单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @param isExistParent
	 *            是否执行上游预算
	 * @throws BusinessException
	 */
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException;

	/**
	 * 事项审批单修改情况下的预算控制
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            是否控制预占数， false表示控制执行数
	 * @param isExistParent
	 *            是否执行上游预算
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException;
}
