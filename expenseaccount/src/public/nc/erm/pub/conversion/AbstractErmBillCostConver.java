package nc.erm.pub.conversion;

import java.util.HashMap;
import java.util.Map;

import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;

public abstract class AbstractErmBillCostConver {
	public static final String BILLDATE = "billdate";
	public static final String BILLMAKER = "billmaker";
	public static final String BILLSTATUS = "billstatus";
	public static final String ASSUME_ORG = "assume_org";
	public static final String ASSUME_DEPT = "assume_dept";
	public static final String PK_PCORG = "pk_pcorg";
	public static final String PK_RESACOSTCENTER = "pk_resacostcenter";
	public static final String PK_IOBSCLASS = "pk_iobsclass";
	public static final String PK_PROJECT = "pk_project";
	public static final String PK_WBS = "pk_wbs";
	public static final String PK_CHECKELE = "pk_checkele";
	public static final String PK_CUSTOMER = "pk_customer";
	public static final String PK_SUPPLIER = "pk_supplier";
	public static final String ASSUME_AMOUNT = "assume_amount";
	public static final String PK_CURRTYPE = "pk_currtype";
	public static final String ORG_CURRINFO = "org_currinfo";
	public static final String ORG_AMOUNT = "org_amount";
	public static final String GLOBAL_AMOUNT = "global_amount";
	public static final String GROUP_AMOUNT = "group_amount";
	public static final String GLOBAL_CURRINFO = "global_currinfo";
	public static final String GROUP_CURRINFO = "group_currinfo";
	public static final String PK_ORG = "pk_org";
	public static final String PK_GROUP = "pk_group";
	public static final String SRC_BILLTYPE = "src_billtype";
	public static final String SRC_TRADETYPE = "src_tradetype";
	public static final String SRC_ID = "src_id";
	public static final String SRC_BILLNO = "src_billno";
	public static final String SRC_BILLTAB = "src_billtab";
	public static final String SRC_SUBID = "src_subid";
	public static final String ACCYEAR = "accyear";
	public static final String ACCMONTH = "accmonth";
	public static final String ACCPERIOD = "accperiod";
	public static final String BX_ORG = "bx_org";
	public static final String BX_GROUP = "bx_group";
	public static final String BX_FIORG = "bx_fiorg";
	public static final String BX_CASHPROJ = "bx_cashproj";
	public static final String BX_DWBM = "bx_dwbm";
	public static final String BX_DEPTID = "bx_deptid";
	public static final String BX_JSFS = "bx_jsfs";
	public static final String BX_CASHITEM = "bx_cashitem";
	public static final String BX_JKBXR = "bx_jkbxr";
	public static final String REASON = "reason";
	public static final String ISWRITEOFF = "iswriteoff";
	public static final String CREATOR = "creator";
	public static final String CREATIONTIME = "creationtime";
	public static final String MODIFIER = "modifier";
	public static final String MODIFIEDTIME = "modifiedtime";
	
	
	//暂时写的一个对照关系
	static Map<String,String> bxfieldMap = new HashMap<String, String>();
	static Map<String,String> costfieldMap = new HashMap<String, String>();
	public static String ZB = "zb.";
	
	static
	{
		costfieldMap.put( CShareDetailVO.ASSUME_ORG , ASSUME_ORG);
		costfieldMap.put( CShareDetailVO.ASSUME_DEPT , ASSUME_DEPT);
		costfieldMap.put( CShareDetailVO.PK_IOBSCLASS , PK_IOBSCLASS);
		costfieldMap.put( CShareDetailVO.JOBID , PK_PROJECT);
		costfieldMap.put( CShareDetailVO.CUSTOMER , PK_CUSTOMER);
		costfieldMap.put( CShareDetailVO.HBBM , PK_SUPPLIER);
		costfieldMap.put( CShareDetailVO.BZBM , PK_CURRTYPE);
		costfieldMap.put( CShareDetailVO.BBHL, ORG_CURRINFO);
		costfieldMap.put( CShareDetailVO.BBJE , ORG_AMOUNT);
		costfieldMap.put( CShareDetailVO.GLOBALBBJE , GLOBAL_AMOUNT);
		costfieldMap.put( CShareDetailVO.GROUPBBJE , GROUP_AMOUNT);
		costfieldMap.put( CShareDetailVO.GLOBALBBHL , GLOBAL_CURRINFO);
		costfieldMap.put( CShareDetailVO.GROUPBBHL , GROUP_CURRINFO);
		costfieldMap.put( CShareDetailVO.PK_BILLTYPE , SRC_BILLTYPE);
		costfieldMap.put( CShareDetailVO.PK_TRADETYPE , SRC_TRADETYPE);
		costfieldMap.put( CShareDetailVO.BILLNO , SRC_BILLNO);
		costfieldMap.put( CShareDetailVO.PK_CSHARE_DETAIL , SRC_SUBID);
		costfieldMap.put( PK_RESACOSTCENTER , PK_RESACOSTCENTER);
		costfieldMap.put( PK_CHECKELE , PK_CHECKELE);
		costfieldMap.put( ASSUME_AMOUNT , ASSUME_AMOUNT);
		costfieldMap.put( PK_ORG , PK_ORG);
		costfieldMap.put( PK_PCORG , PK_PCORG);
		costfieldMap.put( PK_GROUP , PK_GROUP);
		
		costfieldMap.put( CShareDetailVO.PK_CSHARE_DETAIL , ExpenseAccountVO.SRC_SUBID);
		//子表业务业签名
		//costfieldMap.put( "tablecode" , SRC_BILLTAB);
		//重新算出
		//fieldMap.put( "kjnd" , ACCYEAR);
//		fieldMap.put( ACCMONTH , ACCMONTH);
//		fieldMap.put( "kjqj" , ACCPERIOD);
		costfieldMap.put( ZB+CostShareVO.BX_ORG , BX_ORG);
		costfieldMap.put( ZB+CostShareVO.BX_GROUP , BX_GROUP);
		costfieldMap.put( ZB+CostShareVO.BX_FIORG , BX_FIORG);
		costfieldMap.put( ZB+CostShareVO.BILLDATE , BILLDATE);
		costfieldMap.put( ZB+CostShareVO.BILLMAKER , BILLMAKER);
		costfieldMap.put( ZB+CostShareVO.BILLSTATUS , BILLSTATUS);
		costfieldMap.put( ZB+CostShareVO.CASHPROJ, BX_CASHPROJ);
		costfieldMap.put( ZB+CostShareVO.DWBM , BX_DWBM);
		costfieldMap.put( ZB+CostShareVO.DEPTID , BX_DEPTID);
		costfieldMap.put( ZB+CostShareVO.PK_COSTSHARE , ExpenseAccountVO.SRC_SUBID);
		costfieldMap.put( ZB+CostShareVO.JSFS , BX_JSFS);
		costfieldMap.put( ZB+CostShareVO.CASHITEM, BX_CASHITEM);
		costfieldMap.put( ZB+CostShareVO.JKBXR, BX_JKBXR);
		costfieldMap.put( ZB+CostShareVO.ZY, REASON);
		costfieldMap.put( ZB+CostShareVO.PK_COSTSHARE , SRC_ID);
		//计算字段
		//fieldMap.put( ISWRITEOFF , ISWRITEOFF);
	}
	
