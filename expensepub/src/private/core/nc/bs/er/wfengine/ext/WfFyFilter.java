package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.ml.NCLangResOnserver;
import nc.bs.pub.pf.IParticipantFilter;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.itf.uap.pf.IPFOrgUnit;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.org.DeptVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.OrganizeUnit;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.uap.pf.PFRuntimeException;
import nc.vo.uap.rbac.UserRoleVO;

/**
 * 审批流参与者　过滤器：费用承担者
 * nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilter implements IParticipantFilter{

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		HashSet<String> hsId = new HashSet<String>();
		AggregatedValueObject billvo = (AggregatedValueObject) pfc.getBillEntity();

		//::获得单据主表上的费用列支公司？
		String strLiezhiCorp = (String) billvo.getParentVO().getAttributeValue("fydwbm");
		//::获得单据子表上的费用列支部门？
		String strLieZhiDept = (String) billvo.getParentVO().getAttributeValue("fydeptid");
		//::获得单据主表上的报销部门？
		String strBaoxiaoDept = (String) billvo.getParentVO().getAttributeValue("deptid");

		if(strLieZhiDept==null || strLiezhiCorp==null)
			return null;

		//如果列支公司为空，或列支公司=当前公司
		//查找列支部门负责人对应的用户
		List<String> units = findDeptMgrOfDept(strLieZhiDept);

		//查找属于该组织下的所有用户，并可登录当前公司的
		HashSet<String> hsTemp = new HashSet<String>();
		findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsTemp, strLiezhiCorp);

		//判断列支部门负责人是否属于该组织
		boolean bIncluded = false;
		for (Iterator iter = hsTemp.iterator(); iter.hasNext();) {
			String id = (String) iter.next();
			if (units.contains(id)) {
				bIncluded = true;
				hsId.add(id);
			}
		}
		if (!bIncluded && !pfc.isForDispatch())
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000233")/*@res "动态限定错误：列支部门负责人不属于当前角色"*/);

		return hsId;
	}

	/**
	 * 查找属于该组织下的所有用户，并可登录某公司
	 *
	 * @param participantId
	 * @param participantType
	 * @param hsId
	 * @param strCorp
	 * @throws BusinessException
	 */
	private void findUserOfOrgunitByCorp(String participantId, String participantType,
			HashSet<String> hsId, String strCorp) throws BusinessException {
		if(participantType.equals("ROLE")){
			IRoleManageQuery ir=NCLocator.getInstance().lookup(IRoleManageQuery.class);
			UserRoleVO[] userroles = ir.queryUserRoleVOByRoleID(new String[]{participantId});
			for (int i = 0; i < userroles.length; i++) {
				if(!hsId.contains(userroles[i].getCuserid())){
					hsId.add(userroles[i].getCuserid());
				}
			}
		}else{
			hsId.add(participantId);
		}
	}

	public String getCode() {
		return "[U]Stakeholder";
	}

	/**
	 * 查找部门负责人信息
	 * @param deptdocPK 部门PK
	 * @return {部门负责人用户PK;部门VO}
	 * @throws DAOException
	 * @throws BusinessException
	 */
	protected List<String> findDeptMgrOfDept(String deptdocPK) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		//根据PK找到部门VO
		DeptVO deptdocVO = (DeptVO) dao.retrieveByPK(DeptVO.class, deptdocPK);
		if (deptdocVO == null)
			throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000237")/*@res "根据PK找不到部门VO"*/);

		String deptMgr = deptdocVO.getPrincipal();

		List<String> list=new ArrayList<String>();
		retrUser(deptMgr,list);

		return list;
	}

	private void retrUser(String deptMgr, List<String> list) throws PFBusinessException, DAOException, BusinessException {
		if (deptMgr == null || deptMgr.length() == 0)
			return;

		//找到部门负责人员对应的用户
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		UserVO clerkVOs = ucQry.queryUserVOByPsnDocID(deptMgr);
		
		if (clerkVOs != null){
			list.add(clerkVOs.getPrimaryKey());
		}
	}

}