package nc.impl.erm.accruedexpense;

import nc.bs.erm.accruedexpense.ErmAccruedBillBO;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillApprove;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.BusinessException;

public class ErmAccruedBillApproveImpl implements IErmAccruedBillApprove {

	@Override
	public MessageVO[] approveVOs(AggAccruedBillVO[] vos) throws BusinessException {
		return new ErmAccruedBillBO().approveVOS(vos);
	}

	@Override
	public MessageVO[] unApproveVOs(AggAccruedBillVO[] vos) throws BusinessException {
		return new ErmAccruedBillBO().unapproveVOs(vos);
	}

	@Override
	public AggAccruedBillVO updateVOBillStatus(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().updateVOBillStatus(vo);
	}

}
