package nc.bs.er.pub;

import java.sql.Connection;

import javax.naming.NamingException;

import nc.bs.er.data.DataManager;
import nc.bs.pub.SystemException;
import nc.jdbc.framework.PersistenceManager;

public class BaseDMO extends DataManager {

	/**
	 * @throws javax.naming.NamingException
	 * @throws nc.bs.pub.SystemException
	 */
	public BaseDMO() throws NamingException, SystemException {
		super();

	}
	public Connection getConnection() throws Exception
	{
		Connection conn=null;
		conn=PersistenceManager.getInstance().getJdbcSession().getConnection();
		return conn;
	}
}