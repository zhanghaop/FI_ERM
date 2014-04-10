package nc.bs.erm.util;

import java.sql.SQLException;
import nc.bs.er.util.SqlUtils;

import org.apache.commons.lang.StringUtils;

public class ErmReportSqlUtils {
	
	public static final int I_BILL_STATUS_ALL = -1000; // ȫ������״̬
//	private static String CorpFld = "zfdwbm";  //������ǳ�ȡ����������
	/**
	 * �����������ع̶���whereƬ�β���<br>
	 * 
	 */
	public static String getFixedWhere() {
		return " 1 = 1 ";
	}
	/**
	 * ����������õ���״̬��Ӧ��sqlƬ��<br>
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
	 * ��������ҵ��Ԫ<br>
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
//	public static String getGroupSql(String pk_group, ReportTableEnum table) {
//		if (StringUtils.isEmpty(pk_group)) {
//			// ��֧�ֿ缯�Ų�ѯ
//			return " and 1 = 2 ";
//		}
//		return " and " + table.getCode() + ".pk_group = '" + pk_group + "'";
//	}
	/**
	 * ������������״̬��ת��<br>
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
	 * ��������������Ϣ��õĶ�Ӧ��sqlƬ��<br>
	 * 
	 */
	//FIXME ע�������bzbm
	public static String getCurrencySql(String pk_currency, boolean isBalance) {
		if (StringUtils.isEmpty(pk_currency)) {
			return " ";
		}
		if(!isBalance){
			return " and " + getAlias("er_bxzb") + ".bzbm = '" + pk_currency + "' ";
		} else {
			return " and " + getAlias("er_jkzb") + ".bzbm = '" + pk_currency + "' ";
		}
	}
	/**
	 * ��������ͨ�÷�����ȡ�����<br>
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
	 * ��������ͨ�÷�����ȡ�����<br>
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
//			return TBL_ARAP_BALANCE; // �������ѯ
//		}
//		return TBL_ARAP_TALLY; // ����ϸ�ʲ�ѯ
//	}
		
}



