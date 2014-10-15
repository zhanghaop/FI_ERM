package nc.bs.er.wfengine.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.pub.pf.ParticipantFilterContext;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * 费用用单位负责人 :  费用申请单使用
 * 审批流参与者　过滤器：
 * @author wangled
 *
 */

public class WfFyFilterMattAssumeOrg extends WfFyFilterCorp {
	
	public WfFyFilterMattAssumeOrg() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000907");//** "费用承担单位负责人"*/
	}
	
	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();

		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> orgs = new HashSet<String>();// 费用单位
		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();
		for (MtAppDetailVO detailvo : detailvos) {
			orgs.add(detailvo.getAssume_org());
		}

		if (orgs.isEmpty())
			return null;


		// 列支公司非当前公司
		@SuppressWarnings("unchecked")
		List<String> alDistilledUserPKs = pfc.getUserIdsOfParticipant();
		if (alDistilledUserPKs == null || alDistilledUserPKs.size() == 0) {
			// 查找属于该组织负责人
			findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsId,
					orgs.toArray(new String[] {}));
		} else {
			// 把前台的指派用户都返回，不再获取角色下的用户了
			hsId.addAll(alDistilledUserPKs);
		}
		return hsId;
	}
	
	
}
