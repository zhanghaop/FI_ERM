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
 * 费用承担部门负责人: 费用申请单使用
 * 审批流参与者　过滤器：
 * wangled
 */

public class WfFyFilterMattAssumeDept extends WfFyFilter {
	
	public WfFyFilterMattAssumeDept() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000906")/*费用部门负责人"*/;
	}

	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc)
			throws BusinessException {
		
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();
		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> depts = new HashSet<String>();// 费用部门

		
		MtAppDetailVO[] detailvos = billvo.getChildrenVO();// 申请单业务页签
		for (MtAppDetailVO detailvo : detailvos) {
			if (detailvo.getAssume_dept() != null) {
				depts.add(detailvo.getAssume_dept());
			}
		}
		
		if (depts.size() == 0 )
			return null;
		
		// 查找列支部门负责人对应的用户
		List<String> deptUserList = findDeptMgrOfDept(depts.toArray(new String[depts.size()]));
		
		//用户集合
		HashSet<String> allUserList = getPfc().getUserList();
		if(allUserList == null){
			return null;
		}
		
		for (String deptUserId : deptUserList) {
			if (allUserList.contains(deptUserId)) {
				hsId.add(deptUserId);
			} else {
				//抛异常
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}
		return hsId;
	}
	
	
}
