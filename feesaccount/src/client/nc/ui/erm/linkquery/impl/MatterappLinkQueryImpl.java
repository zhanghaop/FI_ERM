package nc.ui.erm.linkquery.impl;


/**
 * ������ϸ�����鵥��<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.MatterappLinkQuerybillOperator;


public class MatterappLinkQueryImpl implements TraceDataInterface {

	public ITraceDataOperator[] provideTraceDataOperator() {
		return new ITraceDataOperator[] { new MatterappLinkQuerybillOperator()};
	}
}


