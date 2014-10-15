package nc.bs.er.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.pub.lang.UFBoolean;

public class IsExitsRecordResultSetProcessor implements ResultSetProcessor {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2555340804147942984L;

	public Object handleResultSet(ResultSet rs) throws SQLException {
		  if(rs.next())
  		{
			  if(rs.getInt(1)==0)
				  return UFBoolean.FALSE;
	            else
	            	return UFBoolean.TRUE;
			  
			  
  		}else{
  			return UFBoolean.FALSE;
  		}
	}
}
