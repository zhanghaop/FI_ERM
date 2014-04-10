package nc.bs.er.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import nc.bs.framework.common.RuntimeEnv;
import nc.bs.logging.Logger;
import nc.bs.mw.sqltrans.TempTable;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.DataSourceCenter;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

/**
 * <p>
 * sqlbuild工具,仅在后台使用
 * </p>
 *
 * @version V6.0 2010-4-28 上午10:45:31
 * @see
 *
 * <br>
 *      Modification：<br>
 *      <li>@author： @time： @modified：</li><br>
 * <br>
 *
 */
public class SqlUtil {

	public static final int MAXLENGTH = 200;
	public static final int inMaxLimitCount = 800;// in(...)里面元素的最大阀值，超过该阀值的时候，SQL解析可能会出错，必须使用临时表等其他办法


	/**
	 * 后台方法，构建In sql条件，根据参数构建in list或者临时表
	 * <p>
	 * 修改记录：
	 * </p>
	 *
	 * @param fieldName
	 * @param values
	 *            值数组
	 * @param listMax
	 *            是否建临时表的阈值
	 * @return 包含in关键字的完成sql 片段
	 * @see
	 * @since V6.0
	 */
	public static String buildInSql(String fieldName, String[] values,
			int listMax) throws BusinessException {
		return buildInSql(fieldName, Arrays.asList(values), true);
	}

	public static String buildInSql(String fieldName, String[] values)
			throws BusinessException {
		return buildInSql(fieldName, Arrays.asList(values), true);
	}

	public static String buildInSql(String fieldName, Collection<String> values)
			throws BusinessException {
		return buildInSql(fieldName, values, true);
	}

	public static <T extends SuperVO>
	String buildInSql(String fieldName, Collection<T> values,String attrName)
		throws BusinessException {
		Collection<String> col = new ArrayList<String>();
		for(T vo : values)
			col.add((String)vo.getAttributeValue(attrName));

		return buildInSql(fieldName, col, true);
	}



	/**
	 * 获得In 语句
	 *
	 * @param fieldName
	 *            字段名
	 * @param pks
	 *            主键数组
	 * @return
	 * @throws SQLException
	 */
	public static String buildInSql(String fieldName, Collection<String> values, boolean autoUseTempTable) throws BusinessException {
		if (fieldName == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0097")/*@res "字段名不允许传空值。"*/);
		}

		if (values == null || values.isEmpty()) {
			return fieldName + " in ('') ";
		}

		List<String> pks = null;
		if(values instanceof List) {
			pks = (List<String>) values;
		} else {
			pks = new ArrayList<String>();
			pks.addAll(values);
		}

		String rsStr = null;
		int length = pks.size();
		if (length > MAXLENGTH && autoUseTempTable) {
			return autoUseTmpTable(fieldName, pks, length);
		}

		if (length > inMaxLimitCount) {
			// 超过in(...)的最大阀值，使用( field in(...) or field in(...) or
			// ...)的方式来实现，效率非常低
			StringBuffer sb = new StringBuffer();
			sb.append(" (");
			for (int i = 0; i < length;) {
				sb.append(getInStr(fieldName, pks, i, i
						+ inMaxLimitCount - 1));
				sb.append(" or");
				i = i + inMaxLimitCount;
			}
			rsStr = sb.substring(0, sb.length() - 3) + ") ";
		} else {
			// 没有超过最大阀值，可以写在一个in(...)里面
			rsStr = getInStr(fieldName, pks, 0, length - 1);
		}
		return rsStr;
	}

