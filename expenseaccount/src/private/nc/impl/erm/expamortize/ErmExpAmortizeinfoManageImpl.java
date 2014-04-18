package nc.impl.erm.expamortize;

import nc.bs.erm.expamortize.ErmExpamortizeBO;
import nc.pubitf.erm.expamortize.IExpAmortizeinfoManage;
import nc.vo.erm.expamortize.AggExpamtinfoVO;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;

public class ErmExpAmortizeinfoManageImpl implements IExpAmortizeinfoManage{
	
	
	@Override
	public void deleteVOs(AggExpamtinfoVO[] vos) throws BusinessException {
		new ErmExpamortizeBO().deleteVOs(vos);
	}

	@Override
	public void insertVOs(AggExpamtinfoVO[] vos) throws BusinessException {
		new ErmExpamortizeBO().insertVO(vos);
		
	}

	@Override
	public ExpamtinfoVO updatePeriod(int newPeriod, ExpamtinfoVO vo,String currAccPeriod)
			throws BusinessException {
		if(vo!=null){
			//修改摊销期开始
			return new ErmExpamortizeBO().updatePeriod(newPeriod,vo,currAccPeriod);
		}
		return null;
	}

}
