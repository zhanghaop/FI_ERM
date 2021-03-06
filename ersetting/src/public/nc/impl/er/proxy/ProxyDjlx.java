package nc.impl.er.proxy;

import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.itf.er.pub.IArapBillTypePublic;

public class ProxyDjlx {

	public static IArapBillTypePublic getIArapBillTypePublic() throws ComponentException {
		return NCLocator.getInstance().lookup(IArapBillTypePublic.class);
	}

	public static IArapBillTypePrivate getIArapBillTypePrivate() throws ComponentException {
		return  NCLocator.getInstance().lookup(IArapBillTypePrivate.class);
	}

}
