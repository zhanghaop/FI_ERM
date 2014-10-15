package nc.bs.er.wfengine.ext;

import nc.bs.pub.pf.IParticipantFilter;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.vo.ml.NCLangRes4VoTransl;

/**
 * 费用审批流参与者过滤器基类
 * @author chenshuaia
 *
 */
public abstract class ErmBaseParticipantFilter implements IParticipantFilter {
	/**
	 * 审批流参与者　过滤器上下文
	 */
	protected ParticipantFilterContext pfc;
	
	/**
	 * 审批流参与者名称，例如 ：费用承担单位负责人
	 */
	protected String participantName;
	
	/**
	 * 获取参与者类型名称
	 * @return
	 */
	public String getParticipantTypeName() {
		if (pfc.getParticipantType().equalsIgnoreCase("POST")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001653") /*
																							 * @
																							 * res
																							 * "岗位"
																							 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("ROLE")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("101203", "UPP101203-000056")/*
																							 * @
																							 * res
																							 * "角色"
																							 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("WFUSERGROUP")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000000")/* 流程用户组 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("USERGROUP")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000001")/* 用户组 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("RoleGroup")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000002")/* 角色组 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("BusiReport")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000004")/* 业务汇报关系 */;
		} else {
			return "";
		}
	}

	public ParticipantFilterContext getPfc() {
		return pfc;
	}

	public void setPfc(ParticipantFilterContext pfc) {
		this.pfc = pfc;
	}

	public String getParticipantName() {
		return participantName;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}
}
