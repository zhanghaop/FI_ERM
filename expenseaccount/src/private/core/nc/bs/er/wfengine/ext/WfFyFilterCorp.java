package nc.bs.er.wfengine.ext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.rbac.IUserPubService;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.sm.UserVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * �����������ߡ��������������õ�λ������ nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilterCorp extends ErmBaseParticipantFilter {
	public WfFyFilterCorp() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000907")/*
																												 * @
																												 * res
																												 * "���óе���λ������"
																												 */;
	}

	private ParticipantFilterContext pfc;

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();

		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();

		String strbaoxiaoCorp = (String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.PK_ORG);
		Set<String> orgs = new HashSet<String>();// ������֧��˾
		if (!ErmForCShareUtil.isHasCShare(billvo)) {// �޷��÷�̯ʱ,��������
			// ��õ��������ϵķ�����֧��˾
			orgs.add((String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.FYDWBM));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();
			for (CShareDetailVO detailvo : detailvos) {
				orgs.add(detailvo.getAssume_org());
			}
		}
		if (strbaoxiaoCorp == null || orgs.isEmpty())
			return null;

		// ��֧��˾�ǵ�ǰ��˾
		@SuppressWarnings("unchecked")
		List<String> alDistilledUserPKs = pfc.getUserIdsOfParticipant();
		if (alDistilledUserPKs == null || alDistilledUserPKs.size() == 0) {
			// �������ڸ���֯������
			findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsId,
					orgs.toArray(new String[] {}));
		} else {
			// ��ǰ̨��ָ���û������أ����ٻ�ȡ��ɫ�µ��û���
			hsId.addAll(alDistilledUserPKs);
		}
		return hsId;
	}

	/**
	 * �������ڸ���֯�µ������û������ɵ�¼ĳ��˾
	 * 
	 * @param participantId
	 * @param participantType
	 * @param hsId
	 * @param strCorp
	 * @throws BusinessException
	 */
	private void findUserOfOrgunitByCorp(String participantId, String participantType, HashSet<String> hsId,
			String[] strCorps) throws BusinessException {
		HashSet<String> userList = getPfc().getUserList();
		
		// ɸѡ
		List<String> fyusers = new ArrayList<String>();//���óе���λ�����˼���
		for (String strCorp : strCorps) {
			fyusers.add(getOrgPrincipal(strCorp));// �����õ�λ������
		}
		
		for (String fyUserId : fyusers) {
			if (userList.contains(fyUserId)) {
				hsId.add(fyUserId);
			} else {
				//���쳣
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000909", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}�����ڵ�ǰ{1}��"
																												 */);
			}
		}
	}

	private String getOrgPrincipal(String strCorp) throws BusinessException {
		IOrgUnitPubService og = NCLocator.getInstance().lookup(IOrgUnitPubService.class);
		OrgVO[] orgs = og.getOrgs(new String[] { strCorp }, null);
		String user = "";
		if (orgs != null && orgs.length != 0) {
			String psn = orgs[0].getPrincipal();
			if (psn != null) {
				IUserPubService ucQry = NCLocator.getInstance().lookup(IUserPubService.class);
				UserVO muser = ucQry.queryUserVOByPsnDocID(psn);
				if (muser != null) {
					user = muser.getPrimaryKey();
				} else {
					throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
							"0201107-0083")/* @res "δ�ҵ����óе���λ������" */);
				}
			} else {
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0083")/* @res "δ�ҵ����óе���λ������" */);
			}
		}
		return user;
	}

	public String getCode() {
		return "[U]Stakeholder";
	}

	public ParticipantFilterContext getPfc() {
		return pfc;
	}

	public void setPfc(ParticipantFilterContext pfc) {
		this.pfc = pfc;
	}
}