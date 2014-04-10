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
 * �������޶��ߣ��ɱ����ĸ����˻�ǩ
 *
 * @author lvhj
 *
 */
public class WfFyFilterCostcenter extends ErmBaseParticipantFilter {
	public WfFyFilterCostcenter() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000908")/*
																												 * @
																												 * res
																												 * "���óɱ����ĸ�����"
																												 */;
	}

	//private ParticipantFilterContext pfc;

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();
		Set<String> resacostcenterPKs = new HashSet<String>();// ���óɱ�����

		if (!ErmForCShareUtil.isHasCShare(billvo)) {// �޷��÷�̯ʱ,���汨��
			// ��õ��������ϵĳɱ�����
			resacostcenterPKs.add((String) billvo.getParentVO()
					.getAttributeValue("pk_resacostcenter"));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();// ���÷�̯ҳǩ
			for (CShareDetailVO detailvo : detailvos) {
				if (detailvo.getPk_resacostcenter() != null) {
					resacostcenterPKs.add(detailvo.getPk_resacostcenter());
				}
			}
		}

		if (resacostcenterPKs.isEmpty())
			return null;

		// ��ѯȫ���ɱ����ĵĸ�����
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
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "δ�ҵ��ɱ����ĸ�����"*/);
			}else{
				psnIDs.add(costcenters[i].getPk_principal());
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

	protected HashSet<String> retrUser(String[] psnIDs)
			throws PFBusinessException, DAOException, BusinessException {
		HashSet<String> userids = new HashSet<String>();
		if (psnIDs == null || psnIDs.length == 0)
			return userids;
		// �ҵ����Ÿ�����Ա��Ӧ���û�
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
					throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0084")/*@res "δ�ҵ��ɱ����ĸ�����"*/);
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