package nc.vo.erm.pub;

/**
 * 报销管理公共常量接口-借款账龄分析<br>
 *
 * @author 连树国<br>
 * @since V60<br>
 */
public class IErmReportAnalyzeConstants {

	// 账龄分析模式
	public static String getACC_ANA_MODE_AGE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0066")/*@res "按账龄"*/;
	}
	
	public static String getACC_ANA_MODE_DATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0067")/*@res "按日期"*/;
	}

	// 分析类型
	public static String getACC_ANA_TYP_SETTLE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0003252")/*@res "结算日期"*/;
	}
	public static String getACC_ANA_TYP_DEADLINE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0001971")/*@res "截止日期"*/;
	}

	// 分析日期
	public static String getACC_ANA_DATE_BILLDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0000799")/*@res "单据日期"*/;
	}
	
	public static String getACC_ANA_DATE_LASTPAYDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0105")/*@res "最迟还款日"*/;
	}
	public static String getACC_ANA_DATE_AUDITDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UCMD1-000325")/*@res "审核日期"*/;
	}
	public static final String getACC_ANA_DATE_EFFECTDATE() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common","UC000-0002942")/*@res "生效日期"*/;
	}

	// 分析方式
	public static final String getACC_ANA_PATTERN_FINAL() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0106")/*@res "最终余额"*/;
	}
	public static final String getACC_ANA_PATTERN_POINT() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0107")/*@res "点余额"*/;
	}


	// 单据状态
	public static final String getBILL_STATUS_EFFECT() {
	    return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0108")/*@res "已生效"*/;
	}

	public static final String INCLUDE_UNEFFECT = "includeuneffect"; // 生效状态

	// 可替换表名
	public static final String REPLACE_TABLE = "@Table";

	// 全部单据状态
	public static final int I_BILL_STATUS_ALL = -1000;

}

