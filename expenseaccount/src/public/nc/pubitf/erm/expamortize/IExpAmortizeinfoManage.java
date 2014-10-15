package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * ̯����Ϣά������
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeinfoManage {

	/**
	 * ����̯����Ϣ
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void insertVOs(AggExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * ɾ��̯����Ϣ
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * ̯�����޸�
	 * 
	 * @param newPeriod
	 * @param vo
	 * @throws BusinessException
	 */
	public ExpamtinfoVO updatePeriod(int newPeriod,ExpamtinfoVO vo,String currAccPeriod) throws BusinessException;
	
}
