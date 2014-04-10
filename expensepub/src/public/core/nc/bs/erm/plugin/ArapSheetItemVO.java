package nc.bs.erm.plugin;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFBoolean;

public class ArapSheetItemVO extends CircularlyAccessibleValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6880096764148310080L;
	private String name;
	private String itemkey;
	private String reftype;
	private UFBoolean downref;
	@Override
	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		return new String []{"name","itemkey","reftype","downref"};
	}

	@Override
	public Object getAttributeValue(String attributeName) {
		// TODO Auto-generated method stub
		
		return null;
	}

	@Override
	public void setAttributeValue(String name, Object value) {
		// TODO Auto-generated method stub
		if("name".equals(name))
			this.name=(String)value;
		else if ("itemkey".equals(name))
			this.itemkey=(String)value;
		else if ("reftype".equals(name))
			this.reftype=(String)value;
		else if ("downref".equals(name))
			this.downref=(UFBoolean)value;
		else;
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

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public UFBoolean getDownRef() {
		return downref;
	}

	public void setDownRef(UFBoolean downRef) {
		this.downref = downRef;
	}

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRefType() {
		return reftype;
	}

	public void setRefType(String refType) {
		this.reftype = refType;
	}
}
