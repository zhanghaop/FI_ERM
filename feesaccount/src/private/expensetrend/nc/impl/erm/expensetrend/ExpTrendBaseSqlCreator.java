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
 *   ���������ʱ���SqlCreatorͳһ���࣬  
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * @liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-04-25 08:27:29
 */
public abstract class ExpTrendBaseSqlCreator {

	protected static final String fixedFields = "@Table.pk_group,@Table.pk_org";

	protected static final String PK_CURR = "pk_currtype";

	protected static final String PK_CURR_ = "bzbm";

	// ��ѯ�������ɵ�VO
	protected ExpTrendQryVO queryVO = null;

	protected static String PK_ORG = "pk_org";

	protected static String PK_GROUP = "pk_group";


	private String compositeWhereSql = null;

	private String fromDummyTable = null;

	protected static final String bdTable = "bd";

	/**
	 * ��������ͨ�÷�����ȡ�����<br>
	 */
	public String getAlias(String strTableName) {
		return ReportSqlUtils.getAlias(strTableName);
	}

	protected String getMultiLangIndex() {
		int intIndex = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		return intIndex == 1 ? "" : String.valueOf(intIndex);
	}

	/**
	 * ��ȡ�ۺ�where��ѯ����
	 * <li>����Э�鵽����SQL
	 * <li>�����ѯģ��SQL
	 * <li>����Ȩ��SQL
	 * @param tempAlias 
	 * 
	 * @return String
	 * @throws BusinessException 
	 */
	protected String getCompositeWhereSql(String tempAlias) throws BusinessException {
		if (StringUtils.isEmpty(compositeWhereSql) || !compositeWhereSql.contains(tempAlias + ".")) {
			StringBuffer sqlBuffer = new StringBuffer();

			// �����ѯģ��SQL
			if (!StringUtils.isEmpty(queryVO.getSqlWhere())) {
				sqlBuffer.append(" and ").append(StringUtils.replace(queryVO.getSqlWhere(), "zb.", tempAlias + "."));
			}

			// �����ѯ����Ȩ��
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