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
 * 用来构造借款余额表所涉及的sql 针对借款余额表，包括几个部分： 借款原值(汇总数)，还款值(汇总数)，原值-冲款值 =期初 借款、还款累计发生
 * </p>
 * 
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午08:27:29
 */
public class LoanBalanceSQLCreator extends ErmBaseSqlCreator {

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;

	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();
	
	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.addAll(getSqlsByMonthOrDate());
		sqlList.add(getComputeTotalSql());
		return sqlList.toArray(new String[0]);
	}

	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", (case when isnull(org_orgs.code, '~') = '~' then 1 else 0 end) as is_org_null"); // is_org_null
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

		if (beForeignCurrency) {
		    sqlBuffer.append(",  v.").append(PK_CURR);
		} else {
            sqlBuffer.append(", null ").append(PK_CURR);
		}
//		sqlBuffer.append(",  v.").append(PK_CURR).append(", v.rn");
        sqlBuffer.append(", v.rn");
		sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

		sqlBuffer.append(", sum(v.init_ori) init_ori, sum(v.init_loc) init_loc, sum(v.gr_init_loc) gr_init_loc, sum(v.gl_init_loc) gl_init_loc");
		sqlBuffer.append(", sum(v.jk_ori) jk_ori, sum(v.jk_loc) jk_loc, sum(v.gr_jk_loc) gr_jk_loc, sum(v.gl_jk_loc) gl_jk_loc");
		sqlBuffer.append(", sum(v.hk_ori) hk_ori, sum(v.hk_loc) hk_loc, sum(v.gr_hk_loc) gr_hk_loc, sum(v.gl_hk_loc) gl_hk_loc");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable ).append( i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", org_orgs.code, org_orgs.name, org_orgs.name").append(getMultiLangIndex());
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", v.").append(qryObjs[i]);

			sqlBuffer.append(", ").append(bdTable ).append( i).append(".")
					.append(qryObjList.get(i).getBd_codeField());

			sqlBuffer.append(", ").append(bdTable ).append( i).append(".")
					.append(qryObjList.get(i).getBd_nameField()).append(", ").append(bdTable ).append( i).append(".")
					.append(qryObjList.get(i).getBd_nameField()).append(getMultiLangIndex());
		}

        if (beForeignCurrency) {
            sqlBuffer.append(", v.").append(PK_CURR);
        } else {
//            sqlBuffer.append(", null ").append(PK_CURR);
        }
		sqlBuffer.append(", rn ");

//		sqlBuffer.append(" order by ");
//		sqlBuffer.append("is_org_null, code_org");
//		for (int i = 0; i < qryObjList.size(); i++) {
//			sqlBuffer.append(", ").append(
//					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
//		}
//		sqlBuffer.append(", rn ");
//		
		sqlBuffer.append(" order by ");
		sqlBuffer.append(ErmReportSqlUtils.caseWhenSql("org_orgs.code"));
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(",");
//			sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(IPubReportConstants.QRY_OBJ_PREFIX+i+"pk"));
            sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(bdTable +  i + "." + qryObjList.get(i).getBd_codeField()));
		}
