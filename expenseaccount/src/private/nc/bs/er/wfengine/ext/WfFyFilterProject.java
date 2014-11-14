package nc.bs.er.wfengine.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.rbac.IUserPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pf.pub.util.UserUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

public class WfFyFilterProject extends ErmBaseParticipantFilter {

	public WfFyFilterProject() {
		this.participantName = "项目负责人";
	}

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> projectPKs = new HashSet<String>();// 项目

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// 无费用分摊时,常规报销
			BXBusItemVO[] children = billvo.getBxBusItemVOS();
			if (children != null && children.length > 0) {// 获得表体的项目
				for (BXBusItemVO child : children) {
					if(child.getJobid() != null){
						projectPKs.add(child.getJobid());
					}
				}
			}
			if(billvo.getParentVO().getJobid() != null){
				projectPKs.add(billvo.getParentVO().getJobid());
			}
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// 费用分摊页签
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getJobid() != null) {
					projectPKs.add(detailvo.getJobid());
				}
			}
		}

		if (projectPKs.isEmpty()) {
			return null;
		}

		String[] psnIDs = new WfFyFilterUtils().getDutierByProjectPks(projectPKs.toArray(new String[projectPKs.size()]));
	
		// 查询负责人对应的用户信息
		HashSet<String> userids = retrUser(psnIDs);

		HashSet<String> userList = getPfc().getUserList();

		// 筛选
		for (String centerUserId : userids) {
			if (userList.contains(centerUserId)) {
				userids.add(centerUserId);
			} else {
				// 抛异常
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000910", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}

		return userids;
	}

	protected HashSet<String> retrUser(String[] psnIDs) throws PFBusinessException, DAOException, BusinessException {
		HashSet<String> userids = new HashSet<String>();
		if (psnIDs == null || psnIDs.length == 0)
			return userids;
		// 找到部门负责人员对应的用户
		IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);

		HashMap<String, UserVO[]> users = ucQry.batchQueryUserVOsByPsnDocID(psnIDs, null);

		if (users != null && !users.isEmpty()) {
			for (UserVO[] user : users.values()) {
				UserVO[] tempuser = UserUtil.filtDisableUsers(user);
				if (tempuser != null && tempuser.length > 0) {
					for (int i = 0; i < tempuser.length; i++) {
						userids.add(tempuser[i].getPrimaryKey());
					}
				} else {
					throw new PFBusinessException("未找到项目档案负责人");
				}
			}
		}
		return userids;
	}

}
