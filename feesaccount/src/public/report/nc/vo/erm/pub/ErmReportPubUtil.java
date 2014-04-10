package nc.vo.erm.pub;

import nc.itf.fi.pub.Currency;
import nc.pubitf.uapbd.CurrencyRateUtilHelper;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.pub.BusinessException;

public class ErmReportPubUtil {


	/**
	 * ȡ��֯��λ����Ϣ(��ָ֯ȫ�֡����š�ҵ��Ԫ)
	 * 
	 * @param pk_org ��֯��������
	 * @return ��֯��λ����Ϣ����
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
