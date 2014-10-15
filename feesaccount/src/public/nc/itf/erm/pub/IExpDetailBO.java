package nc.itf.erm.pub;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;

/**
 * <p>
 * TODO ���������ʱ��ѯ��������ϸ�˲�ѯ�ӿ��ࡣ
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
public interface IExpDetailBO {
	
	/**
	 * ���ܣ���ѯ������ϸ  ���ؽ��ΪDataSet
	 */
	public DataSet queryExpenseDetail(ReportQueryCondVO queryVO , SmartContext context) throws SmartException;
	
	/**
	 * ���ܣ���ѯ������ϸ ���ؽ��ΪMemoryResultSet �ṩ��web�����ʱ��ѯ
	 */ 
	public Object queryExpenseDetailResultSet(ReportQueryCondVO queryVO, SmartContext context, ResultSetProcessor processor) throws BusinessException;
}
