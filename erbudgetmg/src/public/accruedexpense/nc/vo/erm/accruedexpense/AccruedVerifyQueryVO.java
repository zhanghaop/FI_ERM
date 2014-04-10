package nc.vo.erm.accruedexpense;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

/**
 * 核销预提的查询条件包装类
 * 
 * @author lvhj
 *
 */
public class AccruedVerifyQueryVO extends ValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2972971625566183834L;
	/**
	 * 主组织
	 */
	private String pk_org;
	/**
	 * 币种
	 */
	private String pk_currtype;
	/**
	 * 经办人
	 */
	private String pk_bxr;
	/**
	 * 查询条件
	 */
	private String where;
	
	/**
	 * 已有的核销明细记录
	 */
	private AccruedVerifyVO[] verifyvos;
	
	/**
	 * 旧的核销明细记录
	 */
	private AccruedVerifyVO[] oldverifyvos;

	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

	public String getPk_currtype() {
		return pk_currtype;
	}

	public void setPk_currtype(String pk_currtype) {
		this.pk_currtype = pk_currtype;
	}

	public String getPk_bxr() {
		return pk_bxr;
	}

	public void setPk_bxr(String pk_bxr) {
		this.pk_bxr = pk_bxr;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public AccruedVerifyVO[] getVerifyvos() {
		return verifyvos;
	}

	public void setVerifyvos(AccruedVerifyVO[] verifyvos) {
		this.verifyvos = verifyvos;
	}

	
	public AccruedVerifyVO[] getOldverifyvos() {
		return oldverifyvos;
	}

	public void setOldverifyvos(AccruedVerifyVO[] oldverifyvos) {
		this.oldverifyvos = oldverifyvos;
	}

	@Override
	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validate() throws ValidationException {
		// TODO Auto-generated method stub
		
	}
	
	

}