	/**
	 * 自动使用临时表
	 * <p>修改记录：</p>
	 * @param fieldName
	 * @param pks
	 * @param length
	 * @return
	 * @see
	 * @since V6.0
	 */
	private static String autoUseTmpTable(String fieldName, List<String> pks,
			int length) {
		String rsStr;
		boolean canCreateTable = false;// 只有在服务器上运行的时候才可以创建临时表
		try {
			canCreateTable = RuntimeEnv.getInstance()
					.isRunningInServer();
		} catch (Exception e) {
		} catch (Error e) {
		}
		if (canCreateTable) {
			try {
				String tablename = getTempTablename(fieldName);
				String colname = "pk";
				String coltype = "varchar(60)";
				tablename = createTempTable(tablename, colname, coltype);
				if (tablename == null) {
					// 临时表创建失败，重新尝试创建一次
					tablename = getTempTablename(fieldName);
					tablename = createTempTable(tablename, colname,
							coltype);
				}
				if (tablename == null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0098")/*@res "创建临时表失败"*/);
				} else {
					insertIntoTable(tablename, colname, pks);
				}
				rsStr = fieldName + " in (select " + colname + " from "
						+ tablename + ") ";
			} catch (Exception e) {
				ExceptionHandler.consume(new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0007")/*@res "创建临时表失败。尝试使用OR方式。"*/, e));
				if (length > inMaxLimitCount) {
					// 超过in(...)的最大阀值，使用( field in(...) or field in(...)
					// or ...)的方式来实现，效率非常低
					StringBuffer sb = new StringBuffer();
					sb.append(" (");
					for (int i = 0; i < length;) {
						sb.append(getInStr(fieldName, pks, i, i
								+ inMaxLimitCount - 1));
						sb.append(" or");
						i = i + inMaxLimitCount;
					}
					rsStr = sb.substring(0, sb.length() - 3) + ") ";
				} else {
					// 没有超过最大阀值，可以写在一个in(...)里面
					rsStr = getInStr(fieldName, pks, 0, length - 1);
				}
			}
		} else if (length > inMaxLimitCount) {
			// 超过in(...)的最大阀值，使用( field in(...) or field in(...) or
			// ...)的方式来实现，效率非常低
			StringBuffer sb = new StringBuffer();
			sb.append(" (");
			for (int i = 0; i < length;) {
				sb.append(getInStr(fieldName, pks, i, i
						+ inMaxLimitCount - 1));
				sb.append(" or");
				i = i + inMaxLimitCount;
			}
			rsStr = sb.substring(0, sb.length() - 3) + ") ";
		} else {
			// 没有超过最大阀值，可以写在一个in(...)里面
			rsStr = getInStr(fieldName, pks, 0, length - 1);
		}

		return rsStr;
	}


	private static String getInStr(String fieldName, List<String> pks, int start, int end) {
		start = Math.min(start, end);
		end = Math.max(start, end);
		StringBuffer sb = new StringBuffer();
		sb.append(" ");
		sb.append(fieldName);
		sb.append(" in (");
		String key = null;
		for (int i = start; i < pks.size(); i++) {
			if (i > end) {
				break;
			}
			if (pks.get(i) == null)
				continue;
			key = pks.get(i).trim();
			sb.append("'");
			sb.append(key);
			sb.append("',");
		}
		String inStr = sb.substring(0, sb.length() - 1) + ") ";
		return inStr;
	}



	/**
	 *
	 * 构造临时表名, 返回表名长度<=18位
	 *
	 * @param fieldName
	 * @return
	 */
	private static String getTempTablename(String fieldName) {
		StringBuffer tableName = new StringBuffer();
		tableName.append("t_");
		String tempStr = fieldName;
		int index = fieldName.indexOf(".");
		if (index >= 0) {
			tempStr = fieldName.substring(index);
		}
		if (tempStr.length() > 7) {
			tempStr = tempStr.substring(tempStr.length() - 7);
		}
		tableName.append(tempStr);
		tableName.append(new Random().nextInt(9));
		long currtime = System.currentTimeMillis();
		// 取当前时间截取后8为作为调整。
		// 10000000毫秒=10000秒=2.7小时，而我们认为一个线程创建的临时表如果没有在2.7小时内失效应该是程序的问题
		// 另外，即使该线程没有结束，再创建的临时表重复的概率（时间正好相差10000000ms并且random出的数也完全一样）也是极其少数情况，所以不考虑
		String tempStr2 = String.valueOf(currtime);
		if (tempStr2.length() > 8) {
			tempStr2 = tempStr2.substring(tempStr2.length() - 8);
		}
		tableName.append(tempStr2);
		return tableName.toString();
	}

	/**
	 * 创建临时表
	 *
	 * <p>
	 * 修改记录：
	 * </p>
	 *
	 * @param fieldName
	 * @return
	 * @throws SQLException
	 * @see
	 * @since V6.0
	 */
	private static String createTempTable(String tablename, String colname, String coltype) throws SQLException {
		Connection con = null;
		try {
			con = ConnectionFactory.getConnection();
			TempTable tt = new TempTable();
			tablename = tt.createTempTable(con, tablename, " " + colname + " " + coltype + " ", null);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}
		return tablename;
	}


	/**
	 * 把数据插入临时表
	 *
	 * <p>
	 * 修改记录：
	 * </p>
	 *
	 * @param tableName
	 * @param colName
	 * @param datas
	 * @throws java.sql.SQLException
	 * @throws DbException
	 * @see
	 * @since V6.0
	 */
	private static void insertIntoTable(String tableName, String colName, Collection<String> datas) throws java.sql.SQLException, DbException {
		java.sql.Connection con = null;
		JdbcSession session = null;
		try {
			PersistenceManager manager = PersistenceManager.getInstance(DataSourceCenter.getInstance().getDatabaseName());
			manager.setAddTimeStamp(false);
			session = manager.getJdbcSession();
			con = session.getConnection();
			if (con instanceof CrossDBConnection) {
				((CrossDBConnection) con).setAddTimeStamp(false);
			}
			String sql_insert = "insert into " + tableName + " (" + colName + ")  values( ? ) ";
			for (String string : datas) {
				SQLParameter sqlParam = new SQLParameter();
				sqlParam.addParam(string);
				session.addBatch(sql_insert, sqlParam);
			}
			session.executeBatch();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
			try {
				if (session != null)
					session.closeAll();
			} catch (Exception e) {
			}
		}
	}
}