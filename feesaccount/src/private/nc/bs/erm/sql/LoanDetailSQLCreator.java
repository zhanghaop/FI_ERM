package nc.bs.erm.sql;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nc.bs.er.util.SqlUtils;
import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.ReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.utils.fipub.SmartProcessor;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *      用来构造借款明细账表所涉及的sql
 *      针对借款明细账，包括几个部分：
 *      借款原值(汇总数)，还款值(汇总数)，原值-冲款值 =期初
 *      借款、还款累计发生     借款、还款明细数据
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午08:27:29
 */
public class LoanDetailSQLCreator extends ErmBaseSqlCreator{
	// 固定查询字段

	private static String detailFields = "@Table.djrq, @Table.zy,  @Table.pk_billtype, @Table.pk_jkbx,  @Table.djbh, @Table.kjqj, @Table.qzzt";

	private boolean isShowDateTotal = false; // 是否显示日小计

	private static final String PK_CONTRASTJK = "pk_contrastjk";

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;

	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.addAll(getSqlsByMonthOrDate()); // 按月份/日期查询
												// 报销管理部分通过日期来区分按照月份和按照日期的

		sqlList.add(getAddPeriodBeginSql()); // 插入期初(期末)余额
		sqlList.add(getDateSubTotalSql()); // 计算日小计
		sqlList.add(getSubTotalSql()); // 计算小计合计
		sqlList.addAll(getSubTotalBalSql()); // 计算小计合计期末余额

		return sqlList.toArray(new String[0]);
	}

	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
//		sqlBuffer.append(", (case when isnull(org_orgs.code, '~') = '~' then 1 else 0 end) as is_org_null"); // is_org_null
		sqlBuffer.append(", org_orgs.code code_org, isnull(org_orgs.name").append(getMultiLangIndex()).append(", org_orgs.name) org"); // code_org, org

		String[] qryObjs = getQueryObjs();
		List<QryObj> qryObjList = queryVO.getQryObjs();

		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", v.").append(qryObjs[i]).append(", ");

			sqlBuffer.append(bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_codeField()).append(" ")
					.append(IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code, ");

			sqlBuffer.append("isnull(").append(bdTable ).append( i).append(".").append(
					qryObjList.get(i).getBd_nameField()).append(getMultiLangIndex()).append(", ").append(
					bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_nameField()).append(") ").append(
					IPubReportConstants.QRY_OBJ_PREFIX).append(i);
		}

		sqlBuffer.append(", v.pk_currtype"); 
		sqlBuffer.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", v.").append(PK_CONTRASTJK);
		sqlBuffer.append(", v.rn, (case when v.rn = 0 then 0 else 1 end) as is_begin"); // is_begin
		sqlBuffer.append(", v.jk_ori, v.jk_loc, v.gr_jk_loc, v.gl_jk_loc");
		sqlBuffer.append(", v.hk_ori, v.hk_loc, v.gr_hk_loc, v.gl_hk_loc");
		sqlBuffer.append(", v.bal_ori, v.bal_loc, v.gr_bal_loc, v.gl_bal_loc");
		sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

		sqlBuffer.append(" from ").append(getTmpTblName()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable ).append( i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

//		sqlBuffer.append(" order by ");
//		sqlBuffer.append(" code_org");
//		for (int i = 0; i < qryObjList.size(); i++) {
//			sqlBuffer.append(", ").append(IPubReportConstants.QRY_OBJ_PREFIX)
//					.append(i).append("code");
//		}
//		if (beForeignCurrency) {
//			sqlBuffer.append(", pk_currtype");
//		}
//		sqlBuffer.append(",  rn, djrq, pk_billtype");
		sqlBuffer.append(" order by ");
		sqlBuffer.append(ErmReportSqlUtils.caseWhenSql("org_orgs.code"));
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(",");
//			sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(IPubReportConstants.QRY_OBJ_PREFIX+i+"pk"));
            sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(bdTable +  i + "." + qryObjList.get(i).getBd_codeField()));
		}
