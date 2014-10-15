package nc.bs.er.wfengine.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.rbac.IUserPubService;
import nc.pubitf.resa.costcenter.ICostCenterPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pf.pub.util.UserUtil;
import nc.vo.pub.BusinessException;
import nc.vo.resa.costcenter.CostCenterVO;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * 审批流限定者，成本中心负责人会签
 *
 * @author lvhj
 *
 */
public class WfFyFilterCostcenter extends ErmBaseParticipantFilter {
	public WfFyFilterCostcenter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000908")/*
																												 * @
																												 * res
																												 * "费用成本中心负责人"
																												 */;
	}

	//private ParticipantFilterContext pfc;

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> resacostcenterPKs = new HashSet<String>();// 费用成本中心

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// 无费用分摊时,常规报销
			// 获得单据主表上的成本中心
			resacostcenterPKs.add((String) billvo.getParentVO()
					.getAttributeValue("pk_resacostcenter"));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// 费用分摊页签
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getPk_resacostcenter() != null) {
					resacostcenterPKs.add(detailvo.getPk_resacostcenter());
				}
			}
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
			if(costcenters[i].getPk_principal() == null){
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "未找到成本中心负责人"*/);
			}else{
				psnIDs.add(costcenters[i].getPk_principal());
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
						"UPP2011-000909", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}

		return userids;
	}

	protected HashSet<String> retrUser(String[] psnIDs)
			throws PFBusinessException, DAOException, BusinessException {
		HashSet<String> userids = new HashSet<String>();
		if (psnIDs == null || psnIDs.length == 0)
			return userids;
		// 找到部门负责人员对应的用户
		IUserPubService ucQry = NCLocator.getInstance().lookup(
				IUserPubService.class);

		HashMap<String, UserVO[]> users = ucQry.batchQueryUserVOsByPsnDocID(
				psnIDs, null);

		if (users != null && !users.isEmpty()) {
			for (UserVO[] user : users.values()) {
				UserVO[] tempuser = UserUtil.filtDisableUsers(user);
				if (tempuser != null && tempuser.length > 0) {
					for (int i = 0; i < tempuser.length; i++) {
						userids.add(tempuser[i].getPrimaryKey());
					}
				}else{
					throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "未找到成本中心负责人"*/);
				}
			}
		}
		return userids;
	}

//	public ParticipantFilterContext getPfc() {
//		return pfc;
//	}
//
//	public void setPfc(ParticipantFilterContext pfc) {
//		this.pfc = pfc;
//	}
	
}