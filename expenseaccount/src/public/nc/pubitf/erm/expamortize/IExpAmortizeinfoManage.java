package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * 摊销信息维护服务
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeinfoManage {

	/**
	 * 新增摊销信息
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void insertVOs(AggExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * 删除摊销信息
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public void deleteVOs(AggExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * 摊销期修改
	 * 
	 * @param newPeriod
	 * @param vo
	 * @throws BusinessException
	 */
	public ExpamtinfoVO updatePeriod(int newPeriod,ExpamtinfoVO vo,String currAccPeriod) throws BusinessException;
	
}
