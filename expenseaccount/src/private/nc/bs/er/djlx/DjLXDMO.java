package nc.bs.er.djlx;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.util.SqlUtils;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.bs.pub.SystemException;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFBoolean;

/**
 * DjLX的DMO类。
 * 
 * 创建日期：(2001-8-30)
 * 
 * @author：
 */
public class DjLXDMO {// extends DMO {
	/**
	 * DjLXDMO 构造子注解。
	 * 
	 * @exception javax.naming.NamingException
	 *                父类构造子抛出的异常。
	 * @exception nc.bs.pub.SystemException
	 *                父类构造子抛出的异常。
	 */
	public DjLXDMO() throws javax.naming.NamingException, SystemException {
		super();
	}

	/**
	 * DjLXDMO 构造子注解。
	 * 
	 * @param dbName
	 *            java.lang.String 在EJB Server中配置的数据库DataSource名称。
	 * @exception javax.naming.NamingException
	 *                父类构造子抛出的异常。
	 * @exception nc.bs.pub.SystemException
	 *                父类构造子抛出的异常。
	 */

	/**
	 * 根据VO中所设定的条件返回所有符合条件的VO数组
	 * 
	 * 创建日期：(2001-8-28)
	 * 
	 * @return nc.vo.arap.djlx.DjLXVO[]
	 * @param djLXVO
	 *            nc.vo.arap.djlx.DjLXVO
	 * @param isAnd
	 *            boolean 以与条件查询还是以或条件查询
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public boolean checkUnique(DjLXVO vo) throws BusinessException {

		String strSql = "dwbm = '" + vo.getDwbm() + "' and (djlxjc = '" + vo.getDjlxjc() + "' or djlxmc ='" + vo.getDjlxmc() + "' or djlxbm = '" + vo.getDjlxbm() + "')";
		if (vo.getDjlxoid() != null) {
			strSql += " and djlxoid<> '" + vo.getDjlxoid() + "'";
		}
		BaseDAO dao = new BaseDAO();
		boolean bool = false;
		Collection cl = dao.retrieveByClause(DjLXVO.class, strSql);
		if (cl.size() > 0) {
			bool = false;
		} else {
			bool = true;
		}

		return bool;
	}

	/**
	 * *方法说明:存在返回true,否则false; *参数: *返回值: ***注意点*** *@author：屈淑轩 *创建日期：(2001-12-19 13:15:21)
	 */
	public boolean isInUse(DjLXVO vo) throws BusinessException {

		String sTabName = BXConstans.JK_TABLENAME;
		if (BXConstans.JK_DJDL.equalsIgnoreCase(vo.getDjdl())) {
			sTabName = BXConstans.JK_TABLENAME;
		} else if (BXConstans.BX_DJDL.equalsIgnoreCase(vo.getDjdl())) {
			sTabName = BXConstans.BX_TABLENAME;
		}
		String sql = "select count(djlxbm) as num from " + sTabName + " where djlxbm ='" + vo.getDjlxbm() + "' and dwbm = '" + vo.getDwbm() + "' and dr=0";
		PersistenceManager manager = null;
		Object o = UFBoolean.TRUE;
		try {
			manager = PersistenceManager.getInstance(getds());
			JdbcSession session = manager.getJdbcSession();
			o = session.executeQuery(sql, new ResultSetProcessor() {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					Object o = UFBoolean.FALSE;
					if (rs.next()) {
						if (rs.getInt(1) != 0) {
							o = UFBoolean.TRUE;
						} else {
							o = UFBoolean.FALSE;
						}
					}
					return o;
				}
			});

		} catch (Exception e) {
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}

