package nc.impl.erm.accruedexpense;

import nc.bs.erm.accruedexpense.ErmAccruedBillBO;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillManage;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class ErmAccruedBillManageImpl implements IErmAccruedBillManage {
	

	@Override
	public void deleteVOs(AggAccruedBillVO[] vos) throws BusinessException {
		new ErmAccruedBillBO().deleteVOs(vos);
	}

	@Override
	public AggAccruedBillVO insertVO(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().insertVO(vo);
	}

	@Override
	public AggAccruedBillVO updateVO(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().updateVO(vo);
	}
	
	@Override
	public AggAccruedBillVO tempSave(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().tempSave(vo);
	}

	@Override
	public AccruedVO updatePrintInfo(AccruedVO accrueVo) throws BusinessException {
		return new ErmAccruedBillBO().updatePrintInfo(accrueVo);
	}

	@Override
	public AggAccruedBillVO redbackVO(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().redbackVO(vo);
	}

	@Override
	public AggAccruedBillVO unRedbackVO(AggAccruedBillVO vo) throws BusinessException {
		return new ErmAccruedBillBO().unRedbackVO(vo);
	}
	
}
