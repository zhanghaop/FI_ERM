package nc.impl.erm.expensetrend;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.util.ErmReportSqlUtils;
import nc.bs.erm.util.TmpTableCreator;
import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.smartprovider.ExpBalanceDataProvider;
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.erm.expenseaccount.ExpenseBalVO;
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
public class ExpTrendSQLCreator extends ExpTrendBaseSqlCreator {

	private String tmpTblName = null;
	private String[] tmpTblColNames = null;
	private Integer[] tmpTblColTypes = null;


	@Override
	public String[] getArrangeSqls() throws SQLException, BusinessException {
		List<String> sqlList = new ArrayList<String>();
		sqlList.add(getExpenseAccumulativeOccur());
		return sqlList.toArray(new String[0]);
	}

	@Override
	public String[] getDropTableSqls() throws SQLException, BusinessException {
		return new String[0];
	}
	
	@Override
	public String getResultSql() throws SQLException {
		StringBuffer sqlBuffer = new StringBuffer(" select distinct v.pk_org,");
		sqlBuffer.append("org_orgs.code code_org,");
		sqlBuffer.append(" isnull(org_orgs.name" ).append(getMultiLangIndex()).append(", org_orgs.name) org,");
		sqlBuffer.append(" v.");
		sqlBuffer.append(queryVO.getQryobj().getOriginFld());
		sqlBuffer.append(" ,");
		sqlBuffer.append(" bd0." ).append(queryVO.getQryobj().getBd_codeField()).append(
				" qryobj0code,");
		sqlBuffer.append(ExpenseBalVO.ACCMONTH);
        sqlBuffer.append(" , 'a' as accmonth_show, ");
		sqlBuffer.append(" isnull(bd0." ).append(queryVO.getQryobj().getBd_nameField()).append(getMultiLangIndex()).append(
				", bd0." ).append(queryVO.getQryobj().getBd_nameField()).append(
				") qryobj0, ");
		sqlBuffer.append(" v.pk_currtype,");
		sqlBuffer.append(" sum(v.assume_amount" ).append(ExpBalanceDataProvider.SUFFIX_ORI).append(
				") ").append(ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI);

		sqlBuffer.append(", sum(").append("v." )
		    .append(ExpenseBalVO.ORG_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " )
		    .append(ExpenseBalVO.ORG_AMOUNT)
		    .append(ExpBalanceDataProvider.SUFFIX_LOC);
		sqlBuffer.append(", sum(").append("v." )
		    .append(ExpBalanceDataProvider.PREFIX_GR)
		    .append(ExpenseBalVO.GROUP_AMOUNT)
		    .append(ExpBalanceDataProvider.SUFFIX_LOC)
		    .append(") " ).append(ExpBalanceDataProvider.PREFIX_GR)
		    .append(ExpenseBalVO.GROUP_AMOUNT)
		    .append(ExpBalanceDataProvider.SUFFIX_LOC);
		sqlBuffer.append(", sum(").append("v." )
		    .append(ExpBalanceDataProvider.PREFIX_GL)
		    .append(ExpenseBalVO.GLOBAL_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_LOC).append(") " )
		    .append(ExpBalanceDataProvider.PREFIX_GL)
		    .append(ExpenseBalVO.GLOBAL_AMOUNT)
		    .append(ExpBalanceDataProvider.SUFFIX_LOC);
		
        sqlBuffer.append(", 0 ").append(IPubReportConstants.ORDER_MANAGE_VSEQ);
        
		sqlBuffer.append(" from " ).append( getTmpTblName()).append(" v ");
		sqlBuffer.append(" left outer join org_orgs on ");
		sqlBuffer.append("   v.pk_org = org_orgs.pk_org ");
		sqlBuffer.append(" left outer join " ).append(queryVO.getQryobj().getBd_table()).append(
				" bd0 on");
		sqlBuffer.append(" v." ).append(queryVO.getQryobj().getOriginFld()).append(
				" = bd0." ).append(queryVO.getQryobj().getBd_pkField()).append(
				" group by ");
		sqlBuffer.append("  v.pk_org, org_orgs.code, org_orgs.name, ");    
		sqlBuffer.append("  org_orgs.name" ).append(getMultiLangIndex()).append(", v." ).append(queryVO.getQryobj().getOriginFld()).append(
				",bd0." ).append(queryVO.getQryobj().getBd_codeField()).append(
				", ");    
		sqlBuffer.append(ExpenseBalVO.ACCMONTH);
		sqlBuffer.append(" ,");
		sqlBuffer.append(" bd0." ).append(queryVO.getQryobj().getBd_nameField());
		sqlBuffer.append(" ,");
		sqlBuffer.append(" bd0." ).append(queryVO.getQryobj().getBd_nameField()).append(getMultiLangIndex()).
		append(",v.pk_currtype 	order by ");    
		sqlBuffer.append(" accmonth,code_org,qryobj0code ");    
		return sqlBuffer.toString();
	}

	/**
	 * 报销管理：查询报销管理<br>
	 * @return String<br>
	 * @throws BusinessException<br>
	 * @throws SQLException<br>
	 * @throws BusinessException 
	 */
	public String getExpenseAccumulativeOccur() throws SQLException, BusinessException {
		String bxzbAlias = "zb";
		StringBuffer sqlBuffer =  new StringBuffer(" insert into ").append(getTmpTblName());
		sqlBuffer.append(" select ");
		
		for (int i = 0; i < getTmpTblColNames().length - 4; i++) {
			sqlBuffer.append(getTmpTblColNames()[i]);
			sqlBuffer.append(",");
		}
		sqlBuffer.append(" sum(").append("zb." ).append(ExpenseBalVO.ASSUME_AMOUNT).append(") " ).append(
				ExpenseBalVO.ASSUME_AMOUNT).append(ExpBalanceDataProvider.SUFFIX_ORI);
        sqlBuffer.append(", sum(").append("zb." )
                .append(ExpenseBalVO.ORG_AMOUNT).append(") " )
                .append(ExpenseBalVO.ORG_AMOUNT)
                .append(ExpBalanceDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", sum(").append("zb." )
            .append(ExpenseBalVO.GROUP_AMOUNT)
            .append(") " ).append(ExpBalanceDataProvider.PREFIX_GR)
            .append(ExpenseBalVO.GROUP_AMOUNT)
            .append(ExpBalanceDataProvider.SUFFIX_LOC);
        sqlBuffer.append(", sum(").append("zb." )
            .append(ExpenseBalVO.GLOBAL_AMOUNT).append(") " )
            .append(ExpBalanceDataProvider.PREFIX_GL)
            .append(ExpenseBalVO.GLOBAL_AMOUNT)
            .append(ExpBalanceDataProvider.SUFFIX_LOC);
        
		sqlBuffer.append(" from er_expensebal ").append(bxzbAlias);

		// 设置查询条件固定值
		sqlBuffer.append(" where ").append(ErmReportSqlUtils.getFixedWhere());
		if (!StringUtils.isEmpty(getCompositeWhereSql(bxzbAlias))) {
			sqlBuffer.append(getCompositeWhereSql(bxzbAlias));
		}

		//暂存数据不查出
		sqlBuffer.append(" and zb." ).append( ExpenseBalVO.BILLSTATUS ).append("=").append( BXStatusConst.DJZT_Sign);
		sqlBuffer.append(" and zb." ).append( ExpenseBalVO.ASSUME_AMOUNT ).append(" <> 0");
		//非冲销态数据
		sqlBuffer.append(" and zb." ).append( ExpenseBalVO.ISWRITEOFF ).append("<>").append( "'Y'");
		sqlBuffer.append(" and zb." ).append( queryVO.getQryobj().getOriginFld() ).append("<>").append( "'~'");
		sqlBuffer.append(" and ").append(bxzbAlias ).append( ".dr = 0 ");
		sqlBuffer.append(" group by ");
		sqlBuffer.append(" pk_org,");
		sqlBuffer.append("pk_currtype,");
		sqlBuffer.append("pk_iobsclass,");
		sqlBuffer.append("assume_dept,");
		sqlBuffer.append("pk_resacostcenter,");
		sqlBuffer.append("accmonth,");
		sqlBuffer.append("assume_amount");
		return sqlBuffer.toString();
	}

	

	/**
	 * 获取临时表名<br>
	 * 
	 * @return String<br>
	 * @throws SQLException<br>
	 */
	public String getTmpTblName() throws SQLException {
		if (StringUtils.isEmpty(tmpTblName)) {
			tmpTblName = TmpTableCreator.createTmpTable("tmp_erm_exptrend2",
					getTmpTblColNames(), getTmpTblColTypes());
		}

		return tmpTblName;
	}

	/**
	 * 获取临时表列<br>
	 * @return String[]<br>
	 */
	
	public String[] getTmpTblColNames() {
		if (tmpTblColNames == null) {
			tmpTblColNames = new String[10];
			tmpTblColNames[0] = ExpenseBalVO.PK_ORG;
			tmpTblColNames[1] = ExpenseBalVO.PK_CURRTYPE;
			tmpTblColNames[2] = ExpenseBalVO.PK_IOBSCLASS;
			tmpTblColNames[3] = ExpenseBalVO.ASSUME_DEPT;
			tmpTblColNames[4] = ExpenseBalVO.PK_RESACOSTCENTER;
			tmpTblColNames[5] = ExpenseBalVO.ACCMONTH;
			tmpTblColNames[6] = ExpenseBalVO.ASSUME_AMOUNT+ExpBalanceDataProvider.SUFFIX_ORI;
            tmpTblColNames[7] = ExpenseBalVO.ORG_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC;
            tmpTblColNames[8] = ExpDetailDataProvider.PREFIX_GR + ExpenseBalVO.GROUP_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC;
            tmpTblColNames[9] = ExpDetailDataProvider.PREFIX_GL + ExpenseBalVO.GLOBAL_AMOUNT+ExpBalanceDataProvider.SUFFIX_LOC;
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
			for (; i < tmpTblColTypes.length - 4; i++) {
				tmpTblColTypes[i] = Types.VARCHAR;
			}
			
			for (; i < tmpTblColTypes.length; i++) {
	            tmpTblColTypes[i] = Types.DECIMAL;
			}
		}

		return tmpTblColTypes;
	}

}

// /:~
