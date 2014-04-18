package nc.vo.er.util;

import nc.bs.bd.currinfo.ExchangeRateCache;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

public class ErmBillCalUtil {


	/**
	 * 取组织 集团 全局 对原币汇率
	 * 
	 * @param pk_org
	 * @param pk_group
	 * @return
	 */
	public static UFDouble[] getRate(String pk_currtype, String pk_org, String pk_group, UFDate date, String pk_billtype) {
		UFDouble[] rates = new UFDouble[3];
		try {
			rates[0] = ErmBillCalUtil.getOrgRate(pk_currtype, pk_group, pk_org, date, pk_billtype);
			rates[1] = ErmBillCalUtil.getGroupRate(pk_currtype, pk_group, pk_org, date, pk_billtype);
			rates[2] = ErmBillCalUtil.getGlobalRate(pk_currtype, pk_org, date);
		} catch (BusinessException e) {
			ExceptionUtils.wrappException(e);
		}
		return rates;
	}

	public static UFDouble getOrgRate(String pk_currtype, String pk_group, String pk_org, UFDate date, String pk_billtype) throws BusinessException {
//		int rateType = getRateType(pk_billtype, pk_group);
		UFDouble outrate = Currency.getRate(pk_org, pk_currtype, date);
		return outrate == null ? UFDouble.ZERO_DBL : outrate;
	}

	public static UFDouble getGroupRate(String pk_currtype, String pk_group, String pk_org, UFDate date, String pk_billtype) throws BusinessException {
		return Currency.getGroupRate(pk_org,pk_group, pk_currtype, date);
	}

	public static UFDouble getGlobalRate(String pk_currtype, String pk_org, UFDate date) throws BusinessException {
		return Currency.getGlobalRate(pk_org, pk_currtype, date);
	}
	/**
	 * 获取中间价
	 * 
	 * @param pk_billtype
	 * @param pk_group
	 * @return
	 */
	public static int getRateType(String pk_billtype, String pk_group) throws BusinessException {
		String ratePara = SysInit.getParaString(pk_group, "BD001");
		int rateType = 0;
		if (BXConstans.MiddlePrice.equals(ratePara)) {
			rateType = ExchangeRateCache.RATE_TYPE_MIDDLE;
		}

		return rateType;
	}
}
