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
 * �������������ϸ�˱����漰��sql ��Է�����ϸ�ˣ������������֣�
 * </p>
 * 
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li> <br>
 * <br>
 * 
 * @see
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����08:27:29
 */
public class ExpenseDetailSQLCreator extends ErmBaseSqlCreator {
	
	// �̶���ѯ�ֶ�
	private static String detailFields = "@Table.djrq, @Table.zy,  @Table.pk_billtype, @Table.pk_jkbx,  @Table.djbh, @Table.kjqj ";

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;
	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getExpenseDetail()); // ���������ϸ
		sqlList.add(getSubTotalSql()); // ����С�ƺϼ�

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
		sqlBuffer.append(", (case when isnull(v.djrq, '~') = '~' then 1 else 0 end) as is_djrq_null"); // is_tallydate_null
		sqlBuffer.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", v.rn, (case when v.rn = 0 then 0 else 1 end) as is_begin"); // is_begin
		sqlBuffer.append(", v.exp_ori, v.exp_loc, v.gr_exp_loc, v.gl_exp_loc");
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
			sqlBuffer.append(", isnull").append(i).append(", ").append(
					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
		}
		if (beForeignCurrency) {
			sqlBuffer.append(", is_currtype_null, pk_currtype");
		}
		sqlBuffer.append(", is_begin, is_djrq_null, djrq, rn");

		return sqlBuffer.toString();
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}

	/**
	 * new ����������ѯ������ϸ��¼<br>
	 * 
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException
	 */
	private String getExpenseDetail() throws SQLException, BusinessException{
		String bxzbAlias = getAlias("er_bxzb");
		String finitemAlias = getAlias("er_busitem");

		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());

		sqlBuffer.append(" select ");
		sqlBuffer.append(bxzbAlias + "." + PK_GROUP + ", " + bxzbAlias + "." + PK_ORG);
		sqlBuffer.append(", " + queryObjBaseExp);
		// sqlBuffer.append(", ").append(beForeignCurrency ? (bxzbAlias + ".bzbm") : "null").append(" pk_currtype");
		sqlBuffer.append(", " + bxzbAlias + ".bzbm ").append(PK_CURR);
		sqlBuffer.append(", " + bxzbAlias + ".djrq djrq");
		sqlBuffer.append(", " + bxzbAlias + ".zy zy");
		sqlBuffer.append(", " + bxzbAlias + ".djlxbm pk_billtype");
		sqlBuffer.append(", " + bxzbAlias + ".pk_jkbx pk_jkbx");
		sqlBuffer.append(", " + bxzbAlias + ".djbh djbh");
		sqlBuffer.append(", " + bxzbAlias + ".kjqj kjqj");
		sqlBuffer.append(", 1 rn");
		// ��ѯ�������ý��
		sqlBuffer.append(", sum(").append(finitemAlias).append(".ybje) exp_ori");
		sqlBuffer.append(", sum(").append(finitemAlias).append(".bbje) exp_loc");
		sqlBuffer.append(", sum(").append(finitemAlias + ".groupbbje) gr_exp_loc");
		sqlBuffer.append(", sum(").append(finitemAlias + ".globalbbje) gl_exp_loc");

		sqlBuffer.append(" from er_bxzb " + bxzbAlias);
		sqlBuffer.append(" inner join er_busitem ").append(finitemAlias).append(" on ");
		sqlBuffer.append(bxzbAlias).append(".pk_jkbx = ").append(finitemAlias).append(".pk_jkbx ");

		// ���ò�ѯ�����̶�ֵ
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		if (!StringUtils.isEmpty(getCompositeWhereSql(bxzbAlias))) {
			sqlBuffer.append(getCompositeWhereSql(bxzbAlias).replace("zb." + BXBusItemVO.SZXMID,
					finitemAlias + "." + BXBusItemVO.SZXMID));
		}

		if (queryVO.getBeginDate() != null) { // ��ѯ��ʼ����
			sqlBuffer.append(" and " + bxzbAlias + ".djrq >= '").append(queryVO.getBeginDate().toString()).append("' ");
		}
		if (queryVO.getEndDate() != null) { // ��ѯ��������
			sqlBuffer.append(" and " + bxzbAlias + ".djrq <= '").append(queryVO.getEndDate().toString()).append("' ");
		}

		sqlBuffer.append(getQueryObjSql(bxzbAlias, finitemAlias)); // ��ѯ����
		sqlBuffer.append(getBillStatusSQL(queryVO, false, false)); // ����״̬
		sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), false)); // ����
		sqlBuffer.append(" and ").append(SqlUtils.getInStr(bxzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // ҵ��Ԫ
		sqlBuffer.append(" and " + bxzbAlias + "." + PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");
		sqlBuffer.append(" and ").append(bxzbAlias).append(".dr = 0 ");
		sqlBuffer.append(" and ").append(finitemAlias).append(".dr = 0 ");
		sqlBuffer.append(" and ").append(bxzbAlias).append(".ybje != 0 ");
		sqlBuffer.append(" and ").append(finitemAlias).append(".ybje != 0 ");

		sqlBuffer.append(" group by ");
		sqlBuffer.append(groupByBaseExp);
		sqlBuffer.append(", ").append(bxzbAlias + ".djrq");
		sqlBuffer.append(", ").append(bxzbAlias + ".pk_group");
		sqlBuffer.append(", ").append(bxzbAlias + ".pk_org");
		sqlBuffer.append(", ").append(bxzbAlias + ".zy");
		sqlBuffer.append(", ").append(bxzbAlias + ".djlxbm");
		sqlBuffer.append(", ").append(bxzbAlias + ".djbh");
		sqlBuffer.append(", ").append(bxzbAlias + ".kjqj");
		sqlBuffer.append(", ").append(bxzbAlias + ".pk_jkbx");
		sqlBuffer.append(", ").append(bxzbAlias + ".bzbm");

		return sqlBuffer.toString();
	}

	/**
	 * �õ���ѯ���󹹳ɵ�SQL
	 * 
	 * @return String
	 * @throws BusinessException
	 */
	private String getQueryObjSql(String zbAlias, String fbAlias) throws BusinessException {
		List<QryObj> qryObjList = queryVO.getQryObjs();
		StringBuffer sqlBuffer = new StringBuffer(" ");
		for (QryObj qryObj : qryObjList) {
			String sql = qryObj.getSql();
			if (!StringUtils.isEmpty(sql)) {
				sqlBuffer.append(" and ").append(
						sql.replace("zb." + BXBusItemVO.SZXMID, fbAlias + "." + BXBusItemVO.SZXMID)
								.replace("zb.", zbAlias + "."));
			}
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
			total.isDimension = false;
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
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_expdetail" + qryObjLen,
					getTmpTblColNames(), getTmpTblColTypes());
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
			otherColNameBuf.append(", ").append(PK_CURR);

			otherColNameBuf.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", rn, ");
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
	 * ��ȡ��ʱ��������<br>
	 * @return Integer[]<br>
	 */
	private Integer[] getTmpTblColTypes() {
		if (tmpTblColTypes == null || tmpTblColTypes.length == 0) {
			tmpTblColTypes = new Integer[getTmpTblColNames().length];
			int i = 0;
			for (; i < tmpTblColTypes.length - 4-1; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}

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
		sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", ");
		
		sqlBuffer.append("null djrq, null zy, null pk_billtype, null pk_jkbx, null djbh, null kjqj, ");
		i = 0;
		for (; i < computed.size(); i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" + 1 rn, ");
		sqlBuffer.append(" sum(exp_ori) exp_ori, sum(exp_loc) exp_loc, sum(gr_exp_loc) gr_exp_loc, sum(gl_exp_loc) gl_exp_loc ");

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

}

// /:~
