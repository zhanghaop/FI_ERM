package nc.pubitf.erm.expamortize;

import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtprocVO;
import nc.vo.pub.BusinessException;

/**
 * 费用摊销过程记录查询
 * 
 * @author lvhj
 *
 */
public interface IExpAmortizeprocQuery {
	
	/**
	 * 根据费用摊销信息PKS查询
	 * 仅供联查使用
	 * 会将同一会计期间的同
	 * @param infovo 摊销信息表头
	 * @return
	 * @throws BusinessException
	 */
	public ExpamtprocVO[] linkProcByInfoVo(ExpamtinfoVO infovo) throws BusinessException  ;
	
	/**
	 * 根据摊销信息pk集合和会计期间查询摊销记录集合
	 * @param infoPk 摊销明细信息pk
	 * @param accperiod 会计期间（例“2012-12-02”）
	 * @return 摊销记录
	 */
	public ExpamtprocVO[] queryByInfoPksAndAccperiod(String[] infoPks , String accperiod)throws BusinessException;
	
	/**
	 * 根据报销编号和主组织
	 * @param djbh
	 * @param pk_org
	 * @return
	 */
	public ExpamtprocVO[] queryByDjbhAndPKOrg(String djbh,String pk_org)throws BusinessException;
	
	/**
	 * 根据摊销记录pks查询摊销记录
	 * @param djbh
	 * @param pk_org
	 * @return
	 */
	public ExpamtprocVO[] queryByProcPks(String[] pks)throws BusinessException;
}
