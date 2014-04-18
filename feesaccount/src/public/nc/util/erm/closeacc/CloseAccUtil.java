package nc.util.erm.closeacc;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.org.IBatchCloseAccQryPubServicer;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.org.BatchCloseAccBookVO;
import nc.vo.pub.BusinessException;

public class CloseAccUtil {
	public static BatchCloseAccBookVO[] getEndAcc(String pk_group,
			String pk_org, String pk_accperiodmonth, String pk_accperiodscheme)
			throws BusinessException {
		BatchCloseAccBookVO[] ermCloseAccBook = NCLocator.getInstance().lookup(
				IBatchCloseAccQryPubServicer.class).queryCloseAccBookVOs(
				pk_group, BXConstans.ERM_MODULEID, pk_accperiodscheme,
				pk_accperiodmonth, new String[] { pk_org });
		return ermCloseAccBook;
	}
	
}
