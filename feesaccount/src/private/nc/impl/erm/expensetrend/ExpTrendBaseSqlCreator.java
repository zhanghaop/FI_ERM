package nc.impl.erm.expensetrend;

import java.sql.SQLException;

import nc.bs.erm.util.ReportSqlUtils;
import nc.itf.erm.expensetrend.ExpTrendQryVO;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;
/**
 * <p>
 *   报销管理，帐表部分SqlCreator统一父类，  
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * @liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-04-25 08:27:29
 */
public abstract class ExpTrendBaseSqlCreator {

	protected static final String fixedFields = "@Table.pk_group,@Table.pk_org";

	protected static final String PK_CURR = "pk_currtype";

	protected static final String PK_CURR_ = "bzbm";

	// 查询条件构成的VO
	protected ExpTrendQryVO queryVO = null;

	protected static String PK_ORG = "pk_org";

	protected static String PK_GROUP = "pk_group";


	private String compositeWhereSql = null;

	private String fromDummyTable = null;

	protected static final String bdTable = "bd";

	/**
	 * 报销管理通用方法：取表别名<br>
	 */
	public String getAlias(String strTableName) {
		return ReportSqlUtils.getAlias(strTableName);
	}

	protected String getMultiLangIndex() {
		int intIndex = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		return intIndex == 1 ? "" : String.valueOf(intIndex);
	}

	/**
	 * 获取综合where查询条件
	 * <li>处理协议到期日SQL
	 * <li>处理查询模板SQL
	 * <li>处理权限SQL
	 * @param tempAlias 
	 * 
	 * @return String
	 * @throws BusinessException 
	 */
	protected String getCompositeWhereSql(String tempAlias) throws BusinessException {
		if (StringUtils.isEmpty(compositeWhereSql) || !compositeWhereSql.contains(tempAlias + ".")) {
			StringBuffer sqlBuffer = new StringBuffer();

			// 处理查询模板SQL
			if (!StringUtils.isEmpty(queryVO.getSqlWhere())) {
				sqlBuffer.append(" and ").append(StringUtils.replace(queryVO.getSqlWhere(), "zb.", tempAlias + "."));
			}

			// 处理查询数据权限
			String powerSql = ReportSqlUtils.getDataPermissionSql(ReportSqlUtils
					.getUserIdForServer(), ReportSqlUtils.getPkGroupForServer(),
					IPubReportConstants.FI_REPORT_REF_POWER,"cs");

			if (!StringUtils.isEmpty(powerSql)) {
				sqlBuffer.append(powerSql);
			}

			compositeWhereSql = sqlBuffer.toString();
		}

		return compositeWhereSql;
	}	

	public abstract String[] getArrangeSqls() throws SQLException, BusinessException;

	public abstract String getResultSql() throws SQLException, BusinessException;

	public abstract String[] getDropTableSqls() throws SQLException, BusinessException;

	protected String getFromDummyTable() {
		if (fromDummyTable == null) {
			if (SqlBuilder.getDatabaseType() == DBConsts.SQLSERVER) {
				fromDummyTable = " ";
			} else if (SqlBuilder.getDatabaseType() == DBConsts.ORACLE) {
				fromDummyTable = " from dual ";
			} else if (SqlBuilder.getDatabaseType() == DBConsts.DB2) {
				fromDummyTable = " from sysibm.sysdummy1 ";
			}
		}

		return fromDummyTable;
	}
}