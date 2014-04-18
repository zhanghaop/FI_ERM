package nc.bs.er.wfengine.ext;

import nc.bs.pub.pf.IParticipantFilter;
import nc.bs.pub.pf.ParticipantFilterContext;
import nc.vo.ml.NCLangRes4VoTransl;

/**
 * ���������������߹���������
 * @author chenshuaia
 *
 */
public abstract class ErmBaseParticipantFilter implements IParticipantFilter {
	/**
	 * �����������ߡ�������������
	 */
	protected ParticipantFilterContext pfc;
	
	/**
	 * ���������������ƣ����� �����óе���λ������
	 */
	protected String participantName;
	
	/**
	 * ��ȡ��������������
	 * @return
	 */
	public String getParticipantTypeName() {
		if (pfc.getParticipantType().equalsIgnoreCase("POST")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UC000-0001653") /*
																							 * @
																							 * res
																							 * "��λ"
																							 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("ROLE")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("101203", "UPP101203-000056")/*
																							 * @
																							 * res
																							 * "��ɫ"
																							 */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("WFUSERGROUP")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000000")/* �����û��� */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("USERGROUP")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000001")/* �û��� */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("RoleGroup")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000002")/* ��ɫ�� */;
		} else if (pfc.getParticipantType().equalsIgnoreCase("BusiReport")) {
			return NCLangRes4VoTransl.getNCLangRes().getStrByID("pfworkflow", "OrganizeUnitTypes-000004")/* ҵ��㱨��ϵ */;
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
