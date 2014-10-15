package nc.bs.er.util;

import java.sql.Connection;
import java.util.Iterator;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Log;
import nc.bs.mw.sqltrans.TempTable;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.vo.bd.accessor.IBDData;
import nc.vo.er.pub.QryObjList;

/**
 * a功能: 作者：宋涛 创建时间：(2004-10-22 14:22:18) 使用说明：以及别人可能感兴趣的介绍 注意：现存Bug
 */
public class TempTableDAO  {
	
    /**
     * TempTableDMO 构造子注解。
     * 
     * @exception javax.naming.NamingException
     *                异常说明。
     * @exception nc.bs.pub.SystemException
     *                异常说明。
     */
    public TempTableDAO() throws javax.naming.NamingException,
            nc.bs.pub.SystemException {
        super();
    }
	//private HashMap m_hashBhxj=new HashMap();//key-value:pk_subpk-subpk

    /**
     * TempTableDMO 构造子注解。
     * 
     * @param dbName
     *            java.lang.String
     * @exception javax.naming.NamingException
     *                异常说明。
     * @exception nc.bs.pub.SystemException
     *                异常说明。
     */
    public TempTableDAO(String dbName) throws javax.naming.NamingException,
            nc.bs.pub.SystemException {
        //super(dbName);
    }

    /**建立临时表的阀值*/
    public static int getTmpTableMinCount(){
    	return 200;
    }
    /**
     * 建立一个临时表，含有一个字段，字段名和字段的数据类型在colNameAndType 在临时表里插入数据values 返回临时表的表名 public
     * final static int BIT = -7; public final static int TINYINT = -6; public
     * final static int SMALLINT = 5; public final static int INTEGER = 4;
     * public final static int BIGINT = -5;
     * 
     * public final static int FLOAT = 6; public final static int REAL = 7;
     * public final static int DOUBLE = 8;
     * 
     * public final static int NUMERIC = 2; public final static int DECIMAL = 3;
     * 
     * public final static int CHAR = 1; public final static int VARCHAR = 12;
     * public final static int LONGVARCHAR = -1;
     * 
     * public final static int DATE = 91; public final static int TIME = 92;
     * public final static int TIMESTAMP = 93;
     * 
     * public final static int BINARY = -2; public final static int VARBINARY =
     * -3; public final static int LONGVARBINARY = -4;
     * 
     * public final static int NULL = 0;
     * 
     * 
     * 
     * @return java.lang.String
     * @param tableName
     *            java.lang.String
     * @param colNameAndType
     *            java.lang.String
     */
    public String createTable(String tableName, String colName1,
            String colDataType1, Object[] values) throws java.sql.SQLException {
        @SuppressWarnings("rawtypes")
        java.util.List lst = java.util.Arrays.asList(values);
        return createTable(tableName, colName1, colDataType1, lst);
    }

    /**
     * 建立一个临时表，含有一个字段，字段名和字段的数据类型在colNameAndType 在临时表里插入数据values 返回临时表的表名 public
     * final static int BIT = -7; public final static int TINYINT = -6; public
     * final static int SMALLINT = 5; public final static int INTEGER = 4;
     * public final static int BIGINT = -5;
     * 
     * public final static int FLOAT = 6; public final static int REAL = 7;
     * public final static int DOUBLE = 8;
     * 
     * public final static int NUMERIC = 2; public final static int DECIMAL = 3;
     * 
     * public final static int CHAR = 1; public final static int VARCHAR = 12;
     * public final static int LONGVARCHAR = -1;
     * 
     * public final static int DATE = 91; public final static int TIME = 92;
     * public final static int TIMESTAMP = 93;
     * 
     * public final static int BINARY = -2; public final static int VARBINARY =
     * -3; public final static int LONGVARBINARY = -4;
     * 
     * public final static int NULL = 0;
     * 
     * 
     * 
     * @return java.lang.String
     * @param tableName
     *            java.lang.String
     * @param colNameAndType
     *            java.lang.String
     */
    public String createTable(String tableName, String colName1,
            String colDataType1, @SuppressWarnings("rawtypes") java.util.List lst)
            throws java.sql.SQLException {
        Connection con = null;
//        PreparedStatement stmt = null;
        JdbcSession session = getJdbcSession();
        try {
            con = getConnection();
            
            if(colName1.indexOf(",ts")==-1){
            	session.setAddTimeStamp(false);
            }
            TempTable tmpTool = new TempTable();

            String tgtTable = tmpTool.createTempTable(con, tableName, colName1
                    + " " + colDataType1,"");
//            +((colName1.indexOf(",ts")==-1)?",ts char(19)":"")
            String sql_insert =  "insert into " + tgtTable + " ("+ colName1 + ")  values(?) ";
            
            @SuppressWarnings("rawtypes")
            Iterator iter = lst.iterator();
            Object value = null;
            while (iter.hasNext()) {
                Object obj = iter.next();
                if (obj instanceof IBDData) {
                	IBDData bdDataVO = (IBDData) obj;

                    if (lst instanceof QryObjList) {
                        @SuppressWarnings("rawtypes")
                        QryObjList lst2 = (QryObjList) lst;
                        value = lst2.getFieldValue(bdDataVO);
                    } else {
                        value = bdDataVO.getPk();
                    }
                }else{
                    value = obj;
                }

                SQLParameter sqlParam = new SQLParameter();
                sqlParam.addParam(value);
                session.addBatch(sql_insert, sqlParam);
            }
            session.executeBatch();
            return tgtTable;
        } catch(Exception e){
            Log.getInstance(this.getClass()).error(e.getMessage());
            return null;
            }
        finally {

        	try {
        				if (session != null)
        					session.closeAll();
        			} catch (Exception e) {
        			}
        }
    }

