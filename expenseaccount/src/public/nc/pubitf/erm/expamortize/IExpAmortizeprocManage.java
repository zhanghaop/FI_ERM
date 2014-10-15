package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;

/**
 * 费用摊销过程记录数据维护
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeprocManage {

	/**
	 * 新增费用摊销过程记录
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public ExpamtprocVO[] insertVOs(ExpamtprocVO[] vos) throws BusinessException;
}
