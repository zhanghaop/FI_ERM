package nc.ui.arap.bx.refbill;


import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pubapp.uif2app.model.IQueryService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

@SuppressWarnings("restriction")
public class MtAppQueryService implements IQueryService {

	public Object[] queryByWhereSql(String whereSql) throws Exception {
		return queryMaFor35(whereSql);
	}

	/**
	 * 专用：客户费用拉申请单查询过滤
	 * 
	 * @param whereSql
	 * @return
	 * @throws BusinessException
	 */
	private Object[] queryMaFor35(String whereSql) throws BusinessException {
		String fixedCon = " effectstatus=1 and close_status=2 and  pk_tradetype in (select djlxbm from er_djlx where matype=2 )";
		String condition = StringUtil.isEmpty(whereSql) ? fixedCon : whereSql
				+ " and " + fixedCon;
		AggMatterAppVO[] aggMas = NCLocator.getInstance().lookup(
				IErmMatterAppBillQuery.class).queryBillByWhere(condition);
		if (aggMas == null || aggMas.length < 1) {
			return null;
		}
		// 只过滤出未关闭的费用申请明细行
		for (AggMatterAppVO aggMa : aggMas) {
			List<MtAppDetailVO> newChildrenvos = new ArrayList<MtAppDetailVO>();
			for (MtAppDetailVO childvo : aggMa.getChildrenVO()) {
				if (childvo.getClose_status() == 2) {
					newChildrenvos.add(childvo);
				}
			}
			aggMa.setChildrenVO(newChildrenvos.toArray(new MtAppDetailVO[] {}));
		}
		return aggMas;

	}
	
	
}
