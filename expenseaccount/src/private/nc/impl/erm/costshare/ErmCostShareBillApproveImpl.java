package nc.impl.erm.costshare;

import nc.bs.erm.costshare.ErmCostShareBO;
import nc.pubitf.erm.costshare.IErmCostShareBillApprove;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class ErmCostShareBillApproveImpl implements IErmCostShareBillApprove {
	
//	@Override
	public MessageVO[] approveVOs(AggCostShareVO[] vos,UFDate buDate)
			throws BusinessException {
		return new ErmCostShareBO().approveVOs(vos,buDate);
	}

//	@Override
	public MessageVO[] unapproveVOs(AggCostShareVO[] vos)
			throws BusinessException {
		return new ErmCostShareBO().unapproveVOs(vos);
	}

}
