package nc.bs.erm.plugin;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;

public class ArapSheetBodyVO extends CircularlyAccessibleValueObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -393832901503069753L;
	private String name ;
	private ArapSheetItemVO[] items;
	@Override
	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAttributeValue(String attributeName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAttributeValue(String name, Object value) {
		// TODO Auto-generated method stub
		
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

	public ArapSheetItemVO[] getItems() {
		return items;
	}

	public void setItems(ArapSheetItemVO[] items) {
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
