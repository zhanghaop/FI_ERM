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
 * �������޶��ߣ��ɱ����ĸ�����
 *
 * @author wangle
 *
 */
public class WfFyFilterMattCostcenter extends WfFyFilterCostcenter {
	public WfFyFilterMattCostcenter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000908")/*
																												 * "���óɱ����ĸ�����"
																												 */;
	}

	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		this.pfc = pfc;
		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> resacostcenterPKs = new HashSet<String>();// ���óɱ�����
		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();
		for (MtAppDetailVO detailvo : detailvos) {
			resacostcenterPKs.add(detailvo.getPk_resacostcenter());
			
		}
		
		if (resacostcenterPKs.isEmpty())
			return null;
		
		// ��ѯȫ���ɱ����ĵĸ�����
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
					throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "δ�ҵ��ɱ����ĸ�����"*/);
				}else{
					psnIDs.add(costcenters[i].getPk_principal());
				}
			}
		}
		// ��ѯ�����˶�Ӧ���û���Ϣ
		HashSet<String> userids = retrUser(psnIDs.toArray(new String[0]));
		

		HashSet<String> userList = getPfc().getUserList();

		// ɸѡ
		for (String centerUserId : userids) {
			if (userList.contains(centerUserId)) {
				userids.add(centerUserId);
			} else {
				// ���쳣
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}�����ڵ�ǰ{1}��"
																												 */);
			}
		}
		return userids;
	}
	
	
}
