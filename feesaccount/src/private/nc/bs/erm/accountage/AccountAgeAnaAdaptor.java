package nc.bs.erm.accountage;

import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.sql.ErmBaseSqlCreator;
import nc.bs.logging.Logger;
import nc.utils.fipub.MemoryResultSetProcessor;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.rs.MemoryResultSet;

/**
 * 账龄分析适配器<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
public abstract class AccountAgeAnaAdaptor implements IAccountAgeAna {
	
	private BaseDAO dao = null;
	protected ReportQueryCondVO queryVO = null;
	
	
	public MemoryResultSet getAccountAgeAnaResult(ReportQueryCondVO queryVO)	throws BusinessException {
		try {
			ErmBaseSqlCreator sqlCreator = getSqlCreator();
			sqlCreator.setParams(queryVO);
		
			String[] arrangeSqls = sqlCreator.getArrangeSqls();
			String resultSql = sqlCreator.getResultSql();
			
			String[] dropTableSqls = sqlCreator.getDropTableSqls();
		
			for (String sql : arrangeSqls) {
				executeUpdate(sql);
			}
		
			MemoryResultSet resultSet = executeQuery(resultSql);
		
			for (String sql : dropTableSqls) {
				executeUpdate(sql);
			}
		
			return resultSet;
		} catch (SQLException e) {
			Logger.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
	}
	/**
	 * 获取SQL生成器<br>
	 * 
	 * @return ArapBaseSqlCreator<br>
	 */
	protected abstract ErmBaseSqlCreator getSqlCreator();

	protected MemoryResultSet executeQuery(String sql) throws DAOException {
		return (MemoryResultSet) getBaseDAO().executeQuery(sql, new MemoryResultSetProcessor());
	}

	protected void executeUpdate(String sql) throws DAOException {
		if (StringUtils.isEmpty(sql)) {
			return;
		}
		getBaseDAO().executeUpdate(sql);
	}

	protected BaseDAO getBaseDAO() {
		if (dao == null) {
			dao = new BaseDAO();
			dao.setAddTimeStamp(false);
		}
		return dao;
	}

}

