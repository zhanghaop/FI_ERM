package nc.itf.erm.ntb;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;
import nc.vo.tb.obj.NtbParamVO;

/**
 * 联查预算接口
 * @author chenshuaia
 *
 */
public interface IErmLinkBudgetService {
	/**
	 * 联查借款报销费用
	 * @param vo 借款报销VO
	 * @param actionCode 动作编码： audit等
	 * @param nodeCode 节点编码
	 * @return
	 * @throws BusinessException
	 */
	public NtbParamVO[] getBudgetLinkParams(JKBXVO vo , String actionCode, String nodeCode) throws BusinessException;
}
