package nc.bs.er.wfengine.ext;

import java.util.HashSet;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.resa.costcenter.ICostCenterPubService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 审批流限定者，成本中心负责人
 *
 * @author wangle
 *
 */
public class WfFyFilterMattCostcenter extends WfFyFilterCostcenter {
	public WfFyFilterMattCostcenter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000908")/*
																												 * "费用成本中心负责人"
																												 */;
	}

	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		this.pfc = pfc;
		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> resacostcenterPKs = new HashSet<String>();// 费用成本中心
		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();
		for (MtAppDetailVO detailvo : detailvos) {
			resacostcenterPKs.add(detailvo.getPk_resacostcenter());
			
		}
		
		if (resacostcenterPKs.isEmpty())
			return null;
		
		// 查询全部成本中心的负责人
		ICostCenterPubService service = NCLocator.getInstance().lookup(
				ICostCenterPubService.class);
		CostCenterVO[] costcenters = service.queryCostCenterVOByPks(
				resacostcenterPKs.toArray(new String[0]),
				new String[] { CostCenterVO.PK_COSTCENTER,
						CostCenterVO.PK_PRINCIPAL }, true);
		if (costcenters == null || costcenters.length == 0) {
			return null;
		}
		HashSet<String> psnIDs = new HashSet<String>();
		for (int i = 0; i < costcenters.length; i++) {
			if(costcenters[i]!=null){
				if(costcenters[i].getPk_principal() == null){
					throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "未找到成本中心负责人"*/);
				}else{
					psnIDs.add(costcenters[i].getPk_principal());
				}
			}
		}
		// 查询负责人对应的用户信息
		HashSet<String> userids = retrUser(psnIDs.toArray(new String[0]));
		

		HashSet<String> userList = getPfc().getUserList();

		// 筛选
		for (String centerUserId : userids) {
			if (userList.contains(centerUserId)) {
				userids.add(centerUserId);
			} else {
				// 抛异常
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}
		return userids;
	}
	
	
}
