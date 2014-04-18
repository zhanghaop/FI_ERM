package nc.bs.er.wfengine.ext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.pub.pf.ParticipantFilterContext;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pub.BusinessException;
import nc.vo.uap.pf.PFBusinessException;

/**
 * �������뵥�������޶��ߣ����óе����Ÿ�����
 * 
 * @author wangled
 * 
 */
public class WfFyFilterAssumeDept extends WfFyFilter {
	public WfFyFilterAssumeDept() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000906")/*���ò��Ÿ�����"*/;

	}
	
	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();
		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> depts = new HashSet<String>();// ���ò���

		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();// ���뵥ҵ��ҳǩ
		for (MtAppDetailVO detailvo : detailvos) {
			if (detailvo.getAssume_dept() != null) {
				depts.add(detailvo.getAssume_dept());
			}
		}
		
		if (depts.size() == 0 )
			return null;
		
		// ������֧���Ÿ����˶�Ӧ���û�
		List<String> deptUserList = findDeptMgrOfDept(depts.toArray(new String[depts.size()]));
		
		//�û�����
		HashSet<String> allUserList = getPfc().getUserList();
		if(allUserList == null){
			return null;
		}
		
		for (String deptUserId : deptUserList) {
			if (allUserList.contains(deptUserId)) {
				hsId.add(deptUserId);
			} else {
				//���쳣
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}�����ڵ�ǰ{1}��"
																												 */);
			}
		}
		return hsId;
	}
}
