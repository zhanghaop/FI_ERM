package nc.ui.erm.linkquery.impl;


/**
 * 费用明细账联查单据<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.ExpenseLinkQueryVoucherOperator;
import nc.ui.erm.linkquery.operator.ExpenseLinkQuerybillOperator;


public class ExpenseDetailLinkQueryImpl implements TraceDataInterface {

	public ITraceDataOperator[] provideTraceDataOperator() {
		return new ITraceDataOperator[] { new ExpenseLinkQuerybillOperator(),
				new ExpenseLinkQueryVoucherOperator() };
	}
}


