package nc.bs.erm.costshare.eventlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.ext.common.CostshareVOGroupHelper;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 费用结转单会计平台重算实现类，包括分期分摊场景
 * 
 * 合生元专用
 * 
 * @author lvhj
 * 
 */
public class ErmCostshareReflectorServiceImplExt implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		List<String> pklist = new ArrayList<String>();
		
		// 主表pk分组重算pk
		Map<String, List<String>> RelationIdMap = new HashMap<String, List<String>>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			String relationID = iter.next().getRelationID();
			String pk_costshare = relationID;
			if(relationID.length()>20){
				// 分期分摊凭证，按relateid格式为主表pk+利润中心pk+日期（yy-mm-dd）
				pk_costshare = relationID.substring(0, 20);
				List<String> list = RelationIdMap.get(pk_costshare);
				if(list == null){
					list = new ArrayList<String>();
					RelationIdMap.put(pk_costshare, list);
				}
				list.add(relationID);
			}
			pklist.add(pk_costshare);
		}
		// 根据pks查询结转单
		ArrayList<FipExtendAggVO> rs = null;
		AggCostShareVO[] vos = NCLocator.getInstance()
				.lookup(IErmCostShareBillQuery.class)
				.queryBillByPKs(pklist.toArray(new String[pklist.size()]));
		// 包装重算数据
		List<AggCostShareVO> resBillList = new ArrayList<AggCostShareVO>();
		for (int j = 0; j < vos.length; j++) {
			AggCostShareVO aggvo = vos[j];
			String pk = aggvo.getParentVO().getPrimaryKey();
			if(RelationIdMap.containsKey(pk)){
				// 分期分组包装数据
				List<String> relationidlist = RelationIdMap.get(pk);
				for (String relationID : relationidlist) {
					String pk_pcorg = relationID.substring(20, 40);
					// 分期分摊凭证重算
					UFDate billdate = new UFDate(relationID.substring(40));
					Map<String, List<AggCostShareVO>> groupPcorgMap = CostshareVOGroupHelper.groupPcorgVOs(pk_pcorg, billdate, aggvo);
					resBillList.addAll(groupPcorgMap.get(pk_pcorg));
				}
			}else{
				// 直接返回结转单
				resBillList.add(aggvo);
			}
		}
		
		if (!resBillList.isEmpty()) {
			rs = new ArrayList<FipExtendAggVO>();
			for (AggCostShareVO vo : resBillList) {
				FipExtendAggVO tempvo = new FipExtendAggVO();
				tempvo.setBillVO(vo);
				tempvo.setRelationID(vo.getParentVO().getPrimaryKey());
				rs.add(tempvo);
			}
		}
		return rs;
	}

}
