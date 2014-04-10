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
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.pub.ErmCommonReportMethod;
import nc.vo.erm.pub.IErmReportAnalyzeConstants;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.timecontrol.TimeCtrlUtil;
import nc.vo.fipub.timecontrol.TimeCtrlVO;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;

import org.apache.commons.lang.StringUtils;

/**
 * ���������ѯSQL������(����������)<br>
 *
 * @author liansg<br>
 * @since V60 2010-12-14<br>
 */
public class LoanAccAgeAnaSQLCreator extends ErmBaseSqlCreator {

	private static final String getLBL_BAL() {
	    return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000249")/*@res "���"*/;
	}
	
	private String tmpAccTable = null;

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;

	private String tmpTblName2 = null;
	private String[] tmpTblColName2s = null;
	private Integer[] tmpTblColType2s = null;
	
	private String tmpTblName3 = null;
	private String[] tmpTblColName3s = null;
	private Integer[] tmpTblColType3s = null;

	private static final String detailFields = "zb.djlxbm pk_billtype, zb.pk_jkbx, zb.djbh djbh, zb.djrq djrq ";
	private static final String groupByFields = "zb.djlxbm, zb.pk_jkbx, zb.djbh, zb.djrq";
	private static final String detailTemFields = "@Table.pk_billtype, @Table.pk_jkbx, @Table.djbh , @Table.djrq  ";

	// ���䷽��VO
	private TimeCtrlVO timeCtrlVO = null;
    
	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		// ��������Ŀǰֻ֧�ְ������䡱�������������ڡ����ڽ����Ϸ��
		if (IErmReportConstants.getAcc_Ana_Mode_Age().equals(queryVO.getAnaMode())) {
			// ����ģʽ��������
			sqlList.addAll(getAccountAgeByAgeSql());
		}

		// ����ϸ�Ļ���
		if (!queryVO.isQueryDetail()) {
			sqlList.add(getCollectSql()); // ��ϸ����
		}

		sqlList.add(getLoanTotal()); // �������ܶ�
		sqlList.addAll(getTotalSqls()); // ����С�ơ��ϼ�

