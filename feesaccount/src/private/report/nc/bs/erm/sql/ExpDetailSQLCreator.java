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
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.utils.fipub.SmartProcessor;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
import nc.vo.fipub.report.QryObj;
import nc.vo.fipub.utils.SqlBuilder;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>
 * 用来构造费用明细账表所涉及的sql 针对费用明细账，包括几个部分：
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
public class ExpDetailSQLCreator extends ErmCSBaseSqlCreator {
	
	// 固定查询字段
	private static String detailFields = "@Table.billdate, @Table.reason,  @Table.pk_billtype, @Table.src_id,  @Table.src_billno, @Table.accperiod ";
	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;
	private final List<ComputeTotal> allQryobjList = new ArrayList<ComputeTotal>();

	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getExpenseDetail()); // 计算费用明细
		sqlList.add(getSubTotalSql()); // 计算小计合计

		return sqlList.toArray(new String[0]);
	}

	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select ");

		sqlBuffer.append(fixedFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
		sqlBuffer.append(", org_orgs.code as code_org, isnull(org_orgs.name").append(getMultiLangIndex()).append(", org_orgs.name) org"); 

		String[] qryObjs = getQueryObjs();
		List<QryObj> qryObjList = queryVO.getQryObjs();

		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(", v.").append(qryObjs[i]).append(", ");
			sqlBuffer.append(bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_codeField()).append(" ")
					.append(IPubReportConstants.QRY_OBJ_PREFIX).append(i).append("code, ");
			sqlBuffer.append("isnull(").append(bdTable ).append( i).append(".")
			.append(qryObjList.get(i).getBd_nameField())
			.append(getMultiLangIndex()).append(", ").append(bdTable )
			.append( i).append(".").append(qryObjList.get(i).getBd_nameField()).append(") ")
			.append(IPubReportConstants.QRY_OBJ_PREFIX).append(i);
		}

        if (beForeignCurrency) {
            sqlBuffer.append(", v.pk_currtype, ");
        } else {
            sqlBuffer.append(", null pk_currtype, ");
        }
		sqlBuffer.append(detailFields.replace(IErmReportConstants.REPLACE_TABLE, "v"));
//		sqlBuffer.append(", v.rn, (case when v.rn = 0 then 0 else 1 end) as is_begin"); // is_begin
		sqlBuffer.append(", v.rn");
		sqlBuffer.append(", v.").append(ExpenseAccountVO.ASSUME_AMOUNT).append(ExpDetailDataProvider.SUFFIX_ORI);
		sqlBuffer.append(", v.").append(ExpenseAccountVO.ORG_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		sqlBuffer.append(", v.").append(ExpDetailDataProvider.PREFIX_GR).append(ExpenseAccountVO.GROUP_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		sqlBuffer.append(", v.").append(ExpDetailDataProvider.PREFIX_GL).append(ExpenseAccountVO.GLOBAL_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);
		sqlBuffer.append(" from ").append(getTmpTblName()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on v.pk_org = org_orgs.pk_org ");
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(" left outer join ").append(qryObjList.get(i).getBd_table()).append(" ").append(
					bdTable ).append( i).append(" on ").append("v.").append(qryObjs[i]).append(" = ").append(
					bdTable ).append( i).append(".").append(qryObjList.get(i).getBd_pkField());
		}

		sqlBuffer.append(" order by ");
		sqlBuffer.append(ErmReportSqlUtils.caseWhenSql("org_orgs.code"));
		for (int i = 0; i < qryObjList.size(); i++) {
			sqlBuffer.append(",");
			sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(bdTable +  i + "." + qryObjList.get(i).getBd_codeField()));
//			sqlBuffer.append(ErmReportSqlUtils.caseWhenSql(IPubReportConstants.QRY_OBJ_PREFIX+i+"code"));
		}
//		if (beForeignCurrency) {
			sqlBuffer.append(", ").append(ErmReportSqlUtils.caseWhenSql("v.pk_currtype"));
//		}
//		sqlBuffer.append(", ").append("is_begin").append(", ").
		sqlBuffer.append(", v.rn, ").
		append(ErmReportSqlUtils.caseWhenSql("v."+ExpenseAccountVO.BILLDATE))
		.append(", v.src_billno");
		return sqlBuffer.toString();
	}
	
	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}

