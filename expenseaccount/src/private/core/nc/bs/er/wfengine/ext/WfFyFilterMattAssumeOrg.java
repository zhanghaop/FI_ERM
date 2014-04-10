package nc.bs.er.wfengine.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.pub.pf.ParticipantFilterContext;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;

/**
 * �����õ�λ������ :  �������뵥ʹ��
 * �����������ߡ���������
 * @author wangled
 *
 */

public class WfFyFilterMattAssumeOrg extends WfFyFilterCorp {
	
	public WfFyFilterMattAssumeOrg() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000907");//** "���óе���λ������"*/
	}
	
	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();

		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> orgs = new HashSet<String>();// ���õ�λ
		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();
		for (MtAppDetailVO detailvo : detailvos) {
			orgs.add(detailvo.getAssume_org());
		}

		if (orgs.isEmpty())
			return null;


		// ��֧��˾�ǵ�ǰ��˾
		@SuppressWarnings("unchecked")
		List<String> alDistilledUserPKs = pfc.getUserIdsOfParticipant();
		if (alDistilledUserPKs == null || alDistilledUserPKs.size() == 0) {
			// �������ڸ���֯������
			findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsId,
					orgs.toArray(new String[] {}));
		} else {
			// ��ǰ̨��ָ���û������أ����ٻ�ȡ��ɫ�µ��û���
			hsId.addAll(alDistilledUserPKs);
		}
		return hsId;
	}
	
	
}
