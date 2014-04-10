package nc.itf.erm.pub;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;

/**
 * <p>
 * TODO 报销管理帐表查询，借款余额表查询接口类。
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
public interface ILoanBalanceBO {
	
	public DataSet queryLoanBalance(ReportQueryCondVO queryVO, SmartContext context) throws SmartException;
	
	/**
	 * 功能：借款汇总表 
	 * 提供给web报销帐表查询
	 */ 
	public Object queryLoanBalanceResultSet(ReportQueryCondVO queryVO, SmartContext context, ResultSetProcessor processor) throws BusinessException;
	
}
