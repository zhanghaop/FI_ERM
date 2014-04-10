package nc.impl.erm.costshare;

import nc.bs.erm.costshare.actimpl.CostShareYsActControlBO;
import nc.pubitf.erm.costshare.IErmCostShareYsControlService;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * ���ý�ת��Ԥ�����ʵ��
 * 
 * @author lvhj
 *
 */
public class ErmCostShareYsControlServiceImpl implements
		IErmCostShareYsControlService {

	@Override
	public void ysControl(AggCostShareVO[] vos, boolean isContray,
			String actionCode) throws BusinessException {
		CostShareYsActControlBO bo = new CostShareYsActControlBO();
		bo.ysControl(vos, isContray, actionCode);
	}

	@Override
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos)
			throws BusinessException {
		CostShareYsActControlBO bo = new CostShareYsActControlBO();
		bo.ysControlUpdate(vos, oldvos);
	}

}
