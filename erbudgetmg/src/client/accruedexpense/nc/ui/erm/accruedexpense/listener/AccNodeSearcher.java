package nc.ui.erm.accruedexpense.listener;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;

/**
 * 注册在单据类型 自定义项3 长度53
 * @author chenshuaia
 *
 */
public class AccNodeSearcher implements IUINodecodeSearcher {
	@Override
	public String findNodecode(ILinkQueryData lqd) {
		return ErmBillConst.ACC_NODECODE_MN;
	}

}
