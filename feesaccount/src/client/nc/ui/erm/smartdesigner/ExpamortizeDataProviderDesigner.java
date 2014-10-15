package nc.ui.erm.smartdesigner;

import java.awt.Container;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.provider.Provider;
import nc.pub.smart.smartprovider.ExpamortizeDataProvider;
import nc.ui.pub.smart.provider.IProviderDesignWizard;
/**
 * 摊销信息分析语义提供者设计器
 * @author wangled
 *
 */
@SuppressWarnings("restriction")
public class ExpamortizeDataProviderDesigner implements IProviderDesignWizard {

	@Override
	public Provider design(Container parent, Provider provider,
			SmartContext context) {
		
		return new ExpamortizeDataProvider();
	}

}
