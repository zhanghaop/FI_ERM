package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ�ύ����
 * @author shengqy
 *
 */
public interface IErmAccruedBillCommit {
	
	/**
	 * �ύ����
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] vos) throws BusinessException;
	

	/**
	 * �ջص���
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] recallVOs(AggAccruedBillVO[] vos) throws BusinessException;
	
}
