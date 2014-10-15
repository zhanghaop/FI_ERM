package nc.impl.erm.costshare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 会计平台查询单据使用
 * 
 * @author lvhj
 * 
 */
public class ErmCostshareReflectorServiceImpl implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		List<String> pklist = new ArrayList<String>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter
				.hasNext();) {
			pklist.add(iter.next().getRelationID());
		}
		ArrayList<FipExtendAggVO> rs = null;
		AggCostShareVO[] vos = NCLocator.getInstance()
				.lookup(IErmCostShareBillQuery.class)
				.queryBillByPKs(pklist.toArray(new String[pklist.size()]));
		if (!ArrayUtils.isEmpty(vos)) {
			rs = new ArrayList<FipExtendAggVO>();
			for (int i = 0; i < vos.length; i++) {
				FipExtendAggVO tempvo = new FipExtendAggVO();
				tempvo.setBillVO(vos[i]);
				tempvo.setRelationID(vos[i].getParentVO().getPrimaryKey());
				rs.add(tempvo);
			}
		}
		return rs;
	}

}
