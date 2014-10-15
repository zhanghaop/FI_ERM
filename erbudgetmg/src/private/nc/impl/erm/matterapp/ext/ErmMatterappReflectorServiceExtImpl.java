package nc.impl.erm.matterapp.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.bs.erm.matterapp.ext.MtappVOGroupHelper;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.pubitf.fip.external.IBillReflectorService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

/**
 * 费用申请单-会计平台重算服务实现
 * 
 * @author lvhj
 *
 */
public class ErmMatterappReflectorServiceExtImpl  implements IBillReflectorService {

	@Override
	public Collection<FipExtendAggVO> queryBillByRelations(
			Collection<FipRelationInfoVO> relationvos) throws BusinessException {
		if (null == relationvos || relationvos.size() == 0) {
			return null;
		}
		// 申请单pk分组重算pk
		Map<String, List<String>> maRelationIdMap = new HashMap<String, List<String>>();
		for (Iterator<FipRelationInfoVO> iter = relationvos.iterator(); iter.hasNext();) {
			//按relateid格式为申请单pk+利润中心pk+日期（yy-mm-dd）,关闭凭证没有日期
			String relationID = iter.next().getRelationID();
			String pk_mtapp_bill = relationID.substring(0, 20);
			List<String> list = maRelationIdMap.get(pk_mtapp_bill);
			if(list == null){
				list = new ArrayList<String>();
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
			MatterAppVO parentVO = aggvo.getParentVO();
			String pk = parentVO.getPrimaryKey();
			
			List<String> relationidlist = maRelationIdMap.get(pk);
			
			for (String relationID : relationidlist) {
				String pk_pcorg = relationID.substring(20, 40);
				if(relationID.length() == 40){
					// 关闭的凭证重算
					List<AggMatterAppVO> closeVOs = MtappVOGroupHelper.getCloseVOs(pk_pcorg, aggvo);
					resBillList.addAll(closeVOs);
				}else{
					// 分期分摊凭证重算
					UFDate billdate = new UFDate(relationID.substring(40));
					Map<String, List<AggMatterAppVO>> groupPcorgMap = MtappVOGroupHelper.groupPcorgVOs(pk_pcorg, billdate, aggvo);
					resBillList.addAll(groupPcorgMap.get(pk_pcorg));
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
