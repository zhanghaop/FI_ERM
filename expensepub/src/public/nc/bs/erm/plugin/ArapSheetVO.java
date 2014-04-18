package nc.bs.erm.plugin;

import java.util.HashMap;
import java.util.Map;

import nc.vo.arap.engine.IConfigVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ValidationException;

public class ArapSheetVO extends CircularlyAccessibleValueObject implements IConfigVO{


	public static String key="exceldef";
	
	private static final long serialVersionUID = -6880096764148310080L;
	private String name;
	private Map<String,ArapSheetBodyVO> bodys=new HashMap<String,ArapSheetBodyVO>();
	
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public ArapSheetBodyVO[] getBodys() {
		return  bodys.values().toArray(new ArapSheetBodyVO[]{}) ;
	}

	public void setBodys(ArapSheetBodyVO[] bodys) {
		if(null==bodys)
			return ;
		for(ArapSheetBodyVO body:bodys )
			this.bodys.put(body.getName(), body);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
