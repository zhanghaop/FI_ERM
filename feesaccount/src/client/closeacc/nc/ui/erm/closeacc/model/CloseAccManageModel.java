package nc.ui.erm.closeacc.model;

import nc.ui.uif2.model.BillManageModel;

public class CloseAccManageModel extends BillManageModel{
	private String pk_accperiod;//��ǰѡ����ڼ���
	private String pk_org;//��ǰѡ���ҵ��Ԫ
	private String moduleId;//��ǰѡ���ģ��
	private String closeaccorgspks;//��ǰ������֯pk���
	private String orgType;//��ǰѡ��ģ�����֯����
	private String minNotAcc;//��Сδ����
	private String maxAcc;//����ѽ���
	private String pk_accperiodmonth;//��ǰѡ����ڼ���
	

	public String getMinNotAcc() {
		return minNotAcc;
	}

	public void setMinNotAcc(String minNotAcc) {
		this.minNotAcc = minNotAcc;
	}

	public String getMaxAcc() {
		return maxAcc;
	}

	public void setMaxAcc(String maxAcc) {
		this.maxAcc = maxAcc;
	}

	public String getPk_accperiod() {
		return pk_accperiod;
	}

	public void setPk_accperiod(String pkAccperiod) {
		pk_accperiod = pkAccperiod;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public String getCloseaccorgspks() {
		return closeaccorgspks;
	}

	public void setCloseaccorgspks(String closeaccorgspks) {
		this.closeaccorgspks = closeaccorgspks;
	}

	public String getOrgType() {
		return orgType;
	}

	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}

	public String getPk_accperiodmonth() {
		return pk_accperiodmonth;
	}

	public void setPk_accperiodmonth(String pkAccperiodmonth) {
		pk_accperiodmonth = pkAccperiodmonth;
	}
	
	

}
