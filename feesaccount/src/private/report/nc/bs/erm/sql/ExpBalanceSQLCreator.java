package nc.bs.erm.sql;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nc.bs.er.util.SqlUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.pub.smart.smartprovider.ExpBalanceDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pm.util.ArrayUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

/**
 * <p>
 *      ����������û��ܱ����漰��sql
 *      ��Է��û��ܱ������������֣�  
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 * @liansg
 * @see 
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-7 ����08:27:29
 */
public class ExpBalanceSQLCreator extends ErmCSBaseSqlCreator {

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
        sqlBuffer.append(", org_orgs.code code_org, isnull(org_orgs.name").append(getMultiLangIndex()).append(", org_orgs.name) org"); // code_org, org

        String[] qryObjs = getQueryObjs();
        List<QryObj> qryObjList = queryVO.getQryObjs();

        for (int i = 0; i < qryObjList.size(); i++) {
            sqlBuffer.append(", v.").append(qryObjs[i]).append(", ");

            sqlBuffer.append(bdTable ).append(i).append(".").append(qryObjList.get(i).getBd_codeField()).append(" ")
                    .append(IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code, ");

            sqlBuffer.append("isnull(").append(bdTable ).append( i).append(".").append(
                    qryObjList.get(i).getBd_nameField()).append(getMultiLangIndex()).append(", ").append(
                    bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_nameField()).append(") ").append(
                    IPubReportConstants.QRY_OBJ_PREFIX).append(i);

        }

        sqlBuffer.append(", v.pk_currtype"); 
        sqlBuffer.append(", v.rn");
        sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

        sqlBuffer.append(", sum(").append("v." ).append(ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI).append(") ").append(ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI);
        sqlBuffer.append(", sum(").append("v." ).append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " ).append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", sum(").append("v." ).append(ExpBalanceDataProvider.PREFIX_GR).append(ExpenseBalVO.GROUP_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") ").append(ExpBalanceDataProvider.PREFIX_GR).append(
                ExpenseBalVO.GROUP_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", sum(").append("v." ).append(ExpBalanceDataProvider.PREFIX_GL).append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " ).append(ExpBalanceDataProvider.PREFIX_GL).append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);     

        sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);

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
        sqlBuffer.append(", v.").append(PK_CURR);
        sqlBuffer.append(", rn ");

        sqlBuffer.append(" order by ");
        sqlBuffer.append(ErmReportSqlUtils.caseWhenSql("org_orgs.code"));
        for (int i = 0; i < qryObjList.size(); i++) {
            sqlBuffer.append(",");
//            sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(IPubReportConstants.QRY_OBJ_PREFIX+i+"pk"));
            sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(bdTable +  i + "." + qryObjList.get(i).getBd_codeField()));
        }
//      if (beForeignCurrency) {
//            sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.pk_currtype"));
//      }
        sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.rn"));

        return sqlBuffer.toString();
    }

    /**
     * ����������ѯ�����ۼƷ���(������)<br>
     * @return String<br>
     * @throws BusinessException<br>
     * @throws SQLException<br>
     * @throws BusinessException 
     */
    private String getExpenseAccumulativeOccur() throws SQLException, BusinessException {
        String bxzbAlias = "zb";
        StringBuffer sqlBuffer = new StringBuffer(" insert into ");
        sqlBuffer.append(getTmpTblName());

        sqlBuffer.append(" select ");

        if(ExpenseBalVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
            sqlBuffer.append(bxzbAlias).append(".pk_group,").append(bxzbAlias).append(".bx_fiorg pk_org");
        } else {
            sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, bxzbAlias));
        }
        sqlBuffer.append(", ").append(queryObjBaseExp);
        sqlBuffer.append(", ").append(bxzbAlias + ".pk_currtype").append(" pk_currtype, 0 rn");
        
        sqlBuffer.append(", sum(").append("zb." ).append(ExpenseBalVO.ASSUME_AMOUNT).append(") " ).append(
                ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI);
        
        sqlBuffer.append(", sum(").append("zb." ).append(ExpenseBalVO.ORG_AMOUNT).append(
                ") " ).append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);
        
        sqlBuffer.append(", sum(").append("zb." ).append(ExpenseBalVO.GROUP_AMOUNT).append(") " ).append(
                ExpBalanceDataProvider.PREFIX_GR).append(ExpenseBalVO.GROUP_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);
        
        sqlBuffer.append(", sum(").append("zb." ).append(ExpenseBalVO.GLOBAL_AMOUNT).append(
                ") " ).append(
                ExpBalanceDataProvider.PREFIX_GL).append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);

        sqlBuffer.append(" from er_expensebal ").append(bxzbAlias);

        // ���ò�ѯ�����̶�ֵ
        sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
        String cwSql = getCompositeWhereSql(bxzbAlias,"cs");
        if (!StringUtils.isEmpty(cwSql)) {
            sqlBuffer.append(cwSql);
        }
        
        if (queryVO.getBeginDate() != null) { 
            //�������ڲ����ڼ�
            sqlBuffer.append(" and " ).append( "zb." ).append(ExpenseBalVO.BILLDATE).append(
            " >= '").append(queryVO.getBeginDate()).append("' ");
        }
        
        if (queryVO.getEndDate() != null) { // ��ѯ��������
            sqlBuffer.append(" and " ).append( "zb." ).append(ExpenseBalVO.BILLDATE).append(
            " <= '").append(queryVO.getEndDate()).append("' ");
        }
        

        sqlBuffer.append(getQueryObjSql()); // ��ѯ����
        if(queryVO.getPk_currency()!=null){
            String[] pkCurrTypes = queryVO.getPk_currency().split(",");
            String sqlCurrType = SqlUtil.buildInSql("zb." + ExpenseBalVO.PK_CURRTYPE, pkCurrTypes);
            sqlBuffer.append(" and ").append(sqlCurrType).append(" ");
//            sqlBuffer.append(" and zb."); 
//            sqlBuffer.append(ExpenseBalVO.PK_CURRTYPE); 
//            sqlBuffer.append(" = '"); 
//            sqlBuffer.append(queryVO.getPk_currency()); 
//            sqlBuffer.append("' ");  // ����
        }
