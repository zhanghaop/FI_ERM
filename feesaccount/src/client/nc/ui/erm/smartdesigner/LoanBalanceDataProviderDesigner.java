package nc.ui.erm.smartdesigner;

import java.awt.Container;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.provider.Provider;
import nc.pub.smart.smartprovider.LoanBalanceDataProvider;
import nc.ui.pub.smart.provider.IProviderDesignWizard;

/**
 * <p>
 * TODO 借款余额表语义提供者设计器
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2010-12-22 下午03:35:12
 */
@SuppressWarnings("restriction")
public class LoanBalanceDataProviderDesigner implements IProviderDesignWizard {

	@Override
	public Provider design(Container parent, Provider provider,
			SmartContext context) {
		return new LoanBalanceDataProvider();
	}

}
