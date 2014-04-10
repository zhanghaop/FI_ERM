package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单预算控制服务接口
 * @author chenshuaia
 *
 */
public interface IErmAccruedBillYsControlService {
	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            预提单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @param isExistParent
	 *            是否执行上游预算
	 * @throws BusinessException
	 */
	public void ysControl(AggAccruedBillVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException;

	/**
	 * 预提单修改情况下的预算控制
	 * 
	 * @param vos
	 * @param oldvos
	 * @param isPreind
	 *            是否控制预占数， false表示控制执行数
	 * @param isExistParent
	 *            是否执行上游预算
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggAccruedBillVO[] vos, AggAccruedBillVO[] oldvos) throws BusinessException;
}
