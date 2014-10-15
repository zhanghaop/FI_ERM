package nc.bs.erm.accountage;

import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * �������ͨ������ӿ�<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
public interface IAccountAgeAna {

	/**
	 * ��ѯ����������<br>
	 * 
	 * @param queryVO ReportQueryCondVO<br>
	 * @return MemoryResultSet<br>
	 * @throws BusinessException<br>
	 */
	public MemoryResultSet getAccountAgeAnaResult(ReportQueryCondVO queryVO)
			throws BusinessException;

}
