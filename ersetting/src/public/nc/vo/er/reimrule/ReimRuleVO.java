package nc.vo.er.reimrule;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class ReimRuleVO extends SuperVO implements Comparable{
	
	public static final String REMRULE_SPLITER = "@";
	public static final String Reim_body_key = "body@";
	public static final String Reim_head_key = "head@";
	public static final String Reim_deptid_key = "deptid@";
	public static final String Reim_fydeptid_key = "fydeptid@";
	public static final String Reim_jkbxr_key = "jkbxr@";
	public static final String Reim_receiver_key = "receiver@";

	private static final long serialVersionUID = 1L;

	private String pk_reimtype;
	private String pk_reimrule;
	private String pk_expensetype;
	private String pk_billtype;
	private String pk_org;
	private String pk_group;
	private String pk_corp;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	private String def6;
	private String def7;
	private String def8;
	private String def9;
	private String def10;
	
	private String def1_name;
	private String def2_name;
	private String def3_name;
	private String def4_name;
	private String def5_name;
	private String def6_name;
	private String def7_name;
	private String def8_name;
	private String def9_name;
	private String def10_name;
	
	private String pk_deptid;
	private String pk_psn;
	private String pk_currtype;
	private UFDouble amount;
	private String memo;
	private Integer priority;
	private String defformula;
	private String validateformula;
	private UFDateTime ts;
	private Integer dr;
	
	
	
	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	public UFDouble getAmount() {
		return amount;
	}

	public void setAmount(UFDouble amount) {
		this.amount = amount;
	}

	public String getDefformula() {
		return defformula;
	}

	public void setDefformula(String defformula) {
		this.defformula = defformula;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPk_billtype() {
		return pk_billtype;
	}

	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_currtype() {
		return pk_currtype;
	}

	public void setPk_currtype(String pk_currtype) {
		this.pk_currtype = pk_currtype;
	}

	public String getPk_deptid() {
		return pk_deptid;
	}

	public void setPk_deptid(String pk_deptid) {
		this.pk_deptid = pk_deptid;
	}

	public String getPk_expensetype() {
		return pk_expensetype;
	}

	public void setPk_expensetype(String pk_expensetype) {
		this.pk_expensetype = pk_expensetype;
	}

	public String getPk_group() {
		return pk_group;
	}

	public void setPk_group(String pk_group) {
		this.pk_group = pk_group;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_psn() {
		return pk_psn;
	}

	public void setPk_psn(String pk_psn) {
		this.pk_psn = pk_psn;
	}

	public String getPk_reimrule() {
		return pk_reimrule;
	}

	public void setPk_reimrule(String pk_reimrule) {
		this.pk_reimrule = pk_reimrule;
	}

	public String getPk_reimtype() {
		return pk_reimtype;
	}

	public void setPk_reimtype(String pk_reimtype) {
		this.pk_reimtype = pk_reimtype;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}


	public String getDef1() {
		return def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public String getDef10() {
		return def10;
	}

	public void setDef10(String def10) {
		this.def10 = def10;
	}

	public String getDef2() {
		return def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef3() {
		return def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef4() {
		return def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef5() {
		return def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getDef6() {
		return def6;
	}

	public void setDef6(String def6) {
		this.def6 = def6;
	}

	public String getDef7() {
		return def7;
	}

	public void setDef7(String def7) {
		this.def7 = def7;
	}

	public String getDef8() {
		return def8;
	}

	public void setDef8(String def8) {
		this.def8 = def8;
	}

	public String getDef9() {
		return def9;
	}

	public void setDef9(String def9) {
		this.def9 = def9;
	}

	public String getValidateformula() {
		return validateformula;
	}

	public void setValidateformula(String validateformula) {
		this.validateformula = validateformula;
	}

	@Override
	public String getPKFieldName() {
		return "pk_reimrule";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "er_reimrule";
	}

	public int compareTo(Object o) {
		if(o ==null || ! (o instanceof ReimRuleVO))
			return -1;
		ReimRuleVO rule=(ReimRuleVO)o;
		if(rule.getPk_expensetype().equals(getPk_expensetype())){
			if(priority==null)
				priority=new Integer(0);
			Integer priority2 = rule.getPriority();
			if(priority2==null)
				priority2=new Integer(0);
			return priority-priority2;
		}else{
			return rule.getPk_expensetype().compareTo(getPk_expensetype());
		}
	}

	public String getDef1_name() {
		return def1_name;
	}

	public void setDef1_name(String def1_name) {
		this.def1_name = def1_name;
	}

	public String getDef2_name() {
		return def2_name;
	}

	public void setDef2_name(String def2_name) {
		this.def2_name = def2_name;
	}

	public String getDef3_name() {
		return def3_name;
	}

	public void setDef3_name(String def3_name) {
		this.def3_name = def3_name;
	}

	public String getDef10_name() {
		return def10_name;
	}

	public void setDef10_name(String def10_name) {
		this.def10_name = def10_name;
	}

	public String getDef4_name() {
		return def4_name;
	}

	public void setDef4_name(String def4_name) {
		this.def4_name = def4_name;
	}

	public String getDef5_name() {
		return def5_name;
	}

	public void setDef5_name(String def5_name) {
		this.def5_name = def5_name;
	}

	public String getDef6_name() {
		return def6_name;
	}

	public void setDef6_name(String def6_name) {
		this.def6_name = def6_name;
	}

	public String getDef7_name() {
		return def7_name;
	}

	public void setDef7_name(String def7_name) {
		this.def7_name = def7_name;
	}

	public String getDef8_name() {
		return def8_name;
	}

	public void setDef8_name(String def8_name) {
		this.def8_name = def8_name;
	}

	public String getDef9_name() {
		return def9_name;
	}

	public void setDef9_name(String def9_name) {
		this.def9_name = def9_name;
	}

}
