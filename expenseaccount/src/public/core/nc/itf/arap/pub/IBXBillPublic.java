package nc.itf.arap.pub;

import java.util.Map;

import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;
/**
 *	借款报销类单据对外提供的业务处理接口. 
 */
public interface IBXBillPublic {
	/**
	 * 	 
	* 现金流平台通过此接口回写清账状态.
	*  在单据各项处理时维护，单据审核后核销状态记录为“未清账”.
	* 单据被全部核销后记录为“已清账”，单据的其他处理对此字段不作处理.	
	 */
	public void updateQzzt(JKBXVO[] vos)throws BusinessException;

	/**
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * 
	 * 正式保存单据，将进行业务校验，并调用其他模块接口
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException;
	/**
	 * @param vos 要暂存单据的聚合VO数组
	 * @return
	 * @throws BusinessException
	 * 
	 * 暂存单据，不进行任何业务校验，不调用其他模块接口
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException;
	
	/**
	 * @param vos
	 * @return
	 * @throws BusinessException
	 * 
	 * 更新单据
	 */
	public JKBXVO[] update(JKBXVO[] vos)throws BusinessException;
	
	public JKBXHeaderVO updateHeader(JKBXHeaderVO header,String[] fields)throws BusinessException;
	
	/**
	 * @param bills
	 * @return
	 * @throws BusinessException
	 * 
	 * 删除单据
	 */
	public MessageVO[] deleteBills(JKBXVO[] bills)throws BusinessException;
	

	/**
	 * cmp导入数据后进行数据的处理
	 */
	public void updateDataAfterImport(String[] pk_tradetype)throws BusinessException;
	
//	/**
//	 * 为项目预算提供的查询服务接口
//	 * 查询生效状态的报销单(业务场景：先报销，后手动执行项目预算)
//	 * @param pk_group 所属集团
//	 * @param pk_project 项目主键
//	 * @param pk_billtypes 交易类型主键数组
//	 * @return 项目预算执行VO[]
//	 * @throws BusinessException
//	 */
//	public IBudgetExecVO[] queryBxBill4ProjBudget(String pk_group, String pk_project, String[] pk_billtypes) throws BusinessException;
//	
//	/**
//	 * 为项目预算提供的查询服务接口
//	 * <p>按交易类型编码查询生效状态的报销单
//	 * <p>(业务场景：先报销，后手动执行项目预算)
//	 * @param pk_group 所属集团
//	 * @param pk_project 项目主键
//	 * @param djlxbms 交易类型编码s
////	 * @return 项目预算执行VO[]
////	 * @throws BusinessException
////	 */
////	public IBudgetExecVO[] queryBxBill4ProjBudget2(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
//	
	/**
	 * 获取指定项目所有审批通过的报销单的报销总金额之和<br>
	 * 场景：项目决算单获取成本时，要获取该项目所有审批通过的报销单的报销总金额之和<br>
	 * 触发条件：获取成本 按钮
	 * @param pk_group 集团
	 * @param pk_project 项目
	 * @return Map<币种，金额>
	 * @throws BusinessException
	 */
	public Map<String,UFDouble> queryAmount4ProjFinal(String pk_group, String pk_project) throws BusinessException;
	
	/**
	 * 为项目预算提供的查询服务接口
	 * <p>项目预算发布时收取审核通过报销单的执行数
	 * @param pk_group 集团
	 * @param pk_project 项目pk
	 * @param djlxbms 单据类型集合
	 * @return 报销聚合VO[]
	 */
	public JKBXVO[] queryBxApproveBill4ProjBudget(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
	
	/**
	 * 为项目预算提供的查询服务接口
	 * <p>项目预算发布时收取报销单的执行数（不包括暂存，删除数据的所有报销）
	 * @param pk_group 集团
	 * @param pk_project 项目pk
	 * @param djlxbms 单据类型集合
	 * @return 报销聚合VO[]
	 */
	public JKBXVO[] queryBxBill4ProjBudget(String pk_group,String pk_project,String[] djlxbms) throws BusinessException;
}
