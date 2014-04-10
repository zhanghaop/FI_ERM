package nc.ui.erm.report.release;

import java.sql.SQLException;

import nc.vo.pub.rs.MemoryResultSet;

public class ErmCommonReportMethod {
	public static final String CUSTOMER = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001589")/* @res "客户" */;
	public static final String BD_CUSTOMER = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("feesaccount_0", "02011001-0032")/*
																		 * @res
																		 * "客户档案"
																		 */;

	public static final String DEPT = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0004064")/* @res "部门" */;
	public static final String BD_DEPT = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0004064")/* @res "部门" */;

	public static final String PERSON = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UCMD1-000159")/* @res "业务员" */;
	public static final String BD_PERSON = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0000129")/* @res "人员" */;

	public static final String INVMAN = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001439")/* @res "存货" */;
	public static final String BD_INVMAN = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC001-0000080")/* @res "存货档案" */;

	public static final String COSTSUBJ = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0002217")/* @res "收支项目" */;
	public static final String BD_COSTSUBJ = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0002217")/* @res "收支项目" */;

	public static final String CUSTBAS = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001574")/* @res "客商" */;
	public static final String BD_CUSTBAS = nc.vo.ml.NCLangRes4VoTransl
			.getNCLangRes().getStrByID("common", "UC000-0001584")/* @res "客商档案" */;

	/**
	 * 功能：根据统计维度dimen，把结果集originSet和unionSet按照求和字段sumFld进行合并<br>
	 * 
	 * @param originSet
	 *            源结果集<br>
	 * @param unionSet
	 *            合并结果集<br>
	 * @param base
	 *            统计维度<br>
	 * @param sumFld
	 *            求和字段<br>
	 * @return 合并后的结果集<br>
	 */
	public static MemoryResultSet unionMemoryResultSet(
			MemoryResultSet originSet, MemoryResultSet unionSet,
			String[] dimen, String[] sumFld) {
		// TODO:
		return null;
	}

	/**
	 * 转换基本档案名称为持久层标识<br>
	 * 
	 * @param bdName
	 *            基本档案名称<br>
	 * @return 持久层标识<br>
	 */
	public static String convertBD2Fld(Object bdName) {
		String fld = "";
		if (CUSTOMER.equals(bdName)) {
			fld = "customer";
		} else if (DEPT.equals(bdName)) {
			fld = "pk_dept";
		} else if (PERSON.equals(bdName)) {
			fld = "pk_psndoc";
		}
		return fld;
	}

	/**
	 * 功能：计算期末余额<br>
	 * 
	 * @param resultSet内存结果集
	 * <br>
	 * @throws SQLException
	 * <br>
	 */
	public static void computeEndPeriodBalance(MemoryResultSet resultSet)
			throws SQLException {
		String field = "QMBBYE";
		String formula = field + "->qcbbye+jfbbje-dfbbje";
		resultSet.appendClumnByDefaultValue(field, "");
		resultSet.setClumnByFormulate(new String[] { field },
				new String[] { formula });
	}
}

// /:~