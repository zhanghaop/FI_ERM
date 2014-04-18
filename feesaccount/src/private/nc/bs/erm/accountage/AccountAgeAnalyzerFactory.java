package nc.bs.erm.accountage;

import nc.itf.fipub.report.IPubReportConstants;

/**
 * 账龄分析器工厂<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
public class AccountAgeAnalyzerFactory {

	public static IAccountAgeAna getAccountAgeAnalyzer(String accountAgeType) {
		IAccountAgeAna analyzer = null;
		if (IPubReportConstants.LOAN_ACCOUNTAGE_REP_NAME.equals(accountAgeType)) {
			// 借款账龄分析
			analyzer = new LoanAccountAgeAnalyzer();
		} 
		return analyzer;
	}

}