//		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.pk_currtype"));
//		}
		sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.rn")).append(", ").
		append(ErmReportSqlUtils.caseWhenSql("v.djrq"))
		.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.pk_billtype")).append(", ").append(ErmReportSqlUtils.caseWhenSql("v.djbh"));
		return sqlBuffer.toString();
	}

	@Override
	public String[] getDropTableSqls() throws SQLException {
		return new String[0];
	}

	private List<String> getSqlsByMonthOrDate() throws BusinessException, SQLException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getPeriodOriginalSql()); // 借款期初
		sqlList.add(getLoanDetail()); // 借款明细帐
		sqlList.add(getContrastDetail()); // 还款明细
		
		return sqlList;
	}

	private String getPeriodOriginalSql() throws BusinessException, SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "a"));
		sqlBuffer.append(", ").append(queryObjBaseDetailFld);
        sqlBuffer.append(beForeignCurrency ? ", a.pk_currtype" : ", null pk_currtype");
        // sqlBuffer.append(", null djrq, '").append(IErmReportConstants.getConst_Brief()).append("' zy");
        // // 期初
        sqlBuffer.append(", null djrq, 'init__period' zy"); // 期初
		sqlBuffer.append(", null pk_billtype, null pk_jkbx, null billno, null kjqj, null qzzt");
		sqlBuffer.append(", null ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 0 rn");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc");
		sqlBuffer.append(", sum(a.bal_ori) bal_ori, sum(a.bal_loc) bal_loc, sum(a.gr_bal_loc) gr_bal_loc, sum(a.gl_bal_loc) gl_bal_loc");

		sqlBuffer.append(" from (");
		sqlBuffer.append(getLoanOriginalSql() ).append( " union all " ).append( getLoanContrastSql()).append(") a ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "a"));
		sqlBuffer.append(", ").append(queryObjBaseBalExt);

        if (beForeignCurrency) {
            sqlBuffer.append(", a.pk_currtype");
        }

		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询借款原值(汇总数)<br>
	 * 按照查询条件获得对应的期初值，注意可能不存在期初值，这样期初的数值为0
	 * 注意：qcbz的处理，
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException
	 * @throws SQLException<br>
	 */
	private String getLoanOriginalSql() throws BusinessException, SQLException {
		String jkzbAlias = getAlias("er_jkzb");

		StringBuffer sqlBuffer = new StringBuffer(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.szxmid", jkzbAlias + ".szxmid"));
        sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.", jkzbAlias + ".")); // TODO byDetail
        if (beForeignCurrency) {
            sqlBuffer.append(", ").append( jkzbAlias ).append( ".bzbm ").append(PK_CURR);
        } else {
            sqlBuffer.append(", null ").append(PK_CURR);
        }
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".ybje) bal_ori");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".bbje) bal_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".groupbbje) gr_bal_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".globalbbje) gl_bal_loc");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
        }

        // TODO byDetail
        //和明细表连接
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//          sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);

            if (beForeignCurrency) {
                sqlBuffer.append(", ").append(jkzbAlias + ".bzbm");
            } else {
                sqlBuffer.append(", null ").append("bzbm");
            }
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
        
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		if (queryVO.getBeginDate() != null) {
			sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq < '").append(
					queryVO.getBeginDate()).append("' ");

			sqlBuffer.append(" and ").append(jkzbAlias).append(".contrastEndDate >= '").append(
					queryVO.getBeginDate()).append("' ");
		} 

		if(!StringUtils.isEmpty(queryVO.getPk_currency())){
			sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		}

		sqlBuffer.append(ErmReportSqlUtils.getOrgSql(queryVO.getPk_orgs(), true)); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 集团
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true));
		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" and fb.dr = 0 ");
            sqlBuffer.append(") ").append(jkzbAlias);
        }
        
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb.", jkzbAlias + "."));

        if (beForeignCurrency) {
            sqlBuffer.append(", ").append(jkzbAlias).append(".bzbm");
        }

		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询还款值(汇总数)<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException
	 * @throws SQLException<br>
	 */

	private String getLoanContrastSql() throws BusinessException, SQLException {
		String jkzbAlias = getAlias("er_jkzb");
		String cxAlias = getAlias("er_bxcontrast");

		StringBuffer sqlBuffer = new StringBuffer(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.szxmid", jkzbAlias + ".szxmid"));
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
        if (beForeignCurrency) {
            sqlBuffer.append(", ").append( jkzbAlias ).append( ".bzbm ").append(PK_CURR);
        } else {
            sqlBuffer.append(", null ").append(PK_CURR);
        }
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".ybje) bal_ori");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".bbje) bal_loc");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".groupbbje) gr_bal_loc");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".globalbbje) gl_bal_loc");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd ");
        }
        
        // TODO byDetail
        //和明细表连接
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//            sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
            if (beForeignCurrency) {
                sqlBuffer.append(", ").append(jkzbAlias + ".bzbm").append(" ");
            } else {
                sqlBuffer.append(", null ").append("bzbm").append(" ");
            }
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(", ").append(jkzbAlias + ".pk_jkbx").append(" ");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
        
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		// 日期单独处理，包括单据日期以及核销完成日期
		if (queryVO.getBeginDate() != null) {
			sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq < '").append(queryVO.getBeginDate()).append("' ");
			sqlBuffer.append(" and ").append(jkzbAlias).append(".contrastEndDate >= '").append(queryVO.getBeginDate()).append("' ");
//			sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq< '").append(queryVO.getBeginDate()).append("' ");
		} 
		sqlBuffer.append(getQueryObjSql(jkzbAlias));// 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		sqlBuffer.append(SqlUtils.getInStr(" and " + jkzbAlias + ".pk_org", queryVO.getPk_orgs())); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 业务单元
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" and fb.dr = 0 ");
            sqlBuffer.append(") ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd where ");
        } else {
            sqlBuffer.append(" and " );
        }
        if (queryVO.getBeginDate() != null) {
            //冲销日期
            sqlBuffer.append( cxAlias ).append( ".cxrq < '").append(queryVO.getBeginDate().toString()).append("' ");
        }

		
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb.", jkzbAlias + "."));

        if (beForeignCurrency) {
            sqlBuffer.append(", ").append(jkzbAlias).append(".bzbm");
        }
            
		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询借款明细记录<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException
	 */
	private String getLoanDetail() throws SQLException, BusinessException{
		String jkzbAlias = getAlias("er_jkzb");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(jkzbAlias ).append( "." ).append( PK_GROUP ).append( ", " ).append( jkzbAlias ).append( "." ).append( PK_ORG);
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.szxmid", jkzbAlias + ".szxmid"));
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
        if (beForeignCurrency) {
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".bzbm ").append(PK_CURR);
        } else {
            sqlBuffer.append(", null " ).append(PK_CURR);
        }
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djrq djrq");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".zy zy");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djlxbm pk_billtype");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".pk_jkbx pk_jkbx");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djbh billno");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".kjqj kjqj");
		sqlBuffer.append(", case when (").append(jkzbAlias).append(".ybye = 0 or ").append(jkzbAlias)
				.append(".qzzt = 1) then '").append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0113")/*是*/)
				.append("' else '").append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0114")/*否*/).append("' end qzzt");
		sqlBuffer.append(", null ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 1 rn, ");
		sqlBuffer.append(jkzbAlias ).append( ".ybje jk_ori, ");
		sqlBuffer.append(jkzbAlias ).append( ".bbje jk_loc, ");
		sqlBuffer.append(jkzbAlias ).append( ".groupbbje gr_jk_loc, ");
		sqlBuffer.append(jkzbAlias ).append( ".globalbbje gl_jk_loc, ");
		sqlBuffer.append("0.0 hk_ori, ");
		sqlBuffer.append("0.0 hk_loc, ");
		sqlBuffer.append("0.0 gr_hk_loc, ");
		sqlBuffer.append("0.0 gl_hk_loc, ");
		sqlBuffer.append("(" ).append( jkzbAlias ).append( ".ybje - 0.0) bal_ori, ");
		sqlBuffer.append("(" ).append( jkzbAlias ).append( ".bbje -0.0) bal_loc, ");
		sqlBuffer.append("(" ).append( jkzbAlias ).append( ".groupbbje - 0.0) gr_bal_loc, ");
		sqlBuffer.append("(" ).append( jkzbAlias ).append( ".globalbbje -0.0) gl_bal_loc ");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
        }
        
        // TODO byDetail
        //和明细表连接
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//            sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
            if (beForeignCurrency) {
                sqlBuffer.append(", ").append(jkzbAlias + ".bzbm ");
            } else {
                sqlBuffer.append(", null ").append("bzbm ");
            }
            
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djrq");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".zy zy");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djlxbm");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".pk_jkbx");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djbh");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".kjqj");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".qzzt");
//            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".ybye");
            
            sqlBuffer.append(", fb.ybye");
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
        
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		if (queryVO.getBeginDate() != null) {
			// 单据日期的处理
			sqlBuffer.append(" and ").append(jkzbAlias ).append( ".djrq >= '").append(queryVO.getBeginDate()).append("' ");
		}
		if (queryVO.getEndDate() != null) {
			// 单据日期的处理
			sqlBuffer.append(" and ").append(jkzbAlias ).append( ".djrq <= '").append(queryVO.getEndDate()).append("' ");
		}

        sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
