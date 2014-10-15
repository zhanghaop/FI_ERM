package nc.bs.erm.sql;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nc.bs.er.util.SqlUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.itf.erm.report.IErmReportConstants;
import nc.itf.fipub.report.IPubReportConstants;
import nc.jdbc.framework.util.DBConsts;
import nc.pub.smart.smartprovider.MatterappDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * 用来构造费用申请查询所涉及的sql 针对费用申请查询，包括几个部分：
 * </p>
 * 
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li> <br>
 * <br>
 * 
 * @see
 * @version V6.0
 * @since V6.0 创建时间：2010-12-7 下午08:27:29
 */
public class MatterappSQLCreator extends ErmCSBaseSqlCreator {
	
	// 固定查询字段
	private static String detailFields = "@Table.pk_mtapp_bill,@Table.billdate,  @Table.pk_billtype,@Table.billno, @Table.close_status ";
	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;
	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
//		sqlList.add(getMatterappDetail()); // 计算费用明细
		sqlList.add(getMatterappDetailByZb());//计算费用明细
		sqlList.add(getSubTotalSql()); // 计算小计合计

		return sqlList.toArray(new String[0]);
	}
	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", org_orgs.code code_org, isnull(org_orgs.name").append(getMultiLangIndex()).append(", org_orgs.name) org"); 

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
            sqlBuffer.append(" , v.pk_currtype "); 
        } else {
            sqlBuffer.append(" , null pk_currtype "); 
        }
		sqlBuffer.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
