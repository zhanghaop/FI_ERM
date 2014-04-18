package nc.itf.erm.accountageana;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * ������������ѯҵ��ӿ�<br>
 * 
 * @author liansg<br>
 * @since V60 2011-01-22<br>
 */
public interface ILoanAccountAgeAnalyzeBO {

	/**
	 * ������������ѯ����<br>
	 * 
	 * @param queryVO ��ѯ����VO<br>
	 * @return DataSet<br>
	 * @throws SmartException<br>
	 */
	public DataSet accountAgeQuery(ReportQueryCondVO queryVO, SmartContext context) throws SmartException;

}