		return sqlList.toArray(new String[0]);
	}

	@Override
	public String getResultSql() throws SQLException, BusinessException {
		// �ж��Ƿ�����ϸ���ǻ��ܵõ���Ӧ�Ľ��sql
		if (queryVO.isQueryDetail()) {
			return getAccountAgeDetailResultSql();
		} else {
			return getAccountAgeResultSql();
		}
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}

	private String getAccountAgeResultSql() throws SQLException, BusinessException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
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
		sqlBuffer.append(", v.").append(PK_CURR);
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		}
		sqlBuffer.append(",  v.accage_ori, v.accage_loc, v.gr_accage_loc, v.gl_accage_loc");
		sqlBuffer.append(", v.accageid, v.accage, v.rn, 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

		// =====================FROM�Ӿ�=====================
		sqlBuffer.append(" from ");
		sqlBuffer.append(queryVO.isQueryDetail() ? getTmpTblName() : getTmpTblName2()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable ).append( i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

//		sqlBuffer.append(" order by ");
//		sqlBuffer.append(" code_org");
//		for (int i = 0; i < qryObjList.size(); i++) {
//			sqlBuffer.append(", ").append(
//					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
//		}
//		sqlBuffer.append(" , pk_currtype, rn");
//		if (queryVO.isQueryDetail()) {
//			sqlBuffer.append(", djrq, djbh");
//		}
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
		sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.rn"));
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.djrq"));
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.djbh"));
	    }

		return sqlBuffer.toString();
	}

	private String getAccountAgeDetailResultSql() throws SQLException, BusinessException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
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

		sqlBuffer.append(", v.").append(PK_CURR);
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(detailTemFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		}
		sqlBuffer.append(",  v.accage_ori, v.accage_loc, v.gr_accage_loc, v.gl_accage_loc");
		sqlBuffer.append(", v.accageid, v.accage, v.rn, 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

		// =====================FROM�Ӿ�=====================
		sqlBuffer.append(" from ");
		sqlBuffer.append(queryVO.isQueryDetail() ? getTmpTblName() : getTmpTblName2()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable + i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable + i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

//		sqlBuffer.append(" order by ");
//		sqlBuffer.append(" code_org");
//		for (int i = 0; i < qryObjList.size(); i++) {
//			sqlBuffer.append(", ").append(
//					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
//		}
//		sqlBuffer.append(", pk_currtype, rn");
//		if (queryVO.isQueryDetail()) {
//			sqlBuffer.append(", djrq, djbh");
//		}
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
		sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.rn"));
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.djrq"));
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.djbh"));
	    }

		return sqlBuffer.toString();
	}

	/**
	 * ��ȡ�����������SQL
	 * 
	 * @return
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private List<String> getAccountAgeByAgeSql() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();

		// ��ѯ�������
		sqlList.add(getLoanOriAgeByAccAgeLoanSql());

		if (UFBoolean.TRUE.equals(queryVO.getUserObject().get(
				IErmReportAnalyzeConstants.INCLUDE_UNEFFECT))) {
			// ����δ��Ч
			sqlList.add(getLoanAgeByAccAgeContrastSql());
		}

		sqlList.add(getLoanAgeByAccAgeSql());

		return sqlList;
	}

	private String getLoanAgeByAccAgeSql() throws SQLException, BusinessException {
		// ��ѯ���䷽��
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", ").append(queryObjBaseBalExt.replace("a.", "v."));
		sqlBuffer.append(", v.").append(PK_CURR).append(", ");
		sqlBuffer.append(detailTemFields.replace(IErmReportConstants.REPLACE_TABLE, "v")).append(", ");
		sqlBuffer.append("v.accageid, v.accage, v.rn, ");
		sqlBuffer.append("sum(v.accage_ori) accage_ori, ");
		sqlBuffer.append("sum(v.accage_loc) accage_loc, ");
		sqlBuffer.append("sum(v.gr_accage_loc) gr_accage_loc, ");
		sqlBuffer.append("sum(v.gl_accage_loc) gl_accage_loc ");

		sqlBuffer.append(" from ").append(getTmpTblName3()).append(" v ");
		// sqlBuffer.append(" where ").append(ReportSqlUtils.getFixedWhere());
		// sqlBuffer.append(getCompositeWhereSql());

		sqlBuffer.append(" group by ").append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", ").append(queryObjBaseBalExt.replace("a", "v"));
		sqlBuffer.append(", v.").append(PK_CURR);
		sqlBuffer.append(", ").append(detailTemFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", v.accageid, v.accage, v.rn");

		sqlBuffer.append(" having ");
		sqlBuffer.append("sum(v.accage_ori) != 0 ");
		if (IPubReportConstants.ORG_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			sqlBuffer.append(" or sum(v.accage_loc) != 0 ");
		} else if (IPubReportConstants.GROUP_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			sqlBuffer.append(" or sum(v.gr_accage_loc) != 0 ");
		} else if (IPubReportConstants.GLOBLE_LOCAL_CURRENCY.equals(queryVO.getLocalCurrencyType())) {
			sqlBuffer.append(" or sum(v.gl_accage_loc) != 0 ");
		}

		return sqlBuffer.toString();
	}
	
	public String getLoanAgeByAccAgeSumSql() throws SQLException, BusinessException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
		sqlBuffer.append(" select ");

		sqlBuffer.append(queryObjOrderExt);
		sqlBuffer.append(", sum(loanageori) originalbalance, sum(loanageloc) localbalance from ");
		sqlBuffer.append(getTmpTblName());
		sqlBuffer.append(" group by ").append(queryObjOrderExt);

		StringBuffer sqlBufferLink = new StringBuffer(" select ");

		sqlBufferLink.append(queryObjBaseBalExt.replace("a.", "tmp.")).append(", ");
		sqlBufferLink.append("m.originalbalance, m.localbalance, ");
		sqlBufferLink.append("tmp.loanageori, tmp.loanageloc, ");
		sqlBufferLink.append("tmp.propertyid propertyid, tmp.accountage accountage ");

		sqlBufferLink.append(" from ").append(getTmpTblName()).append(" tmp ");
		sqlBufferLink.append(" full outer join (").append(sqlBuffer).append(") m ");
		sqlBufferLink.append(" on ");
		String[] objs = queryObjOrderExt.split(",");
		for (String o : objs) {
			sqlBufferLink.append("tmp.").append(o).append(" = ").append("m.")
					.append(o).append(" and ");
		}
		sqlBufferLink.append(" 1 = 1 ");

		return sqlBufferLink.toString();
	}

	/**
	 * ��ȡ��ѯ�������(���������)�Ľ�SQL��<br>
	 * 
	 * @return String<br>
	 * @throws SQLException<br>
	 * @throws BusinessException<br>
	 */
	private String getLoanOriAgeByAccAgeLoanSql() throws SQLException, BusinessException {
		String jkzbAlias = getAlias("er_jkzb");

		// ��ѯ���䷽��
		TimeCtrlVO timeCtrlVO = getTimeCtrlVO();

		String tmpAccTable = getTmpAccTable(timeCtrlVO);
		String tmpTableAlias = getAlias("fipub_timecontrol_b");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName3());

		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(queryObjBaseBal.replaceAll("fb.", "zb."));
