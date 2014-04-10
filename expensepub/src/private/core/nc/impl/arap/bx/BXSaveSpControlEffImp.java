package nc.impl.arap.bx;

import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class BXSaveSpControlEffImp extends ArapBxActEffImp {

	public JKBXVO[] afterEffectAct(JKBXVO[] vos) throws BusinessException {
		spControl(vos, false,true);
		
		return vos;
	}

	public JKBXVO[] beforeEffectAct(JKBXVO[] vos) throws BusinessException {

		return vos;
	}

}
