package nc.bs.erm.accountage;

import nc.bs.erm.sql.LoanAccAgeAnaSQLCreator;

/**
 * ╫Х©НукаД╥жнЖ<br>
 * 
 * @author liansg<br>
 * @since V60<br>
 */
public class LoanAccountAgeAnalyzer extends AccountAgeAnaAdaptor {

	protected LoanAccAgeAnaSQLCreator getSqlCreator() {
		return new LoanAccAgeAnaSQLCreator();
	}

}

