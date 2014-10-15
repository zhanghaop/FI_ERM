package nc.vo.er.paramsetting;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

/**

 */
public class ParamVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String param_pk;
	public String pk_org;
	public String param_code;
	public String param_name;
	public String param_value;
	public Integer dr;
	public UFDateTime ts;
	public Integer syscode;

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getParam_code() {
		return param_code;
	}

	public void setParam_code(String param_code) {
		this.param_code = param_code;
	}

	public String getParam_name() {
		return param_name;
	}

	public void setParam_name(String param_name) {
		this.param_name = param_name;
	}

	public String getParam_pk() {
		return param_pk;
	}

	public void setParam_pk(String param_pk) {
		this.param_pk = param_pk;
	}

	public String getParam_value() {
		return param_value;
	}

	public void setParam_value(String param_value) {
		this.param_value = param_value;
	}

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_corp) {
		this.pk_org = pk_corp;
	}

	public Integer getSyscode() {
		return syscode;
	}

	public void setSyscode(Integer syscode) {
		this.syscode = syscode;
	}

	public UFDateTime getTs() {
		return ts;
	}

	public void setTs(UFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return "param_pk";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "erm_param";
	}

}
