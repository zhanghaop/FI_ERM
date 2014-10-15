package nc.pubitf.erm.accruedexpense;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AccruedVerifyQueryVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

/**
 * Ԥ�ᵥ��������
 * 
 * @author lvhj
 *
 */
public interface IErmAccruedBillVerifyService {

	/**
	 * ����Ԥ����ϸ
	 * 
	 * @param vos ���±����ĺ�����ϸ
	 * @param bxdpks �����������ϸ�ı�����pks
	 * @throws BusinessException
	 */
	public void verifyAccruedVOs(AccruedVerifyVO[] vos,String[] bxdpks) throws BusinessException;
	
	
	/**
	 * ֻ��������Ԥ����ϸ������дԤ�ᵥ
	 * 
	 * @param vos ���±����ĺ�����ϸ
	 * @param bxdpks �����������ϸ�ı�����pks
	 * @throws BusinessException
	 */
	public void tempVerifyAccruedVOs(AccruedVerifyVO[] vos,String[] bxdpks) throws BusinessException;
	
	/**
	 * ��Ч����Ԥ����ϸ
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void effectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException;
	/**
	 * ȡ����Ч����Ԥ����ϸ
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void uneffectAccruedVerifyVOs(JKBXVO[] bxvos) throws BusinessException;
	
	/**
	 * ��ѯ��ǰ���������ɺ�����Ԥ�ᵥ
	 * 
	 * where����Ϊ��ʱ����Ĭ�Ϲ���������ѯ ��Ԥ����� >0 and ����֯=��������֯ and ����=�������� and ������=�����ˣ����򣬲��޶� ������=����������
	 * 
	 * @param pk_org
	 * @param pk_currtype
	 * @param pk_bxr
	 * @param where
	 * @return
	 * @throws BusinessException 
	 */
	public AggAccruedBillVO[] queryAggAccruedBillVOsByWhere(AccruedVerifyQueryVO queryvo) throws BusinessException;
	
	/**
	 * �Ƿ���ں���Ԥ��ĵ���δ��Ч
	 * @return
	 */
	public boolean isExistAccruedVerifyEffectStatusNo(String pk_accrued_bill)throws BusinessException;
	
}
