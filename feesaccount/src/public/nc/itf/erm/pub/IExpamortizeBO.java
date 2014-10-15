package nc.itf.erm.pub;

import nc.itf.fipub.report.IReportQueryCond;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;

public interface IExpamortizeBO {
	/**
	 * 功能：查询摊销表
	 * 返回结果为DataSet
	 */
	public DataSet queryExpamortize(IReportQueryCond queryVO, SmartContext context) throws SmartException;
}