    public String createTable(String tableName, String[] colNames,
            String[] dataTypes) throws java.sql.SQLException {
        java.sql.Connection con = null;
//        java.sql.PreparedStatement stmt = null;
        String tabname = null;
        try {
            con = getConnection();
            nc.bs.mw.sqltrans.TempTable tmpTool = new nc.bs.mw.sqltrans.TempTable();
            String colName1andDataType = "";
            String colName_insert = "";
            String value_insert = "";
            for (int i = 0; i < colNames.length; i++) {
                String temp = "";
                if (i > 0) {
                    temp += ",";
                    colName_insert += ",";
                    value_insert += ",";
                }
                temp += colNames[i] + " " + dataTypes[i];
                colName1andDataType += temp;
                colName_insert += colNames[i];
                value_insert += "?";
            }
            colName1andDataType+=",ts varchar(19) ";
            tabname=tmpTool.createTempTable(con, tableName, colName1andDataType,"");
        } finally {
//            try {
//                if (stmt != null) {
//                    stmt.close();
//                }
//            } catch (Exception e) {
//            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }
        return tabname;
    }

    public String createTable(String tableName, String[] colNames,
            String[] dataTypes, Object[][] datas) throws java.sql.SQLException, DbException {
       String tn= createTable(tableName, colNames, dataTypes);
        insertIntoTable(tn, colNames, dataTypes, datas);

        return tableName;
    }
//    private boolean isInclude(Object[] keys){
//    	if(keys==null||keys.length==0){
//    		return true;
//    	}
//    	String str_key="";
//    	for(int i =0; i<keys.length;i++){
//    		str_key+=keys[i];
//    	}
//    	if(m_hashBhxj.containsKey(str_key)){
//    		return true;
//    	}
//    	m_hashBhxj.put(str_key, keys[keys.length-1]);
//    	return false;
//
//    }
    public void insertIntoTable(String tableName, String[] colNames,
            String[] dataTypes, Object[][] datas) throws java.sql.SQLException, DbException {
        java.sql.Connection con = null;
//        java.sql.PreparedStatement stmt = null;
        JdbcSession session = getJdbcSession();
        try {

            	        con = getConnection();
            	        if(con instanceof CrossDBConnection){
            	        	((CrossDBConnection)con).setAddTimeStamp(false);
            	        }
            	  
            //	        nc.bs.mw.sqltrans.TempTable tmpTool = new
            // nc.bs.mw.sqltrans.TempTable();
            //            String colName1andDataType = "";
            String colName_insert = "";
            String value_insert = "";
            for (int i = 0; i < colNames.length; i++) {
                //            	String temp = "";
                if (i > 0) {
                    //            		temp += ",";
                    colName_insert += ",";
                    value_insert += ",";
                }
                //            	temp += colNames[i] + " " +dataTypes[i];
                //            	colName1andDataType += temp;
                colName_insert += colNames[i];
                value_insert += "?";
            }

//            stmt = prepareStatement(con, "insert into " + tableName + " ("
//                    + colName_insert + ")  values(" + value_insert + ") ");
            String sql_insert = "insert into " + tableName + " (" + colName_insert + ")  values(" + value_insert + ") ";

            for (int i = 0; i < datas.length; i++) {
                Object[] row = datas[i];
//                if(isInclude(row)){
//                	continue;
//                }
                SQLParameter sqlParam = new SQLParameter();
                for (int j = 0; j < row.length; j++) {
                    Object obj = row[j];
                    String value = (String) obj;
//                    int idx=j+1;;
//                    if (dataTypes[j].indexOf("char") != -1) {
//                        if (value == null) {
//                            stmt.setNull(idx, java.sql.Types.CHAR);
//                        } else {
//                            stmt.setString(idx, value.trim());
//                        }
//                    } else if (dataTypes[j].indexOf("numeric") != -1) {
//                        if (value == null) {
//                            stmt.setNull(idx, java.sql.Types.NUMERIC);
//                        } else {
//                            stmt.setDouble(idx, new Double(value.trim())
//                                    .doubleValue());
//                        }
//                    }         
                    
                    sqlParam.addParam(value);
                   
                }
                session.addBatch(sql_insert, sqlParam);
//                session.executeUpdate(sql_insert);
            }
//            executeBatch(stmt);
            session.executeBatch();
            //	        return tableName;
        } finally {
        	try {
        				if (session != null)
        					session.closeAll();
        			} catch (Exception e) {
        			}
        }
    }
    
    private JdbcSession getJdbcSession() {
    	PersistenceManager manager = null;
    	JdbcSession session = null;
    	try {
    		manager = PersistenceManager.getInstance(getds());
    	} catch (DbException e) {
    		Log.getInstance(this.getClass()).error(e.getMessage(),e);
    	}
    	session= manager.getJdbcSession();
    	
    	return session;
    }
    private String getds(){	
    	return InvocationInfoProxy.getInstance().getUserDataSource();
    }

    private Connection getConnection(){
    	JdbcSession session = getJdbcSession();
    	return session.getConnection();
    }

}