//		sqlBuffer.append(" , v.reason "); 
        //执行数 
		sqlBuffer.append(", v." ).append(MtAppDetailVO.EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(
		", v." ).append(MtAppDetailVO.ORG_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
		", v." ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
		", v." ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
		//金额
		sqlBuffer.append(", v." ).append(MtAppDetailVO.ORIG_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(
				", v." ).append(MtAppDetailVO.ORG_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				", v." ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				", v." ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
		//余额
		sqlBuffer.append(", v." ).append(MtAppDetailVO.REST_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(
				", v." ).append(MtAppDetailVO.ORG_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				", v." ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				", v." ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);
        sqlBuffer.append(", v.rn, v.reason ");
		
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
//			sqlBuffer.append(", ").append(
//					IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code");
//		}
//		if (beForeignCurrency) {
//			sqlBuffer.append(", pk_currtype");
//		}
//		sqlBuffer.append(" , " ).append(MtAppDetailVO.BILLDATE);
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
		sqlBuffer.append(", ").	append(ErmReportSqlUtils.caseWhenSql("v." + MtAppDetailVO.BILLDATE));

        sqlBuffer.append(", "). append(ErmReportSqlUtils.caseWhenSql("v.rn"));
		return sqlBuffer.toString();
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}
	
	/**
	 * new 报销管理：查询费用明细记录<br>
	 * 
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException
	 */
//	protected String getMatterappDetail() throws SQLException, BusinessException{
//        String origAmountSqlWhere = (String)queryVO.getUserObject().get("zb.orig_amount");
//        
//		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
//		
//		//主表的事由
//	    sqlBuffer.append(" select lb.*, rb.reason from (");
//		
//		if (StringUtils.isNotEmpty(origAmountSqlWhere)) {
//	        sqlBuffer.append(" select * from (");
//        }
//		
//		sqlBuffer.append(" select ");
//		sqlBuffer.append("zb." ).append( MtAppDetailVO.PK_GROUP ).append( ", " ).append( "zb." ).append( MtAppDetailVO.PK_ORG);
//		sqlBuffer.append(", " ).append( queryObjBaseExp);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_CURRTYPE);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_MTAPP_BILL);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLDATE);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_TRADETYPE).append(" pk_billtype");
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLNO);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.CLOSE_STATUS);
//		
//        sqlBuffer.append(", 0 rn " );
//        
//		 // 查询费用申请执行数
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.EXE_AMOUNT).append(") " ).append(
//				MtAppDetailVO.EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.ORG_EXE_AMOUNT).append(
//		") " ).append(
//				MtAppDetailVO.ORG_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GROUP_EXE_AMOUNT).append(
//		") " ).append(MatterappDataProvider.PREFIX_GR).append(
//				MtAppDetailVO.GROUP_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(
//		") " ).append(MatterappDataProvider.PREFIX_GL).append(
//				MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//	 // 查询费用申请金额
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.ORIG_AMOUNT).append(") " ).append(
//				MtAppDetailVO.ORIG_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.ORG_AMOUNT).append(
//		") " ).append(
//				MtAppDetailVO.ORG_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GROUP_AMOUNT).append(
//		") " ).append(MatterappDataProvider.PREFIX_GR).append(
//				MtAppDetailVO.GROUP_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GLOBAL_AMOUNT).append(
//		") " ).append(MatterappDataProvider.PREFIX_GL).append(
//				MtAppDetailVO.GLOBAL_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		// 查询费用申请余额
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.REST_AMOUNT).append(") " ).append(
//				MtAppDetailVO.REST_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
//		
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.ORG_REST_AMOUNT).append(
//				") " ).append(
//				MtAppDetailVO.ORG_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GROUP_REST_AMOUNT).append(
//				") " ).append(MatterappDataProvider.PREFIX_GR).append(
//				MtAppDetailVO.GROUP_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		sqlBuffer.append(", sum(").append("zb." ).append(MtAppDetailVO.GLOBAL_REST_AMOUNT).append(
//				") " ).append(MatterappDataProvider.PREFIX_GL).append(
//				MtAppDetailVO.GLOBAL_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
//		
//		sqlBuffer.append(" from er_mtapp_detail zb ");
//		// 设置查询条件固定值
//		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
//		sqlBuffer.append(getCompositeWhereSql("zb",MtAppDetailVO.getDefaultTableName()));
//		String[] codes = (String[])queryVO.getUserObject().get("zb.pk_tradetype");
//		if (!ArrayUtil.isEmpty(codes)) {
//		    String sqlCode = SqlUtil.buildInSql("zb.pk_tradetype", codes);
//		    sqlBuffer.append(" and ").append(sqlCode);
//		}
//		if (queryVO.getBeginDate() != null) { // 查询开始日期
//			sqlBuffer.append(" and " ).append( "zb." ).append(MtAppDetailVO.BILLDATE).append(
//					" >= '").append(queryVO.getBeginDate().toString()).append("' ");
//		}
//		
//		if (queryVO.getEndDate() != null) { // 查询结束日期
//			sqlBuffer.append(" and " ).append( "zb." ).append(MtAppDetailVO.BILLDATE).append(
//					" <= '").append(queryVO.getEndDate().toString()).append("' ");
//		}
//
//        // 单据状态
//        sqlBuffer.append(" and zb.").append(MtAppDetailVO.BILLSTATUS);
//        if (IPubReportConstants.BILL_STATUS_ALL.equals(queryVO.getBillState())) {
//            sqlBuffer.append(" >= ").append(ErmMatterAppConst.BILLSTATUS_SAVED);
//        } else if (IPubReportConstants.BILL_STATUS_SAVE.equals(queryVO
//                .getBillState())) {
//            sqlBuffer.append(" >= ").append(ErmMatterAppConst.BILLSTATUS_SAVED);
//        } else if (IErmReportConstants.BILL_STATUS_COMMIT.equals(queryVO
//                .getBillState())) {
//            sqlBuffer.append(" >= ").append(
//                    ErmMatterAppConst.BILLSTATUS_COMMITED);
//        } else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(queryVO
//                .getBillState())) {
//            sqlBuffer.append(" >= ").append(
//                    ErmMatterAppConst.BILLSTATUS_APPROVED);
//        }
//
//        sqlBuffer.append(getQueryObjSql()); // 查询对象
//        
//		if (queryVO.getPk_currency() !=null) {
//			sqlBuffer.append(" and zb." ).append( MtAppDetailVO.PK_CURRTYPE ).append(" ='").append( queryVO.getPk_currency() ).append( "' "); // 币种
//			
//		}
//		
//		sqlBuffer.append(" and ").append(SqlUtils.getInStr("zb." + PK_ORG, queryVO.getPk_orgs())); // 业务单元
//		sqlBuffer.append(" and " ).append( "zb." ).append( PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");
//		sqlBuffer.append(" group by ");
//		sqlBuffer.append("zb." ).append( MtAppDetailVO.PK_GROUP ).append( ", " ).append( "zb." ).append( MtAppDetailVO.PK_ORG);
//		sqlBuffer.append(", " ).append( groupByBaseExp);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_CURRTYPE);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLDATE);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_TRADETYPE);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLNO);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.CLOSE_STATUS);
//		sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_MTAPP_BILL);
//		
//		if (StringUtils.isNotEmpty(origAmountSqlWhere)) {
//		    origAmountSqlWhere = origAmountSqlWhere.replace("zb.orig_amount", "zbb.orig_amount_ori");
//            sqlBuffer.append(") zbb where ").append(origAmountSqlWhere);
//        }
//
//        //主表的事由
//        sqlBuffer.append(") lb left join er_mtapp_bill rb on lb.pk_mtapp_bill = rb.pk_mtapp_bill");
//		return sqlBuffer.toString();
//	}
	
	/**
     * new 报销管理：查询费用明细记录<br>
     * 
     * @return String<br>
     * @throws BusinessException<br>
     * @throws SQLException<br>
     * @throws BusinessException
     */
    private String getMatterappDetailByZb() throws SQLException, BusinessException{
        String origAmountSqlWhere = (String)queryVO.getUserObject().get("zb.orig_amount");
        
        StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());
        
        //主表的事由
//        sqlBuffer.append(" select lb.*, rb.reason from (");
        
//        if (StringUtils.isNotEmpty(origAmountSqlWhere)) {
//            sqlBuffer.append(" select * from (");
//        }
        
        sqlBuffer.append(" select ");
        sqlBuffer.append("zb." ).append( MtAppDetailVO.PK_GROUP ).append( ", " ).append( "zb." ).append( MtAppDetailVO.PK_ORG);
        sqlBuffer.append(", " ).append( queryObjBaseExp);

        if (beForeignCurrency) {
            sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_CURRTYPE);
        } else {
            sqlBuffer.append(", null " ).append(MtAppDetailVO.PK_CURRTYPE);
        }
        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_MTAPP_BILL);
        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLDATE);
        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_TRADETYPE).append(" pk_billtype");
        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLNO);
        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.CLOSE_STATUS);
        
        sqlBuffer.append(", 0 rn " );
        
         // 查询费用申请执行数
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.EXE_AMOUNT).append(" " ).append(
                MtAppDetailVO.EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.ORG_EXE_AMOUNT).append(
        " " ).append(
                MtAppDetailVO.ORG_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GROUP_EXE_AMOUNT).append(
        " " ).append(MatterappDataProvider.PREFIX_GR).append(
                MtAppDetailVO.GROUP_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(
        " " ).append(MatterappDataProvider.PREFIX_GL).append(
                MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
     // 查询费用申请金额
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.ORIG_AMOUNT).append(" " ).append(
                MtAppDetailVO.ORIG_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.ORG_AMOUNT).append(
        " " ).append(
                MtAppDetailVO.ORG_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GROUP_AMOUNT).append(
        " " ).append(MatterappDataProvider.PREFIX_GR).append(
                MtAppDetailVO.GROUP_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GLOBAL_AMOUNT).append(
        " " ).append(MatterappDataProvider.PREFIX_GL).append(
                MtAppDetailVO.GLOBAL_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        // 查询费用申请余额
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.REST_AMOUNT).append(" " ).append(
                MtAppDetailVO.REST_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI);
        
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.ORG_REST_AMOUNT).append(
                " " ).append(
                MtAppDetailVO.ORG_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GROUP_REST_AMOUNT).append(
                " " ).append(MatterappDataProvider.PREFIX_GR).append(
                MtAppDetailVO.GROUP_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", ").append("zb." ).append(MtAppDetailVO.GLOBAL_REST_AMOUNT).append(
                " " ).append(MatterappDataProvider.PREFIX_GL).append(
                MtAppDetailVO.GLOBAL_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);
        
        sqlBuffer.append(", zb.reason");
        
        sqlBuffer.append(" from er_mtapp_bill zb ");
        // 设置查询条件固定值
        sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
        sqlBuffer.append(getCompositeWhereSql("zb",MtAppDetailVO.getDefaultTableName()));
        String[] codes = (String[])queryVO.getUserObject().get("zb.pk_tradetype");
        if (!ArrayUtils.isEmpty(codes)) {
            String sqlCode = SqlUtil.buildInSql("zb.pk_tradetype", codes);
            sqlBuffer.append(" and ").append(sqlCode);
        }
        if (queryVO.getBeginDate() != null) { // 查询开始日期
            sqlBuffer.append(" and " ).append( "zb." ).append(MtAppDetailVO.BILLDATE).append(
                    " >= '").append(queryVO.getBeginDate().toString()).append("' ");
        }
        
        if (queryVO.getEndDate() != null) { // 查询结束日期
            sqlBuffer.append(" and " ).append( "zb." ).append(MtAppDetailVO.BILLDATE).append(
                    " <= '").append(queryVO.getEndDate().toString()).append("' ");
        }

        // 单据状态
        sqlBuffer.append(" and zb.").append(MtAppDetailVO.BILLSTATUS);
        if (IPubReportConstants.BILL_STATUS_ALL.equals(queryVO.getBillState())) {
            sqlBuffer.append(" >= ").append(ErmMatterAppConst.BILLSTATUS_SAVED);
        } else if (IPubReportConstants.BILL_STATUS_SAVE.equals(queryVO
                .getBillState())) {
            sqlBuffer.append(" >= ").append(ErmMatterAppConst.BILLSTATUS_SAVED);
        } else if (IErmReportConstants.BILL_STATUS_COMMIT.equals(queryVO
                .getBillState())) {
            sqlBuffer.append(" >= ").append(
                    ErmMatterAppConst.BILLSTATUS_SAVED);
        } else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(queryVO
                .getBillState())) {
            sqlBuffer.append(" >= ").append(
                    ErmMatterAppConst.BILLSTATUS_APPROVED);
        }

        sqlBuffer.append(getQueryObjSql()); // 查询对象
        
        if (queryVO.getPk_currency() !=null) {
            sqlBuffer.append(" and zb." ).append( MtAppDetailVO.PK_CURRTYPE ).append(" ='").append( queryVO.getPk_currency() ).append( "' "); // 币种
            
        }
        
        sqlBuffer.append(" and ").append(SqlUtils.getInStr("zb." + PK_ORG, queryVO.getPk_orgs())); // 业务单元
        sqlBuffer.append(" and " ).append( "zb." ).append( PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");

        sqlBuffer.append(" and zb.pk_tradetype in (select DJLXBM from er_djlx where djdl = 'ma' and matype = 1 and pk_group = '");
        sqlBuffer.append(queryVO.getPk_group()).append("') ");
        //金额字段
        if (StringUtils.isNotEmpty(origAmountSqlWhere)) {
            sqlBuffer.append(" and ").append(origAmountSqlWhere);
        }
//        sqlBuffer.append(" group by ");
//        sqlBuffer.append("zb." ).append( MtAppDetailVO.PK_GROUP ).append( ", " ).append( "zb." ).append( MtAppDetailVO.PK_ORG);
//        sqlBuffer.append(", " ).append( groupByBaseExp);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_CURRTYPE);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLDATE);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_TRADETYPE);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.BILLNO);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.CLOSE_STATUS);
//        sqlBuffer.append(", " ).append( "zb.").append(MtAppDetailVO.PK_MTAPP_BILL);
        
//        if (StringUtils.isNotEmpty(origAmountSqlWhere)) {
//            origAmountSqlWhere = origAmountSqlWhere.replace("zb.orig_amount", "zbb.orig_amount_ori");
//            sqlBuffer.append(") zbb where ").append(origAmountSqlWhere);
//        }

        //主表的事由
//        sqlBuffer.append(") lb left join er_mtapp_bill rb on lb.pk_mtapp_bill = rb.pk_mtapp_bill");
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
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_matterapp21" + qryObjLen,
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
//			otherColNameBuf.append("reason, ");
			otherColNameBuf.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(",").append(PK_CURR);

			otherColNameBuf.append(",").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", rn, ");
			//执行数
			otherColNameBuf.append(MtAppDetailVO.EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(// 金额原币
					"," ).append(MtAppDetailVO.ORG_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额本币
					"," ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额集团本币
					"," ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);// 金额全局本币
			otherColNameBuf.append(",");
			//金额
			otherColNameBuf.append(MtAppDetailVO.ORIG_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(// 金额原币
			"," ).append(MtAppDetailVO.ORG_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额本币
			"," ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额集团本币
			"," ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);// 金额全局本币
			//余额
			otherColNameBuf.append(",");
			otherColNameBuf.append(MtAppDetailVO.REST_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(// 金额原币
					"," ).append(MtAppDetailVO.ORG_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额本币
					"," ).append(MatterappDataProvider.PREFIX_GR).append(MtAppDetailVO.GROUP_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(// 金额集团本币
					"," ).append(MatterappDataProvider.PREFIX_GL).append(MtAppDetailVO.GLOBAL_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC);// 金额全局本币
			otherColNameBuf.append(", reason");
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
			for (; i < tmpTblColTypes.length - 14; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}
			
			tmpTblColTypes[i++] = Types.INTEGER;
			
			for (; i < tmpTblColTypes.length - 1; i++) {
				tmpTblColTypes[i] = Types.DECIMAL;
			}
			tmpTblColTypes[i] = Types.VARCHAR;
		}
		return tmpTblColTypes;
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
//		sqlBuffer.append("null ").append(allQryobjs.get(i).field).append(", ");
		
		sqlBuffer.append("null " ).append(MtAppDetailVO.BILLDATE).append(
				", null " ).append(MtAppDetailVO.CLOSE_STATUS).append(
				", null " ).append("pk_billtype").append(
				", null " ).append(MtAppDetailVO.BILLNO).append(
				", null " ).append(MtAppDetailVO.PK_MTAPP_BILL).append(
//				", null " ).append(MtAppDetailVO.REASON).append(
				", ");
        i = 0;
        for (; i < computed.size(); i++) {
            sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
        }
        sqlBuffer.append(SmartProcessor.MAX_ROW).append(" + 1 rn, ");
		 // 查询费用申请执行数
	    sqlBuffer.append(" sum(").append(MtAppDetailVO.EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(") " );
	    
	    sqlBuffer.append(", sum(").append(MtAppDetailVO.ORG_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
	    ") " );
	    
	    sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GR).append(
	    		MtAppDetailVO.GROUP_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
	    		") " );
	    
	    sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GL).append(
	    		MtAppDetailVO.GLOBAL_EXE_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
	    		") " );
	    // 查询费用申请金额
		sqlBuffer.append(", sum(").append(MtAppDetailVO.ORIG_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(") " );
		
		sqlBuffer.append(", sum(").append(MtAppDetailVO.ORG_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				") " );
		
		sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GR).append(
				MtAppDetailVO.GROUP_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				") " );
		
		sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GL).append(
				MtAppDetailVO.GLOBAL_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
				") " );
		// 查询费用申请金额
		sqlBuffer.append(", sum(").append(MtAppDetailVO.REST_AMOUNT).append(MatterappDataProvider.SUFFIX_ORI).append(") " );
		
		sqlBuffer.append(", sum(").append(MtAppDetailVO.ORG_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
		") " );
		
		sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GR).append(
				MtAppDetailVO.GROUP_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
		") " );
		
		sqlBuffer.append(", sum(").append(MatterappDataProvider.PREFIX_GL).append(
				MtAppDetailVO.GLOBAL_REST_AMOUNT).append(MatterappDataProvider.SUFFIX_LOC).append(
		") " );
		sqlBuffer.append(", null ").append(MtAppDetailVO.REASON);

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
			sqlBuffer.append(" and grouping(").append(allQryobjs.get(1).field).append(") = 0 ");
		}

		return sqlBuffer.toString();
	}

}