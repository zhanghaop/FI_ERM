package nc.itf.erm.prv;

import java.util.Map;

import nc.vo.pub.BusinessException;

/**
 * 费用公共业务服务
 * 
 * @author lvhj
 * 
 */
public interface IErmBsCommonService {

	/**
	 * 缓存有权限的组织
	 * 
	 * @param pk_user
	 * @param nodeCode
	 * @param pk_group
	 * @return
	 * @throws BusinessException
	 */
	public Map<String, String> getPermissonOrgMapCall(String pk_user, String nodeCode, String pk_group)
			throws BusinessException;

	/**
	 * 查询用户已审批、待审批单据
	 * 
	 * @param pk_user
	 *            用户pk，为空时，按当前登录用户进行查询
	 * @param tradeTypes
	 *            交易类型数组，为空时，则进行
	 * @param isApproved
	 *            true 表示已审批， false 表示待我审批
	 * @return 单据pk数组
	 * @throws BusinessException
	 */
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException;
}
