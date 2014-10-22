package nc.bs.er.wfengine.ext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import nc.bs.dao.DAOException;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.itf.pmbd.pub.IProjectQueryService;
import nc.pubitf.rbac.IUserPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.pf.pub.util.UserUtil;
import nc.vo.pim.project.ProjectHeadVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

public class WfFyFilterProject extends ErmBaseParticipantFilter {

	public WfFyFilterProject() {
		this.participantName = "��Ŀ������";
	}

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> projectPKs = new HashSet<String>();// ��Ŀ

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// �޷��÷�̯ʱ,���汨��
			BXBusItemVO[] children = billvo.getBxBusItemVOS();
			if (children != null && children.length > 0) {// ��ñ������Ŀ
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
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// ���÷�̯ҳǩ
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getJobid() != null) {
					projectPKs.add(detailvo.getJobid());
				}
			}
		}

		if (projectPKs.isEmpty()) {
			return null;
		}

		// ��ѯȫ����Ŀ�ĸ�����
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
