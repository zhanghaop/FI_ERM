package nc.itf.erm.expensetrend;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;

/**
 * <p>
 * TODO 报销管理帐表查询，费用明细账查询接口类。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author luolch
 * @version V6.0
 * @since V6.0 创建时间：2010-11-23 下午09:01:56
 */
public interface IExpenseTrendBO {
	
	/**
	 * 功能：查询费用明细  返回结果为DataSet
	 */
	public DataSet queryExpenseTrend(ExpTrendQryVO queryVO , SmartContext context) throws SmartException;

}
