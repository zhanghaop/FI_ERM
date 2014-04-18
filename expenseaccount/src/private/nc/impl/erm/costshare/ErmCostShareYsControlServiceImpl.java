package nc.impl.erm.costshare;

import java.util.List;

import nc.bs.erm.costshare.actimpl.CostShareYsActControlBO;
import nc.pubitf.erm.costshare.IErmCostShareYsControlService;
import nc.vo.erm.control.YsControlVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;

/**
 * 费用结转单预算控制实现
 * 
 * @author lvhj
 * 
 */
public class ErmCostShareYsControlServiceImpl implements IErmCostShareYsControlService {

	@Override
	public void ysControl(AggCostShareVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		CostShareYsActControlBO bo = new CostShareYsActControlBO();
		bo.ysControl(vos, isContray, actionCode);
	}

	@Override
	public void ysControlUpdate(AggCostShareVO[] vos, AggCostShareVO[] oldvos) throws BusinessException {
		CostShareYsActControlBO bo = new CostShareYsActControlBO();
		bo.ysControlUpdate(vos, oldvos);
	}

	@Override
	public List<YsControlVO> getCostShareYsVOList(AggCostShareVO[] vos, boolean isContray, String actionCode)
			throws BusinessException {
		// TODO Auto-generated method stub
		CostShareYsActControlBO bo = new CostShareYsActControlBO();
		return bo.getCostShareYsVOList(vos, isContray, actionCode);
	}
}
