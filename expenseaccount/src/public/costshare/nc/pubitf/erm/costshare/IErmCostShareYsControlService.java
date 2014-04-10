package nc.pubitf.erm.costshare;

import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 
 * 费用结转单控制、回写接口
 * 
 * @author lvhj
 *
 */
public interface IErmCostShareYsControlService {
	
	
	/**
	 * 预算业务处理
	 * 
	 * @param vos
	 *            费用结转单vos
	 * @param isContray
	 *            是否反向控制
	 * @param actionCode
	 *            动作编码
	 * @throws BusinessException
	 */
	public void ysControl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException;
	
	
	/**
	 * 费用结转单修改情况下的预算控制
	 * 
	 * @param vos
	 * @param oldvos 
	 * @throws BusinessException
	 */
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos) throws BusinessException ;

}
