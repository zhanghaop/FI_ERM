package nc.impl.erm.matterapp;

import nc.bs.erm.matterapp.ErmMatterAppBO;
import nc.pubitf.erm.matterapp.IErmMatterAppBillApprove;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.pub.BusinessException;

public class ErmMatterAppBillApproveImpl implements IErmMatterAppBillApprove {

	@Override
	public AggMatterAppVO[] commitVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().commitVOs(vos);
	}

	@Override
	public AggMatterAppVO[] recallVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().recallVOs(vos);
	}

	@Override
	public MessageVO[] approveVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().approveVOs(vos);
	}

	@Override
	public MessageVO[] unApproveVOs(AggMatterAppVO[] vos)
			throws BusinessException {
		return new ErmMatterAppBO().unapproveVOs(vos);
	}

	@Override
	public AggMatterAppVO updateVOBillStatus(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().updateVOBillStatus(vo);
	}

}
