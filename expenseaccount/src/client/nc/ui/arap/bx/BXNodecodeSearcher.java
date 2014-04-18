package nc.ui.arap.bx;

import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;
import nc.vo.arap.bx.util.BXConstans;

/**
 * 借款报销单，单据类型联查节点获得类
 * 该类注册于单据类型表中 自定义项3
 * 在联查单据等操作时，联查节点会取该类
 * @author chenshuaia
 *
 */
public class BXNodecodeSearcher implements IUINodecodeSearcher {

	@Override
	public String findNodecode(ILinkQueryData lqd) {
		return BXConstans.BXMNG_NODECODE;
	}
}
