package nc.plugin.erm.expamortize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 摊销信息查业务插件实体实现</br>
 * 主要目的是通过会计平台的单据关联号和相关单据类型来获得业务单据实体。
 * @see nc.pubitf.fip.external.IBillReflectorService
 * @author chenshuaia
 *
 */
public class ErmExpamortizeReflectorServiceImpl implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(Collection<FipRelationInfoVO> relationvos)
			throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		ArrayList<FipExtendAggVO> result = null;
		List<String> pklist = new ArrayList<String>();
		String preriod = null;
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			
			String[] values = iter.next().getRelationID().split("_");
			pklist.add(values[0]);
			preriod = values[1];
		}
		
		AggExpamtinfoVO[] aggVos = NCLocator.getInstance().lookup(IExpAmortizeinfoQuery.class)
				.queryByPks(pklist.toArray(new String[]{}), preriod);

		if (!ArrayUtils.isEmpty(aggVos)) {
			result = new ArrayList<FipExtendAggVO>();
			for (int i = 0; i < aggVos.length; i++) {
				FipExtendAggVO tempvo = new FipExtendAggVO();
				tempvo.setBillVO(aggVos[i]);
				tempvo.setRelationID(aggVos[i].getParentVO().getPrimaryKey() + "_" + preriod);
				result.add(tempvo);
			}
		}
		return result;
	}

}