//    private String getQryobjpk() {
//        List<QryObj> qryObjList = queryVO.getQryObjs();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < qryObjList.size(); i++) {
//            sb.append(" qryobj").append(i).append("pk, ");
//        }
//        return sb.toString();
//    }

	/**
	 * new 报销管理：查询费用明细记录<br>
	 * 
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException
	 */
	private String getExpenseDetail() throws SQLException, BusinessException{
	    
//        String sqlOut = " select pk_group, pk_org, "
//                + getQryobjpk()
//                + " pk_currtype, billdate, reason, "
//                + "(case when k.pk_billtype = '~' then (select distinct x.SRC_BILLTYPE from er_expenseaccount x where x.SRC_ID = k.src_id) else k.pk_billtype end) as pk_billtype, "
//                + "src_id, src_billno, accperiod, 1 rn, assume_amount_ori, org_amount_loc, gr_group_amount_loc, gl_global_amount_loc from (";
	    
		StringBuffer sqlBuffer = new StringBuffer(" insert into ").append(getTmpTblName());

//        sqlBuffer.append(sqlOut);
		sqlBuffer.append(" select ");

        if(ExpenseBalVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
            sqlBuffer.append("zb.").append(ExpenseAccountVO.PK_GROUP).append(", ").append("zb.bx_fiorg pk_org");
        } else {
            sqlBuffer.append("zb.").append(ExpenseAccountVO.PK_GROUP).append(", ").append("zb." ).append(ExpenseAccountVO.PK_ORG);
        }
		
		sqlBuffer.append(", ").append(queryObjBaseExp);

        if (beForeignCurrency) {
            sqlBuffer.append(", zb." ).append(ExpenseAccountVO.PK_CURRTYPE);
        } else {
            sqlBuffer.append(", null " ).append(ExpenseAccountVO.PK_CURRTYPE);
        }
		
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.BILLDATE);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.REASON);
        sqlBuffer.append(", ").append("zb.")
                .append(ExpenseAccountVO.SRC_TRADETYPE).append(" pk_billtype");
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.SRC_ID);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.SRC_BILLNO);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.ACCPERIOD);
		sqlBuffer.append(", 1 rn");
		// 查询报销费用金额
		sqlBuffer.append(", sum(").append("zb." ).append(ExpenseAccountVO.ASSUME_AMOUNT).append(") " ).append(
				ExpenseAccountVO.ASSUME_AMOUNT).append(ExpDetailDataProvider.SUFFIX_ORI);
		
		sqlBuffer.append(", sum(").append("zb." ).append(ExpenseAccountVO.ORG_AMOUNT).append(
				") " ).append(
				ExpenseAccountVO.ORG_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		
		sqlBuffer.append(", sum(").append("zb." ).append(ExpenseAccountVO.GROUP_AMOUNT).append(
				") " ).append(ExpDetailDataProvider.PREFIX_GR).append(
				ExpenseAccountVO.GROUP_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		
		sqlBuffer.append(", sum(").append("zb." ).append(ExpenseAccountVO.GLOBAL_AMOUNT).append(
				") " ).append(ExpDetailDataProvider.PREFIX_GL).append(
				ExpenseAccountVO.GLOBAL_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
		
		sqlBuffer.append(" from er_expenseaccount zb ");

		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		sqlBuffer.append(getCompositeWhereSql("zb","cs"));
		if (queryVO.getBeginDate() != null) { // 查询开始日期
			sqlBuffer.append(" and " ).append( "zb." ).append(ExpenseAccountVO.BILLDATE).append(
					" >= '").append(queryVO.getBeginDate().toString()).append("' ");
		}
		
		if (queryVO.getEndDate() != null) { // 查询结束日期
			sqlBuffer.append(" and " ).append( "zb." ).append(ExpenseAccountVO.BILLDATE).append(
					" <= '").append(queryVO.getEndDate().toString()).append("' ");
		}
		
		//单据状态
		int billstatus = BXStatusConst.DJZT_Saved; 
        if(IPubReportConstants.BILL_STATUS_SAVE.equals(queryVO.getBillState())){
            billstatus = BXStatusConst.DJZT_Saved;
        }else if(IPubReportConstants.BILL_STATUS_CONFIRM.equals(queryVO.getBillState())){
            billstatus = BXStatusConst.DJZT_Verified;
        }else if(IPubReportConstants.BILL_STATUS_EFFECT.equals(queryVO.getBillState())){
            billstatus = BXStatusConst.DJZT_Sign;
        }
        sqlBuffer.append(" and zb.").append( ExpenseAccountVO.BILLSTATUS).append(" >= ").append(billstatus);
		
        sqlBuffer.append(" and zb.src_billtype <> '2647' and zb.src_tradetype <> '2647' ");
		if(ExpenseAccountVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
			//如果是报销人，只查出报销单的费用账数据
            sqlBuffer
                    .append(" and zb.")
                    .append(ExpenseAccountVO.SRC_TRADETYPE)
                    .append(" in ")
                    .append(" (select  pk_billtypecode from bd_billtype where  parentbilltype ='264X' and istransaction = 'Y' ");
			sqlBuffer.append(" and islock ='N' and  pk_group='" ).append(queryVO.getPk_group()).append(
					"' )");
            sqlBuffer.append(" and ").append(SqlUtils.getInStr("zb.bx_fiorg", queryVO.getPk_orgs())); // 业务单元
		}else {
			//非冲销态数据
			sqlBuffer.append(" and (zb." ).append( ExpenseAccountVO.ISWRITEOFF ).append("<>").append( "'Y'" ).append(" or ").append(ExpenseAccountVO.ISWRITEOFF).append(" is null").append(")");
			sqlBuffer.append(" and ").append(SqlUtils.getInStr("zb." + PK_ORG, queryVO.getPk_orgs())); // 业务单元
		}
		
        String[] codes = (String[]) queryVO.getUserObject()
                .get("src_tradetype");
        if (!ArrayUtils.isEmpty(codes)) {
            String sqlCode = SqlUtil.buildInSql("zb.src_tradetype", codes);
            sqlBuffer.append(" and ").append(sqlCode);
        }
		
		sqlBuffer.append(getQueryObjSql()); // 查询对象
		if (queryVO.getPk_currency() !=null) {
//			sqlBuffer.append(" and zb." ).append( ExpenseAccountVO.PK_CURRTYPE ).append(" ='").append( queryVO.getPk_currency() ).append( "' "); // 币种
			String[] pkCurrTypes = queryVO.getPk_currency().split(",");
            String sqlCurrType = SqlUtil.buildInSql("zb." + ExpenseBalVO.PK_CURRTYPE, pkCurrTypes);
            sqlBuffer.append(" and ").append(sqlCurrType).append(" ");
		}
		
		sqlBuffer.append(" and " ).append( "zb." ).append( PK_GROUP).append(" = '").append(queryVO.getPk_group()).append("' ");

		sqlBuffer.append(" group by ");
		
		if(ExpenseBalVO.BX_JKBXR.equals(queryVO.getQryObjs().get(0).getOriginFld())){
            sqlBuffer.append("zb." ).append( ExpenseAccountVO.PK_GROUP ).append( ", " ).append( "zb.bx_fiorg" );
        } else {
            sqlBuffer.append("zb." ).append( ExpenseAccountVO.PK_GROUP ).append( ", " ).append( "zb." ).append( ExpenseAccountVO.PK_ORG);
        }
		
		sqlBuffer.append(", " ).append( groupByBaseExp);

        if (beForeignCurrency) {
            sqlBuffer.append(", zb." ).append(ExpenseAccountVO.PK_CURRTYPE);
        }
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.BILLDATE);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.REASON);
        sqlBuffer.append(", ").append("zb.")
                .append(ExpenseAccountVO.SRC_TRADETYPE);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.SRC_ID);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.SRC_BILLNO);
		sqlBuffer.append(", " ).append( "zb.").append(ExpenseAccountVO.ACCPERIOD);
