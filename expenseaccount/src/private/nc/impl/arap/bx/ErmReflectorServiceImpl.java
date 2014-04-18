package nc.impl.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		
		/**
		 * 记录单据pk和凭证标志:同一个pk为一组
		 */
		Map<String,List<String>> map =  new HashMap<String, List<String>>();
		List<String> voutertag = null;
 		for(int i=0 ; i<list.size() ;i++){
			String[] value = list.get(i).split("_");
			if(map.get(value[0])==null){
				voutertag = new ArrayList<String>();
			}
			voutertag.add(value[1]);
			map.put(value[0], voutertag);
		}
		
		List<JKBXVO> bills = getBusiBill(list.toArray(ArrayUtil.newEmptyArrays(list)));
		List<FipExtendAggVO> ret = new ArrayList<FipExtendAggVO>(bills.size());
		for (JKBXVO JKBXVO : bills) {
			List<String> taglist = map.get(JKBXVO.getParentVO().getPrimaryKey());
			for (String tag : taglist) {

				FipExtendAggVO vo = new FipExtendAggVO();
				JKBXVO = ErVOUtils.prepareBxvoHeaderToItemClone(JKBXVO);

				AggregatedValueObject object = new FipUtil()
						.addOtherInfo(JKBXVO);
				vo.setBillVO(object);

				vo.setRelationID(JKBXVO.getParentVO().getPrimaryKey() + "_"+ tag);
				ret.add(vo);
			}
		}
		return ret;
	}
}
