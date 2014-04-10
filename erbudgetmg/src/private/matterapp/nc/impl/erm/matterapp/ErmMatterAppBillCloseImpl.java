package nc.impl.erm.matterapp;

import nc.bs.erm.matterapp.ErmMatterAppBO;
import nc.pubitf.erm.matterapp.IErmMatterAppBillClose;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

public class ErmMatterAppBillCloseImpl implements IErmMatterAppBillClose{

	@Override
	public AggMatterAppVO[] closeVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().closeVOs(vos, false);
	}

	@Override
	public AggMatterAppVO[] openVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().openVOs(vos, false);
	}

	@Override
	public AggMatterAppVO[] autoCloseVOs(AggMatterAppVO[] vos) throws BusinessException {
		return new ErmMatterAppBO().closeVOs(vos, true);
	}

	@Override
	public AggMatterAppVO[] autoOpenVOs(AggMatterAppVO[] vos) throws BusinessException {
		return new ErmMatterAppBO().openVOs(vos, true);
	}
}