//        sqlBuffer.append(") k");
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
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_expdetail" + qryObjLen,
					getTmpTblColNames(), getTmpTblColTypes());
		}
//		tmpTblName = "nnn";
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

			otherColNameBuf.append(", ").append(detailFields.replace(IErmReportConstants.REPLACE_TABLE + ".", ""));
			otherColNameBuf.append(", rn, ");
//			otherColNameBuf.append("exp_ori, exp_loc, gr_exp_loc, gl_exp_loc");
			otherColNameBuf.append(ExpenseAccountVO.ASSUME_AMOUNT).append(ExpDetailDataProvider.SUFFIX_ORI).append(
					", " ).append(ExpenseAccountVO.ORG_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC).append(
					", " ).append(ExpDetailDataProvider.PREFIX_GR).append(ExpenseAccountVO.GROUP_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC).append(
					", " ).append(ExpDetailDataProvider.PREFIX_GL).append(ExpenseAccountVO.GLOBAL_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC);
			
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
			for (; i < tmpTblColTypes.length - 4-1; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}

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
		
		sqlBuffer.append("null " ).append(ExpenseAccountVO.BILLDATE).append(
				", null " ).append(ExpenseAccountVO.REASON).append(
				", null " ).append("pk_billtype").append(
				", null " ).append(ExpenseAccountVO.SRC_ID).append(
				", null " ).append(ExpenseAccountVO.SRC_BILLNO).append(
				", null " ).append(ExpenseAccountVO.ACCPERIOD).append(
				", ");
		i = 0;
		for (; i < computed.size(); i++) {
			sqlBuffer.append("grouping(").append(computed.get(i)).append(") + ");
		}
		sqlBuffer.append(SmartProcessor.MAX_ROW).append(" + 1 rn, ");
		// 查询报销费用金额
		sqlBuffer.append(" sum(").append(ExpenseAccountVO.ASSUME_AMOUNT).append(ExpDetailDataProvider.SUFFIX_ORI).append(") ");
		sqlBuffer.append(", sum(").append(ExpenseAccountVO.ORG_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC).append(") ");
		sqlBuffer.append(", sum(").append(ExpDetailDataProvider.PREFIX_GR).append(ExpenseAccountVO.GROUP_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC).append(") " );
		sqlBuffer.append(", sum(").append(ExpDetailDataProvider.PREFIX_GL).append(ExpenseAccountVO.GLOBAL_AMOUNT).append(ExpDetailDataProvider.SUFFIX_LOC).append(") ");

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

//        String[] qryobjs = queryObjOrderExt.split(",");
//        for (int n = 0; n < qryobjs.length; n++) {
//            sqlBuffer.append(" and grouping(").append(qryobjs[n].trim()).append(") = 0 ");
//        }
        
		return sqlBuffer.toString();
	}

}

// /:~
