package nc.bs.erm.expamortize.eventlistener;

import nc.bs.erm.expamortize.control.IErmExpamortizeYsControlService;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.pub.BusinessException;

public class ExpamortizeYsControlServiceImpl implements IErmExpamortizeYsControlService {

	@Override
	public void ysControl(AggExpamtinfoVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		new ExpamortizeYsActControlBO().ysControl(vos, isContray, actionCode);
	}

}