//        sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.pk_currtype"));
		sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.rn"));
		return sqlBuffer.toString();
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}

	/**
	 * 获取按月份或者日期查询明细账的SQL<br>
	 * 
	 * @return String[]<br>
	 * @throws SQLException<br>
	 * @throws BusinessException<br>
	 */
	private List<String> getSqlsByMonthOrDate() throws BusinessException, SQLException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getPeriodOriginalSql()); // 借款期初
		sqlList.add(getLoanAccumulativeOccur()); // 本期借款累计
		sqlList.add(getContrastAccumulativeOccur()); // 本期还款明细

		return sqlList;
	}

	/**
	 * 获得借款余额表期初值
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 * @see
	 * @since V6.0
	 */
	private String getPeriodOriginalSql() throws BusinessException, SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());
		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "a"));
		sqlBuffer.append(", ").append(queryObjBaseDetailFld);
		sqlBuffer.append(", a.").append(PK_CURR);
		sqlBuffer.append(", 0 rn");
		sqlBuffer.append(", a.init_ori init_ori, a.init_loc init_loc, a.gr_init_loc gr_init_loc, a.gl_init_loc gl_init_loc");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc");

		sqlBuffer.append(" from (");
		sqlBuffer.append(getLoanOriginalSql() ).append( " union all " ).append( getLoanContrastSql()).append(") a ");

		return sqlBuffer.toString();
	}
	
    protected boolean isQueryByDetail(String fieldCode) {
        return isDetailField(fieldCode);
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

		boolean foreignTmp = beForeignCurrency;
		beForeignCurrency = true;
		
		StringBuffer sqlBuffer = new StringBuffer(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
	    sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.", jkzbAlias + ".")); // TODO byDetail
		sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".ybje) init_ori");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".bbje) init_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".groupbbje) gr_init_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".globalbbje) gl_init_loc");

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
//	        sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
	        sqlBuffer.append(", ").append(jkzbAlias + ".bzbm");
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

		if (queryVO.getBeginDate() != null) { // 查询开始日期
			sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq < '").append(
					queryVO.getBeginDate().toString()).append("' ");

			sqlBuffer.append(" and ").append(jkzbAlias).append(".contrastEndDate >= '").append(
					queryVO.getBeginDate().toString()).append("' ");
		} 

		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		sqlBuffer.append(ErmReportSqlUtils.getOrgSql(queryVO.getPk_orgs(), true)); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 集团
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
			sqlBuffer.append(", zb.bzbm");
		}

        beForeignCurrency = foreignTmp;
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
		
		boolean foreignTmp = beForeignCurrency;
        beForeignCurrency = true;
        
		StringBuffer sqlBuffer = new StringBuffer(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.szxmid", jkzbAlias + ".szxmid"));
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
		sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".ybje) init_ori");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".bbje) init_loc");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".groupbbje) gr_init_loc");
		sqlBuffer.append(", -sum(" ).append( cxAlias ).append( ".globalbbje) gl_init_loc");

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
            sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" ");
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(", zb.pk_jkbx");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
        
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ReportSqlUtils.getFixedWhere());
		// 借款单的收支项目权限走借款单表头
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		if (queryVO.getBeginDate() != null) {
			// 单据日期的处理
			sqlBuffer.append(" and ").append(jkzbAlias ).append( ".djrq < '").append(queryVO.getBeginDate().toString()).append("' ");
			// 核销完成日期
			sqlBuffer.append(" and ").append(jkzbAlias).append(".contrastEndDate >= '").append(queryVO.getBeginDate().toString()).append("' ");
		} else {
		}

		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
			sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		sqlBuffer.append(ErmReportSqlUtils.getOrgSql(queryVO.getPk_orgs(), true)); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 集团
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");
		sqlBuffer.append(" and ").append(cxAlias).append(".dr = 0 ");
		
		// TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(") ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd ");
        }
        if (queryVO.getBeginDate() != null) {
            //冲销日期
            sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq < '").append(queryVO.getBeginDate().toString()).append("' ");
        }
        
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb.", jkzbAlias + "."));
		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(jkzbAlias ).append( ".bzbm");
		}
		
        beForeignCurrency = foreignTmp;
		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询借款累计发生(汇总数)<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException 
	 */
	private String getLoanAccumulativeOccur() throws SQLException, BusinessException{
		String jkzbAlias = getAlias("er_jkzb");

		boolean foreignTmp = beForeignCurrency;
        beForeignCurrency = true;
        
        StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb.szxmid", jkzbAlias + ".szxmid"));
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
		sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", 0 rn ");
		sqlBuffer.append(", 0.0 init_ori, 0.0 init_loc, 0.0 gr_init_loc, 0.0 gl_init_loc");
		sqlBuffer.append(", sum(" ).append( jkzbAlias ).append( ".ybje) jk_ori");
		sqlBuffer.append(", sum(" ).append( jkzbAlias ).append( ".bbje) jk_loc");
		sqlBuffer.append(", sum(" ).append( jkzbAlias ).append( ".groupbbje) gr_jk_loc");
		sqlBuffer.append(", sum(" ).append( jkzbAlias ).append( ".globalbbje) gl_jk_loc");
		sqlBuffer.append(", 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc ");

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
            sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" ");
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

		// 日期单独处理，包括单据日期以及核销完成日期
		if (queryVO.getBeginDate() != null) { // 查询开始日期
			sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".djrq >= '").append(queryVO.getBeginDate()).append("' ");
		}

		if (queryVO.getEndDate() != null) { // 查询截止日期
			sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".djrq <= '").append(queryVO.getEndDate()).append("' ");
		}

		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		sqlBuffer.append(ErmReportSqlUtils.getOrgSql(queryVO.getPk_orgs(), true)); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 集团
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");
		
		// TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(") ").append(jkzbAlias);
        }
        
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb.", jkzbAlias + "."));
		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(jkzbAlias ).append( ".bzbm");
		}
		beForeignCurrency = foreignTmp;
		
		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询还款累计发生(汇总数)<br>
	 * 
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException
	 */
	private String getContrastAccumulativeOccur() throws SQLException, BusinessException {
		String jkzbAlias = getAlias("er_jkzb");
		String cxAlias = getAlias("er_bxcontrast");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());
		
		boolean foreignTmp = beForeignCurrency;
        beForeignCurrency = true;
        
		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//		sqlBuffer.append(", ").append(queryObjBaseBal);
        sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", jkzbAlias + ".")); // TODO byDetail
		sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", 0 rn ");
		sqlBuffer.append(", 0.0 init_ori, 0.0 init_loc, 0.0 gr_init_loc, 0.0 gl_init_loc");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", sum(" ).append( cxAlias ).append( ".ybje) hk_ori");
		sqlBuffer.append(", sum(" ).append( cxAlias ).append( ".bbje) hk_loc");
		sqlBuffer.append(", sum(" ).append( cxAlias ).append( ".groupbbje) gr_hk_loc");
		sqlBuffer.append(", sum(" ).append( cxAlias ).append( ".globalbbje) gl_hk_loc");

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
            sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" ");
            sqlBuffer.append(", fb.ybje");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(", zb.pk_jkbx");
            sqlBuffer.append(", zb.contrastEndDate");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
        
		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

        //当期的借款单和期初的借款单
        sqlBuffer.append(" and ((").append(jkzbAlias).append(".djrq >= '").append(
                queryVO.getBeginDate()).append("' and ");
        sqlBuffer.append(jkzbAlias).append(".djrq <= '").append(queryVO.getEndDate()).append("') or  (");
        sqlBuffer.append(jkzbAlias).append(".qcbz = 'Y' and ");
        sqlBuffer.append(jkzbAlias).append(".djrq < '").append(queryVO.getBeginDate()).append("') )");
        
		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // 查询对象
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // 单据状态
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // 币种
		sqlBuffer.append(SqlUtils.getInStr(" and " + jkzbAlias + ".pk_org", queryVO.getPk_orgs())); // 业务单元
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // 集团
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0");
		sqlBuffer.append(" and ").append(cxAlias).append(".dr = 0");
		
		// TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(") ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd");
            sqlBuffer.append(" left join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd where ");
        } else {
            sqlBuffer.append(" and ");
        }
        
     // 日期单独处理，包括单据日期以及核销完成日期
