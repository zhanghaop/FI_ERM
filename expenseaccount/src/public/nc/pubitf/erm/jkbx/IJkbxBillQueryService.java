package nc.pubitf.erm.jkbx;

import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * 借款报销单-查询服务，对外接口
 * 
 * @author lvhj
 *
 */
public interface IJkbxBillQueryService {

	/**
	 * 查询借款单主表关键字段信息
	 * 
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryJKBills() throws BusinessException;
	/**
	 * 查询借款单的业务行关键字段信息
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryJKBusItems(String headpk) throws BusinessException;
	/**
	 * 查询报销单主表关键字段信息
	 * 
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryBXBills() throws BusinessException;
	/**
	 * 查询报销单的业务行关键字段信息
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Map<String,String>> queryBXBusItems(String headpk) throws BusinessException;
	
	
	
}
