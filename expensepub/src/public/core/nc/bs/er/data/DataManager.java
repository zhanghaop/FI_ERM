package nc.bs.er.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.generator.SequenceGenerator;

public class DataManager {

	public DataManager() {
		super();
		// TODO 自动生成构造函数存根
	}
	public Connection getConnection() throws Exception
	{
		Connection conn=null;
		conn=PersistenceManager.getInstance().getJdbcSession().getConnection();
		return conn;
	}
	
	protected final void beforeCallMethod(String className, String methodName, Object[] params) {
	     
    }
	
	protected final void afterCallMethod(String className, String methodName, Object[] params) {
    }
	
    protected PreparedStatement prepareStatement(Connection con, String sql) throws SQLException {
        return con.prepareStatement(sql);
    }
    
    protected String getOID(){
    	 return new SequenceGenerator().generate();
    	}

}
