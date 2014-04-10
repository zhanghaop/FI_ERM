package nc.ui.erm.closeacc.model;

import nc.ui.uif2.model.BillManageModel;

public class CloseAccManageModel extends BillManageModel{
	private String pk_accperiod;//当前选择的期间年
	private String pk_org;//当前选择的业务单元
	private String moduleId;//当前选择的模块
	private String closeaccorgspks;//当前关账组织pk组合
	private String orgType;//当前选择模块的组织类型
	private String minNotAcc;//最小未结账
	private String maxAcc;//最大已结账
	private String pk_accperiodmonth;//当前选择的期间月
	

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
