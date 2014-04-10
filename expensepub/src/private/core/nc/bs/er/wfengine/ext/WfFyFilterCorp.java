package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.HashSet;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.IParticipantFilter;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.itf.uap.rbac.IRoleManageQuery;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IFunctionPermissionPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.org.OrgVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.rbac.UserRoleVO;

/**
 * 审批流参与者　过滤器：费用用单位负责人
 * nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilterCorp implements IParticipantFilter{

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		HashSet<String> hsId = new HashSet<String>();
		AggregatedValueObject billvo = (AggregatedValueObject) pfc.getBillEntity();

		String strbaoxiaoCorp = (String) billvo.getParentVO().getAttributeValue("pk_org");
		//::获得单据主表上的费用列支公司？
		String strLiezhiCorp = (String) billvo.getParentVO().getAttributeValue("fydwbm");

		if(strbaoxiaoCorp==null || strLiezhiCorp==null)
			return null;

		//列支公司非当前公司
		ArrayList alDistilledUserPKs = pfc.getUserIdsOfParticipant();
		if (alDistilledUserPKs == null || alDistilledUserPKs.size() == 0) {
			//如果没有指派信息
			//查找属于该组织下的所有用户，并可登录列支公司的
			findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsId,
					strLiezhiCorp);
		} else {
			//把前台的指派用户都返回，不再获取角色下的用户了
			hsId.addAll(alDistilledUserPKs);
		}
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


		//查询某组织下，可登录某公司的所有用户
//		OrganizeUnit[] orgUnits = NCLocator.getInstance().lookup(IPFOrgUnit.class)
//				.queryUsersByCorpAndDept(participantType, participantId, strCorp, null);
//		if (orgUnits == null || orgUnits.length == 0)
//			throw new PFRuntimeException(NCLangResOnserver.getInstance().getStrByID("pfworkflow",
//					"UPPpfworkflow-000259")/* @res "找不到执行人" */);
		
		if(participantType.equals("ROLE")){
			IRoleManageQuery ir=NCLocator.getInstance().lookup(IRoleManageQuery.class);
			UserRoleVO[] userroles = ir.queryUserRoleVOByRoleID(new String[]{participantId});
	
			//查询可登录该公司的所有用户
			IFunctionPermissionPubService umq = NCLocator.getInstance().lookup(IFunctionPermissionPubService.class);
//modified by chendya@ufida.com.cn
			//筛选
			String fyuser = getOrgPrincipal(strCorp);//费用用单位负责人
			
			for (int i = 0; i < userroles.length; i++) {
				if(fyuser != null && !hsId.contains(userroles[i].getCuserid()) && fyuser.equals(userroles[i].getCuserid())){
					hsId.add(userroles[i].getCuserid());
				}
			}
//--end			
		}else{
			hsId.add(participantId);
		}

	}

	private String getOrgPrincipal(String strCorp) throws BusinessException {
		IOrgUnitPubService og=NCLocator.getInstance().lookup(IOrgUnitPubService.class);
		OrgVO[] orgs = og.getOrgs(new String[]{strCorp}, null);
		String user="";
		if(orgs!=null && orgs.length!=0){
			String psn=orgs[0].getPrincipal();
			if(psn!=null){
				IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);
				UserVO muser = ucQry.queryUserVOByPsnDocID(psn);
				if (muser != null){
					user=muser.getPrimaryKey();
				}
			}
		}
		return user;
	}

	public String getCode() {
		return "[U]Stakeholder";
	}


}