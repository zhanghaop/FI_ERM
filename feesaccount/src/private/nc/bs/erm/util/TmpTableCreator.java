package nc.bs.erm.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import nc.bs.mw.sqltrans.TempTable;
import nc.jdbc.framework.ConnectionFactory;
             
public class TmpTableCreator {

	/**
	 * 创建临时表
	 * 
	 * @param tableName String
	 * @param colNames String[]
	 * @param colTypes Integer[]
	 * @return String
	 * @throws SQLException
	 */
	public static String createTmpTable(String tableName, String[] colNames, Integer[] colTypes)
			throws SQLException {
		String[] types = new String[colTypes.length];
		for (int i = 0; i < colTypes.length; i++) {
			if (colNames[i] != null && (colNames[i].trim().equalsIgnoreCase("reason") 
					|| colNames[i].trim().equalsIgnoreCase("zy"))) {
				types[i] = "varchar(250)";
				continue;
			}
			switch (colTypes[i]) {
			case Types.VARCHAR:
				types[i] = "varchar(100)";
				break;
			case Types.DECIMAL:
				types[i] = "decimal(28, 8)";
				break;
			case Types.INTEGER:
				types[i] = "integer";
				break;
			default:
				break;
			}
		}

		return createTmpTable(tableName, colNames, types);
	}

	/**
	 * 创建临时表
	 * 
	 * @param tableName
	 * @param colNames
	 * @param colTypes
	 * @return
	 * @throws SQLException
	 */
	public static String createTmpTable(String tableName, String[] colNames,
			String[] colTypes) throws SQLException {
		Connection conn = null;
		try {
			conn = ConnectionFactory.getConnection();
			TempTable tempTable = new TempTable();
			StringBuffer colsBuffer = new StringBuffer();
			for (int i = 0; i < colTypes.length; i++) {
				colsBuffer.append(colNames[i]).append(" ").append(colTypes[i]).append(", ");
			}
			// colsBuffer.append("ts char(19)");

			String cols = colsBuffer.toString().substring(0, colsBuffer.length() - 2);
			tableName = tempTable.createTempTable(conn, tableName, cols, "");
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
			}
		}

		return tableName;
	}

}

// /:~
