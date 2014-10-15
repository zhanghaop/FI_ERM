package nc.itf.erm.service;

import nc.vo.pub.BusinessException;

/**
 * 客户及供应商合并，费用服务
 * 
 * @author chenshuaia
 * 
 */
public interface IErmMergeService {

	/**
	 * 供应商合并
	 * 
	 * @param targetSup
	 *            合并后的供应商pk
	 * @param sourceSup
	 *            原供应商pk
	 */
	public void mergeSupplier(String targetSup, String sourceSup) throws BusinessException;

	/**
	 * 客户合并
	 * 
	 * @param targetCus
	 *            合并后的客户pk
	 * @param sourceCus
	 *            原客户pk
	 */
	public void mergeCustomer(String targetCus, String sourceCus) throws BusinessException;
}