//		sqlBuffer.append(", ").append(beForeignCurrency ? jkzbAlias + ".bzbm " : "null ").append(PK_CURR);
		sqlBuffer.append(", ").append(jkzbAlias).append(".bzbm ").append(PK_CURR);
		sqlBuffer.append(", ").append(detailFields.replace("zb.", jkzbAlias + "."));
		
        sqlBuffer.append(", ").append(tmpTableAlias ).append( ".propertyid accageid");
        sqlBuffer.append(", ").append(tmpTableAlias ).append( ".descr accage");
		sqlBuffer.append(", 0 rn, ");
		sqlBuffer.append("isnull(sum(" ).append( jkzbAlias ).append( ".ybye), 0.0) accage_ori, ");
		sqlBuffer.append("isnull(sum(" ).append( jkzbAlias ).append( ".bbye), 0.0) accage_loc, ");
		sqlBuffer.append("isnull(sum(" ).append( jkzbAlias ).append( ".groupbbye), 0.0) gr_accage_loc, ");
		sqlBuffer.append("isnull(sum(" ).append( jkzbAlias ).append( ".globalbbye), 0.0) gl_accage_loc ");

//		sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);

        String anaDateField = ReportSqlUtils.getAnaDateField(queryVO.getAnaDate());
		// TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
        }
        
        // TODO byDetail
        //����ϸ������
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//            sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
            sqlBuffer.append(", ").append(jkzbAlias + ".bzbm ");
            
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djlxbm");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".pk_jkbx");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djbh");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djrq");
//            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".ybye");
            if (anaDateField.indexOf("djrq") < 0) {
                sqlBuffer.append(", ").append(anaDateField);
            }
            sqlBuffer.append(", fb.ybye");
            sqlBuffer.append(", fb.bbye");
            sqlBuffer.append(", fb.groupbbye");
            sqlBuffer.append(", fb.globalbbye");
//            sqlBuffer.append(", ").append(tmpTableAlias).append(".propertyid");
//            sqlBuffer.append(", ").append(tmpTableAlias).append(".descr");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        } else {
            // �������䷽��(��ʱ)��
            sqlBuffer.append(", ").append(tmpAccTable).append(tmpTableAlias);
        }
		sqlBuffer.append(" where ").append(ReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		

		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // ��ѯ����
		sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".dr = 0 ");
		sqlBuffer.append(" and " ).append( jkzbAlias).append(".sxbz = ").append(BXStatusConst.SXBZ_VALID); // ����״̬
		sqlBuffer.append(" and (zb.ybye <> 0 or zb.bbye <> 0 or zb.groupbbye <> 0 or zb.globalbbye <> 0)");
		sqlBuffer.append(ReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), jkzbAlias)); // ����
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(jkzbAlias + ".pk_org", queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".pk_group = '" ).append( queryVO.getPk_group() ).append( "'"); // ����

        // TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(") ").append(jkzbAlias);
            // �������䷽��(��ʱ)��
            sqlBuffer.append(", ").append(tmpAccTable).append(tmpTableAlias);
            sqlBuffer.append(" where 1 = 1");
        }
     // ȡ�ý�ֹ�����ֶ�
        String dateline = "'" + queryVO.getDateline() + "'";
        // ȡ�÷��������ֶ�

        // ��������
        // select datediff(mm, '2010-01-30', '2010-03-05') + datediff(day, dateadd(mm, datediff(mm, '2010-01-30', '2010-03-05'), '2010-01-30'), '2010-03-05') / 30.0
        // result��1.166667(����)
        StringBuffer tempBuffer = new StringBuffer();
        switch (SqlBuilder.getDatabaseType()) {
        case DBConsts.DB2: // SQLServer��
            tempBuffer.append("days(").append(dateline).append(")-days(").append(anaDateField).append(") ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue) ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue) ");
            break;
        case DBConsts.SQLSERVER:
            tempBuffer.append("datediff(day, ").append(anaDateField).append(", ").append(dateline).append(") ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue) ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue) ");
            break;

        case DBConsts.ORACLE: // Oracle��
            tempBuffer.append("datediff(day, ").append(anaDateField).append(", ").append(dateline).append(") ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue ");
            break;

        default:
            break;
        }
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replaceAll("fb.", "zb."));
		sqlBuffer.append(", ").append(groupByFields);
		sqlBuffer.append(", ").append(tmpTableAlias ).append( ".descr ");
		sqlBuffer.append(", ").append(tmpTableAlias ).append( ".propertyid ");
