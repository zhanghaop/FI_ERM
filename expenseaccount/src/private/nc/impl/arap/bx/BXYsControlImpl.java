package nc.impl.arap.bx;

import nc.itf.erm.ntb.IBXYsControlService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXYsControlImpl extends ArapBxActEffImp implements IBXYsControlService {
	@Override
	public void ysControl(JKBXVO[] vos, boolean isContray, String actionCode) throws BusinessException {
		super.ysControl(vos, isContray, actionCode);
	}

	@Override
	public void ysControlUpdate(JKBXVO[] vos) throws BusinessException {
		super.ysControlUpdate(vos);
	}
	
}
