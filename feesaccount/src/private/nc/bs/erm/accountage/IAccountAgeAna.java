package nc.bs.erm.accountage;

import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * 账龄分析通用适配接口<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
public interface IAccountAgeAna {

	/**
	 * 查询账龄分析结果<br>
	 * 
	 * @param queryVO ReportQueryCondVO<br>
	 * @return MemoryResultSet<br>
	 * @throws BusinessException<br>
	 */
	public MemoryResultSet getAccountAgeAnaResult(ReportQueryCondVO queryVO)
			throws BusinessException;

}
