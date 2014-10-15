package nc.itf.erm.pub;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.data.DataSet;
import nc.pub.smart.exception.SmartException;
import nc.vo.fipub.report.ReportQueryCondVO;

/**
 * <p>
 *  费用申请明细帐表查询，费用明细账查询接口类。
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author luolch
 * @version V6.0
 * @since V6.0 创建时间：2010-11-23 下午09:01:56
 */
public interface IMatterappDataBO {
	
	/**
	 * 功能：费用申请明细帐表查询  返回结果为DataSet
	 */
	public DataSet queryMatterappData(ReportQueryCondVO queryVO , SmartContext context) throws SmartException;
	
}
