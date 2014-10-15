package nc.bs.arap.loancontrol;

import nc.vo.ep.bx.LoanControlModeVO;
import nc.vo.ep.bx.LoanControlVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.pub.BusinessException;

/**
 * @author twei
 *
 * nc.bs.arap.loancontrol.LoanControlMode
 * 
 * 借款控制方式定义
 */
public interface LoanControlMode {
	
	/**
	 * @param defvo    借款控制设置VO
	 * @param modevo   借款控制方式VO
	 * @param vo	   需要控制的VO数组
	 * @return
	 * @throws BusinessException
	 * 
	 * 进行控制操作, 控制不通过返回错误信息
	 */
	public String control(LoanControlVO defvo,LoanControlModeVO modevo, IFYControl[] vo) throws BusinessException;
	
}
