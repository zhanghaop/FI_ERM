package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.processor.ArrayListProcessor;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDateTime;

/**
 * @author twei
 *
 * nc.bs.arap.bx.BXSuperDAO
 * 
 * 
 * 	 * !!!!!注意!!!!：
 *   参数为数组的方法直接调用baseDao的更新方法，不能同时更新报销单和借款单
 */

public class BXSuperDAO{
	
	public BaseDAO baseDao = new BaseDAO();

	public SuperVO[] save(SuperVO[] vos) throws DAOException {
		
		baseDao.insertVOArray(vos);
		
		return vos;
	}
	

	/**
	 * @param vos
	 * @return
	 * @throws DAOException
	 * 
	 * 更新报销vo数组
	 * 
	 */
	public SuperVO[] update(SuperVO[] vos) throws DAOException {
		
		baseDao.updateVOArray(vos);
		
		addTsToBXVOs(vos);
		
		return vos;
	}
	
	
	public SuperVO[] update(SuperVO[] vos,String[] fields) throws DAOException {
		
		baseDao.updateVOArray(vos,fields);
		
		addTsToBXVOs(vos);
		
		return vos;
	}
	
	public void delete(SuperVO[] vos) throws DAOException {
		
		baseDao.deleteVOArray(vos);
		
	}
	
	
	public void delete(Class className,String field, String[] values) throws DAOException, SQLException {
		baseDao.deleteByClause(className, SqlUtils.getInStr(field,values));
	}
	
	
	protected void addTsToBXVOs(SuperVO[] vos) throws DAOException{
		if(vos==null||vos.length==0)
			return;
		List<String> pks=new ArrayList<String>();
		for (int i = 0; i < vos.length; i++) {
			pks.add(vos[i].getPrimaryKey());
		}
		String tablename=vos[0].getTableName();
		String pkfield=vos[0].getPKFieldName();
		try {
			Map<String,UFDateTime> tsMap = getTsMap(pks, tablename, pkfield);
			
			for (int i = 0; i < vos.length; i++) {
				vos[i].setAttributeValue("ts", tsMap.get(vos[i].getPrimaryKey()));
			}
			
		} catch (DbException e) {
			ExceptionHandler.consume(e);
			throw new DAOException(e.getMessage(),e);
		}
	}
	
	public BXSuperDAO() throws NamingException {
		super();
	}
	
	public Map<String,UFDateTime> getTsMap(List<String> pkAry,String tablename,String pkfield) throws DbException{
		Map<String,UFDateTime> newTsMap = new HashMap<String,UFDateTime>();
		String[] strTemp = getTsSqlStr(pkAry, tablename, pkfield);
		for (int i = 0; i < strTemp.length; i++) {
			List lResult = getTsChanged(strTemp[i]);
			for (Iterator iter = lResult.iterator(); iter.hasNext();) {
				Object[] objs = (Object[]) iter.next();
				newTsMap.put(objs[0].toString(), new UFDateTime(objs[1].toString()));
			}
		}
		return newTsMap;
	}
	
	private List getTsChanged(String strSql) throws DbException {
		PersistenceManager persist = null;
		try {
			persist = PersistenceManager.getInstance();
			JdbcSession jdbc = persist.getJdbcSession();
			List lRet = (List) jdbc.executeQuery(strSql, new ArrayListProcessor());
			return lRet;
		} finally {
			if (persist != null)
				persist.release();
		}

	}

	private String[] getTsSqlStr(List<String> pkAry, String tableName, String fieldName) {
		
		String[] strTemp = getSplitSqlIn(pkAry);

		for (int i = 0; i < strTemp.length; i++) {
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("select ");
			sqlBuffer.append(fieldName);
			sqlBuffer.append(",ts from ");
			sqlBuffer.append(tableName);
			sqlBuffer.append(" where ");
			sqlBuffer.append(fieldName);
			sqlBuffer.append(" in ");
			sqlBuffer.append(strTemp[i]);
			strTemp[i] = sqlBuffer.toString();
		}
		return strTemp;
	}
	
	private static String[] getSplitSqlIn(List<String> pkAry) {
		/**-------------------增加了拆分功能-----------------*/
		int PACKAGESIZE = 400;
		int fixedPKLength = pkAry.size();
		int numofPackage = fixedPKLength / PACKAGESIZE + (fixedPKLength % PACKAGESIZE > 0 ? 1 : 0);
		/**-------------------------------------------------*/

		/**根据包的个数组成SQL数组*/
		String[] strTemp = new String[numofPackage];

		/**根据包的个数循环构建numofPackage个SQL数组*/
		for (int s = 0; s < numofPackage; s++) {
			StringBuffer strArys = new StringBuffer();
			/**确定起始点和中止点*/
			int beginIndex = s * PACKAGESIZE;
			int endindex = beginIndex + PACKAGESIZE;
			/**长度超出后进行微调*/
			if (endindex > fixedPKLength)
				endindex = fixedPKLength;

			for (int i = beginIndex; i < endindex; i++) {
				strArys.append("'").append(pkAry.get(i)).append("',");
			}
			
			strTemp[s] = "(" + strArys.toString().substring(0, strArys.toString().length() - 1) + ")";
		}
		return strTemp;
	}
}