//		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(jkzbAlias ).append( ".bzbm");
//		}

		return sqlBuffer.toString();
	}

	private String getTmpAccTable(TimeCtrlVO timeCtrlVO) {
		if (StringUtils.isEmpty(tmpAccTable)) {
//			if (UnitDays.CALENDAR == UnitDays.valueOf(timeCtrlVO.getDays())) {
				// ���䷽������������
//				tmpAccTable = "fipub_timecontrol_b";
//			} else {
				// ��ȡ��ʱ��
	        //��ȡ��ǰҵ������
//	        UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
				tmpAccTable = TimeCtrlUtil.getTimeCtrlTmpTable(queryVO.getAccAgePlan(), new UFDate());
//			}
		}

		return tmpAccTable;
	}

	/**
	 * ��ȡ��ѯ�������(���������)�ĳ��SQL��<br>
	 * �������������幦��������
	 * @return String<br>
	 * @throws SQLException<br>
	 * @throws BusinessException<br>
	 */
	public  String getLoanAgeByAccAgeContrastSql() throws SQLException, BusinessException {
		String jkzbAlias = getAlias("er_jkzb");
		String bxzbAlias = "bx" + getAlias("er_bxzb");
		String cxAlias = getAlias("er_bxcontrast");

		// ��ѯ���䷽��
		TimeCtrlVO timeCtrlVO = getTimeCtrlVO();

		String tmpAccTable = getTmpAccTable(timeCtrlVO);
		String tmpTableAlias = getAlias("fipub_timecontrol_b");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName3());
		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(queryObjBaseBal);
//		sqlBuffer.append(", ").append(beForeignCurrency ? jkzbAlias + ".bzbm " : "null ").append(PK_CURR);
		sqlBuffer.append(", ").append(jkzbAlias).append(".bzbm ").append(PK_CURR);
		sqlBuffer.append(", ").append(detailFields);
		sqlBuffer.append(", ").append(tmpTableAlias ).append( ".propertyid accageid");
		sqlBuffer.append(", ").append(tmpTableAlias ).append( ".descr accage");
		sqlBuffer.append(", 0 rn, ");
		sqlBuffer.append("isnull(-sum(" ).append( cxAlias ).append( ".cjkybje), 0.0) accage_ori, ");
		sqlBuffer.append("isnull(-sum(" ).append( cxAlias ).append( ".cjkbbje), 0.0) accage_loc, ");
		sqlBuffer.append("isnull(-sum(" ).append( cxAlias ).append( ".groupcjkbbje), 0.0) gr_accage_loc, ");
		sqlBuffer.append("isnull(-sum(" ).append( cxAlias ).append( ".globalcjkbbje), 0.0) gl_accage_loc ");

