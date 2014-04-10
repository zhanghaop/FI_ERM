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
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.report.ReportQueryCondVO;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *      ������������ϸ�˱����漰��sql
 *      ��Խ����ϸ�ˣ������������֣�
 *      ���ԭֵ(������)������ֵ(������)��ԭֵ-���ֵ =�ڳ�
 *      �������ۼƷ���     ��������ϸ����
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����08:27:29
 */
public class LoanDetailSQLCreator extends ErmBaseSqlCreator{
	// �̶���ѯ�ֶ�

	private static String detailFields = "@Table.djrq, @Table.zy,  @Table.pk_billtype, @Table.pk_jkbx,  @Table.djbh, @Table.kjqj, @Table.qzzt";

	private boolean isShowDateTotal = false; // �Ƿ���ʾ��С��

	private String PK_CONTRASTJK = "pk_contrastjk";

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;

	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public void setParams(ReportQueryCondVO queryVO) {
		super.setParams(queryVO);
		Object showDateTotal = queryVO.getUserObject().get(IErmReportConstants.KEY_SHOW_DATE_TOTAL);
		if (showDateTotal != null) {
			isShowDateTotal = ((UFBoolean) showDateTotal).booleanValue();
		}
	}

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.addAll(getSqlsByMonthOrDate()); // ���·�/���ڲ�ѯ
												// ����������ͨ�����������ְ����·ݺͰ������ڵ�

		sqlList.add(getAddPeriodBeginSql()); // �����ڳ�(��ĩ)���
		sqlList.add(getDateSubTotalSql()); // ������С��
		sqlList.add(getSubTotalSql()); // ����С�ƺϼ�
		sqlList.addAll(getSubTotalBalSql()); // ����С�ƺϼ���ĩ���

		return sqlList.toArray(new String[0]);
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
		sqlBuffer.append(", (case when isnull(v.djrq, '~') = '~' then 1 else 0 end) as is_djrq_null"); // is_djrq_null
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
					bdTable + i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable + i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

