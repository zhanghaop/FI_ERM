package nc.itf.erm.matterapp.ext;

import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 费用申请单预算控制、回写接口
 * 
 * 合生元项目专用
 * 
 * @author lvhj
 * 
 */
public interface IErmMatterAppYsControlServiceExt {

	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            费用申请单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode)
			throws BusinessException;

	/**
	 * 费用申请单修改情况下的预算控制
	 * 
	 * @param vos
	 * @param oldvos
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException;
}
