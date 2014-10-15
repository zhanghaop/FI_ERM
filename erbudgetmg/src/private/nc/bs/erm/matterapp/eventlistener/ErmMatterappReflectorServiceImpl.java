package nc.bs.erm.matterapp.eventlistener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.er.util.UFDoubleTool;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

/**
 * 费用申请单-会计平台重算服务实现
 * 
 * @author lvhj
 *
 */
public class ErmMatterappReflectorServiceImpl  implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		// 申请单pk分组重算pk,防止重复使用set
		Map<String, Set<String>> maRelationIdMap = new HashMap<String, Set<String>>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			//relateid关闭凭证ID为申请单pk_CLOSE
			String relationID = iter.next().getRelationID();
			String pk_mtapp_bill = relationID.substring(0, 20);
			Set<String> list = maRelationIdMap.get(pk_mtapp_bill);
			if(list == null){
				list = new HashSet<String>();
				maRelationIdMap.put(pk_mtapp_bill, list);
			}
			list.add(relationID);
		}
		// 查询联查信息对应的申请单
		IErmMatterAppBillQuery qryservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
		AggMatterAppVO[] aggvos = qryservice.queryBillByPKs(maRelationIdMap.keySet().toArray(new String[0]));
		
		// 包装待重算的数据
		List<AggMatterAppVO> resBillList = new ArrayList<AggMatterAppVO>();
		for (int j = 0; j < aggvos.length; j++) {
			AggMatterAppVO aggvo = aggvos[j];
			String pk = aggvo.getPrimaryKey();
			
			Set<String> relationidlist = maRelationIdMap.get(pk);
			
			for (String relationID : relationidlist) {
				if(relationID.length()>20){
					AggMatterAppVO clonevo = (AggMatterAppVO) aggvo.clone();
					// 重新设置主键，区分普通凭证与差额凭证
					MatterAppVO parentVO = clonevo.getParentVO();
					parentVO.setPrimaryKey(parentVO.getPrimaryKey()+"_CLOSE");
					MtAppDetailVO[] childrenVO = clonevo.getChildrenVO();
					for (int m = 0; m < childrenVO.length; m++) {
						MtAppDetailVO mtAppDetailVO = childrenVO[m];
						UFDouble rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getRest_amount());
						UFDouble org_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getOrg_rest_amount());
						UFDouble group_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getGroup_rest_amount());
						UFDouble global_rest_amount = UFDoubleTool.getDoubleValue(mtAppDetailVO.getGlobal_rest_amount());
						
						if(rest_amount.compareTo(UFDouble.ZERO_DBL)<0){
							// 余额小于0情况，按0处理
							rest_amount = UFDouble.ZERO_DBL;
							org_rest_amount = UFDouble.ZERO_DBL;
							group_rest_amount = UFDouble.ZERO_DBL;
							global_rest_amount = UFDouble.ZERO_DBL;
						}
						
						mtAppDetailVO.setOrig_amount(rest_amount.multiply(-1));
						mtAppDetailVO.setOrg_amount(org_rest_amount.multiply(-1));
						mtAppDetailVO.setGroup_amount(group_rest_amount.multiply(-1));
						mtAppDetailVO.setGlobal_amount(global_rest_amount.multiply(-1));
					}
					resBillList.add(clonevo);
				}else{
					resBillList.add(aggvo);
				}
			}
			
		}
		// 包装返回的重算数据
		List<FipExtendAggVO> result = new ArrayList<FipExtendAggVO>();
		for (AggMatterAppVO newaggvo : resBillList) {
			// 包装
			FipExtendAggVO tempvo = new FipExtendAggVO();
			tempvo.setBillVO(newaggvo);
			tempvo.setRelationID(newaggvo.getPrimaryKey());
			result.add(tempvo);
		}
		return result;
	}
}
