package nc.vo.erm.control;

import java.util.HashMap;

import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;

public class ErmNtbResultVO extends CircularlyAccessibleValueObject {

	private static final long serialVersionUID = 8847399033116724674L;
	
	private HashMap<String, Object> attrName_Value_Map = new HashMap<String, Object>();

	@Override
	public String[] getAttributeNames() {
		return null;
	}

	@Override
	public Object getAttributeValue(String attributeName) {
		return attrName_Value_Map.get(attributeName);
	}

	@Override
	public void setAttributeValue(String name, Object value) {
		attrName_Value_Map.put(name, value);
	}

	@Override
	public String getEntityName() {
		return null;
	}

	@Override
	public void validate() throws ValidationException {

	}

}
