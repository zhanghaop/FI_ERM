package nc.itf.erm.pub;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * <p>
 *  ����������ϸ�ʱ��ѯ��������ϸ�˲�ѯ�ӿ��ࡣ
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author luolch
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-11-23 ����09:01:56
 */
public interface IMatterappDataBO {
	
	/**
	 * ���ܣ�����������ϸ�ʱ��ѯ  ���ؽ��ΪDataSet
	 */
	public DataSet queryMatterappData(ReportQueryCondVO queryVO , SmartContext context) throws SmartException;
	
}
