package nc.pubitf.erm.expamortize;

import nc.vo.erm.common.MessageVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

/**
 * 费用待摊摊销服务
 * 
 * @author lvhj
 *
 */
public interface IExpAmortize {
	
	/**
	 * 费用摊销
	 * 
	 * @param pk_org 组织pk
	 * @param currYearMonth 当前会计年月
	 * @param vos 摊销信息
	 * @throws BusinessException
	 */
	public MessageVO[] amortize(String pk_org,String currYearMonth, ExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * 费用摊销（独立事务）
	 * @param pk_org 组织pk
	 * @param currYearMonth 当前会计年月
	 * @param vo 摊销信息
	 * @throws BusinessException
	 */
	public MessageVO amortize_RequiresNew(String pk_org,String currYearMonth,ExpamtinfoVO vo) throws BusinessException;
	
	/**
	 * 费用反摊销
	 * 
	 * @param pk_org 组织pk
	 * @param currYearMonth 当前会计年月
	 * @param vos 摊销信息
	 * @throws BusinessException
	 */
	public MessageVO[] unAmortize(String pk_org,String currYearMonth, ExpamtinfoVO[] vos) throws BusinessException;
	
	/**
	 * 费用摊销（独立事务）
	 * @param pk_org 组织pk
	 * @param currYearMonth 当前会计年月
	 * @param vo 摊销信息
	 * @throws BusinessException
	 */
	public MessageVO unAmortize_RequiresNew(String pk_org,String currYearMonth,ExpamtinfoVO vo) throws BusinessException;
}
