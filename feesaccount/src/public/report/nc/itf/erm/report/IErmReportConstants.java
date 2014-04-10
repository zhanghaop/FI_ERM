package nc.itf.erm.report;

import nc.itf.fipub.report.IPubReportConstants;

/**
 * <p>
 * 接口/类功能说明:报销管理部分帐表常量接口类。
 * </p>
 * 
 * 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-11-19 上午11:15:05
 */
public interface IErmReportConstants {

	// 是否显示日小计：支持UFBoolean值
	public static final String KEY_SHOW_DATE_TOTAL = "showDateTotalComb";

	// 报销管理借款类账表
	public static String ERM_LOAN_REPORT = "erm_loan_report";

	// 报销管理借款类帐表名称
	public static String ERM_LOAN_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0056")/* @res "借款类账表" */;

	// 报销管理费用类帐表
	public static String ERM_EXPENSE_REPORT = "erm_expense_report";

	// 报销管理费用类帐表名称
	public static String ERM_EXPENSE_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0", "02011001-0057")/*  @res "费用类账表" */;

	//报销管理账龄分析
	public static String ERM_ACCOUNTAGE_REPORT = "erm_accountage_report";

	//报销管理账龄分析名称
	public static String ERM_ACCOUNTAGE_REPORT_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0058")/*@res "账龄分析"*/;


	//借款查询-借款人
	public static String BORROWER_REP = "borrower_rep";

	//借款查询-借款人名称
	public static String BORROWER_REP_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0059")/*@res "借款查询-借款人"*/;

	//借款明细表
	public static String LOAN_DETAIL_REP = "loan_detail_rep";

	//借款明细表名称
	public static String LOAN_DETAIL_REP_NAME = "loandetail";

	public static String LOAN_DETAIL_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0060")/*@res "借款明细账"*/;

	//借款余额表
	public static String LOAN_BALANCE_REP = "loan_balance_rep";

	public static String LOAN_BALANCE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0061")/*@res "借款余额表"*/;

	//借款余额表名称
	public static String LOAN_BALANCE_REP_NAME = "loanbalance";

	//费用明细表
	public static String EXPENSE_DETAIL_REP = "expense_detail_rep";

	public static String EXPENSE_DETAIL_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0062")/*@res "费用明细账"*/;

	//费用明细表名称
	public static String EXPENSE_DETAIL_REP_NAME = "expensedetail";

	//费用余额表
	public static String EXPENSE_BALANCE_REP = "expense_balance_rep";

	public static String EXPENSE_BALANCE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0063")/*@res "费用汇总表"*/;

	//费用余额表名称

	public static String EXPENSE_BALANCE_REP_NAME = "expensebalance";

	//借款账龄分析
	public static String LOAN_ACCOUNTAGE_REP = "loan_accountage_rep";

	//借款账龄分析名称
	public static String LOAN_ACCOUNTAGE_REP_NAME = "loanaccount";

	public static String LOAN_ACCOUNTAGE_REP_NAME_LBL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0064")/*@res "借款账龄分析"*/;

	//借款账龄明细分析
	public static String LOAN_ACCOUNTAGE_DETAIL_REP = "loan_accountage_detail_rep";

	//借款账龄明细分析名称
	public static String LOAN_ACCOUNTAGE_DETAIL_REP_NAME = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0065")/*@res "借款账龄明细分析"*/;

	// 报销产品模块信息
	public final String ERM_PRODUCT_CODE = "ERM"; // 模块编码大写形式

	public final String ERM_PRODUCT_CODE_Lower = "erm"; // 模块编码小写形式

	public final String ERM_MODULEID = "2011";

	public final String ERM_REPORT_NODECODE = "201109"; // 报销管理帐表查询nodecode

	public final String ERM_LOAN_DETAIL = "20111RLD"; // 报销管理借款明细账nodecode

	public final String ERM_LOAN_BALANCE = "20111RBA"; // 报销管理借款余额表nodecode

	public final String ERM_EXPENSE_DETAIL = "20111RED"; // 报销管理费用明细账nodecode

	public final String ERM_EXPENSE_TOTAL = "20111RET"; // 报销管理费用汇总表nodecode

	public final String ERM_LOAN_ACCOUNTAGE = "2011LAA"; // 报销管理借款账龄分析nodecode

	public final String ERM_LOAN_ACCOUNTAGE_DETAIL = "2011LAAD"; // 报销管理借款账龄明细分析nodecode

	public static final String QUERY_SCOPE_ALL_REC = "er_jkzb";

	// 账龄分析模式
	public static final String ACC_ANA_MODE_AGE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "按账龄"*/;

	public static final String ACC_ANA_MODE_DATE = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "按日期"*/;

	// 可替换表名
	public static final String REPLACE_TABLE = "@Table";

	// 全部单据状态
	public static final int I_BILL_STATUS_ALL = -1000;

	public static final String CONST_BRIEF = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000085")/*@res "期初"*/; // 期初
	public static final String CONST_SUB_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0068")/*@res "小计"*/; // 小计
	public static final String CONST_AGG_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001146")/*@res "合计"*/; // 合计
	public static final String CONST_ALL_TOTAL = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0069")/*@res "总计"*/; // 总计

}

// /:~
