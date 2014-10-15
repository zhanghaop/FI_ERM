package nc.vo.erm.common;

import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

/**
 * 报销单据参数VO
 * @author zhaoruic
 *
 */
public class ParamVO extends SuperVO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -437301821588719752L;
	
	public String param_pk;			//参数pk，主键
	public String pk_org;			//业务组织pk
	public String param_code;		//参数编码
	public String param_name;		//参数名称
	public String param_value;		//参数值
	public Integer dr;				
	public UFDateTime ts;			
	public Integer syscode;			
	
	public String getParentPKFieldName() {
		// TODO 自动生成方法存根
		return null;
	}
	public String getPKFieldName() {
		// TODO 自动生成方法存根
		return "param_pk";
	}
	public String getTableName() {
		// TODO 自动生成方法存根
		return "erm_param";
	}
	
	public String getParam_pk() {
		return param_pk;
	}
	public void setParam_pk(String paramPk) {
		param_pk = paramPk;
	}
	public String getPk_org() {
		return pk_org;
	}
	public void setPk_org(String pkOrg) {
		pk_org = pkOrg;
	}
	public String getParam_code() {
		return param_code;
	}
	public void setParam_code(String paramCode) {
		param_code = paramCode;
	}
	public String getParam_name() {
		return param_name;
	}
	public void setParam_name(String paramName) {
		param_name = paramName;
	}
	public String getParam_value() {
		return param_value;
	}
	public void setParam_value(String paramValue) {
		param_value = paramValue;
	}
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
	public Integer getSyscode() {
		return syscode;
	}
	public void setSyscode(Integer syscode) {
		this.syscode = syscode;
	}


}
