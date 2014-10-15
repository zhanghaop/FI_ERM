package nc.bs.er.control;

/**
 * 此处插入类型描述。
 * 创建日期：(2004-3-5 14:00:59)
 * @author：钟悦
 */

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Log;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.vo.arap.bdcontrastinfo.BdcontrastinfoVO;
import nc.vo.erm.control.TestChangeVO;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.rs.ComTool;
import nc.vo.pub.rs.MemoryResultSet;

public class QueryFuncDAO  {

/**
 * QueryFuncDmo 构造子注解。
 * @exception javax.naming.NamingException 异常说明。
 * @exception nc.bs.pub.SystemException 异常说明。
 */
public QueryFuncDAO()  {
	super();
}
/**
 * 所有项目的code-pk
 * 创建日期：(2004-3-8 11:24:59)
 */
private HashMap<Object, Vector<CircularlyAccessibleValueObject>> fenzu(Vector vect) {

    if (vect == null || vect.size() == 0) {
        return new HashMap<Object, Vector<CircularlyAccessibleValueObject>>();
    }
    //按类别分组
    HashMap<Object, Vector<CircularlyAccessibleValueObject>> hash = new java.util.HashMap<Object, Vector<CircularlyAccessibleValueObject>>(); //fieldName-itemvo

    for (int i = 0; i < vect.size(); i++) {
        nc.vo.pub.CircularlyAccessibleValueObject itemvo =
            (nc.vo.pub.CircularlyAccessibleValueObject) vect.get(i);
        Object fieldName = itemvo.getAttributeValue("m_FieldName");
        if (fieldName == null) {
            fieldName = "#null#";
        }
        if (!hash.containsKey(fieldName)) {
            Vector<CircularlyAccessibleValueObject> v = new Vector<CircularlyAccessibleValueObject>();
            v.add(itemvo);
            hash.put(fieldName, v);
        } else {
            Vector<CircularlyAccessibleValueObject> v = hash.get(fieldName);
            v.add(itemvo);
        }
    }

    return hash;

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public Connection getConn()  {

    return getConnection();

}

/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public HashMap<String, ArrayList<UFDouble>> query(String sql, int groupCount, Connection con,String sAppendKey)
    throws SQLException, Exception {
    HashMap<String, ArrayList<UFDouble>> hash = null;
    PreparedStatement stmt = null;
    try {
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        hash = new HashMap<String, ArrayList<UFDouble>>();
        while (rs.next()) {
            String attrValues = "";
            for (int i = 0; i < groupCount; i++) {
                String s = rs.getString(i + 1);
                attrValues += s + TestChangeVO.Spliter;
            }
            attrValues+=sAppendKey;
//            BigDecimal ybje = rs.getBigDecimal(groupCount + 1); //ybje
//            BigDecimal fbje = rs.getBigDecimal(groupCount + 2); //fbje
//            BigDecimal bbje = rs.getBigDecimal(groupCount + 3); //bbje
//
//            ArrayList<UFDouble> array = new ArrayList<UFDouble>(); //按 yuan fu ben 的顺序加到arraylist中
//            array.add(new UFDouble(ybje));
//            array.add(new UFDouble(fbje));
//            array.add(new UFDouble(bbje));
          BigDecimal globalbbje = rs.getBigDecimal(groupCount + 1); //globalbbje
          BigDecimal groupbbje = rs.getBigDecimal(groupCount + 2); //groupbbje
          BigDecimal bbje = rs.getBigDecimal(groupCount + 3); //bbje
          BigDecimal ybje = rs.getBigDecimal(groupCount + 4); //ybje

          ArrayList<UFDouble> array = new ArrayList<UFDouble>(); //按 globalbbje,groupbbje,bbje,ybje 的顺序加到arraylist中
          array.add(new UFDouble(globalbbje));
          array.add(new UFDouble(groupbbje));
          array.add(new UFDouble(bbje));
          array.add(new UFDouble(ybje));
          hash.put(attrValues, array);

        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }

    }

    return hash;

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public HashMap<String, String> queryAllCorp() throws SQLException, Exception {

    String sql = "select unitcode,pk_corp from bd_corp where dr=0 ";
    HashMap<String, String> hash = new HashMap<String, String>();
    Connection con = null;
    PreparedStatement stmt = null;

    try {
        con = getConnection();
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        //
        while (rs.next()) {
            String code = rs.getString(1);
            String pk = rs.getString(2);
            if (code != null && pk != null) {
                hash.put(code.trim(), pk.trim());
            }

        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
    //
    return hash;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public Vector<CircularlyAccessibleValueObject> queryCust(Vector<CircularlyAccessibleValueObject> vect) throws SQLException, Exception {
    if (vect == null || vect.size() == 0) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String custCode = "";
    Vector<CircularlyAccessibleValueObject> vect2 = new Vector<CircularlyAccessibleValueObject>();
    for (int i = 0; i < vect.size(); i++) {
        CircularlyAccessibleValueObject itemvo =vect.get(i);
        Object fieldName = itemvo.getAttributeValue("m_FieldName");
        if (fieldName == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000049")/*@res "m_FieldName 参数值为空"*/);
        }
        if (!fieldName.toString().trim().equals(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000050")/*@res "客户档案"*/)) {
            continue;
        }
        vect2.add(itemvo);
        if (i > 0) {
            custCode += " or ";
        }

        Object code = itemvo.getAttributeValue("m_CodeValue");
        if (code == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000051")/*@res "code 参数值为空"*/);
        }
        String[] codesArray = (String[]) code;
        String temp = "";
        for (int j = 0; j < codesArray.length; j++) {

            if (j == codesArray.length - 1) {
                temp += "'" + codesArray[j].toString().trim() + "' ";
            } else {
                temp += "'" + codesArray[j].toString().trim() + "', ";
            }
        }
        temp = " custcode in (" + temp + ")";
        Object pkCorp = itemvo.getAttributeValue("m_PkCorp");
        if (pkCorp == null || pkCorp.toString().trim().equals("")) {
            Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0096")/*@res "没有设置QueryItemVO的公司pk"*/);/*-=nottranslate=-*/
        }

        temp += " and pk_corp ='" + pkCorp + "' ";
        custCode += temp;
    }
    if (custCode.equals("")) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String sql = "select custcode,pk_cumandoc from bd_cumanddoc where dr=0 and " + custCode;

    java.util.HashMap<String, String> hash = new HashMap<String, String>();
    Connection con = null;
    PreparedStatement stmt = null;
    try {
        con = getConnection();
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        //
        while (rs.next()) {

            // deptcode :
            String code = rs.getString(1);
            String pk = rs.getString(2);
            hash.put(code, pk);
        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
    //回写到QueryItemVO
    setPk(vect2, hash);

    return vect; //返回参数的副作用

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public Vector<CircularlyAccessibleValueObject> queryDept(Vector<CircularlyAccessibleValueObject> vectDept) throws SQLException, Exception {
    if (vectDept == null || vectDept.size() == 0) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String deptCode = "";
    Vector<CircularlyAccessibleValueObject> vectDept2 = new Vector<CircularlyAccessibleValueObject>();
    for (int i = 0; i < vectDept.size(); i++) {
        CircularlyAccessibleValueObject itemvo = vectDept.get(i);
        Object fieldName = itemvo.getAttributeValue("m_FieldName");
        if (fieldName == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000049")/*@res "m_FieldName 参数值为空"*/);
        }
        if (!fieldName.toString().trim().equals(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("common","UC000-0004064")/*@res "部门"*/)) {
            continue;
        }
        vectDept2.add(itemvo);
        if (i > 0) {
            deptCode += " or ";
        }

        //code是字符串数组
        Object code = itemvo.getAttributeValue("m_CodeValue");
        if (code == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000051")/*@res "code 参数值为空"*/);
        }
        String[] codesArray = (String[]) code;
        String temp = "";
        for (int j = 0; j < codesArray.length; j++) {

            if (j == codesArray.length - 1) {
                temp += "'" + codesArray[j].toString().trim() + "' ";
            } else {
                temp += "'" + codesArray[j].toString().trim() + "', ";
            }
        }
        temp = " deptcode in (" + temp + ")";
        Object pkCorp = itemvo.getAttributeValue("m_PkCorp");
        if (pkCorp == null || pkCorp.toString().trim().equals("")) {
            Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0096")/*@res "没有设置QueryItemVO的公司pk"*/);
        }

        temp += " and pk_corp ='" + pkCorp + "' ";
        deptCode += temp;
    }
    if (deptCode.equals("")) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String sql = "select deptcode,pk_deptdoc from bd_deptdoc where dr=0 and " + deptCode;

    java.util.HashMap<String, String> hash = new HashMap<String, String>();
    Connection con = null;
    PreparedStatement stmt = null;
    try {
        con = getConnection();
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        //
        while (rs.next()) {

            String code = rs.getString(1);
            String pk = rs.getString(2);
            hash.put(code, pk);
        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
    setPk(vectDept2, hash);

    return vectDept; //返回参数的副作用

}
/**
 * 所有项目的code-pk
 * 创建日期：(2004-3-8 11:24:59)
 */
public Vector<CircularlyAccessibleValueObject> queryPk(Vector<CircularlyAccessibleValueObject> vect) throws SQLException, Exception {

	HashMap<Object, Vector<CircularlyAccessibleValueObject>> hashmap = fenzu(vect);
    Iterator<Vector<CircularlyAccessibleValueObject>> iter = hashmap.values().iterator();
    java.util.HashMap<String, String> hash = new HashMap<String, String>();

    while (iter.hasNext()) {
    	Vector<CircularlyAccessibleValueObject> vector = iter.next();
        String code = "";
        BdcontrastinfoVO bdContrastVO = null;
        bdContrastVO = null;
        String codeFieldName = null;

        for (int i = 0; i < vector.size(); i++) {
            nc.vo.pub.CircularlyAccessibleValueObject itemvo =vector.get(i);
            bdContrastVO =
                (BdcontrastinfoVO) itemvo.getAttributeValue("m_BdContrastVO");

            if (i > 0) {
                code += " or ";
            }

            Object codes = itemvo.getAttributeValue("m_CodeValue");
            if (codes == null) {
                throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000051")/*@res "code 参数值为空"*/);
            }
            String[] codesArray = (String[]) codes;
            String temp = "";
            for (int j = 0; j < codesArray.length; j++) {

                if (j == codesArray.length - 1) {
                    temp += "'" + codesArray[j].toString().trim() + "' ";
                } else {
                    temp += "'" + codesArray[j].toString().trim() + "', ";
                }
            }
            codeFieldName = bdContrastVO.getCodefieldname();
            temp = codeFieldName + " in (" + temp + ")";
            Object pkCorp = itemvo.getAttributeValue("m_PkCorp");
            if (pkCorp == null || pkCorp.toString().trim().equals("")) {
                Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0096")/*@res "没有设置QueryItemVO的公司pk"*/);
            }

            temp += " and pk_corp ='" + pkCorp + "' ";
            code += temp;
        }

        String tablePkName = bdContrastVO.getTablepkname();
        String tableName = bdContrastVO.getTablename();
        String sql =
            "select "
                + codeFieldName
                + ","
                + tablePkName
                + " from "
                + tableName
                + " where dr=0 and "
                + code;
        if (code.trim().length() == 0) {
            sql =
                "select " + codeFieldName + "," + tablePkName + " from " + tableName + " where dr=0 ";
        }

        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = getConnection();
            stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            //
            while (rs.next()) {
                String str_code = rs.getString(1);
                String str_pk = rs.getString(2);
                hash.put(str_code, str_pk);
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception e) {
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
            }
        }

}
    setPk(vect, hash);
    return vect; //返回参数的副作用

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public Vector<CircularlyAccessibleValueObject> queryRyda(Vector<CircularlyAccessibleValueObject> vect) throws SQLException, Exception {
    if (vect == null || vect.size() == 0) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String rydaCode = "";
    Vector<CircularlyAccessibleValueObject> vect2 = new Vector<CircularlyAccessibleValueObject>();
    for (int i = 0; i < vect.size(); i++) {
        nc.vo.pub.CircularlyAccessibleValueObject itemvo =vect.get(i);
        Object fieldName = itemvo.getAttributeValue("m_FieldName");
        if (fieldName == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000049")/*@res "m_FieldName 参数值为空"*/);
        }
        if (!fieldName.toString().trim().equals(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000052")/*@res "人员档案"*/)) {
            continue;
        }
        vect2.add(itemvo);
        if (i > 0) {
            rydaCode += " or ";
        }
        Object code = itemvo.getAttributeValue("m_CodeValue");
        if (code == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000051")/*@res "code 参数值为空"*/);
        }
        String[] codesArray = (String[]) code;
        String temp = "";
        for (int j = 0; j < codesArray.length; j++) {

            if (j == codesArray.length - 1) {
                temp += "'" + codesArray[j].toString().trim() + "' ";
            } else {
                temp += "'" + codesArray[j].toString().trim() + "', ";
            }
        }
        temp = " psncode in (" + temp + ")";
        Object pkCorp = itemvo.getAttributeValue("m_PkCorp");
        if (pkCorp == null || pkCorp.toString().trim().equals("")) {
            Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0096")/*@res "没有设置QueryItemVO的公司pk"*/);
        }

        temp += " and pk_corp ='" + pkCorp + "' ";
        rydaCode += temp;
    }
    if (rydaCode.equals("")) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String sql = "select psncode,pk_psndoc from bd_psndoc where dr=0 and " + rydaCode;

    java.util.HashMap<String, String> hash = new HashMap<String, String>();
    Connection con = null;
    PreparedStatement stmt = null;
    try {
        con = getConnection();
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        //
        while (rs.next()) {

            // deptcode :
            String code = rs.getString(1);
            String pk = rs.getString(2);
            hash.put(code, pk);
        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
    //回写到QueryItemVO
    setPk(vect2, hash);

    return vect; //返回参数的副作用

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
public Vector<CircularlyAccessibleValueObject> querySzxm(Vector<CircularlyAccessibleValueObject> vectSzxm) throws SQLException, Exception {
    if (vectSzxm == null || vectSzxm.size() == 0) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String szxmCode = "";
    Vector<CircularlyAccessibleValueObject> vectSzxm2 = new Vector<CircularlyAccessibleValueObject>();
    for (int i = 0; i < vectSzxm.size(); i++) {
        nc.vo.pub.CircularlyAccessibleValueObject itemvo =vectSzxm.get(i);
        Object fieldName = itemvo.getAttributeValue("m_FieldName");
        if (fieldName == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000049")/*@res "m_FieldName 参数值为空"*/);
        }
        if (!fieldName.toString().trim().equals(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("common","UC000-0002217")/*@res "收支项目"*/)) {
            continue;
        }
        vectSzxm2.add(itemvo);
        if (i > 0) {
            szxmCode += " or ";
        }

        Object code = itemvo.getAttributeValue("m_CodeValue");
        if (code == null) {
            throw new Exception(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000051")/*@res "code 参数值为空"*/);
        }
        String[] codesArray = (String[]) code;
        String temp = "";
        for (int j = 0; j < codesArray.length; j++) {

            if (j == codesArray.length - 1) {
                temp += "'" + codesArray[j].toString().trim() + "' ";
            } else {
                temp += "'" + codesArray[j].toString().trim() + "', ";
            }
        }
        temp = " costcode in (" + temp + ")";
        Object pkCorp = itemvo.getAttributeValue("m_PkCorp");
        if (pkCorp == null || pkCorp.toString().trim().equals("")) {
            Log.getInstance(this.getClass()).debug(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0096")/*@res "没有设置QueryItemVO的公司pk"*/);
        }

        temp += " and pk_corp ='" + pkCorp + "' ";
        szxmCode += temp;
    }
    if (szxmCode.equals("")) {
        return new Vector<CircularlyAccessibleValueObject>();
    }
    String sql = "select costcode,pk_costsubj from bd_costsubj where dr=0 and " + szxmCode;

    java.util.HashMap<String, String> hash = new HashMap<String, String>();
    Connection con = null;
    PreparedStatement stmt = null;
    try {
        con = getConnection();
        stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String code = rs.getString(1);
            String pk = rs.getString(2);
            hash.put(code, pk);
        }
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (Exception e) {
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
        }
    }
    setPk(vectSzxm2, hash);

    return vectSzxm; //返回参数的副作用

}
/**
 * 此处插入方法描述。
 * 创建日期：(2004-3-8 11:24:59)
 */
private void setPk(Vector<CircularlyAccessibleValueObject> vectDept, HashMap<String,String> hash) throws Exception {

    for (int i = 0; i < vectDept.size(); i++) {
        nc.vo.pub.CircularlyAccessibleValueObject itemvo =vectDept.get(i);

        Object code = itemvo.getAttributeValue("m_CodeValue");
        if (code == null) {
//            nc.vo.pub.rs.Debug.println("编码值为空");
        }
        String[] codesArr = (String[]) code;
        if (codesArr.length == 0) {
//            nc.vo.pub.rs.Debug.println("编码值为空");
        }
        String[] keys = new String[codesArr.length];
        for (int j = 0; j < codesArr.length; j++) {
            Object pk = hash.get(codesArr[j].trim());
            if (pk == null || pk.toString().trim().equals("")) {
                //throw new Exception("code没有相应的pk");
            } else {
                keys[j] = pk.toString().trim();
            }
        }
        itemvo.setAttributeValue("m_PkValue", keys);
    }
}
public MemoryResultSet execQuery(String sql)throws SQLException{
    MemoryResultSet mrs = null;
	Connection con = null;
	try {
		con = getConnection();
		mrs= ComTool.getMemoryResultSet(con,sql);
	} catch (Exception e) {
//		nc.vo.pub.rs.Debug.println(e);
		throw new SQLException(nc.bs.ml.NCLangResOnserver.getInstance().getStrByID("20060504","UPP20060504-000062")/*@res "在ListQueryDMO::getQueryResult(String[] strSQL)中发生异常"*/);
	} finally {
		try {
			if (con != null)
				con.close();
		} catch (Exception e) {
		}
	}
	return mrs;
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