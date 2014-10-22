package nc.bs.er.wfengine.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.itf.pmbd.pub.IProjectQueryService;
import nc.pubitf.rbac.IUserPubService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.pf.pub.util.UserUtil;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.pim.project.ProjectHeadVO;

public class WfFyFilterMattProject extends ErmBaseParticipantFilter {

	public WfFyFilterMattProject() {
		this.participantName = "��Ŀ������";
	}

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		AggMatterAppVO billvo = (AggMatterAppVO) pfc.getBillEntity();
		Set<String> projectPKs = new HashSet<String>();// ��Ŀ

		MtAppDetailVO[] detailvos = billvo.getChildrenVO();
		for (MtAppDetailVO detailvo : detailvos) {
			projectPKs.add(detailvo.getPk_project());
			
		}
		if (projectPKs.isEmpty()) {
			return null;
		}

//		String condition = SqlUtils.getInStr(ProjectHeadVO.PK_PROJECT,
//				projectPKs.toArray(new String[projectPKs.size()]));

		// ��ѯȫ����Ŀ�ĸ�����
//		IProjectQuery service = NCLocator.getInstance().lookup(IProjectQuery.class);
//		ProjectHeadVO[] projecters = (ProjectHeadVO[]) service.queryProjectHeadVOsByCondition(condition);
		// ��ѯȫ����Ŀ�ĸ����� ����Ԫʹ��IProjectQueryService�ӿ�
		IProjectQueryService service = NCLocator.getInstance().lookup(IProjectQueryService.class);
		ProjectHeadVO[] projecters = (ProjectHeadVO[]) service.queryProjectHeadVOsByPK(projectPKs
				.toArray(new String[0]));

		if (projecters == null || projecters.length == 0) {
			return null;
		}
		HashSet<String> psnIDs = new HashSet<String>();
		for (int i = 0; i < projecters.length; i++) {
			if (projecters[i].getPk_dutier() == null) {
				throw new PFBusinessException("δ�ҵ���Ŀ������");
			} else {
				psnIDs.add(projecters[i].getPk_dutier());
			}
		}
		// ��ѯ�����˶�Ӧ���û���Ϣ
		HashSet<String> userids = retrUser(psnIDs.toArray(new String[0]));

		HashSet<String> userList = getPfc().getUserList();

		// ɸѡ
		for (String centerUserId : userids) {
			if (userList.contains(centerUserId)) {
				userids.add(centerUserId);
			} else {
				// ���쳣
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000909", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}�����ڵ�ǰ{1}��"
																												 */);
			}
		}

		return userids;
	}

	protected HashSet<String> retrUser(String[] psnIDs) throws PFBusinessException, DAOException, BusinessException {
		HashSet<String> userids = new HashSet<String>();
		if (psnIDs == null || psnIDs.length == 0)
			return userids;
		// �ҵ����Ÿ�����Ա��Ӧ���û�
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
					throw new PFBusinessException("δ�ҵ���Ŀ����������");
				}
			}
		}
		return userids;
	}

}