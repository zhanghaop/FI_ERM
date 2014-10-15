package nc.itf.erm.pub;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;

/**
 * <p>
 * TODO ���������ʱ��ѯ����������ѯ�ӿ��ࡣ
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-23 ����09:01:56
 */
public interface ILoanBalanceBO {
	
	public DataSet queryLoanBalance(ReportQueryCondVO queryVO, SmartContext context) throws SmartException;
	
	/**
	 * ���ܣ������ܱ� 
	 * �ṩ��web�����ʱ��ѯ
	 */ 
	public Object queryLoanBalanceResultSet(ReportQueryCondVO queryVO, SmartContext context, ResultSetProcessor processor) throws BusinessException;
	
}
