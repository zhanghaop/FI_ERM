package nc.ui.erm.smartdesigner;

import java.awt.Container;

import nc.pub.smart.context.SmartContext;
import nc.pub.smart.provider.Provider;
import nc.pub.smart.smartprovider.ExpDetailDataProvider;
import nc.ui.pub.smart.provider.IProviderDesignWizard;

/**
 * <p>
 * TODO ������ϸ�������ṩ�������
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2010-12-22 ����03:34:45
 */
@SuppressWarnings("restriction")
public class ExpDetailDataProviderDesigner implements IProviderDesignWizard {

	@Override
	public Provider design(Container parent, Provider provider,
			SmartContext context) {
		return new ExpDetailDataProvider();
	}

}