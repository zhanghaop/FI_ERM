package nc.itf.erm.pub;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;

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
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-23 下午09:01:56
 */
public interface IExpDetailBO {
	
	/**
	 * 功能：查询费用明细  返回结果为DataSet
	 */
	public DataSet queryExpenseDetail(ReportQueryCondVO queryVO , SmartContext context) throws SmartException;
	
	/**
	 * 功能：查询费用明细 返回结果为MemoryResultSet 提供给web报销帐表查询
	 */ 
	public Object queryExpenseDetailResultSet(ReportQueryCondVO queryVO, SmartContext context, ResultSetProcessor processor) throws BusinessException;
}
