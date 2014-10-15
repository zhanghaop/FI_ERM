package nc.impl.erm.accruedexpense;

import nc.bs.erm.accruedexpense.ErmAccruedBillBO;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillCommit;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class ErmAccruedBillCommitImpl implements IErmAccruedBillCommit {

	@Override
	public AggAccruedBillVO[] commitVOs(AggAccruedBillVO[] vos) throws BusinessException {
		return new ErmAccruedBillBO().commitVOs(vos);
	}

	@Override
	public AggAccruedBillVO[] recallVOs(AggAccruedBillVO[] vos) throws BusinessException {
		return new ErmAccruedBillBO().recallVOs(vos);
	}

}
