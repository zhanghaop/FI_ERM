package nc.ui.erm.accruedexpense.listener;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;

/**
 * ע���ڵ������� �Զ�����3 ����53
 * @author chenshuaia
 *
 */
public class AccNodeSearcher implements IUINodecodeSearcher {
	@Override
	public String findNodecode(ILinkQueryData lqd) {
		return ErmBillConst.ACC_NODECODE_MN;
	}

}