//		sqlBuffer.append(ReportSqlUtils.getQueryObjSql(queryVO.getQryObjs())); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
		sqlBuffer.append(ReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), jkzbAlias)); // 币种
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(jkzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // 业务单元
		sqlBuffer.append(" and ").append(jkzbAlias ).append( "." ).append( PK_GROUP ).append( " = '").append(queryVO.getPk_group()).append("' "); // 集团
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(") ").append(jkzbAlias);
        }
        
		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询还款明细记录<br>
	 * @return String<br>
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private String getContrastDetail() throws BusinessException, SQLException {
		String jkzbAlias = getAlias("er_jkzb");
		String cxAlias = getAlias("er_bxcontrast");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(jkzbAlias ).append( "." ).append( PK_GROUP ).append( ", " ).append( cxAlias ).append( "." ).append( PK_ORG);
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("zb.", jkzbAlias + ".")); // 查询对象
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
        if (beForeignCurrency) {
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".bzbm ").append(PK_CURR);
        } else {
            sqlBuffer.append(", null " ).append(PK_CURR);
        }
		sqlBuffer.append(", " ).append( cxAlias ).append( ".cxrq djrq"); // 冲销日期
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".zy zy");
		sqlBuffer.append(", bxzb.djlxbm pk_billtype");
		sqlBuffer.append(", bxzb.pk_jkbx pk_jkbx");
		sqlBuffer.append(", bxzb.djbh billno");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".kjqj kjqj");
		sqlBuffer.append(", null qzzt");
		sqlBuffer.append(", ").append(cxAlias ).append( ".pk_jkd ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 1 rn");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", " ).append( "sum(" ).append( cxAlias  ).append( ".ybje) hk_ori");
		sqlBuffer.append(", " ).append( "sum(" ).append( cxAlias ).append( ".bbje) hk_loc");
		sqlBuffer.append(", " ).append( "sum(" ).append( cxAlias ).append( ".groupbbje) gr_hk_loc");
		sqlBuffer.append(", " ).append( "sum(" ).append( cxAlias ).append( ".globalbbje) gl_hk_loc");

		sqlBuffer.append(", (0.0 - sum(" ).append( cxAlias ).append( ".ybje)) bal_ori ");
		sqlBuffer.append(", (0.0 - sum(" ).append( cxAlias ).append( ".bbje)) bal_loc ");
		sqlBuffer.append(", (0.0 - sum(" ).append( cxAlias ).append( ".groupbbje)) gr_bal_loc  ");
		sqlBuffer.append(", (0.0 - sum(" ).append( cxAlias ).append( ".globalbbje)) gl_bal_loc ");

