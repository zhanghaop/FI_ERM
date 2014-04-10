package nc.bs.er.wfengine.ext;

import java.util.HashSet;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 费用申请单审批流限定者：费用承担部门负责人
 * 
 * @author shengqy
 * 
 */
public class WfFyFilterAssumeDept extends ErmBaseParticipantFilter {
	public WfFyFilterAssumeDept() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000909")/*
																												 * @
																												 * res
																												 * "费用承担部门负责人"
																												 */;
	}
	
	@Override
	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		AggMatterAppVO aggvo = (AggMatterAppVO) pfc.getBillEntity();
		String assumeDept = aggvo.getParentVO().getAssume_dept();
		String pk_group = aggvo.getParentVO().getPk_group();
		
		HashSet<String> userIdSet = findDeptMgrOfDept(assumeDept, pk_group);
		
		HashSet<String> userParIds = pfc.getUserList();
		if(userParIds == null){
			return null;
		}
		
		for(String userId : userIdSet){
			if(!userParIds.contains(userId)){
				//抛异常
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000909", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}
		
		return userIdSet;
	}

	/**
	 * 部门负责人对应用户
	 * 
	 * @param assumeDept
	 * @throws BusinessException
	 */
	private HashSet<String> findDeptMgrOfDept(String assumeDept, String pk_group) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		DeptVO deptdocVO = (DeptVO) dao.retrieveByPK(DeptVO.class, assumeDept);
		if (deptdocVO == null)
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000237")/*
																														 * @res
																														 * "根据PK找不到部门VO"
																														 */);
		return retrUser(deptdocVO.getPrincipal(), pk_group);
	}

	private HashSet<String> retrUser(String principal, String pk_group) throws BusinessException {
		if (StringUtil.isEmpty(principal)) {
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0082")/*
																															 * @res
																															 * "未找到费用承担部门负责人"
																															 */);
		}

		// 找到部门负责人员对应的用户
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		UserVO[] clerkvos = ucQry.queryUserVOsByPsnDocID(principal, pk_group);

		HashSet<String> userid = new HashSet<String>();
		if (clerkvos != null && clerkvos.length > 0) {
			for (UserVO vo : clerkvos) {
				userid.add(vo.getPrimaryKey());
			}
		} else {
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0082")/*
																															 * @res
																															 * "未找到费用承担部门负责人"
																															 */);
		}
		return userid;
	}

}
