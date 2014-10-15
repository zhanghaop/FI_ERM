package nc.vo.erm.pub;

import nc.itf.fi.pub.Currency;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.pub.BusinessException;

public class ErmReportPubUtil {


	/**
	 * 取组织本位币信息(组织指全局、集团、业务单元)
	 * 
	 * @param pk_org 组织主键数组
	 * @return 组织本位币信息数组
	 * @throws BusinessException 
	 */
	public static CurrtypeVO[] getLocalCurrencyByOrgID(String[] pk_orgs)
			throws BusinessException {
		if (pk_orgs == null || pk_orgs.length == 0) {
			return new CurrtypeVO[0];
		}

		CurrtypeVO[] currtypeVOs = new CurrtypeVO[pk_orgs.length];
		for (int i = 0; i < pk_orgs.length; i++) {
			currtypeVOs[i] = Currency.getCurrInfo(CurrencyRateUtilHelper.getInstance()
					.getLocalCurrtypeByOrgID(pk_orgs[i]));
		}
		return currtypeVOs;
	}
	
	
}

// /:~
