package nc.itf.erm.jkbx.ext;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 报销单拉单超申请情况， 预算控制、回写接口
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public interface IBXYsControlServiceExt {
	
	/**
	 * 预算业务处理
	 * @param vos 借款报销vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException;
	
	/**
	 * 预算控制修改
	 * @param vos 借款报销VO
	 * @throws BusinessException
	 */
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException;
}
