package nc.arap.mobile.itf;

import java.util.List;
import java.util.Map;

import nc.vo.pub.BusinessException;
/**
 * clf、jtf、txf三种基本类型报销单公共接口
 */

public interface IErmMobileCtrl {
	public String addJkbx(Map<String, Object> valuemap,String djlxbm) throws BusinessException;
	
	
	/**
	 * 按pk查询报销单
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public Map<String,Object> getJkbxCard(String headpk) throws BusinessException;
	
	
	/**
	 * 根据PK删除报销单
	 * 
	 * @param headpk
	 * @throws BusinessException
	 */
	public String deleteJkbx(String headpk,String userid) throws BusinessException;
	
	public String commitJkbx(String userid,String headpk) throws BusinessException;

	
	/**
	 * 分页查询当前用户未完成\已完成的单据
	 * 
	 * @param userid
	 * @param flag 审批状态标志0-未完成  1-已完成 
	 * @return 交易类型名称分组的报销单属性值
	 * @throws BusinessException
	 */
	public Map<String, List<Map<String, String>>> getBXHeadsByUser(String userid,String flag,String startline,String pagesize,String pks)
	throws BusinessException ;
	/**
	 * 查询当前用户待审批单据
	 * 
	 * @param userid
	 * @return 借款报销单
	 * @throws BusinessException
	 */
	public Map<String,List<Map<String, String>>> getBXApproveBillByUser(String userid,String flag)
	throws BusinessException ;
	
	/**
	 * 查询当前用户已审批单据
	 * 
	 * @param userid
	 * @return 借款报销单
	 * @throws BusinessException
	 */
	public Map<String,Map<String, Map<String, String>>> getBXApprovedBillByUser(String userid,String flag)
			throws BusinessException;
	
	/**
	 * 审批单据
	 * 
	 * @param pks
	 * @return 借款报销单
	 * @throws BusinessException
	 */
	String auditBXBillByPKs(String[] pks,String userid) throws Exception;
	/**

	    * 按pk查询审批流

	    * 

	    * @param headpk

	    * @throws BusinessException

	    */

	   public Map<String,Map<String,String>> loadBxdWorkflownote(String pk_jkbx,String djlxbm) throws BusinessException;
	   
	   /**
		 * 根据条件查询单据
		 * 
		 * @param pks
		 * @return 所有类型单据
		 * @throws BusinessException
		 */
	   public Map<String,Map<String,String>> getBxdByCond(Map condMap,String userid) throws BusinessException;
	   
	   /**
		 * 查询费用项目
		 * 
		 */
	   public Map<String, Map<String, String>> loadExpenseTypeInfoString(
			String userid) throws BusinessException;

	   /**
		 * 取消审批
		 * 
		 */
		public String unAuditbxd(String pk_jkbx,String userid) throws BusinessException;
		/**
		 * 当前用户待审批单据数
		 * 
		 */
		public String get_unapproved_bxdcount(String userid) throws BusinessException;
		
//	/**
//	 * 查询当前用户全部未审批通过的报销单,按djlxbm分组
//	 * 
//	 * @param userid
//	 * @return 交易类型名称分组的报销单属性值
//	 * @throws BusinessException
//	 */
//	public Map<String, Map<String, String>> getUnApprovedBXHeadsByUser(String userid)
//	throws BusinessException ;
//	
//	/**
//	 * 查询报销单主表信息列表,本周、本月、近三个月
//	 * 
//	 * @return
//	 * @throws BusinessException
//	 */
//	public Map<String, Map<String, String>> queryBXHeads(String userid,String querydate) throws BusinessException ;
//	
	
}
