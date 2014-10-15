package nc.ui.erm.closeaccount.action;

import nc.bs.uif2.BusinessExceptionAdapter;
import nc.ui.er.util.BXUiUtil;
import nc.ui.org.closeaccbook.action.AntiCloseAccBookAction;
import nc.vo.erm.closeacc.ErmGLCloseAccListener;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 报销反关帐校验
 * @author chendya
 *
 */
@SuppressWarnings("serial")
public class ErmAntiCloseAccBookAction extends AntiCloseAccBookAction {

	@Override
	protected void validate(Object value) {
		if (value != null && value instanceof nc.vo.org.CloseAccBookVO) {
			nc.vo.org.CloseAccBookVO vo = (nc.vo.org.CloseAccBookVO) value;
			final String pk_accperiodmonth = vo.getPk_accperiodmonth();
			try {
				UFDate startDate = new UFDate(BXUiUtil.getColValue("bd_accperiodmonth", "begindate", "pk_accperiodmonth", pk_accperiodmonth));
				ErmGLCloseAccListener listener = new ErmGLCloseAccListener();
				final String year = "" + startDate.getYear();
				final int iMonth = startDate.getMonth();
				String month = "" + iMonth;
				if (iMonth < 10) {
					month = "0" + month;
				}

				listener.checkUnCloseAcc(year, month, vo.getPk_org());
			} catch (BusinessException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}
	}

}
