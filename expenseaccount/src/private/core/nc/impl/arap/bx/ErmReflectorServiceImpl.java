package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nc.bs.arap.bx.FipUtil;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.utils.ArrayUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

public abstract class ErmReflectorServiceImpl implements IBillReflectorService {

	protected abstract List<JKBXVO> getBusiBill(String[] keys)throws BusinessException;

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			list.add(iter.next().getRelationID());
		}
		List<JKBXVO> bills = getBusiBill(list.toArray(ArrayUtil.newEmptyArrays(list)));
		List<FipExtendAggVO> ret = new ArrayList<FipExtendAggVO>(bills.size());
		for (JKBXVO JKBXVO : bills) {
			FipExtendAggVO vo = new FipExtendAggVO();
			JKBXVO = ErVOUtils.prepareBxvoHeaderToItemClone(JKBXVO);
			
			AggregatedValueObject object = new FipUtil().addOtherInfo(JKBXVO);
			vo.setBillVO(object);
			vo.setRelationID(JKBXVO.getParentVO().getPrimaryKey());
			ret.add(vo);
		}
		return ret;
	}
}
