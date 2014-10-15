package nc.itf.erm.ntb;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * ����Ԥ��ӿ�
 * @author chenshuaia
 *
 */
public interface IErmLinkBudgetService {
	/**
	 * �����������
	 * @param vo ����VO
	 * @param actionCode �������룺 audit��
	 * @param nodeCode �ڵ����
	 * @return
	 * @throws BusinessException
	 */
	public NtbParamVO[] getBudgetLinkParams(JKBXVO vo , String actionCode, String nodeCode) throws BusinessException;
}