//		sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
//		sqlBuffer.append(" inner join er_bxcontrast ").append(cxAlias).append(" on ").append(jkzbAlias).append(".pk_jkbx = ").append(cxAlias).append(".pk_jkd ");
//		sqlBuffer.append(" inner join er_bxzb ").append(bxzbAlias).append(" on ").append(cxAlias).append(".pk_bxd = ").append(bxzbAlias).append(".pk_jkbx ");

        // ȡ�÷��������ֶ�
        String anaDateField = ReportSqlUtils.getAnaDateField(queryVO.getAnaDate());
		// TODO byDetail
        if (needQueryByDetail()) {
            sqlBuffer.append(" from ( ");
        } else {
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_bxcontrast ").append(cxAlias)
                     .append(" on ").append(jkzbAlias).append(".pk_jkbx = ").append(cxAlias).append(".pk_jkd ");
            sqlBuffer.append(" inner join er_bxzb ").append(bxzbAlias).append(" on ")
                     .append(cxAlias).append(".pk_bxd = ").append(bxzbAlias).append(".pk_jkbx ");
            // �������䷽��(��ʱ)��
            sqlBuffer.append(", ").append(tmpAccTable).append(tmpTableAlias);
        }
        
        // TODO byDetail
        //����ϸ������
        if (needQueryByDetail()) {
            sqlBuffer.append("select ");
            sqlBuffer.append(fixedFields.replaceAll(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
//            sqlBuffer.append(", ").append(queryObjBaseBal);
            sqlBuffer.append(", ").append(groupByBaseExp);
            sqlBuffer.append(", ").append(jkzbAlias + ".bzbm ");
            
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djlxbm");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".pk_jkbx");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djbh");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".djrq");
//            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".ybye");
            sqlBuffer.append(", " ).append( jkzbAlias ).append( ".contrastEndDate");
            if (anaDateField.indexOf("djrq") < 0) {
                sqlBuffer.append(", ").append(anaDateField);
            }
            sqlBuffer.append(", fb.ybye");
            sqlBuffer.append(", fb.bbye");
            sqlBuffer.append(", fb.groupbbye");
            sqlBuffer.append(", fb.globalbbye");
            sqlBuffer.append(", fb.bbje");
            sqlBuffer.append(", fb.groupbbje");
            sqlBuffer.append(", fb.globalbbje");
            sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);
            sqlBuffer.append(" inner join er_busitem fb on ")
            .append(jkzbAlias).append(".pk_jkbx = fb.pk_jkbx");
        }
		
		sqlBuffer.append(" where ").append(ReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		sqlBuffer.append(getQueryObjSql(jkzbAlias)); // ��ѯ����
		sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".sxbz = ").append(BXStatusConst.SXBZ_VALID); // ����״̬
		sqlBuffer.append(ReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), jkzbAlias)); // ����
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(jkzbAlias + ".pk_org", queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(" and " ).append( jkzbAlias ).append( ".pk_group = '" ).append( queryVO.getPk_group() ).append( "'"); // ����
		// TODO byDetail
        if (needQueryByDetail()) {
            String[] fields = groupByBaseBal.split(",");
            StringBuilder sql = new StringBuilder();
            for (String field : fields) {
                int nPos = field.indexOf("fb."); 
                if (nPos >= 0) {
                    String fbField = field.replaceAll("fb.", cxAlias + ".");
                    String zbField = field.replaceAll("fb.", jkzbAlias + ".");
                    sql.append(zbField).append(" = ");
                    sql.append(fbField);
                    sql.append(" and ");
                }
            }
            sqlBuffer.append(") ").append(jkzbAlias);
            sqlBuffer.append( " inner join er_bxcontrast " ).append( cxAlias);
            sqlBuffer.append(" on " ).append( jkzbAlias ).append( ".pk_jkbx = " ).append( cxAlias ).append( ".pk_jkd");
            sqlBuffer.append(" inner join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd ");
            // �������䷽��(��ʱ)��
            sqlBuffer.append(", ").append(tmpAccTable).append(tmpTableAlias);
            sqlBuffer.append(" where ").append(sql.toString());
        } else {
            sqlBuffer.append(" and " );
        }
        
        sqlBuffer.append( bxzbAlias ).append( ".sxbz < ").append(BXStatusConst.SXBZ_VALID); // ����״̬
        sqlBuffer.append(" and " ).append( bxzbAlias ).append( ".pk_group = '" ).append( queryVO.getPk_group() ).append( "'"); // ����
        
        // ȡ�ý�ֹ�����ֶ�
        String dateline = "'" + queryVO.getDateline() + "'";

        // ��������
        // select datediff(mm, '2010-01-30', '2010-03-05') + datediff(day, dateadd(mm, datediff(mm, '2010-01-30', '2010-03-05'), '2010-01-30'), '2010-03-05') / 30.0
        // result��1.166667(����)
        StringBuffer tempBuffer = new StringBuffer();
        switch (SqlBuilder.getDatabaseType()) {
        case DBConsts.DB2: // db2��
            tempBuffer.append("days(").append(dateline).append(")-days(").append(anaDateField).append(") ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue) ");
            sqlBuffer.append(" and (").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue) ");
            break;
        case DBConsts.SQLSERVER: // SQLServer��
            tempBuffer.append("datediff(day, ").append(anaDateField).append(", ").append(dateline).append(") ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue ");
            break;
        case DBConsts.ORACLE: // Oracle��
            tempBuffer.append("datediff(day, ").append(anaDateField).append(", ").append(dateline).append(") ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" > ").append(tmpTableAlias).append(".startvalue ");
            sqlBuffer.append(" and ").append(tempBuffer).append(" <= ").append(tmpTableAlias).append(".endvalue ");
            break;
        default:
            break;
        }
        
		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal);
		sqlBuffer.append(", ").append(groupByFields);
		sqlBuffer.append(", ").append(tmpTableAlias).append(".descr");
		sqlBuffer.append(", ").append(tmpTableAlias).append(".propertyid");
