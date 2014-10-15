package nc.itf.erm.proxy;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.tb.control.ILinkQuery;

/**
 * <p>
 * TODO 报销部分代理类
 * </p>
 *
 * 修改记录：<br>
 * <li>修改人：修改日期：修改内容：</li>
 * <br><br>
 *
 * @see 
 * @author liansg
 * @version V6.0
 * @since V6.0 创建时间：2011-3-10 下午02:31:47
 */
public class ErmProxy {
	
	public static ILinkQuery getILinkQuery() throws ComponentException {
		return ((ILinkQuery) NCLocator.getInstance().lookup(ILinkQuery.class.getName()));
	}
}