//		sqlBuffer.append(" from er_jkzb " ).append( jkzbAlias).append(" inner join er_bxcontrast " ).append( cxAlias);
//		sqlBuffer.append(" on ").append(jkzbAlias ).append( ".pk_jkbx = ").append(cxAlias ).append( ".pk_jkd");
//		sqlBuffer.append(" left join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd ");

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd");
            sqlBuffer.append(" left join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd ");
        }
        
     // TODO byDetail
        //和明细表连接
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//            sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
            if (beForeignCurrency) {
                sqlBuffer.append(", ").append(jkzbAlias + ".bzbm ");
            } else {
                sqlBuffer.append(", null bzbm");
            }
            
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djrq");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".zy zy");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djlxbm");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".pk_jkbx");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djbh");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".kjqj");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".qzzt");
//            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".ybye");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".contrastEndDate");
            
            sqlBuffer.append(", fb.ybye");
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
		
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		if (!StringUtils.isEmpty(getCompositeWhereSql(jkzbAlias))) {
			sqlBuffer.append(getCompositeWhereSql(jkzbAlias).replace("zb.szxmid",
					cxAlias + ".szxmid"));
		}

		//当期的还款单据和期初的借款单
		sqlBuffer.append(" and ((").append(cxAlias).append(".cxrq >= '").append(
                queryVO.getBeginDate()).append("' and ");
		sqlBuffer.append(cxAlias).append(".cxrq <= '").append(queryVO.getEndDate()).append("') or  (");
		sqlBuffer.append(jkzbAlias).append(".qcbz = 'Y' and ");
		sqlBuffer.append(jkzbAlias).append(".djrq < '").append(queryVO.getBeginDate()).append("') )");

		sqlBuffer.append(getQueryObjSql(jkzbAlias).replace("zb.", jkzbAlias + ".")); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, false)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), false)); // 币种
		sqlBuffer.append(" and ").append(jkzbAlias ).append( ".pk_group = '").append(queryVO.getPk_group()).append("' ");
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0");

        // TODO byDetail
        if (needQueryByDetail()) {
            String[] fields = groupByBaseBal.split(",");
            StringBuilder sql = new StringBuilder();
            for (String field : fields) {
                int nPos = field.indexOf("fb."); 
                if (nPos >= 0) {
                    String fbField = field.replaceAll("fb.", cxAlias + ".");
                    if (!isContrastField(fbField)) {
                        continue;
                    }
                    String zbField = field.replaceAll("fb.", jkzbAlias + ".");
                    sql.append(zbField).append(" = ");
                    sql.append(fbField);
                    sql.append(" and ");
                }
            }
            sqlBuffer.append(") ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd");
            sqlBuffer.append(" left join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd where ");
            sqlBuffer.append(sql.toString());
        } else {
            sqlBuffer.append(" and ");
        }

        sqlBuffer.append(SqlUtils.getInStr(cxAlias + ".pk_org", queryVO.getPk_orgs())); // 业务单元
        sqlBuffer.append(" and ").append(cxAlias).append(".dr = 0 and ");
        
