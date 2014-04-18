package nc.vo.ep.bx;

import nc.vo.pub.SuperVO;

public class MyDefusedVO extends SuperVO{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String pk_defused;
	public String objcode;
	public String objname;
	public Integer defnum;
	public Integer freenum;
	private String fun_code;
	public String pk_corp;
	public Integer getDefnum() {
		return defnum;
	}
	public void setDefnum(Integer defnum) {
		this.defnum = defnum;
	}
	public Integer getFreenum() {
		return freenum;
	}
	public void setFreenum(Integer freenum) {
		this.freenum = freenum;
	}
	public String getFun_code() {
		return fun_code;
	}
	public void setFun_code(String fun_code) {
		this.fun_code = fun_code;
	}
	public String getObjcode() {
		return objcode;
	}
	public void setObjcode(String objcode) {
		this.objcode = objcode;
	}
	public String getObjname() {
		return objname;
	}
	public void setObjname(String objname) {
		this.objname = objname;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public String getPk_defused() {
		return pk_defused;
	}
	public void setPk_defused(String pk_defused) {
		this.pk_defused = pk_defused;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_defused";
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "bd_defused";
	}
	
}
