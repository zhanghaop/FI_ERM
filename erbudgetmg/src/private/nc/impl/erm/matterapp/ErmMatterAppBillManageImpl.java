package nc.impl.erm.matterapp;

import nc.bs.erm.matterapp.ErmMatterAppBO;
import nc.pubitf.erm.matterapp.IErmMatterAppBillManage;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.pub.BusinessException;

public class ErmMatterAppBillManageImpl implements IErmMatterAppBillManage {

	@Override
	public AggMatterAppVO insertVO(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().insertVO(vo);
	}

	@Override
	public AggMatterAppVO updateVO(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().updateVO(vo);
	}

	@Override
	public void deleteVOs(AggMatterAppVO[] vos) throws BusinessException {
		new ErmMatterAppBO().deleteVOs(vos);
	}

	@Override
	public AggMatterAppVO tempSave(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().tempSave(vo);
	}

	@Override
	public AggMatterAppVO invalidBill(AggMatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().invalidBill(vo);
	}

	@Override
	public MatterAppVO updatePrintInfo(MatterAppVO vo) throws BusinessException {
		return new ErmMatterAppBO().updatePrintInfo(vo);
	}
}