		sqlBuffer.append(" order by ");
		sqlBuffer.append("is_org_null, code_org");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", isnull").append(i).append(", ").append(IPubReportConstants.QRY_OBJ_PREFIX)
					.append(i).append("code");
		}
		if (beForeignCurrency) {
			sqlBuffer.append(", is_currtype_null, pk_currtype");
		}
		sqlBuffer.append(", is_begin, is_djrq_null, djrq,  rn, pk_billtype");

		return sqlBuffer.toString();
	}

	@Override
	public String[] getDropTableSqls() throws SQLException {
		return new String[0];
	}

	private List<String> getSqlsByMonthOrDate() throws BusinessException, SQLException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getPeriodOriginalSql()); // ����ڳ�
		sqlList.add(getLoanDetail()); // �����ϸ��
		sqlList.add(getContrastDetail()); // ������ϸ

		return sqlList;
	}

	private String getPeriodOriginalSql() throws BusinessException, SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
		sqlBuffer.append(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "a"));
		sqlBuffer.append(", ").append(queryObjBaseDetailFld);
		sqlBuffer.append(", a.pk_currtype");
		sqlBuffer.append(", null djrq, '").append(IErmReportConstants.CONST_BRIEF).append("' zy"); // �ڳ�
		sqlBuffer.append(", null pk_billtype, null pk_jkbx, null billno, null kjqj, null qzzt");
		sqlBuffer.append(", null ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 0 rn");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc");
		sqlBuffer.append(", sum(a.bal_ori) bal_ori, sum(a.bal_loc) bal_loc, sum(a.gr_bal_loc) gr_bal_loc, sum(a.gl_bal_loc) gl_bal_loc");

		sqlBuffer.append(" from (");
		sqlBuffer.append(getLoanOriginalSql() + " union all " + getLoanContrastSql()).append(") a ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "a"));
		sqlBuffer.append(", ").append(queryObjBaseBalExt);
		sqlBuffer.append(", a.pk_currtype");

		return sqlBuffer.toString();
	}

	/**
	 * ����������ѯ���ԭֵ(������)<br>
	 * ���ղ�ѯ������ö�Ӧ���ڳ�ֵ��ע����ܲ������ڳ�ֵ�������ڳ�����ֵΪ0
	 * ע�⣺qcbz�Ĵ���
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException
	 * @throws SQLException<br>
	 */
	private String getLoanOriginalSql() throws BusinessException, SQLException {
		String jkzbAlias = getAlias("er_jkzb");

		StringBuffer sqlBuffer = new StringBuffer(" select ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb." + BXBusItemVO.SZXMID, jkzbAlias + "." + BXBusItemVO.SZXMID));
		// sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", " + jkzbAlias + ".bzbm ").append(PK_CURR);
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".ybje) bal_ori");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".bbje) bal_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".groupbbje) gr_bal_loc");
		sqlBuffer.append(", sum(").append(jkzbAlias).append(".globalbbje) gl_bal_loc");

		sqlBuffer.append(" from er_jkzb ").append(jkzbAlias);

		// ���ò�ѯ�����̶�ֵ
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		if (queryVO.getBeginDate() != null) {
			sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq < '").append(
					queryVO.getBeginDate()).append("' ");

			sqlBuffer.append(" and (").append(jkzbAlias).append(".contrastEndDate >= '").append(
					queryVO.getBeginDate()).append("' ");
			sqlBuffer.append(" or isnull(").append(jkzbAlias).append(".contrastEndDate, '~') = '~')");
		} else {
			sqlBuffer.append(" and isnull(").append(jkzbAlias).append(".contrastEndDate, '~') = '~'");
		}

		if(!StringUtils.isEmpty(queryVO.getPk_currency())){
			sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // ����
		}

		sqlBuffer.append(ErmReportSqlUtils.getOrgSql(queryVO.getPk_orgs(), true)); // ҵ��Ԫ
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // ����
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true));
		sqlBuffer.append(getQueryObjSql()); // ��ѯ����
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb." + BXBusItemVO.SZXMID, jkzbAlias + "." + BXBusItemVO.SZXMID));
		sqlBuffer.append(", ").append(jkzbAlias + ".bzbm");

		return sqlBuffer.toString();
	}

	/**
	 * ����������ѯ����ֵ(������)<br>
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
		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb." + BXBusItemVO.SZXMID, jkzbAlias + "." + BXBusItemVO.SZXMID));
		// sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", " + jkzbAlias + ".bzbm ").append(PK_CURR);
		sqlBuffer.append(", -sum(" + cxAlias + ".ybje) bal_ori");
		sqlBuffer.append(", -sum(" + cxAlias + ".bbje) bal_loc");
		sqlBuffer.append(", -sum(" + cxAlias + ".groupbbje) gr_bal_loc");
		sqlBuffer.append(", -sum(" + cxAlias + ".globalbbje) gl_bal_loc");

		sqlBuffer.append(" from er_jkzb ").append(jkzbAlias).append(" inner join er_bxcontrast ").append(cxAlias);
		sqlBuffer.append(" on ").append(jkzbAlias).append(".pk_jkbx = ").append(cxAlias).append(".pk_jkd ");

		// ���ò�ѯ�����̶�ֵ
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		// ���ڵ��������������������Լ������������
		if (queryVO.getBeginDate() != null) {
			sqlBuffer.append(" and ").append(jkzbAlias).append(".djrq < '").append(queryVO.getBeginDate()).append("' ");
			sqlBuffer.append(" and (").append(jkzbAlias).append(".contrastEndDate >= '").append(queryVO.getBeginDate()).append("' ");
			sqlBuffer.append(" or isnull(").append(jkzbAlias).append(".contrastEndDate,'~') = '~')");
			sqlBuffer.append(" and " + cxAlias + ".cxrq< '").append(queryVO.getBeginDate()).append("' ");
		} else {
			sqlBuffer.append(" and isnull(").append(jkzbAlias).append(".contrastEndDate, '~') = '~' ");
		}
		sqlBuffer.append(getQueryObjSql());// ��ѯ����
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // ����״̬
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), true)); // ����
		sqlBuffer.append(SqlUtils.getInStr(" and " + jkzbAlias + ".pk_org", queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(ErmReportSqlUtils.getGroupSql(queryVO.getPk_group(), true)); // ҵ��Ԫ
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0 ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, jkzbAlias));
		sqlBuffer.append(", ").append(groupByBaseBal.replace("fb." + BXBusItemVO.SZXMID, jkzbAlias + "." + BXBusItemVO.SZXMID));
		sqlBuffer.append(", ").append(jkzbAlias).append(".bzbm");

		return sqlBuffer.toString();
	}

	/**
	 * ����������ѯ�����ϸ��¼<br>
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
		sqlBuffer.append(jkzbAlias + "." + PK_GROUP + ", " + jkzbAlias + "." + PK_ORG);
		sqlBuffer.append(", ").append(queryObjBaseBal.replace("fb." + BXBusItemVO.SZXMID, jkzbAlias + "." + BXBusItemVO.SZXMID));
		// sqlBuffer.append(", ").append(beForeignCurrency ? (jkzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", " + jkzbAlias + ".bzbm ").append(PK_CURR);
		sqlBuffer.append(", " + jkzbAlias + ".djrq djrq");
		sqlBuffer.append(", " + jkzbAlias + ".zy zy");
		sqlBuffer.append(", " + jkzbAlias + ".djlxbm pk_billtype");
		sqlBuffer.append(", " + jkzbAlias + ".pk_jkbx pk_jkbx");
		sqlBuffer.append(", " + jkzbAlias + ".djbh billno");
		sqlBuffer.append(", " + jkzbAlias + ".kjqj kjqj");
		sqlBuffer.append(", case when (").append(jkzbAlias).append(".ybye = 0 or ").append(jkzbAlias)
				.append(".qzzt = 1) then '").append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0113")/*��*/)
				.append("' else '").append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0114")/*��*/).append("' end qzzt");
		sqlBuffer.append(", null ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 1 rn, ");
		sqlBuffer.append(jkzbAlias + ".ybje jk_ori, ");
		sqlBuffer.append(jkzbAlias + ".bbje jk_loc, ");
		sqlBuffer.append(jkzbAlias + ".groupbbje gr_jk_loc, ");
		sqlBuffer.append(jkzbAlias + ".globalbbje gl_jk_loc, ");
		sqlBuffer.append("0.0 hk_ori, ");
		sqlBuffer.append("0.0 hk_loc, ");
		sqlBuffer.append("0.0 gr_hk_loc, ");
		sqlBuffer.append("0.0 gl_hk_loc, ");
		sqlBuffer.append("(" + jkzbAlias + ".ybje - 0.0) bal_ori, ");
		sqlBuffer.append("(" + jkzbAlias + ".bbje -0.0) bal_loc, ");
		sqlBuffer.append("(" + jkzbAlias + ".groupbbje - 0.0) gr_bal_loc, ");
		sqlBuffer.append("(" + jkzbAlias + ".globalbbje -0.0) gl_bal_loc ");

		sqlBuffer.append(" from er_jkzb " + jkzbAlias);

		// ���ò�ѯ�����̶�ֵ
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql(jkzbAlias));

		if (queryVO.getBeginDate() != null) {
			// �������ڵĴ���
			sqlBuffer.append(" and ").append(jkzbAlias + ".djrq >= '").append(queryVO.getBeginDate()).append("' ");
		}
		if (queryVO.getEndDate() != null) {
			// �������ڵĴ���
			sqlBuffer.append(" and ").append(jkzbAlias + ".djrq <= '").append(queryVO.getEndDate()).append("' ");
		}

		sqlBuffer.append(ReportSqlUtils.getQueryObjSql(queryVO.getQryObjs())); // ��ѯ����
		sqlBuffer.append(getBillStatusSQL(queryVO, false, true)); // ����״̬
		sqlBuffer.append(ReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), jkzbAlias)); // ����
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(jkzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(" and ").append(jkzbAlias + "." + PK_GROUP + " = '").append(queryVO.getPk_group()).append("' "); // ����
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0");

		return sqlBuffer.toString();
	}

	/**
	 * ����������ѯ������ϸ��¼<br>
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
		sqlBuffer.append(jkzbAlias + "." + PK_GROUP + ", " + cxAlias + "." + PK_ORG);
		sqlBuffer.append(", ").append(queryObjBaseBal.replace("zb.", jkzbAlias + ".")); // ��ѯ����
		sqlBuffer.append(", " + jkzbAlias + ".bzbm ").append(PK_CURR);
		sqlBuffer.append(", " + cxAlias + ".cxrq djrq"); // ��������
		sqlBuffer.append(", " + jkzbAlias + ".zy zy");
		sqlBuffer.append(", bxzb.djlxbm pk_billtype");
		sqlBuffer.append(", bxzb.pk_jkbx pk_jkbx");
		sqlBuffer.append(", bxzb.djbh billno");
		sqlBuffer.append(", " + jkzbAlias + ".kjqj kjqj");
		sqlBuffer.append(", null qzzt");
		sqlBuffer.append(", ").append(cxAlias + ".pk_jkd ").append(PK_CONTRASTJK);
		sqlBuffer.append(", 1 rn");
		sqlBuffer.append(", 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc");
		sqlBuffer.append(", " + "sum(" + cxAlias + ".ybje) hk_ori");
		sqlBuffer.append(", " + "sum(" + cxAlias + ".bbje) hk_loc");
		sqlBuffer.append(", " + "sum(" + cxAlias + ".groupbbje) gr_hk_loc");
		sqlBuffer.append(", " + "sum(" + cxAlias + ".globalbbje) gl_hk_loc");

		sqlBuffer.append(", (0.0 - sum(" + cxAlias + ".ybje)) bal_ori ");
		sqlBuffer.append(", (0.0 - sum(" + cxAlias + ".bbje)) bal_loc ");
		sqlBuffer.append(", (0.0 - sum(" + cxAlias + ".groupbbje)) gr_bal_loc  ");
		sqlBuffer.append(", (0.0 - sum(" + cxAlias + ".globalbbje)) gl_bal_loc ");

		sqlBuffer.append(" from er_jkzb " + jkzbAlias).append(" inner join er_bxcontrast " + cxAlias);
		sqlBuffer.append(" on ").append(jkzbAlias + ".pk_jkbx = ").append(cxAlias + ".pk_jkd");
		sqlBuffer.append(" left join er_bxzb bxzb on bxzb.pk_jkbx = fb.pk_bxd ");

		// ���ò�ѯ�����̶�ֵ
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		if (!StringUtils.isEmpty(getCompositeWhereSql(jkzbAlias))) {
			sqlBuffer.append(getCompositeWhereSql(jkzbAlias).replace("zb." + BXBusItemVO.SZXMID,
					cxAlias + "." + BXBusItemVO.SZXMID));
		}

		if (queryVO.getBeginDate() != null) { // ��ѯ��ʼ����
			sqlBuffer.append(" and ").append(jkzbAlias).append(".contrastEndDate >= '").append(
					queryVO.getBeginDate()).append("' ");
			sqlBuffer.append(" and " + cxAlias + ".cxrq >= '").append(queryVO.getBeginDate()).append("' ");
		}

		if (queryVO.getEndDate() != null) { // ��ѯ��ֹ����
			sqlBuffer.append(" and " + cxAlias + ".cxrq <= '").append(queryVO.getEndDate()).append("' ");
		}

		sqlBuffer.append(getQueryObjSql().replace("zb.", jkzbAlias + ".")); // ��ѯ����
		sqlBuffer.append(getBillStatusSQL(queryVO, false, false)); // ����״̬
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), false)); // ����
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(cxAlias + ".pk_org", queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(" and ").append(jkzbAlias + ".pk_group = '").append(queryVO.getPk_group()).append("' ");
		sqlBuffer.append(" and ").append(jkzbAlias).append(".dr = 0");
		sqlBuffer.append(" and ").append(cxAlias).append(".dr = 0");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(jkzbAlias + "." + PK_GROUP + ", " + cxAlias + "." + PK_ORG);
		sqlBuffer.append(", ").append(groupByBaseBal.replace("zb.", jkzbAlias + "."));
		sqlBuffer.append(", " + cxAlias + ".cxrq");
		sqlBuffer.append(", " + jkzbAlias + ".zy");
		sqlBuffer.append(", bxzb.djlxbm");
		sqlBuffer.append(", bxzb.djbh");
		sqlBuffer.append(", bxzb.pk_jkbx");
		sqlBuffer.append(", " + jkzbAlias + ".kjqj");
		sqlBuffer.append(", " + cxAlias + ".pk_jkd ");
		sqlBuffer.append(", " + jkzbAlias + ".bzbm");

		return sqlBuffer.toString();
	}

	/**
	 * �õ���ѯ���󹹳ɵ�SQL
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
	 * ������Ҫ����С�ƺϼƵĶ���
	 *
	 * @return
	 */
	private List<ComputeTotal> getAllQryObj() {
		// ������Ҫ����С�ƺϼƵĶ���
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
			allQryobjList.add(total);

			total = new ComputeTotal();
			total.field = "djrq";
			total.isDimension = isShowDateTotal;
			allQryobjList.add(total);
		}

		return allQryobjList;
	}

	/**
	 * ��ȡ��ʱ����<br>
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
	 * ��ȡ��ʱ����<br>
	 * @return String[]<br>
	 */
	private String[] getTmpTblColNames() {
		if (tmpTblColNames == null) {
			// ��ѯ�������
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
	 * ��ȡ��ʱ��������<br>
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
			tmpTblColTypes[i++] = Types.INTEGER; // rn��

			for (; i < tmpTblColTypes.length - 4 + 1; i += 4) {
				tmpTblColTypes[i] = Types.DECIMAL;
				tmpTblColTypes[i + 1] = Types.DECIMAL;
				tmpTblColTypes[i + 2] = Types.DECIMAL;
				tmpTblColTypes[i + 3] = Types.DECIMAL;
			}
		}

		return tmpTblColTypes;
	}

	/**
	 * 
	 * @param tmpTbl
	 * @param qryObjs
	 * @param alias
	 * @param numCols
	 * @deprecated
	 * @return
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String getOnePeriodEndBal(String tmpTbl, String[] qryObjs, String[] alias,
			String[] numCols) {
		StringBuffer sqlBuffer = new StringBuffer("(select ((select ");
		sqlBuffer.append("sum(").append(alias[0]).append(".").append(numCols[0])
				.append(" - ").append(alias[0]).append(".").append(numCols[1]).append(") from ")
				.append(tmpTbl).append(" ").append(alias[0]);

		sqlBuffer.append(" where ");

		for (String qryObj : qryObjs) {
			sqlBuffer.append(alias[0]).append(".").append(qryObj);
			sqlBuffer.append(" = ").append(tmpTbl).append(".").append(qryObj).append(" and ");
		}

		sqlBuffer.append("coalesce(").append(alias[0]).append(".pk_currtype, '~') = ")
				.append("coalesce(").append(tmpTbl).append(".pk_currtype, '~') and ");

		sqlBuffer.append(alias[0]).append(".pk_group = ").append(tmpTbl).append(".pk_group and ");
		sqlBuffer.append(alias[0]).append(".pk_org = ").append(tmpTbl).append(".pk_org and ");
		sqlBuffer.append(alias[0]).append(".rn <= ").append(tmpTbl).append(".rn) ");

		sqlBuffer.append(" + ");

		sqlBuffer.append(" (select ").append(alias[1]).append(".").append(numCols[2]).append(
				" from ").append(tmpTbl).append(" ").append(alias[1]);

		sqlBuffer.append(" where ");

		for (String qryObj : qryObjs) {
			sqlBuffer.append(alias[1]).append(".").append(qryObj);
			sqlBuffer.append(" = ").append(tmpTbl).append(".").append(qryObj).append(" and ");
		}
		sqlBuffer.append("coalesce(").append(alias[1]).append(".pk_currtype, '~') = ")
				.append("coalesce(").append(tmpTbl).append(".pk_currtype, '~') and ");
		sqlBuffer.append(alias[1]).append(".pk_group = ").append(tmpTbl).append(".pk_group and ");
		sqlBuffer.append(alias[1]).append(".pk_org = ").append(tmpTbl).append(".pk_org and ");
		sqlBuffer.append(alias[1]).append(".rn = 0)) ");

		sqlBuffer.append(getFromDummyTable());
		sqlBuffer.append(") ");

		return sqlBuffer.toString();
	}

	/**
	 * ����С�ƺϼ�
	 *
	 * @return
	 * @deprecated
	 * @throws SQLException
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String getComputeTotalSql() throws SQLException {

		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(queryObjOrderExt);
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0047")/*@res ", substring(djrq, 1, 10) + ' С��' djrq, "*/);
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0048")/*@res ", substr(djrq, 1, 10) || ' С��' djrq, "*/);
			break;
		default:
			sqlBuffer.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0049")/*@res "��֧�ֵ����ݿ�����"*/);
			break;
		}

		sqlBuffer.append("null zy, null pk_currtype, null pk_jkbx, null pk_org, null pk_group, null pk_billtype, null billno, null kjqj, null qzzt, ");

		sqlBuffer.append("sum(jk_ori) jk_ori, sum(jk_loc) jk_loc, sum(gr_jk_loc) gr_jk_loc, sum(gl_jk_loc) gl_jk_loc, ");
		sqlBuffer.append("sum(hk_ori) hk_ori, sum(hk_ori) hk_ori, sum(hk_loc) hk_loc, sum(gr_hk_loc) gr_hk_loc, ");
		sqlBuffer.append("0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc, ");

		String[] qryObjs = getQueryObjs();
		for (int i = 0; i < qryObjs.length; i++) {
			sqlBuffer.append("grouping(").append(qryObjs[i]).append(") + ");
		}
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append("grouping(substring(djrq, 1, 10)) + ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append("grouping(substr(djrq, 1, 10)) + ");
			break;
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" rn ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" where rn >= 0 ");

		sqlBuffer.append(" group by ");
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append(queryObjOrderExt);
			sqlBuffer.append(", substring(djrq, 1, 10) ");
			sqlBuffer.append(" with cube ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append("cube(");
			sqlBuffer.append(queryObjOrderExt);
			sqlBuffer.append(", substr(djrq, 1, 10)) ");
			break;
		default:
			break;
		}

		sqlBuffer.append(" having ");
		for (int i = 0; i < qryObjs.length - 1; i++) {
			sqlBuffer.append("grouping(").append(qryObjs[i]).append(") <= grouping(").append(
					qryObjs[i + 1]).append(") and ");
		}
		sqlBuffer.append("grouping(").append(qryObjs[qryObjs.length - 1]);
		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.SQLSERVER:
			sqlBuffer.append(") <= grouping(substring(djrq, 1, 10)) ");
			break;
		case DBConsts.DB2:
		case DBConsts.ORACLE:
			sqlBuffer.append(") <= grouping(substr(djrq, 1, 10)) ");
			break;
		default:
			break;
		}

		return sqlBuffer.toString();
	}

	/**
	 * 
	 * @param tmpTbl
	 * @param qryObjCond
	 * @param nums
	 * @param alias
	 * @deprecated
	 * @return
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String getOneDateSubTotalPeriodEndBal(String tmpTbl,
			String qryObjCond, String[] nums, String[] alias) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(tmpTbl).append(".").append(nums[2]).append(" = (select ((select sum(").append(alias[0]).append(".")
				.append(nums[2]).append(") from ").append(tmpTbl).append(" ").append(alias[0]).append(" where ").append(
						qryObjCond.replace("@reptbl", alias[0])).append(alias[0]).append(".rn = 0) ");

		sqlBuffer.append(" + ");

		sqlBuffer.append(" coalesce((select sum(").append(alias[1]).append(".").append(nums[0])
				.append(" - ").append(alias[1]).append(".").append(nums[1]).append(") from ")
				.append(tmpTbl).append(" ").append(alias[1]).append(" where ").append(qryObjCond.replace("@reptbl", alias[1]))
				.append(alias[1]).append(".djrq < ").append(tmpTbl).append(".djrq and ")
				.append(alias[1]).append(".rn < ").append(SmartProcessor.MAX_ROW).append("), 0)) ");

		sqlBuffer.append(getFromDummyTable());
		sqlBuffer.append(") ");

		return sqlBuffer.toString();
	}

	/**
	 * �����ѯ����С����ĩ���
	 * 
	 * @return
	 * @deprecated
	 * @throws SQLException
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private List<String> getUpdateSubTotalSql() throws SQLException {
		List<String> subTotalSqlList = new ArrayList<String>();

		String tmpTbl = getTmpTblName();

		String[][] nums = {
				{ "jk_ori", "hk_ori", "bal_ori" },
				{ "jk_loc", "hk_loc", "bal_loc" },
				{ "gr_jk_loc", "gr_hk_loc", "gr_bal_loc" },
				{ "gl_jk_loc", "gl_hk_loc", "gl_bal_loc" } };

		String[][] alias = { { "a", "b" }, { "c", "d" }, { "e", "f" }, { "g", "h" } };

		String[] qryObjs = getQueryObjs();

		// �����ѯ�����С��
		for (int i = 0; i < qryObjs.length; i++) {

			StringBuffer qryObjCondBuf = new StringBuffer();
			for (int j = 0; j <= i; j++) {
				qryObjCondBuf.append(tmpTbl).append(".").append(qryObjs[j]).append(" = ").append(
						"@reptbl.").append(qryObjs[j]).append(" and ");
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
					+ ".rn > " + SmartProcessor.MAX_ROW + " and rn != (select max(rn) from "
					+ tmpTbl + ") ");
		}

		return subTotalSqlList;
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
	 * �����ܼ���ĩ���
	 *
	 * @return
	 * @deprecated
	 * @throws SQLException
	 */
	@Deprecated
	@SuppressWarnings("unused")
	private String getUpdateTotalSql() throws SQLException {
		String tmpTbl = getTmpTblName();

		String totalLine = " rn = (select max(rn) from " + tmpTbl + ")";

		StringBuffer sqlBuffer = new StringBuffer(" update ");
		sqlBuffer.append(tmpTbl);

		sqlBuffer.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0050")/*@res " set zy = '�ϼ�', "*/);

		switch (SqlBuilder.getDatabaseType()) {
		case DBConsts.ORACLE:
		case DBConsts.DB2:
			sqlBuffer.append("qryobj0pk = (select 'A' || max(qryobj0pk) ");
			break;

		case DBConsts.SQLSERVER:
			sqlBuffer.append("qryobj0pk = (select 'A' + max(qryobj0pk) ");
			break;
		default:
			break;
		}

		sqlBuffer.append(" from ").append(tmpTbl).append("), ");


		sqlBuffer.append("bal_ori = (select ((select sum(bal_ori) from ").append(tmpTbl).append(
				" where rn = 0) + (select ").append("(jk_ori - hk_ori) from ")
				.append(tmpTbl).append(" where ").append(totalLine).append(")) ").append(
						getFromDummyTable()).append("), ");

		sqlBuffer.append("bal_loc = (select ((select sum(bal_loc) from ").append(tmpTbl).append(
				" where rn = 0) + (select ").append("(jk_loc - hk_loc) from ")
				.append(tmpTbl).append(" where ").append(totalLine).append(")) ").append(
						getFromDummyTable()).append("), ");

		sqlBuffer.append("gr_bal_loc = (select ((select sum(gr_bal_loc) from ").append(tmpTbl)
				.append(" where rn = 0) + (select ").append(
						"(gr_jk_loc - gr_hk_loc) from ").append(tmpTbl).append(" where ")
				.append(totalLine).append(")) ").append(getFromDummyTable()).append("), ");

		sqlBuffer.append("gl_bal_loc = (select ((select sum(gl_bal_loc) from ").append(tmpTbl)
				.append(" where rn = 0) + (select ").append(
						"(gl_jk_loc - gl_hk_loc) from ").append(tmpTbl).append(" where ")
				.append(totalLine).append(")) ");
		sqlBuffer.append(getFromDummyTable()).append(") ");

		sqlBuffer.append(" where ").append(totalLine);

		return sqlBuffer.toString();
	}

	/**
	 * �����ڳ�(��ĩ)���
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
		sqlBuffer.append("null djrq, '").append(IErmReportConstants.CONST_BRIEF).append("' zy, "); // �ڳ�
		sqlBuffer.append(" null pk_billtype, null pk_jkbx, null djbh, null kjqj, null qzzt, ");
		sqlBuffer.append("null ").append(PK_CONTRASTJK).append(", ");
		sqlBuffer.append("0 rn, ");
		sqlBuffer.append(" 0.0 jk_ori, 0.0 jk_loc, 0.0 gr_jk_loc, 0.0 gl_jk_loc, ");
		sqlBuffer.append(" 0.0 hk_ori, 0.0 hk_loc, 0.0 gr_hk_loc, 0.0 gl_hk_loc, ");
		sqlBuffer.append(" 0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc ");

		sqlBuffer.append(" from ").append(getTmpTblName()).append(" a ");

		sqlBuffer.append(" where coalesce(a.zy, '~') != '").append(IErmReportConstants.CONST_BRIEF).append("' ");
		sqlBuffer.append(" and not exists ");
		sqlBuffer.append("(select null from ").append(getTmpTblName()).append(" b ");
		sqlBuffer.append(" where b.zy = '").append(IErmReportConstants.CONST_BRIEF).append("' "); // �ڳ�
		sqlBuffer.append(" and a.pk_group = b.pk_group and a.pk_org = b.pk_org ");
		String[] qryObjs = getQueryObjs();
		for (String qryObj : qryObjs) {
			sqlBuffer.append(" and a.").append(qryObj).append(" = b.").append(qryObj);
		}
		if (beForeignCurrency) {
			sqlBuffer.append(" and coalesce(a.pk_currtype, '~') = coalesce(b.pk_currtype, '~')");
		}
		sqlBuffer.append(") ");

		return sqlBuffer.toString();
	}
	/**
	 * ������С��
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getDateSubTotalSql() throws SQLException {
		if (!isShowDateTotal) {
			// ����ʾ��С��
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
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" rn, "); // ��С��rn = SmartProcessor.MAX_ROW
		sqlBuffer.append(" sum(jk_ori) jk_ori, sum(jk_loc) jk_loc, sum(gr_jk_loc) gr_jk_loc, sum(gl_jk_loc) gl_jk_loc, ");
		sqlBuffer.append(" sum(hk_ori) hk_ori, sum(hk_loc) hk_loc, sum(gr_hk_loc) gr_hk_loc, sum(gl_hk_loc) gl_hk_loc, ");
		sqlBuffer.append(" 0.0 bal_ori, 0.0 bal_loc, 0.0 gr_bal_loc, 0.0 gl_bal_loc ");

		sqlBuffer.append(" from ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" where ");
		sqlBuffer.append(" coalesce(djrq, '~') != '~' ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(computed.get(0));
		i = 1;
		for (; i < computed.size(); i++) {
			sqlBuffer.append(", ").append(computed.get(i));
		}

		return sqlBuffer.toString();
	}

	/**
	 * ����С�ƺϼ�<br>
	 *
	 * ˵�����ڳ���rn = 0����ϸ��rn = 1����һ���ϼ���rn = SmartProcessor.MAX_ROW���Ժ��������ơ�
	 * ��С����ĩ�����nc.impl.arap.report.DetailBOImpl�м��㣬������ĩ�������ݿ����㡣
	 * Ϊ��ʵ������������SmartProcessor.MAX_PK������������ԡ�
	 *
	 * @return
	 * @throws SQLException
	 */
	private String getSubTotalSql() throws SQLException {
		List<ComputeTotal> allQryobjs = getAllQryObj();
		List<String> computed = new ArrayList<String>();

		// ��ʽƴдSQL
		StringBuffer sqlBuffer = new StringBuffer(" insert into ");
		sqlBuffer.append(getTmpTblName());

		sqlBuffer.append(" select ");
		int i = 0;
		for (; i < allQryobjs.size() - 1; i++) {
			if (allQryobjs.get(i).isDimension) {
				sqlBuffer.append(allQryobjs.get(i).field).append(", ");
				computed.add(allQryobjs.get(i).field);
			} else {
				sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", ");
			}
		}
		sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", "); // ��С���Ѿ�����

		sqlBuffer.append("null zy, null pk_billtype, null pk_jkbx, null djbh, null kjqj, null qzzt, ");
		sqlBuffer.append("null ").append(PK_CONTRASTJK).append(", ");
		i = 0;
		for (; i < computed.size(); i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" + 1 rn, "); // ��С��ռ����rn = SmartProcessor.MAX_ROW
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
		sqlBuffer.append("grouping(").append(computed.get(0)).append(") = 0 "); // ���Ų������ܼ�
		if (queryVO.getPk_orgs().length <= 1) {
			// ��ҵ��Ԫ��ѯ�ż����ܼ�
			sqlBuffer.append(" and grouping(").append(allQryobjs.get(1).field).append(") = 0 ");
		}

		return sqlBuffer.toString();
	}

	/**
	 * �����ѯ����С����ĩ���
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
			// Ϊ������ܣ���С�Ƶ���ĩ�������������
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