//		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(jkzbAlias + ".bzbm");
//		}

		return sqlBuffer.toString();
	}

	/**
	 * ��ѯÿ����ѯ����Ļ�������
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getCollectSql() throws SQLException {
		String collFixedFields = fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName2());

		sqlBuffer.append(" select ");
		sqlBuffer.append(collFixedFields).append(", ");
		sqlBuffer.append(queryObjOrderExt).append(", ");
		sqlBuffer.append(PK_CURR);
		sqlBuffer.append(", accageid, accage, ");
		sqlBuffer.append("(row_number() over (order by ");
		sqlBuffer.append(collFixedFields).append(", ");
		sqlBuffer.append(queryObjOrderExt);
		sqlBuffer.append(")) rn, ");

		sqlBuffer.append(" sum(accage_ori) accage_ori, sum(accage_loc) accage_loc, sum(gr_accage_loc) gr_accage_loc, sum(gl_accage_loc) gl_accage_loc ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" group by ");
		sqlBuffer.append(collFixedFields);
		sqlBuffer.append(", ").append(queryObjOrderExt);
		sqlBuffer.append(", ").append(PK_CURR);
		sqlBuffer.append(", accageid, accage");

		return sqlBuffer.toString();

	}

	/**
	 * ��ѯÿ����ѯ����Ľ�����
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getLoanTotal() throws SQLException {
		String collFixedFields = fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "");
		String collDetailFields = detailTemFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "");


		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(getTmpTblName());
		} else {
			sqlBuffer.append(getTmpTblName2());
		}


		sqlBuffer.append(" select ");

		sqlBuffer.append(collFixedFields);
		sqlBuffer.append(", ").append(queryObjOrderExt);
		sqlBuffer.append(", ").append(PK_CURR);
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(collDetailFields);
		}
		sqlBuffer.append(", -7 accageid, '");
		sqlBuffer.append(getLBL_BAL());
		sqlBuffer.append("' accage, 0 rn, ");
		sqlBuffer.append(" sum(accage_ori) accage_ori, sum(accage_loc) accage_loc, sum(gr_accage_loc) gr_accage_loc, sum(gl_accage_loc) gl_accage_loc ");

		sqlBuffer.append(" from ");
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(getTmpTblName());
		} else {
			sqlBuffer.append(getTmpTblName2());
		}

		sqlBuffer.append(" group by ");
		sqlBuffer.append(collFixedFields);
		sqlBuffer.append(", ").append(queryObjOrderExt);
		sqlBuffer.append(", ").append(PK_CURR);
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(", ").append(collDetailFields);
		}

		return sqlBuffer.toString();

	}
	/**
	 * ����С�ƺϼ�
	 *
	 * @return
	 * @throws SQLException
	 */
	private List<String> getTotalSqls() throws SQLException {
		List<String> totalSqlList = new ArrayList<String>();

		// ������Ҫ����С�ƺϼƵĶ���
		List<String> allQryobjList = new ArrayList<String>();
		String[] fixedObjs = fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "").split(",");
		for (int i = 0; i < fixedObjs.length; i++) {
			allQryobjList.add(fixedObjs[i].trim());
		}

		String[] qryobjs = queryObjOrderExt.split(",");
		for (int i = 0; i < qryobjs.length; i++) {
			allQryobjList.add(qryobjs[i].trim());
		}

		allQryobjList.add(PK_CURR);

		int totalCnt = allQryobjList.size() - 2;
//		if (beForeignCurrency) {

//        if (queryVO.isQueryDetail()) {
            totalCnt += 1;
