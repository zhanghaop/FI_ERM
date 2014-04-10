package nc.ui.erm.linkquery.impl;

import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.ExpamDetailLinkQueryOperator;
import nc.ui.erm.linkquery.operator.ExpamortizeLinkQueryOperator;

public class ExpamortizeLinkQueryImpl implements TraceDataInterface {
	@Override
	public ITraceDataOperator[] provideTraceDataOperator() {
		return new ITraceDataOperator[] { new ExpamortizeLinkQueryOperator(),
				new ExpamDetailLinkQueryOperator() };
	}
}
