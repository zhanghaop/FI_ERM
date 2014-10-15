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
 * 审批流参与者　过滤器：费用用单位负责人 nc.bs.er.wfengine.ext.WfFyFilter
 */
public class WfFyFilterCorp extends ErmBaseParticipantFilter {
	public WfFyFilterCorp() {
		this.participantName = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000907")/*
																												 * @
																												 * res
																												 * "费用承担单位负责人"
																												 */;
	}

	//private ParticipantFilterContext pfc;

	public HashSet<String> filterUsers(ParticipantFilterContext pfc) throws BusinessException {
		this.pfc = pfc;
		HashSet<String> hsId = new HashSet<String>();

		JKBXVO billvo = (JKBXVO) pfc.getBillEntity();

		String strbaoxiaoCorp = (String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.PK_ORG);
		Set<String> orgs = new HashSet<String>();// 费用列支公司
		if (!ErmForCShareUtil.isHasCShare(billvo)) {// 无费用分摊时,正常报销
			// 获得单据主表上的费用列支公司
			orgs.add((String) billvo.getParentVO().getAttributeValue(JKBXHeaderVO.FYDWBM));
		} else {
			CShareDetailVO[] detailvos = billvo.getcShareDetailVo();
			for (CShareDetailVO detailvo : detailvos) {
				orgs.add(detailvo.getAssume_org());
			}
		}
		if (strbaoxiaoCorp == null || orgs.isEmpty())
			return null;

		// 列支公司非当前公司
		@SuppressWarnings("unchecked")
		List<String> alDistilledUserPKs = pfc.getUserIdsOfParticipant();
		if (alDistilledUserPKs == null || alDistilledUserPKs.size() == 0) {
			// 查找属于该组织负责人
			findUserOfOrgunitByCorp(pfc.getParticipantId(), pfc.getParticipantType(), hsId,
					orgs.toArray(new String[] {}));
		} else {
			// 把前台的指派用户都返回，不再获取角色下的用户了
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
	protected void findUserOfOrgunitByCorp(String participantId, String participantType, HashSet<String> hsId,
			String[] strCorps) throws BusinessException {
		HashSet<String> userList = getPfc().getUserList();
		
		// 筛选
		List<String> fyusers = new ArrayList<String>();//费用承担单位负责人集合
		for (String strCorp : strCorps) {
			fyusers.add(getOrgPrincipal(strCorp));// 费用用单位负责人
		}
		
		for (String fyUserId : fyusers) {
			if (userList.contains(fyUserId)) {
				hsId.add(fyUserId);
			} else {
				//抛异常
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("common", null,
						"UPP2011-000909", null, new String[] { getParticipantName(), getParticipantTypeName() })/*
																												 * @
																												 * res
																												 * "{0}不属于当前{1}！"
																												 */);
			}
		}
	}

	protected String getOrgPrincipal(String strCorp) throws BusinessException {
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
							"0201107-0083")/* @res "未找到费用承担单位负责人" */);
				}
			} else {
				throw new PFBusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0",
						"0201107-0083")/* @res "未找到费用承担单位负责人" */);
			}
		}
		return user;
	}

	public String getCode() {
		return "[U]Stakeholder";
	}

//	public ParticipantFilterContext getPfc() {
//		return pfc;
//	}
//
//	public void setPfc(ParticipantFilterContext pfc) {
//		this.pfc = pfc;
//	}
}