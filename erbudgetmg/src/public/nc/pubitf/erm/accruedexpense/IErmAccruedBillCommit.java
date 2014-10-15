package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * 预提单提交服务
 * @author shengqy
 *
 */
public interface IErmAccruedBillCommit {
	
	/**
	 * 提交单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] vos) throws BusinessException;
	

	/**
	 * 收回单据
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] recallVOs(AggAccruedBillVO[] vos) throws BusinessException;
	
}
