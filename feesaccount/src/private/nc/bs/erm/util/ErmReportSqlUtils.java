package nc.bs.erm.util;

import java.sql.SQLException;

import nc.bs.er.util.SqlUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.logging.Logger;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.StringUtils;

public class ErmReportSqlUtils {
	
	public static final int I_BILL_STATUS_ALL = -1000; // 全部单据状态
//	private static String CorpFld = "zfdwbm";  //这个考虑抽取到常量类中
	/**
	 * 报销管理：返回固定的where片段部分<br>
	 * 
	 */
	public static String getFixedWhere() {
		return " 1 = 1 ";
	}
	/**
	 * 报销管理：获得单据状态对应的sql片段<br>
	 * 
	 */
//	public static String getBillStatusSql(String billStatus, boolean isBalance) throws SQLException {
//		int iBillStatus = convertBillStatus(billStatus);
//		if (I_BILL_STATUS_ALL == iBillStatus) {
//			return " ";
//		}
////		return " and " + getTargetTable(isBalance) + ".billstatus >= " + iBillStatus;
//		return null;
//	}
	/**
	 * 报销管理：业务单元<br>
	 * 
	 */
	public static String getOrgSql(String[] pk_orgs, boolean isBalance) throws SQLException {
		if(!isBalance){
			return " and " + SqlUtils.getInStr(getAlias("er_bxzb") + ".pk_org", pk_orgs);
		}else {
			return " and " + SqlUtils.getInStr(getAlias("er_jkzb") + ".pk_org", pk_orgs);
		}
	}
	public static String getGroupSql(String pk_group, boolean isBalance) throws SQLException {
		if(!isBalance){
			return " and " + getAlias("er_bxzb") + ".pk_group = '" + pk_group + "'";
		}else {
			return " and " + getAlias("er_jkzb") + ".pk_group = '" + pk_group + "'";
		}
	}
	
	/**
	 * 适配不同数据库写的casewhen
	 * @param sql
	 * @return
	 */
	public static String caseWhenSql(String field){
		return "case when " +field+" is null then 1 else 0 end, " +field;
	}
	
//	public static String getGroupSql(String pk_group, ReportTableEnum table) {
//		if (StringUtils.isEmpty(pk_group)) {
//			// 不支持跨集团查询
//			return " and 1 = 2 ";
//		}
//		return " and " + table.getCode() + ".pk_group = '" + pk_group + "'";
//	}
	/**
	 * 报销管理：单据状态的转换<br>
	 * 
	 */
	
//	public static int convertBillStatus(String billStatus) {
//		if (IPubReportConstants.BILL_STATUS_ALL.equals(billStatus)) {
//			return I_BILL_STATUS_ALL;
//		} else if (IPubReportConstants.BILL_STATUS_SAVE.equals(billStatus)) {
//			return BillEnumCollection.BillSatus.Save.VALUE;
//		} else if (IPubReportConstants.BILL_STATUS_CONFIRM.equals(billStatus)) {
//			return BillEnumCollection.BillSatus.Audit.VALUE;
//		} 
////		else if (IPubReportConstants.BILL_STATUS_EFFECT.equals(billStatus)) {
////			return BillEnumCollection.BillSatus.Sign.VALUE;
////		}
//		return BillEnumCollection.BillSatus.Tempeorary.VALUE;
//	}
	/**
	 * 报销管理：币种信息获得的对应的sql片段<br>
	 * 
	 */
	//FIXME 注意下面的bzbm
	public static String getCurrencySql(String pk_currency, boolean isBalance) {
		if (StringUtils.isEmpty(pk_currency)) {
			return " ";
		}
		String field;
		if(!isBalance){
		    field = getAlias("er_bxzb") + ".bzbm";
        } else {
            field = getAlias("er_jkzb") + ".bzbm";
        }
		String[] pkCurrTypes = pk_currency.split(",");
        String sqlCurrType;
        try {
            sqlCurrType = SqlUtil.buildInSql(field, pkCurrTypes);
        } catch (BusinessException e) {
            Logger.error(e.getMessage(), e);
            sqlCurrType = "1 = 1";
        }
        return " and " + sqlCurrType + " ";
//        sqlBuffer.append(" and ").append(sqlCurrType).append(" ");
//		if(!isBalance){
//			return " and " + getAlias("er_bxzb") + ".bzbm = '" + pk_currency + "' ";
//		} else {
//			return " and " + getAlias("er_jkzb") + ".bzbm = '" + pk_currency + "' ";
//		}
	}
	/**
	 * 报销管理通用方法：取表别名<br>
	 * 
	 */
	private static String getAlias(String strTableName)
	{
		if(strTableName.equals("er_jkzb"))
	    	return "zb";
	    else if(strTableName.equals("er_bxcontrast"))
	    	return "fb";
	    else if(strTableName.equals("er_bxzb"))
	    	return "zb";
	    else
	        return null;
	}
	/**
	 * 报销管理通用方法：取表别名<br>
	 * 
	 */
//	private String getGroupSql(String queryObjBaseBal,String yeartable,boolean isNeedYear){
//		
//		StringBuffer grpSql = new StringBuffer(" ");
//		
//		grpSql.append(getAlias("er_jkzb") + "."+CorpFld+",");
//		if(!isNeedYear){
//		}else if(yeartable==null){
//			grpSql.append(getAlias("er_jkzb") + ".kjnd,");
//		}else if(yeartable.equals("er_bxcontrast")){
//			grpSql.append(getAlias(yeartable) + ".cxnd,");
//		}else{
//			grpSql.append(getAlias(yeartable) + ".kjnd,");
//		}
//		
////		Vector<QryObjVO> v_qryObj = voQryStruct.getVetQryObj();
////		for (int i = 0; i < v_qryObj.size(); i++){
////			QryObjVO qryObj = v_qryObj.elementAt(i);
////			if(qryObj != null){
////				grpSql.append(qryObj.getFldorigin() + "." + qryObj.getQryfld() + ",");
////			}
////		}
//		grpSql.append(queryObjBaseBal);
//		
////	    return grpSql.toString().substring(0, grpSql.length() - 1);
//		return grpSql.toString();
//	}
	
//	private static String getTargetTable(boolean isNeedYear) {
//		if (isBalance) {
//			return TBL_ARAP_BALANCE; // 从余额表查询
//		}
//		return TBL_ARAP_TALLY; // 从明细帐查询
//	}
		
}



