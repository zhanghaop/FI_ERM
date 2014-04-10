package nc.bs.erm.sql;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nc.bs.er.util.SqlUtils;
import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.utils.fipub.SmartProcessor;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *      用来构造费用汇总表所涉及的sql
 *      针对费用汇总表，包括几个部分：  
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 * @liansg
 * @see 
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午08:27:29
 */
public class ExpenseBalanceSQLCreator extends ErmBaseSqlCreator {

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;

	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getExpenseAccumulativeOccur());
		sqlList.add(getComputeTotalSql());

		return sqlList.toArray(new String[0]);
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}

	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", (case when isnull(org_orgs.code, '~') = '~' then 1 else 0 end) as is_org_null"); // is_org_null
		sqlBuffer.append(", org_orgs.code code_org, coalesce(org_orgs.name").append(getMultiLangIndex()).append(", org_orgs.name) org"); // code_org, org

		String[] qryObjs = getQueryObjs();
		List<QryObj> qryObjList = queryVO.getQryObjs();

		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", v.").append(qryObjs[i]).append(", ");

			sqlBuffer.append(bdTable + i).append(".").append(qryObjList.get(i).getBd_codeField()).append(" ")
					.append(IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code, ");

			sqlBuffer.append("coalesce(").append(bdTable + i).append(".").append(
					qryObjList.get(i).getBd_nameField()).append(getMultiLangIndex()).append(", ").append(
					bdTable + i).append(".").append(qryObjList.get(i).getBd_nameField()).append(") ").append(
					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append(", ");

			sqlBuffer.append("(case when isnull(").append(qryObjs[i]).append(
					", '~') = '~' then 1 else 0 end) as isnull").append(i);
		}

		sqlBuffer.append(", v.pk_currtype, (case when isnull(v.pk_currtype, '~') = '~' then 1 else 0 end) as is_currtype_null"); // is_currtype_null
		sqlBuffer.append(", v.rn");
		sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

		sqlBuffer.append(", sum(v.exp_ori) exp_ori, sum(v.exp_loc) exp_loc, sum(v.gr_exp_loc) gr_exp_loc, sum(v.gl_exp_loc) gl_exp_loc");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable + i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable + i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", org_orgs.code, org_orgs.name, org_orgs.name").append(getMultiLangIndex());
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", v.").append(qryObjs[i]);

			sqlBuffer.append(", ").append(bdTable + i).append(".")
					.append(qryObjList.get(i).getBd_codeField());

			sqlBuffer.append(", ").append(bdTable + i).append(".")
					.append(qryObjList.get(i).getBd_nameField()).append(", ").append(bdTable + i).append(".")
					.append(qryObjList.get(i).getBd_nameField()).append(getMultiLangIndex());
		}
		sqlBuffer.append(", v.").append(PK_CURR);
		sqlBuffer.append(", rn ");

		sqlBuffer.append(" order by ");
		sqlBuffer.append("is_org_null, code_org");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", isnull").append(i).append(", ").append(
					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
		}
		if (beForeignCurrency) {
			sqlBuffer.append(", is_currtype_null, pk_currtype");
		}

		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询报销累计发生(汇总数)<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException 
	 */
	private String getExpenseAccumulativeOccur() throws SQLException, BusinessException {
		String bxzbAlias = getAlias("er_bxzb");
		String fifbAlias = getAlias("er_busitem");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, bxzbAlias));
		sqlBuffer.append(", ").append(queryObjBaseExp);
		sqlBuffer.append(", ").append(beForeignCurrency ? (bxzbAlias + ".bzbm") : "null").append(" pk_currtype, 0 rn");
		sqlBuffer.append(", sum(" + fifbAlias + ".ybje) exp_ori");
		sqlBuffer.append(", sum(" + fifbAlias + ".bbje) exp_loc");
		sqlBuffer.append(", sum(" + fifbAlias + ".groupbbje) gr_exp_loc");
		sqlBuffer.append(", sum(" + fifbAlias + ".globalbbje) gl_exp_loc");

		sqlBuffer.append(" from er_bxzb ").append(bxzbAlias);
		sqlBuffer.append(" inner join er_busitem ").append(fifbAlias).append(" on ");
		sqlBuffer.append(bxzbAlias).append(".pk_jkbx = ").append(fifbAlias).append(".pk_jkbx ");	

		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		if (!StringUtils.isEmpty(getCompositeWhereSql(bxzbAlias))) {
			sqlBuffer.append(getCompositeWhereSql(bxzbAlias).replace("zb." + BXBusItemVO.SZXMID,
					fifbAlias + "." + BXBusItemVO.SZXMID));
		}

		if (queryVO.getBeginDate() != null) { // 查询开始日期
			sqlBuffer.append(" and ").append(bxzbAlias + ".djrq >= '").append(
					queryVO.getBeginDate().toString()).append("' ");
		}

		if (queryVO.getEndDate() != null) { // 查询结束日期
			// 单据结束月份的处理
			sqlBuffer.append(" and ").append(bxzbAlias + ".djrq <= '").append(
					queryVO.getEndDate().toString()).append("'  ");
		}

		sqlBuffer.append(getQueryObjSql()); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, false)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), false)); // 币种
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(bxzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // 业务单元
		sqlBuffer.append(" and " + bxzbAlias + "." + PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");
		sqlBuffer.append(" and ").append(bxzbAlias + ".dr = 0 ");
		sqlBuffer.append(" and ").append(fifbAlias + ".dr = 0 ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, bxzbAlias));
		sqlBuffer.append(", ").append(groupByBaseExp);
		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(bxzbAlias + ".bzbm");
		}

		return sqlBuffer.toString();
	}

	private String getComputeTotalSql() throws SQLException {
		// 构造小计、合计对象
		List<ComputeTotal> allQryObjs = getAllQryObj();
		List<String> computed = new ArrayList<String>();

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		for (ComputeTotal total : allQryObjs) {
			if (total.isDimension) {
				sqlBuffer.append(total.field).append(", ");
				computed.add(total.field);
			} else {
				sqlBuffer.append("null ").append(total.field).append(", ");
			}
		}
		int i = 0;
		for (; i < computed.size(); i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" rn, ");

		sqlBuffer.append(" sum(exp_ori) exp_ori, sum(exp_loc) exp_loc, sum(gr_exp_loc) gr_exp_loc, sum(gl_exp_loc) gl_exp_loc ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" group by ");
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append(computed.get(0));
			i = 1;
			for (; i < computed.size(); i++) {
				sqlBuffer.append(", ").append(computed.get(i));
			}
			sqlBuffer.append(" with cube ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append("cube(");
			sqlBuffer.append(computed.get(0));
			i = 1;
			for (; i < computed.size(); i++) {
				sqlBuffer.append(", ").append(computed.get(i));
			}
			sqlBuffer.append(")");
			break;
		default:
			break;
		}

		sqlBuffer.append(" having ");
		i = 0;
		for (; i < computed.size() - 1; i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") <= grouping(").append(
					computed.get(i + 1)).append(") and ");
		}
		sqlBuffer.append("grouping(").append(computed.get(0)).append(") = 0 "); // 集团不计算总计
		if (queryVO.getPk_orgs().length <= 1) {
			// 多业务单元查询才计算总计
			sqlBuffer.append(" and grouping(").append(allQryObjs.get(1).field).append(") = 0 ");
		}

		return sqlBuffer.toString();
	}

	/**
	 * 构造需要计算小计合计的对象
	 * 
	 * @return
	 */
	private List<ComputeTotal> getAllQryObj() {
		// 构造需要计算小计合计的对象
		if (allQryobjList.size() == 0) {
			List<String> dimensions = new ArrayList<String>();

			dimensions.add(PK_GROUP);
			dimensions.add(PK_ORG);

			String[] qryobjs = queryObjOrderExt.split(",");
			for (int i = 0; i < qryobjs.length; i++) {
				dimensions.add(qryobjs[i].trim());
			}

			int i = 0;
			ComputeTotal total = null;
			for (; i < dimensions.size() - 1; i++) {
				total = new ComputeTotal();
				total.field = dimensions.get(i);
				total.isDimension = true;
				allQryobjList.add(total);
			}

			total = new ComputeTotal();
			total.field = dimensions.get(i);
			total.isDimension = beForeignCurrency;
			allQryobjList.add(total);

			total = new ComputeTotal();
			total.field = "pk_currtype";
			total.isDimension = false;
			allQryobjList.add(total);
		}

		return allQryobjList;
	}
	
	
	/**
	 * 得到查询对象构成的SQL
	 * 
	 * @return String
	 * @throws BusinessException
	 */
	private String getQueryObjSql() throws BusinessException {
		List<QryObj> qryObjList = queryVO.getQryObjs();
		StringBuffer sqlBuffer = new StringBuffer(" ");
		for (QryObj qryObj : qryObjList) {
			sqlBuffer.append(" and ").append(qryObj.getSql());
		}

		return sqlBuffer.toString();
	}

	/**
	 * 获取临时表名<br>
	 * 
	 * @return String<br>
	 * @throws SQLException<br>
	 */
	private String getTmpTblName() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName)) {
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_expbalance" + qryObjLen,
					getTmpTblColNames(), getTmpTblColTypes());
		}

		return tmpTblName;
	}

	/**
	 * 获取临时表列<br>
	 * @return String[]<br>
	 */
	
	private String[] getTmpTblColNames() {
		if (tmpTblColNames == null) {
			// 查询对象个数
			int qryObjLen = queryVO.getQryObjs().size();

			StringBuffer otherColNameBuf = new StringBuffer();
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "")).append(", ");
			otherColNameBuf.append("pk_currtype, ");
			otherColNameBuf.append("rn, ");
			otherColNameBuf.append("exp_ori, exp_loc, gr_exp_loc, gl_exp_loc");
			String[] otherColNames = otherColNameBuf.toString().split(",");

			tmpTblColNames = new String[qryObjLen + otherColNames.length];

			tmpTblColNames[0] = otherColNames[0];
			tmpTblColNames[1] = otherColNames[1];

			for (int i = 0; i < qryObjLen; i++) {
				tmpTblColNames[i+2] = IPubReportConstants.QRY_OBJ_PREFIX + i + "pk";
			}

			System.arraycopy(otherColNames, 2, tmpTblColNames, qryObjLen + 2, otherColNames.length-2);
		}

		return tmpTblColNames;
		
	}

	/**
	 * 获取临时表列类型<br>
	 * 
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColTypes() {
		if (tmpTblColTypes == null || tmpTblColTypes.length == 0) {
			tmpTblColTypes = new Integer[getTmpTblColNames().length];
			int i = 0;
			for (; i < tmpTblColTypes.length - 4 - 1; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}

			tmpTblColTypes[i++] = Types.INTEGER; // rn

			for (; i < tmpTblColTypes.length - 4 + 1; i += 4) {
				tmpTblColTypes[i] = Types.DECIMAL;
				tmpTblColTypes[i + 1] = Types.DECIMAL;
				tmpTblColTypes[i + 2] = Types.DECIMAL;
				tmpTblColTypes[i + 3] = Types.DECIMAL;
			}
		}

		return tmpTblColTypes;
	}

}

// /:~
