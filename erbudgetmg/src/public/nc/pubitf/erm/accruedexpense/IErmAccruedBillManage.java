package nc.pubitf.erm.accruedexpense;

import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥά������
 * @author shengqy
 *
 */
public interface IErmAccruedBillManage {
	
	/**
	 * ��������
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO insertVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * �޸ĵ���
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO updateVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * ɾ������
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggAccruedBillVO [] vos) throws BusinessException;
	
	/**
	 * �����ݴ�
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO tempSave(AggAccruedBillVO vo) throws BusinessException;
	
	/**
	 * ��ʽ��ӡ��Ϣ����
	 * @param accrueVo
	 * @return
	 * @throws BusinessException
	 */
	public AccruedVO updatePrintInfo(AccruedVO accrueVo) throws BusinessException;
	
	/**
	 * ���
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO redbackVO(AggAccruedBillVO vo) throws BusinessException;


	/**
	 * ɾ����嵥��
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AggAccruedBillVO unRedbackVO(AggAccruedBillVO vo) throws BusinessException;
	
	/**
	 * Ԥ�ᵥ����
	 */
	public AggAccruedBillVO invalidBill(AggAccruedBillVO vo) throws BusinessException;

}