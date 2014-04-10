package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXUnSettleSpControlEffImp extends ArapBxActEffImp {
	
	public JKBXVO[] afterEffectAct(JKBXVO[] param) throws BusinessException {

		return param;
	}


	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {
		
		spControl(vos, true,false);
		return vos;
	}

}
