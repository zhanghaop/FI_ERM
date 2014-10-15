package nc.itf.erm.accountageana;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * 借款账龄分析查询业务接口<br>
 * 
 * @author liansg<br>
 * @since V60 2011-01-22<br>
 */
public interface ILoanAccountAgeAnalyzeBO {

	/**
	 * 借款账龄分析查询函数<br>
	 * 
	 * @param queryVO 查询条件VO<br>
	 * @return DataSet<br>
	 * @throws SmartException<br>
	 */
	public DataSet accountAgeQuery(ReportQueryCondVO queryVO, SmartContext context) throws SmartException;

}