//        }
//		}

		String[] details = detailTemFields.replace(IErmReportConstants.REPLACE_TABLE + ".", "").split(",");

		// ��ʽƴдSQL
		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(getTmpTblName());
		} else {
			sqlBuffer.append(getTmpTblName2());
		}

		sqlBuffer.append(" select ");
		int i = 0;
		for (; i < totalCnt; i++) {
			sqlBuffer.append(allQryobjList.get(i)).append(", ");
		}
		for (; i < allQryobjList.size(); i++) {
			sqlBuffer.append("null ").append(allQryobjList.get(i)).append(", ");
		}

		if (queryVO.isQueryDetail()) {
			for (String d : details) {
				sqlBuffer.append("null ").append(d).append(", ");
			}
		}

		sqlBuffer.append("accageid, accage, ");

		i = 0;
		for (; i < totalCnt; i++) {
			sqlBuffer.append("grouping(").append(allQryobjList.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW + 1).append(" rn, ");

		sqlBuffer.append("sum(accage_ori) accage_ori, sum(accage_loc) accage_loc, sum(gr_accage_loc) gr_accage_loc, sum(gl_accage_loc) gl_accage_loc ");

		sqlBuffer.append(" from ");
		if (queryVO.isQueryDetail()) {
			sqlBuffer.append(getTmpTblName());
		} else {
			sqlBuffer.append(getTmpTblName2());
		}

		sqlBuffer.append(" group by ");
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			i = 0;
			for (; i < totalCnt; i++) {
				sqlBuffer.append(allQryobjList.get(i)).append(", ");
			}
			sqlBuffer.append("accageid, accage");
			sqlBuffer.append(" with cube ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append("cube(");
			i = 0;
			for (; i < totalCnt; i++) {
				sqlBuffer.append(allQryobjList.get(i)).append(", ");
			}
			sqlBuffer.append("accageid, accage)");
			break;
		default:
			break;
		}

		sqlBuffer.append(" having ");
		i = 0;
		for (; i < totalCnt - 1; i++) {
			sqlBuffer.append("grouping(").append(allQryobjList.get(i)).append(") <= grouping(")
					.append(allQryobjList.get(i + 1)).append(") and ");
		}
		sqlBuffer.append("grouping(").append(allQryobjList.get(0)).append(") = 0 "); // ���Ų�����С��
		sqlBuffer.append(" and grouping(").append(allQryobjList.get(1)).append(") = 0 "); // ҵ��Ԫ�ܼƵ�������
		sqlBuffer.append(" and grouping(").append("accageid").append(") = 0 ");
		sqlBuffer.append(" and grouping(").append("accage").append(") = 0 ");

		totalSqlList.add(sqlBuffer.toString());

		if (queryVO.getPk_orgs().length > 1) {
			// ����֯��ѯ���ż����ܼ�
			// �����ܼ�
			sqlBuffer = new StringBuffer();
			sqlBuffer.append(" insert into ");
			if (queryVO.isQueryDetail()) {
				sqlBuffer.append(getTmpTblName());
			} else {
				sqlBuffer.append(getTmpTblName2());
			}

			sqlBuffer.append(" select ");
			i = 0;
			for (; i < allQryobjList.size(); i++) {
				sqlBuffer.append("null ").append(allQryobjList.get(i)).append(", ");
			}
			if (queryVO.isQueryDetail()) {
				for (String d : details) {
					sqlBuffer.append("null ").append(d).append(", ");
				}
			}

			sqlBuffer.append("accageid, accage, ");
			sqlBuffer.append(SmartProcessor.MAX_ROW ).append( 10).append(" rn, ");
			sqlBuffer.append("sum(accage_ori) accage_ori, sum(accage_loc) accage_loc, sum(gr_accage_loc) gr_accage_loc, sum(gl_accage_loc) gl_accage_loc ");

			sqlBuffer.append(" from ");
			if (queryVO.isQueryDetail()) {
				sqlBuffer.append(getTmpTblName());
			} else {
				sqlBuffer.append(getTmpTblName2());
			}

			sqlBuffer.append(" where rn < ").append(SmartProcessor.MAX_ROW);

			sqlBuffer.append(" group by ");
			sqlBuffer.append("accageid, accage");

			totalSqlList.add(sqlBuffer.toString());
		}

		return totalSqlList;
	}

	/**
	 * ��ѯ���䷽��
	 *
	 * @return TimeCtrlVO
	 * @throws BusinessException
	 */
	private TimeCtrlVO getTimeCtrlVO() throws BusinessException {
		if (timeCtrlVO == null) {
			timeCtrlVO = ErmCommonReportMethod.getTimeCtrlVO(queryVO.getAccAgePlan());
		}
		return timeCtrlVO;
	}

	/**
	 * ��ȡ��ʱ����<br>
	 *
	 * @return String<br>
	 * @throws SQLException<br>
	 */

	private String getTmpTblName() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName)) {
			String[] colNames = getTmpTblColNames();
			Integer[] colTypes = getTmpTblColTypes();
			String tableName = "tmp_erm_loanacc_" + qryObjLen;
			tmpTblName = TmpTableCreator.createTmpTable(tableName, colNames, colTypes);
		}

		return tmpTblName;
	}

	/**
	 * ��ȡ��ʱ����<br>
	 * @return String[]<br>
	 */
	private String[] getTmpTblColNames() {
		if (tmpTblColNames == null) {
			// ��ѯ�������
			int qryObjLen = queryVO.getQryObjs().size();

			StringBuffer otherColNameBuf = new StringBuffer();
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", ").append(PK_CURR).append(", ");
			otherColNameBuf.append(detailTemFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", accageid, accage, rn, ");
			otherColNameBuf.append("accage_ori, accage_loc, gr_accage_loc, gl_accage_loc");

			String[] otherColNames = otherColNameBuf.toString().split(",");

			tmpTblColNames = new String[qryObjLen + otherColNames.length];

			tmpTblColNames[0] = otherColNames[0];
			tmpTblColNames[1] = otherColNames[1];

			for (int i = 0; i < qryObjLen; i++) {
				tmpTblColNames[i+2] = IPubReportConstants.QRY_OBJ_PREFIX + i + "pk";
			}

			System.arraycopy(otherColNames, 2, tmpTblColNames, qryObjLen + 2, otherColNames.length - 2);
		}

		return tmpTblColNames;
	}

	/**
	 * ��ȡ��ʱ��������<br>
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColTypes() {
		if (tmpTblColTypes == null || tmpTblColTypes.length == 0) {
			tmpTblColTypes = new Integer[getTmpTblColNames().length];
			int i = 0;
			for (; i < tmpTblColTypes.length - 7; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}

			tmpTblColTypes[i++] = Types.INTEGER; //����ID
			tmpTblColTypes[i++] = Types.VARCHAR; //��������
			tmpTblColTypes[i++] = Types.INTEGER; // RN��

			tmpTblColTypes[i++] = Types.DECIMAL;
			tmpTblColTypes[i++] = Types.DECIMAL;
			tmpTblColTypes[i++] = Types.DECIMAL;
			tmpTblColTypes[i++] = Types.DECIMAL;
		}

		return tmpTblColTypes;
	}
	
	/**
	 * ��ȡ��ʱ����<br>
	 *
	 * @return String<br>
	 * @throws SQLException<br>
	 */
	private String getTmpTblName3() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName3)) {
			String[] colNames = getTmpTblColNames3();
			Integer[] colTypes = getTmpTblColTypes3();
			String tableName = "tmp_erm_loanacc3_" + qryObjLen;
			tmpTblName3 = TmpTableCreator.createTmpTable(tableName, colNames, colTypes);
		}

		return tmpTblName3;
	}

	/**
	 * ��ȡ��ʱ����<br>
	 * @return String[]<br>
	 */
	private String[] getTmpTblColNames3() {
		if (tmpTblColName3s == null) {
			// ��ѯ�������
			int qryObjLen = queryVO.getQryObjs().size();

			StringBuffer otherColNameBuf = new StringBuffer();
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", ").append(PK_CURR).append(", ");
			otherColNameBuf.append(detailTemFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", accageid, accage, rn, ");
			otherColNameBuf.append("accage_ori, accage_loc, gr_accage_loc, gl_accage_loc");



			String[] otherColNames = otherColNameBuf.toString().split(",");

			tmpTblColName3s = new String[qryObjLen + otherColNames.length];

			tmpTblColName3s[0] = otherColNames[0];
			tmpTblColName3s[1] = otherColNames[1];


			for (int i = 0; i < qryObjLen; i++) {
				tmpTblColName3s[i+2] = IPubReportConstants.QRY_OBJ_PREFIX + i + "pk";
			}

			System.arraycopy(otherColNames, 2, tmpTblColName3s, qryObjLen + 2, otherColNames.length - 2);
		}

		return tmpTblColName3s;
	}


	/**
	 * ��ȡ��ʱ��������<br>
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColTypes3() {
		if (tmpTblColType3s == null || tmpTblColType3s.length == 0) {
			tmpTblColType3s = new Integer[getTmpTblColNames().length];
			int i = 0;
			for (; i < tmpTblColType3s.length - 7; i++) {
				tmpTblColType3s[i] = Types.VARCHAR;
			}

			tmpTblColType3s[i++] = Types.INTEGER; //����ID
			tmpTblColType3s[i++] = Types.VARCHAR; //��������
			tmpTblColType3s[i++] = Types.INTEGER; // RN��

			tmpTblColType3s[i++] = Types.DECIMAL;
			tmpTblColType3s[i++] = Types.DECIMAL;
			tmpTblColType3s[i++] = Types.DECIMAL;
			tmpTblColType3s[i++] = Types.DECIMAL;
		}

		return tmpTblColType3s;
	}

	/**
	 * ��ȡ��ʱ����<br>
	 *
	 * @return String<br>
	 * @throws SQLException<br>
	 */

	private String getTmpTblName2() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName2)) {
			String[] colNames = getTmpTblColName2s();
			Integer[] colTypes = getTmpTblColType2s();
			String tableName = "tmp_erm_loanacc2_" + qryObjLen;
			tmpTblName2 = TmpTableCreator.createTmpTable(tableName, colNames, colTypes);
		}

		return tmpTblName2;
	}


	/**
	 * ��ȡ��ʱ����<br>
	 *
	 * @return String[]<br>
	 */
	private String[] getTmpTblColName2s() {
		if (tmpTblColName2s == null) {
			// ��ѯ�������
			int qryObjLen = queryVO.getQryObjs().size();

			StringBuffer otherColNameBuf = new StringBuffer();
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", ").append(PK_CURR);
			otherColNameBuf.append(", accageid, accage, rn");
			otherColNameBuf.append(", accage_ori, accage_loc, gr_accage_loc, gl_accage_loc");
			String[] otherColNames = otherColNameBuf.toString().split(",");

			tmpTblColName2s = new String[qryObjLen + otherColNames.length];

			tmpTblColName2s[0] = otherColNames[0];
			tmpTblColName2s[1] = otherColNames[1];
			for (int i = 0; i < qryObjLen; i++) {
				tmpTblColName2s[i + 2] = IPubReportConstants.QRY_OBJ_PREFIX + i + "pk";
			}

			System.arraycopy(otherColNames, 2, tmpTblColName2s, qryObjLen + 2, otherColNames.length - 2);
		}

		return tmpTblColName2s;
	}

	/**
	 * ��ȡ��ʱ��������<br>
	 *
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColType2s() {
		if (tmpTblColType2s == null || tmpTblColType2s.length == 0) {
			tmpTblColType2s = new Integer[getTmpTblColName2s().length];
			int i = 0;
			for (; i < tmpTblColType2s.length - 7; i++) {
				tmpTblColType2s[i] = Types.VARCHAR;
			}
			tmpTblColType2s[i++] = Types.INTEGER; //����ID
			tmpTblColType2s[i++] = Types.VARCHAR; //��������
			tmpTblColType2s[i++] = Types.INTEGER; //RN ��

			tmpTblColType2s[i++] = Types.DECIMAL;
			tmpTblColType2s[i++] = Types.DECIMAL;
			tmpTblColType2s[i++] = Types.DECIMAL;
			tmpTblColType2s[i++] = Types.DECIMAL;
		}

		return tmpTblColType2s;
	}


}

