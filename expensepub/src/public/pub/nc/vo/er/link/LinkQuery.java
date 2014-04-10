package nc.vo.er.link;

import java.util.Collection;

import nc.itf.pub.link.ILinkQueryDataPlural;

public class LinkQuery implements ILinkQueryDataPlural {

	private String billID;

	private String[] billIDs;

	private String pkOrg;

	private String billType;

	private Object userObject;

	Collection<Object> billVOs;
	
	public LinkQuery(String billType,String[] billIDs) {
		super();
		this.billIDs = billIDs;
		this.billType = billType;
	}
	
	public LinkQuery(String billType,String billID) {
		super();
		this.billID = billID;
		this.billType = billType;
	}
	
	public LinkQuery(String billID) {
		super();
		this.billID = billID;
	}
	
	public LinkQuery(String[] billIDs) {
		super();
		this.billIDs = billIDs;
	}

	public String[] getBillIDs() {
		return billIDs;
	}

	public void setBillIDs(String[] billIDs) {
		this.billIDs = billIDs;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public Object getUserObject() {
		return userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	@Override
	public Collection<Object> getBillVOs() {
		return billVOs;
	}

	@Override
	public String getBillID() {
		return billID;
	}

	@Override
	public String getPkOrg() {
		return pkOrg;
	}

}
