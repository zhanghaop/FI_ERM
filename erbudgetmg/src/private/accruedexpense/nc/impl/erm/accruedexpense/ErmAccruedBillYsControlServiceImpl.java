package nc.impl.erm.accruedexpense;

import nc.bs.erm.accruedexpense.AccrueBillYsActControlBO;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillYsControlService;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class ErmAccruedBillYsControlServiceImpl implements IErmAccruedBillYsControlService {

	@Override
	public void ysControl(AggAccruedBillVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException {
		new AccrueBillYsActControlBO().ysControl(vos, isContray, actionCode, isExistParent);

	}

	@Override
	public void ysControlUpdate(AggAccruedBillVO[] vos, AggAccruedBillVO[] oldvos) throws BusinessException {
		new AccrueBillYsActControlBO().ysControlUpdate(vos, oldvos);
	}

}
