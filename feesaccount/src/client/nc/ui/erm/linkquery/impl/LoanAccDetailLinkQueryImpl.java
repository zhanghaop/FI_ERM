package nc.ui.erm.linkquery.impl;


/**
 * ����������鵥��<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.LinkQueryBillOperator;
import nc.ui.erm.linkquery.operator.LinkQueryVoucherOperator;


public class LoanAccDetailLinkQueryImpl implements TraceDataInterface {

	public ITraceDataOperator[] provideTraceDataOperator() {
		return new ITraceDataOperator[] { new LinkQueryBillOperator(),
				new LinkQueryVoucherOperator() };
	}
}


