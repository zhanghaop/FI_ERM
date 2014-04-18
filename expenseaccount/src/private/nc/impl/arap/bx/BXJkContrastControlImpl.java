package nc.impl.arap.bx;

import nc.bs.arap.bx.ContrastBO;
import nc.itf.erm.ntb.IBXJkContrastControlService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXJkContrastControlImpl extends ArapBxActEffImp implements IBXJkContrastControlService {
	
	@Override
	public void jkControl(JKBXVO[] vos) throws BusinessException {
		super.jkControl(vos);
	}

	public void dealUnEffectContrast(JKBXVO[] param) throws BusinessException {
		for (int i = 0; i < param.length; i++) {
			new ContrastBO().unEffectContrast(param[i]);
		}
	}
	
	public void dealEffectContrast(JKBXVO[] param) throws BusinessException {
		for (int i = 0; i < param.length; i++) {
			new ContrastBO().effectContrast(param[i]);
		}
	}
}