//      sqlBuffer.append(ErmReportSqlUtils.getCurrencySql(queryVO.getPk_currency(), false)); // ����
        
        sqlBuffer
                .append(" and zb.src_billtype <> '2647' and zb.src_tradetype <> '2647' ");
        if(ExpenseBalVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
            //����Ǳ����ˣ�ֻ����������ķ���������
            sqlBuffer
                    .append(" and zb.")
                    .append(ExpenseBalVO.SRC_TRADETYPE)
                    .append(" in ")
                    .append(" (select  pk_billtypecode from bd_billtype where  parentbilltype ='264X' and istransaction = 'Y' ");
            sqlBuffer.append(" and islock ='N'  and  pk_group='" ).append(queryVO.getPk_group()).append(
                    "' )");
            sqlBuffer.append(" and ").append(SqlUtils.getInStr(bxzbAlias + "." + "bx_fiorg", queryVO.getPk_orgs())); // ҵ��Ԫ
        }else {
            sqlBuffer.append(" and (zb." ).append( ExpenseBalVO.ISWRITEOFF ).append("<> 'Y' or zb.iswriteoff is null)");
            sqlBuffer.append(" and ").append(SqlUtils.getInStr(bxzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // ҵ��Ԫ
        }
        
        String[] codes = (String[]) queryVO.getUserObject().get(
                "src_tradetype");
        if (!ArrayUtil.isEmpty(codes)) {
            String sqlCode = SqlUtil.buildInSql("zb.src_tradetype", codes);
            sqlBuffer.append(" and ").append(sqlCode);
        }
        
        //����״̬
        int billstatus = BXStatusConst.DJZT_Saved; 
        if(IPubReportConstants.BILL_STATUS_SAVE.equals(queryVO.getBillState())) {
            billstatus =BXStatusConst.DJZT_Saved;
        } else if(IPubReportConstants.BILL_STATUS_CONFIRM.equals(queryVO.getBillState())){
            billstatus =BXStatusConst.DJZT_Verified;
        } else if(IPubReportConstants.BILL_STATUS_EFFECT.equals(queryVO.getBillState())){
            billstatus =BXStatusConst.DJZT_Sign;
        }
        sqlBuffer.append(" and zb." ).append( ExpenseBalVO.BILLSTATUS ).append(">=").append(billstatus);
//      sqlBuffer.append(" and zb.").append( ExpenseBalVO.BILLSTATUS).append(ReportSqlUtils.getBillStatusExpression(queryVO));
        
//      sqlBuffer.append(" and ").append(SqlUtils.getInStr(bxzbAlias + "." + PK_ORG, queryVO.getPk_orgs())); // ҵ��Ԫ
        sqlBuffer.append(" and " ).append( bxzbAlias ).append( "." ).append( PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");
        sqlBuffer.append(" and ").append(bxzbAlias ).append( ".dr = 0 ");
        sqlBuffer.append(" and ").append(bxzbAlias ).append( "." ).append(ExpenseBalVO.ASSUME_AMOUNT).append(
                " <> 0 ");

        sqlBuffer.append(" group by ");
        if(ExpenseBalVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
            sqlBuffer.append(bxzbAlias).append(".pk_group,").append(bxzbAlias).append(".bx_fiorg ");
        } else {
            sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, bxzbAlias));
        }
        sqlBuffer.append(", ").append(groupByBaseExp);
//        if (beForeignCurrency) {
            sqlBuffer.append(",").append(bxzbAlias).append(".pk_currtype");
//        }

        return sqlBuffer.toString();
    }

    private String getComputeTotalSql() throws SQLException {
        // ����С�ơ��ϼƶ���
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

        sqlBuffer.append(" sum(").append(ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI).append(") " );
        sqlBuffer.append(", sum(").append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " );
        sqlBuffer.append(", sum(").append(ExpBalanceDataProvider.PREFIX_GR).append(ExpenseBalVO.GROUP_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " );
        sqlBuffer.append(", sum(").append(ExpBalanceDataProvider.PREFIX_GL).append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") ");

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
        sqlBuffer.append("grouping(").append(computed.get(0)).append(") = 0 "); // ���Ų������ܼ�
        if (queryVO.getPk_orgs().length <= 1) {
            // ��ҵ��Ԫ��ѯ�ż����ܼ�
            sqlBuffer.append(" and grouping(").append(allQryObjs.get(1).field).append(") = 0 ");
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
     * ��ȡ��ʱ����<br>
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
            otherColNameBuf.append("rn, ");
            
            otherColNameBuf.append(ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI).append(
                    ", " ).append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(
                    ", " ).append(ExpBalanceDataProvider.PREFIX_GR).append(ExpenseBalVO.GROUP_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(
                    ", " ).append(ExpBalanceDataProvider.PREFIX_GL).append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC);
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
