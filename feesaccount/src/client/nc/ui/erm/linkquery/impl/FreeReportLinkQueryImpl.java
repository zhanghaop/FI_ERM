package nc.ui.erm.linkquery.impl;

import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.LinkQueryBillOperator;

/**
 * 自由报表联查通配器<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */

public class FreeReportLinkQueryImpl implements TraceDataInterface {

	public ITraceDataOperator[] provideTraceDataOperator() {
		// 默认返回单据联查
		return new ITraceDataOperator[] { new LinkQueryBillOperator() };
	}

}

// /:~
