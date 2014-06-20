package nc.bs.er.callouter;

import nc.bs.erm.util.CacheUtil;
import nc.itf.cmp.busi.IBusi4CMPAutoSettleServcie;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

/**
 * 自动签字实现，不能有后台类出现，处于public中；
 * 插件放到后台，插件反射报错，这里提到后台
 * @author chenshuaia
 *
 */
public class ErmCmpAutoSettleServiceImpl implements IBusi4CMPAutoSettleServcie {
	/**
	 * 自动结算
	 */
	public boolean isAutoSettle(String pk_group, String pk_tradetype, SettlementAggVO... settlementAggVOs) throws BusinessException {
		boolean isAutoSettle = false;
		DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, "pk_group = '" + pk_group + "' and djlxbm = '" + pk_tradetype + "'");
		if (vos == null || vos.length == 0) {
			return false;
		} else {
			isAutoSettle = vos[0].getAutosettle() == null ? false : vos[0].getAutosettle().booleanValue();
		}
		return isAutoSettle;
	}

	public boolean isSuportBilltype(String billtype) throws BusinessException {
		if (billtype != null && (billtype.equals(BXConstans.BX_DJLXBM) || billtype.equals(BXConstans.JK_DJLXBM))) {
			return true;
		}
		return false;
	}
}
