package nc.bs.erm.sharerule;

import nc.vo.erm.sharerule.AggshareruleVO;
import nc.vo.erm.sharerule.ShareruleDataVO;
import nc.vo.erm.sharerule.ShareruleVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;

public class SharerulePubUtil {

	/**
	 * 计算vo中分摊比例之和
	 * 
	 * @param vo
	 * @return
	 */
	public static UFDouble getTotalRatio(AggshareruleVO vo) {
		UFDouble totalRatio = UFDouble.ZERO_DBL;
		
		ShareruleVO parentvo = (ShareruleVO) vo.getParentVO();
		if (parentvo.getRule_type() == ShareruleConst.SRuletype_Ratio) {
			ShareruleDataVO[] datavos = (ShareruleDataVO[]) vo.getTableVO(vo
					.getTableCodes()[1]);
			for (ShareruleDataVO datavo : datavos) {
				if (datavo.getStatus() != VOStatus.DELETED) {
					UFDouble temp = datavo.getShare_ratio();
					if (temp == null) {
						totalRatio = totalRatio.add(UFDouble.ZERO_DBL);
					} else {
						totalRatio = totalRatio.add(temp);
					}
				}
			}
		}

		return totalRatio;
	}

}
