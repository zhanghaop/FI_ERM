package nc.itf.erm.pub;

import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 费用报销单费用归集接口
 * 
 * @since 6.31
 * @version 2013
 * @author chenshuaia
 */
public interface ICombineResultForME {

	/**
	 * 营销费用归集处理 关联参数临时表查询归集数据
	 * 
	 * @param conditionTable
	 *            查询条件临时表
	 * @param elementTable
	 *            单据表体行对应核算要素
	 * @return ExpenseAccountVO 费用帐（汇总后的结果）
	 * 
	 * 
	 * @throws BusinessException
	 */
	public ExpenseAccountVO[] combineProcess(String conditionTable, String elementTable ) throws BusinessException;

	/**
	 * 取得来源单据可归集交易类型范围
	 * 
	 * @return String[] 交易类型pk集合
	 * @throws BusinessException
	 */
	public String[] getAllTranstypes() throws BusinessException;
	
	/**
	 * 根据日期范围 查询所有生效的费用帐id
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return String[]
	 * @throws BusinessException
	 */
	String[] getEarlyDataIDs(UFDate startDate, UFDate endDate) throws BusinessException;

	/**
	 * 根据费用帐id数组查询费用帐vo
	 * 
	 * @param ids
	 *            费用帐id数组
	 * @return ExpenseAccountVO[]
	 * @throws BusinessException
	 */
	ExpenseAccountVO[] getEarlyDataVOs(String[] ids) throws BusinessException;
}
