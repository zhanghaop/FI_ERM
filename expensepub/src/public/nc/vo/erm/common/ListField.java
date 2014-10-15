package nc.vo.erm.common;

import java.util.List;

import nc.vo.pub.FieldObject;
import nc.vo.pub.ValidationException;

public class ListField extends FieldObject{

	@Override
	public Class getFieldType() {
		return List.class;
	}

	@Override
	public boolean validate(Object o) throws ValidationException {
		
		if (o == null) {
			return true;
		}else{
			if (!(o instanceof List)){
				return false;
			}else{
				return true;
			}
		}
	}

}
