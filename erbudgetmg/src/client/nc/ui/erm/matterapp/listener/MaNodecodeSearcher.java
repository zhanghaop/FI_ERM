package nc.ui.erm.matterapp.listener;

import nc.bs.erm.common.ErmBillConst;
import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;

/**
 * �������뵥��������������ڵ�����
 * 
 * @author lvhj
 *
 */
public class MaNodecodeSearcher implements IUINodecodeSearcher {

	@Override
	public String findNodecode(ILinkQueryData lqd) {
		// Ĭ�Ϸ��ص��ݹ���ڵ�
		return ErmBillConst.MatterApp_FUNCODE;
	}

}