//      if (queryVO.getBeginDate() != null) { // 查询日期
            // 核销完成日期
       sqlBuffer.append(jkzbAlias).append(".contrastEndDate >= '").append(
                    queryVO.getBeginDate()).append("' ");
            // 冲销日期
       sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq >= '").append(queryVO.getBeginDate()).append("' ");
//      }
//      if (queryVO.getEndDate() != null) {
            // 冲销日期
//          sqlBuffer.append(" and " ).append( cxAlias ).append( ".cxrq <= '").append(queryVO.getEndDate()).append("' ");
//      }
            

        String billStatus = queryVO.getBillState().toString();
        if (IPubReportConstants.BILL_STATUS_EFFECT.equals(billStatus)) {
            sqlBuffer.append(" and bxzb.djzt >= 3 "); //单据状态——签字BXStatusConst
            // sqlBuffer.append(" and " + cxAlias + ".sxbz = 1 ");
        } else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(billStatus)) {
            sqlBuffer.append(" and bxzb.djzt >= 2 "); //单据状态——签字BXStatusConst
        } else {
            sqlBuffer.append(" and bxzb.djzt >= 1 "); //单据状态——保存BXStatusConst
        }
            
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replaceAll("fb.", "zb."));
		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(jkzbAlias ).append( ".bzbm ");
		}
		beForeignCurrency = foreignTmp;
		
		return sqlBuffer.toString();
	}

	/**
	 * 计算合计行
	 * 
	 * @return
	 * @throws SQLException
	 */
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

		sqlBuffer.append("sum(init_ori) init_ori, sum(init_loc) init_loc, sum(gr_init_loc) gr_init_loc, sum(gl_init_loc) gl_init_loc, ");
		sqlBuffer.append("sum(jk_ori) jk_ori, sum(jk_loc) jk_loc, sum(gr_jk_loc) gr_jk_loc, sum(gl_jk_loc) gl_jk_loc, ");
		sqlBuffer.append("sum(hk_ori) hk_ori, sum(hk_loc) hk_loc, sum(gr_hk_loc) gr_hk_loc, sum(gl_hk_loc) gl_hk_loc ");

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

			dimensions.add("pk_group");
			dimensions.add("pk_org");

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
//			total.isDimension = beForeignCurrency;
            total.isDimension = true;
			allQryobjList.add(total);

			total = new ComputeTotal();
			total.field = "pk_currtype";
			total.isDimension = false;
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
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_loanbalance" + qryObjLen,
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
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", ").append(PK_CURR);
			otherColNameBuf.append(", ").append("rn,"); // rn
			otherColNameBuf.append("init_ori,init_loc,gr_init_loc,gl_init_loc,");
			otherColNameBuf.append("jk_ori,jk_loc,gr_jk_loc,gl_jk_loc,");
			otherColNameBuf.append("hk_ori,hk_loc,gr_hk_loc,gl_hk_loc");
			String[] otherColNames = otherColNameBuf.toString().split(",");

			tmpTblColNames = new String[qryObjLen + otherColNames.length];

			tmpTblColNames[0] = otherColNames[0];
			tmpTblColNames[1] = otherColNames[1];
			for (int i = 0; i < qryObjLen; i++) {
				tmpTblColNames[i + 2] = IPubReportConstants.QRY_OBJ_PREFIX + i + "pk";
			}

			System.arraycopy(otherColNames, 2, tmpTblColNames, qryObjLen + 2, otherColNames.length - 2);
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
			for (; i < tmpTblColTypes.length - 12 - 1; i++) {
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
