package nc.ui.erm.matterapp.listener;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;

/**
 * 费用申请单，单据类型联查节点获得类
 * 
 * @author lvhj
 *
 */
public class MaNodecodeSearcher implements IUINodecodeSearcher {

	@Override
	public String findNodecode(ILinkQueryData lqd) {
		// 默认返回单据管理节点
		return ErmBillConst.MatterApp_FUNCODE;
	}

}