//      if (queryVO.getBeginDate() != null) { // 查询开始日期
//      sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq >= '").append(
//                queryVO.getBeginDate()).append("' ");
        sqlBuffer.append(jkzbAlias).append(".contrastEndDate >= '").append(
                queryVO.getBeginDate()).append("' ");
        sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq >= '").append(queryVO.getBeginDate()).append("' ");
//  }

//  if (queryVO.getEndDate() != null) { // 查询截止日期
//      sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq <= '").append(queryVO.getEndDate()).append("' ");
//      sqlBuffer.append(" and " ).append(jkzbAlias).append( ".djrq <= '").append(queryVO.getEndDate()).append("' ");
//  }
		
        
        String billStatus = queryVO.getBillState().toString();
        if (IPubReportConstants.BILL_STATUS_EFFECT.equals(billStatus)) {
            sqlBuffer.append(" and bxzb.djzt >= 3 "); //单据状态——签字BXStatusConst
//            sqlBuffer.append(" and " + cxAlias + ".sxbz = 1 ");
        } else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(billStatus)) {
            sqlBuffer.append(" and bxzb.djzt >= 2 "); //单据状态——签字BXStatusConst
        } else {
            sqlBuffer.append(" and bxzb.djzt >= 1 "); //单据状态——保存BXStatusConst
        }
        
		sqlBuffer.append(" group by ");
		sqlBuffer.append(jkzbAlias ).append( "." ).append( PK_GROUP ).append( ", " ).append( cxAlias ).append( "." ).append( PK_ORG);
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb.", jkzbAlias + "."));
		sqlBuffer.append(", " ).append( cxAlias ).append( ".cxrq");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".zy");
		sqlBuffer.append(", bxzb.djlxbm");
		sqlBuffer.append(", bxzb.djbh");
		sqlBuffer.append(", bxzb.pk_jkbx");
		sqlBuffer.append(", " ).append( jkzbAlias ).append( ".kjqj");
		sqlBuffer.append(", " ).append( cxAlias ).append( ".pk_jkd ");

        if (beForeignCurrency) {
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".bzbm");
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
			String[] fixedObjs = fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "").split(",");
			for (int i = 0; i < fixedObjs.length; i++) {
				dimensions.add(fixedObjs[i].trim());
			}

			String[] qryobjs = queryObjOrderExt.split(",");
			for (int i = 0; i < qryobjs.length; i++) {
				dimensions.add(qryobjs[i].trim());
			}

			ComputeTotal total = null;
			for (int i = 0; i < dimensions.size(); i++) {
				total = new ComputeTotal();
				total.field = dimensions.get(i);
				total.isDimension = true;
				allQryobjList.add(total);
			}

			total = new ComputeTotal();
			total.field = "pk_currtype";
			total.isDimension = beForeignCurrency;
