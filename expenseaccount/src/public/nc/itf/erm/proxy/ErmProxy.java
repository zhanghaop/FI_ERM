package nc.itf.erm.proxy;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.tb.control.ILinkQuery;

/**
 * <p>
 * TODO �������ִ�����
 * </p>
 *
 * �޸ļ�¼��<br>
 * <li>�޸��ˣ��޸����ڣ��޸����ݣ�</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 ����ʱ�䣺2011-3-10 ����02:31:47
 */
public class ErmProxy {
	
	public static ILinkQuery getILinkQuery() throws ComponentException {
		return ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName()));
	}
}
