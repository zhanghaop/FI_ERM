package nc.bs.erm.matterapp.eventlistener;

import nc.bs.erm.matterapp.control.IErmMatterAppYsControlService;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

public class ErmMatterAppYsControlServiceImpl implements
		IErmMatterAppYsControlService {

	@Override
	public void ysControl(AggMatterAppVO[] vos, boolean isContray, String actionCode, boolean isExistParent)
			throws BusinessException {
		new MatterAppYsActControlBO().ysControl(vos, isContray, actionCode, isExistParent);
	}

	@Override
	public void ysControlUpdate(AggMatterAppVO[] vos, AggMatterAppVO[] oldvos) throws BusinessException {
		new MatterAppYsActControlBO().ysControlUpdate(vos, oldvos);
	}

}