//			total.isDimension = true;
			allQryobjList.add(total);

			total = new ComputeTotal();
			total.field = "djrq";
			total.isDimension = isShowDateTotal;
			allQryobjList.add(total);
		}

		return allQryobjList;
	}

	/**
	 * 获取临时表名<br>
	 *
	 * @return String<br>
	 * @throws SQLException<br>
	 */
	private String getTmpTblName() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName)) {
			String tableName = "tmp_erm_loandetail" + qryObjLen;
			tmpTblName = TmpTableCreator.createTmpTable(tableName, getTmpTblColNames(), getTmpTblColTypes());
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

			otherColNameBuf.append(detailFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "")).append(", ");
			otherColNameBuf.append(PK_CONTRASTJK).append(", ");
			otherColNameBuf.append("rn, ");
			otherColNameBuf.append("jk_ori, jk_loc, gr_jk_loc, gl_jk_loc, ");
			otherColNameBuf.append("hk_ori, hk_loc, gr_hk_loc, gl_hk_loc, ");
			otherColNameBuf.append("bal_ori, bal_loc, gr_bal_loc, gl_bal_loc ");
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
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColTypes() {
		if (tmpTblColTypes == null || tmpTblColTypes.length == 0) {
			tmpTblColTypes = new Integer[getTmpTblColNames().length];
			int i = 0;
			for (; i < tmpTblColTypes.length - 12-2; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}

			tmpTblColTypes[i++] = Types.VARCHAR;
			tmpTblColTypes[i++] = Types.INTEGER; // rn列

			for (; i < tmpTblColTypes.length - 4 + 1; i += 4) {
				tmpTblColTypes[i] = Types.DECIMAL;
				tmpTblColTypes[i + 1] = Types.DECIMAL;
				tmpTblColTypes[i + 2] = Types.DECIMAL;
				tmpTblColTypes[i + 3] = Types.DECIMAL;
			}
		}

		return tmpTblColTypes;
	}



	private String getOneSubTotalPeriodEndBal(String tmpTbl, String qryObjCond, String[] nums, String[] alias) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(tmpTbl).append(".").append(nums[2]).append(" = (select ((select sum(").append(
				alias[0]).append(".").append(nums[2]).append(") from ").append(tmpTbl).append(" ")
				.append(alias[0]).append(" where ").append(qryObjCond.replace("@reptbl", alias[0]))
				.append(alias[0]).append(".rn = 0) ");

		sqlBuffer.append(" + ");

		sqlBuffer.append(" (select sum(").append(alias[1]).append(".").append(nums[0]).append(" - ")
				.append(alias[1]).append(".").append(nums[1]).append(") from ").append(tmpTbl)
				.append(" ").append(alias[1]).append(" where ").append(qryObjCond.replace("@reptbl", alias[1]))
				.append(alias[1]).append(".rn = ").append(tmpTbl).append(".rn)) ");

		sqlBuffer.append(getFromDummyTable());
		sqlBuffer.append(")");

		return sqlBuffer.toString();
	}

	/**
	 * 插入期初(期末)余额
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getAddPeriodBeginSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select distinct ");
		sqlBuffer.append("a.pk_group, a.pk_org, ");
		sqlBuffer.append(queryObjBaseBalExt).append(", ");
		if (beForeignCurrency) {
			sqlBuffer.append("a.pk_currtype, ");
		} else {
			sqlBuffer.append("null pk_currtype, ");
		}
        // sqlBuffer.append("null djrq, '").append(IErmReportConstants.getConst_Brief()).append("' zy, ");
        sqlBuffer.append("null djrq, 'init__period' zy, "); // 期初
		sqlBuffer.append(" null pk_billtype, null pk_jkbx, null djbh, null kjqj, null qzzt, ");
		sqlBuffer.append("null ").append(PK_CONTRASTJK).append(", ");
		sqlBuffer.append("0 rn, ");
		sqlBuffer.append(" 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc, ");
		sqlBuffer.append(" 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc, ");
		sqlBuffer.append(" 0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc ");

		sqlBuffer.append(" from ").append(getTmpTblName()).append(" a ");

        // sqlBuffer.append(" where isnull(a.zy, '~') != '").append(IErmReportConstants.getConst_Brief()).append("' ");
        sqlBuffer.append(" where isnull(a.zy, '~') != 'init__period' ");

		sqlBuffer.append(" and not exists ");
		sqlBuffer.append("(select null from ").append(getTmpTblName()).append(" b ");
        // sqlBuffer.append(" where b.zy = '").append(IErmReportConstants.getConst_Brief()).append("' ");
        // // 期初
        sqlBuffer.append(" where b.zy = 'init__period' "); // 期初
        sqlBuffer
                .append(" and a.pk_group = b.pk_group and a.pk_org = b.pk_org ");
		String[] qryObjs = getQueryObjs();
		for (String qryObj : qryObjs) {
			sqlBuffer.append(" and a.").append(qryObj).append(" = b.").append(qryObj);
		}
		if (beForeignCurrency) {
			sqlBuffer.append(" and a.pk_currtype = b.pk_currtype");
		}
		sqlBuffer.append(") ");

		return sqlBuffer.toString();
	}
	/**
	 * 计算日小计
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getDateSubTotalSql() throws SQLException {
		if (!isShowDateTotal) {
			// 不显示日小计
			return null;
		}

		List<ComputeTotal> allQryobjs = getAllQryObj();
		List<String> computed = new ArrayList<String>();

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		int i = 0;
		for (; i < allQryobjs.size(); i++) {
			if (allQryobjs.get(i).isDimension) {
				sqlBuffer.append(allQryobjs.get(i).field).append(", ");
				computed.add(allQryobjs.get(i).field);
			} else {
				sqlBuffer.append(" null ").append(allQryobjs.get(i).field).append(", ");
			}
		}
		sqlBuffer.append("null zy, null pk_billtype, null pk_jkbx, null djbh, null kjqj, null qzzt, ");
		sqlBuffer.append("null ").append(PK_CONTRASTJK).append(", ");
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" rn, "); // 日小计rn = SmartProcessor.MAX_ROW
		sqlBuffer.append(" sum(jk_ori) jk_ori, sum(jk_loc) jk_loc, sum(gr_jk_loc) gr_jk_loc, sum(gl_jk_loc) gl_jk_loc, ");
		sqlBuffer.append(" sum(hk_ori) hk_ori, sum(hk_loc) hk_loc, sum(gr_hk_loc) gr_hk_loc, sum(gl_hk_loc) gl_hk_loc, ");
		sqlBuffer.append(" 0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" where ");
		sqlBuffer.append(" isnull(djrq, '~') != '~' ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(computed.get(0));
		i = 1;
		for (; i < computed.size(); i++) {
			sqlBuffer.append(", ").append(computed.get(i));
		}

		return sqlBuffer.toString();
	}

	/**
	 * 计算小计合计<br>
	 *
	 * 说明：期初行rn = 0，明细行rn = 1，第一级合计行rn = SmartProcessor.MAX_ROW，以后依次类推。
	 * 日小计期末余额在nc.impl.arap.report.DetailBOImpl中计算，其余期末余额，在数据库层计算。
	 * 为了实现排序，利用了SmartProcessor.MAX_PK的主键最大特性。
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getSubTotalSql() throws SQLException {
		List<ComputeTotal> allQryobjs = getAllQryObj();
		List<String> computed = new ArrayList<String>();

		// 正式拼写SQL
		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		int i = 0;
		for (; i < allQryobjs.size(); i++) {
			if (allQryobjs.get(i).isDimension) {
				sqlBuffer.append(allQryobjs.get(i).field).append(", ");
				computed.add(allQryobjs.get(i).field);
			} else {
				sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", ");
			}
		}
//		sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", "); // 日小计已经计算

		sqlBuffer.append("null zy, null pk_billtype, null pk_jkbx, null djbh, null kjqj, null qzzt, ");
		sqlBuffer.append("null ").append(PK_CONTRASTJK).append(", ");
		i = 0;
		for (; i < computed.size(); i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" + 1 rn, "); // 日小计占用了rn = SmartProcessor.MAX_ROW
		sqlBuffer.append(" sum(jk_ori) jk_ori, sum(jk_loc) jk_loc, sum(gr_jk_loc) gr_jk_loc, sum(gl_jk_loc) gl_jk_loc, ");
		sqlBuffer.append(" sum(hk_ori) hk_ori, sum(hk_loc) hk_loc, sum(gr_hk_loc) gr_hk_loc, sum(gl_hk_loc) gl_hk_loc, ");
		sqlBuffer.append(" 0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" where rn > 0 and rn < ").append(SmartProcessor.MAX_ROW);

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
			sqlBuffer.append(" and grouping(").append(allQryobjs.get(1).field).append(") = 0 ");
		}

		return sqlBuffer.toString();
	}

	/**
	 * 计算查询对象小计期末余额
	 *
	 * @return
	 * @throws SQLException
	 */
	private List<String> getSubTotalBalSql() throws SQLException {
		List<String> subTotalSqlList = new ArrayList<String>();

		String[][] nums = {	{ "jk_ori", "hk_ori", "bal_ori" },
				{ "jk_loc", "hk_loc", "bal_loc" },
				{ "gr_jk_loc", "gr_hk_loc", "gr_bal_loc" },
				{ "gl_jk_loc", "gl_hk_loc", "gl_bal_loc" } };

		String[][] alias = { { "a", "b" }, { "c", "d" }, { "e", "f" }, { "g", "h" } };

		List<ComputeTotal> allQryobjs = getAllQryObj();
		List<String> computed = new ArrayList<String>();
		for (ComputeTotal total : allQryobjs) {
			if (total.isDimension) {
				computed.add(total.field);
			}
		}
		int totalCnt = computed.size();

		if (isShowDateTotal) {
			// 为提高性能，日小计的期末余额，不在这里计算
			totalCnt -= 1;
		}

		int beginObj = queryVO.getPk_orgs().length <= 1 ? 1 : 0;
		String tmpTbl = getTmpTblName();
		int r = 0;
		for (int i = totalCnt - 1; i >= beginObj; i--) {
			StringBuffer qryObjCondBuf = new StringBuffer();
			for (int j = 0; j <= i; j++) {
				qryObjCondBuf.append(tmpTbl).append(".").append(computed.get(j)).append(" = ")
						.append("@reptbl.").append(computed.get(j)).append(" and ");
			}

			StringBuffer sqlBuffer = new StringBuffer(" update ");
			sqlBuffer.append(tmpTbl);

			sqlBuffer.append(" set ");

			for (int k = 0; k < nums.length; k++) {
				sqlBuffer.append(getOneSubTotalPeriodEndBal(tmpTbl, qryObjCondBuf.toString(),
						nums[k], alias[k]));
				sqlBuffer.append(", ");
			}

			subTotalSqlList.add(sqlBuffer.substring(0, sqlBuffer.length() - 2) + " where " + tmpTbl
					+ ".rn >= " + (SmartProcessor.MAX_ROW + 1 + r));
			r++;
		}

		return subTotalSqlList;
	}

}

// /:~
