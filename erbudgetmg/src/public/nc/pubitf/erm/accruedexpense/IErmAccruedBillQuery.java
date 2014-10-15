package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ��ѯ����
 * 
 * @author shengqy
 * 
 */
public interface IErmAccruedBillQuery {

	/**
	 * ����pk��ѯԤ��vo
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO queryBillByPk(String pk) throws BusinessException;

	/**
	 * ����pks��ѯԤ��vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks) throws BusinessException;

	/**
	 * ����pks��ѯԤ��vos
	 * 
	 * @param pk
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByPks(String[] pks, boolean lazyLoad) throws BusinessException;

	/**
	 * ����������ѯԤ��vos
	 * 
	 * @param condition
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO[] queryBillByWhere(String condition) throws BusinessException;
	

}
