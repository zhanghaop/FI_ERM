package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtil;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.rbac.IUserPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.org.DeptVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 审批流参与者　过滤器：费用承担部门负责人 nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilter extends ErmBaseParticipantFilter {
	
	public WfFyFilter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000906")/*
																												 * @
																												 * res
																												 * "费用部门负责人"
																												 */;
	}
	
	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;

		HashSet<String> hsId = new HashSet<String>();

		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> depts = new HashSet<String>();// 费用列支部门

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// 无费用分摊时,常规报销
			// 获得单据子表上的费用列支部门？
			depts.add((String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.FYDEPTID));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// 费用分摊页签
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getAssume_dept() != null) {
					depts.add(detailvo.getAssume_dept());
				}
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

	public String getCode() {
		return "[U]Stakeholder";
	}

	/**
	 * 查找各部门负责人信息
	 *
	 * @param deptdocPks
	 *            各部门PKs
	 * @return {各部门负责人用户PK;部门VO}
	 * @throws DAOException
	 * @throws BusinessException
	 */

	protected List<String> findDeptMgrOfDept(String[] deptPks)
			throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> list = new ArrayList<String>();
		String where = SqlUtil.buildInSql(DeptVO.PK_DEPT, deptPks);
		@SuppressWarnings("rawtypes")
        Collection result = dao.retrieveByClause(DeptVO.class, where);
		if (result != null && result.size() > 0) {
		    for (Object obj : result.toArray()) {
		        DeptVO vo = (DeptVO)obj;
		        String deptMgrpsn = vo.getPrincipal();
	            retrUser(deptMgrpsn, list);
		    }
		} else if (result == null || deptPks.length != result.size()) {
		    throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl
                    .getNCLangRes().getStrByID("2011", "UPP2011-000237")/*
                                                                         * @res
                                                                         * "根据PK找不到部门VO"
                                                                         */);
		}
		return list;
	}

	private void retrUser(String deptMgr, List<String> list)
			throws PFBusinessException, DAOException, BusinessException {
		if (StringUtil.isEmpty(deptMgr)){
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0082")/*@res "未找到费用承担部门负责人"*/);
		}

		// 找到部门负责人员对应的用户
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		UserVO clerkVOs = ucQry.queryUserVOByPsnDocID(deptMgr);

		if (clerkVOs != null) {
			list.add(clerkVOs.getPrimaryKey());
		}else{
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0082")/*@res "未找到费用承担部门负责人"*/);
		}
	}

	public ParticipantFilterContext getPfc() {
		return pfc;
	}

	public void setPfc(ParticipantFilterContext pfc) {
		this.pfc = pfc;
	}
}