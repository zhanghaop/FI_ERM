package nc.ui.arap.bx;

import nc.ui.pub.linkoperate.ILinkQueryData;
import nc.ui.pub.pf.IUINodecodeSearcher;
import nc.vo.arap.bx.util.BXConstans;

/**
 * ��������������������ڵ�����
 * ����ע���ڵ������ͱ��� �Զ�����3
 * �����鵥�ݵȲ���ʱ������ڵ��ȡ����
 * @author chenshuaia
 *
 */
public class BXNodecodeSearcher implements IUINodecodeSearcher {

	@Override
	public String findNodecode(ILinkQueryData lqd) {
		return BXConstans.BXMNG_NODECODE;
	}
}
