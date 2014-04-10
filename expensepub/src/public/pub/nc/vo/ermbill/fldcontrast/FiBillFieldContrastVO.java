/**
 * 
 */
package nc.vo.ermbill.fldcontrast;

import nc.vo.pub.SuperVO;

/**
 * 财务单据预算控制字段对照VO
 * @author jianghao
 * @version V5.5
 * @since V5.5
 * 2008-7-24
 */
public class FiBillFieldContrastVO extends SuperVO {

	private String pk_fldcontrast;  
	/**
	 * yt/ss/arap/wb/cmp
	 */
	private String subsys_id;	   
	/**
	 * 单据大类编码
	 */
	private String billtypecode;
	private String common_attfield;
	private String busi_attfield;
	private String reserved1;
	private String reserved2;


	public String getBilltypecode() {
		return billtypecode;
	}

	public void setBilltypecode(String billtypecode) {
		this.billtypecode = billtypecode;
	}

	public String getBusi_attfield() {
		return busi_attfield;
	}

	public void setBusi_attfield(String busi_attfield) {
		this.busi_attfield = busi_attfield;
	}

	public String getCommon_attfield() {
		return common_attfield;
	}

	public void setCommon_attfield(String common_attfield) {
		this.common_attfield = common_attfield;
	}

	public String getPk_fldcontrast() {
		return pk_fldcontrast;
	}

	public void setPk_fldcontrast(String pk_fldcontrast) {
		this.pk_fldcontrast = pk_fldcontrast;
	}

	public String getReserved1() {
		return reserved1;
	}

	public void setReserved1(String reserved1) {
		this.reserved1 = reserved1;
	}

	public String getReserved2() {
		return reserved2;
	}

	public void setReserved2(String reserved2) {
		this.reserved2 = reserved2;
	}

	public String getSubsys_id() {
		return subsys_id;
	}

	public void setSubsys_id(String subsys_id) {
		this.subsys_id = subsys_id;
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getPKFieldName()
	 */
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return "pk_fieldcontrast";
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getParentPKFieldName()
	 */
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see nc.vo.pub.SuperVO#getTableName()
	 */
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "fibill_fieldcontrast";
	}

}
