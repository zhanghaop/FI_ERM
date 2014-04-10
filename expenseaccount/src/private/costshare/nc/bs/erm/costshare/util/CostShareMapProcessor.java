package nc.bs.erm.costshare.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import nc.jdbc.framework.processor.BaseProcessor;

/**
 *
 * User: luolch<br>
 * Date: 2005-1-14<br>
 * Time: 13:45:20<br>
 * <p>数组集合处理器，返回一个map集合
 */
public class CostShareMapProcessor extends  BaseProcessor {
    /**
	 * <code>serialVersionUID</code> 的注释
	 */
	private static final long serialVersionUID = -3631733378522079801L;
	
	private String keyField;
	private String valueField;
	
	public CostShareMapProcessor(String keyField,String valueField){
		this.keyField = keyField;
		this.valueField = valueField;
	}

	public Object processResultSet(ResultSet rs) throws SQLException {

        Map<String,String> result = new HashMap<String,String>();
        while (rs.next()) {
        	result.put(rs.getString(keyField), rs.getString(valueField));
        }
        return result;
    }

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	public String getKeyField() {
		return keyField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getValueField() {
		return valueField;
	}
	

}
