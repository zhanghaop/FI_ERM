package nc.ui.erm.smartdesigner;

import java.awt.Container;

import nc.itf.fipub.report.IPubReportConstants;
import nc.pub.smart.context.SmartContext;
import nc.pub.smart.provider.Provider;
import nc.pub.smart.smartprovider.LoanAgeDetailAnalyzeDataProvider;
import nc.ui.pub.smart.provider.IProviderDesignWizard;

@SuppressWarnings("restriction")
public class LoanAgeDetailDataProviderDesigner implements IProviderDesignWizard {

	public Provider design(Container parent, Provider provider, SmartContext context) {
		Provider provider2 = new LoanAgeDetailAnalyzeDataProvider();
		provider2.setCode(IPubReportConstants.SEMANTIC_PROVIDER_ALIAS);
		provider2.setTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("feesaccount_0","02011001-0044")/*@res "账龄明细语义提供者"*/); // 账龄明细语义提供者
		return provider2;
	}

}

// /:~