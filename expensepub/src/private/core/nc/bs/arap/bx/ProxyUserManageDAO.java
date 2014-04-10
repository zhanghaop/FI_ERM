package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.logging.Logger;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.exception.DbException;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.fipub.exception.ExceptionHandler;

/**
 * 代理用户管理数据库操作
 * @author wdh
 *
 */
public class ProxyUserManageDAO {
	
	private BaseDAO dao = null;
	
	
	
	public boolean executeUpdate(String sql,  SQLParameter parameter) throws DbException {
		dao = new BaseDAO();
		try {
			dao.executeUpdate(sql, parameter);
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
			return false;
		}
		return false;
	}
	
	public boolean executeUpdate(SqdlrVO[] vos) throws DbException {
		dao = new BaseDAO();
		try {
			dao.updateVOArray(vos);
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
		}
		return false;
	}

	
	public List<SqdlrVO> getResultVo(Class className, String condition) throws DbException {
		dao = new BaseDAO();
		Collection coll = null;
		List<SqdlrVO> ls = new ArrayList<SqdlrVO>();
		try {
			coll = dao.retrieveByClause(className, condition);
			//dao.
			if(coll != null){
				for(Object obj : coll){
					ls.add((SqdlrVO)obj);
				}
			}
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
		}
		return ls;
	}

	
	
	
	public boolean saveData(SqdlrVO[] vos) throws DbException {
		dao = new BaseDAO();
		try {
			dao.insertVOArray(vos);
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
			return false;
		}
		return true;
	}
	
	public boolean deleteData(SqdlrVO[] vos){
		dao = new BaseDAO();
		try {
			dao.deleteVOArray(vos);
		} catch (DAOException e) {
			ExceptionHandler.consume(e);
			return false;
		}
		return false;
	}

}
