package nc.util.erm.costshare;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.erm.util.ErmDjlxConst;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.costshare.CostShareYsControlVO;
import nc.vo.erm.costshare.ext.CostShareYsControlVOExt;
import nc.vo.pub.BusinessException;

/**
 * 结转单预算工具
 * 
 * @author chenshuaia
 * 
 */
public class ErCostBudgetUtil {
	public static IFYControl[] getCostControlVOByCSVO(AggCostShareVO[] csVo, String pk_user) throws BusinessException {
		IFYControl[] items;
		// 查询费用结转主vo
		List<CostShareYsControlVO> itemList = new ArrayList<CostShareYsControlVO>();

		for (int i = 0; i < csVo.length; i++) {
			AggCostShareVO aggvo = (AggCostShareVO) csVo[i];
			CostShareVO headvo = (CostShareVO) aggvo.getParentVO();
			CShareDetailVO[] dtailvos = (CShareDetailVO[]) aggvo.getChildrenVO();

			boolean isAdjust = ErmDjlxCache.getInstance().isNeedBxtype(headvo.getPk_group(), headvo.getDjlxbm(), ErmDjlxConst.BXTYPE_ADJUST);

			for (int j = 0; j < dtailvos.length; j++) {
				// 转换生成controlvo
				CShareDetailVO detailvo = dtailvos[j];
				CostShareYsControlVOExt cscontrolvo = new CostShareYsControlVOExt(headvo, detailvo);
				if (isAdjust) {
					// 调整单情况，需要根据分摊明细行的预算占用日期进行预算控制
					cscontrolvo.setYsDate(detailvo.getYsdate());
				}
				itemList.add(cscontrolvo);
			}
		}
		items = itemList.toArray(new IFYControl[0]);
		return items;
	}
}