		return ((UFBoolean) o).booleanValue();
	}

	/**
	 * *方法说明: *参数: *返回值: ***注意点*** *@author：屈淑轩 *创建日期：(2001-12-19 13:15:21)
	 */
	public boolean isRefered(DjLXVO vo) {
		BaseDAO dao = new BaseDAO();
		try {
			Collection cl = dao.retrieveByClause(DjLXVO.class, "dwbm <>'0001' and djlxbm ='" + vo.getDjlxbm() + "'");
			if (cl.size() > 0) {
				return false;
			} else {
				return true;
			}

		} catch (DAOException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}

	}

	/**
	 * 通过单位编码返回指定公司所有记录VO数组。如果单位编码为空返回所有记录。
	 * 
	 * 已知问题：请注意生成的sql语句：where子句中假设公司编码字段为pk_corp。 如果你要针对公司进行查询，那么应采用你的实际字段名来手工修改 sql语句。 创建日期：(2003-3-17)
	 * 
	 * @return nc.vo.arap.djlx.DjlxVO[]
	 * @param unitCode
	 *            int
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public DjLXVO[] queryAllByInit(String pk_corp) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		Collection cl = dao.retrieveByClause(DjLXVO.class, " 1=1 order by dwbm,djlxbm");
		DjLXVO djlxs[] =  (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
		djlxs = new DjLXBO().queryBusiTypes(djlxs);
		return djlxs;

	}

	public DjLXVO getDjlxvoByDjlxbm(String djlxbm, String pk_corp) throws BusinessException {
		try {
			return queryByDjbm(djlxbm, pk_corp)[0];
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage());
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * 通过单位编码返回指定公司所有记录VO数组。如果单位编码为空返回所有记录。
	 * 
	 * 已知问题：请注意生成的sql语句：where子句中假设公司编码字段为pk_corp。 如果你要针对公司进行查询，那么应采用你的实际字段名来手工修改 sql语句。 创建日期：(2003-3-17)
	 * 
	 * @return nc.vo.arap.djlx.DjlxVO[]
	 * @param unitCode
	 *            int
	 * @exception java.sql.SQLException
	 *                异常说明。
	 */
	public DjLXVO[] queryByDjbm(String djlxbm, String pk_corp) {

		String sWhere = "";
		if (djlxbm != null) {
			sWhere += "and djlxbm='" + djlxbm + "' ";
		}
		if (pk_corp != null)
			sWhere += "and dwbm='" + pk_corp + "' ";
		if (sWhere.length() > 1) {
			sWhere = sWhere.substring(3);
		}
		BaseDAO dao = new BaseDAO();
		Collection cl;
		try {
			cl = dao.retrieveByClause(DjLXVO.class, sWhere);
		} catch (DAOException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}

		DjLXVO djlxs[] =  (DjLXVO[]) changeCollection2Array(cl, DjLXVO.class);
		try {
			djlxs = new DjLXBO().queryBusiTypes(djlxs);
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage());
		}
		return djlxs;

	}

	@SuppressWarnings("unchecked")
	public Vector<DjLXVO> getByPrimaryKeys(String[] pks, String pk_corp) throws BusinessException {

		String sql;
		try {
			sql = SqlUtils.getInStr("djlxoid", pks);
			if (pk_corp != null) {
				sql += " and dwbm='" + pk_corp + "'";
			}
			Vector<DjLXVO> v = new Vector<DjLXVO>();
			BaseDAO dao = new BaseDAO();
			Collection<DjLXVO> cl = dao.retrieveByClause(DjLXVO.class, sql);
			new DjLXBO().queryBusiTypes((DjLXVO[]) cl.toArray(new DjLXVO[] {}));
			v.addAll(cl);
			return v;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			ExceptionHandler.handleException(this.getClass(), e);
		}
		return null;

	}

	private String getds() {
		return InvocationInfoProxy.getInstance().getUserDataSource();
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getDjdlByDjlxbm(String[] djlxbms) throws Exception {

		String sql = "select distinct djlxbm,djdl from arap_djlx where " + SqlUtils.getInStr("djlxbm", djlxbms);
		PersistenceManager manager = null;
		try {
			manager = PersistenceManager.getInstance(getds());
			JdbcSession session = manager.getJdbcSession();
			return (Map<String, String>) session.executeQuery(sql, new ResultSetProcessor() {
				private static final long serialVersionUID = 1L;

				public Object handleResultSet(ResultSet rs) throws SQLException {
					Map<String, String> temp = new HashMap<String, String>();
					if (rs.next()) {
						temp.put(rs.getString("djlxbm"), rs.getString("djdl"));
					}
					return temp;
				}
			});

		} finally {
			if (manager != null)
				manager.release();
		}

	}
	
	private Object changeCollection2Array(Collection cl, Class sClass) {
		if (cl.isEmpty()) {
			return null;
		}
		Object[] o = (Object[]) Array.newInstance(sClass, cl.size());
		o = cl.toArray(o);
		return o;

	}
}
