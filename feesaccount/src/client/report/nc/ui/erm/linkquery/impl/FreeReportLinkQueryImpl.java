package nc.ui.erm.linkquery.impl;

import nc.pub.smart.tracedata.ITraceDataOperator;
import nc.pub.smart.tracedata.TraceDataInterface;
import nc.ui.erm.linkquery.operator.LinkQueryBillOperator;

/**
 * ���ɱ�������ͨ����<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */

public class FreeReportLinkQueryImpl implements TraceDataInterface {

	public ITraceDataOperator[] provideTraceDataOperator() {
		// Ĭ�Ϸ��ص�������
		return new ITraceDataOperator[] { new LinkQueryBillOperator() };
	}

}

// /:~
