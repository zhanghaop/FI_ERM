package nc.ui.erm.billpub.remote;

import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.ui.er.util.BXUiUtil;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.service.ServiceVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.ICalendar;
import nc.vo.pub.lang.UFDate;

/**
 *　返回组织的期初日期　
 * 
 */
public class QcDateCall extends AbstractCall implements IRemoteCallItem {

	public static String QcDate_Org_PK_ = "QcDate_Org_PK_";
	
	public static String QcDate_Date_PK_ = "QcDate_Date_PK_";

	public QcDateCall() {
		super();
	}

	@Override
	public ServiceVO getServcallVO() {
		final String pk_psndoc = BXUiUtil.getPk_psndoc();
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.prv.IBXBillPrivate");
		callvo.setMethodname("queryDefaultOrgAndQcrq");
		callvo.setParamtype(new Class[] { String.class });
		callvo.setParam(new Object[] { pk_psndoc });
		return callvo;
	}

	@SuppressWarnings("unchecked")
	public void handleResult(Map<String, Object> datas)
			throws BusinessException {
		final Map<String, String> map = (Map<String, String>) datas.get(callvo.getCode());
		if (map != null && map.size() != 0) {
			String pk_org = map.keySet().iterator().next();
			String strStartDate = map.get(pk_org);
			WorkbenchEnvironment.getInstance().putClientCache(QcDate_Org_PK_ + BXUiUtil.getPK_group(), pk_org);
			WorkbenchEnvironment.getInstance().putClientCache(QcDate_Date_PK_ + pk_org, new UFDate(strStartDate.toLowerCase(), ICalendar.BASE_TIMEZONE));
		}
	}

}