	static
	{
		bxfieldMap.put( "djrq" , BILLDATE);
		bxfieldMap.put( "operator" , BILLMAKER);
		bxfieldMap.put( "djzt" , BILLSTATUS);
		bxfieldMap.put( "fydwbm" , ASSUME_ORG);
		bxfieldMap.put( "fydeptid" , ASSUME_DEPT);
		bxfieldMap.put( PK_PCORG , PK_PCORG);
		bxfieldMap.put( PK_RESACOSTCENTER , PK_RESACOSTCENTER);
		bxfieldMap.put( "szxmid" , PK_IOBSCLASS);
		bxfieldMap.put( "jobid" , PK_PROJECT);
	//	fieldMap.put( PK_WBS , PK_WBS);
		bxfieldMap.put( PK_CHECKELE , PK_CHECKELE);
		bxfieldMap.put( "customer" , PK_CUSTOMER);
		bxfieldMap.put( "hbbm" , PK_SUPPLIER);
		bxfieldMap.put( "ybje" , ASSUME_AMOUNT);
		bxfieldMap.put( "bzbm" , PK_CURRTYPE);
		bxfieldMap.put( "bbhl" , ORG_CURRINFO);
		bxfieldMap.put( "bbje" , ORG_AMOUNT);
		bxfieldMap.put( "globalbbje" , GLOBAL_AMOUNT);
		bxfieldMap.put( "groupbbje" , GROUP_AMOUNT);
		bxfieldMap.put( "globalbbhl" , GLOBAL_CURRINFO);
		bxfieldMap.put( "groupbbhl" , GROUP_CURRINFO);
		bxfieldMap.put( PK_ORG , PK_ORG);
		bxfieldMap.put( PK_GROUP , PK_GROUP);
		
		bxfieldMap.put( "djlxbm" , SRC_BILLTYPE);
		bxfieldMap.put( SRC_TRADETYPE , SRC_TRADETYPE);
		
		bxfieldMap.put( "pk_jkbx" , SRC_ID);
		bxfieldMap.put( "djbh" , SRC_BILLNO);
		//子表业务业签名
		bxfieldMap.put( "tablecode" , SRC_BILLTAB);
		//
		bxfieldMap.put( "pk_busitem" , SRC_SUBID);
		bxfieldMap.put( "kjnd" , ACCYEAR);
		//重新算出
		bxfieldMap.put( ACCMONTH , ACCMONTH);
		bxfieldMap.put( "kjqj" , ACCPERIOD);
		bxfieldMap.put( "pk_org" , BX_ORG);
		bxfieldMap.put( "pk_group" , BX_GROUP);
		bxfieldMap.put( "pk_fiorg" , BX_FIORG);
		bxfieldMap.put( "cashproj" , BX_CASHPROJ);
		bxfieldMap.put( "dwbm" , BX_DWBM);
		bxfieldMap.put( "deptid" , BX_DEPTID);
		bxfieldMap.put( "jsfs" , BX_JSFS);
		bxfieldMap.put( BX_CASHITEM , BX_CASHITEM);
		bxfieldMap.put( "jkbxr" , BX_JKBXR);
		bxfieldMap.put( "zy" , REASON);
		
		//计算字段
		bxfieldMap.put( ISWRITEOFF , ISWRITEOFF);
		
	}
}
