package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXSettleSpControlEffImp extends ArapBxActEffImp {
	
	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {

		return param;
	}
	public JKBXVO[] beforeEffectAct(JKBXVO[] param) throws BusinessException {
		spControl(param, false,false);
		return param;
	}
}
