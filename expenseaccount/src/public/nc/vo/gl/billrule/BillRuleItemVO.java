package nc.vo.gl.billrule;

import nc.vo.pub.ValidationException;
import nc.vo.pub.ValueObject;

/**
 * @author chengsc
 *
 */
public class BillRuleItemVO extends ValueObject{ 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attrName;
	private String pk_billtype;
	private String pk_voitem;
	private String showName;
	private String refType;
	private String itemType;
	
	public String getAttrName() {
		return attrName;
	}
	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}
	public String getPk_billtype() {
		return pk_billtype;
	}
	public void setPk_billtype(String pk_billtype) {
		this.pk_billtype = pk_billtype;
	}
	public String getPk_voitem() {
		return pk_voitem;
	}
	public void setPk_voitem(String pk_voitem) {
		this.pk_voitem = pk_voitem;
	}
	public String getRefType() {
		return refType;
	}
	public void setRefType(String refType) {
		this.refType = refType;
	}
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
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
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	/**
	 * @param scrvo
	 * @return
	 */
	public BillRuleItemVO replicate() {
		BillRuleItemVO vo = new BillRuleItemVO();
		vo.setAttrName(this.getAttrName());
		vo.setPk_billtype(this.getPk_billtype());
		vo.setPk_voitem(this.getPk_voitem());
		vo.setRefType(this.getRefType());
		vo.setShowName(this.getShowName());
		vo.setItemType(this.getItemType());
		return vo;
	}

}
